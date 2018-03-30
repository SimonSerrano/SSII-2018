package tdim;

import java.awt.image.BufferedImage;
import java.awt.Color;

public class SimpleTransform extends ImageTransform {


    public SimpleTransform() {
	super();
	name = "inverse video";
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
		c = new Color(col);
		r = 255 - c.getRed();
		g = 255 - c.getGreen();
		b = 255 - c.getBlue();
		col = b+256*(g+256*r);
		bout.setRGB(i,j,col);
	    }
	return bout;
    }
}
