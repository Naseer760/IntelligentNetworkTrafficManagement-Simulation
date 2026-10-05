package networktraffic;

public class ComparisonResult {

    private String trafficType;
    private int totalPackets;
    private int transmittedPackets;
    private int droppedPackets;
    private double averageDelay;
    private double dropRate;
    private double bufferUtilization;

    public ComparisonResult(
            String trafficType,
            int totalPackets,
            int transmittedPackets,
            int droppedPackets,
            double averageDelay,
            double dropRate,
            double bufferUtilization) {

        this.trafficType = trafficType;
        this.totalPackets = totalPackets;
        this.transmittedPackets = transmittedPackets;
        this.droppedPackets = droppedPackets;
        this.averageDelay = averageDelay;
        this.dropRate = dropRate;
        this.bufferUtilization = bufferUtilization;
    }

    public String getTrafficType() {
        return trafficType;
    }

    public int getTotalPackets() {
        return totalPackets;
    }

    public int getTransmittedPackets() {
        return transmittedPackets;
    }

    public int getDroppedPackets() {
        return droppedPackets;
    }

    public double getAverageDelay() {
        return averageDelay;
    }

    public double getDropRate() {
        return dropRate;
    }

    public double getBufferUtilization() {
        return bufferUtilization;
    }
}
