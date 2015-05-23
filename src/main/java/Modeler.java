import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The Modeler is responsible for consuming a stream of data produced by
 * the Arduino. This data stream will be used to produce a 3-dimensional
 * model of the user's limb in space during each time-slice. The 3-dimensional
 * model produced by the Modeler will be consumed by the Renderer.
 */
public class Modeler implements Iterable<BothArms>{
	
	private List<BothArms> pastArms = new ArrayList<BothArms>();
	private double secondsBetweenSamples = 0.25;//Currently four samples per second
	private double elbowToWrist;
	private double shoulderToElbow;
	private double startLeftPitch;
	private double startLeftRoll;
	//private double startLeftYaw;
	private double startRightPitch;
	private double startRightRoll;
	//private double startRightYaw;
	private PointInSpace leftShoulder;
	private PointInSpace rightShoulder;
	
	private int iterationUpTo;
	
	//takes an input of some kind and outputs the arm positions
	//currently assuming arms start relaxed
	public Modeler(){
		elbowToWrist = 30;
		shoulderToElbow = 30;//TODO:Make this dynamic
		startLeftPitch = 0;//TODO: Make these inputs!
		startLeftRoll = 0;
		//startLeftYaw = 0;
		startRightPitch = 0;//Shoulder forwards/backwards
		startRightRoll = 0;//Shoulder up/down
		//startRightYaw = 0;//Theoretically unused
		leftShoulder = new PointInSpace(0, 0.2, -0.1);//TODO: Dynamic again
		rightShoulder = new PointInSpace(0, 0.2, 0.1);
		BothArms currentArms = new BothArms(new Arm(leftShoulder, 0, shoulderToElbow, 0, 0, elbowToWrist, 0, true),
				new Arm(rightShoulder, 0, shoulderToElbow, 0, 0, elbowToWrist, 0, false));//Create arms at rest
		pastArms.add(currentArms);
		
		iterationUpTo = 0;
	}
	
	//TODO calibration step
	//TODO work out position from orientation of sensors
	
	public void advanceIteration(double leftPitch, double leftRoll, double leftYaw, double rightPitch,
			double rightRoll, double rightYaw){
		double currentLeftPitch = leftPitch - startLeftPitch;
		double currentLeftRoll = leftRoll - startLeftRoll;
		//double currentLeftYaw = leftYaw - startLeftYaw;
		double currentRightPitch = rightPitch - startRightPitch;
		double currentRightRoll = rightRoll - startRightRoll;
		//double currentRightYaw = rightYaw - startRightYaw;
		
		double lEX = shoulderToElbow * Math.sin(currentLeftRoll) * Math.cos(currentLeftPitch);//Forwards/back
		double lEY = shoulderToElbow * Math.sin(currentLeftRoll) * Math.sin(currentLeftPitch);//Up down
		double lEZ = -shoulderToElbow * Math.cos(currentLeftRoll);//Z being left/right
		
		double rEX = shoulderToElbow * Math.sin(currentRightRoll) * Math.cos(currentRightPitch);
		double rEY = shoulderToElbow * Math.sin(currentRightRoll) * Math.sin(currentRightPitch);
		double rEZ = shoulderToElbow * Math.cos(currentRightRoll);
		
		double lWX = elbowToWrist * Math.sin(-Math.PI) * Math.cos(0);
		double lWY = elbowToWrist * Math.sin(-Math.PI) * Math.sin(0);
		double lWZ = -elbowToWrist * Math.cos(-Math.PI);
		
		double rWX = elbowToWrist * Math.sin(-Math.PI) * Math.cos(0);
		double rWY = elbowToWrist * Math.sin(-Math.PI) * Math.sin(0);
		double rWZ = elbowToWrist * Math.cos(-Math.PI);

		Arm newLeftArm = new Arm(leftShoulder, lEX, lEY, lEZ, lWX, lWY, lWZ, true);
		Arm newRightArm = new Arm(rightShoulder, rEX, rEY, rEZ, rWX, rWY, rWZ, false);
		BothArms currentArms = new BothArms(newLeftArm, newRightArm);
		pastArms.add(currentArms);
	}
	
	/**
	 * Returns true if there is an unread sample, false otherwise
	 * Use getNextSample to read the next one
	 */
	public boolean hasUnreadSample(){
		//past arms index = size - 1
		//iterationUpTo
		//have we read them all - is past arms index larger than iterationUpTo?
		return ((pastArms.size() - 1) >= iterationUpTo);
	}
	
	/**
	 * Returns the next BothArms object
	 * @return A BothArms object, or null if all objects have been read
	 */
	public BothArms getNextSample(){
		if (iterationUpTo >= pastArms.size()){
			return null;
		}
		BothArms result = pastArms.get(iterationUpTo);
		iterationUpTo++;
		return result;
	}
	
	/**
	 * Identical to getPastLeftArm(0)
	 * @return the most recent left arm object
	 */
	public Arm getMostRecentLeftArm(){
		return getPastLeftArm(0);
	}
	
	/**
	 * Identical to getPastRightArm(0)
	 * @return the most recent right arm object
	 */
	public Arm getMostRecentRightArm(){
		return getPastRightArm(0);
	}
	
	/**
	 * Retrieve the left arm's position at a given iteration
	 * @param iterationsAgo the number of iterations since the arm position desired (0 is a valid value)
	 * @return The object representing the left arm of the subject
	 */
	public Arm getPastLeftArm(int iterationsAgo){
		if(iterationsAgo < 0 || iterationsAgo > pastArms.size()){
			BothArms armsInQuestion = pastArms.get(0);
			return armsInQuestion.getLeftArm();
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
	 * Returns an iterator for the past arms, with the most recent arms at the end of the array
	 */
	public Iterator<BothArms> iterator(){
		return pastArms.iterator();
	}
}
