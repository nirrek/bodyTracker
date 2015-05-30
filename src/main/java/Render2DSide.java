
import java.util.Random;

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
        background(0);
    }

    public void draw() {
        noLoop();
    }

    public void render(Point2D from, Point2D to) {

    	  strokeWeight(.3f);

    	  Random rand = new Random();
    	  int randomNum = rand.nextInt((3 - 1) + 1) + 1;

    	  switch (randomNum) {
    	  case 1:
    		  stroke(random(0, 110),random(0, 200),random(0, 130));
    		  break;
    	  case 2:
    		  stroke(random(0, 190),random(0, 105),random(0, 200));
    		  break;
    	  default:
    		  stroke(random(0, 120),random(0, 110),random(0, 210));
    		  break;
    	  }


    	  line((float)from.getX(), (float)from.getY(), (float)to.getX(), 
    			  (float)to.getY());
    	  redraw();

    }

    public void finalRender() {}

}