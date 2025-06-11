package process;

import object.Employee;
import database.updaterXLSX;

import java.util.concurrent.CyclicBarrier;

public class afterCycle implements Runnable{
    @Override
    public void run() {
        if (Process.completedTasks != Process.totalTasks){

            Process.barrier = new CyclicBarrier(Process.countEmployers, new afterCycle());
            System.out.println("Прошел день");
            updaterXLSX.updateStatistics("D:/Java Projects/Multithread/src/main/resources/Statistics.xlsx");
        }
        else{
            Process.isProcess = false;
            updaterXLSX.updateStatistics("D:/Java Projects/Multithread/src/main/resources/Statistics.xlsx");
            System.out.println("Все задачи выполнены");
        }
    }
}
