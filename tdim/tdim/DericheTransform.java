package tdim;

import java.awt.image.BufferedImage;
import java.awt.Color;

public class DericheTransform extends ImageTransform {

    protected static final double ALPHA = 2; // default value for the filter size

    protected static final int LISSAGE=0;
    protected static final int DERIV_X=1;
    protected static final int DERIV_Y=2;
    protected static final int LAPLACE_1=3;
    protected static final int LAPLACE_2=4;
    protected static final int DERIVEE=5;
    protected static final int LAPLACE=6;
    protected static final int LAPLACE_SIGN=7;
    protected static final int LAPLACE_ZEROS=8;

    public static final String [] NAMES = {"Deriche smoothing", "Deriche X derivative", "Deriche Y derivative", "Laplace step 1", "Laplace step 2","Norm of Deriche derivative", "Whole Laplace Deriche","Signed Laplace","Zeros of Laplace"};

    protected double alpha; 
    protected double expoAlpha, expo2Alpha;
    protected int type;

    protected double a1,a2,a3,a4,a5,a6,a7,a8,b1,b2,c1,c2,k;

    public DericheTransform(ImageFrame iff, int index) {
	super();
	name = NAMES[index] + " filter";
	this.alpha=iff.fparams.alpha;
	System.out.println("alpha = " + alpha);
	expoAlpha=Math.exp(-alpha);
	expo2Alpha=Math.exp(-2*alpha);
	type = index;
	switch(type) {
	case LISSAGE:
	    k= (1-expoAlpha)*(1-expoAlpha)/(1+2*alpha*expoAlpha-expo2Alpha);
	    a1 = k;
	    a2 = k*expoAlpha*(alpha-1);
	    a3 = k*expoAlpha*(alpha+1);
	    a4 = -k*expo2Alpha;
	    a5 = k;
	    a6 = k*expoAlpha*(alpha-1);
	    a7 = k*expoAlpha*(alpha+1);
	    a8 = -k*expo2Alpha;
	    b1= 2*expoAlpha;
	    b2= -expo2Alpha;
	    c1= 1;
	    c2= 1;
	    break;
	case DERIV_X:
	case DERIVEE:
	    k= (1-expoAlpha)*(1-expoAlpha)/(1+2*alpha*expoAlpha-expo2Alpha);
	    a1 = 0;
	    a2 = 1;
	    a3 = -1;
	    a4 = 0;
	    a5 = k;
	    a6 = k*expoAlpha*(alpha-1);
	    a7 = k*expoAlpha*(alpha+1);
	    a8 = -k*expo2Alpha;
	    b1= 2*expoAlpha;
	    b2= -expo2Alpha;
	    c1= -(1-expoAlpha)*(1-expoAlpha);
	    c2= 1;
	    break;
	case DERIV_Y:
	    k= (1-expoAlpha)*(1-expoAlpha)/(1+2*alpha*expoAlpha-expo2Alpha);
	    a5 = 0;
	    a6 = 1;
	    a7 = -1;
	    a8 = 0;
	    a1 = k;
	    a2 = k*expoAlpha*(alpha-1);
	    a3 = k*expoAlpha*(alpha+1);
	    a4 = -k*expo2Alpha;
	    b1= 2*expoAlpha;
	    b2= -expo2Alpha;
	    c1= 1;
	    c2= -(1-expoAlpha)*(1-expoAlpha);
	    break;
	case LAPLACE_1:
	case LAPLACE:
	case LAPLACE_SIGN:
	case LAPLACE_ZEROS:
	    a1 = 1;
	    a2 = 0;
	    a3 = expoAlpha;
	    a4 = 0;
	    a5 = 1;
	    a6 = 0;
	    a7 = expoAlpha;
	    a8 = 0;
	    b1= expoAlpha; 
	    b2= 0;
	    c1= 1;
	    c2= 1;
	    k= 1;
	    break;
	case LAPLACE_2:
	    a1 = 0;
	    a2 = 1;
	    a3 = 1;
	    a4 = 0;
	    a5 = 0;
	    a6 = 1;
	    a7 = 1;
	    a8 = 0;
	    b1= 2*expoAlpha;
	    b2= -expo2Alpha;
	    c1= (1-expo2Alpha)/2.0;
	    c2= c1;
	    k= 1;
	    break;
	default:
	    System.out.println("Deriche filter not known");
	}
    }

