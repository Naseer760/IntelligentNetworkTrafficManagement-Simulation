package networktraffic;

public class TrafficSimulator {

    private PacketGenerator generator;
    private NetworkBuffer buffer;
    private LeakyBucket leakyBucket;
    private Statistics statistics;

    private int packetCounter;

    // Constructor
    public TrafficSimulator(
            int bufferCapacity,
            int outputRate) {

        generator = new PacketGenerator();

        buffer = new NetworkBuffer(bufferCapacity);

        leakyBucket = new LeakyBucket(outputRate);

        statistics = new Statistics();

        packetCounter = 0;
    }

    // Run simulation
    public void runSimulation(
            String trafficType,
            int numberOfPackets,
            int arrivalDelay) {

        System.out.println();
        System.out.println("========================================");
        System.out.println("          TRAFFIC SIMULATION");
        System.out.println("========================================");

        System.out.println(
                "Traffic Type : " + trafficType
        );

        System.out.println(
                "Total Packets: " + numberOfPackets
        );

        System.out.println();

        // Set total packets
        statistics.setTotalPackets(numberOfPackets);

        // Generate packets
        for (int i = 0; i < numberOfPackets; i++) {

            packetCounter++;

            Packet packet =
                    generator.generatePacket(packetCounter);

            System.out.println(
                    "Incoming Packet " + packet.getId()
                            + " | Priority: "
                            + packet.getPriority()
            );

            // Add packet to buffer
            buffer.addPacket(packet);

            // Track buffer usage
            statistics.updateBufferUsage(
                    buffer.getBufferSize()
            );

            // Wait before next packet
            try {
                Thread.sleep(arrivalDelay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        // Record dropped packets
        statistics.setDroppedPackets(
                buffer.getDroppedPackets()
        );

        System.out.println();
        System.out.println(
                "Packets waiting in buffer: "
                        + buffer.getBufferSize()
        );

        // Process packets
        leakyBucket.processPackets(
                buffer,
                statistics
        );

        // Display statistics
        statistics.displayStatistics(
                buffer.getCapacity()
        );
    }
}