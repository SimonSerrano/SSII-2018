package tdim;

import java.awt.image.BufferedImage;
import java.awt.Color;

public class BinaryTransforms extends ImageTransform {
    protected BufferedImage biSelect;
    protected int type;

    protected static final int ADD = 0;
    protected static final int SUB = 1;

    protected static final String [] NAMES = {"addition of two images","substraction of two images"};

    public BinaryTransforms(BufferedImage b, int t) {
	super();
	biSelect = b;
	type = t;
	name = NAMES[type];
    }
  
    public BufferedImage filter(BufferedImage b) {
	switch(type) {
	case ADD:
	    return add(biSelect,b);
	case SUB:
	    return sub(biSelect,b);
	default:
	    return b;
	}
    }
  
	
public static BufferedImage sub(BufferedImage bi1, BufferedImage bi2) {
	int w = bi1.getWidth();
	int h = bi1.getHeight();
	if( (bi2.getWidth() != w) || (bi2.getHeight() != h)) {
	    System.out.println("You must select 2 images with same size using the Edit/Set image as selected  menu !");
	    return null;
	}
	BufferedImage res = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
	for( int i = 0 ; i < w ; i++ ) {
	    for( int j = 0 ; j < h ; j++ ) {
		Color col1 = new Color(bi1.getRGB(i,j));
		Color col2 = new Color(bi2.getRGB(i,j));
		int r = Math.abs(col1.getRed()-col2.getRed());
		int g = Math.abs(col1.getGreen()-col2.getGreen());
		int b = Math.abs(col1.getBlue()-col2.getBlue());
		int col = b + 256 * ( g + 256 * r);
		res.setRGB(i,j,col);
	    }
	}
	return res;
    }

       public static BufferedImage add(BufferedImage bi1, BufferedImage bi2) {
	int w = bi1.getWidth();
	int h = bi1.getHeight();
	if( (bi2.getWidth() != w) || (bi2.getHeight() != h)) {
	    System.out.println("You must select 2 images with same size using the Edit/Set image as selected  menu !");
	    return null;
	}
	BufferedImage res = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
	for( int i = 0 ; i < w ; i++ ) {
	    for( int j = 0 ; j < h ; j++ ) {
		Color col1 = new Color(bi1.getRGB(i,j));
		Color col2 = new Color(bi2.getRGB(i,j));
		int r = Math.min(255,col1.getRed()+col2.getRed());
		int g = Math.min(255,col1.getGreen()+col2.getGreen());
		int b = Math.min(255,col1.getBlue()+col2.getBlue());
		int col = b + 256 * ( g + 256 * r);
		res.setRGB(i,j,col);
	    }
	}
	return res;
    }
 
}
