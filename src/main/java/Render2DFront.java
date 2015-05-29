
import javafx.geometry.Point2D;
import processing.core.PApplet;

/**
 * Canvas responsible for rendering a 2D representation of the arm
 * Front view
 */
public class Render2DFront extends RenderCanvas {

    public Render2DFront(int canvasSize) {
        super(canvasSize);
    }

    public void setup() {
        size(canvasSize, canvasSize);
        background(100);
    }

    public void draw() {
        noLoop();
    }

    public void render(Point2D from, Point2D to) {
        line((float) from.getX(), (float) from.getY(), (float) to.getX(),
                (float) to.getY());
        redraw();

    }

    public void finalRender() {
    }

}