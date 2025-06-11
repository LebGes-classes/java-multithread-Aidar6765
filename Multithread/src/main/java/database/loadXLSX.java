package database;

import object.Employee;
import object.Task;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class loadXLSX {
    public static ArrayList<Employee> loadEmployee(String path){
        try{
            ArrayList<Employee> employees = new ArrayList<>();
            XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(path));
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet.getPhysicalNumberOfRows() == 0 || sheet.getPhysicalNumberOfRows() == 1){
                System.out.println("Список работников пуст");
                System.exit(0);
            }
            for (Row row : sheet){
                if (row.getRowNum() == 0){
                    continue;
                }
                Employee employee = new Employee((int) row.getCell(0).getNumericCellValue(),
                                                row.getCell(1).getStringCellValue(),
                                                (int) row.getCell(2).getNumericCellValue());

                if (employee.getId() == 0){
                    continue;
                }
                if (employee.getWorkTime() <= 0 || employee.getWorkTime() > 8){
                    System.out.println("У работника с id " + employee.getId() + " указано неправильное время работы");
                    System.exit(0);
                }
                employees.add(employee);
            }

            return employees;


        }catch(IOException ex){
            System.out.println("Ошибка при загрузке данных");
            System.exit(0);
        }catch (NullPointerException ex){
            System.out.println("В файле работников имеются пустые клетки");
            System.exit(0);
        }
        return null;
    }

    public static ArrayList<Task> loadTask(String path){
        try{
            ArrayList<Task> tasks = new ArrayList<>();
            XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(path));
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet.getPhysicalNumberOfRows() == 0 || sheet.getPhysicalNumberOfRows() == 1){
                System.out.println("Список задач пуст");
                System.exit(0);
            }
            for (Row row : sheet){
                if (row.getRowNum() == 0){
                    continue;
                }
                Task task = new Task((int) row.getCell(0).getNumericCellValue(),
                        (int) row.getCell(1).getNumericCellValue());
                if (task.getId() == 0){
                    continue;
                }
                if (task.getDuration() > 16 || task.getDuration() <= 0){
                    System.out.println("Неправильно указана длительность задачи с id " + task.getId());
                    System.exit(0);
                }
                tasks.add(task);

            }
            return tasks;
        }catch (IOException ex){
            System.out.println("Ошибка при загрузке данных");
            System.exit(0);
        }catch (NullPointerException ex){
            System.out.println("В файле задач имеются пустые клетки");
        }
        return null;
    }

}
