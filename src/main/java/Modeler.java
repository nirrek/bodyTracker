import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * The Modeler is responsible for consuming a stream of data produced by
 * the Arduino. This data stream will be used to produce a 3-dimensional
 * model of the user's limb in space during each time-slice. The 3-dimensional
 * model produced by the Modeler will be consumed by the Renderer.
 */
public class Modeler {
	
	private ArrayList<BothArms> pastArms = new ArrayList<BothArms>();
	private double secondsBetweenSamples = 0.25;//Currently four samples per second
	
	//takes an input of some kind and outputs the arm positions
	//currently assuming arms start relaxed
	public Modeler(){
		BothArms currentArms = new BothArms(new Arm(true), new Arm(false));//Create arms at rest
		pastArms.add(currentArms);
	}
	
	public void advanceIteration(){
		BigDecimal zero = new BigDecimal("0");
		Arm pastLeftArm = getPastLeftArm(0);
		Arm pastRightArm = getPastRightArm(0);
		Arm newLeftArm = new Arm(pastLeftArm, zero, zero, zero, zero);//Currently working on actual position changes
		Arm newRightArm = new Arm(pastRightArm, zero, zero, zero, zero);
		BothArms currentArms = new BothArms(newLeftArm, newRightArm);
		pastArms.add(currentArms);
	}
	
	/**
	 * Work out the degrees turned by a gyroscope in that session
	 * @param dps the dps reading from the gyroscope
	 * @return the degrees turned in that sensor session
	 */
	public double degreesTurned(double dps){
		return (dps * secondsBetweenSamples);
	}
	
	/**
	 * Retrieve the left arm's position at a given iteration
	 * @param iterationsAgo the number of iterations since the arm position desired (0 is a valid value)
	 * @return The object representing the left arm of the subject
	 */
	public Arm getPastLeftArm(int iterationsAgo){
		if(iterationsAgo < 0 || iterationsAgo > pastArms.size()){
			throw new Error("That is not a valid number of iterations ago!");
		}
		BothArms armsInQuestion = pastArms.get(pastArms.size() - (iterationsAgo + 1));
		return armsInQuestion.getLeftArm();
	}
	
	/**
	 * Retrieve the right arm's position at a given iteration
	 * @param iterationsAgo the number of iterations since the arm position desired (0 is a valid value)
	 * @return The object representing the right arm of the subject
	 */
	public Arm getPastRightArm(int iterationsAgo){
		if(iterationsAgo < 0 || iterationsAgo > pastArms.size()){
			throw new Error("That is not a valid number of iterations ago!");
		}
		BothArms armsInQuestion = pastArms.get(pastArms.size() - (iterationsAgo + 1));
		return armsInQuestion.getRightArm();
	}
	
	/**
	 * Basic wrapper class for two arms
	 */
	public class BothArms{
		private Arm leftArm;
		private Arm rightArm;
		
		/**
		 * Basic wrapper class for two arms
		 * @param leftArm the left arm
		 * @param rightArm the right arm
		 */
		public BothArms(Arm leftArm, Arm rightArm){
			this.leftArm = leftArm;
			this.rightArm = rightArm;
		}
		
		public Arm getLeftArm(){
			return leftArm;
		}
		
		public Arm getRightArm(){
			return rightArm;
		}
	}	
}
