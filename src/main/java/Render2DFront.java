import java.util.ArrayList;

import javafx.geometry.Point2D;

/**
 * Canvas responsible for rendering a 2D representation of the arm
 * Front view
 */
public class Render2DFront extends RenderCanvas {

	int xPos, yPos;
	int rectWidth = 160, rectHeight = 280;  //dimension for model's body
	int radius = 60;  //radius of model's head

	int firstTime = 0;  //initialisation process flag

	 ArrayList<Line> lines;  //array to hold the lines of the 3 previous points

	 /**
	  * This class is responsible from drawing the 2D front representation of
	  * the arm
	  * @param canvasWidth - Width of the canvas for rendering
	  * @param canvasHeight - Height of the canvas for rendering
	  */
	public Render2DFront(int canvasWidth, int canvasHeight) {
		super(canvasWidth, canvasHeight);

		xPos = width/2;
		yPos = height/2;

		//set rebase point to the drawing of the arm
		this.rebasePoint = new Point2D(canvasWidth/2 - 80, canvasHeight /2 - 130);
	}

	/**
	 * Performs initialisation steps for the PApplet.
	 */
	public void setup() {
		lines = new ArrayList<Line>();
		size(canvasWidth, canvasHeight);
	}

	/**
	 * Draws the full model in it's initial state
	 */
	public void drawModelWithArm() {
		drawModel();
		//left arm
		line(xPos - rectWidth/2, yPos - rectHeight/2 + 20,
				xPos - rectWidth/2 - 50, yPos + rectHeight/2 - 30);

	}

	/**
	 * Initialises the canvas into default state when it is selected for the
	 * first time.
	 */
	public void draw() {
		noLoop();
		/* only draw the full model when the canvas is selected */
		if (firstTime == 1) {
			drawModelWithArm();
		}
		firstTime++;
	}


	/**
	 * Draws the arm in the current position, as well as the previous 3 positions
	 */
	public void render(Point2D from, Point2D to) {

		/* Draws the model without the arm that we are trying to render */
		drawModel();

		lines.add( new Line((float)from.getX(), (float)from.getY(), 
				(float)to.getX(), (float)to.getY()));

		/* Remove the last trace as a new render point is added */
		if (lines.size() > 4) {
			lines.remove(0);
		}

		/* Draw the past 3 lines to give the illusion of a 3 dimensional trace */
		for ( int i = 0; i <= lines.size()-1; i++ ) {
			Line l = lines.get(i);

			switch(i) {
			case 0:
				stroke(84,83, 83);
				break;
			case 1:
				stroke(149, 149, 149);
				break;
			case 2:
				stroke(195,191,191);
				break;
			case 3:
				stroke(255,255,255);
				break;
			}

			l.draw( );
		}

	}

	/**
	 * Draws the anatomy of the model character on the screen, less the arm that
	 * we a trying to render
	 */  
	
	private void drawModel() {
		background(0);
		smooth();

		xPos = width/2;
		yPos = height/2;
		

		rectMode(CENTER);
		ellipseMode(RADIUS);

		noStroke();
		fill(247, 201, 170);

		rect(xPos, yPos, rectWidth, rectHeight);
		//head
		ellipse(xPos, yPos - 200, radius, radius);
		//neck
		rect(xPos, yPos - 150, 30, 30);
		fill(0);
		//eyes
		ellipse(xPos - 20, yPos - 215, 6, 6);
		ellipse(xPos + 20, yPos - 215, 6, 6);
		//mouth
		rect(xPos, yPos - 175, 20, 4);

		stroke(247, 201, 170);
		strokeWeight(45);

		//right arm
		line(xPos + rectWidth / 2, yPos - rectHeight / 2 + 20, xPos +
				rectWidth / 2 + 50, yPos + rectHeight / 2 - 30);
		
		//left leg
		line(xPos - rectWidth/2 + 15, yPos + rectHeight/2 , 
				xPos - rectWidth/2 - 50, yPos + rectHeight/2 + 220);

		//right  leg
		line(xPos + rectWidth / 2 - 15, yPos + rectHeight / 2,
				xPos + rectWidth / 2 + 50, yPos + rectHeight/2 + 220);

	}


	public void finalRender() {}
	
	/**
	 * This class is used to store the lines that have been drawn.
	 * @author Lisa
	 */
	class Line {
		float fromX;
		float fromY;
		float toX;
		float toY;
		
		Line(float fromX, float fromY, float toX, float toY) {
			this.fromX = fromX;
			this.fromY = fromY;
			this.toX = toX;
			this.toY = toY;
			
		}
		
		/* Draws a specified line */
		public void draw() {
			strokeWeight(45);
			line(fromX, fromY, toX, toY);
			redraw();
		}
	}

}