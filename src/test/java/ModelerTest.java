import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class ModelerTest {
	Modeler modeler;

	// Allowable delta whe comparing equality of doubles. Required for handling
	// imprecision of the 64-bit binary floating point representation.
	double delta = 0.0001;

    @Before
    public void beforeEach() throws Exception {
    	modeler = new Modeler();
    }

    @Test
    public void doWeEvenHaveArms() throws Exception {
    	Arm sampleArm = modeler.getPastRightArm(0);
    	assertFalse("Arm initulisation failed", sampleArm.isLeftArm());
    }

//    @Test
//    public void doesTimePass() throws Exception{
//    	modeler.advanceIteration(0, -90, 0, 0, 0, 0);
//    	modeler.advanceIteration(0, -90, 0, 0, 0, 0);
//    	modeler.advanceIteration(24, 80, -24, 0, 0, 0);
//    	Arm sampleArm = modeler.getPastLeftArm(2);
//
//		assertEquals("Elbow X relaxed position calculation failed",
//				0.0, sampleArm.elbowPos().getX(), delta);
//		assertEquals("Elbow Y relaxed position calculation failed",
//				-298, sampleArm.elbowPos().getY(), delta);
//		assertEquals("Wrist X relaxed position calculation failed",
//				0.0, sampleArm.wristPos().getX(), delta);
//		assertEquals("Wrist Y relaxed position calculation failed",
//				-598, sampleArm.wristPos().getY(), delta);
//    }

//    @Test
//    public void readThemAll() throws Exception{
//    	assertTrue("Initial position not there", modeler.getNextSample() != null);
//    	assertFalse("Our only position should already be read", modeler.hasUnreadSample());
//    	modeler.advanceIteration(24, 80, 23, 0, 0, 0);
//    	assertTrue("There is a new iteration that should register as unread!", modeler.hasUnreadSample());
//   }

	// The calculations for arm position should be correct
	@Test
	public void calculationsShouldBeCorrect() {
		Sample nextSample = new Sample(0, 0, -90.0, 0.0, 0.0);//Roll, yaw, pitch
		Arm sampleArm = modeler.computeNewArmPosition(nextSample, true);

		assertEquals("Elbow X relaxed position calculation failed",
				0.0, sampleArm.elbowPos().getX(), delta);
		assertEquals("Elbow Y relaxed position calculation failed",
				-300, sampleArm.elbowPos().getY(), delta);
		assertEquals("Elbow Z relaxed position calculation failed",
				0, sampleArm.elbowPos().getZ(), delta);
		assertEquals("Wrist X relaxed position calculation failed",
				0.0, sampleArm.wristPos().getX(), delta);
		assertEquals("Wrist Y relaxed position calculation failed",
				-600, sampleArm.wristPos().getY(), delta);
		assertEquals("Wrist Z relaxed position calculation failed",
				0, sampleArm.wristPos().getZ(), delta);


		nextSample = new Sample(0, 0, 0.0, 0.0, 0.0);
		sampleArm = modeler.computeNewArmPosition(nextSample, true);
		assertEquals("Left Elbow X abduction position calculation failed",
				0.0, sampleArm.elbowPos().getX(), delta);
		assertEquals("Left Elbow Y abduction position calculation failed",
				0, sampleArm.elbowPos().getY(), delta);
		assertEquals("Left Elbow Z abduction position calculation failed",
				-300, sampleArm.elbowPos().getZ(), delta);
		assertEquals("Left Wrist X abduction position calculation failed",
				0.0, sampleArm.wristPos().getX(), delta);
		assertEquals("Left Wrist Y abduction position calculation failed",
				-300, sampleArm.wristPos().getY(), delta);
		assertEquals("Left Elbow Z abduction position calculation failed",
				-300, sampleArm.elbowPos().getZ(), delta);

		nextSample = new Sample(0, 0, 0.0, 0.0, 90.0);
		sampleArm = modeler.computeNewArmPosition(nextSample, false);
		assertEquals("Right Elbow X abduction position calculation failed",
				0.0, sampleArm.elbowPos().getX(), delta);
		assertEquals("Right Elbow Y abduction position calculation failed",
				0, sampleArm.elbowPos().getY(), delta);
		assertEquals("Right Elbow Z abduction position calculation failed",
				300, sampleArm.elbowPos().getZ(), delta);
		assertEquals("Right Wrist X abduction position calculation failed",
				0.0, sampleArm.wristPos().getX(), delta);
		assertEquals("Right Wrist Y abduction position calculation failed",
				-300, sampleArm.wristPos().getY(), delta);
		assertEquals("Right Elbow Z abduction position calculation failed",
				300, sampleArm.elbowPos().getZ(), delta);

		nextSample = new Sample(0, 0, -90.0, 0.0, -90.0);
		sampleArm = modeler.computeNewArmPosition(nextSample, true);
		assertEquals("Left Elbow X flexion position calculation failed",
				300, sampleArm.elbowPos().getX(), delta);
		assertEquals("Left Elbow Y flexion position calculation failed",
				0, sampleArm.elbowPos().getY(), delta);
		assertEquals("Left Elbow Z flexion position calculation failed",
				0, sampleArm.elbowPos().getZ(), delta);
		assertEquals("Left Wrist X flexion position calculation failed",
				300, sampleArm.wristPos().getX(), delta);
		assertEquals("Left Wrist Y flexion position calculation failed",
				-300, sampleArm.wristPos().getY(), delta);
		assertEquals("Left Elbow Z flexion position calculation failed",
				0, sampleArm.elbowPos().getZ(), delta);

		nextSample = new Sample(0, 0, -90.0, 0.0, 90.0);
		sampleArm = modeler.computeNewArmPosition(nextSample, true);
		assertEquals("Left Elbow X extension position calculation failed",
				-300, sampleArm.elbowPos().getX(), delta);
		assertEquals("Left Elbow Y extension position calculation failed",
				0, sampleArm.elbowPos().getY(), delta);
		assertEquals("Left Elbow Z extension position calculation failed",
				0, sampleArm.elbowPos().getZ(), delta);
		assertEquals("Left Wrist X extension position calculation failed",
				-300, sampleArm.wristPos().getX(), delta);
		assertEquals("Left Wrist Y extension position calculation failed",
				-300, sampleArm.wristPos().getY(), delta);
		assertEquals("Left Elbow Z extension position calculation failed",
				0, sampleArm.elbowPos().getZ(), delta);
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