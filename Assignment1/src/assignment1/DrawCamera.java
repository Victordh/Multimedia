package assignment1;

import uvamult.assignment1.R;
import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import assignment1.android.CameraView;

public class DrawCamera {
	
	/*
	 * This class is the only file that needs changes to show
	 * the histogram of the green values.
	 */
	
	public int[] rgb;			// the array of integers
	public int[] bins = new int[256];
	public Size imageSize;
	public Paint p;
	public Activity activity;
	
	
	public void imageReceived(byte[] data) {
		// Allocate the image as an array of integers if needed.
		// Then, decode the raw image data in YUV420SP format into a red-green-blue array (rgb array)
		// Note that per pixel the RGB values are packed into an integer. See the methods r(), g() and b().
		
		if (rgb == null) rgb = new int[imageSize.width*imageSize.height];
		decodeYUV420SP(rgb, data);
		
		// below you should calculate the histogram of the image
		// the histogram should be class member so you can access 
		// it in other methods of this class.
		// ....
		
		//dit reset de bins array tot nul, omdat hij bewaard blijft tussen de verschillende frames door
		for(int i = 0; i < bins.length; i++) {
			bins[i] = 0;
		}
		
		//verdeelt de verschillende groenwaardes in 256 verschillende bins
		for (int i = 0; i < rgb.length; i++) {
			int gvalue = g(rgb[i]);
			bins[gvalue]++;
		}
	}
	
	private void axisDraw(Canvas c, float len) {
		// utility function to draw the coordinate axis in a canvas with length 'len'
		// the x axis is drawn in red, the y axis in green
		p.setColor(combine(255,0,0));
		c.drawLine(30f, 100f, 286f, 100f, p);
		p.setColor(combine(0,255,0));
		c.drawLine(30f, 0f, 30f, 100f, p);
		
	}
	
	public void draw(Canvas c) {
		int w = c.getWidth();
		int h = c.getHeight();
		
		// here you should draw the histogram. The number of bins should be user selectable.
		// note that at this point the canvas origin is in the top left corner of the surface 
		// just below the preview window.
		
		// you could translate translate an reflect the coordinate system to make
		// into a standard coordinate system.
		
		// please not that the canvas height is larger then what can be seen on the screen. 
		// For hints/tips why that is the case..... please mail Rein vd Boomgaard
		
		// instead of drawing the histogram below we draw the origin, put some text in it
		// and draw a line.
		
		//nrBars moet een macht zijn van 2 (anders crasht ie)
		
		SeekBar seekbar = (SeekBar)activity.findViewById(R.id.seekBar1);
		
		int nrBars = (int)Math.pow(2, seekbar.getProgress());
		int[] hist = new int[nrBars];
		int temp = 0;
		float mean;
		float median = 0;
		float standardDeviation = 0;
		int barwidth = 256/nrBars;
		
		//voegt de verschillende bins toe tot de gewenste grootte bins.
		for (int i = 0; i < 256; i++) {
			hist[(int)i/(barwidth)] += bins[i];
		}
		
		//berekent het gemiddelde
		for (int i = 0; i < 256; i++) {
			temp += (bins[i] * i);
		}
		mean = (float)temp/rgb.length;
		
		//berekent de mediaan
		temp = 0;
		for (int i = 0; i < 256; i++) {
			temp += bins[i];
			if (temp*2 >= rgb.length) {
				median = i;
				break;
			}
		}
		
		//berekent de standaard afwijking
		temp = 0;
		for (int i = 0; i < 256; i++) {
			temp += ((i - mean) * (i - mean) * bins[i]);
		}
		standardDeviation = (float)Math.sqrt((float)temp/rgb.length);
		
		c.drawColor(Color.GRAY);
		axisDraw(c,250f);
		p.setColor(combine(255, 0, 0));
		c.drawText("nrOfPixels = "+rgb.length, 15, 95, p);
		c.drawText("standard deviation = "+standardDeviation, 15, 110, p);
		c.drawText("median = "+median, 15, 125, p);
		c.drawText("mean = "+mean, 15, 140, p);
		
		int biggestBin = 0;
		for(int i = 1; i < nrBars; i++) {
			if (hist[i] > hist[biggestBin]) {
				biggestBin = i;
			}
		}
		
		for(int i = 0; i < nrBars; i++) {
			drawBin(c, i, hist[i], hist[biggestBin], barwidth);
		}
		
		//c.drawLine(0.0f,0.0f,(float)c.getWidth()-5, c.getHeight(),p);
	
		Log.d("DEBUG", "canvas w = " + c.getWidth() + " h = " + c.getHeight()); // this writes to the LogCat that can be read on your PC in
																				// the phone is connected
	}
	
