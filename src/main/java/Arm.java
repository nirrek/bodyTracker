/**
 * This arm class contains the position of an individual arm
 */
public class Arm {
	
	private float shoulderFirstDegree;
	private float shoulderSecondDegree;
	private float elbowFirstDegree;
	private float elbowSecondDegree;
	
	/*
	 * Initialise the Arm object
	 */
	public Arm(){
		shoulderFirstDegree = 0;
		shoulderSecondDegree = 0;
		elbowFirstDegree = 0;
		elbowSecondDegree = 0;
	}
	
	/*
	 * Initialise the Arm object with known positions
	 */
	public Arm(float sf, float ss, float ef, float es){
		shoulderFirstDegree = sf;
		shoulderSecondDegree = ss;
		elbowFirstDegree = ef;
		elbowSecondDegree = es;
	}
	
	/*
	 * Reprort the angle of the shoulder in the back-and-forth axis.
	 * 0 means the arm is pointing straight down, 90 mean the arm is pointing straight forward, and -90 is straight back
	 */
	public float shoulderFirstDegree(){
		return shoulderFirstDegree;
	}
	
	/*
	 * Reprort the angle of the shoulder in the side-to-side axis.
	 * 0 means the elbow is pointing straight down, 90 mean the elbow is pointing right, parrallel to the ground,
	 *  and -90 is pointing left, parralell to the ground (from the wearer's POV)
	 */
	public float shoulderSecondDegree(){
		return shoulderSecondDegree;
	}
	
	/*
	 * Reprort the angle of the elbow in the back-and-forth axis.
	 * 0 means the arm is completly straight, 90 mean the arm is forming a 90 degree turn,
	 *  and 180 means the arm is resting against itself
	 */
	public float elbowFirstDegree(){
		return elbowFirstDegree;
	}
	
	/*
	 * Reprort the angle of the elbow in the back-and-forth axis.
	 * (The following examples assume a shoulderFirstDegree and shoulderSecondDegree of 0, and an elbowFirstDegree of 90)
	 * 0 means the fist is pointing forward, 90 mean the fist is pointing right,
	 *  and -90 means the fist is pointing left
	 */
	public float elbowSecondDegree(){
		return elbowSecondDegree;
	}
}
