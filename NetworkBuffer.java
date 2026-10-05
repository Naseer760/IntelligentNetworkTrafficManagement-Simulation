package networktraffic;

import java.util.PriorityQueue;
import java.util.ArrayList;
import java.util.List;

public class NetworkBuffer {

    private final int capacity;

    private final PriorityQueue<Packet> buffer;

    private int droppedPackets = 0;


    // =============================================
    // CONSTRUCTOR
    // =============================================

    public NetworkBuffer(int capacity) {

        if (capacity <= 0) {

            throw new IllegalArgumentException(
                    "Buffer capacity must be greater than zero."
            );
        }

        this.capacity = capacity;

        buffer = new PriorityQueue<>(
                (p1, p2) -> {

                    // Lower priority number
                    // means higher priority

                    int priorityCompare =
                            Integer.compare(
                                    p1.getPriority(),
                                    p2.getPriority()
                            );

                    if (priorityCompare != 0) {

                        return priorityCompare;
                    }

                    // If priorities are equal,
                    // process smaller packet ID first

                    return Integer.compare(
                            p1.getId(),
                            p2.getId()
                    );
                }
        );
    }


    // =============================================
    // ADD PACKET
    // =============================================

    public synchronized boolean addPacket(
            Packet packet) {

        if (packet == null) {

            return false;
        }


        if (buffer.size() >= capacity) {

            droppedPackets++;

            System.out.println(
                    "BUFFER FULL -> Packet "
                            + packet.getId()
                            + " DROPPED"
            );

            return false;
        }


        buffer.offer(packet);

        return true;
    }


    // =============================================
    // REMOVE HIGHEST PRIORITY PACKET
    // =============================================

    public synchronized Packet removePacket() {

        return buffer.poll();
    }


    // =============================================
    // CHECK EMPTY
    // =============================================

    public synchronized boolean isEmpty() {

        return buffer.isEmpty();
    }


    // =============================================
    // CURRENT BUFFER SIZE
    // =============================================

    public synchronized int getBufferSize() {

        return buffer.size();
    }

    // Snapshot of packets currently waiting in priority order.
    public synchronized List<Packet> getPacketsSnapshot() {
        return new ArrayList<>(buffer);
    }


    // =============================================
    // CAPACITY
    // =============================================

    public int getCapacity() {

        return capacity;
    }


    // =============================================
    // DROPPED PACKETS
    // =============================================

    public synchronized int getDroppedPackets() {

        return droppedPackets;
    }


    // =============================================
    // CLEAR BUFFER
    // =============================================

    public synchronized void clear() {

        buffer.clear();

        droppedPackets = 0;
    }
}