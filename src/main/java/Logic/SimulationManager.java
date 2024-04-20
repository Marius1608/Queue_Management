package Logic;
import GUI.SimulationFrame;
import Model.Server;
import Model.Task;
import javax.swing.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimulationManager implements Runnable {

    private int timeLimit, maxProcessingTime, minProcessingTime, minArrivalTime;
    private int maxArrivalTime, numberOfServers, numberOfClients;
    private SelectionPolicy policy;

    private Scheduler scheduler;
    private SimulationFrame frame;
    private List<Task> generatedTasks;

    private double averageWaitingTime, averageServiceTime;
    private int peakHour;
    private int peakTaskCount;

    private FileWriter fileWriter;
    private BufferedWriter bufferedWriter;


    public SimulationManager(SimulationFrame frame) {
        this.frame = frame;
        timeLimit = 0;
        maxProcessingTime = 0;
        minProcessingTime = 0;
        minArrivalTime = 0;
        maxArrivalTime = 0;
        numberOfServers = 0;
        numberOfClients = 0;
        policy = null;
        scheduler = null;
        generatedTasks = new ArrayList<>();

        averageWaitingTime = 0.0;
        averageServiceTime = 0.0;
        peakHour = -1;
        peakTaskCount = 0;

        try {
            fileWriter = new FileWriter("Results3.txt");
            bufferedWriter = new BufferedWriter(fileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setSimulationParameters(int numberOfClients, int numberOfServers, int timeLimit, int minArrivalTime, int maxArrivalTime, int minProcessingTime, int maxProcessingTime, SelectionPolicy policy) {

        this.timeLimit = timeLimit;
        this.maxProcessingTime = maxProcessingTime;
        this.minProcessingTime = minProcessingTime;
        this.minArrivalTime = minArrivalTime;
        this.maxArrivalTime = maxArrivalTime;
        this.numberOfServers = numberOfServers;
        this.numberOfClients = numberOfClients;
        this.policy = policy;
        this.scheduler = new Scheduler(numberOfServers, numberOfClients);
        this.scheduler.changeStrategy(policy);

        ExecutorService executor = Executors.newFixedThreadPool(numberOfServers);
        for (int i = 0; i < numberOfServers; i++) {
            Server server = new Server(numberOfClients);
            executor.execute(server);
        }
        executor.shutdown(); //inchide executorul dupa ce sarciniile sunt finalizate
    }

    public void generateNRandomTasks(int minArrivalTime, int maxArrivalTime, int minServiceTime, int maxServiceTime) {

        for (int i = 0; i < numberOfClients; i++) {
            int arrivalTime = (int) (Math.random() * (maxArrivalTime - minArrivalTime + 1) + minArrivalTime);
            //+minArrivalTime asigura ca numerele respecta intervalul
            int serviceTime = (int) (Math.random() * (maxServiceTime - minServiceTime + 1) + minServiceTime);
            Task task = new Task(i + 1, arrivalTime, serviceTime);
            generatedTasks.add(task);
        }
        generatedTasks.sort(Comparator.comparing(Task::getArrivalTime));
    }

    public void run() {

        System.out.println("Simulation started.");
        writeFile("Simulation started."+"\n");

        int currentTime = 0;
        while (currentTime <= timeLimit && !generatedTasks.isEmpty()) {
            System.out.println("Current time: " + currentTime);
            writeFile("Current time: " + currentTime+ "\n");

            List<Task> processedTasks = new ArrayList<>();
            for (Task task : generatedTasks) {
                if (task.getArrivalTime() == currentTime) {
                    scheduler.dispatchTask(task);
                }
                if (task.getServiceTime() == 0) {
                    processedTasks.add(task);
                }
                if (task.getArrivalTime() > currentTime) {
                    System.out.print(task);
                    writeFile(String.valueOf(task));
                }
            }

            generatedTasks.removeAll(processedTasks);
            printTask();
            peekHourCalculate(currentTime);

            currentTime++;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Simulation ended.");
        writeFile("Simulation ended."+ "\n");

        calculateTime();
        closeFile();
    }

    private void printTask() {

        System.out.println();
        writeFile("\n");

        for (int i = 0; i < scheduler.getServers().size(); i++) {

            Server server = scheduler.getServers().get(i);
            Task[] tasksQueue = server.getTasks();
            System.out.print("Queue " + (i + 1) + ": ");
            writeFile("Queue " + (i + 1) + ": ");

            if (tasksQueue.length == 0) {
                System.out.println("Empty");
                writeFile("Empty"+"\n");
            } else {
                for (Task task : tasksQueue) {
                    System.out.print(task + " ");
                    writeFile(task + " ");
                }
                System.out.println();
                writeFile("\n");
            }
        }

        System.out.println();
        writeFile("\n");
    }

    private void peekHourCalculate(int currentTime) {

        for (int i = 0; i < scheduler.getServers().size(); i++) {
            Server server = scheduler.getServers().get(i);
            int queueSize = server.getTasks().length;
            if (queueSize > peakTaskCount) {
                peakTaskCount = queueSize;
                peakHour = currentTime;
            }
        }
    }

    public void calculateTime() {

        averageWaitingTime = (double) Server.getTotalWaitingTime() / numberOfClients;
        averageServiceTime = (double) Server.getTotalServiceTime() / Server.totalTaskCompleted;

        System.out.println("Average Waiting Time: " + averageWaitingTime);
        System.out.println("Average Service Time: " + averageServiceTime);
        System.out.println("Peak Hour: " + peakHour);

        writeFile("Average Waiting Time: " + averageWaitingTime+"\n");
        writeFile("Average Service Time: " + averageServiceTime+"\n");
        writeFile("Peak Hour: " + peakHour+"\n");
    }

    private void writeFile(String message) {

        try {
            bufferedWriter.write(message); //stocate intr un buffer intern
            bufferedWriter.flush(); // forțarea scrierea oricăror date restante din buffer către sursa de ieșire
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeFile() {
        try {
            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            SimulationFrame frame = new SimulationFrame();
            SimulationManager manager = new SimulationManager(frame);
            frame.setSimulationManager(manager);
            frame.setVisible(true);
        });
    }
}
