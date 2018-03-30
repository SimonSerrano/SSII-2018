package tdim;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class SaltAndPepperNoiseTr extends ImageTransform {
    protected int proba;
    protected static final int BLACK = 0;
    protected static final int WHITE = 16777215; // 255(1 + 256*257)

    public SaltAndPepperNoiseTr(int p) {
	super();
	proba = p;
	name = "Additive salt and pepper noise of " + proba + " %";
    }

    public BufferedImage filter(BufferedImage bin) {
	int w = bin.getWidth();
	int h = bin.getHeight();
	Random aleatoire = new Random(w);
	int maxPix = 100/proba;

	BufferedImage bout = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
	
	int col, bruit, r, g, b;

	for(int i = 0 ; i < w ; i++ )
	    for(int j = 0 ; j < h ; j++ ) {
		col = bin.getRGB(i,j);
		bruit = aleatoire.nextInt(maxPix);
		col = bruit==1?WHITE:BLACK;
		bout.setRGB(i,j,col);
	    }
	return bout;
    }

}
