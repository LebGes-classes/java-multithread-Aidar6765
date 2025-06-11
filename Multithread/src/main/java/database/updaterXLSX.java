package database;

import object.Employee;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import process.Process;

import java.io.FileOutputStream;
import java.io.IOException;

public class updaterXLSX {
    public static void updateStatistics(String path){
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        for (int i = 0; i <= Process.employees.size(); i++){
            Row row = sheet.createRow(i);

            Cell id = row.createCell(0);
            Cell name = row.createCell(1);
            Cell countWork = row.createCell(2);
            Cell  plainTime = row.createCell(3);

            if (i == 0){
                id.setCellValue("id");
                name.setCellValue("Имя");
                countWork.setCellValue("Отработанное время");
                plainTime.setCellValue("Время простоя");
            }
            else{
                Employee employee = Process.employees.get(i - 1);
                id.setCellValue(employee.getId());
                name.setCellValue(employee.getName());
                countWork.setCellValue(employee.getCountWork());
                plainTime.setCellValue(employee.getPlainTime());

            }

        }
        try {
            workbook.write(new FileOutputStream(path));
            workbook.close();
        } catch (IOException ex){
            System.out.println("Ошибка записи данных в Excel");
            System.exit(0);
        }


    }

}
