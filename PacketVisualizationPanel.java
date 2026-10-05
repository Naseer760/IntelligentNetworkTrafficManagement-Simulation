package networktraffic;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Packet based network visualization.
 *
 * Each stage has its own scroll bar:
 * Incoming Packets -> Network Buffer -> Leaky Bucket -> Output
 */
public class PacketVisualizationPanel extends JPanel {

    private int bufferCapacity = 10;
    private int outputRate = 3;
    private int transmittedCount = 0;
    private int droppedCount = 0;

    private String trafficType = "MEDIUM";
    private String status = "READY";

    private Packet incomingPacket;
    private Packet leakingPacket;
    private Packet lastTransmittedPacket;
    private Packet lastDroppedPacket;

    private List<Packet> bufferPackets = new ArrayList<>();

    // History for visualization
    private final List<Packet> incomingHistory = new ArrayList<>();
    private final List<Packet> transmittedHistory = new ArrayList<>();
    private final List<Packet> droppedHistory = new ArrayList<>();

    private JPanel incomingList;
    private JPanel bufferList;
    private JPanel bucketList;
    private JPanel outputList;

    private JLabel infoLabel;

    public PacketVisualizationPanel() {

        setBackground(new Color(248, 250, 252));
        setBorder(BorderFactory.createLineBorder(
                new Color(205, 211, 219)));

        setLayout(new BorderLayout(10, 10));

        buildVisualization();
    }

    // =========================================================
    // BUILD COMPLETE VISUALIZATION
    // =========================================================

    private void buildVisualization() {

        JPanel main = new JPanel(new BorderLayout(8, 8));
        main.setOpaque(false);
        main.setBorder(new EmptyBorder(10, 10, 10, 10));

        // -----------------------------------------------------
        // TITLE
        // -----------------------------------------------------

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);

        JLabel title = new JLabel(
                "NETWORK SIMULATION VISUALIZATION");

        title.setFont(new Font(
                "Arial",
                Font.BOLD,
                21));

        title.setForeground(
                new Color(20, 28, 40));

        infoLabel = new JLabel(
                "Traffic: MEDIUM   |   Buffer: 0/10   |   Leaky Bucket: 3 packets/cycle");

        infoLabel.setFont(new Font(
                "Arial",
                Font.PLAIN,
                13));

        infoLabel.setForeground(
                new Color(90, 98, 108));

        header.add(title);
        header.add(Box.createVerticalStrut(5));
        header.add(infoLabel);

        main.add(header, BorderLayout.NORTH);

        // -----------------------------------------------------
        // FOUR CARDS
        // -----------------------------------------------------

        JPanel flowPanel = new JPanel(new GridBagLayout());
        flowPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;

        // Incoming
        gbc.gridx = 0;
        gbc.weightx = 1;
        flowPanel.add(
                createIncomingCard(),
                gbc);

        // Arrow
        gbc.gridx = 1;
        gbc.weightx = 0;
        flowPanel.add(
                createArrow("→"),
                gbc);

        // Buffer
        gbc.gridx = 2;
        gbc.weightx = 1.5;
        flowPanel.add(
                createBufferCard(),
                gbc);

        // Arrow
        gbc.gridx = 3;
        gbc.weightx = 0;
        flowPanel.add(
                createArrow("→"),
                gbc);

        // Leaky Bucket
        gbc.gridx = 4;
        gbc.weightx = 1;
        flowPanel.add(
                createBucketCard(),
                gbc);

        // Arrow
        gbc.gridx = 5;
        gbc.weightx = 0;
        flowPanel.add(
                createArrow("→"),
                gbc);

        // Output
        gbc.gridx = 6;
        gbc.weightx = 1;
        flowPanel.add(
                createOutputCard(),
                gbc);

        main.add(flowPanel, BorderLayout.CENTER);

        // -----------------------------------------------------
        // STATUS BAR
        // -----------------------------------------------------

        JPanel statusPanel = new JPanel(
                new BorderLayout());

        statusPanel.setBackground(
                new Color(31, 41, 55));

        statusPanel.setBorder(
                new EmptyBorder(8, 15, 8, 15));

        JLabel statusText = new JLabel(
                "STATUS: " + status);

        statusText.setName("statusText");

        statusText.setForeground(Color.WHITE);

        statusText.setFont(
                new Font("Arial", Font.BOLD, 12));

        statusPanel.add(
                statusText,
                BorderLayout.WEST);

        JLabel counters = new JLabel(
                "Transmitted: 0    |    Dropped: 0");

