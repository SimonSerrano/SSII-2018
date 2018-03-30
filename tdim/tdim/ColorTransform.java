package tdim;

import java.awt.image.BufferedImage;
import java.awt.Color;

public class ColorTransform extends ImageTransform {
    public static final int RED_IN_RED = 0;
    public static final int GREEN_IN_GREEN = 1;
    public static final int BLUE_IN_BLUE = 2;
    public static final int RED_IN_GREY = 3;
    public static final int GREEN_IN_GREY = 4;
    public static final int BLUE_IN_GREY = 5;
    public static final int Y_IN_GREY = 6;
    public static final int U_IN_GREY = 7;
    public static final int V_IN_GREY = 8;
    public static final int UV_WITH_Y_CST = 9;
    public static final String [] TRANSF_NAMES = {"(r,0,0) : Red in Red",
						  "(0,g,0) : Green in Green",
						  "(0,0,b) : Blue in Blue",
						  "(r,r,r) : Red in Grey",
						  "(g,g,g) : Green in Grey",
						  "(b,b,b) : Blue in Grey",
						  "(y,y,y) : luminance Y",
						  "(u,u,u) : chrominance U",
						  "(v,v,v) : chrominance V",
						  "chrominances U and V with Y constant"};
    protected int type; // type of color transformation. For example : RED_IN_RED

    public ColorTransform(int transformType) {
	type = transformType;
	name = TRANSF_NAMES[type];
    }

    public BufferedImage filter(BufferedImage bin) {
	int w = bin.getWidth();
	int h = bin.getHeight();
	BufferedImage bout = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
	double vmax = 0.877*255;
    double ymax = 0.299*255 + 0.587*255 + 0.114*255;
    double umax = 0.493*255;
    double vmin = -0.877*ymax;
    double umin = -0.493*ymax;

	int col, r, g, b;
	Color c;

	switch(type) {
	case RED_IN_RED:
	    for(int i = 0 ; i < w; i++ )
		for(int j = 0 ; j < h; j++ ) {
		    col = bin.getRGB(i,j);
		    c = new Color(col);
		    r = c.getRed();
		    c = new Color(r,0,0);
		    col = c.getRGB();// 65536 * r; //256 * 256 = 65536
		    bout.setRGB(i,j,col);
		}
	    break;
	case GREEN_IN_GREEN:
		for(int i = 0 ; i < w; i++ )
			for(int j = 0 ; j < h; j++ ) {
				col = bin.getRGB(i,j);
				c = new Color(col);
				g = c.getGreen();
				c = new Color(0,g,0);
				col = c.getRGB();// 65536 * r; //256 * 256 = 65536
				bout.setRGB(i,j,col);
			}
	    break;
	case BLUE_IN_BLUE:
		for(int i = 0 ; i < w; i++ )
			for(int j = 0 ; j < h; j++ ) {
				col = bin.getRGB(i,j);
				c = new Color(col);
				b = c.getBlue();
				c = new Color(0,0,b);
				col = c.getRGB();// 65536 * r; //256 * 256 = 65536
				bout.setRGB(i,j,col);
			}
	    break;
	case RED_IN_GREY:
		for(int i = 0 ; i < w; i++ )
			for(int j = 0 ; j < h; j++ ) {
				col = bin.getRGB(i,j);
				c = new Color(col);
				r = c.getRed();
				c = new Color(r,r,r);
				col = c.getRGB();// 65536 * r; //256 * 256 = 65536
				bout.setRGB(i,j,col);
			}
	    break;
	case GREEN_IN_GREY:
		for(int i = 0 ; i < w; i++ )
			for(int j = 0 ; j < h; j++ ) {
				col = bin.getRGB(i,j);
				c = new Color(col);
				g = c.getGreen();
				c = new Color(g,g,g);
				col = c.getRGB();// 65536 * r; //256 * 256 = 65536
				bout.setRGB(i,j,col);
			}
	    break;
	case BLUE_IN_GREY:
		for(int i = 0 ; i < w; i++ )
			for(int j = 0 ; j < h; j++ ) {
				col = bin.getRGB(i,j);
				c = new Color(col);
				b = c.getBlue();
				c = new Color(b,b,b);
				col = c.getRGB();// 65536 * r; //256 * 256 = 65536
				bout.setRGB(i,j,col);
			}
	    break;
	case Y_IN_GREY:
        for(int i = 0 ; i < w; i++ )
            for(int j = 0 ; j < h; j++ ) {
                col = bin.getRGB(i,j);
                c = new Color(col);
                r = c.getRed();
                g = c.getGreen();
                b = c.getBlue();
                double y = 0.299*r+ 0.587*g + 0.114*b;
                int yy = (int) y;
                if (yy>255) yy = 255;
                if (yy<0) yy = 0;
                c = new Color(yy,yy,yy);
                col = c.getRGB();// 65536 * r; //256 * 256 = 65536
                bout.setRGB(i,j,col);
            }
	    break;
	case U_IN_GREY:
        for(int i = 0 ; i < w; i++ )
            for(int j = 0 ; j < h; j++ ) {
                col = bin.getRGB(i,j);
                c = new Color(col);
                r = c.getRed();
                g = c.getGreen();
                b = c.getBlue();
                double y = 0.299*r+ 0.587*g + 0.114*b;
                double u = 0.493*(b-y);
                int uu = (int)((u-umin)/(umax-umin)*255);
                c = new Color(uu,uu,uu);
                col = c.getRGB();// 65536 * r; //256 * 256 = 65536
                bout.setRGB(i,j,col);
            }
	    break;
	case V_IN_GREY:
        for(int i = 0 ; i < w; i++ )
            for(int j = 0 ; j < h; j++ ) {
                col = bin.getRGB(i,j);
                c = new Color(col);
                r = c.getRed();
                g = c.getGreen();
                b = c.getBlue();
                double y = 0.299*r+ 0.587*g + 0.114*b;
                double u = 0.493*(b-y);
                double v = 0.877*(r-y);
                int vv = (int)((v-vmin)/(vmax-vmin)*255);
                c = new Color(vv,vv,vv);
                col = c.getRGB();// 65536 * r; //256 * 256 = 65536
                bout.setRGB(i,j,col);
            }
	    break;
	case UV_WITH_Y_CST:
        for(int i = 0 ; i < w; i++ )
            for(int j = 0 ; j < h; j++ ) {
                col = bin.getRGB(i,j);
                c = new Color(col);
                r = c.getRed();
                b = c.getBlue();
                int y = 128;
                double u = 0.493*(b-y);
                double v = 0.877*(r-y);
                r = (int)((v/0.877) + y);
                g = (int)((y-0.299*r-0.114*b)/0.587);
                b = (int)((u/0.493)+y);
                if (r>255) r=255;
                if (r<0) r=0;
                if (g>255) g=255;
                if (g<0)g=0;
                if (b>255)b=255;
                if (b<0)b=0;
                c = new Color(r,g,b);
                col = c.getRGB();// 65536 * r; //256 * 256 = 65536
                bout.setRGB(i,j,col);
            }
	    break;
	default:
	    for(int i = 0 ; i < w; i++ )
		for(int j = 0 ; j < h; j++ ) {
		    col = bin.getRGB(i,j);
		    c = new Color(col);
		    r = c.getRed();
		    g = c.getGreen();
		    b = c.getBlue();
		    col = b + 256 * ( g + 256 * r);
		    bout.setRGB(i,j,col);
		}
	}
	return bout;
    }
    
    
}
