package telegram;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import process.Process;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bot extends TelegramLongPollingBot {
    private enum UserState {
        IDLE,
        WAITING_FOR_TASKS_FILE,
        WAITING_FOR_EMPLOYEES_FILE
    }

    private Map<Long, UserState> userStates = new HashMap<>();
    private Map<String, String> tempFiles = new HashMap<>();
    private Map<Long, String> statisticsFiles = new HashMap<>();
    @Override
    public String getBotUsername() {
        return "Work bot";
    }

    @Override
    public String getBotToken() {
        return "7920389277:AAFsXFuDl98e7j6Mq8CYvZP8e3Gxbi_6R7Q";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            long chatId = update.getMessage().getChatId();
            UserState currentState = userStates.getOrDefault(chatId, UserState.IDLE);

            if (update.getMessage().hasText()) {
                handleTextMessage(chatId, update.getMessage().getText(), currentState);
            } else if (update.getMessage().hasDocument()) {
                handleDocument(chatId, update.getMessage().getDocument(), currentState);
            }
        }
    }

    private void handleTextMessage(long chatId, String text, UserState currentState) {
        switch (text) {
            case "/start":
                sendWelcomeMessage(chatId);
                break;
            case "Загрузить файл с задачами":
                userStates.put(chatId, UserState.WAITING_FOR_TASKS_FILE);
                sendMessage(chatId, "Загрузите Excel-файл с задачами");
                break;
            case "Загрузить файл с сотрудниками":
                userStates.put(chatId, UserState.WAITING_FOR_EMPLOYEES_FILE);
                sendMessage(chatId, "Загрузите Excel-файл с сотрудниками");
                break;
            case "Запустить обработку":
                if (tempFiles.containsKey(chatId + "_tasks") && tempFiles.containsKey(chatId + "_employees")) {
                    processFiles(chatId);
                } else {
                    sendMessage(chatId, "Сначала загрузите оба файла: с задачами и сотрудниками");
                }
                break;
            case "Получить статистику":
                if (statisticsFiles.containsKey(chatId)) {
                    sendStatistics(chatId);
                } else {
                    sendMessage(chatId, "Статистика ещё не готова. Сначала запустите обработку файлов.");
                }
                break;
            default:
                sendMessage(chatId, "Используйте кнопки меню для взаимодействия с ботом");
        }
    }

    private void handleDocument(long chatId, Document document, UserState currentState) {
        try {
            if (!document.getFileName().endsWith(".xlsx")) {
                sendMessage(chatId, "Загрузите файл в формате .xlsx");
                return;
            }

            // Скачиваем файл
            GetFile getFile = new GetFile();
            getFile.setFileId(document.getFileId());
            String filePath = execute(getFile).getFilePath();

            // Сохраняем файл
            String fileName = "user_" + chatId + "_" + document.getFileName();
            File downloadedFile = downloadFile(filePath, new File("temp_files", fileName));

            switch (currentState) {
                case WAITING_FOR_TASKS_FILE:
                    tempFiles.put(chatId + "_tasks", downloadedFile.getAbsolutePath());
                    userStates.put(chatId, UserState.IDLE);
                    sendMessage(chatId, "Файл с задачами успешно загружен");
                    break;
                case WAITING_FOR_EMPLOYEES_FILE:
                    tempFiles.put(chatId + "_employees", downloadedFile.getAbsolutePath());
                    userStates.put(chatId, UserState.IDLE);
                    sendMessage(chatId, "Файл с сотрудниками успешно загружен");
                    break;
                default:
                    sendMessage(chatId, "Сначала выберите, какой файл вы загружаете");
            }
        } catch (Exception e) {
            sendMessage(chatId, "Ошибка при загрузке файла: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void processFiles(long chatId) {
        new Thread(() -> {
            try {
                String tasksPath = tempFiles.get(chatId + "_tasks");
                String employeesPath = tempFiles.get(chatId + "_employees");

                if (tasksPath == null || employeesPath == null) {
                    sendMessage(chatId, "Оба файла должны быть загружены перед обработкой");
                    return;
                }

                sendMessage(chatId, "Начинаю обработку файлов...");

                // Создаем путь для файла статистики
                String statsPath = "temp_files/statistics_" + chatId + ".xlsx";

                // Запускаем процесс с указанием пути для сохранения статистики
                Process.process(tasksPath, employeesPath, statsPath);

                // Сохраняем путь к файлу статистики (используем chatId как Long)
                statisticsFiles.put(chatId, statsPath);

                sendMessage(chatId, "Обработка завершена! Теперь вы можете получить статистику.");
            } catch (Exception e) {
                sendMessage(chatId, "Ошибка при обработке: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    private void sendStatistics(long chatId) {
        try {
            String statsPath = statisticsFiles.get(chatId);
            File statsFile = new File(statsPath);

            SendDocument sendDocument = new SendDocument();
            sendDocument.setChatId(String.valueOf(chatId));
            sendDocument.setDocument(new InputFile(statsFile));
            sendDocument.setCaption("Статистика по сотрудникам");

            execute(sendDocument);
        } catch (Exception e) {
            sendMessage(chatId, "Ошибка при отправке статистики: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendWelcomeMessage(long chatId) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();


        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("Загрузить файл с задачами"));
        row1.add(new KeyboardButton("Загрузить файл с сотрудниками"));


        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("Запустить обработку"));
        row2.add(new KeyboardButton("Получить статистику"));

        keyboard.add(row1);
        keyboard.add(row2);
        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Добро пожаловать в Task Manager Bot!\n\n" +
                "1. Загрузите файлы с задачами и сотрудниками\n" +
                "2. Запустите обработку\n" +
                "3. Получите статистику\n\n" +
                "Выберите действие:");
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
