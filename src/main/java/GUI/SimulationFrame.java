package GUI;
import Logic.SelectionPolicy;
import Logic.SimulationManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SimulationFrame extends JFrame {

    private JTextField clientsField,queuesField,simulationIntervalField,minArrivalTimeField;
    private JTextField maxArrivalTimeField,minServiceTimeField,maxServiceTimeField;
    private JButton queueStrategyButton,timeStrategyButton;
    private SimulationManager manager;


    public SimulationFrame() {

        setTitle("Queue Management Simulation");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(9, 3));

        add(new JLabel("Number of Clients:"));
        clientsField = new JTextField();
        clientsField.setBackground(Color.cyan);
        add(clientsField);

        add(new JLabel("Number of Queues:"));
        queuesField = new JTextField();
        queuesField.setBackground(Color.cyan);
        add(queuesField);

        add(new JLabel("Simulation Interval (seconds):"));
        simulationIntervalField = new JTextField();
        simulationIntervalField.setBackground(Color.cyan);
        add(simulationIntervalField);

        add(new JLabel("Min Arrival Time:"));
        minArrivalTimeField = new JTextField();
        minArrivalTimeField.setBackground(Color.cyan);
        add(minArrivalTimeField);

        add(new JLabel("Max Arrival Time:"));
        maxArrivalTimeField = new JTextField();
        maxArrivalTimeField.setBackground(Color.cyan);
        add(maxArrivalTimeField);

        add(new JLabel("Min Service Time:"));
        minServiceTimeField = new JTextField();
        minServiceTimeField.setBackground(Color.cyan);
        add(minServiceTimeField);

        add(new JLabel("Max Service Time:"));
        maxServiceTimeField = new JTextField();
        maxServiceTimeField.setBackground(Color.cyan);
        add(maxServiceTimeField);

        queueStrategyButton = new JButton("Shortest Queue Strategy");
        queueStrategyButton.setBackground(Color.cyan);
        add(queueStrategyButton);


        ActionListener queueStrategyListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                manager.setSimulationParameters(
                        Integer.parseInt(clientsField.getText()),
                        Integer.parseInt(queuesField.getText()),
                        Integer.parseInt(simulationIntervalField.getText()),
                        Integer.parseInt(minArrivalTimeField.getText()),
                        Integer.parseInt(maxArrivalTimeField.getText()),
                        Integer.parseInt(minServiceTimeField.getText()),
                        Integer.parseInt(maxServiceTimeField.getText()),
                        SelectionPolicy.SHORTEST_QUEUE);
                manager.generateNRandomTasks(
                        Integer.parseInt(minArrivalTimeField.getText()),
                        Integer.parseInt(maxArrivalTimeField.getText()),
                        Integer.parseInt(minServiceTimeField.getText()),
                        Integer.parseInt(maxServiceTimeField.getText()));
                Thread t = new Thread(manager);
                t.start();
            }
        };
        queueStrategyButton.addActionListener(queueStrategyListener);


        timeStrategyButton = new JButton("Shortest Time Strategy");
        timeStrategyButton.setBackground(Color.cyan);
        add(timeStrategyButton);
        ActionListener timeStrategyListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                manager.setSimulationParameters(
                        Integer.parseInt(clientsField.getText()),
                        Integer.parseInt(queuesField.getText()),
                        Integer.parseInt(simulationIntervalField.getText()),
                        Integer.parseInt(minArrivalTimeField.getText()),
                        Integer.parseInt(maxArrivalTimeField.getText()),
                        Integer.parseInt(minServiceTimeField.getText()),
                        Integer.parseInt(maxServiceTimeField.getText()),
                        SelectionPolicy.SHORTEST_TIME);
                manager.generateNRandomTasks(
                        Integer.parseInt(minArrivalTimeField.getText()),
                        Integer.parseInt(maxArrivalTimeField.getText()),
                        Integer.parseInt(minServiceTimeField.getText()),
                        Integer.parseInt(maxServiceTimeField.getText()));
                Thread t = new Thread(manager);
                t.start();
            }
        };
        timeStrategyButton.addActionListener(timeStrategyListener);
    }


    public void setSimulationManager(SimulationManager manager) {
        this.manager = manager;
    }
}
