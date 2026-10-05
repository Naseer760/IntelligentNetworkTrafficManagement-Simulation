 package networktraffic;

import java.util.Random;

public class PacketGenerator {

    private Random random = new Random();

    // Generate one packet
    public Packet generatePacket(int id) {

        // Priority: 1 to 3
        int priority = random.nextInt(3) + 1;

        // Size: 100 to 1000 bytes
        int size = random.nextInt(901) + 100;

        return new Packet(id, priority, size);
    }
}