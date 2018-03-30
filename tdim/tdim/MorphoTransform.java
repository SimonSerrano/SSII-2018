package tdim;

import java.awt.image.BufferedImage;

public class MorphoTransform extends ImageTransform {

    protected static final int EROSION = 0;
    protected static final int DILATATION = 1;
    protected static final int OPENING = 2;
    protected static final int CLOSING = 3;
    protected static final int THINNING = 4;
    protected static final int SQUELETT = 5;

    protected static final int VERTICAL = 0;
    protected static final int HORIZONTAL = 1;
    protected static final int CROSS = 2;
    protected static final int SQUARE = 3;

    protected static final int [][] K_V = {{0,1,0},{0,1,0},{0,1,0}};
    protected static final int [][] K_H = {{0,0,0},{1,1,1},{0,0,0}};
    protected static final int [][] K_C = {{0,1,0},{1,1,1},{0,1,0}};
    protected static final int [][] K_S = {{1,1,1},{1,1,1},{1,1,1}};

    protected int type;
    protected int kernelType;
    protected int [][] k;

    protected static final String [] KERNELS = {"Vertical", "Horizontal", "Cross", "Square"};
    protected static final String [] NAMES = {"erosion", "dilatation", "opening", "closing", "thinning", "squelett"};

    public MorphoTransform(int t, int kt) {
	super();
	type = t;
	kernelType = kt; 
	switch(kernelType) {
	case VERTICAL:    
	    k = K_V; 
	    break;
	case HORIZONTAL:	    
	    k = K_H; 
	    break;
	case CROSS:	    
	    k = K_C; 
	    break;
	case SQUARE:
	default:	    
	    k = K_S; 
	    break;
	}
	name = NAMES[type] + " operation with a " + KERNELS[kernelType] + " kernel";
    }

    public BufferedImage filter(BufferedImage bin) {
	BufferedImage bout;
	
	switch(type) {
	case EROSION:
	    bout = erode(bin);
	    break;
	case DILATATION:
	    bout = dilate(bin);
	    break;
	case OPENING:
	    bout = dilate(erode(bin));
	    break;
	case CLOSING:
	    bout = erode(dilate(bin));
	    break;
	case THINNING:
	    bout = thin(bin);
	    break;
	case SQUELETT:
	    bout = squelett(bin);
	    break;
	default:
	    bout = bin;
	    break;
	}
	return bout;
    }

    public BufferedImage erode(BufferedImage bin) {
	int w = bin.getWidth();
	int h = bin.getHeight();

	BufferedImage bout = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);

	int uc = k.length/2;
	int vc = k[0].length/2;

	// TO BE DONE
	
	return bout;
    }  

    public BufferedImage dilate(BufferedImage bin) {
	int w = bin.getWidth();
	int h = bin.getHeight();

	BufferedImage bout = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);

	// TO BE DONE
	
	return bout;
    }  
    
    public BufferedImage thin(BufferedImage bin) {
	int w = bin.getWidth();
	int h = bin.getHeight();
	BufferedImage bout = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);

	// TO BE DONE
	
	return bout;
    }  


    public BufferedImage squelett(BufferedImage bin) {
	int w = bin.getWidth();
	int h = bin.getHeight();
	BufferedImage bout = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);

	// TO BE DONE
	
	return bout;
    }

}  
