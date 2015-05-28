/**
 * Canvas responsible for rendering a 2D representation of the arm
 * Side view
 *
 * @author TODO Lisa & Romain
 */
public class Render2DSide extends RenderCanvas {

    public Render2DSide() {
        //
    }

    public void setup() {
        size(400, 400);
        background(100);
    }

    public void draw() {
        //noLoop();
        //background(val * 50);
        //fill(100 + val * 2);
        ellipseMode(CENTER);
        ellipse(mouseX, mouseY, 80, 80);
        //val = val + 1;
    }

    // -------------------------------------------------------- //
// -------- OLD CODE KERRIN WROTE TO RENDER IN 2D---------- //
// -------------------------------------------------------- //

//import javafx.geometry.Point2D;
//import javafx.geometry.Point3D;
//import javafx.scene.Node;
//import javafx.scene.canvas.Canvas;
//import javafx.scene.canvas.GraphicsContext;
//import javafx.scene.paint.Color;
//import javafx.scene.shape.StrokeLineCap;
//
//
//// TODO decide on an interface for custom views, or see if there is an
//// appropriate primitive in JavaFX to extend.
///**
// * Canvas responsible for rendering a 2D representation of the arm
// * @author Kerrin
// */
//public class RenderCanvas {
//    // The default length of the arm segment
//    private static final int ARM_LENGTH = 300;
//
//    // The underlying canvas node
//    private Canvas canvas;
//
//    // The canvas context
//    private GraphicsContext ctx;
//
//    // Width and height dimensions for the canvas in pixels
//    private int width;
//    private int height;
//
//    // Brush parameters
//    private double brushSize = 3.0;
//    private Color brushColor = Color.rgb(67, 188, 135, 0.6);
//
//    // The point in the original projected 2d plane that will become
//    // (0, 0) in the canvas coordinate system.
//    private Point2D rebasePoint = new Point2D(ARM_LENGTH, ARM_LENGTH);
//
//    public RenderCanvas() {
//        // TODO, decide how we wish to expose the functionality for setting
//        // the canvas dimensions
//        width = 2 * ARM_LENGTH;
//        height = 2 * ARM_LENGTH;
//        this.canvas = new Canvas(width, height);
//        ctx = this.canvas.getGraphicsContext2D();
//
//        // Set default brush parameters
//        ctx.setStroke(brushColor);
//        ctx.setLineWidth(brushSize);
//    }
//
//    /**
//     * Expose the underlying canvas scene graph node.
//     * @return The underlying canvas node.
//     */
//    public Node getNode() {
//        return canvas;
//    }
//
//    /**
//     * Draws the specified arm on the canvas from a particular side.
//     * @param arm The arm to draw.
//     * @param side The side from which to draw the arm. "front" || "side"
//     */
//    public void drawArm(Arm arm, String side) {
//        // TODO, arm should be exposing the shoulder position.
//        Point3D shoulder = new Point3D(0, 0, 0);
//        Point3D elbow = arm.elbowPos();
//
//        // Project into the 2D plane for the particular side
//        Point2D shoulder2D = projectTo2DPlane(shoulder, side);
//        Point2D elbow2D = projectTo2DPlane(elbow, side);
//
//        // Rebase to the canvas coordinate system (+ve reals only)
//        shoulder2D = rebase(shoulder2D, rebasePoint);
//        elbow2D = rebase(elbow2D, rebasePoint);
//
//        drawLine(shoulder2D, elbow2D, ctx);
//    }
//
//    /**
//     * Clears the canvas of existing drawings.
//     */
//    public void clearCanvas() {
//        ctx.clearRect(0, 0, width, height);
//    }
//
//    public void setBrush(Color color, double size) {
//        setBrushColor(color);
//        setBrushSize(size);
//    }
//
//    public void setBrushColor(Color color) {
//        brushColor = color;
//        ctx.setStroke(color);
//    }
//
//    public void setBrushSize(double size) {
//        brushSize = size;
//        ctx.setLineWidth(size);
//    }
//
//    // between 0.0 and 1.0
//    public void setBrushOpacity(double opacity) {
//        // JavaFX uses a whack version of RGB by default. Uses doubles in
//        // range 0.0 -> 1.0, instead of ints from 0 -> 255
//        double r = brushColor.getRed();
//        double g = brushColor.getGreen();
//        double b = brushColor.getBlue();
//        setBrushColor(Color.color(r, g, b, opacity));
//    }
//
//    /**
//     * Draws a line between the two specified 2D points on the canvas.
//     * @param from The start coordinate for the line.
//     * @param to The end coordinate for the line.
//     * @param ctx The graphics of the canvas on which to draw the line.
//     */
//    private void drawLine(Point2D from, Point2D to, GraphicsContext ctx) {
//        // TODO decide how we want to be configuring stroke/line width
//        // Most probably the determiner of this will be how we want to be doing
//        // the artistic generative art.
//        ctx.setStroke(Color.rgb(67, 188, 135, 0.6));
//        ctx.setLineWidth(3);
//        ctx.setLineCap(StrokeLineCap.ROUND);
//
//        ctx.strokeLine(from.getX(), from.getY(), to.getX(), to.getY());
//    }
//
//    /**
//     * Rebases a 2D point with respect to another 2D point. What this means is
//     * that the 0,0 coordinate for the 2D plane is changed to be that of the
//     * rebasePoint. This is important for converting between a coordinate system
//     * that uses the full cartesian plane (cross-product of the Reals) and the
//     * subset of the cartesian plane used in 2D renderers (cross-product of the
//     * positive Reals)
//     * @param point The point to rebase
//     * @param rebasePoint The point (in terms of the original coordinate system)
//     *                    that should be treated as (0, 0)
//     * @return The rebased point.
//     */
//    private Point2D rebase(Point2D point, Point2D rebasePoint) {
//        double x = Math.abs(rebasePoint.getX() - point.getX());
//        double y = Math.abs(rebasePoint.getY() - point.getY());
//
//        return new Point2D(x, y);
//    }
//
//    /**
//     * Takes a 3D coordinate and projects it into the 2D plane. Can project
//     * either the "front" side, or the "side" side.
//     * @param coord The 3d coordinate to project into the 2D plane.
//     * @param side The side for which you want the projection. Either
//     *             "front" or "side".
//     * @return
//     */
//    private Point2D projectTo2DPlane(Point3D coord, String side) {
//        if (side.equals("side"))
//            return new Point2D(coord.getX(), coord.getY());
//        return new Point2D(coord.getZ(), coord.getY());
//    }
//}
}
