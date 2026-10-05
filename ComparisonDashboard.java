package networktraffic;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ComparisonDashboard extends JFrame {

    private JTable resultTable;

    private DefaultTableModel tableModel;

    private JButton runButton;

    private JTextArea statusArea;

    private int bufferCapacity = 10;

    private int totalPackets = 20;

    private int outputRate = 3;


    public ComparisonDashboard() {

        setTitle(
                "Network Traffic Performance Comparison"
        );

        setSize(1100, 650);

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        setLocationRelativeTo(null);


        // =========================================
        // MAIN PANEL
        // =========================================

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


        // =========================================
        // TITLE
        // =========================================

        JLabel title =
                new JLabel(
                        "NETWORK TRAFFIC PERFORMANCE COMPARISON",
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


        // =========================================
        // TABLE
        // =========================================

        String[] columns = {

                "Traffic",

                "Total Packets",

                "Transmitted",

                "Dropped",

                "Avg Delay (ms)",

                "Drop Rate (%)",

                "Buffer Usage (%)"
        };


        tableModel =
                new DefaultTableModel(
                        columns,
                        0
                );


        resultTable =
                new JTable(tableModel);


        resultTable.setRowHeight(30);


        resultTable.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        13
                )
        );


        resultTable.getTableHeader()
                .setFont(
                        new Font(
                                "Arial",
                                Font.BOLD,
                                13
                        )
                );


        JScrollPane tableScroll =
                new JScrollPane(
                        resultTable
                );


        mainPanel.add(
                tableScroll,
                BorderLayout.CENTER
        );


        // =========================================
        // BOTTOM PANEL
        // =========================================

        JPanel bottomPanel =
                new JPanel(
                        new BorderLayout(10, 10)
                );


        runButton =
                new JButton(
                        "RUN COMPARISON"
                );


        runButton.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        14
                )
        );


        bottomPanel.add(
                runButton,
                BorderLayout.WEST
        );


        statusArea =
                new JTextArea(4, 50);


        statusArea.setEditable(false);


        statusArea.setFont(
                new Font(
                        "Monospaced",
                        Font.PLAIN,
                        12
                )
        );


        bottomPanel.add(
                new JScrollPane(statusArea),
                BorderLayout.CENTER
        );


        mainPanel.add(
                bottomPanel,
                BorderLayout.SOUTH
        );


        // =========================================
        // BUTTON EVENT
        // =========================================

        runButton.addActionListener(
                e -> runComparison()
        );


        add(mainPanel);
    }


    // =============================================
    // RUN COMPARISON
    // =============================================

    private void runComparison() {

        runButton.setEnabled(false);

        tableModel.setRowCount(0);

        statusArea.setText("");

        Thread comparisonThread =
                new Thread(() -> {

                    String[] trafficTypes = {

                            "LIGHT",
                            "MEDIUM",
                            "HEAVY"
                    };


                    int[] arrivalDelays = {

                            1000,
                            500,
                            100
                    };


                    for (int i = 0;
                         i < trafficTypes.length;
                         i++) {

                        String traffic =
                                trafficTypes[i];

                        int arrivalDelay =
                                arrivalDelays[i];


                        appendStatus(
                                "Running "
                                        + traffic
                                        + " traffic...\n"
                        );


                        ComparisonResult result =
                                runSingleSimulation(
                                        traffic,
                                        arrivalDelay
                                );


                        addResultToTable(
                                result
                        );


                        appendStatus(
                                traffic
                                        + " simulation completed.\n"
                        );
                    }


                    appendStatus(
                            "\nAll simulations completed."
                    );


                    SwingUtilities.invokeLater(
                            () -> runButton
                                    .setEnabled(true)
                    );
                });


        comparisonThread.start();
    }


    // =============================================
    // RUN ONE SIMULATION
    // =============================================

    private ComparisonResult runSingleSimulation(
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


        boolean[] arrivalFinished =
                {false};


        // =========================================
        // ARRIVAL THREAD
        // =========================================

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
                                InterruptedException e) {

                            Thread
                                    .currentThread()
                                    .interrupt();

                            return;
                        }
                    }


                    arrivalFinished[0] =
                            true;
                });


        // =========================================
        // TRANSMISSION THREAD
        // =========================================

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
                                InterruptedException e) {

                            Thread
                                    .currentThread()
                                    .interrupt();

                            return;
                        }
                    }
                });


        arrivalThread.start();

        transmissionThread.start();


        // =========================================
        // WAIT
        // =========================================

        try {

            arrivalThread.join();

            transmissionThread.join();

        } catch (
                InterruptedException e) {

            Thread
                    .currentThread()
                    .interrupt();
        }


        // =========================================
        // CALCULATE RESULTS
        // =========================================

        int dropped =
                buffer.getDroppedPackets();


        statistics.setDroppedPackets(
                dropped
        );


        int transmitted =
                leakyBucket
                        .getTransmittedPackets();


        double averageDelay =
                statistics
                        .calculateAverageDelay();


        double dropRate =
                statistics
                        .calculateDropRate();


        double bufferUsage =
                statistics
                        .calculateBufferUtilization(
                                bufferCapacity
                        );


        return new ComparisonResult(

                trafficType,

                totalPackets,

                transmitted,

                dropped,

                averageDelay,

                dropRate,

                bufferUsage
        );
    }


    // =============================================
    // ADD RESULT TO TABLE
    // =============================================

    private void addResultToTable(
            ComparisonResult result) {

        SwingUtilities.invokeLater(() -> {

            tableModel.addRow(
                    new Object[]{

                            result.getTrafficType(),

                            result.getTotalPackets(),

                            result.getTransmittedPackets(),

                            result.getDroppedPackets(),

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


    // =============================================
    // STATUS OUTPUT
    // =============================================

    private void appendStatus(
            String message) {

        SwingUtilities.invokeLater(() -> {

            statusArea.append(message);

            statusArea.setCaretPosition(
                    statusArea
                            .getDocument()
                            .getLength()
            );
        });
    }


    // =============================================
    // MAIN
    // =============================================

    public static void main(
            String[] args) {

        SwingUtilities.invokeLater(() -> {

            ComparisonDashboard dashboard =
                    new ComparisonDashboard();

            dashboard.setVisible(true);
        });
    }
}
