package networktraffic;

import java.util.ArrayList;
import java.util.List;

public class LeakyBucket {

    private int outputRate;
    private int transmittedPackets;

    public LeakyBucket(int outputRate) {
        this.outputRate = outputRate;
        this.transmittedPackets = 0;
    }

    // Process packets from buffer
    public List<Packet> processPackets(
            NetworkBuffer buffer,
            Statistics statistics) {

        List<Packet> transmittedThisCycle = new ArrayList<>();

        System.out.println();
        System.out.println("================================");
        System.out.println("       LEAKY BUCKET OUTPUT");
        System.out.println("================================");

        for (int i = 0; i < outputRate; i++) {

            if (!buffer.isEmpty()) {

                Packet packet = buffer.removePacket();

                // Record transmission time
                packet.setTransmissionTime(
                        System.currentTimeMillis()
                );

                transmittedPackets++;
                transmittedThisCycle.add(packet);

                // Send packet information to Statistics
                statistics.addTransmittedPacket(packet);

                System.out.println(
                        "Packet " + packet.getId()
                                + " transmitted | Priority: "
                                + packet.getPriority()
                );

            } else {

                System.out.println(
                        "Buffer is empty. No packet to transmit."
                );

                break;
            }
        }

        return transmittedThisCycle;
    }

    public int getTransmittedPackets() {
        return transmittedPackets;
    }

    public int getOutputRate() {
        return outputRate;
    }
}

