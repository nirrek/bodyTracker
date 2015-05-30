import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    /**
     * Parses a message from the Arduino, and returns a list of Samples
     * from the message.
     * @param msg The message to parse
     * @return A list of Samples, or null if no valid samples in the message.
     */
    public static List<Sample> parseMessage(String msg) {
        Pattern sampleRegex = Pattern.compile(
                "^id ([0-9]+) " + // group 1
                        "time ([0-9]+) " + // group 2
                        "x ([-]?[0-9]+\\.?[0-9]+) " + // group 3
                        "y ([-]?[0-9]+\\.?[0-9]+) " + // group 4
                        "z ([-]?[0-9]+\\.?[0-9]+)[ ]?$"  // group 5
        );

        List<Sample> samples = new ArrayList<>();

        for (String line : msg.split("\n")) {
            Matcher m = sampleRegex.matcher(line);
            if (!m.matches()) {
                System.out.println("Invalid sample line: ");
                System.out.println(line);
                continue; // skip invalid lines
            }

            Sample sample = new Sample();
            sample.sensorId = Integer.parseInt(m.group(1));
            sample.timestamp = Long.parseLong(m.group(2));
            sample.yaw = Double.parseDouble(m.group(3));   // X => yaw
            sample.pitch = Double.parseDouble(m.group(4)); // Y => pitch
            sample.roll = Double.parseDouble(m.group(5));  // Z => roll

            samples.add(sample);
        }

        return samples;
    }
}