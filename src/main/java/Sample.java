public class Sample {
    public int sensorId;
    public long timestamp;
    public Double roll;
    public Double yaw;
    public Double pitch;

    public Sample() {}

    public Sample(int sensorId, long timestamp, Double roll, Double yaw, Double pitch) {
        this.sensorId = sensorId;
        this.timestamp = timestamp;
        this.roll = roll;
        this.yaw = yaw;
        this.pitch = pitch;
    }
}