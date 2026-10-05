package networktraffic;

public class ContinuousTrafficSimulator {

    private PacketGenerator generator;
    private NetworkBuffer buffer;
    private LeakyBucket leakyBucket;
    private Statistics statistics;

    private int packetCounter = 0;

    private boolean simulationRunning = true;

    public ContinuousTrafficSimulator(
            int bufferCapacity,
            int outputRate,
            int totalPackets) {

        generator = new PacketGenerator();

        buffer = new NetworkBuffer(bufferCapacity);

        leakyBucket = new LeakyBucket(outputRate);

        statistics = new Statistics();

        statistics.setTotalPackets(totalPackets);
    }

    // Start the complete simulation
    public void start(
            String trafficType,
            int totalPackets,
            int arrivalDelay) {

        System.out.println();
        System.out.println(
                "========================================"
        );

        System.out.println(
                "      CONTINUOUS TRAFFIC SIMULATION"
        );

        System.out.println(
                "========================================"
        );

        System.out.println(
                "Traffic Type: " + trafficType
        );

        System.out.println();

        // Thread 1: Packet arrival
        Thread arrivalThread = new Thread(() -> {

            for (int i = 0; i < totalPackets; i++) {

                packetCounter++;

                Packet packet =
                        generator.generatePacket(packetCounter);

                buffer.addPacket(packet);

                statistics.updateBufferUsage(
                        buffer.getBufferSize()
                );

                System.out.println(
                        "ARRIVAL -> Packet "
                                + packet.getId()
                                + " | Priority: "
                                + packet.getPriority()
                );

                try {

                    Thread.sleep(arrivalDelay);

                } catch (InterruptedException e) {

                    Thread.currentThread().interrupt();
                    break;
                }
            }

            simulationRunning = false;
        });


        // Thread 2: Packet transmission
        Thread transmissionThread = new Thread(() -> {

            while (simulationRunning
                    || !buffer.isEmpty()) {

                if (!buffer.isEmpty()) {

                    // Process packets according
                    // to the Leaky Bucket rate
                    leakyBucket.processPackets(
                            buffer,
                            statistics
                    );

                    statistics.updateBufferUsage(
                            buffer.getBufferSize()
                    );

                } else {

                    System.out.println(
                            "TRANSMISSION -> Buffer empty"
                    );
                }

                try {

                    // One transmission cycle
                    Thread.sleep(1000);

                } catch (InterruptedException e) {

                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });


        // Start both threads
        arrivalThread.start();
        transmissionThread.start();


        // Wait for completion
        try {

            arrivalThread.join();

            transmissionThread.join();

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
        }


        // Save dropped packet count
        statistics.setDroppedPackets(
                buffer.getDroppedPackets()
        );


        // Display final results
        displayResults();
    }


    private void displayResults() {

        System.out.println();
        System.out.println(
                "========================================"
        );

        System.out.println(
                "          FINAL RESULTS"
        );

        System.out.println(
                "========================================"
        );

        System.out.println(
                "Total Packets       : "
                        + statisticsTotalPackets()
        );

        System.out.println(
                "Transmitted Packets : "
                        + leakyBucket.getTransmittedPackets()
        );

        System.out.println(
                "Dropped Packets     : "
                        + buffer.getDroppedPackets()
        );

        System.out.printf(
                "Average Delay       : %.2f ms%n",
                statistics.calculateAverageDelay()
        );

        System.out.printf(
                "Packet Drop Rate    : %.2f%%%n",
                statistics.calculateDropRate()
        );

        System.out.printf(
                "Buffer Utilization  : %.2f%%%n",
                statistics.calculateBufferUtilization(
                        buffer.getCapacity()
                )
        );

        System.out.println(
                "========================================"
        );
    }


    private int statisticsTotalPackets() {

        return leakyBucket.getTransmittedPackets()
                + buffer.getDroppedPackets()
                + buffer.getBufferSize();
    }
}

