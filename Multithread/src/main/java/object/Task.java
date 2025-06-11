package object;

import java.util.ArrayList;
import java.util.NoSuchElementException;

public class Task {
    private static ArrayList<Task> tasks;
    private int id;
    private int duration;
    private int progress;

    public Task(int id, int duration){
        this.id = id;
        this.duration = duration;
        progress = 0;
    }

    public static Task getTask(){

        synchronized (tasks){
            if (tasks.size() == 0){
                throw new NoSuchElementException();
            }
            else{
                Task task = tasks.getFirst();
                tasks.removeFirst();
                return task;
            }
        }
    }

    public int getId() {
        return id;
    }

    public int getDuration() {
        return duration;
    }

    public int getProgress() {
        return progress;
    }

    public static ArrayList<Task> getTasks() {
        return tasks;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static void setTasks(ArrayList<Task> tasks) {
        Task.tasks = tasks;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", duration=" + duration +
                ", progress=" + progress +
                '}';
    }
}
