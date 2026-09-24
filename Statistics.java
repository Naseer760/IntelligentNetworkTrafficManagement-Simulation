 package networktraffic;


import java.util.ArrayList;
import java.util.List;

public class Statistics {

    // Store successfully transmitted packets
    private List<Packet> transmittedPackets;

    // Store total number of generated packets
    private int totalPackets;

    // Store total number of dropped packets
    private int droppedPackets;

    // Store maximum buffer usage
    private int maximumBufferUsage;

    // Constructor
    public Statistics() {
        transmittedPackets = new ArrayList<>();
        totalPackets = 0;
        droppedPackets = 0;
        maximumBufferUsage = 0;
    }

    // Set total packets
    public void setTotalPackets(int totalPackets) {
        this.totalPackets = totalPackets;
    }

    // Set dropped packets
    public void setDroppedPackets(int droppedPackets) {
        this.droppedPackets = droppedPackets;
    }

    // Add a transmitted packet
    public void addTransmittedPacket(Packet packet) {
        transmittedPackets.add(packet);
    }

    // Update maximum buffer usage
    public void updateBufferUsage(int currentUsage) {

        if (currentUsage > maximumBufferUsage) {
            maximumBufferUsage = currentUsage;
        }
    }

    // Calculate average packet delay
    public double calculateAverageDelay() {

        if (transmittedPackets.isEmpty()) {
            return 0;
        }

        long totalDelay = 0;

        for (Packet packet : transmittedPackets) {
            totalDelay += packet.getDelay();
        }

        return (double) totalDelay / transmittedPackets.size();
    }

    // Calculate packet drop rate
    public double calculateDropRate() {

        if (totalPackets == 0) {
            return 0;
        }

        return ((double) droppedPackets / totalPackets) * 100;
    }

    // Calculate buffer utilization
    public double calculateBufferUtilization(int capacity) {

        if (capacity == 0) {
            return 0;
        }

        return ((double) maximumBufferUsage / capacity) * 100;
    }

    // Display statistics
    public void displayStatistics(int bufferCapacity) {

        System.out.println();
        System.out.println("========================================");
        System.out.println("        NETWORK PERFORMANCE");
        System.out.println("========================================");

        System.out.println(
                "Total Packets       : " + totalPackets
        );

        System.out.println(
                "Transmitted Packets : "
                        + transmittedPackets.size()
        );

        System.out.println(
                "Dropped Packets     : " + droppedPackets
        );

        System.out.printf(
                "Average Delay       : %.2f ms%n",
                calculateAverageDelay()
        );

        System.out.printf(
                "Packet Drop Rate    : %.2f%%%n",
                calculateDropRate()
        );

        System.out.printf(
                "Buffer Utilization  : %.2f%%%n",
                calculateBufferUtilization(bufferCapacity)
        );

        System.out.println(
                "Maximum Buffer Usage: " + maximumBufferUsage
        );

        System.out.println("========================================");
    }
}


