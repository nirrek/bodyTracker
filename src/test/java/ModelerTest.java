import java.math.BigDecimal;

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
    	testModeler.advanceIteration();
    	testModeler.advanceIteration();
    	Arm sampleArm = testModeler.getPastLeftArm(1);
    	assertTrue("That's weird...", sampleArm.elbowSecondDegree().compareTo(new BigDecimal("0")) == 0);
    }

    @After
    public void tearDown() throws Exception {
    	
    }
}