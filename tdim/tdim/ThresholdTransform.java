package tdim;

import java.awt.image.BufferedImage;
import java.awt.Color;

public class ThresholdTransform extends ImageTransform {

    public ThresholdTransform() {
	super();
	name = "threshold";
    }

    public BufferedImage filter(BufferedImage bin) {
	int w = bin.getWidth();
	int h = bin.getHeight();
	BufferedImage bout = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
	int col, r, g, b;
	Color c;
	for(int i = 0 ; i < w; i++ )
	    for(int j = 0 ; j < h; j++ ) {
		col = bin.getRGB(i,j);
		// TO BE MODIFIED
		bout.setRGB(i,j,col);
	    }
	return bout;
    }
 
}
