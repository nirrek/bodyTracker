import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ModelerTest {
	Modeler testModeler;

    @Before
    public void setUp() throws Exception {
    	testModeler = new Modeler();
    }
    
    @Test
    public void doWeEvenHaveArms() throws Exception {
    	Arm sampleArm = testModeler.getPastRightArm(0);
    	assertFalse("Arm initulisation failed", sampleArm.isLeftArm());
    }
    
    @Test
    public void doesTimePass() throws Exception{
    	testModeler.advanceIteration(0, 0, 0, 0, 0, 0);
    	testModeler.advanceIteration(0, 0, 0, 0, 0, 0);
    	testModeler.advanceIteration(24, 80, 23, 0, 0, 0);
    	Arm sampleArm = testModeler.getPastLeftArm(2);
    	assertTrue("Elbow position calculation failed", sampleArm.elbowPos().X() - 30 < 0.0001);
    	assertTrue("Wrist position calculation failed", sampleArm.wristPos().X() - 60 < 0.0001);
    }

    @After
    public void tearDown() throws Exception {
    	
    }
}