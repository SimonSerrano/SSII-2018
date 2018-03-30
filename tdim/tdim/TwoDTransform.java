package tdim;

import java.awt.image.BufferedImage;

import static java.lang.Math.*;

public class TwoDTransform extends ImageTransform {
    // interpolation type
    protected static final int NEAREST_NEIGHBOR = 0;
    protected static final int BILINEAR = 1;
    protected static final int BELL = 2;
    protected static final int ORDER3_B1_C0 = 3;
    protected static final int ORDER3_B0_C1 = 4;
    protected static final int ORDER3 = 5;
    protected static final String [] ORDERS = {"Nearest neighbor","Bilinear","Bell",  "third order (B=1) (C=0)", "third order (B=0) (C=1)", "third order (B= ... ) (C= ...)"};

    // displacement type
    protected static final int TRANSLATION = 0;
    protected static final int ROTATION = 1;
    protected static final int ZOOM = 2; // image size is not changed
    protected static final int SCALE = 3; // image size is now oldSize * scale factor
    protected static final String [] NAMES = {"Translation", "Rotation", "Zoom", "Scaling"};

    protected int interpolationMode;
    protected int type;
    protected double b; // B in the Mitchell polynoms
    protected double c; // C in the Mitchell polynoms

    protected double angle = 0.0;
    protected double scale = 1.0;
    protected double tx = 0.0;
    protected double ty = 0.0;
    protected double cx = 0.0;
    protected double cy = 0.0;
 
    // rot angle
    // rot angle cx cy
    // trans tx ty
    // zoom or scaling scale
    // zoom of scaling scale cx cy
    public TwoDTransform(FrameParameters fp, int type, double d) {
	super();
	this.interpolationMode = fp.interpolationMode;
	this.b = fp.b;
	this.c = fp.c;
	this.type = type;
	if(type == ROTATION)
	    angle = d;
	else if((type == ZOOM)||(type == SCALE))
	    scale = d;
    }
    public TwoDTransform(FrameParameters fp, int type, double d1, double d2) {
	super();
	this.interpolationMode = fp.interpolationMode;
	this.b = fp.b;
	this.c = fp.c;
	this.type = type;
	if(type == TRANSLATION) {
	    tx = d1;
	    ty = d2;
	}
    }
    public TwoDTransform(FrameParameters fp, int type, double d1, double d2, double d3) {
	 this(fp, type, d1);
	 cx = d2;
	 cy = d3;
     }

    public String toString() {
	String res = "";

	res += NAMES[type];
	switch(type) {
	case TRANSLATION: res += " of vector " + tx + " ; " + ty; break;
	case ROTATION: res += " of angle " + angle  + " degrees and center " + cx + " ; " + cy; break;
	case ZOOM:
	case SCALE: res += " of factor " + scale + "and center " + cx + " ; " + cy; break;
	}
	return res;
    }

    public BufferedImage filter(BufferedImage bin) {
	int w = bin.getWidth();
	int h = bin.getHeight();

	BufferedImage bout;

	switch(type) {
	case TRANSLATION:
	    bout = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
	    for( int i = 0; i < w; i++ )
		for( int j = 0 ; j < h; j++ ) {
            double x = i - tx;
            double y = j - ty;
            bout.setRGB(i, j, getPixel(bin, x, y));
        }
	    break;
	case ROTATION:
	    bout = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
	    double anglerad = (PI*angle)/180;
	    for( int i = 0; i < w; i++ )
		for( int j = 0 ; j < h; j++ ) {
            double newCenterx = (i-cx);
            double newCentery = (j-cy);
            double x = (newCenterx*cos(anglerad)) - (newCentery*sin(anglerad)+cx);
            double y = (newCenterx*cos(anglerad)) + (newCentery*sin(anglerad)+cy);
            bout.setRGB(i, j, getPixel(bin, x, y));
        }
	    break;
	case ZOOM:
	    bout = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
	    for( int i = 0; i < w; i++ )
		for( int j = 0 ; j < h; j++ ) {
	        double newCenterx = (i-cx);
	        double newCentery = (j-cy);
	        double x = (newCenterx/scale)+cx;
	        double y = (newCentery/scale)+cy;
            bout.setRGB(i, j, getPixel(bin, x, y));
        }
	    break;
	case SCALE:
	    bout = new BufferedImage(w*((int)scale),h*((int)scale),BufferedImage.TYPE_INT_RGB);
	    for( int i = 0; i < w; i++ )
		for( int j = 0 ; j < h; j++ ) {

            bout.setRGB(i, j, getPixel(bin, i, j));
        }
	    break;
	    
	default:
	    bout = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
	    for( int i = 0; i < w; i++ )
		for( int j = 0 ; j < h; j++ ) {
            bout.setRGB(i, j, bin.getRGB(i, j));
        }
	}
	
	return bout;
    }
    
    private int getPixel(BufferedImage bi, double x, double y) {
	int h = bi.getHeight();
	int w = bi.getWidth();

	if(x < 0 || y < 0 || x >= w || y >= h)
	    return 0;
	int x0 = (int) x;
	int y0 = (int) y;
	double ex2, ey2;
	double ex = x - x0;
	double ey = y - y0;
	int [][] r; int [][] g; int [][] b;
	int col; int rz = 0; int gz = 0; int bz = 0;
	int xc, yc;
	double [] Rx; double [] Ry; 	double Rxy;
	double rd = 0.0; double gd = 0.0; double bd = 0.0;

	switch(interpolationMode) {
	case NEAREST_NEIGHBOR:

	    x0 = (int) (x+0.5);
	    y0 = (int) (y+0.5);
	    int x1 = min(x0, w-1);
	    int y1 = min(y0, h-1);
	    return bi.getRGB(x1,y1);

	case BILINEAR:
        int col_00 = bi.getRGB(x0,y0);
        int col_01 = bi.getRGB(x0,y0+1);
        int col_10 = bi.getRGB(x0+1,y0);
        int col_11 = bi.getRGB(x0+1,y0+1);
        col = (int)((1-ex)*(1-ey)*col_00+(1-ex)*ey*col_01+ex*(1-ey)*col_10+ex*ey*col_11);
	    return col;
	case BELL:
	    break;
	case ORDER3_B1_C0:
	    break;
	case ORDER3_B0_C1:
	    break;
	case ORDER3:// to be implemented with B=... C=...
	    break;
	default: 
	    System.out.println("not yet implemented");
	}
	return 0;

    }
}
