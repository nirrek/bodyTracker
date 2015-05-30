import javafx.geometry.Point2D;
import processing.core.PApplet;
import processing.core.PImage;
import processing.opengl.*;

public class Digital2DSketch extends RenderCanvas{

	boolean miden, smoothFade;
	boolean xar=true;

	// Sound Input Variables
	float amp1, freq1, amp2, freq2, ampout, freqout;
	float ElegxosAmp;

	SketchLine  line0, line1, line2;
	float x1, y1, x2, y2;
	PImage cur;

	int stoixeia = 30, lineAlpha = 50;

	//  Color Variables
	int colorL=255,strokeL, strokeValue = 20, strokeBackground = 5;
	int paintCount = 0, alphaSform;
	int r0,g0,b0,r1,g1,b1;

	// Physics Variables
	float[] x = new float[stoixeia];
	float[] y = new float[stoixeia];
	float[] epitaxinsiX = new float[stoixeia];
	float[] epitaxinsiY = new float[stoixeia];
	float[] elastikotita = new float[stoixeia];
	float[] aposbesi = new float[stoixeia];
	float[] deltaX = new float[stoixeia];
	float[] deltaY = new float[stoixeia];
	float fxMouse, fyMouse;

	public Digital2DSketch(int canvasSize) {
		super(canvasSize);
	}

	public void setup()  {
		frameRate(240);
		size(640, 640);


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

	public void draw() {
		  myLine();
		  noFill();
		  if (mousePressed == true)  { 
		    line0.calcPoints(mouseX, mouseY);
			line0.render(240,31,166, lineAlpha);
		    line1.calcPoints(mouseX, mouseY);
			line1.render(156,96,235, lineAlpha);
		    line2.calcPoints(mouseX, mouseY);
		    line2.render(159,209,252, lineAlpha);
		  } else {
		      line0.calcPoints(mouseX, mouseY);
				line0.render(240,31,166, 0);
		    line1.calcPoints(mouseX, mouseY);
			line1.render(156,96,235, 0);
		    line2.calcPoints(mouseX, mouseY);
		    line2.render(159,209,252, 0);
		   
		  }
		  if (smoothFade) {
		    fill(0,12);
		    rect(-10,-10,width,height);
		  }
	}

	public void render(Point2D from, Point2D to)  {
	
		float x = (float)to.getX() * 3;
		float y = (float)to.getY() * 3;
		
		noFill();
		myLine(x, y);
	

		line0.calcPoints(x, y);
		line0.render(240,31,166, lineAlpha);
		line1.calcPoints(x, y);
		line1.render(156,96,235, lineAlpha);
		line2.calcPoints((float)to.getX(), (float)to.getY());
		line2.render(159,209,252, lineAlpha);

		if (smoothFade) {
			fill(0,12);
			rect(-10,-10,width,height);
		}
	}
	
	void myLine(){
		for (int i=0; i<stoixeia; i++){
			x[i] = mouseX;// move worm
			y[i] = mouseY;
		}
		strokeL = strokeValue;
		noFill();
		drawline();

	}

	void myLine(float xCord, float yCord){

		for (int i=0; i<stoixeia; i++){
			x[i] = xCord;
			y[i] = yCord;

		}

		strokeL = strokeValue;

		noFill();
		drawline();
	}


	void drawline(){
		fxMouse = mouseX;
		fyMouse = mouseY;
		for (int i=0; i<5; i++){
			if (i==0){
				deltaX[i] = (fxMouse - x[i]);
				deltaY[i] = (fyMouse - y[i]);
				if (mousePressed && xar)  {  
				}

			}
			else {
				deltaX[i] = (x[i-1]-x[i]);
				deltaY[i] = (y[i-1]-y[i]);
			}
			deltaX[i] *= elastikotita[i];    // create elastikotita effect
			deltaY[i] *= elastikotita[i];
			epitaxinsiX[i] += deltaX[i];
			epitaxinsiY[i] += deltaY[i];
			x[i] += epitaxinsiX[i];// move it
			y[i] += epitaxinsiY[i];
			vertex(x[i],y[i]);
			epitaxinsiX[i] *= aposbesi[i];    // slow down elastikotita
			epitaxinsiY[i] *= aposbesi[i];
		}
		endShape();
	}

/*

	public void mouseReleased()  {	 
		line0.calcPointsStart(ARM_LENGTH/2, ARM_LENGTH/2);
	}

	public void mousePressed()  {
		line0.calcPointsStart(ARM_LENGTH/2, ARM_LENGTH/2);
		line1.calcPointsStart(ARM_LENGTH/2, ARM_LENGTH/2);
		line2.calcPointsStart(ARM_LENGTH/2, ARM_LENGTH/2); 
	} */




	public void keyPressed(){
		if (key == 'z') {
		}
		if (key == 'b') {
			background(0);
		} 
		if (key == 's') {
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



	@Override
	public void finalRender() {
		// TODO Auto-generated method stub

	}

}