    public BufferedImage filter(BufferedImage bin) {
	int w = bin.getWidth();
	int h = bin.getHeight();
	BufferedImage bout = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);

	double [][] rc = new double[w][h];
	double [][] gc = new double[w][h];
	double [][] bc = new double[w][h];

	// initialisation of the green, red and blue arrays
	for(int i = 0 ; i < w; i++ )
	    for(int j = 0; j < h; j++) {
		Color c = new Color(bin.getRGB(i,j));
		rc[i][j] = c.getRed();
		gc[i][j] = c.getGreen();
		bc[i][j] = c.getBlue();
	    }
	double [][][] r1 = filtering(rc, gc, bc, w, h);

	if(type < 5) {
	    // writing the result to bout
	    int red, green, blue,col;
	    for(int i = 0 ; i < w; i++ )
		for(int j = 0; j < h; j++) {
		    red = (int) (r1[0][i][j]+0.5);
		    green = (int) (r1[1][i][j]+0.5);
		    blue = (int) (r1[2][i][j]+0.5);
		    red=Math.abs(red);
		    green=Math.abs(green);
		    blue=Math.abs(blue);
		    red=(red>255)?255:red;
		    green=(green>255)?255:green;  
		    blue=(blue>255)?255:blue;
		    col = 65536 * red + 256 * green + blue;
		    bout.setRGB(i,j,col);
		}	
	    return bout;
	}
	if(type == DERIVEE) {
	    k= (1-expoAlpha)*(1-expoAlpha)/(1+2*alpha*expoAlpha-expo2Alpha);
	    a5 = 0;	    a6 = 1;	    a7 = -1;	    a8 = 0;
	    a1 = k;	    a2 = k*expoAlpha*(alpha-1);	    a3 = k*expoAlpha*(alpha+1);	    a4 = -k*expo2Alpha;
	    b1= 2*expoAlpha;	    b2= -expo2Alpha;	    c1= 1;	    c2= -(1-expoAlpha)*(1-expoAlpha);	    
	    double [][][] r2 = filtering(rc, gc, bc, w, h);
	    // writing the result to bout
	    int red, green, blue,col;
	    for(int i = 0 ; i < w; i++ )
		for(int j = 0; j < h; j++) {
		    red = (int) (Math.sqrt(r2[0][i][j]*r2[0][i][j]+r1[0][i][j]*r1[0][i][j])+0.5);
		    green = (int) (Math.sqrt(r2[1][i][j]*r2[1][i][j]+r1[1][i][j]*r1[1][i][j])+0.5);
		    blue = (int) (Math.sqrt(r2[2][i][j]*r2[2][i][j]+r1[2][i][j]*r1[2][i][j])+0.5);
		    red=Math.abs(red);
		    green=Math.abs(green);
		    blue=Math.abs(blue);
		    red=(red>255)?255:red;
		    green=(green>255)?255:green;  
		    blue=(blue>255)?255:blue;
		    col = 65536 * red + 256 * green + blue;
		    bout.setRGB(i,j,col);
		}	
	    return bout;
	}
	//if(type == LAPLACE)||(type == LAPLACE_ZEROS)||(type==LAPLACE_SIGN) {
	a1 = 0;	    a2 = 1;	    a3 = 1;	    a4 = 0;
	a5 = 0;	    a6 = 1;	    a7 = 1;	    a8 = 0;
	b1= 2*expoAlpha;	    b2= -expo2Alpha;
	c1= (1-expo2Alpha)/2.0;	    c2= c1;
	double [][][] r2 = filtering(rc, gc, bc, w, h);

