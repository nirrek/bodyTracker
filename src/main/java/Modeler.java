import javafx.geometry.Point3D;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The Modeler is responsible for consuming a stream of data produced by
 * the Arduino. This data stream will be used to produce a 3-dimensional
 * model of the user's limb in space during each time-slice. The 3-dimensional
 * model produced by the Modeler will be consumed by the Renderer.
 */
public class Modeler extends EventEmitter implements Iterable<BothArms> {
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
	private Point3D leftShoulder;
	private Point3D rightShoulder;

	private int iterationUpTo;

	// Event constants
	public static final String NEW_SAMPLE = "newSample";

	//takes an input of some kind and outputs the arm positions
	//currently assuming arms start relaxed
	public Modeler(){

		elbowToWrist = 300;//Millimeters
		shoulderToElbow = 300;//TODO:Make this dynamic
		startLeftPitch = 0;//TODO: Make these inputs!
		startLeftRoll = 0;//-90;
		//startLeftYaw = 0;
		startRightPitch = 0;//Shoulder forwards/backwards
		startRightRoll = 0;//90;//Shoulder up/down
		//startRightYaw = 0;//Theoretically unused
		leftShoulder = new Point3D(0, 0, 0);//TODO: Dynamic again
		rightShoulder = new Point3D(0, 0, 0);
		BothArms currentArms = new BothArms(new Arm(leftShoulder, 0, -shoulderToElbow, 0, 0, -elbowToWrist, 0, true),
				new Arm(rightShoulder, 0, -shoulderToElbow, 0, 0, -elbowToWrist, 0, false));//Create arms at rest
		pastArms.add(currentArms);

		iterationUpTo = 0;
	}

	//TODO calibration step
	//TODO work out position from orientation of sensors

//	public void advanceIteration(double leftPitch, double leftRoll, double leftYaw, double rightPitch,
//			double rightRoll, double rightYaw){
//
//		double currentLeftPitch = leftPitch - startLeftPitch;
//		double currentLeftRoll = leftRoll - startLeftRoll;
//		//double currentLeftYaw = leftYaw - startLeftYaw;
//		double currentRightPitch = rightPitch - startRightPitch;
//		double currentRightRoll = rightRoll - startRightRoll;
//		//double currentRightYaw = rightYaw - startRightYaw;
//
//		double lEX = shoulderToElbow * sine(currentLeftRoll) * sine(currentLeftPitch);//Forwards/back
//		double lEY = shoulderToElbow * sine(currentLeftRoll) * cosine(currentLeftPitch);//Up down
//		double lEZ = shoulderToElbow * sine(currentLeftRoll);//Z being left/right
//
//		double rEX = shoulderToElbow * sine(currentRightRoll) * sine(currentRightPitch);
//		double rEY = shoulderToElbow * sine(currentRightRoll) * cosine(currentRightPitch);
//		double rEZ = -shoulderToElbow * sine(currentRightRoll);
//
//		double lWX = elbowToWrist * sine(-90) * sine(0);
//		double lWY = -elbowToWrist * sine(-90) * cosine(0);
//		double lWZ = elbowToWrist * cosine(-90);
//
//		double rWX = elbowToWrist * sine(-90) * sine(0);
//		double rWY = elbowToWrist * sine(-90) * cosine(0);
//		double rWZ = -elbowToWrist * cosine(-90);
//
//		Arm newLeftArm = new Arm(leftShoulder, lEX, lEY, lEZ, lWX, lWY, lWZ, true);
//		Arm newRightArm = new Arm(rightShoulder, rEX, rEY, rEZ, rWX, rWY, rWZ, false);
//		BothArms currentArms = new BothArms(newLeftArm, newRightArm);
//		pastArms.add(currentArms);
//
//		this.emit(NEW_SAMPLE);
//	}

	/**
	 * Computes an arm's position using the specified sensor sample. The new
	 * position is modeled by the returned Arm object.
	 * @param armSample The sensor sample used to compute the arm position.
	 * @param isLeftArm Whether we are computing the arm position for a left arm.
	 *                  If false, it implied we are computing right arm position.
	 * @return A new arm object that models the arm's position in space.
	 */
	public Arm computeNewArmPosition(Sample armSample, boolean isLeftArm) {
		// flip the sign for certain operations when it is a left arm.
		double sign =  isLeftArm ? 1 : -1;

		// Compute the roll/pitch/yaw relative to calibrated start position
		double initialRoll = isLeftArm ? startLeftRoll : startRightRoll;
		double initialPitch = isLeftArm ? startLeftPitch : startRightPitch;
		double roll = armSample.roll - initialRoll;
		double pitch = armSample.pitch - initialPitch;

		// Upper arm
		double upperX = shoulderToElbow * sine(roll) * sine(pitch); // Forwards/back
		double upperY = (sign) * shoulderToElbow * sine(roll) * cosine(pitch); // Up down
		double upperZ = (-sign) * shoulderToElbow * cosine(roll);			// Z being left/right

		// Lower arm
		double lowerX = elbowToWrist * sine(-90) * sine(0);
		double lowerY = elbowToWrist * sine(-90) * cosine(0);
		double lowerZ = (-sign) * elbowToWrist * cosine(-90);

		Point3D shoulderLocation = (isLeftArm) ? leftShoulder
													: rightShoulder;
		return new Arm(shoulderLocation,
				       upperX, upperY, upperZ,
				       lowerX, lowerY, lowerZ,
				       isLeftArm);
	}

	/**
	 * Works out the cosine of an anglee in degrees
	 * @param angleInDegrees
	 * @return the cosine of the angle
	 */
	private double cosine(double angleInDegrees){
		return Math.cos(Math.toRadians(angleInDegrees));
	}

	/**
	 * Works out the sine of an anglee in degrees
	 * @param angleInDegrees
	 * @return the sine of the angle
	 */
	private double sine(double angleInDegrees){
		return Math.sin(Math.toRadians(angleInDegrees));
	}

	// TODO: (Kerrin) might be better to rename Sample => SensorReading

	/**
	 * Adds a new arm position when only a right arm sample is available.
	 * Right now the shirt is only using a single sensor, so we want the modeler
	 * to work under those conditions as well.
	 * @param leftArmSample The sensor reading for the right arm.
	 */
	public void newSensorReading(Sample rightArmSample) {
		Arm rightArm = computeNewArmPosition(rightArmSample, false);
		BothArms arms = new BothArms(null, rightArm);
		pastArms.add(arms);
		this.emit(NEW_SAMPLE);
	}

	// TODO decide how we determine left/right sample in the client code
	// for this method. Possibly just a hardcoded mapping from sensorId => side
	/**
	 * Adds an arm position to the modeler for a new time slice from the
	 * specified sensor readings.
	 * @param leftArmSample  The left arm sensor reading
	 * @param rightArmSample The righ arm sensor reading
	 */
	public void newSensorReading(Sample leftArmSample, Sample rightArmSample) {
		Arm leftArm = computeNewArmPosition(leftArmSample, true);
		Arm rightArm = computeNewArmPosition(rightArmSample, false);
		BothArms arms = new BothArms(leftArm, rightArm);
		pastArms.add(arms);
		this.emit(NEW_SAMPLE);
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

	// number of past arms calculated
	// mainly used for unit testing
	public int pastArmsCount() {
		return pastArms.size();
	}
}
