import processing.core.PApplet;
import processing.serial.Serial;

/**
 * The application's GUI.
 * Connects to the Arduino via serial USB. Fetch/Stream data from the Arduino,
 * and display it in an artistic manner after conversion by the Modeler.
 */
public class RenderR extends PApplet {
    Serial port;
    int inputPin = 0;
    int outputPin = 2;

    public static void main(String args[]) {
        PApplet.main(new String[] { "--present", "RenderR" });
      }
    
    public void setup() {
        size(200,200);
        background(0);
      }

      public void draw() {
        stroke(255);
        if (mousePressed) {
          line(mouseX,mouseY,pmouseX,pmouseY);
        }
      }

}
