/**
 * This arm class contains the position of an individual arm
 */
public class Arm {
	
	private boolean leftArm;
	private PointInSpace elbow;
	private PointInSpace wrist;
	
	/**
	 * Initialize the Arm object with known positions
	 */
	public Arm(double EX, double EY, double EZ, double WX, double WY, double WZ, boolean left){
		elbow = new PointInSpace(EX, EY, EZ);
		wrist = new PointInSpace(EX + WX, EY + WY, EZ + WZ);
		this.leftArm = left;
	}
	
	/**
	 * Returns true if this is a left arm, false if a right arm
	 */
	public boolean isLeftArm(){
		return leftArm;
	}
	
	public PointInSpace elbowPos(){
		return elbow;
	}
	
	public PointInSpace wristPos(){
		return wrist;
	}
}
