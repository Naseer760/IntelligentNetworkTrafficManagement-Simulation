package networktraffic;

public class Main {

    public static void main(String[] args) {

        // ============================================
        // PROJECT 4
        // INTELLIGENT NETWORK BUFFER AND
        // TRAFFIC MANAGEMENT SYSTEM
        // ============================================

        System.out.println();
        System.out.println("==================================================");
        System.out.println("   INTELLIGENT NETWORK BUFFER & TRAFFIC SYSTEM");
        System.out.println("==================================================");

        // ============================================
        // SIMULATION CONFIGURATION
        // ============================================

        int bufferCapacity = 10;

        int outputRate = 3;

        int totalPackets = 20;

        // ============================================
        // TRAFFIC CONDITIONS
        // ============================================

        // LIGHT  -> 1000 ms
        // MEDIUM -> 500 ms
        // HEAVY  -> 100 ms

        String trafficType = "HEAVY";

        int arrivalDelay;

        switch (trafficType) {

            case "LIGHT":
                arrivalDelay = 1000;
                break;

            case "MEDIUM":
                arrivalDelay = 500;
                break;

            case "HEAVY":
                arrivalDelay = 100;
                break;

            default:
                System.out.println(
                        "Invalid traffic type."
                );

                return;
        }

        // ============================================
        // DISPLAY CONFIGURATION
        // ============================================

        System.out.println();
        System.out.println("Simulation Configuration");
        System.out.println("--------------------------------------------");

        System.out.println(
                "Traffic Type       : " + trafficType
        );

        System.out.println(
                "Buffer Capacity    : " + bufferCapacity
        );

        System.out.println(
                "Total Packets      : " + totalPackets
        );

        System.out.println(
                "Output Rate        : "
                        + outputRate
                        + " packets/cycle"
        );

        System.out.println(
                "Arrival Delay      : "
                        + arrivalDelay
                        + " ms"
        );

        System.out.println("--------------------------------------------");

        // ============================================
        // CREATE TRAFFIC SIMULATOR
        // ============================================

        ContinuousTrafficSimulator simulator =
                new ContinuousTrafficSimulator(
                        bufferCapacity,
                        outputRate,
                        totalPackets
                );

        // ============================================
        // START SIMULATION
        // ============================================

        simulator.start(
                trafficType,
                totalPackets,
                arrivalDelay
        );

        // ============================================
        // SIMULATION COMPLETED
        // ============================================

        System.out.println();
        System.out.println("==================================================");
        System.out.println("           SIMULATION COMPLETED");
        System.out.println("==================================================");
    }
}