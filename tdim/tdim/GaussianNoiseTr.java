package tdim;

import java.awt.image.BufferedImage;
import java.awt.Color;
import java.util.Random;

public class GaussianNoiseTr extends ImageTransform {
    protected double sigma;

    public GaussianNoiseTr(double s) {
	super();
	sigma = s;
	name = "Additive gaussian noise with sigma = " + sigma;
    }

    public BufferedImage filter(BufferedImage bin) {
	int w = bin.getWidth();
	int h = bin.getHeight();

	Random aleatoire = new Random(w);
	BufferedImage bout = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
	
	int col, bruit, r, g, b;
	Color c;

	for(int i = 0 ; i < w ; i++ )
	    for(int j = 0 ; j < h ; j++ ) {
		col = bin.getRGB(i,j);
		Color color = new Color(col);
		r = color.getRed();
		g = color.getGreen();
		b = color.getBlue();
		bruit = (int) ((int) aleatoire.nextGaussian()*sigma);
		int nr = r+bruit;
		int ng = g+bruit;
		int nb = b+bruit;
		nr = nr>255?255:nr<0?0:nr;
		ng = ng>255?255:ng<0?0:ng;
		nb = nb>255?255:nb<0?0:nb;
		col = nb + 256*(ng + 256*nr);
		bout.setRGB(i,j,col);
	    }
	return bout;
    }

}
