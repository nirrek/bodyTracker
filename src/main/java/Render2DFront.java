import java.util.ArrayList;

import javafx.geometry.Point2D;

/**
 * Canvas responsible for rendering a 2D representation of the arm
 * Front view
 */
public class Render2DFront extends RenderCanvas {

	int xPos, yPos;
	int rectWidth = 160;
	int rectHeight = 280;
	int radius = 60;

	int firstTime = 0;

	 ArrayList<Line> lines;


	public Render2DFront(int canvasWidth, int canvasHeight) {
		super(canvasWidth, canvasHeight);

		xPos = width/2;
		yPos = height/2;

		this.rebasePoint = new Point2D(canvasWidth/2 + 80, canvasHeight /2 - 150);
		//this.rebasePoint = new Point2D(xPos + rectWidth / 2, yPos - rectHeight / 2 + 20);

	}

	public void setup() {
		
		lines = new ArrayList<Line>();
		
		size(canvasWidth, canvasHeight);
	}

	public void drawModelWithArm() {
		drawModel();
		//right arm
		line(xPos + rectWidth / 2, yPos - rectHeight / 2 + 20, xPos +
				rectWidth / 2 + 50, yPos + rectHeight / 2 - 30);

	}

	public void draw() {
		noLoop();
		if (firstTime == 1) {
			drawModelWithArm();
		}
		firstTime++;
	}


	public void render(Point2D from, Point2D to) {

		drawModel();

		lines.add( new Line((float)from.getX(), (float)from.getY(), 
				(float)to.getX(), (float)to.getY()));

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
		
		//left arm
		line(xPos - rectWidth/2, yPos - rectHeight/2 + 20, 
				xPos - rectWidth/2 - 50, yPos + rectHeight/2 - 30);
		
		//left leg
		line(xPos - rectWidth/2 + 15, yPos + rectHeight/2 , 
				xPos - rectWidth/2 - 50, yPos + rectHeight/2 + 220);

		//right  leg
		line(xPos + rectWidth / 2 - 15, yPos + rectHeight / 2,
				xPos + rectWidth / 2 + 50, yPos + rectHeight/2 + 220);

	}


	public void finalRender() {}
	
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
		
		public void draw() {
			strokeWeight(45);
			line(fromX, fromY, toX, toY);
			redraw();
		}
	}
	
}