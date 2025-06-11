package process;

import database.loadXLSX;
import object.Employee;
import object.Task;

import java.util.ArrayList;
import java.util.concurrent.CyclicBarrier;

public class Process {
    public static int totalTasks;
    public static int completedTasks;
    public static ArrayList<Employee> employees;
    public static int countEmployers;
    public static CyclicBarrier barrier;
    public static boolean isProcess;


    public static void process(String taskPath, String employeePath){
        isProcess = true;
        Task.setTasks(loadXLSX.loadTask(taskPath));
        employees = loadXLSX.loadEmployee(employeePath);
        countEmployers = employees.size();
        completedTasks = 0;
        totalTasks = Task.getTasks().size();

        barrier = new CyclicBarrier(Process.countEmployers, new afterCycle());
        for (Employee employee : employees){
            Thread thread = new Thread(employee);
            thread.start();
        }
    }
}
