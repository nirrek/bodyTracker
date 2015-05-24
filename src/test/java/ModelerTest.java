import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class ModelerTest {
	Modeler modeler;

    @Before
    public void beforeEach() throws Exception {
    	modeler = new Modeler();
    }

    @Test
    public void doWeEvenHaveArms() throws Exception {
    	Arm sampleArm = modeler.getPastRightArm(0);
    	assertFalse("Arm initulisation failed", sampleArm.isLeftArm());
    }

    @Test
    public void doesTimePass() throws Exception{
    	modeler.advanceIteration(0, 0, 0, 0, 0, 0);
    	modeler.advanceIteration(0, 0, 0, 0, 0, 0);
    	modeler.advanceIteration(24, 80, 23, 0, 0, 0);
    	Arm sampleArm = modeler.getPastLeftArm(2);
    	//assertTrue("Elbow position calculation failed", Math.abs(sampleArm.elbowPos().X()) - 0 < 0.0001);
    	//assertTrue("Elbow position calculation failed", Math.abs(sampleArm.elbowPos().Y()) - 30.2 < 0.0001);
    	//assertTrue("Wrist position calculation failed", Math.abs(sampleArm.wristPos().X()) - 0 < 0.0001);
    	//assertTrue("Wrist position calculation failed", Math.abs(sampleArm.wristPos().Y()) - 60.2 < 0.0001);

		// TODO. See whether Harrison was trying to see if the above results
		// were meant to check that the results were within 0.0001 of 0.0.
		double delta = 0.0001; // handle imprecision of binary floating point.
		assertEquals("Elbow X relaxed position calculation failed",
				0.0, Math.abs(sampleArm.elbowPos().X()) - 0, delta);
		assertEquals("Elbow Y relaxed position calculation failed",
				0.0, Math.abs(sampleArm.elbowPos().Y()) - 298, delta);
		assertEquals("Wrist X relaxed position calculation failed",
				0.0, Math.abs(sampleArm.wristPos().X()) - 0, delta);
		assertEquals("Wrist Y relaxed position calculation failed",
				0.0, Math.abs(sampleArm.wristPos().Y()) - 598, delta);
    }

    @Test
    public void readThemAll() throws Exception{
    	assertTrue("Initial position not there", modeler.getNextSample() != null);
    	assertFalse("Our only position should already be read", modeler.hasUnreadSample());
    	modeler.advanceIteration(24, 80, 23, 0, 0, 0);
    	assertTrue("There is a new iteration that should register as unread!", modeler.hasUnreadSample());
    }

	// The calculations for arm position should be correct
	@Test
	public void calculationsShouldBeCorrect() {
		Sample nextSample = new Sample();
		
		// TODO: Harrison should fill this test in

		// 1. create a sample with the desired position
		// 2. run computeNewArmPosition() to calculate a new arm position
		// 3. test that the return Arm has all the right coordinates

		// the above algorithm should be applied to a number of different
		// samples
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