        counters.setName("counterText");

        counters.setForeground(Color.WHITE);

        counters.setFont(
                new Font("Arial", Font.BOLD, 12));

        statusPanel.add(
                counters,
                BorderLayout.EAST);

        main.add(
                statusPanel,
                BorderLayout.SOUTH);

        add(main, BorderLayout.CENTER);
    }

    // =========================================================
    // INCOMING CARD
    // =========================================================

    private JPanel createIncomingCard() {

        incomingList = createListPanel();

        return createCard(
                "INCOMING PACKETS",
                new Color(226, 239, 255),
                incomingList,
                280);
    }

    // =========================================================
    // BUFFER CARD
    // =========================================================

    private JPanel createBufferCard() {

        bufferList = createListPanel();

        return createCard(
                "NETWORK BUFFER",
                new Color(234, 248, 238),
                bufferList,
                340);
    }

    // =========================================================
    // LEAKY BUCKET CARD
    // =========================================================

    private JPanel createBucketCard() {

        bucketList = createListPanel();

        return createCard(
                "LEAKY BUCKET",
                new Color(255, 245, 224),
                bucketList,
                260);
    }

    // =========================================================
    // OUTPUT CARD
    // =========================================================

    private JPanel createOutputCard() {

        outputList = createListPanel();

        return createCard(
                "OUTPUT",
                new Color(245, 235, 255),
                outputList,
                280);
    }

    // =========================================================
    // GENERIC CARD
    // =========================================================

    private JPanel createCard(
            String title,
            Color background,
            JPanel content,
            int width) {

        JPanel card = new JPanel(
                new BorderLayout(5, 5));

        card.setBackground(background);

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(190, 198, 208),
                                1),
                        new EmptyBorder(
                                8, 8, 8, 8)));

        JLabel titleLabel = new JLabel(
                title,
                SwingConstants.CENTER);

        titleLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        15));

        titleLabel.setForeground(
                new Color(30, 40, 55));

        card.add(
                titleLabel,
                BorderLayout.NORTH);

        // Individual scrollbar
        JScrollPane scrollPane =
                new JScrollPane(content);

        scrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        scrollPane.getVerticalScrollBar()
                .setUnitIncrement(16);

        scrollPane.setBorder(
                BorderFactory.createLineBorder(
                        new Color(205, 212, 220)));

        scrollPane.setPreferredSize(
                new Dimension(width, 300));

        card.add(
                scrollPane,
                BorderLayout.CENTER);

        card.setPreferredSize(
                new Dimension(width, 350));

        return card;
    }

    // =========================================================
    // LIST PANEL
    // =========================================================

    private JPanel createListPanel() {

        JPanel panel = new JPanel();

        panel.setLayout(
                new BoxLayout(
                        panel,
                        BoxLayout.Y_AXIS));

        panel.setBackground(Color.WHITE);

        panel.setBorder(
                new EmptyBorder(
                        8, 8, 8, 8));

        return panel;
    }

    // =========================================================
    // ARROW
    // =========================================================

    private JPanel createArrow(String symbol) {

        JPanel panel = new JPanel(
                new GridBagLayout());

        panel.setOpaque(false);

        JLabel arrow = new JLabel(symbol);

        arrow.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        30));

        arrow.setForeground(
                new Color(70, 80, 95));

        panel.add(arrow);

        return panel;
    }

    // =========================================================
    // PACKET CARD
    // =========================================================

    private JPanel createPacketCard(
            Packet packet,
            String state) {

        JPanel card = new JPanel(
                new BorderLayout(5, 3));

        card.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        70));

        Color background;

        switch (state) {

            case "TRANSMITTED":
                background =
                        new Color(220, 252, 231);
                break;

            case "DROPPED":
                background =
                        new Color(254, 226, 226);
                break;

            case "TRANSMITTING":
                background =
                        new Color(254, 243, 199);
                break;

            case "BUFFERED":
                background =
                        new Color(219, 234, 254);
                break;

            default:
                background =
                        new Color(241, 245, 249);
        }

        card.setBackground(background);

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(170, 180, 190)),
                        new EmptyBorder(
                                7, 8, 7, 8)));

        JLabel packetLabel =
                new JLabel(
                        "P" + packet.getId()
                                + "   |   Priority "
                                + packet.getPriority());

        packetLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        12));

        JLabel sizeLabel =
                new JLabel(
                        "Size: "
                                + packet.getSize()
                                + " B");

        sizeLabel.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        11));

        JLabel statusLabel =
                new JLabel(state);

        statusLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        10));

        statusLabel.setHorizontalAlignment(
                SwingConstants.RIGHT);

        card.add(
                packetLabel,
                BorderLayout.WEST);

        card.add(
                sizeLabel,
                BorderLayout.CENTER);

        card.add(
                statusLabel,
                BorderLayout.EAST);

        return card;
    }

    // =========================================================
    // RESET
    // =========================================================

    public synchronized void reset(
            int capacity,
            int rate,
            String traffic) {

        bufferCapacity = capacity;
        outputRate = rate;
        trafficType = traffic;

        transmittedCount = 0;
        droppedCount = 0;

        incomingPacket = null;
        leakingPacket = null;
        lastTransmittedPacket = null;
        lastDroppedPacket = null;

        bufferPackets.clear();

        incomingHistory.clear();
        transmittedHistory.clear();
        droppedHistory.clear();

        refreshLists();

        updateInfo();

        setStatus("READY");

        repaint();
    }

    // =========================================================
    // STATUS
    // =========================================================

    public synchronized void setStatus(
            String newStatus) {

        status = newStatus;

        updateStatusBar();

        repaint();
    }

    // =========================================================
    // INCOMING
    // =========================================================

    public synchronized void showIncoming(
            Packet packet,
            List<Packet> currentBuffer) {

        incomingPacket = packet;

        bufferPackets =
                new ArrayList<>(currentBuffer);

        incomingHistory.add(packet);

        refreshLists();

        updateInfo();

        repaint();
    }

    // =========================================================
    // BUFFER
    // =========================================================

    public synchronized void showBuffered(
            List<Packet> currentBuffer) {

        incomingPacket = null;

        bufferPackets =
                new ArrayList<>(currentBuffer);

        refreshLists();

        updateInfo();

        repaint();
    }

    // =========================================================
    // DROPPED
    // =========================================================

    public synchronized void showDropped(
            Packet packet,
            List<Packet> currentBuffer) {

        incomingPacket = packet;

        lastDroppedPacket = packet;

        bufferPackets =
                new ArrayList<>(currentBuffer);

        droppedHistory.add(packet);

        droppedCount++;

        refreshLists();

        updateInfo();

        repaint();
    }

    // =========================================================
    // TRANSMISSION
    // =========================================================

    public synchronized void showTransmission(
            Packet packet,
            List<Packet> currentBuffer,
            int transmitted) {

        incomingPacket = null;

        leakingPacket = packet;

        lastTransmittedPacket = packet;

        bufferPackets =
                new ArrayList<>(currentBuffer);

        transmittedCount =
                transmitted;

        transmittedHistory.add(packet);

        refreshLists();

        updateInfo();

        repaint();
    }

    // =========================================================
    // FINISH TRANSMISSION
    // =========================================================

    public synchronized void finishTransmission(
            List<Packet> currentBuffer) {

        leakingPacket = null;

        bufferPackets =
                new ArrayList<>(currentBuffer);

        refreshLists();

        updateInfo();

        repaint();
    }

    // =========================================================
    // CLEAR
    // =========================================================

    public synchronized void clearVisualization() {

        incomingPacket = null;
        leakingPacket = null;

        lastTransmittedPacket = null;
        lastDroppedPacket = null;

        bufferPackets.clear();

        incomingHistory.clear();
        transmittedHistory.clear();
        droppedHistory.clear();

        transmittedCount = 0;
        droppedCount = 0;

        refreshLists();

        updateInfo();

        setStatus("READY");

        repaint();
    }

    // =========================================================
    // REFRESH ALL CARDS
    // =========================================================

    private void refreshLists() {

        if (incomingList == null)
            return;

        // -----------------------------------------------------
        // INCOMING
        // -----------------------------------------------------

        incomingList.removeAll();

        int incomingStart =
                Math.max(
                        0,
                        incomingHistory.size() - 50);

        for (int i = incomingStart;
             i < incomingHistory.size();
             i++) {

            Packet packet =
                    incomingHistory.get(i);

            incomingList.add(
                    createPacketCard(
                            packet,
                            "BUFFERED"));

            incomingList.add(
                    Box.createVerticalStrut(6));
        }

        if (incomingHistory.isEmpty()) {

            incomingList.add(
                    createEmptyLabel(
                            "Waiting for packet..."));
        }

        // -----------------------------------------------------
        // BUFFER
        // -----------------------------------------------------

        bufferList.removeAll();

        if (bufferPackets.isEmpty()) {

            bufferList.add(
                    createEmptyLabel(
                            "Buffer empty"));

        } else {

            for (Packet packet :
                    bufferPackets) {

                bufferList.add(
                        createPacketCard(
                                packet,
                                "BUFFERED"));

                bufferList.add(
                        Box.createVerticalStrut(6));
            }
        }

        // -----------------------------------------------------
        // LEAKY BUCKET
        // -----------------------------------------------------

        bucketList.removeAll();

        if (leakingPacket != null) {

            bucketList.add(
                    createPacketCard(
                            leakingPacket,
                            "TRANSMITTING"));

        } else {

            bucketList.add(
                    createEmptyLabel(
                            "Waiting for packet"));

            bucketList.add(
                    Box.createVerticalStrut(10));

            JLabel rate =
                    new JLabel(
                            "Rate: "
                                    + outputRate
                                    + " packets/cycle",
                            SwingConstants.CENTER);

            rate.setAlignmentX(
                    Component.CENTER_ALIGNMENT);

            rate.setFont(
                    new Font(
                            "Arial",
                            Font.BOLD,
                            12));

            bucketList.add(rate);
        }

        // -----------------------------------------------------
        // OUTPUT
        // -----------------------------------------------------

        outputList.removeAll();

        int transmittedStart =
                Math.max(
                        0,
                        transmittedHistory.size() - 50);

        for (int i = transmittedStart;
             i < transmittedHistory.size();
             i++) {

            Packet packet =
                    transmittedHistory.get(i);

            outputList.add(
                    createPacketCard(
                            packet,
                            "TRANSMITTED"));

            outputList.add(
                    Box.createVerticalStrut(6));
        }

        int droppedStart =
                Math.max(
                        0,
                        droppedHistory.size() - 50);

        for (int i = droppedStart;
             i < droppedHistory.size();
             i++) {

            Packet packet =
                    droppedHistory.get(i);

            outputList.add(
                    createPacketCard(
                            packet,
                            "DROPPED"));

            outputList.add(
                    Box.createVerticalStrut(6));
        }

        if (transmittedHistory.isEmpty()
                && droppedHistory.isEmpty()) {

            outputList.add(
                    createEmptyLabel(
                            "No output yet"));
        }

        incomingList.revalidate();
        incomingList.repaint();

        bufferList.revalidate();
        bufferList.repaint();

        bucketList.revalidate();
        bucketList.repaint();

        outputList.revalidate();
        outputList.repaint();
    }

    // =========================================================
    // EMPTY MESSAGE
    // =========================================================

    private JLabel createEmptyLabel(
            String text) {

        JLabel label =
                new JLabel(
                        text,
                        SwingConstants.CENTER);

        label.setAlignmentX(
                Component.CENTER_ALIGNMENT);

        label.setFont(
                new Font(
                        "Arial",
                        Font.ITALIC,
                        12));

        label.setForeground(
                new Color(110, 120, 130));

        label.setBorder(
                new EmptyBorder(
                        20, 5, 20, 5));

        return label;
    }

    // =========================================================
    // UPDATE HEADER
    // =========================================================

    private void updateInfo() {

        if (infoLabel == null)
            return;

        infoLabel.setText(
                "Traffic: "
                        + trafficType
                        + "   |   Buffer: "
                        + bufferPackets.size()
                        + "/"
                        + bufferCapacity
                        + "   |   Leaky Bucket: "
                        + outputRate
                        + " packets/cycle");
    }

    // =========================================================
    // UPDATE STATUS BAR
    // =========================================================

    private void updateStatusBar() {

        Container root =
                getComponent(0) instanceof Container
                        ? (Container) getComponent(0)
                        : null;

        if (root == null)
            return;

        findAndUpdateLabel(
                root,
                "statusText",
                "STATUS: " + status);

        findAndUpdateLabel(
                root,
                "counterText",
                "Transmitted: "
                        + transmittedCount
                        + "    |    Dropped: "
                        + droppedCount);
    }

    private void findAndUpdateLabel(
            Container container,
            String name,
            String text) {

        for (Component component :
                container.getComponents()) {

            if (name.equals(
                    component.getName())
                    && component instanceof JLabel) {

                ((JLabel) component)
                        .setText(text);

                return;
            }

            if (component instanceof Container) {

                findAndUpdateLabel(
                        (Container) component,
                        name,
                        text);
            }
        }
    }
}