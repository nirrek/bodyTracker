import javafx.geometry.Point3D;

/**
 * This arm class contains the position of an individual arm
 */
public class Arm {

	private boolean leftArm;
	private Point3D elbow;
	private Point3D wrist;

	/**
	 * Initialize the Arm object with known positions
	 */
	public Arm(double EX, double EY, double EZ, double WX, double WY, double WZ, boolean left){
		elbow = new Point3D(EX, EY, EZ);
		wrist = new Point3D(EX + WX, EY + WY, EZ + WZ);
		this.leftArm = left;
	}

	/**
	 * Initialize the Arm object with known positions
	 */
	public Arm(Point3D shoulder, double EX, double EY, double EZ, double WX, double WY, double WZ, boolean left){
		elbow = new Point3D(EX + shoulder.getX(), EY + shoulder.getY(), EZ + shoulder.getZ());
		wrist = new Point3D(EX + WX + shoulder.getX(), EY + WY + shoulder.getY(), EZ + WZ + shoulder.getZ());
		this.leftArm = left;
		
	}

	/**
	 * Returns true if this is a left arm, false if a right arm
	 */
	public boolean isLeftArm(){
		return leftArm;
	}

	/**
	 * Returns the elbow position of the arm as a 3D point-in-space.
	 *
	 * The meaning of the X/Y/Z values are shown below. All directional words,
	 * such as 'in front', are with respect to the viewpoint of the arm's owner.
	 * 		- The X coordinate is positive for distance in front of the shoulder
	 * 		  and negative for distance behind the shoulder.
	 * 		- The Y coordinate is positive for distance above the shoulder and
	 * 		  negative for distance below the shoulder.
	 * 		- The Z coordinate is positive for distance to the right of the
	 * 		  resting position (arm beside body), and negative for distance
	 * 		  to the left of the resting position. This means that lifting
	 * 		  the arm outwards from the body will result in +ve values on the
	 * 		  right arm, and -ve values on the left arm.
	 *
	 * @return The 3D point-in-space representing the elbow's position.
	 */
	public Point3D elbowPos(){
		return elbow;
	}

	/**
	 * Returns the wrist position of the arm as a 3D point-in-space.
	 *
	 * The meaning of the X/Y/Z values are shown below. All directional words,
	 * such as 'in front', are with respect to the viewpoint of the arm's owner.
	 * 		- The X coordinate is positive for distance in front of the shoulder
	 * 		  and negative for distance behind the shoulder.
	 * 		- The Y coordinate is positive for distance above the shoulder and
	 * 		  negative for distance below the shoulder.
	 * 		- The Z coordinate is positive for distance to the right of the
	 * 		  resting position (arm beside body), and negative for distance
	 * 		  to the left of the resting position. This means that lifting
	 * 		  the arm outwards from the body will result in +ve values on the
	 * 		  right arm, and -ve values on the left arm.
	 *
	 * @return The 3D point-in-space representing the wrist's position.
	 */
	public Point3D wristPos(){
		return wrist;
	}
	
}
