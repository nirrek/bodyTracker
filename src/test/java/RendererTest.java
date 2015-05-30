import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class RendererTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    // should parse single line message correctly
    @Test
    public void parseSingleLineMessage() {
        String msg = "id 55 time 25262 x 352.6250 y -0.5625 z -100.1875 \n";
        List<Sample> samples = Sample.parseMessage(msg);

        assertEquals(1, samples.size());
        Sample sample = samples.get(0);
        assertEquals(55, sample.sensorId);
        assertEquals(25262, sample.timestamp);
        assertEquals(352.6250, sample.yaw, 0.01);
        assertEquals(-0.5625, sample.pitch, 0.01);
        assertEquals(-100.1875, sample.roll, 0.01);
    }

    // should parse multiline message correctly
    @Test
    public void parseMultiLineMessage() {
        String msg = "id 55 time 9299 x 359.9375 y -39.8750 z -22.6250\n" +
                     "id 155 time 19299 x 159.9375 y -139.8750 z -122.6250\n";
        List<Sample> samples = Sample.parseMessage(msg);

        // X => yaw      Y => pitch      Z => roll  (may 23rd)
        assertEquals(2, samples.size());
        Sample sample = samples.get(0);
        assertEquals(55, sample.sensorId);
        assertEquals(9299, sample.timestamp);
        assertEquals(359.9375, sample.yaw, 0.01);
        assertEquals(-39.8750, sample.pitch, 0.01);
        assertEquals(-22.6250, sample.roll, 0.01);

        Sample sample2 = samples.get(1);
        assertEquals(155, sample2.sensorId);
        assertEquals(19299, sample2.timestamp);
        assertEquals(159.9375, sample2.yaw, 0.01);
        assertEquals(-139.8750, sample2.pitch, 0.01);
        assertEquals(-122.6250, sample2.roll, 0.01);
    }

    // should exclude malformed lines
    @Test
    public void excludesMalformedLines() {
        String msg = "id 55 time 9299 x 359.9375 y -39.8750 z -22.6250\n" +
                "id 1:)55 time 19299 x 159.9375 y -139.8750 z -122.6250\n";
        List<Sample> samples = Sample.parseMessage(msg);

        assertEquals(1, samples.size());
    }

    // should return the empty list if no valid samples
    @Test
    public void emptyListOnNoValidSamples() {
        String msg = "?id 55 time 9299 x 359.9375 y -39.8750 z -22.6250\n" +
                "id 1:)55 time 19299 x 159.9375 y -139.8750 z -122.6250\n";
        List<Sample> samples = Sample.parseMessage(msg);

        assertEquals(0, samples.size());
    }


}