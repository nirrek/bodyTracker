import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import processing.core.PApplet;

/**
 * A wrapper for all the Processing Applets (as enumerated in RenderCanvasEnum)
 * The applets that are used to render the movements extend this class.
 *
 * The class is made abstract because the implementation of some function
 * differs from one applet to the other.
 */
public abstract class RenderCanvas extends PApplet {

	// The default length of the arm segment
	protected static final int ARM_LENGTH = 300;

	// The size of the canvas
	int canvasWidth;
	int canvasHeight;

	// TODO Lisa
	protected Point2D rebasePoint;
	protected boolean init;

	/**
	 * The constructor set the canvasWidth and canvasHeight variables.
	 *
	 * @param canvasWidth: The width of the canvas
	 * @param canvasHeight: The height of the canvas
	 */
	public RenderCanvas(int canvasWidth, int canvasHeight) {
		this.canvasWidth = canvasWidth;
		this.canvasHeight = canvasHeight;
		this.rebasePoint = new Point2D(ARM_LENGTH, ARM_LENGTH);
	}

	// -------------------------------------------------------------------------
	//      ABSTRACT METHODS
	// -------------------------------------------------------------------------

	public abstract void drawModelWithArm();

	public abstract void render(Point2D from, Point2D to);

	public abstract void finalRender();

	// -------------------------------------------------------------------------
	//      METHODS USED BY ALL APPLETS
	// -------------------------------------------------------------------------

	/**
	 * Clear the canvas by resetting its background to black. This will erase
	 * any prior drawing
	 */
	public void clearCanvas() {
		this.init = true;
		background(0);
		redraw();
	}

	/**
	 * Save the canvas on the computer as a JPG.
	 *
	 * @param path: Contain an absolute path to the folder and the name of the canvas
	 *            eg: path = "Home/Desktop/2DSideView"
	 */
	public void save(String path) {
		// The 3 '#' added to the file name will be converted to a number
		// incrementing every time the same canvas is saved.
		saveFrame(path + "-###.jpg");
	}

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

	// -------------------------------------------------------------------------
	//      HELPER METHODS
	// -------------------------------------------------------------------------

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
}
