package tdim;

import java.awt.image.BufferedImage;
import java.awt.Color;

public class HistogramTransform extends ImageTransform {


    public HistogramTransform() {
	super();
	name = "histogram computation";
    }

    // 3 histograms are computed : one on the red component, one on the blue and the last one on the red
    // the image width is 512
    // the image height is 3*200

    public BufferedImage filter(BufferedImage bin) {
	int w = bin.getWidth();
	int h = bin.getHeight();
	BufferedImage bout = new BufferedImage(512,601,BufferedImage.TYPE_INT_RGB);
	int [] r = new int[256];
	int [] g = new int[256];
	int [] b = new int[256];
	Color c;
	for(int i = 0 ; i < w; i++ )
	    for(int j = 0 ; j < h; j++ ) {
		c = new Color(bin.getRGB(i,j));
		r[c.getRed()] ++;
		g[c.getGreen()]++;
		b[c.getBlue()]++;
	    }
	// computing the max occurence
	int max = 0;
	for(int i = 0 ; i < 256; i++ ) {
	    if(r[i]>max)
		max = r[i];
	    if(g[i]>max)
		max = g[i];
	    if(b[i]>max)
		max = b[i];
	}
	for(int i = 0 ; i < 256; i++ ) {
	    r[i] = r[i]* 200/max;
	    g[i] = g[i]* 200/max;
	    b[i] = b[i]* 200/max;
	}
	// drawing the histogram in the image bout
	for(int i = 0 ; i < 256; i++ ) {
	    bout.setRGB(2*i,200,16777215);
	    bout.setRGB(2*i,400,16777215);
	    bout.setRGB(2*i,600,16777215);
	    bout.setRGB(2*i+1,200,16777215);
	    bout.setRGB(2*i+1,400,16777215);
	    bout.setRGB(2*i+1,600,16777215);
	    for(int j = 1; j < r[i]; j++) {
		bout.setRGB(2*i,200-j,16777215);
		bout.setRGB(2*i+1,200-j,16777215);
	    }
	    for(int j = 1; j < g[i]; j++) {
		bout.setRGB(2*i,400-j,16777215);
		bout.setRGB(2*i+1,400-j,16777215);
	    }
	    for(int j = 1; j < b[i]; j++) {
		bout.setRGB(2*i,600-j,16777215);
		bout.setRGB(2*i+1,600-j,16777215);
	    }
	}
	return bout;
    }
}
