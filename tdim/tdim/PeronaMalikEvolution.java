package tdim;

import java.awt.image.BufferedImage;

public class PeronaMalikEvolution extends ImageTransform {
    public static final String [] NAMES = {"Evolution Gaussian without blur", "Evolution Gaussian with blur", "Evolution Perona-Malik without blur", "Evolution Perona-Malik with blur"};
    public static final int PURE_G = 0;
    public static final int PURE_G_B = 1;
    public static final int PM = 2;
    public static final int PM_B = 3;

    public int index ;
    public int nbIter;
    public double evolStep;
    public boolean withBlur = false;    
    public boolean pureGaussian = false;  
    
    public PeronaMalikEvolution(ImageFrame jf, int _index) {
	super();
	index = _index;
	nbIter = jf.fparams.iterate;
	evolStep = jf.fparams.h;
	switch(index) {
	case 0: withBlur = false; pureGaussian =  true;  break;
	case 1: withBlur = true; pureGaussian =  true;  break;
	case 2: withBlur = false; pureGaussian =  false;  break;
	case 3: withBlur = true; pureGaussian =  false;  break;
	}
	name = NAMES[index];
    }

    public BufferedImage filter(BufferedImage bin) {
	int w = bin.getWidth();
	int h = bin.getHeight();
	BufferedImage bout = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
	
	double [][] v = new double[w][h];
	double [][] u = new double[w][h];
	double [][] ux = new double[w][h];
	double [][] uy = new double[w][h];
	double [][] uxx = new double[w][h];
	double [][] uyy = new double[w][h];

	int iu;
	int red, green, blue;

      	// initialisation of the u and v  arrays
	for(int i = 0 ; i < w; i++ )
	    for(int j = 0; j < h; j++) {
		red = (bin.getRGB(i,j)>>16)&0xff;
		green = (bin.getRGB(i,j)>>8)&0xff;
		blue = (bin.getRGB(i,j))&0xff;
		v[i][j] = red*0.299 + green*0.587 + blue*0.114 ;
	    }

	if(withBlur) {
	    double [][] vx = new double[w][h];
	    double [][] vy = new double[w][h];
	    double [][] vxx = new double[w][h];
	    double [][] vyy = new double[w][h];
	    
	    for(int iter = 0; iter < nbIter; iter++ ) {
		for(int i = 1 ; i < w-1; i++ ) {
		    // j = 0
		    u[i][0] = (v[i-1][0]+v[i][0]+v[i+1][0]+v[i-1][1]+v[i][1]+v[i+1][1])/6.0;
		    // j = h-1
		    u[i][h-1] = (v[i-1][h-2]+v[i][h-2]+v[i+1][h-2]+v[i-1][h-1]+v[i][h-1]+v[i+1][h-1])/6.0;
		    for(int j = 1; j < h-1; j++) 
			u[i][j] = (v[i][j-1]+v[i][j]+v[i][j+1]+v[i-1][j-1]+v[i-1][j]+v[i-1][j+1]+v[i+1][j-1]+v[i+1][j]+v[i+1][j+1])/9.0;
		}
		for(int j = 1; j < h-1; j++) {
		    // i = 0
		    u[0][j] = (v[0][j]+v[0][j-1]+v[0][j+1]+v[1][j]+v[1][j-1]+v[1][j+1])/6.0;
		    // i = w-1
		    u[w-1][j] = (v[w-1][j]+v[w-1][j-1]+v[w-1][j+1]+v[w-2][j]+v[w-2][j-1]+v[w-2][j+1])/6.0;
		}
		u[0][0] = 0.25*(v[0][0]+v[1][1]+v[0][1]+v[1][0]);
		u[w-1][h-1] = 0.25*(u[w-1][h-1] + u[w-2][h-1] + u[w-1][h-2] + u[w-2][h-2]);
		
		// initialisation of the derivatives
		for(int j = 1; j < h-1; j++) {
		    for(int i = 1 ; i < w-1; i++ ) {
			ux[i][j] = 0.5*(u[i+1][j]-u[i-1][j]);
			uxx[i][j] = 0.25*(u[i+1][j]+u[i-1][j]-2*u[i][j]);
			vx[i][j] = 0.5*(v[i+1][j]-v[i-1][j]);
		    }
		}
		for(int i = 1 ; i < w-1; i++ ) {
		    for(int j = 1; j < h-1; j++) {
			uy[i][j] = 0.5*(u[i][j+1]-u[i][j-1]);
			uyy[i][j] = 0.25*(u[i][j+1]+u[i][j-1]-2*u[i][j]);
			vy[i][j] = 0.5*(v[i][j+1]-v[i][j-1]);
		    }
		}
		
		// evolution
		double toto; double sqrtUV;

		for(int i = 1 ; i < w-1; i++ ) {
		    for(int j = 1; j < h-1; j++) {
			if(pureGaussian)
			    v[i][j] += evolStep*(uxx[i][j]+uyy[i][j]);
			else {
			    //c(s) (Ixx + Iyy) + c'(s)*d/dx(|nabla I|^2) Ix + c'(s)*d/dy(|nabla I|^2) Iy
			    double sp1 = 1 + ux[i][j]*ux[i][j]+uy[i][j]*uy[i][j];
			    toto = (vxx[i][j]+vyy[i][j])/sp1
				- 0.5*vx[i][j]/(sp1*sp1)*(ux[i+1][j]*ux[i+1][j]+uy[i+1][j]*uy[i+1][j]-ux[i-1][j]*ux[i-1][j]+uy[i-1][j]*uy[i-1][j] )
				- 0.5*vy[i][j]/(sp1*sp1)*(ux[i][j+1]*ux[i][j+1]+uy[i][j+1]*uy[i][j+1]-ux[i][j-1]*ux[i][j-1]+uy[i][j-1]*uy[i][j-1]);
			    v[i][j] += evolStep*toto;
			}
		    }
		}
	    }
	}
	// WITHOUT BLUR
	else {
	    double [][] uxy = new double[w][h];
	    
	    for(int iter=0; iter < nbIter; iter++) {
		u = v;
		// initialisation of the derivatives
		for(int j = 0; j < h; j++) {
		    // special case i = 0
		    ux[0][j] = u[1][j]-u[0][j]; uxx[0][j] = 0.5*(u[1][j]-u[0][j]);
		    // special case i = w-1
		    ux[w-1][j] = u[w-1][j] - u[w-2][j]; uxx[w-1][j] = 0.5*(u[w-1][j] - u[w-2][j]);
		    for(int i = 1 ; i < w-1; i++ ) {
			ux[i][j] = 0.5*(u[i+1][j]-u[i-1][j]);
			uxx[i][j] = 0.25*(u[i+1][j]+u[i-1][j]-2*u[i][j]);
		    }
		}
		for(int i = 0 ; i < w; i++ ) {
		    //special case j=0
		    uy[i][0] = u[i][1]-u[i][0]; uyy[i][0] = 0.5*(u[i][1]-u[i][0]);
		    //special case j=h-1
		    uy[i][h-1] = u[i][h-1]-u[i][h-2]; uyy[i][h-1] = 0.5*(u[i][h-1]-u[i][h-2]);
		    for(int j = 1; j < h-1; j++) {
			uy[i][j] = 0.5*(u[i][j+1]-u[i][j-1]);
			uyy[i][j] = 0.25*(u[i][j+1]+u[i][j-1]-2*u[i][j]);
		    }
		}
		uxy[0][0] = 0.25*(u[1][1]-u[0][1]-u[1][0]+u[0][0]);
		uxy[w-1][h-1] = 0.25*(u[w-1][h-1]-u[w-2][h-1]-u[w-1][h-2]+u[w-2][h-2]);
		for(int i = 1 ; i < w-1; i++ ) {
		    // j = 0
		    uxy[i][0] = 0.25*(u[i+1][1]-u[i+1][0]-u[i-1][1]-u[i-1][0]);
		    // j = h-1
		    uxy[i][h-1] = 0.25*(u[i+1][h-1]-u[i+1][h-2]-u[i-1][h-1]-u[i-1][h-2]);
		    for(int j = 1; j < h-1; j++) 
			uxy[i][j] = 0.25*(u[i+1][j+1]-u[i+1][j-1]-u[i-1][j+1]-u[i-1][j-1]);
		}
		for(int j = 1; j < h-1; j++) {
		    // i = 0
		    uxy[0][j] = 0.25*(u[1][j+1]-u[1][j-1]-u[0][j+1]-u[0][j-1]);
		    // i = w-1
		    uxy[w-1][j] = 0.25*(u[w-1][j+1]-u[w-1][j-1]-u[w-2][j+1]-u[w-2][j-1]);
		}
		
		// evolution
		for(int i = 0 ; i < w; i++ ) {
		    for(int j = 0; j < h; j++) {
			if(pureGaussian)
			    v[i][j] += evolStep*(uxx[i][j]+uyy[i][j]);
			else {
			    double toto = (uyy[i][j]*(1+ux[i][j]*ux[i][j]) + uxx[i][j]*(1+uy[i][j]*uy[i][j]) -2*ux[i][j]*uy[i][j]*uxy[i][j])*Math.pow(ux[i][j]*ux[i][j]+uy[i][j]*uy[i][j]+1,-1.5);
			    v[i][j] += evolStep*toto;
			}
		    }
		}
	    }
	}
	// writing the result
	for(int i = 0 ; i < w; i++ )
	    for(int j = 0; j < h; j++) {
		iu = (int) (0.5+v[i][j]);		    
		iu = (iu<0)? 0 : ((iu<256)? iu:255);
		bout.setRGB(i,j,iu*65793);
	    }
	
	return bout;
	
    }
}
