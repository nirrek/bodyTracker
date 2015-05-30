import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import processing.core.PApplet;

/**
 * A wrapper for all Processing Applets
 */
public abstract class RenderCanvas extends PApplet {

	int canvasSize;

	public RenderCanvas(int canvasSize) {
		this.canvasSize = canvasSize;
	}

	public void clearCanvas() {
		background(83);
		redraw();
	}


	// The default length of the arm segment
	protected static final int ARM_LENGTH = 300;

	private Point2D rebasePoint = new Point2D(ARM_LENGTH, ARM_LENGTH);

	public Point2D drawArm(Arm arm, String side) {
		Point3D shoulder = new Point3D(0, 0, 0);
		Point3D elbow = arm.elbowPos();

		// Project into the 2D plane for the particular side
		Point2D shoulder2D = projectTo2DPlane(shoulder, side);
		Point2D elbow2D = projectTo2DPlane(elbow, side);

		// Rebase to the canvas coordinate system (+ve reals only)
		shoulder2D = rebase(shoulder2D, rebasePoint);
		elbow2D = rebase(elbow2D, rebasePoint);

		render(shoulder2D, elbow2D);
		
		return elbow2D;

	}

	private Point2D rebase(Point2D point, Point2D rebasePoint) {
		double x = Math.abs(rebasePoint.getX() - point.getX());
		double y = Math.abs(rebasePoint.getY() - point.getY());

		return new Point2D(x, y);
	}

	private Point2D projectTo2DPlane(Point3D coord, String side) {
		if (side.equals("side"))
			return new Point2D(coord.getX(), coord.getY());
		return new Point2D(coord.getZ(), coord.getY());
	}
	
	public abstract void render(Point2D from, Point2D to);
	 
    public abstract void finalRender();
	
	public void save(String s) {
		saveFrame(s + "-###.jpg");
	}
}
