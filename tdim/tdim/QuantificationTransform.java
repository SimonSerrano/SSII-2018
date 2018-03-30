package tdim;

import java.awt.image.BufferedImage;
import java.awt.Color;

public class QuantificationTransform extends ImageTransform {
    protected static final String [] NAMES = {"uniform", "exponential"};
    protected static final int [] TWO_POWER = {1,2,4,8,16,32,64,128,256,512};
    protected static final int UNIFORM = 0; 
    protected static final int EXPONENTIAL = 1; 
    protected static final double LOG265 = Math.log(26.5);
    protected static final double LOG256 = Math.log(25.6);

    protected int type;
    protected int rBits, gBits, bBits;

    public QuantificationTransform(int [] p) {
	this(p[0],p[1],p[2],p[3]);
    }
    public QuantificationTransform(int type, int redBits, int greenBits, int blueBits) {
	super();
	this.type = type;
	rBits = redBits;
	gBits = greenBits;
	bBits = blueBits;
	name = NAMES[type] + " quantification with red in " + rBits + " bits, green in " + gBits + " bits and blue in " + bBits + " bits.\n";
    }

    public BufferedImage filter(BufferedImage bin) {
	int w = bin.getWidth();
	int h = bin.getHeight();
	BufferedImage bout = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);

	int col, r, g, b, r0, g0, b0;
	Color c;
	if(type == UNIFORM) {
	    for(int i = 0 ; i < w ; i++ )
		for(int j = 0 ; j < h ; j++ ) {
		    col = bin.getRGB(i,j);
		    c = new Color(col);
		    r = c.getRed();
		    g = c.getGreen();
		    b = c.getBlue();
		    r0 = (int) (r*TWO_POWER[rBits]/256);
		    g0 = (int) (g*TWO_POWER[gBits]/256);
		    b0 = (int) (b*TWO_POWER[bBits]/256);
		    r = r0*TWO_POWER[8-rBits] + TWO_POWER[8-rBits]/2;
		    g = g0*TWO_POWER[8-gBits] + TWO_POWER[8-gBits]/2;
		    b = b0*TWO_POWER[8-bBits] + TWO_POWER[8-bBits]/2;
		    r = r > 255 ? 255 : (r < 0 ? 0 : r);
		    g = g > 255 ? 255 : (g < 0 ? 0 : g);
		    b = b > 255 ? 255 : (b < 0 ? 0 : b);
		    col = 65536 * r + 256 * g + b;
		    bout.setRGB(i,j,col);
		}
	}
	else { // type == EXPONENTIAL
	    for(int i = 0 ; i < w ; i++ )
		for(int j = 0 ; j < h ; j++ ) {
		    col = bin.getRGB(i,j);
		    c = new Color(col);
		    r = c.getRed();
		    g = c.getGreen();
		    b = c.getBlue();
		    r0 = (int) (toLog(r,rBits));
		    g0 = (int) (toLog(g,gBits));
		    b0 = (int) (toLog(b,bBits));
		    r = (int) ((toExp(r0,rBits) + toExp(r0+1,rBits))/2.0);
		    g = (int) ((toExp(g0,gBits) + toExp(g0+1,gBits))/2.0);
		    b = (int) ((toExp(b0,bBits) + toExp(b0+1,bBits))/2.0);
		    r = r > 255 ? 255 : (r < 0 ? 0 : r);
		    g = g > 255 ? 255 : (g < 0 ? 0 : g);
		    b = b > 255 ? 255 : (b < 0 ? 0 : b);
		    col = 65536 * r + 256 * g + b;
		    bout.setRGB(i,j,col);
		}
	}
	return bout;
    }

    private double toLog( int d, int nbBits) {
	return 255*Math.log(d/10.0+1)*TWO_POWER[nbBits]/(LOG265*256);
    }
    
    private double toExp( int d, int nbBits) {
	return (Math.exp(256*d*LOG256/(TWO_POWER[nbBits]*255))-1)*10;
    }

}
