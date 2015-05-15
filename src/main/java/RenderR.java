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

    // PROCESSING
    Serial myPort;  // Create object from Serial class
    String val;     // Data received from the serial port

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

    private void processingConnect() throws Exception {
        String portName = Serial.list()[0]; //change the 0 to a 1 or 2 etc. to match your port
        myPort = new Serial(this, portName, 9600);
    }
}
