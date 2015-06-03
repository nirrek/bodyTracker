import processing.core.PApplet;
import processing.core.PVector;

import java.util.*;

import javafx.geometry.Point2D;

public class RenderGenerativeArt extends RenderCanvas{
	

	public RenderGenerativeArt(int canvasWidth, int canvasHeight) {
		super(canvasWidth, canvasHeight);
	}

	int currentTool = 1; // Set the default tool to be the first pattern
	List<PVector> history;   // Define the history for pattern3
	boolean render = false;
	float xCo;
	float yCo;
	float prevX;
	float prevY;
	int count;
    boolean smoothFade;
    int previousTool = 0;
	 
	 
	public void setup() {
	  size(canvasWidth, canvasHeight);
	  background(0);
	  smooth();
	  resetCoordinates();
		 
	}

	public void drawModelWithArm() {}

	public void draw() {
	   
	  if (render) {
	    switch(currentTool) {     
	    case 1:     
	      pattern1(xCo, yCo, 5, 18, color(204, 102, 0), color(0, 102, 153));
	      break;
	    case 2:     
	      pattern2();
	      break;     
	    case 3:
	      pattern3();
	      break;   
	    case 4:
	      pattern4();
	      break; 
	    case 5:
	      pattern5();
	      break;
			case 6:
				pattern6();
				break;
	    }

	  }

        if (smoothFade) {
            fill(0,12);
            rect(0,0,canvasWidth,canvasHeight);
        }
	}

	@Override
	public void render(Point2D from, Point2D to) {
		render = true;
		prevX = xCo;
		prevY = yCo;
		xCo = map((float) to.getX(), 0, 600, 0, canvasWidth);
		yCo = map((float) to.getY(), 250, 600, 0, canvasHeight) *  0.7f ;
		
		count ++;
		
		 if (count % 20 == 0) {
			 count = 1;
			 switch(currentTool) {
			 case 5:
				 currentTool = 1;
				 break;
			 case 2:
				 history  = new ArrayList<PVector> ();
		    default:
		    	currentTool ++;
		    	break;
			 }	
		 }
	}

	 
	/*
	 Drawing tools
	 */
	// A recursive pattern that draws a spray of circles mirrored across the
	// x-axis.
	public void pattern1(float x, float y, int r, int num, int fromC, int toC) { 
	  int interA = lerpColor(fromC, toC, .12f); // get the inbetween colour
	  noStroke();
	  fill(fromC, 80);
	  ellipse(x, y, r, r);
	  stroke(fromC);
	//  ellipse(width-x, y, r, r);  // draw the mirror of the ellipse
	  if (num > 0) {
	    float newY = (float) (y + sin(random(0, TWO_PI)) * 12.0);   
	    // recursive call
	    pattern1(x+(num/3), newY, (int)(random(r/2, r+(num/5))), num-1, interA, toC);
	  }
	}
	 
	// pattern2 draws a rainbow web. Code inspired by Mr Doob's project harmony.
	// http://www.mrdoob.com/projects/harmony/
	public void pattern2() {
	  int extra = 3;
	  // Randomise the colours during each frame
	  stroke(random(0,255), random(0,255), random(0,255));
	  strokeWeight(0.2f);
	  line(xCo, yCo, prevX, prevY);
//	  line(width-xCo, yCo, width-xCo, yCo); // Mirror
	 
	  for(int i = 0; i < history.size(); i++){
	    PVector p = (PVector) history.get(i);
	     
	    // Draw a line from the current mouse point to
	    // the historical point if the distance is less
	    // than 50
	    if(dist(xCo, yCo, p.x, p.y) < 50){
	      line(xCo, yCo, p.x + extra, p.y + extra);
	    }
	    // repeat for the mirror line
//	    if(dist(width-xCo, yCo, p.x, p.y) < 50){
//	      line(width-xCo, yCo, p.x + extra, p.y + extra);
//	    }     
	  }
	   
	  // Add the current point to the history
	  history.add(new PVector(xCo, yCo));
	  history.add(new PVector(width - xCo, yCo));
	}

    public void keyPressed(){
        if (key == 'f') {
            smoothFade = !smoothFade;
        }

        if (key == 's') {

            System.out.println(currentTool);
            System.out.println(previousTool);

            if (currentTool == 6 ) {
                currentTool = previousTool;
            } else {
                previousTool = currentTool;
                currentTool = 6;
            }
        }
    }


    // pattern3 draws hundreds and thousands food dressing
	public void pattern3() {



		float red = map(xCo, 0, canvasWidth, 0, 255);
		float blue = map(yCo, 0, canvasWidth, 0, 255);
		float green = dist(xCo, yCo, canvasWidth/2, canvasHeight/2);

		float lineWidth = random(3, 8);

		stroke(red, green, blue, 255);
		strokeWeight(lineWidth);
		line(prevX, prevY, xCo, yCo);

	}
	 
	// pattern4 draws circles that seem to fade in towards the middle
	public void pattern4(){
	  noStroke();
	  fill(random(0,255), random(0,255), random(0,255),10);
	  // alter the width size
	  float widthDistance = abs(width/2 - xCo) * 0.4f ;
	  ellipse(xCo, yCo, widthDistance, widthDistance);
	}
	 
	// Code inspired by Mr Doob's project harmony.
	// http://www.mrdoob.com/projects/harmony/
	public void pattern5(){
	  // Randomise the colours during each frame
	  stroke(255);
	  line(xCo, yCo, prevX, prevY);
	 
	  for(int i = 0; i < history.size(); i++){
	    PVector p = (PVector) history.get(i);
	    float d = dist(xCo, yCo, p.x, p.y);
	    // Adjust the stroke weight according to the distance
	    strokeWeight(1/d);
	     
	    // Draw a line from the current mouse point to
	    // the historical point if the distance is less
	    // than 25
	    if(d < 25){
	     if(random(10) < 5) // Skip some lines randomly
	        line(xCo, yCo, p.x + 2, p.y + 2);
	    }
	  }
	   
	  // Add the current point to the history
	  history.add(new PVector(xCo, yCo));
	  strokeWeight(0.2f);
	}

    //draw invisible line.
    public void pattern6() {
        stroke(255,255,255,0);
        line(xCo, yCo, prevX, prevY);
    }
	 

	@Override
	public void finalRender() {
		// TODO Auto-generated method stub
		
	}
	
	public void resetCoordinates() {
		history  = new ArrayList<PVector> ();  
		xCo = 0;
		yCo = 0;
		prevX = 0;
		prevY = 0;
	}
	
	@Override
	public void clearCanvas() {
		this.init = true;
		resetCoordinates();
		background(0);
		redraw();
	}



}
