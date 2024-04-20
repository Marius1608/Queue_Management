package Model;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable {

    private final BlockingQueue<Task> tasks;
    private final AtomicInteger waitingPeriod;
    public static int totalServiceTime;
    public static int totalWaitingTime;
    public static int totalTaskCompleted;


    public Server(int maxTasks)
    {
        this.tasks = new LinkedBlockingQueue<>(maxTasks);
        this.waitingPeriod = new AtomicInteger(0);
    }


    public void addTask(Task newTask)
    {
        tasks.add(newTask);
        totalWaitingTime+=waitingPeriod.get();
        waitingPeriod.addAndGet(newTask.getServiceTime());
    }


    @Override
    public void run() {
        while (true) {
            if (!tasks.isEmpty()) {
                Task currentTask = tasks.peek();
                int serviceTime=currentTask.getServiceTime();
                while (currentTask != null && currentTask.getServiceTime() > 0) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    waitingPeriod.getAndDecrement();
                    currentTask.setServiceTime(currentTask.getServiceTime() - 1);
                }
                tasks.poll();
                totalTaskCompleted++;
                totalServiceTime+=serviceTime;
            }
        }
    }
    

    public Task[] getTasks() {
        return tasks.toArray(new Task[0]);
    }


    public int getWaitingPeriod() {
        return waitingPeriod.get();
    }


    public static int getTotalServiceTime() {
        return totalServiceTime;
    }


    public static int getTotalWaitingTime() {
        return totalWaitingTime;
    }


    public static int totalTaskCompleted() {return totalTaskCompleted;}
}

