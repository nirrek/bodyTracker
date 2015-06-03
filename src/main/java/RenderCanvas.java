import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import processing.core.PApplet;

/**
 * A wrapper for all Processing Applets
 */
public abstract class RenderCanvas extends PApplet {

	// The default length of the arm segment
	protected static final int ARM_LENGTH = 300;
	int canvasWidth, canvasHeight;
	//The point at which canvases use as the origin to start rendering
	protected Point2D rebasePoint;
	protected boolean init;

	/**
	 * This is the class the all the canvases extend - provides basic functions
	 * that are common to all the rendering canvases 
	 * @param canvasWidth - The width of the canvas
	 * @param canvasHeight - The height of the canvas
	 */
	public RenderCanvas(int canvasWidth, int canvasHeight) {
		this.canvasWidth = canvasWidth;
		this.canvasHeight = canvasHeight;
		this.rebasePoint = new Point2D(ARM_LENGTH, ARM_LENGTH);
	}

	/**
	 * Clears the current state of the canvas, and set's background to black
	 */
	public void clearCanvas() {
		this.init = true;
		background(0);
		redraw();
	}


	/**
	 * This is the method that encapsulates the arm rendering process. 
	 * @param arm - The arm object that is passed in to process for obtaining
	 * 			    coordinates that can be used for rendering
	 * @param side - front or side view, which uses the (x,z) plane or the (z,y)
	 */
	public void drawArm(Arm arm, String side) {
		Point3D shoulder = new Point3D(0, 0, 0);
		Point3D elbow = arm.elbowPos();

		// Project into the 2D plane for the particular side
		Point2D shoulder2D = projectTo2DPlane(shoulder, side);
		Point2D elbow2D = projectTo2DPlane(elbow, side);

		// Rebase to the canvas coordinate system (+ve reals only)
		shoulder2D = rebase(shoulder2D, rebasePoint);
		elbow2D = rebase(elbow2D, rebasePoint);

		// Call the render function.
		render(shoulder2D, elbow2D);

	}

	/**
	 * @param point - The point to rebase
	 * @param rebasePoint - The point in the canvas coordinate system that 
	 * 				        serves as a reference point
	 * @return Point2D point which is rebased to the canvas coordinate system
	 */
	private Point2D rebase(Point2D point, Point2D rebasePoint) {
		double x = Math.abs(rebasePoint.getX() - point.getX());
		double y = Math.abs(rebasePoint.getY() - point.getY());

		return new Point2D(x, y);
	}

	/**
	 * 
	 * @param coord - The 3d cordinate to project onto the 2 plane
	 * @param side - The 2 dimensional plane we wish to project to (front [z,y]) or
	 * 		         (side [x,y])
	 * @return A 2-dimensional point based on the string side.
	 */
	private Point2D projectTo2DPlane(Point3D coord, String side) {
		if (side.equals("side"))
			return new Point2D(coord.getX(), coord.getY());
		return new Point2D(coord.getZ(), coord.getY());
	}

	/**
	 * Draws the model with the arm - used for Render2DFront and Render2DSide 
	 * in the canvas initialisation process.
	 */
	public abstract void drawModelWithArm();

	/**
	 * Render function for artistic representation of the arm
	 * @param from - The point that is used to render from 
	 * @param to - The point that is used to render to 
	 */
	public abstract void render(Point2D from, Point2D to);
	 
	/**
	 * Produces a final render if required - used for Digital3DSketch
	 */
    public abstract void finalRender();
	
    /**
     * Allows the users to save the current state of the canvas.
     */
	public void save(String s) {
		saveFrame(s + "-###.jpg");
	}
}
