/**
 * This arm class contains the position of an individual arm
 */
public class Arm {
	
	private float shoulderFirstDegree;
	private float shoulderSecondDegree;
	private float elbowFirstDegree;
	private float elbowSecondDegree;
	private boolean leftArm;
	
	/**
	 * Initialise the Arm object
	 */
	public Arm(boolean left){
		this.shoulderFirstDegree = 0;
		this.shoulderSecondDegree = 0;
		this.elbowFirstDegree = 0;
		this.elbowSecondDegree = 0;
		this.leftArm = left;
	}
	
	/**
	 * Initialise the Arm object with known positions
	 */
	public Arm(float sf, float ss, float ef, float es, boolean left){
		shoulderFirstDegree = sf;
		shoulderSecondDegree = ss;
		elbowFirstDegree = ef;
		elbowSecondDegree = es;
		this.leftArm = left;
	}
	
	/**
	 * Initialise the Arm object as an altered version of an eariler arm
	 */
	public Arm(Arm initialArm, float sf, float ss, float ef, float es){
		shoulderFirstDegree = initialArm.shoulderFirstDegree() + sf;
		shoulderSecondDegree = initialArm.shoulderSecondDegree() + ss;
		elbowFirstDegree = initialArm.elbowFirstDegree() + ef;
		elbowSecondDegree = initialArm.elbowSecondDegree() + es;
		this.leftArm = initialArm.isLeftArm();
	}
	
	/**
	 * Reprort the angle of the shoulder in the back-and-forth axis.
	 * 0 means the arm is pointing straight down, 90 mean the arm is pointing straight forward, and -90 is straight back
	 */
	public float shoulderFirstDegree(){
		return shoulderFirstDegree;
	}
	
	/**
	 * Reprort the angle of the shoulder in the side-to-side axis.
	 * 0 means the elbow is pointing straight down, 90 mean the elbow is pointing right, parrallel to the ground,
	 *  and -90 is pointing left, parralell to the ground (from the wearer's POV)
	 */
	public float shoulderSecondDegree(){
		return shoulderSecondDegree;
	}
	
	/**
	 * Reprort the angle of the elbow in the back-and-forth axis.
	 * 0 means the arm is completly straight, 90 mean the arm is forming a 90 degree turn,
	 *  and 180 means the arm is resting against itself
	 */
	public float elbowFirstDegree(){
		return elbowFirstDegree;
	}
	
	/**
	 * Reprort the angle of the elbow in the back-and-forth axis.
	 * (The following examples assume a shoulderFirstDegree and shoulderSecondDegree of 0, and an elbowFirstDegree of 90)
	 * 0 means the fist is pointing forward, 90 mean the fist is pointing right,
	 *  and -90 means the fist is pointing left
	 */
	public float elbowSecondDegree(){
		return elbowSecondDegree;
	}
	
	/**
	 * Returns true if this is a left arm, false if a right arm
	 */
	public boolean isLeftArm(){
		return leftArm;
	}
}
