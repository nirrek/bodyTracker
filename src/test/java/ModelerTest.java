import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class ModelerTest {
	Modeler modeler;
	double delta = 0.0001; // handle imprecision of binary floating point.

    @Before
    public void beforeEach() throws Exception {
    	modeler = new Modeler();
    }

    @Test
    public void doWeEvenHaveArms() throws Exception {
    	Arm sampleArm = modeler.getPastRightArm(0);
    	assertFalse("Arm initulisation failed", sampleArm.isLeftArm());
    }

    @Ignore("Y position calculation being re-done")
    @Test
    public void doesTimePass() throws Exception{
    	modeler.advanceIteration(0, 0, 0, 0, 0, 0);
    	modeler.advanceIteration(0, 0, 0, 0, 0, 0);
    	modeler.advanceIteration(24, 80, 23, 0, 0, 0);
    	Arm sampleArm = modeler.getPastLeftArm(2);
    	
		double delta = 0.0001; // handle imprecision of binary floating point.
		assertEquals("Elbow X relaxed position calculation failed",
				0.0, sampleArm.elbowPos().X() - 0, delta);
		assertEquals("Elbow Y relaxed position calculation failed",
				0.0, sampleArm.elbowPos().Y() - (-298), delta);
		assertEquals("Wrist X relaxed position calculation failed",
				0.0, sampleArm.wristPos().X() - 0, delta);
		assertEquals("Wrist Y relaxed position calculation failed",
				0.0, sampleArm.wristPos().Y() - (-598), delta);
    }

    @Test
    public void readThemAll() throws Exception{
    	assertTrue("Initial position not there", modeler.getNextSample() != null);
    	assertFalse("Our only position should already be read", modeler.hasUnreadSample());
    	modeler.advanceIteration(24, 80, 23, 0, 0, 0);
    	assertTrue("There is a new iteration that should register as unread!", modeler.hasUnreadSample());
    }

	// The calculations for arm position should be correct
    @Ignore("Y position calculation being re-done")
	@Test
	public void calculationsShouldBeCorrect() {
		Sample nextSample = new Sample(0, 0, 0.0, 0.0, 0.0);
		Arm sampleArm = modeler.computeNewArmPosition(nextSample, true);
		
		assertEquals("Elbow X relaxed position calculation failed",
				0.0, sampleArm.elbowPos().X() - 0, delta);
		assertEquals("Elbow Y relaxed position calculation failed",
				0.0, sampleArm.elbowPos().Y() - (-298), delta);
		assertEquals("Elbow Z relaxed position calculation failed",
				0.0, sampleArm.elbowPos().Z() - (-1), delta);
		assertEquals("Wrist X relaxed position calculation failed",
				0.0, sampleArm.wristPos().X() - 0, delta);
		assertEquals("Wrist Y relaxed position calculation failed",
				0.0, sampleArm.wristPos().Y() - (-598), delta);
		assertEquals("Wrist Z relaxed position calculation failed",
				0.0, sampleArm.wristPos().Z() - (-1), delta);
		
		
		nextSample = new Sample(0, 0, 90.0, 0.0, 0.0);
		sampleArm = modeler.computeNewArmPosition(nextSample, true);
		assertEquals("Left Elbow X abduction position calculation failed",
				0.0, sampleArm.elbowPos().X() - 0, delta);
		assertEquals("Left Elbow Y abduction position calculation failed",
				0.0, sampleArm.elbowPos().Y() - 2, delta);
		System.out.println(sampleArm.elbowPos().Z());
		assertEquals("Left Elbow Z abduction position calculation failed",
				0.0, sampleArm.elbowPos().Z() - (-301), delta);
		assertEquals("Left Wrist X abduction position calculation failed",
				0.0, sampleArm.wristPos().X() - 0, delta);
		assertEquals("Left Wrist Y abduction position calculation failed",
				0.0, sampleArm.wristPos().Y() - (-298), delta);
		assertEquals("Left Elbow Z abduction position calculation failed",
				0.0, sampleArm.elbowPos().Z() - (-301), delta);
		
		nextSample = new Sample(0, 0, -90.0, 0.0, 0.0);
		sampleArm = modeler.computeNewArmPosition(nextSample, false);
		assertEquals("Right Elbow X abduction position calculation failed",
				0.0, sampleArm.elbowPos().X() - 0, delta);
		assertEquals("Right Elbow Y abduction position calculation failed",
				0.0, sampleArm.elbowPos().Y() - 2, delta);
		assertEquals("Right Elbow Z abduction position calculation failed",
				0.0, sampleArm.elbowPos().Z() - 301, delta);
		assertEquals("Right Wrist X abduction position calculation failed",
				0.0, sampleArm.wristPos().X() - 0, delta);
		assertEquals("Right Wrist Y abduction position calculation failed",
				0.0, sampleArm.wristPos().Y() - (-298), delta);
		assertEquals("Right Elbow Z abduction position calculation failed",
				0.0, sampleArm.elbowPos().Z() - 301, delta);
	}


	// should add a new arms positions that correspond to sensor readings.
	@Test
	public void shouldCreateNewArmsFromSensorReading() {
		Sample sampleLeft = new Sample(0, 5250, 0.0, 0.0, 0.0);
		Sample sampleRight = new Sample(0, 5250, 0.0, 0.0, 0.0);
		modeler.newSensorReading(sampleLeft, sampleRight);

		sampleLeft = new Sample(0, 5500, 0.0, 0.0, 0.0);
		sampleRight = new Sample(0, 5500, 0.0, 0.0, 0.0);
		modeler.newSensorReading(sampleLeft, sampleRight);

		sampleLeft = new Sample(0, 5750, 24.0, 80.0, 23.0);
		sampleRight = new Sample(0, 5750, 0.0, 0.0, 0.0);
		modeler.newSensorReading(sampleLeft, sampleRight);

		// The initial arm position is added on construction. Thus, +1 count.
		assertEquals(1 + 3, modeler.pastArmsCount());
	}

	// should allow just adding left arms
	@Test
	public void sensorReadingsFromLeftArmOnly() {
		Sample sampleLeft = new Sample(0, 5250, 0.0, 0.0, 0.0);
		modeler.newSensorReading(sampleLeft);

		sampleLeft = new Sample(0, 5500, 0.0, 0.0, 0.0);
		modeler.newSensorReading(sampleLeft);

		sampleLeft = new Sample(0, 5750, 24.0, 80.0, 23.0);
		modeler.newSensorReading(sampleLeft);

		// The initial arm position is added on construction. Thus, +1 count.
		assertEquals(1 + 3, modeler.pastArmsCount());
	}


}