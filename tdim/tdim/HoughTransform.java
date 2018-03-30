package tdim;

import java.awt.image.BufferedImage;
import java.awt.Color;

public class HoughTransform extends ImageTransform {
    protected static final int CARTESIAN = 0;
    protected static final int POLAR = 1;
    protected static final String [] NAMES = {"Cartesian","Polar"};

    protected int type;

    public HoughTransform(int t) {
	super();
	type = t;
	name = NAMES[type] + " Hough transform";
    }
    
    public BufferedImage filter(BufferedImage bin) {
	int w = bin.getWidth();
	int h = bin.getHeight();
	BufferedImage bout;


	if(type == POLAR) {
	    int THETA_SIZE = 90;
	    int TFAC = 360/THETA_SIZE;
	    int RFAC = 10;
	    int RSIZE = (w+h)/RFAC;
	    
	    int rho = 0;
	    int pixel = 0;
	    int [][] score = new int[THETA_SIZE][w+h];
	    int SMAX = 300;

	    bout = new BufferedImage(THETA_SIZE,RSIZE,BufferedImage.TYPE_INT_RGB);

	    // calcul du tableau de scores
	    for(int i = 0 ; i < w ; i++ ) {
		for( int j = 0 ; j < h ; j++ ) {
		    pixel = (new Color(bin.getRGB(i,j))).getRed();
		    if(pixel == 255) {
			for(int t = 0 ; t < THETA_SIZE ; t ++) {
			    rho = (int) (((i*Math.cos(t*TFAC*Math.PI/360.0)+j*Math.sin(t*TFAC*Math.PI/360.0)))/(2.0*RFAC)) + RSIZE/2;
			    rho = Math.max(0,Math.min(RSIZE-1,rho));
			    score[t][rho]++;
			}
		    }
		}
	    }
	    // recherche du max dans le tableau
	    int max = score[0][0];
	    int tmax = 0;
	    int rmax = 0;
	    int nbDroites = 0;
	    for(int t = 0 ; t < THETA_SIZE ; t ++) {
		for(int r = 0 ; r < w+h ; r ++) {
		    if(score[t][r] > max) {
			max = score[t][r];
			rmax = r;
			tmax = t;
		    }}}

	    
	    // trace de l'image de Hough
	    for(int r = 0; r < RSIZE ; r++)
		for(int t = 0 ; t < THETA_SIZE ; t ++)
		    bout.setRGB(tmax,rmax,max*65793);
	
	    
	}
	else { // CARTESIAN
	    // intervalle des valeurs de a : [-10 ; +10]
	    // intervalle des valeurs de A : [0;A_SIZE]
	    // a = ARES + A*AFAC
	    int A_SIZE = 800;
	    double AFAC = 20.0/A_SIZE;
	    int ARES = -10;
	    
	    // intervalle des valeurs de b : [-w; h+10*w]
	    // intervalle des valeurs de B : [0;B_SIZE]
	    // b = BRES + B*BFAC
	    int B_SIZE = 800;
	    double BFAC = (h+20.0*w)/B_SIZE;
	    int BRES = -w*10;
	    
	    int bo = 0;
	    int pixel = 0;
	    int [][] score = new int[A_SIZE][B_SIZE];
	    int SMAX = 400;
	    
	    // calcul du tableau de scores
	    for(int i = 0 ; i < w ; i++ ) {
		for( int j = 0 ; j < h ; j++ ) {
		    pixel = (bin.getRGB(i,j)>>16)&0xff;
		    if(pixel == 255) {
			for(int a = 0 ; a < A_SIZE ; a ++) {
			    bo = (int) (((j - (AFAC*a+ARES) * i)-BRES)/BFAC);
			    bo = Math.max(0,Math.min(B_SIZE-1,bo));
			    score[a][bo]++;
			}}}}
	    // recherche du max dans le tableau
	    int max = score[0][0];
	    double amax = 0;
	    double bmax = 0;
	    int nbDroites = 0;
	    
	    for(int a = 0 ; a < A_SIZE ; a ++) {
		for(int b = 0 ; b < B_SIZE ; b ++) {
		    if( score[a][b] > max) {
			max = score[a][b];
			amax = a;
			bmax = b;
		    }}}

	    bout = new BufferedImage(A_SIZE,B_SIZE,BufferedImage.TYPE_INT_RGB);
	    
	    for(int a = 0; a < A_SIZE ; a++)
		for(int b = 0 ; b < B_SIZE ; b ++)
		    bout.setRGB((int) amax,(int) bmax,max*65793);
	    
	}
	return bout;
    }
}