	/*
	 * Setup your environment
	 */
	public void setup(CameraView view) {
		// Example: add a button to the bottom bar
		/*
		view.addButton("Debug Message", new View.OnClickListener() {
			public void onClick(View arg) {
				// Using Log, you can print debug messages to Eclipse
				Log.d("DrawCamera", "Debug message");
			}
		});
		*/
		// You can add your own buttons here
		
	}
	
	
	
	/*
	 * Below are some convenience methods,
	 * like grabbing colors and decoding.
	 */
    
	// Extract the red element from the given color
    private int r(int rgb) {
    	return (rgb & 0xff0000) >> 16;
    }

	// Extract the green element from the given color
    private int g(int rgb) {
    	return (rgb & 0x00ff00) >> 8;
    }

	// Extract the blue element from the given color
    private int b(int rgb) {
    	return (rgb & 0x0000ff);
    }
    
    // Combine red, green and blue into a single color int
    private int combine(int r, int g, int b) {
    	 return 0xff000000 | (r << 16) | (g << 8) | b;
    }
    
    
    /*
     * Draw the rgb image onto the canvas
     */
    private void drawImage(Canvas c) {
    	c.save();
    	
		//c.scale(c.getWidth(), c.getHeight()); // Note: turn to 1x1
		//c.rotate(90, 0.5f, 0.5f); // Note: rotate around half
		//c.scale(1.0f/imageSize.width, 1.0f/imageSize.height); // Note: scale to image sizes 
		
		//c.translate(10f,5f);
		axisDraw(c, 100f);
		
		c.drawBitmap(rgb, 0, imageSize.width, 0f, 0f, imageSize.width, imageSize.height, true, null);
		c.restore();
    }
    
    
    
    /*
     * Decode the incoming data (YUV format) to a red-green-blue format
     */
	private void decodeYUV420SP(int[] rgb, byte[] yuv420sp) {
		final int width = imageSize.width;
		final int height = imageSize.height;
    	final int frameSize = width * height;
    	
    	for (int j = 0, yp = 0; j < height; j++) {
    		int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
    		for (int i = 0; i < width; i++, yp++) {
    			int y = (0xff & ((int) yuv420sp[yp])) - 16;
    			if (y < 0) y = 0;
    			if ((i & 1) == 0) {
    				v = (0xff & yuv420sp[uvp++]) - 128;
    				u = (0xff & yuv420sp[uvp++]) - 128;
    			}
    			
    			int y1192 = 1192 * y;
    			int r = (y1192 + 1634 * v);
    			int g = (y1192 - 833 * v - 400 * u);
    			int b = (y1192 + 2066 * u);
    			
    			if (r < 0) r = 0; else if (r > 262143) r = 262143;
    			if (g < 0) g = 0; else if (g > 262143) g = 262143;
    			if (b < 0) b = 0; else if (b > 262143) b = 262143;
    			
    			rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
    		}
    	}
    }

    
	public DrawCamera(Activity activity) {
		p = new Paint();
		this.activity = activity;
	}
	
	/* tekent een bin van de gegeven grootte en plaats */
	public void drawBin(Canvas c, int location, float height, int nrPixels, int width) {
		height = (height/nrPixels) * 100;
		p.setColor(combine(0,0,0));
		c.drawLine(30f + (width*location), 100f, 30f + (width*location), 100f - height, p);
		c.drawLine(30f + width + (width*location), 100f, 30f + width + (width*location), 100f - height, p);
		c.drawLine(30f + (width*location), 100f - height, 30f + width + (width*location), 100f - height, p);
	}
}
