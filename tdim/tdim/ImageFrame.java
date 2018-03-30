package tdim;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;

public class ImageFrame extends JFrame {
    public static int imageFrameOpened = 0;
    protected JLabel label;
    protected JScrollPane scrollPane;
    
    protected FrameParameters fparams;
    protected ImageTD imageTD;
    protected ImageCanvas imageCanvas;

    public ImageFrame(ImageTD itd, String title, String list) {
	this(itd, title);
	try {
	    File f = new File(list);
	    BufferedImage bio = ImageIO.read(f);
	    fparams.historic += "image " + f + " opened \n";
	    imageCanvas.setImage(bio);	
	}
	catch(IOException e) { System.out.println("Unable to open file " + list); }
	// add the opening of all files in the String [] list
    }    

    public ImageFrame(ImageTD itd, String title) {
	this(itd, title,(FrameParameters) null);
    }

    public ImageFrame(ImageTD itd, String title, FrameParameters params) {
	super(title);
	imageTD = itd;
	setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	imageFrameOpened ++;

	ImageMenuBar imb = new ImageMenuBar(this);
	setJMenuBar(imb);
	label = new JLabel("Welcome");
	getContentPane().add(label, BorderLayout.NORTH);

	imageCanvas = new ImageCanvas(this);
	scrollPane = new JScrollPane(imageCanvas);
	scrollPane.setPreferredSize(new Dimension(500, 400));
	getContentPane().add(scrollPane, BorderLayout.CENTER);

	if(params == null)
	    fparams = new FrameParameters();
	else
	    fparams = new FrameParameters(params);
    }

    public void doNTransform(ImageTransform it) {
	if(imageCanvas.bi == null)
	    return;
	BufferedImage btemp = imageCanvas.bi;
	BufferedImage bt = btemp;
	if(fparams.newWindow) {
	    ImageFrame nif = new ImageFrame(imageTD,""+fparams.iterate+" times :" + it,fparams);
	    nif.fparams.historic += fparams.iterate+" times :" + it +"\n";
	    BufferedImage bn;
	    for(int i = 0; i < fparams.iterate ; i++) {
		bt = it.filter(btemp);
		btemp = bt;
	    }
	    bn = btemp;
	    nif.imageCanvas.setImage(bn);
	    nif.pack();
	    nif.setVisible(true);
	}
	else {
	    fparams.historic += fparams.iterate+" times :" + it+"\n";
	    for(int i = 0; i < fparams.iterate ; i++) {
		bt = it.filter(btemp);
		btemp = bt;
	    }
	    imageCanvas.setImage(btemp);
	}
    }

    // for operators valid only q time
    public void doTransform(ImageTransform it) {
	if(imageCanvas.bi == null)
	    return;
	if(fparams.newWindow) {
	    ImageFrame nif = new ImageFrame(imageTD,"" + it,fparams);
	    nif.fparams.historic += it +"\n";
	    nif.imageCanvas.setImage(it.filter(imageCanvas.bi));
	    nif.pack();
	    nif.setVisible(true);
	}
	else {
	    fparams.historic +=  it+"\n";
	    imageCanvas.setImage(it.filter(imageCanvas.bi));
	}
    }

    public void quit() {
	imageFrameOpened --;
	dispose();
	if (imageFrameOpened < 1 ) {
	    System.out.println("Bye bye !");
	    System.exit(0);
	}
    }

    public void setNewWindow(boolean b) {
	fparams.newWindow = b;
    }
    public void setInterpolationMode(int i) {
	fparams.interpolationMode = i;
    }
    public void setInterpolationMode(int i, double b, double c) {
	fparams.interpolationMode = i;
	fparams.b = b;
	fparams.c = c;
    }
    public void setMorphoKernel(int i) {
	fparams.morphoKernel = i;
    }
    public void setDericheAlpha(double a) {
	fparams.alpha = a;
    }
    public void setEvolutionStep(double h) {
	fparams.h = h;
    }
    public void setIterate(int i) {
	fparams.iterate = i;
    }
    public void setFatBits(boolean b) {
	fparams.fatBits = b;
	imageCanvas.setFatBits(b);
	repaint();
    }
    public boolean isFatBits() {
	return fparams.fatBits;
    }
}

/**
 * class englobing ImageFrame parameters that must be propagated to new ImageFrame
 * created from this one
 **/

class FrameParameters {
    protected String historic; // historic of transformations
    protected boolean newWindow; // new result in new window ?
    protected int interpolationMode;
    protected double b = 0; // used in case of interpolation of order 3
    protected double c = 0; // used in case of interpolation of order 3
    protected int morphoKernel; // which kernel to use in morphomathematical operations
    protected boolean fatBits; // true : display fat pixels, false : normal display
    protected double alpha; // alpha coefficient for Deriche filters
    protected double h; // evolution step for evolution algorithms (gaussian, Perona Malik, ...)
    protected int iterate; // how many times do we repeat this operation ?

    public FrameParameters(String sh, boolean nw, int im, double b, double c, int mk, boolean fb, double a, double _h, int it) {
	historic = sh;
	newWindow = nw;
	interpolationMode = im;
	this.b = b;
	this.c = c;
	morphoKernel = mk;
	fatBits = fb;
	alpha = a;
	h = _h;
	iterate = it;
    }
    public FrameParameters(FrameParameters fp) {
	this(fp.historic, fp.newWindow, fp.interpolationMode, fp.b, fp.c, fp.morphoKernel, fp.fatBits,fp.alpha,fp.h,fp.iterate);
    }
    public FrameParameters() {
	this("",true,0,0.0,0.0,0,false,3,0.5,1);
    }
    public String toString() {
	String res = "";
	
	res += "Historic of transformations:\n" + historic + "\n";
	if(newWindow)
	    res += "new transformation in new window\n";
	else
	    res += "new transformation in this window\n";
	res+= "interpolation mode : " + TwoDTransform.ORDERS[interpolationMode] + "\n";
	if(interpolationMode == TwoDTransform.ORDER3)
	    res += " with B = " + b + " and C = " + c + "\n";
	res+= "morphological kernel : " + MorphoTransform.KERNELS[morphoKernel] + "\n";
	res+= "fat bits mode ";
	if(fatBits) res+= "enabled\n";
	else res+= "disabled\n";
	res+= "alpha coefficient for Deriche filters = " + alpha + "\n";
	res+= "evolution step = " + h + "\n";
	res+= "each operation will be repeated " + iterate + " times.\n";
	return res;
    }
}
