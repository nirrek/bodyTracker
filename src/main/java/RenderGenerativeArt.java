import processing.core.PApplet;
import processing.core.PVector;

import java.util.*;

import javafx.geometry.Point2D;

/**
 * @author Generative brushes applet by Jason Barles - modified by Lisa
 * from www.openprocessing.org
 */
public class RenderGenerativeArt extends RenderCanvas{


	public RenderGenerativeArt(int canvasWidth, int canvasHeight) {
		super(canvasWidth, canvasHeight);
	}

	int previousTool = 0;
	int currentTool = 1; // Set the default tool to be the first pattern
	List<PVector> history;   // Define the history for pattern3
	float xCo, yCo;       //the x,y coordinates to render
	float prevX, prevY;   // the previous x,y coordinates
	int count;
	boolean smoothFade, render = false;

	/**
	 * This function performs initialisation steps. It is the first thing that
	 * is called when a RenderGenerativeArt object is made. 
	 */
	public void setup() {
		size(canvasWidth, canvasHeight);
		background(0);
		smooth();
		resetCoordinates();
	}

	/**
	 * This method is called after setup() and loops infinitely for the 
	 * duration of the program.
	 */
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
			}

		}

		/* If smooth fade is selected, put a black rectangle with low opacity
		 * onto the canvas. */
		if (smoothFade) {
			fill(0,12);
			rect(0,0,canvasWidth,canvasHeight);
		}
	}

	/**
	 * Main render function for this canvas. The brush is continuously changed 
	 * between the 4 different ones available every 20 frames.
	 */
	public void render(Point2D from, Point2D to) {
		render = true;
		prevX = xCo;
		prevY = yCo;
		xCo = abs(map((float) to.getX(), 0, 600, 0, canvasWidth));
		yCo = abs(map((float) to.getY(), 250, 600, 0, canvasHeight) *  0.7f) ;

		count ++;

		/* change brushes for every 20 frames */
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


	/* Drawing tools */


	/**
	 * Pattern one draws a spray of circles.
	 */
	public void pattern1(float x, float y, int r, int num, int fromC, int toC) { 
		int interA = lerpColor(fromC, toC, .12f); // get the inbetween colour
		noStroke();
		fill(fromC, 80);
		ellipse(x, y, r, r);
		stroke(fromC);
		if (num > 0) {
			float newY = (float) (y + sin(random(0, TWO_PI)) * 12.0);   
			pattern1(x+(num/3), newY, (int)(random(r/2, r+(num/5))), num-1, interA, toC);
		}
	}

	/*
	 * Pattern2 draws a rainbow web. Code inspired by Mr Doob's project harmony.
	 * http://www.mrdoob.com/projects/harmony/
	 */
	public void pattern2() {
		int extra = 3;

		/* Randomise the colours during each frame */
		stroke(random(0,255), random(0,255), random(0,255));
		strokeWeight(0.2f);
		line(xCo, yCo, prevX, prevY);

		for(int i = 0; i < history.size(); i++){
			PVector p = (PVector) history.get(i);

			/* Draw a line from the current mouse point to
	       the historical point if the distance is less
	       than 50 */
			if(dist(xCo, yCo, p.x, p.y) < 50){
				line(xCo, yCo, p.x + extra, p.y + extra);
			}    
		}

		/* Add the current point to the history */
		history.add(new PVector(xCo, yCo));
		history.add(new PVector(width - xCo, yCo));
	}

	/**
	 * Draws circles that seem to fade towards the middle 
	 */
	public void pattern3(){
		noStroke();
		fill(random(0,255), random(0,255), random(0,255),10);
		/* alter the width size */
		float widthDistance = abs(width/2 - xCo) * 0.4f ;
		ellipse(xCo, yCo, widthDistance, widthDistance);
	}

	/*
	 * Code draws line pattern - Code inspired by Mr Doob's project harmony.
	 * http://www.mrdoob.com/projects/harmony/
	 */
	public void pattern4(){
		/* Randomise the colours during each frame */
		stroke(255);
		line(xCo, yCo, prevX, prevY);

		for(int i = 0; i < history.size(); i++){
			PVector p = (PVector) history.get(i);
			float d = dist(xCo, yCo, p.x, p.y);
			/* Adjust the stroke weight according to the distance */
			strokeWeight(1/d);

			/* Draw a line from the current point to
	       the historical point if the distance is less
	       than 25 */
			if(d < 25){
				if(random(10) < 5) /* Skip some lines randomly */
					line(xCo, yCo, p.x + 2, p.y + 2);
			}
		}

		/* Add the current point to the history */
		history.add(new PVector(xCo, yCo));
		strokeWeight(0.2f);
	}

	/**
	 * Draw invisible line - done to keep track of the movements, but the user
	 * has 'paused the sketch' to reposition arm.
	 */
	public void pattern5() {
		stroke(255,255,255,0);
		line(xCo, yCo, prevX, prevY);
	}

	/** 
	 * Method that toggles fading option when f is pressed.
	 * Also toggles whether the current tool should be an invisible sketch
	 * to emulate 'releasing the mouse' when drawing a picture.
	 */
	public void keyPressed(){
		if (key == 'f') {
			smoothFade = !smoothFade;
		}

		if (key == 's') {
			if (currentTool == 5 ) {
				currentTool = previousTool;
			} else {
				previousTool = currentTool;
				currentTool = 5;
			}
		}
	}

	/**
	 * This method resets the drawing coordinates back to origin
	 */
	public void resetCoordinates() {
		history  = new ArrayList<PVector> ();  
		xCo = width/2;
		yCo = height/2;
		prevX = width/2;
		prevY = height/2;
	}

	@Override
	public void clearCanvas() {
		this.init = true;
		resetCoordinates();
		background(0);
		redraw();
	}

	public void finalRender() {	}
	public void drawModelWithArm() {}



}
