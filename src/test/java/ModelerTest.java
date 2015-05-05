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
    	Arm sampleArm = testModeler.getRightArm();
    	assertFalse("We failed already!", sampleArm.isLeftArm());
    }

    @After
    public void tearDown() throws Exception {
    	
    }
}