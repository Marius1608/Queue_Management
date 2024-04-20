package Logic;
import Model.Task;
import Model.Server;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Scheduler {

    private List<Server> servers;
    private Strategy strategy;
    private int maxNoServers,maxTasksPerServer;


    public Scheduler(int maxNoServers, int maxTasksPerServer) {

        this.servers=new ArrayList<>();
        this.maxNoServers = maxNoServers;
        this.maxTasksPerServer = maxTasksPerServer;
        this.strategy=new ConcreteStrategyQueue();
        ExecutorService executorService = Executors.newFixedThreadPool(maxNoServers);
        for (int i = 0; i < maxNoServers; i++) {
            Server server = new Server(maxTasksPerServer);
            servers.add(server);
            executorService.execute(server);
        }
    }


    public void changeStrategy(SelectionPolicy policy) {

        if (policy == SelectionPolicy.SHORTEST_QUEUE) {
            strategy = new ConcreteStrategyQueue();
        } else if (policy == SelectionPolicy.SHORTEST_TIME) {
            strategy = new ConcreteStrategyTime();
        }
    }


    public void dispatchTask(Task task) {
        strategy.addTask(servers, task);
    }


    public List<Server> getServers() {
        return servers;
    }

}
