import object.Task;
import database.*;
import process.Process;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import telegram.Bot;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
//        try {
//            // Создаем папку для временных файлов
//            Files.createDirectories(Paths.get("temp_files"));
//
//            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
//            botsApi.registerBot(new Bot());
//            System.out.println("Бот успешно запущен!");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        Process.process("D:/Java Projects/Multithread/src/main/resources/Tasks.xlsx",
                "D:/Java Projects/Multithread/src/main/resources/Employers.xlsx",
                "D:/Java Projects/Multithread/src/main/resources/Statistics.xlsx");
    }
}
