package Logic;
import Model.Server;
import Model.Task;

import java.util.List;

public class ConcreteStrategyTime implements Strategy {

    @Override
    public void addTask(List<Server> servers, Task task) {
        Server shortServer= servers.getFirst();
        for (Server server : servers) {
            if (server.getWaitingPeriod() == 0) {
                shortServer = server;
                break;
            }
            else {
                if (server.getWaitingPeriod() < shortServer.getWaitingPeriod()) {
                    shortServer = server;
                }
            }
        }
        shortServer.addTask(task);
    }
}
