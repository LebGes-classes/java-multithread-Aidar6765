package object;

import java.util.ArrayList;
import java.util.concurrent.BrokenBarrierException;

import database.loadXLSX;
import process.Process;

public class Employee implements Runnable {
    private int id;
    private String name;
    private Task completeTask;
    private int workTime;
    private int amountTask;
    private int hourInDay;
    private int plainTime;
    private int countWork;


    public Employee(int id, String name, int workTime) {
        this.id = id;
        this.name = name;
        this.workTime = workTime;
        amountTask = 0;
        hourInDay = 0;
        completeTask = null;
        plainTime = 0;
        countWork = 0;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getWorkTime() {
        return workTime;
    }

    public int getAmountTask(){
        return amountTask;
    }

    public int getCountWork() {
        return countWork;
    }

    public int getHourInDay() {
        return hourInDay;
    }

    public int getPlainTime() {
        return plainTime;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setWorkTime(int workTime) {
        this.workTime = workTime;
    }

    public void setAmountTask(int amountTask) {
        this.amountTask = amountTask;
    }



    public void setHourInDay(int hourInDay) {
        this.hourInDay = hourInDay;
    }



    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", completeTask=" + completeTask +
                ", workTime=" + workTime +
                ", amountTask=" + amountTask +
                ", hourInDay=" + hourInDay +
                "plainTime=" + plainTime +
                "countWork=" + countWork +
                '}';
    }

    @Override
    public void run() {

        while (Task.getTasks().size() > 0 || completeTask != null){
            
            if (completeTask == null){
                completeTask = Task.getTask();
                System.out.println("Работник с id "+ id + " взял задачу с номером " + completeTask.getId());
                
            }
            

                if (completeTask.getDuration() - completeTask.getProgress() > workTime - hourInDay){
                    completeTask.setProgress(completeTask.getProgress() + (workTime - hourInDay));
                    try {
                        Thread.sleep(1000);
                        countWork += workTime;
                        Process.barrier.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } catch (BrokenBarrierException e) {
                        throw new RuntimeException(e);
                    }
                    hourInDay = 0;
                }
                else{
                    hourInDay += (completeTask.getDuration() - completeTask.getProgress());
                    completeTask.setProgress(completeTask.getDuration());
                    completeTask = null;
                    Process.completedTasks += 1;
                    amountTask += 1;

                    if (hourInDay == workTime || Task.getTasks().size() == 0){
                        plainTime += 8 - hourInDay;
                        countWork += hourInDay;
                        try {
                            Thread.sleep(1000);
                            Process.barrier.await();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        } catch (BrokenBarrierException e) {
                            throw new RuntimeException(e);
                        }
                        hourInDay = 0;
                    }

                    

            }

        }
        while (Process.isProcess){
            plainTime += 8;
            try {
                Process.barrier.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (BrokenBarrierException e) {
                throw new RuntimeException(e);
            }
        }



    }
}
