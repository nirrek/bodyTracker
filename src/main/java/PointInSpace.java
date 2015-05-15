
public class PointInSpace {
	
	private double X;
	private double Y;
	private double Z;
	
	public PointInSpace(double X, double Y, double Z){
		this.X = X;
		this.Y = Y;
		this.Z = Z;
	}
	
	/**
	 * The distance in front (positive) or behind (negative) the shoulder from its owner's viewpoint
	 * @return the double X
	 */
	public double X(){
		return X;
	}
	
	/**
	 * The distance above (positive) or below (negative) the shoulder from its owner's viewpoint
	 * @return the double Y
	 */
	public double Y(){
		return Y;
	}
	
	/**
	 * The distance to the right (positive) or left (negative) the shoulder from its owner's viewpoint
	 * @return the double Z
	 */
	public double Z(){
		return Z;
	}
}