	// writing the result to bout
	if(type == LAPLACE) {

	int red, green, blue,col;
	double maxR = 0;
	double maxG = 0;
	double maxB = 0;

	    // we normalize the Laplacien
	    for(int i = 0 ; i < w; i++ )
		for(int j = 0; j < h; j++) {
		    r1[0][i][j] = Math.abs(r2[0][i][j]-r1[0][i][j]);
		    r1[1][i][j] = Math.abs(r2[1][i][j]-r1[1][i][j]);
		    r1[2][i][j] = Math.abs(r2[2][i][j]-r1[2][i][j]);
		    if(r1[0][i][j] >maxR) maxR = r1[0][i][j]; 
		    if(r1[1][i][j] >maxG) maxG = r1[1][i][j]; 
		    if(r1[2][i][j] >maxB) maxB = r1[2][i][j]; 
		}
	    for(int i = 0 ; i < w; i++ )
		for(int j = 0; j < h; j++) {
		    red = (int) (255*r1[0][i][j] /maxR);
		    green = (int) (255*r1[1][i][j] /maxG);
		    blue = (int) (255*r1[2][i][j] /maxB);
		    col = 65536 * red + 256 * green + blue;
		    bout.setRGB(i,j,col);
		}
	}
	else if(type == LAPLACE_SIGN) { 
	    double max = 0;
	    double [][] y = new double[w][h];

	    for(int i = 0 ; i < w; i++ )
		for(int j = 0; j < h; j++) {
		    y[i][j] = 0.299*(r2[0][i][j]-r1[0][i][j])+ 0.587*(r2[1][i][j]-r1[1][i][j]) + 0.114*(r2[2][i][j]-r1[2][i][j]) ;
		    if(Math.abs(y[i][j])>max) max = Math.abs(y[i][j]);
		}
	    for(int i = 0 ; i < w; i++ )
		for(int j = 0; j < h; j++) {
		    if(y[i][j] >=0)  bout.setRGB(i,j,257 * ((int) (y[i][j]*255/max)));
		    else bout.setRGB(i,j, 65792 * ((int) (-y[i][j]*255/max)));
		}
	}
	else {// LAPLACE_ZEROS
	    double [][] y = new double[w][h];
	    for(int i = 0 ; i < w; i++ )
		for(int j = 0; j < h; j++) {
		    y[i][j] = 0.299*(r2[0][i][j]-r1[0][i][j])+ 0.587*(r2[1][i][j]-r1[1][i][j]) + 0.114*(r2[2][i][j]-r1[2][i][j]) ;
		}
	    for(int i = 0 ; i < w-1; i++ )
		for(int j = 0; j < h-1; j++) {
		    if( (y[i][j]*y[i+1][j]<0)|| (y[i][j]*y[i+1][j+1]<0)||(y[i][j]*y[i][j+1]<0)) 
			bout.setRGB(i,j,16777215);
		    else
			bout.setRGB(i,j,0);
		}
	}
	
