package networktraffic;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.util.List;

/**
 * Main application dashboard for the Intelligent Network Buffer and Traffic
 * Management System. The visualization is driven by real Packet objects from
 * the simulation.
 */
public class Dashboard extends JFrame {

    private JTextField bufferField;
    private JTextField packetsField;
    private JTextField outputRateField;
    private JComboBox<String> trafficComboBox;
    private JComboBox<String> speedComboBox;

    private JTextArea outputArea;
    private JButton startButton;
    private JButton clearButton;
    private JButton comparisonButton;

    private PacketVisualizationPanel visualizationPanel;
    private PerformancePanel performancePanel;
    private JTable packetTable;
    private DefaultTableModel packetTableModel;
    private JTable comparisonTable;
    private DefaultTableModel comparisonTableModel;

    private JLabel statusLabel;
    private JLabel totalValue;
    private JLabel transmittedValue;
    private JLabel droppedValue;
    private JLabel delayValue;
    private JLabel dropRateValue;
    private JLabel bufferUsageValue;

    private JScrollPane wholeDashboardScroll;
    private JPanel dashboardRoot;

    public Dashboard() {
        setTitle("Intelligent Network Buffer and Traffic Management System");
        setSize(1500, 950);
        setMinimumSize(new Dimension(1200, 750));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        dashboardRoot = new JPanel(new BorderLayout(10, 10));
        dashboardRoot.setBorder(new EmptyBorder(10, 10, 10, 10));
        dashboardRoot.setBackground(new Color(245, 247, 250));

        dashboardRoot.add(buildHeader(), BorderLayout.NORTH);
        dashboardRoot.add(buildMainContent(), BorderLayout.CENTER);

        wholeDashboardScroll = new JScrollPane(dashboardRoot);
        wholeDashboardScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        wholeDashboardScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        wholeDashboardScroll.setBorder(null);
        wholeDashboardScroll.getVerticalScrollBar().setUnitIncrement(25);
        wholeDashboardScroll.getHorizontalScrollBar().setUnitIncrement(25);
        wholeDashboardScroll.getViewport().setBackground(new Color(245, 247, 250));

        installWholeDashboardMouseWheel();
        setContentPane(wholeDashboardScroll);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(8, 8));
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(215, 219, 225)),
                new EmptyBorder(10, 14, 10, 14)));

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("INTELLIGENT NETWORK BUFFER & TRAFFIC MANAGEMENT SYSTEM");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(new Color(20, 28, 40));

        JLabel subtitle = new JLabel("Priority Queue + Leaky Bucket Traffic Management");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitle.setForeground(new Color(90, 98, 108));

        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(3));
        titlePanel.add(subtitle);
        header.add(titlePanel, BorderLayout.WEST);

        statusLabel = new JLabel("● READY");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 13));
        statusLabel.setForeground(new Color(21, 128, 61));
        header.add(statusLabel, BorderLayout.EAST);
        return header;
    }

    private JPanel buildMainContent() {
        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setOpaque(false);

        // ---------------------------------------------------------
        // TOP: smaller configuration + full visualization
        // ---------------------------------------------------------
        JPanel top = new JPanel(new BorderLayout(10, 10));
        top.setOpaque(false);

        JPanel configuration = buildConfigurationPanel();
        configuration.setPreferredSize(new Dimension(195, 220));
        top.add(configuration, BorderLayout.WEST);

        visualizationPanel = new PacketVisualizationPanel();
        visualizationPanel.setPreferredSize(new Dimension(1030, 390));

        JPanel visualizationCard = wrap(
                "Network Simulation Visualization",
                visualizationPanel);
        visualizationCard.setPreferredSize(new Dimension(1030, 425));

        top.add(visualizationCard, BorderLayout.CENTER);
        content.add(top, BorderLayout.NORTH);

        // ---------------------------------------------------------
        // MIDDLE: log + larger performance metrics
        // ---------------------------------------------------------
        JPanel middle = new JPanel(new BorderLayout(10, 10));
        middle.setOpaque(false);

        JPanel output = buildOutputPanel();
        output.setPreferredSize(new Dimension(760, 220));

        JPanel metrics = buildMetricsPanel();
        metrics.setPreferredSize(new Dimension(400, 220));

        middle.add(output, BorderLayout.CENTER);
        middle.add(metrics, BorderLayout.EAST);
        content.add(middle, BorderLayout.CENTER);

        // ---------------------------------------------------------
        // BOTTOM: packet details, comparison and graph
        // ---------------------------------------------------------
        JPanel bottom = new JPanel();
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.setOpaque(false);

        JPanel packetPanel = buildPacketTablePanel();
        packetPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        packetPanel.setPreferredSize(new Dimension(1280, 190));
        packetPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 190));
        bottom.add(packetPanel);
        bottom.add(Box.createVerticalStrut(10));

        JPanel comparisonPanel = buildComparisonPanel();
        comparisonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        comparisonPanel.setPreferredSize(new Dimension(1280, 180));
        comparisonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));
        bottom.add(comparisonPanel);
        bottom.add(Box.createVerticalStrut(10));

        // Create the graph ONCE. Do not add it dynamically from startSimulation().
        performancePanel = new PerformancePanel();
        performancePanel.setBufferCapacity(10);
        performancePanel.setPreferredSize(new Dimension(1280, 300));

        JPanel graphHolder = wrap("Live Performance Graph", performancePanel);
        graphHolder.setAlignmentX(Component.LEFT_ALIGNMENT);
        graphHolder.setPreferredSize(new Dimension(1280, 330));
        graphHolder.setMaximumSize(new Dimension(Integer.MAX_VALUE, 330));
        bottom.add(graphHolder);

        content.add(bottom, BorderLayout.SOUTH);

        // Force enough vertical content for the main dashboard scrollbar.
        content.setPreferredSize(new Dimension(1280, 1450));

        return content;
    }

    private JPanel buildConfigurationPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setPreferredSize(new Dimension(205, 250));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Simulation Configuration"),
                new EmptyBorder(4, 6, 6, 6)));

        bufferField = new JTextField("10", 7);
        packetsField = new JTextField("20", 7);
        outputRateField = new JTextField("3", 7);
        trafficComboBox = new JComboBox<>(new String[]{"LIGHT", "MEDIUM", "HEAVY"});
        speedComboBox = new JComboBox<>(new String[]{"0.2x","0.5x", "1x", "1.5x"});
        speedComboBox.setSelectedItem("1x");

        addField(panel, 0, "Buffer Capacity", bufferField);
        addField(panel, 1, "Number of Packets", packetsField);
        addField(panel, 2, "Leaky Bucket Rate", outputRateField);
        addField(panel, 3, "Traffic Condition", trafficComboBox);
        addField(panel, 4, "Visualization Speed", speedComboBox);

        startButton = new JButton("START SIMULATION");
        clearButton = new JButton("CLEAR");
        comparisonButton = new JButton("RUN COMPARISON");

        startButton.setFont(new Font("Arial", Font.BOLD, 11));
        comparisonButton.setFont(new Font("Arial", Font.BOLD, 11));
        clearButton.setFont(new Font("Arial", Font.BOLD, 11));

        GridBagConstraints b = new GridBagConstraints();
        b.gridx = 0;
        b.gridy = 5;
        b.gridwidth = 2;
        b.weightx = 1;
        b.fill = GridBagConstraints.HORIZONTAL;
        b.insets = new Insets(5, 2, 3, 2);
        panel.add(startButton, b);

        b.gridy = 6;
        panel.add(comparisonButton, b);

        b.gridy = 7;
        panel.add(clearButton, b);

        startButton.addActionListener(e -> startSimulation());
        clearButton.addActionListener(e -> clearDashboard());
        comparisonButton.addActionListener(e -> runComparison());

        return panel;
    }

    private void addField(JPanel panel, int row, String label, JComponent field) {
        GridBagConstraints l = new GridBagConstraints();
        l.gridx = 0;
        l.gridy = row;
        l.anchor = GridBagConstraints.WEST;
        l.insets = new Insets(4, 2, 4, 4);
        panel.add(new JLabel(label + ":"), l);

        GridBagConstraints f = new GridBagConstraints();
        f.gridx = 1;
        f.gridy = row;
        f.weightx = 1;
        f.fill = GridBagConstraints.HORIZONTAL;
        f.insets = new Insets(4, 0, 4, 2);
        panel.add(field, f);
    }

    private JPanel buildOutputPanel() {
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        outputArea.setBackground(new Color(250, 251, 253));
        outputArea.setLineWrap(false);

        JScrollPane scroll = new JScrollPane(outputArea);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        scroll.setPreferredSize(new Dimension(760, 220));

        return wrap("Live Simulation Log", scroll);
    }

    private JPanel buildMetricsPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBackground(new Color(245, 247, 250));
        panel.setPreferredSize(new Dimension(400, 220));

        totalValue = createMetric(panel, "Total Packets");
        transmittedValue = createMetric(panel, "Transmitted");
        droppedValue = createMetric(panel, "Dropped");
        delayValue = createMetric(panel, "Average Delay");
        dropRateValue = createMetric(panel, "Drop Rate");
        bufferUsageValue = createMetric(panel, "Buffer Utilization");

        return wrap("Performance Metrics", panel);
    }

    private JLabel createMetric(JPanel parent, String name) {
        JPanel card = new JPanel(new BorderLayout(4, 4));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 222, 228)),
                new EmptyBorder(8, 10, 8, 10)));

        JLabel n = new JLabel(name);
        n.setFont(new Font("Arial", Font.PLAIN, 12));
        n.setForeground(new Color(90, 98, 108));

        JLabel v = new JLabel("0", SwingConstants.CENTER);
        v.setFont(new Font("Arial", Font.BOLD, 20));
        v.setForeground(new Color(25, 31, 40));

        card.add(n, BorderLayout.NORTH);
        card.add(v, BorderLayout.CENTER);
        parent.add(card);
        return v;
    }

    private JPanel buildPacketTablePanel() {
        String[] columns = {"Packet", "Priority", "Size (B)", "Status", "Buffer", "Delay (ms)"};

        packetTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        packetTable = new JTable(packetTableModel);
        packetTable.setRowHeight(24);
        packetTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        packetTable.setFont(new Font("Arial", Font.PLAIN, 11));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < packetTable.getColumnCount(); i++) {
            packetTable.getColumnModel()
                    .getColumn(i)
                    .setCellRenderer(center);
        }

        JScrollPane scroll = new JScrollPane(packetTable);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        scroll.setPreferredSize(new Dimension(1280, 190));

        return wrap(
                "Packet Details — actual packets from this simulation",
                scroll);
    }

    private JPanel buildComparisonPanel() {
        String[] columns = {
                "Traffic",
                "Total",
                "Transmitted",
                "Dropped",
                "Avg Delay (ms)",
                "Drop Rate (%)",
                "Buffer Usage (%)"
        };

        comparisonTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        comparisonTable = new JTable(comparisonTableModel);
        comparisonTable.setRowHeight(23);
        comparisonTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));

        JScrollPane scroll = new JScrollPane(comparisonTable);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        scroll.setPreferredSize(new Dimension(1280, 180));

        return wrap("Traffic Performance Comparison", scroll);
    }

    private JPanel wrap(String title, Component component) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(title),
                new EmptyBorder(3, 3, 3, 3)));
        panel.add(component, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Nested scrolling behavior:
     * - Output/log scrolls inside its own JScrollPane.
     * - Packet table scrolls inside its own JScrollPane.
     * - Any other dashboard area scrolls the main dashboard.
     */
    private void installWholeDashboardMouseWheel() {
        Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
            if (!(event instanceof MouseWheelEvent)) {
                return;
            }

            MouseWheelEvent wheel = (MouseWheelEvent) event;
            Object source = wheel.getSource();

            if (!(source instanceof Component)) {
                return;
            }

            Component component = (Component) source;

            if (!SwingUtilities.isDescendingFrom(component, dashboardRoot)
                    && component != dashboardRoot) {
                return;
            }

            // If the cursor is inside an inner scroll pane that can scroll,
            // leave the event alone. Swing will scroll that inner card.
            JScrollPane innerScroll = findScrollPaneAncestor(component);

            if (innerScroll != null && innerScroll != wholeDashboardScroll) {
                JScrollBar innerBar = innerScroll.getVerticalScrollBar();

                if (innerBar.isVisible()
                        && innerBar.getMaximum() > innerBar.getVisibleAmount()) {
                    return;
                }
            }

            // Otherwise scroll the complete dashboard.
            JScrollBar outerBar = wholeDashboardScroll.getVerticalScrollBar();

            if (!outerBar.isVisible()) {
                return;
            }

            int current = outerBar.getValue();
            int amount = wheel.getUnitsToScroll() * 25;
            int maximum = outerBar.getMaximum() - outerBar.getVisibleAmount();

            int newValue = Math.max(
                    0,
                    Math.min(current + amount, maximum));

            outerBar.setValue(newValue);
            wheel.consume();
        }, AWTEvent.MOUSE_WHEEL_EVENT_MASK);
    }

    private JScrollPane findScrollPaneAncestor(Component component) {
        Component current = component;

        while (current != null) {
            if (current instanceof JScrollPane) {
                return (JScrollPane) current;
            }
            current = current.getParent();
        }

        return null;
    }

    private double parseSpeed(String value) {
        if (value == null) return 1.0;
        try {
            return Double.parseDouble(value.replace("x", "").trim());
        } catch (NumberFormatException ex) {
            return 1.0;
        }
    }

    private String formatSpeed(double speed) {
        if (speed == 0.2) return "0.2x";
        if (speed == 0.5) return "0.5x";
        if (speed == 1.5) return "1.5x";
        return "1x";
    }

    private int scaleDelay(int baseDelay, double speed) {
        return Math.max(30, (int) Math.round(baseDelay / speed));
    }

    private double getCurrentSpeed() {
        return parseSpeed((String) speedComboBox.getSelectedItem());
    }

    private void startSimulation() {
        final int bufferCapacity;
        final int totalPackets;
        final int outputRate;
        final String trafficType;
        final double visualizationSpeed;

        try {
            bufferCapacity = Integer.parseInt(bufferField.getText().trim());
            totalPackets = Integer.parseInt(packetsField.getText().trim());
            outputRate = Integer.parseInt(outputRateField.getText().trim());
            trafficType = (String) trafficComboBox.getSelectedItem();
            visualizationSpeed = parseSpeed((String) speedComboBox.getSelectedItem());
        } catch (NumberFormatException ex) {
            showError("Please enter valid numeric values.");
            return;
        }

        if (bufferCapacity <= 0 || totalPackets <= 0 || outputRate <= 0) {
            showError("All values must be greater than zero.");
            return;
        }

        int arrivalDelay = getArrivalDelay(trafficType);

        outputArea.setText("");
        packetTableModel.setRowCount(0);
        comparisonTableModel.setRowCount(0);

        // The graph is created once in buildMainContent().
        performancePanel.setBufferCapacity(bufferCapacity);
        performancePanel.clearData();

        visualizationPanel.reset(bufferCapacity, outputRate, trafficType);
        visualizationPanel.setStatus("RUNNING");

        resetMetrics(totalPackets);
        setStatus("RUNNING", new Color(180, 83, 9));

        startButton.setEnabled(false);
        comparisonButton.setEnabled(false);

        appendOutput("==============================================================\n");
        appendOutput("                    LIVE TRAFFIC SIMULATION\n");
        appendOutput("==============================================================\n");
        appendOutput("Traffic       : " + trafficType + "\n");
        appendOutput("Buffer        : " + bufferCapacity + "\n");
        appendOutput("Packets       : " + totalPackets + "\n");
        appendOutput("Bucket Rate   : " + outputRate + " packets/cycle\n");
        appendOutput("Arrival Delay : " + arrivalDelay + " ms\n");
        appendOutput("Visualization: " + formatSpeed(visualizationSpeed) + "\n\n");

        Thread simulationThread = new Thread(
                () -> runSimulation(
                        bufferCapacity,
                        outputRate,
                        totalPackets,
                        trafficType,
                        arrivalDelay,
                        visualizationSpeed),
                "network-simulation");

        simulationThread.start();
    }

    private void runSimulation(int bufferCapacity, int outputRate, int totalPackets,
                               String trafficType, int arrivalDelay, double visualizationSpeed) {
        PacketGenerator generator = new PacketGenerator();
        NetworkBuffer buffer = new NetworkBuffer(bufferCapacity);
        LeakyBucket leakyBucket = new LeakyBucket(outputRate);
        Statistics statistics = new Statistics();
        statistics.setTotalPackets(totalPackets);

        final boolean[] arrivalFinished = {false};

        Thread arrivalThread = new Thread(() -> {
            for (int i = 1; i <= totalPackets; i++) {
                Packet packet = generator.generatePacket(i);
                boolean accepted = buffer.addPacket(packet);
                int currentSize = buffer.getBufferSize();
                statistics.updateBufferUsage(currentSize);

                if (accepted) {
                    appendOutput(String.format("ARRIVAL   -> P%-3d | Priority: %d | Size: %4d B | BUFFER: %d/%d%n",
                            packet.getId(), packet.getPriority(), packet.getSize(), currentSize, bufferCapacity));
                    updatePacketRow(packet, "BUFFERED", currentSize, 0);
                    updateVisualizationIncoming(packet, buffer.getPacketsSnapshot());
                } else {
                    appendOutput(String.format("DROP      -> P%-3d | BUFFER FULL (%d/%d)%n",
                            packet.getId(), currentSize, bufferCapacity));
                    updatePacketRow(packet, "DROPPED", currentSize, 0);
                    updateVisualizationDropped(packet, buffer.getPacketsSnapshot());
                }
                updateGraph(currentSize, leakyBucket.getTransmittedPackets(), buffer.getDroppedPackets());
                updateMetrics(totalPackets, leakyBucket.getTransmittedPackets(), buffer.getDroppedPackets(),
                        statistics.calculateAverageDelay(), statistics.calculateDropRate(),
                        statistics.calculateBufferUtilization(bufferCapacity));

                try {
                    Thread.sleep(scaleDelay(arrivalDelay, visualizationSpeed));
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            arrivalFinished[0] = true;
        }, "packet-arrival");

        Thread transmissionThread = new Thread(() -> {
            while (!arrivalFinished[0] || !buffer.isEmpty()) {
                if (!buffer.isEmpty()) {
                    List<Packet> sent = leakyBucket.processPackets(buffer, statistics);
                    for (Packet packet : sent) {
                        appendOutput(String.format("TRANSMIT  -> P%-3d | Priority: %d | Delay: %d ms | Remaining: %d%n",
                                packet.getId(), packet.getPriority(), packet.getDelay(), buffer.getBufferSize()));
                        updatePacketRow(packet, "TRANSMITTED", buffer.getBufferSize(), packet.getDelay());
                        updateVisualizationTransmission(packet, buffer.getPacketsSnapshot(), leakyBucket.getTransmittedPackets());
                    }
                    statistics.updateBufferUsage(buffer.getBufferSize());
                    updateGraph(buffer.getBufferSize(), leakyBucket.getTransmittedPackets(), buffer.getDroppedPackets());
                    updateMetrics(totalPackets, leakyBucket.getTransmittedPackets(), buffer.getDroppedPackets(),
                            statistics.calculateAverageDelay(), statistics.calculateDropRate(),
                            statistics.calculateBufferUtilization(bufferCapacity));
                }
                try {
                    Thread.sleep(scaleDelay(1000, visualizationSpeed));
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, "packet-transmission");

        arrivalThread.start();
        transmissionThread.start();

        try {
            arrivalThread.join();
            transmissionThread.join();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        statistics.setDroppedPackets(buffer.getDroppedPackets());
        double avgDelay = statistics.calculateAverageDelay();
        double dropRate = statistics.calculateDropRate();
        double usage = statistics.calculateBufferUtilization(bufferCapacity);

        appendOutput("\n==============================================================\n");
        appendOutput("                         FINAL RESULTS\n");
        appendOutput("==============================================================\n");
        appendOutput("Total       : " + totalPackets + "\n");
        appendOutput("Transmitted : " + leakyBucket.getTransmittedPackets() + "\n");
        appendOutput("Dropped     : " + buffer.getDroppedPackets() + "\n");
        appendOutput(String.format("Avg Delay   : %.2f ms%n", avgDelay));
        appendOutput(String.format("Drop Rate   : %.2f%%%n", dropRate));
        appendOutput(String.format("Buffer Use  : %.2f%%%n", usage));
        appendOutput("==============================================================\n");

        SwingUtilities.invokeLater(() -> {
            visualizationPanel.setStatus("COMPLETED");
            setStatus("COMPLETED", new Color(21, 128, 61));
            startButton.setEnabled(true);
            comparisonButton.setEnabled(true);
        });
    }

    private void updateVisualizationIncoming(Packet packet, List<Packet> snapshot) {
        SwingUtilities.invokeLater(() -> visualizationPanel.showIncoming(packet, snapshot));
    }

    private void updateVisualizationDropped(Packet packet, List<Packet> snapshot) {
        SwingUtilities.invokeLater(() -> visualizationPanel.showDropped(packet, snapshot));
    }

    private void updateVisualizationTransmission(Packet packet, List<Packet> snapshot, int count) {
        SwingUtilities.invokeLater(() -> {
            visualizationPanel.showTransmission(packet, snapshot, count);
            Timer t = new Timer(scaleDelay(500, getCurrentSpeed()), e -> visualizationPanel.finishTransmission(snapshot));
            t.setRepeats(false);
            t.start();
        });
    }

    private void updatePacketRow(Packet packet, String status, int bufferSize, long delay) {
        SwingUtilities.invokeLater(() -> {
            int row = findPacketRow(packet.getId());
            if (row < 0) {
                packetTableModel.addRow(new Object[]{
                        "P" + packet.getId(), packet.getPriority(), packet.getSize(), status, bufferSize, delay
                });
            } else {
                packetTableModel.setValueAt(status, row, 3);
                packetTableModel.setValueAt(bufferSize, row, 4);
                packetTableModel.setValueAt(delay, row, 5);
            }
        });
    }

    private int findPacketRow(int packetId) {
        for (int i = 0; i < packetTableModel.getRowCount(); i++) {
            if (String.valueOf(packetTableModel.getValueAt(i, 0)).equals("P" + packetId)) return i;
        }
        return -1;
    }

    private void updateGraph(int bufferSize, int transmitted, int dropped) {
        SwingUtilities.invokeLater(() -> performancePanel.addData(bufferSize, transmitted, dropped));
    }

    private void updateMetrics(int total, int transmitted, int dropped, double delay, double dropRate, double usage) {
        SwingUtilities.invokeLater(() -> {
            totalValue.setText(String.valueOf(total));
            transmittedValue.setText(String.valueOf(transmitted));
            droppedValue.setText(String.valueOf(dropped));
            delayValue.setText(String.format("%.1f ms", delay));
            dropRateValue.setText(String.format("%.1f%%", dropRate));
            bufferUsageValue.setText(String.format("%.1f%%", usage));
        });
    }

    private void resetMetrics(int total) {
        totalValue.setText(String.valueOf(total));
        transmittedValue.setText("0");
        droppedValue.setText("0");
        delayValue.setText("0 ms");
        dropRateValue.setText("0%");
        bufferUsageValue.setText("0%");
    }

    private void appendOutput(String text) {
        SwingUtilities.invokeLater(() -> {
            outputArea.append(text);
            outputArea.setCaretPosition(outputArea.getDocument().getLength());
        });
    }

    private void clearDashboard() {
        outputArea.setText("");
        packetTableModel.setRowCount(0);
        comparisonTableModel.setRowCount(0);
        visualizationPanel.clearVisualization();
        visualizationPanel.setStatus("READY");
        if (performancePanel != null) performancePanel.clearData();
        resetMetrics(0);
        setStatus("READY", new Color(21, 128, 61));
        startButton.setEnabled(true);
        comparisonButton.setEnabled(true);
    }

    private void runComparison() {
        final int bufferCapacity;
        final int totalPackets;
        final int outputRate;
        try {
            bufferCapacity = Integer.parseInt(bufferField.getText().trim());
            totalPackets = Integer.parseInt(packetsField.getText().trim());
            outputRate = Integer.parseInt(outputRateField.getText().trim());
        } catch (NumberFormatException ex) {
            showError("Please enter valid numeric values.");
            return;
        }
        if (bufferCapacity <= 0 || totalPackets <= 0 || outputRate <= 0) {
            showError("All values must be greater than zero.");
            return;
        }

        comparisonButton.setEnabled(false);
        comparisonTableModel.setRowCount(0);
        appendOutput("\n================ TRAFFIC COMPARISON ================\n");

        Thread t = new Thread(() -> {
            for (String traffic : new String[]{"LIGHT", "MEDIUM", "HEAVY"}) {
                appendOutput("Running " + traffic + " traffic...\n");
                ComparisonResult result = executeComparisonSimulation(
                        bufferCapacity, outputRate, totalPackets, traffic, getArrivalDelay(traffic));
                addComparisonResult(result);
            }
            appendOutput("Comparison completed.\n");
            SwingUtilities.invokeLater(() -> comparisonButton.setEnabled(true));
        }, "traffic-comparison");
        t.start();
    }

    private ComparisonResult executeComparisonSimulation(int bufferCapacity, int outputRate,
                                                         int totalPackets, String trafficType, int arrivalDelay) {
        PacketGenerator generator = new PacketGenerator();
        NetworkBuffer buffer = new NetworkBuffer(bufferCapacity);
        LeakyBucket bucket = new LeakyBucket(outputRate);
        Statistics stats = new Statistics();
        stats.setTotalPackets(totalPackets);
        final boolean[] finished = {false};

        Thread arrivals = new Thread(() -> {
            for (int i = 1; i <= totalPackets; i++) {
                buffer.addPacket(generator.generatePacket(i));
                stats.updateBufferUsage(buffer.getBufferSize());
                try { Thread.sleep(arrivalDelay); }
                catch (InterruptedException e) { Thread.currentThread().interrupt(); return; }
            }
            finished[0] = true;
        });

        Thread service = new Thread(() -> {
            while (!finished[0] || !buffer.isEmpty()) {
                if (!buffer.isEmpty()) {
                    bucket.processPackets(buffer, stats);
                    stats.updateBufferUsage(buffer.getBufferSize());
                }
                try { Thread.sleep(1000); }
                catch (InterruptedException e) { Thread.currentThread().interrupt(); return; }
            }
        });
        arrivals.start(); service.start();
        try { arrivals.join(); service.join(); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        stats.setDroppedPackets(buffer.getDroppedPackets());
        return new ComparisonResult(trafficType, totalPackets, bucket.getTransmittedPackets(),
                buffer.getDroppedPackets(), stats.calculateAverageDelay(), stats.calculateDropRate(),
                stats.calculateBufferUtilization(bufferCapacity));
    }

    private void addComparisonResult(ComparisonResult r) {
        SwingUtilities.invokeLater(() -> comparisonTableModel.addRow(new Object[]{
                r.getTrafficType(), r.getTotalPackets(), r.getTransmittedPackets(), r.getDroppedPackets(),
                String.format("%.2f", r.getAverageDelay()), String.format("%.2f", r.getDropRate()),
                String.format("%.2f", r.getBufferUtilization())
        }));
    }

    private int getArrivalDelay(String trafficType) {
        switch (trafficType) {
            case "LIGHT": return 1000;
            case "MEDIUM": return 500;
            case "HEAVY": return 100;
            default: return 500;
        }
    }

    private void setStatus(String status, Color color) {
        statusLabel.setText("● " + status);
        statusLabel.setForeground(color);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Input Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Dashboard().setVisible(true));
    }
}
