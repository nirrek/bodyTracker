import javafx.geometry.Point2D;
import processing.core.PImage;



public class Digital3DSketch extends RenderCanvas {

	public Digital3DSketch(int canvasWidth, int canvasHeight) {
		super(canvasWidth, canvasHeight);
	}
	/**
	 * Peter de Jong attractor applet by Thor Fr&#248;lich.<br>
	 * <br>
	 * Hold mouse button and move mouse to adjust variables.<br>
	 * Release mouse button to render attractor in high quality.
	 */
	 
	deJongAttractor dj;
	boolean stop;
	int stepCounter;
	 
	public void setup() {
	  size(canvasWidth, canvasHeight);
	  noFill();
	  smooth();
	  colorMode(HSB, 255);
	  dj = new deJongAttractor();
	  dj.reparam(0,0);
	}
	 
	public void draw() {
	  if (!stop) {
	    stepCounter++;
	    if (stepCounter > 127) {
	      stop = true;
	      return;
	    }
	    dj.incrementalupdate();
	  }
	  image(dj.pi, 0, 0, width, height);
	}
	
	/* While still reading samples */
	public void render(Point2D from, Point2D to) {
	
	  noLoop();
	  stop = true;
	  dj.reparam((float) to.getX(), (float) to.getY());
	  redraw();
	}

	/* Finished reading samples */
	public void finalRender() {
	  loop();
	  stop = false;
	  stepCounter = 0;
	  dj.updateloop();
	}


	class deJongAttractor {
	  PImage pi;
	  float pa, pb, pc, pd, newx, newy, oldx, oldy, logmaxd;
	  int N = width;
	  int maxdense = 0;
	  int[][] density = new int[N][N];
	  float[][] previousx = new float[N][N];
	 
	  void construct(float x, float y) {
	    //Produces the four variables to pass to the attractor
	    float sensitivity = 0.017f;
	    pa = map(x, 0, 600, -1, 1) * sensitivity;
	    pb = map(y, 0, 600, -1, 1) * sensitivity;
	    pc = map(x, 0, 600, 1, -1) * sensitivity;
	    pd = map(y, 0, 600, 1, -1) * sensitivity;
	    oldx = width/2;
	    oldy = height/2;
	  }
	 
	  void populate(int s, boolean c) {
	    //Populate array with density info with s number of samples
	    int samples = s;
	    boolean clear = c;
	    if (clear) {
	      for (int i = 0; i < N; i++) {
	        for (int j = 0; j < N; j++) {
	          density[i][j] = 0;
	          previousx[i][j] = 0;
	        }
	      }
	    }
	    for (int i = 0; i < samples; i++) {
	      for (int j = 0; j < 10000; j++) {
	        //De Jong's attractor
	        newx = (float) (((sin(pa * oldy) - cos(pb * oldx)) * N * 0.2) + N/2);
	        newy = (float) (((sin(pc * oldx) - cos(pd * oldy)) * N * 0.2) + N/2);
	        //Smoothie
	        newx += random(-0.001f, 0.001f);
	        newy += random(-0.001f, 0.001f);
	        //If coordinates are within range, up density count at its position
	        if ((newx > 0) && (newx < N) && (newy > 0) && (newy < N) ) {
	          density[(int)(newx)][(int)(newy)] += 1;
	          previousx[(int)(newx)][(int)(newy)] = oldx;
	        }
	        oldx = newx;
	        oldy = newy;
	      }
	    }
	    //Put maximum density and its log()-value into variables
	    for (int i = 0; i < N; i++) {
	      for (int j = 0; j < N; j++) {
	        if (density[i][j] > maxdense) {
	          maxdense = density[i][j];
	          logmaxd = log(maxdense);
	        }
	      }
	    }
	  }
	 
	  void updateloop() {
	    stop = false;
	    stepCounter = 0;
	  }
	 
	  void incrementalupdate() {
	    //Loops the non-clearing update and plotting to produce low-noise render
	    populate(16, false);
	    plot(0, false);
	    redraw();
	  }
	 
	  void reparam(float x, float y) {
	    //Fast reparametrization of variables
	    dj.construct(x, y);
	    dj.populate(1, true);
	    dj.plot(100, true);
	  }
	 
	  PImage plot(int f, boolean c) {
	    int factor = f;
	    boolean clear = c;
	    //Plot image from density array
	    if (clear) {
	      pi = createImage(N, N, RGB);
	    }
	    pi.loadPixels();
	    for (int i = 0; i < N; i++) {
	      for (int j = 0; j < N; j++) {
	        if (density[i][j] > 0) {
	          float myhue = map(previousx[i][j], 0, N, 128, 255); //Select hue based on the x-coord that gave rise to current coord
	          float mysat = map(log(density[i][j]), 0, logmaxd, 128, 0);
	          float mybright = map(log(density[i][j]), 0, logmaxd, 0, 255) + factor;
	          int newc = color(myhue, mysat, mybright);
	          int oldc = pi.pixels[i * N + j];
	          newc = blendColor(newc, oldc, SOFT_LIGHT);
	          pi.pixels[i * N + j] = newc;
	        }
	      }
	    }
	    pi.updatePixels();
	    return pi;
	  }
	 
	}


}
