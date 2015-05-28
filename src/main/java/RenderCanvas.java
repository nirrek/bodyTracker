import processing.core.PApplet;

/**
 * A wrapper for all Processing Applets
 */
public class RenderCanvas extends PApplet {

    public void clearCanvas() {
        background(100);
        redraw();
    }
}
