import processing.core.PApplet;

/**
 * The application's GUI.
 * Connects to the Arduino via serial USB. Fetch/Stream data from the Arduino,
 * and display it in an artistic manner after conversion by the Modeler.
 */
public class RendeR extends PApplet {

    private static int val = 0;

    public RendeR() {
        //
    }

    public void setup() {
        size(400, 400);
        background(0);
    }

    public void draw() {
        //noLoop();
        background(val * 50);
        fill(100 + val * 2);
        ellipseMode(CENTER);
        ellipse(mouseX, mouseY, 80, 80);
        val = val + 1;
    }


    @Override
    public void mouseClicked() {
        System.out.println("click!");
    }

    public void stopLoop() {
        System.out.println("Stop loop");
        noLoop();
    }

    public void startLoop() {
        System.out.println("Restart loop");
        loop();
    }

}