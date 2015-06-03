import javafx.geometry.Point2D;
import processing.core.PImage;

/**
 * @author sketch applet by Aris Bezas - modified by Lisa
 * from www.openprocessing.org
 */
public class Digital2DSketch extends RenderCanvas{

	boolean smoothFade, render;

	SketchLine  line0, line1, line2;
	float x1, y1, x2, y2;
	PImage cur;
	float xCo, yCo;

	int stoixeia = 30, lineAlpha = 50, count;

	// Physics Variables
	float[] x = new float[stoixeia];
	float[] y = new float[stoixeia];
	float[] epitaxinsiX = new float[stoixeia];
	float[] epitaxinsiY = new float[stoixeia];
	float[] elastikotita = new float[stoixeia];
	float[] aposbesi = new float[stoixeia];
	float[] deltaX = new float[stoixeia];
	float[] deltaY = new float[stoixeia];

	/**
	 * This class draws a 2 dimensional digital sketch based on the position
	 * of the arm.
	  * @param canvasWidth - Width of the canvas for rendering
	  * @param canvasHeight - Height of the canvas for rendering
	 */
	public Digital2DSketch(int canvasWidth, int canvasHeight) {
		super(canvasWidth, canvasHeight);
		this.rebasePoint = new Point2D(canvasWidth/2, canvasHeight /2);
		render = false;
	}

	/**
	 * This function performs initialisation steps. It is the first thing that
	 * is called when a Digital2DSketch object is made. 
	 */
	public void setup()  {
		
		frameRate(240);
		size(canvasWidth, canvasHeight);
		count = 3;


		line0 = new SketchLine(30);
		line1 = new SketchLine(stoixeia);
		line2 = new SketchLine(stoixeia + 1);

		line0.calcType( 0.2f, 0.65f );
		line1.calcType( 0.1f, 0.65f );
		line2.calcType( 0.3f, 0.65f );

		background(0);
		noFill();
		smooth();

		for (int i=0; i<stoixeia; i++){
			elastikotita[i] = (float) (0.2*(.07*(i+1)));
			aposbesi[i] = (float) (0.55-(0.02*i));
		}
		
		
	}

	@Override
	public void clearCanvas() {
		this.init = true;
		this.rebasePoint = new Point2D(canvasWidth/2, canvasHeight /4);
		background(0);
		render = false;
		redraw();
	}


	/**
	 * This method is called after setup() and loops infinitely for the 
	 * duration of the program.
	 */
	public void draw() {
		noFill();
		
		if (render == false ) {
			/* Configure the starting point for the sketch */
			  line0.calcPointsStart(canvasWidth/2, canvasHeight/4);
			  line1.calcPointsStart(canvasWidth/2, canvasHeight/4);
			  line2.calcPointsStart(canvasWidth/2, canvasHeight/4); 
		}

		if (render == true)  { 
			line0.calcPoints(xCo, yCo);
			line0.render(240,31,166, lineAlpha);
			line1.calcPoints(xCo, yCo);
			line1.render(156,96,235, lineAlpha);
			line2.calcPoints(xCo, yCo);
			line2.render(159,209,252, lineAlpha);
		} else {
			line0.calcPoints(xCo, yCo);
			line0.render(240,31,166, 0);
			line1.calcPoints(xCo, yCo);
			line1.render(156,96,235, 0);
			line2.calcPoints(xCo, yCo);
			line2.render(159,209,252, 0);
		}

		/* If smooth fade is selected, put a black rectangle with low opacity
		 * onto the canvas. */
		if (smoothFade) {
			fill(0,12);
			rect(0,0,canvasWidth,canvasHeight);
		}
	}

	/* This method changes the coordinates of the line which is to be rendered
	 * in the draw function */
	public void render(Point2D from, Point2D to)  {	
		render = true;
		xCo = (float)to.getX() * 1.7f - 50;
		yCo = (float)to.getY() * 1.2f - 300 ; 
		
	}

	/**
	 * When the f key is pressed on the keyboard, the fade effect option is 
	 * toggled.
	 */
	public void keyPressed(){
		if (key == 'f') {
			smoothFade = !smoothFade;
		} 
	}


	class  SketchLine  {
		int stoixeia = 1000, colorR, colorG, colorB, lineAlpha = 25;
		float elast, aposv;
		float[] x = new float[stoixeia];
		float[] y = new float[stoixeia];
		float[] epitaxinsiX = new float[stoixeia];
		float[] epitaxinsiY = new float[stoixeia];
		float[] elastikotita = new float[stoixeia];
		float[] aposvesi = new float[stoixeia];
		float[] deltaX = new float[stoixeia];
		float[] deltaY = new float[stoixeia];

		float pointX, pointY;


		SketchLine(int stoixeiaVar)  {
			stoixeia = stoixeiaVar;
		}

		void calcType(float elastikotitaVar, float aposvesiVar)  {
			elast = elastikotitaVar;
			aposv = aposvesiVar;
			for (int i=0; i < stoixeia; i++){
				elastikotita[i] = (float) (elast*(.07*(i+1)));// 0.05  kai 0.005
				aposvesi[i] = (float) (aposv-(0.02*i));
			}
		}

		void calcPoints(float pointXVar, float pointYVar)  {
			pointX = pointXVar;
			pointY = pointYVar;

			for (int i=0; i<stoixeia; i++){
				if (i==0){
					deltaX[i] = (pointX - x[i]);
					deltaY[i] = (pointY - y[i]);

				} 
				else  {
					deltaX[i] = (x[i-1]-x[i]);
					deltaY[i] = (y[i-1]-y[i]);
				}
				deltaX[i] *= elastikotita[i];    // create elastikotita effect
				deltaY[i] *= elastikotita[i];
				epitaxinsiX[i] += deltaX[i];
				epitaxinsiY[i] += deltaY[i];
				x[i] += epitaxinsiX[i];// move it
				y[i] += epitaxinsiY[i];
				epitaxinsiX[i] *= aposvesi[i];    // slow down elastikotita
				epitaxinsiY[i] *= aposvesi[i];
			}
		}
		void calcPointsStart(float pointXVar, float pointYVar)  {
			pointX = pointXVar;
			pointY = pointYVar;
			for (int i=0; i<stoixeia; i++){
				x[i] = ARM_LENGTH/2;
				y[i] = ARM_LENGTH/2;
			}
		}

		
		void render(int colorRVar, int colorGVar, int colorBVar, int lineAlphaVar)  {
			colorR = colorRVar;
			colorG = colorGVar;
			colorB = colorBVar;   
			lineAlpha = lineAlphaVar;   
			noFill();
			stroke(colorR, colorG, colorB, lineAlpha);
			beginShape();
			for (int i = 0; i < stoixeia; i++)  {
				curveVertex(x[i], y[i]);
			}
			endShape();
		}

	}

	public void drawModelWithArm() {}
	public void finalRender() {}

}
