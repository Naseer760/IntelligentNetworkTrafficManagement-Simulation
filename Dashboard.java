package networktraffic;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class Dashboard extends JFrame {

    // =====================================================
    // INPUT COMPONENTS
    // =====================================================

    private JTextField bufferField;
    private JTextField packetsField;
    private JTextField outputRateField;

    private JComboBox<String> trafficComboBox;

    // =====================================================
    // GUI COMPONENTS
    // =====================================================

    private JTextArea outputArea;

    private JButton startButton;
    private JButton clearButton;
    private JButton comparisonButton;

    private PerformancePanel performancePanel;

    private JTable comparisonTable;

    private DefaultTableModel comparisonTableModel;


    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public Dashboard() {

        setTitle(
                "Intelligent Network Buffer & Traffic Management"
        );

        setSize(1250, 850);

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        setLocationRelativeTo(null);


        // =================================================
        // MAIN PANEL
        // =================================================

        JPanel mainPanel =
                new JPanel(
                        new BorderLayout(10, 10)
                );

        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        10,
                        10,
                        10,
                        10
                )
        );


        // =================================================
        // TITLE
        // =================================================

        JLabel title =
                new JLabel(
                        "INTELLIGENT NETWORK BUFFER & "
                                + "TRAFFIC MANAGEMENT SYSTEM",
                        SwingConstants.CENTER
                );

        title.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        22
                )
        );

        mainPanel.add(
                title,
                BorderLayout.NORTH
        );


        // =================================================
        // LEFT CONFIGURATION PANEL
        // =================================================

        JPanel configurationPanel =
                new JPanel(
                        new GridLayout(
                                6,
                                2,
                                10,
                                10
                        )
                );

        configurationPanel.setBorder(
                BorderFactory.createTitledBorder(
                        "Simulation Configuration"
                )
        );


        // Buffer Capacity
        configurationPanel.add(
                new JLabel(
                        "Buffer Capacity:"
                )
        );

        bufferField =
                new JTextField("10");

        configurationPanel.add(
                bufferField
        );


        // Total Packets
        configurationPanel.add(
                new JLabel(
                        "Number of Packets:"
                )
        );

        packetsField =
                new JTextField("20");

        configurationPanel.add(
                packetsField
        );


        // Output Rate
        configurationPanel.add(
                new JLabel(
                        "Leaky Bucket Rate:"
                )
        );

        outputRateField =
                new JTextField("3");

        configurationPanel.add(
                outputRateField
        );


        // Traffic
        configurationPanel.add(
                new JLabel(
                        "Traffic Condition:"
                )
        );

        trafficComboBox =
                new JComboBox<>(
                        new String[]{
                                "LIGHT",
                                "MEDIUM",
                                "HEAVY"
                        }
                );

        configurationPanel.add(
                trafficComboBox
        );


        // Start
        startButton =
                new JButton(
                        "START SIMULATION"
                );

        configurationPanel.add(
                startButton
        );


        // Clear
        clearButton =
                new JButton(
                        "CLEAR"
                );

        configurationPanel.add(
                clearButton
        );


        // Comparison
        comparisonButton =
                new JButton(
                        "RUN COMPARISON"
                );

        configurationPanel.add(
                comparisonButton
        );


        // Empty component to maintain grid
        configurationPanel.add(
                new JLabel("")
        );


        mainPanel.add(
                configurationPanel,
                BorderLayout.WEST
        );


        // =================================================
        // CENTER PANEL
        // =================================================

        JPanel centerPanel =
                new JPanel(
                        new BorderLayout(10, 10)
                );


        // =================================================
        // LIVE PERFORMANCE GRAPH
        // =================================================

        performancePanel =
                new PerformancePanel();

        performancePanel.setPreferredSize(
                new Dimension(
                        750,
                        350
                )
        );


        centerPanel.add(
                performancePanel,
                BorderLayout.NORTH
        );


        // =================================================
        // LIVE OUTPUT
        // =================================================

        outputArea =
                new JTextArea();

        outputArea.setEditable(false);

        outputArea.setFont(
                new Font(
                        "Monospaced",
                        Font.PLAIN,
                        12
                )
        );


        JScrollPane outputScroll =
                new JScrollPane(
                        outputArea
                );


        outputScroll.setBorder(
                BorderFactory.createTitledBorder(
                        "Live Simulation Output"
                )
        );


        centerPanel.add(
                outputScroll,
                BorderLayout.CENTER
        );


        mainPanel.add(
                centerPanel,
                BorderLayout.CENTER
        );


        // =================================================
        // COMPARISON TABLE
        // =================================================

        String[] columns = {

                "Traffic",

                "Total",

                "Transmitted",

                "Dropped",

                "Avg Delay (ms)",

                "Drop Rate (%)",

                "Buffer Usage (%)"
        };


        comparisonTableModel =
                new DefaultTableModel(
                        columns,
                        0
                );


        comparisonTable =
                new JTable(
                        comparisonTableModel
                );


        comparisonTable.setRowHeight(
                28
        );


        comparisonTable.getTableHeader()
                .setFont(
                        new Font(
                                "Arial",
                                Font.BOLD,
                                12
                        )
                );


        JScrollPane comparisonScroll =
                new JScrollPane(
                        comparisonTable
                );


        comparisonScroll.setBorder(
                BorderFactory.createTitledBorder(
                        "Traffic Performance Comparison"
                )
        );


        comparisonScroll.setPreferredSize(
                new Dimension(
                        1200,
                        150
                )
        );


        mainPanel.add(
                comparisonScroll,
                BorderLayout.SOUTH
        );


        // =================================================
        // BUTTON EVENTS
        // =================================================

        startButton.addActionListener(
                e -> startSimulation()
        );


        clearButton.addActionListener(
                e -> clearDashboard()
        );


        comparisonButton.addActionListener(
                e -> runComparison()
        );


        // =================================================
        // ADD MAIN PANEL
        // =================================================

        add(mainPanel);
    }


    // =====================================================
    // START SINGLE SIMULATION
    // =====================================================

    private void startSimulation() {

        try {

            int bufferCapacity =
                    Integer.parseInt(
                            bufferField.getText()
                    );


            int totalPackets =
                    Integer.parseInt(
                            packetsField.getText()
                    );


            int outputRate =
                    Integer.parseInt(
                            outputRateField.getText()
                    );


            String trafficType =
                    (String)
                            trafficComboBox
                                    .getSelectedItem();


            // =============================================
            // VALIDATION
            // =============================================

            if (bufferCapacity <= 0
                    || totalPackets <= 0
                    || outputRate <= 0) {

                showError(
                        "All values must be greater than zero."
                );

                return;
            }


            // =============================================
            // ARRIVAL DELAY
            // =============================================

            int arrivalDelay =
                    getArrivalDelay(
                            trafficType
                    );


            // =============================================
            // RESET GRAPH
            // =============================================

            outputArea.setText("");

            performancePanel.clearData();

            performancePanel.setBufferCapacity(
                    bufferCapacity
            );


            startButton.setEnabled(false);


            appendOutput(
                    "========================================\n"
            );


            appendOutput(
                    "       LIVE TRAFFIC SIMULATION\n"
            );


            appendOutput(
                    "========================================\n\n"
            );


            appendOutput(
                    "Traffic Type    : "
                            + trafficType
                            + "\n"
            );


            appendOutput(
                    "Buffer Capacity : "
                            + bufferCapacity
                            + "\n"
            );


            appendOutput(
                    "Total Packets   : "
                            + totalPackets
                            + "\n"
            );


            appendOutput(
                    "Output Rate     : "
                            + outputRate
                            + "\n"
            );


            appendOutput(
                    "Arrival Delay   : "
                            + arrivalDelay
                            + " ms\n\n"
            );


            // =============================================
            // BACKGROUND THREAD
            // =============================================

            Thread simulationThread =
                    new Thread(() -> {

                        runSimulation(
                                bufferCapacity,
                                outputRate,
                                totalPackets,
                                trafficType,
                                arrivalDelay
                        );
                    });


            simulationThread.start();


        } catch (
                NumberFormatException ex) {

            showError(
                    "Please enter valid numeric values."
            );
        }
    }


    // =====================================================
    // RUN SIMULATION
    // =====================================================

    private void runSimulation(
            int bufferCapacity,
            int outputRate,
            int totalPackets,
            String trafficType,
            int arrivalDelay) {


        PacketGenerator generator =
                new PacketGenerator();


        NetworkBuffer buffer =
                new NetworkBuffer(
                        bufferCapacity
                );


        LeakyBucket leakyBucket =
                new LeakyBucket(
                        outputRate
                );


        Statistics statistics =
                new Statistics();


        statistics.setTotalPackets(
                totalPackets
        );


        final boolean[] arrivalFinished =
                {false};


        // =================================================
        // PACKET ARRIVAL
        // =================================================

        Thread arrivalThread =
                new Thread(() -> {

                    for (
                            int i = 1;
                            i <= totalPackets;
                            i++
                    ) {


                        Packet packet =
                                generator
                                        .generatePacket(i);


                        buffer.addPacket(
                                packet
                        );


                        statistics
                                .updateBufferUsage(
                                        buffer
                                                .getBufferSize()
                                );


                        appendOutput(
                                "ARRIVAL -> Packet "
                                        + packet.getId()
                                        + " | Priority: "
                                        + packet.getPriority()
                                        + " | Buffer: "
                                        + buffer
                                        .getBufferSize()
                                        + "\n"
                        );


                        updateGraph(
                                buffer.getBufferSize(),
                                leakyBucket
                                        .getTransmittedPackets(),
                                buffer
                                        .getDroppedPackets()
                        );


                        try {

                            Thread.sleep(
                                    arrivalDelay
                            );

                        } catch (
                                InterruptedException ex) {

                            Thread
                                    .currentThread()
                                    .interrupt();

                            return;
                        }
                    }


                    arrivalFinished[0] =
                            true;

                });


        // =================================================
        // PACKET TRANSMISSION
        // =================================================

        Thread transmissionThread =
                new Thread(() -> {

                    while (
                            !arrivalFinished[0]
                                    || !buffer
                                    .isEmpty()
                    ) {


                        if (!buffer.isEmpty()) {


                            leakyBucket
                                    .processPackets(
                                            buffer,
                                            statistics
                                    );


                            statistics
                                    .updateBufferUsage(
                                            buffer
                                                    .getBufferSize()
                                    );


                            appendOutput(
                                    "TRANSMISSION -> "
                                            + "Transmitted: "
                                            + leakyBucket
                                            .getTransmittedPackets()
                                            + " | Buffer: "
                                            + buffer
                                            .getBufferSize()
                                            + "\n"
                            );


                            updateGraph(
                                    buffer
                                            .getBufferSize(),
                                    leakyBucket
                                            .getTransmittedPackets(),
                                    buffer
                                            .getDroppedPackets()
                            );
                        }


                        try {

                            Thread.sleep(
                                    1000
                            );

                        } catch (
                                InterruptedException ex) {

                            Thread
                                    .currentThread()
                                    .interrupt();

                            return;
                        }
                    }
                });


        // =================================================
        // START THREADS
        // =================================================

        arrivalThread.start();

        transmissionThread.start();


        // =================================================
        // WAIT
        // =================================================

        try {

            arrivalThread.join();

            transmissionThread.join();

        } catch (
                InterruptedException ex) {

            Thread
                    .currentThread()
                    .interrupt();
        }


        // =================================================
        // FINAL RESULTS
        // =================================================

        statistics.setDroppedPackets(
                buffer.getDroppedPackets()
        );


        appendOutput(
                "\n========================================\n"
        );


        appendOutput(
                "           FINAL RESULTS\n"
        );


        appendOutput(
                "========================================\n"
        );


        appendOutput(
                "Transmitted : "
                        + leakyBucket
                        .getTransmittedPackets()
                        + "\n"
        );


        appendOutput(
                "Dropped     : "
                        + buffer
                        .getDroppedPackets()
                        + "\n"
        );


        appendOutput(
                String.format(
                        "Avg Delay   : %.2f ms%n",
                        statistics
                                .calculateAverageDelay()
                )
        );


        appendOutput(
                String.format(
                        "Drop Rate   : %.2f%%%n",
                        statistics
                                .calculateDropRate()
                )
        );


        appendOutput(
                String.format(
                        "Buffer Use  : %.2f%%%n",
                        statistics
                                .calculateBufferUtilization(
                                        bufferCapacity
                                )
                )
        );


        appendOutput(
                "========================================\n"
        );


        SwingUtilities.invokeLater(
                () -> startButton.setEnabled(true)
        );
    }


    // =====================================================
    // RUN LIGHT / MEDIUM / HEAVY COMPARISON
    // =====================================================

    private void runComparison() {

        try {

            int bufferCapacity =
                    Integer.parseInt(
                            bufferField.getText()
                    );


            int totalPackets =
                    Integer.parseInt(
                            packetsField.getText()
                    );


            int outputRate =
                    Integer.parseInt(
                            outputRateField.getText()
                    );


            if (bufferCapacity <= 0
                    || totalPackets <= 0
                    || outputRate <= 0) {

                showError(
                        "All values must be greater than zero."
                );

                return;
            }


            comparisonButton.setEnabled(
                    false
            );


            comparisonTableModel
                    .setRowCount(0);


            appendOutput(
                    "\n========================================\n"
            );


            appendOutput(
                    "       STARTING COMPARISON\n"
            );


            appendOutput(
                    "========================================\n"
            );


            Thread comparisonThread =
                    new Thread(() -> {

                        String[] trafficTypes = {

                                "LIGHT",
                                "MEDIUM",
                                "HEAVY"
                        };


                        for (
                                String traffic :
                                trafficTypes
                        ) {


                            int arrivalDelay =
                                    getArrivalDelay(
                                            traffic
                                    );


                            appendOutput(
                                    "\nRunning "
                                            + traffic
                                            + " traffic...\n"
                            );


                            ComparisonResult result =
                                    executeComparisonSimulation(
                                            bufferCapacity,
                                            outputRate,
                                            totalPackets,
                                            traffic,
                                            arrivalDelay
                                    );


                            addComparisonResult(
                                    result
                            );
                        }


                        appendOutput(
                                "\nComparison completed.\n"
                        );


                        SwingUtilities.invokeLater(
                                () -> comparisonButton
                                        .setEnabled(true)
                        );
                    });


            comparisonThread.start();


        } catch (
                NumberFormatException ex) {

            showError(
                    "Please enter valid numeric values."
            );
        }
    }


    // =====================================================
    // COMPARISON SIMULATION
    // =====================================================

    private ComparisonResult
    executeComparisonSimulation(
            int bufferCapacity,
            int outputRate,
            int totalPackets,
            String trafficType,
            int arrivalDelay) {


        PacketGenerator generator =
                new PacketGenerator();


        NetworkBuffer buffer =
                new NetworkBuffer(
                        bufferCapacity
                );


        LeakyBucket leakyBucket =
                new LeakyBucket(
                        outputRate
                );


        Statistics statistics =
                new Statistics();


        statistics.setTotalPackets(
                totalPackets
        );


        final boolean[] arrivalFinished =
                {false};


        Thread arrivalThread =
                new Thread(() -> {

                    for (
                            int i = 1;
                            i <= totalPackets;
                            i++
                    ) {


                        Packet packet =
                                generator
                                        .generatePacket(i);


                        buffer.addPacket(
                                packet
                        );


                        statistics
                                .updateBufferUsage(
                                        buffer
                                                .getBufferSize()
                                );


                        try {

                            Thread.sleep(
                                    arrivalDelay
                            );

                        } catch (
                                InterruptedException ex) {

                            Thread
                                    .currentThread()
                                    .interrupt();

                            return;
                        }
                    }


                    arrivalFinished[0] =
                            true;
                });


        Thread transmissionThread =
                new Thread(() -> {

                    while (
                            !arrivalFinished[0]
                                    || !buffer
                                    .isEmpty()
                    ) {


                        if (!buffer.isEmpty()) {


                            leakyBucket
                                    .processPackets(
                                            buffer,
                                            statistics
                                    );


                            statistics
                                    .updateBufferUsage(
                                            buffer
                                                    .getBufferSize()
                                    );
                        }


                        try {

                            Thread.sleep(
                                    1000
                            );

                        } catch (
                                InterruptedException ex) {

                            Thread
                                    .currentThread()
                                    .interrupt();

                            return;
                        }
                    }
                });


        arrivalThread.start();

        transmissionThread.start();


        try {

            arrivalThread.join();

            transmissionThread.join();

        } catch (
                InterruptedException ex) {

            Thread
                    .currentThread()
                    .interrupt();
        }


        statistics.setDroppedPackets(
                buffer.getDroppedPackets()
        );


        return new ComparisonResult(

                trafficType,

                totalPackets,

                leakyBucket
                        .getTransmittedPackets(),

                buffer
                        .getDroppedPackets(),

                statistics
                        .calculateAverageDelay(),

                statistics
                        .calculateDropRate(),

                statistics
                        .calculateBufferUtilization(
                                bufferCapacity
                        )
        );
    }


    // =====================================================
    // ADD COMPARISON RESULT
    // =====================================================

    private void addComparisonResult(
            ComparisonResult result) {


        SwingUtilities.invokeLater(() -> {

            comparisonTableModel.addRow(
                    new Object[]{

                            result.getTrafficType(),

                            result.getTotalPackets(),

                            result
                                    .getTransmittedPackets(),

                            result
                                    .getDroppedPackets(),

                            String.format(
                                    "%.2f",
                                    result
                                            .getAverageDelay()
                            ),

                            String.format(
                                    "%.2f",
                                    result
                                            .getDropRate()
                            ),

                            String.format(
                                    "%.2f",
                                    result
                                            .getBufferUtilization()
                            )
                    }
            );
        });
    }


    // =====================================================
    // GET ARRIVAL DELAY
    // =====================================================

    private int getArrivalDelay(
            String trafficType) {

        switch (trafficType) {

            case "LIGHT":

                return 1000;

            case "MEDIUM":

                return 500;

            case "HEAVY":

                return 100;

            default:

                return 500;
        }
    }


    // =====================================================
    // GRAPH UPDATE
    // =====================================================

    private void updateGraph(
            int bufferSize,
            int transmitted,
            int dropped) {


        SwingUtilities.invokeLater(
                () -> performancePanel.addData(
                        bufferSize,
                        transmitted,
                        dropped
                )
        );
    }


    // =====================================================
    // OUTPUT UPDATE
    // =====================================================

    private void appendOutput(
            String text) {


        SwingUtilities.invokeLater(() -> {

            outputArea.append(text);

            outputArea.setCaretPosition(
                    outputArea
                            .getDocument()
                            .getLength()
            );
        });
    }


    // =====================================================
    // CLEAR DASHBOARD
    // =====================================================

    private void clearDashboard() {

        outputArea.setText("");

        performancePanel.clearData();

        comparisonTableModel
                .setRowCount(0);
    }


    // =====================================================
    // ERROR MESSAGE
    // =====================================================

    private void showError(
            String message) {

        JOptionPane.showMessageDialog(
                this,
                message,
                "Input Error",
                JOptionPane.ERROR_MESSAGE
        );
    }


    // =====================================================
    // MAIN
    // =====================================================

    public static void main(
            String[] args) {


        SwingUtilities.invokeLater(() -> {

            Dashboard dashboard =
                    new Dashboard();

            dashboard.setVisible(true);
        });
    }
}
