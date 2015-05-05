import java.math.BigDecimal;

/**
 * This arm class contains the position of an individual arm
 */
public class Arm {
	
	private BigDecimal shoulderFirstDegree;
	private BigDecimal shoulderSecondDegree;
	private BigDecimal elbowFirstDegree;
	private BigDecimal elbowSecondDegree;
	private boolean leftArm;
	
	/**
	 * Initialize the Arm object
	 */
	public Arm(boolean left){
		this.shoulderFirstDegree = new BigDecimal("0");
		this.shoulderSecondDegree = new BigDecimal("0");
		this.elbowFirstDegree = new BigDecimal("0");
		this.elbowSecondDegree = new BigDecimal("0");
		this.leftArm = left;
	}
	
	/**
	 * Initialize the Arm object with known positions
	 */
	public Arm(BigDecimal sf, BigDecimal ss, BigDecimal ef, BigDecimal es, boolean left){
		shoulderFirstDegree = sf;
		shoulderSecondDegree = ss;
		elbowFirstDegree = ef;
		elbowSecondDegree = es;
		this.leftArm = left;
	}
	
	/**
	 * Initialize the Arm object as an altered version of an earlier arm
	 */
	public Arm(Arm initialArm, BigDecimal sf, BigDecimal ss, BigDecimal ef, BigDecimal es){
		shoulderFirstDegree = initialArm.shoulderFirstDegree().add(sf);
		shoulderSecondDegree = initialArm.shoulderSecondDegree().add(ss);
		elbowFirstDegree = initialArm.elbowFirstDegree().add(ef);
		elbowSecondDegree = initialArm.elbowSecondDegree().add(es);
		this.leftArm = initialArm.isLeftArm();
	}
	
	/**
	 * Report the angle of the shoulder in the back-and-forth axis.
	 * 0 means the arm is pointing straight down, 90 mean the arm is pointing straight forward, and -90 is straight back
	 */
	public BigDecimal shoulderFirstDegree(){
		return shoulderFirstDegree;
	}
	
	/**
	 * Report the angle of the shoulder in the side-to-side axis.
	 * 0 means the elbow is pointing straight down, 90 mean the elbow is pointing right, parallel to the ground,
	 *  and -90 is pointing left, parallel to the ground (from the wearer's POV)
	 */
	public BigDecimal shoulderSecondDegree(){
		return shoulderSecondDegree;
	}
	
	/**
	 * Report the angle of the elbow in the back-and-forth axis.
	 * 0 means the arm is completely straight, 90 mean the arm is forming a 90 degree turn,
	 *  and 180 means the arm is resting against itself
	 */
	public BigDecimal elbowFirstDegree(){
		return elbowFirstDegree;
	}
	
	/**
	 * Report the angle of the elbow in the back-and-forth axis.
	 * (The following examples assume a shoulderFirstDegree and shoulderSecondDegree of 0, and an elbowFirstDegree of 90)
	 * 0 means the fist is pointing forward, 90 mean the fist is pointing right,
	 *  and -90 means the fist is pointing left
	 */
	public BigDecimal elbowSecondDegree(){
		return elbowSecondDegree;
	}
	
	/**
	 * Returns true if this is a left arm, false if a right arm
	 */
	public boolean isLeftArm(){
		return leftArm;
	}
}
