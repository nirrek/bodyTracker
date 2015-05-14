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
    	assertFalse("We failed already!", sampleArm.isLeftArm());
    }
    
    @Test
    public void doesTimePass() throws Exception{
    	testModeler.advanceIteration(0, 0, 0, 0, 0, 0);
    	testModeler.advanceIteration(0, 0, 0, 0, 0, 0);
    	Arm sampleArm = testModeler.getPastLeftArm(1);
    	assertTrue("That's weird...", sampleArm.elbowPos().X() - 0.3 < 0.0001);
    	assertTrue("That's weird...", sampleArm.wristPos().X() - 0.6 < 0.0001);
    }

    @After
    public void tearDown() throws Exception {
    	
    }
}