	return bout;
    }
    
    public double [][][] filtering(double [][]rc, double [][]gc, double [][]bc, int w, int h) {
	double [][][] res = new double[3][w][h];

	double [][] ry1 = new double[w][h];
	double [][] gy1 = new double[w][h];
	double [][] by1 = new double[w][h];
	double [][] ry2 = new double[w][h];
	double [][] gy2 = new double[w][h];
	double [][] by2 = new double[w][h];

	//STEP 1
	for(int i = 0; i < w; i++) {
	    // j = 0
	    ry1[i][0]=a1*rc[i][0];
	    gy1[i][0]=a1*gc[i][0];
	    by1[i][0]=a1*bc[i][0];
	    // j = 1
	    ry1[i][1]=a1*rc[i][1]+a2*rc[i][0];
	    gy1[i][1]=a1*gc[i][1]+a2*gc[i][0];
	    by1[i][1]=a1*bc[i][1]+a2*bc[i][0];
	    for(int j = 2; j < h ; j++) {
		ry1[i][j]=a1*rc[i][j]+a2*rc[i][j-1]+b1*ry1[i][j-1]+b2*ry1[i][j-2];
		gy1[i][j]=a1*gc[i][j]+a2*gc[i][j-1]+b1*gy1[i][j-1]+b2*gy1[i][j-2];
		by1[i][j]=a1*bc[i][j]+a2*bc[i][j-1]+b1*by1[i][j-1]+b2*by1[i][j-2];
	    }
	}
	for(int i = w-1; i > 0; i--) {
	    // j = h - 1
	    ry2[i][h-1]= 0;
	    gy2[i][h-1]= 0;
	    by2[i][h-1]= 0;
	    // j = h - 2
	    ry2[i][h-2]= a3*rc[i][h-1]+b1*ry2[i][h-1];
	    gy2[i][h-2]= a3*gc[i][h-1]+b1*gy2[i][h-1];
	    by2[i][h-2]= a3*bc[i][h-1]+b1*by2[i][h-1];
	    for(int j = h-3; j > 0 ; j--) {
		ry2[i][j]=a3*rc[i][j+1]+a4*rc[i][j+2]+b1*ry2[i][j+1]+b2*ry2[i][j+2];
		gy2[i][j]=a3*gc[i][j+1]+a4*gc[i][j+2]+b1*gy2[i][j+1]+b2*gy2[i][j+2];
		by2[i][j]=a3*bc[i][j+1]+a4*bc[i][j+2]+b1*by2[i][j+1]+b2*by2[i][j+2];
	    }
	}
	for(int i = 0 ; i < w; i++ )
	    for(int j = 0; j < h; j++) {
		res[0][i][j] = c1*(ry1[i][j]+ry2[i][j]);
		res[1][i][j] = c1*(gy1[i][j]+gy2[i][j]);
		res[2][i][j] = c1*(by1[i][j]+by2[i][j]);
	    }
	//STEP 2
	for(int j = 0; j < h ; j++) {
	    // i = 0
	    ry1[0][j]=a5*res[0][0][j];
	    gy1[0][j]=a5*res[1][0][j];
	    by1[0][j]=a5*res[2][0][j];
	    // i = 1
	    ry1[1][j]=a5*res[0][1][j]+a6*res[0][0][j]+b1*ry1[0][j];
	    gy1[1][j]=a5*res[1][1][j]+a6*res[1][0][j]+b1*gy1[0][j];
	    by1[1][j]=a5*res[2][1][j]+a6*res[2][0][j]+b1*gy1[0][j];
	    for(int i = 2; i < w; i++) {
		ry1[i][j]=a5*res[0][i][j]+a6*res[0][i-1][j]+b1*ry1[i-1][j]+b2*ry1[i-2][j];
		gy1[i][j]=a5*res[1][i][j]+a6*res[1][i-1][j]+b1*gy1[i-1][j]+b2*gy1[i-2][j];
		by1[i][j]=a5*res[2][i][j]+a6*res[2][i-1][j]+b1*by1[i-1][j]+b2*by1[i-2][j];
	    }
	}
	for(int j = h-1; j > 0 ; j--) {
	    // i = w - 1
	    ry2[w-1][j]= 0;
	    gy2[w-1][j]= 0;
	    by2[w-1][j]= 0;
	    // i = w - 2
	    ry2[w-2][j]= a7*res[0][w-1][j]+b1*ry2[w-1][j];
	    gy2[w-2][j]= a7*res[1][w-1][j]+b1*gy2[w-1][j];
	    by2[w-2][j]= a7*bc[w-1][j]+b1*by2[w-1][j];
	    for(int i = w-3; i > 0; i--) {
		ry2[i][j]=a7*res[0][i+1][j]+a8*res[0][i+2][j]+b1*ry2[i+1][j]+b2*ry2[i+2][j];
		gy2[i][j]=a7*res[1][i+1][j]+a8*res[1][i+2][j]+b1*gy2[i+1][j]+b2*gy2[i+2][j];
		by2[i][j]=a7*res[2][i+1][j]+a8*res[2][i+2][j]+b1*by2[i+1][j]+b2*by2[i+2][j];
	    }
	}
	for(int i = 0 ; i < w; i++ )
	    for(int j = 0; j < h; j++) {
		res[0][i][j] = c2*(ry1[i][j]+ry2[i][j]);
		res[1][i][j] = c2*(gy1[i][j]+gy2[i][j]);
		res[2][i][j] = c2*(by1[i][j]+by2[i][j]);
	    }
	return res;
    }
}
