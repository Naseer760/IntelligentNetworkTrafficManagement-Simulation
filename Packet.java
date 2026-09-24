package networktraffic;

public class Packet {

    private int id;
    private int priority;
    private int size;
    private long arrivalTime;
    private long transmissionTime;

    // Constructor
    public Packet(int id, int priority, int size) {
        this.id = id;
        this.priority = priority;
        this.size = size;

        // Store the time when packet arrives
        this.arrivalTime = System.currentTimeMillis();
    }

    // Get Packet ID
    public int getId() {
        return id;
    }

    // Get Packet Priority
    public int getPriority() {
        return priority;
    }

    // Get Packet Size
    public int getSize() {
        return size;
    }

    // Get Arrival Time
    public long getArrivalTime() {
        return arrivalTime;
    }

    // Set Transmission Time
    public void setTransmissionTime(long transmissionTime) {
        this.transmissionTime = transmissionTime;
    }

    // Get Transmission Time
    public long getTransmissionTime() {
        return transmissionTime;
    }

    // Calculate Packet Delay
    public long getDelay() {
        return transmissionTime - arrivalTime;
    }
}
