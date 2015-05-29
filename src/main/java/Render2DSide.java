
import javafx.geometry.Point2D;

/**
 * Canvas responsible for rendering a 2D representation of the arm
 * Side view
 */
public class Render2DSide extends RenderCanvas {

    public Render2DSide(int canvasSize) {
        super(canvasSize);
    }

    public void setup() {
        size(canvasSize, canvasSize);
        background(200);
    }

    public void draw() {
        noLoop();
    }

    public void render(Point2D from, Point2D to) {
        line((float) from.getX(), (float) from.getY(), (float) to.getX(),
                (float) to.getY());
        redraw();

    }

    public void finalRender() {}

}