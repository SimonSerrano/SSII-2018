package tdim;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.io.*;
import javax.imageio.ImageIO;

public class ImageMenuBar extends JMenuBar {
    protected static final int JPG=0, PNG=1, PPM_ascii=2, PPM_binary=3, SVG = 4, PS = 5;
    protected static final int RED=0, GREEN=1, BLUE=2, GREY=3, MACH=4, RGB3=5, RGB5=6;
    protected static final String [] FORMAT = {"jpg","png","ppm (ascii)","ppm (binary)"};
    private String curDir = "./";
    private ImageFrame parentFrame;

    public ImageMenuBar(ImageFrame ifp) {
	super();
	parentFrame = ifp;

	// creating menus
	JMenu jmFile = createFileMenu();
	JMenu jmEdit = createEditMenu();
	JMenu jmUnaryTransforms = createUnaryTransformsMenu();
        JMenu jmBinaryTransforms = createBinaryTransformsMenu();
        JMenu jmHelp = createHelpMenu();

        // linking menus
        add(jmFile);
        add(jmEdit);
        add(jmUnaryTransforms);
        add(jmBinaryTransforms);
        add(jmHelp);
    }

    protected JMenu createFileMenu() {

	JMenu fm = new JMenu("File");
	JMenuItem loadImage = new JMenuItem("Load image from file ...");
	JMenu openImage = new JMenu("Load image");
	JMenuItem saveImage = new JMenuItem("Save image in file ...");
	JMenuItem quit = new JMenuItem("Quit");

	fm.add(loadImage);
	fm.add(openImage);
	fm.add(saveImage);
	fm.add(quit);
	
	// JMenuItem loadImage
	loadImage.setAccelerator(KeyStroke.getKeyStroke("alt O"));
	loadImage.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e){ loadImage(); }
		});

	// JMenu openImage
	JMenuItem grey = new JMenuItem("grey gradient");
	JMenuItem red = new JMenuItem("red gradient");
	JMenuItem blue = new JMenuItem("blue gradient");
	JMenuItem green = new JMenuItem("green gradient");
	JMenuItem machBand = new JMenuItem("Mach Band");
	JMenuItem jmiRgb3 = new JMenuItem("Red(1) Green(1) Blue(1) (width=3)");
	JMenuItem jmiRgb5 = new JMenuItem("Red(2) Green(2) Blue(1) (width=5)");
	openImage.add(grey);
	grey.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e){ openImage(GREY);}});
	openImage.add(red);
	red.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e){ openImage(RED);}});
	openImage.add(blue);
	blue.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e){ openImage(BLUE);}});
	openImage.add(green);
	green.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e){ openImage(GREEN);}});
	openImage.add(new JSeparator());
	openImage.add(machBand);
	machBand.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e){ openImage(MACH);}});
	openImage.add(new JSeparator());
	openImage.add(jmiRgb3);
	jmiRgb3.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e){ openImage(RGB3);}});
	openImage.add(jmiRgb5);
	jmiRgb5.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e){ openImage(RGB5);}});

	// JMenuItem saveImage
	saveImage.setAccelerator(KeyStroke.getKeyStroke("alt S"));
	saveImage.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e){ saveImage(); }
		});

	// JMenuItem quit
	quit.setAccelerator(KeyStroke.getKeyStroke("alt Q"));
	quit.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e){ parentFrame.quit();}
	    });


	return fm;
    }

    protected JMenu createEditMenu() {
	JMenu em = new JMenu("Edit");
	
	JMenuItem jmiSetAsSelected = new JMenuItem("Set this Image as selected");
	JMenu jmiSetInterpolationMode = new JMenu("Set interpolation mode");
	JMenu jmiSetMorphoKernel = new JMenu("Set morphomathematical kernel");
	JMenu jmiSetWindowOption = new JMenu("Set window option");
	JMenu jmiSetFatBitMode = new JMenu("Set fat bits mode as ...");
	JMenuItem jmiSetDericheAlpha = new JMenuItem("Set alpha coefficient for Deriche filters");
	JMenuItem jmiSetEvolutionStep = new JMenuItem("Set the evolution step for evolutive algorithms");
	JMenuItem jmiSetIterate = new JMenuItem("Set the iteration number (default = 1)");
	em.add(jmiSetAsSelected);
	em.add(jmiSetInterpolationMode);
	em.add(jmiSetMorphoKernel);
	em.add(jmiSetWindowOption);
	em.add(jmiSetFatBitMode);
	em.add(jmiSetDericheAlpha);
	em.add(jmiSetEvolutionStep);
	em.add(jmiSetIterate);

	// jmiSetAsSelected
	jmiSetAsSelected.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) { 
		    parentFrame.imageTD.setSelected(parentFrame.imageCanvas.bi);
		}});

	// jmiSetInterpolationMode
	JMenuItem [] jmiOrders = new JMenuItem[5];
	for(int i = 0; i < jmiOrders.length; i++) {
	    jmiOrders[i] = new JMenuItem(TwoDTransform.ORDERS[i]);
	    jmiSetInterpolationMode.add(jmiOrders[i]);
	    jmiOrders[i].addActionListener(new InterpolationAL(parentFrame,i));
	}
	JMenuItem jmiOrder3 = new JMenuItem("third order (B= ... ) (C= ...)");
	jmiSetInterpolationMode.add(jmiOrder3);
	jmiOrder3.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    Order3Interpolation oi = new Order3Interpolation(parentFrame);
		    double [] params = oi.getParams();
		    if(params != null)
			parentFrame.setInterpolationMode(TwoDTransform.ORDER3, params[0], params[1]);
		}});


	// jmiSetMorphoKernel
	JMenuItem jmiKv = new JMenuItem("Vertical");
	JMenuItem jmiKh = new JMenuItem("Horizontal");
	JMenuItem jmiKc = new JMenuItem("Cross");
	JMenuItem jmiKs = new JMenuItem("Square");
	jmiSetMorphoKernel.add(jmiKv);
	jmiSetMorphoKernel.add(jmiKh);
	jmiSetMorphoKernel.add(jmiKc);
	jmiSetMorphoKernel.add(jmiKs);
	jmiKv.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {parentFrame.setMorphoKernel(MorphoTransform.VERTICAL);}});
	jmiKh.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {parentFrame.setMorphoKernel(MorphoTransform.HORIZONTAL);}});
	jmiKc.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {parentFrame.setMorphoKernel(MorphoTransform.CROSS);}});
	jmiKs.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {parentFrame.setMorphoKernel(MorphoTransform.SQUARE);}});

	// jmiSetWindowOption
	JMenuItem jmiSameWindow = new JMenuItem("show result in the same window");
	JMenuItem jmiNewWindow = new JMenuItem("show result in a new window");
	jmiSetWindowOption.add(jmiSameWindow);
	jmiSetWindowOption.add(jmiNewWindow);
	jmiSameWindow.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {parentFrame.setNewWindow(false);}});
	jmiNewWindow.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {parentFrame.setNewWindow(true);}});

	// jmiSetFatBitMode
	JMenuItem jmiSetOn = new JMenuItem("on");
	JMenuItem jmiSetOff = new JMenuItem("off");
	jmiSetFatBitMode.add(jmiSetOn);
	jmiSetFatBitMode.add(jmiSetOff);
	jmiSetOn.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {parentFrame.setFatBits(true);}});
	jmiSetOff.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {parentFrame.setFatBits(false);}});

	// jmiSetDericheAlpha
	jmiSetDericheAlpha.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {parentFrame.setDericheAlpha(Double.parseDouble(JOptionPane.showInputDialog(parentFrame, "Alpha coefficient for Deriche filters","alpha",JOptionPane.QUESTION_MESSAGE)));}});

	// jmiSetEvolutionStep
	jmiSetEvolutionStep.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {parentFrame.setEvolutionStep(Double.parseDouble(JOptionPane.showInputDialog(parentFrame, "Evolution Step","h",JOptionPane.QUESTION_MESSAGE)));}});

	// jmiSetIterate
	jmiSetIterate.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {parentFrame.setIterate(Integer.parseInt(JOptionPane.showInputDialog(parentFrame, "How many times do you want to perform an operation ?","Iterations",JOptionPane.QUESTION_MESSAGE)));}});
	
	return em;
    }

    protected JMenu createUnaryTransformsMenu() {
	JMenu ut = new JMenu("Unary Transforms");

	JMenu jmColorComponent = new JMenu("See a color component");
	JMenu jmSimpleTransform = new JMenu("Simple transforms");
	JMenuItem jmQuantification = new JMenuItem("Quantification");
	JMenu jm2Dtransforms = new JMenu("2D transforms");
	JMenu jmEdgeDetection = new JMenu("Edge Detection");
	JMenu jmMorphoOp = new JMenu("Morphomathematical operations");
	JMenu jmRegionSegmentation = new JMenu("Region Segmentation");
	JMenu jmNoise = new JMenu("add Noise to Image");
	JMenu jmRestauration = new JMenu("Restauration");
	ut.add(jmColorComponent);
	ut.add(jmSimpleTransform);
	ut.add(jmQuantification);
	ut.add(jm2Dtransforms);
	ut.add(jmEdgeDetection);
	ut.add(jmMorphoOp);
	ut.add(jmRegionSegmentation);
	ut.add(jmNoise);
	ut.add(jmRestauration);

	jmQuantification.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) { quantif();} });

	// jmColorComponent
	JMenuItem [] jmiColorComp = new JMenuItem[ColorTransform.TRANSF_NAMES.length];

	for(int i = 0; i < ColorTransform.TRANSF_NAMES.length; i++) {
	    if((i>0) && (i%3 == 0)) jmColorComponent.add(new JSeparator());
	    jmiColorComp[i] = new JMenuItem(ColorTransform.TRANSF_NAMES[i]);
	    jmColorComponent.add(jmiColorComp[i]);
	    jmiColorComp[i].addActionListener(new ColorActionListener(parentFrame, i));
	}

	// jmSimpleTransform
	JMenuItem jmiHistogram = new JMenuItem("Histogram");
	jmSimpleTransform.add(jmiHistogram);
	jmiHistogram.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) { 
		    HistogramTransform t = new HistogramTransform();
		    parentFrame.doTransform(t);
		}});
	JMenuItem jmiInverseVideo = new JMenuItem("Inverse Video");
	jmiInverseVideo.setAccelerator(KeyStroke.getKeyStroke("ctrl I"));
	jmSimpleTransform.add(jmiInverseVideo);
	jmiInverseVideo.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) { 
		    SimpleTransform t = new SimpleTransform();
		    parentFrame.doTransform(t);
		}});
	JMenuItem jmiThreshold = new JMenuItem("Thresholding (b>g and b>r)");
	jmSimpleTransform.add(jmiThreshold);
	jmiThreshold.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) { 
		    ThresholdTransform t = new ThresholdTransform();
		    parentFrame.doTransform(t);
		}});

	// jm2Dtransforms
	JMenuItem jmiRotation = new JMenuItem("Rotation");
	JMenuItem jmiRotationXY = new JMenuItem("Rotation with center ...");
	JMenuItem jmiTranslation = new JMenuItem("Translation");
	JMenuItem jmiZoom = new JMenuItem("Zoom");
	JMenuItem jmiZoomXY = new JMenuItem("Zoom with center ...");
	JMenuItem jmiScale = new JMenuItem("Scale");
	jm2Dtransforms.add(jmiRotation);
	jm2Dtransforms.add(jmiRotationXY);
	jm2Dtransforms.add(jmiTranslation);
	jm2Dtransforms.add(jmiZoom);
	jm2Dtransforms.add(jmiZoomXY);
	jm2Dtransforms.add(jmiScale);
	jmiRotation.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) { rotate(); } });
	jmiRotationXY.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) { rotateXY(); } });
	jmiTranslation.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) { translate(); } });
	jmiZoom.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) { zoom(false); } });
	jmiZoomXY.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) { zoomXY(); } });
	jmiScale.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) { zoom(true); } });
	
	// jmEdgeDetection 
	JMenuItem jmiNaiveX = new JMenuItem("Naive x");
	JMenuItem jmiNaiveY = new JMenuItem("Naive y");
	JMenuItem jmiNaive = new JMenuItem("Naive");
	JMenuItem jmiRobertsX = new JMenuItem("Roberts x");
	JMenuItem jmiRobertsY = new JMenuItem("Roberts y");
	JMenuItem jmiRoberts = new JMenuItem("Roberts");
	JMenuItem jmiSobelX = new JMenuItem("Sobel x");
	JMenuItem jmiSobelY = new JMenuItem("Sobel y");
	JMenuItem jmiSobel = new JMenuItem("Sobel");
	JMenuItem jmiPrewitt = new JMenuItem("Prewitt");
	JMenuItem jmiKirch = new JMenuItem("Kirch");
	JMenuItem jmiLaplace4 = new JMenuItem("Laplace (connexity 4)");
	JMenuItem jmiLaplace8 = new JMenuItem("Laplace (connexity 8)");
	JMenuItem jmiLaplace4Z = new JMenuItem("Zeros of Laplace (connexity 4)");
	JMenuItem jmiLaplace8Z = new JMenuItem("Zeros of Laplace (connexity 8)");
	JMenuItem jmiHuertasMedioni = new JMenuItem("Hertas Medioni");
	JMenu jmiDeriche = new JMenu("Deriche");

	JMenuItem jmiDericheLissage = new JMenuItem("lissage");
	JMenuItem jmiDericheDerivX = new JMenuItem("derive X");
	JMenuItem jmiDericheDerivY = new JMenuItem("derive Y");
	JMenuItem jmiDericheDerivee= new JMenuItem("derivative norm");
	JMenuItem jmiDericheLaplace1 = new JMenuItem("laplace step 1");
	JMenuItem jmiDericheLaplace2 = new JMenuItem("laplace step 2");
	JMenuItem jmiDericheLaplace = new JMenuItem("Laplace all");
	JMenuItem jmiDericheLaplaceSign = new JMenuItem("Laplace sign");
	JMenuItem jmiDericheLaplaceZeros = new JMenuItem("Laplace zeros");
	jmiDeriche.add(jmiDericheLissage);
	jmiDeriche.add(new JSeparator());
	jmiDeriche.add(jmiDericheDerivX);
	jmiDeriche.add(jmiDericheDerivY);
	jmiDeriche.add(jmiDericheDerivee);
	jmiDeriche.add(new JSeparator());
	jmiDeriche.add(jmiDericheLaplace1);
	jmiDeriche.add(jmiDericheLaplace2);
	jmiDeriche.add(jmiDericheLaplace);
	jmiDeriche.add(jmiDericheLaplaceSign);
	jmiDeriche.add(jmiDericheLaplaceZeros);

	JMenuItem jmiCustom = new JMenuItem("Customized filter ...");
	jmEdgeDetection.add(jmiNaiveX);
	jmEdgeDetection.add(jmiNaiveY);
	jmEdgeDetection.add(jmiNaive);
	jmEdgeDetection.add(new JSeparator());
	jmEdgeDetection.add(jmiRobertsX);
	jmEdgeDetection.add(jmiRobertsY);
	jmEdgeDetection.add(jmiRoberts);
	jmEdgeDetection.add(new JSeparator());
	jmEdgeDetection.add(jmiSobelX);
	jmEdgeDetection.add(jmiSobelY);
	jmEdgeDetection.add(jmiSobel);
	jmEdgeDetection.add(new JSeparator());
	jmEdgeDetection.add(jmiPrewitt);
	jmEdgeDetection.add(jmiKirch);
	jmEdgeDetection.add(new JSeparator());
	jmEdgeDetection.add(new JSeparator());
	jmEdgeDetection.add(jmiLaplace4);
	jmEdgeDetection.add(jmiLaplace8);
	jmEdgeDetection.add(jmiLaplace4Z);
	jmEdgeDetection.add(jmiLaplace8Z);
	jmEdgeDetection.add(jmiHuertasMedioni);
	jmEdgeDetection.add(new JSeparator());
	jmEdgeDetection.add(jmiDeriche);
	jmEdgeDetection.add(new JSeparator());
	jmEdgeDetection.add(jmiCustom);

	jmiNaiveX.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) { 
		    ConvolutionFilter t = new ConvolutionFilter(ConvolutionFilter.NAIVE_X);
		    parentFrame.doTransform(t);
		}});
	jmiNaiveY.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) { 
		    ConvolutionFilter t = new ConvolutionFilter(ConvolutionFilter.NAIVE_Y);
		    parentFrame.doTransform(t);
		}});
	jmiNaive.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) { 
		    ConvolutionFilter t = new ConvolutionFilter(ConvolutionFilter.NAIVE);
		    parentFrame.doTransform(t);
		}});
	jmiRobertsX.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) { 
		    ConvolutionFilter t = new ConvolutionFilter(ConvolutionFilter.ROBERTS_X);
		    parentFrame.doTransform(t);
		}});
	jmiRobertsY.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) { 
		    ConvolutionFilter t = new ConvolutionFilter(ConvolutionFilter.ROBERTS_Y);
		    parentFrame.doTransform(t);
		}});
	jmiRoberts.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) { 
		    ConvolutionFilter t = new ConvolutionFilter(ConvolutionFilter.ROBERTS);
		    parentFrame.doTransform(t);
		}});
	jmiSobelX.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) { 
		    ConvolutionFilter t = new ConvolutionFilter(ConvolutionFilter.SOBEL_X);
		    parentFrame.doTransform(t);
		}});
	jmiSobelY.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) { 
		    ConvolutionFilter t = new ConvolutionFilter(ConvolutionFilter.SOBEL_Y);
		    parentFrame.doTransform(t);
		}});
	jmiSobel.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) { 
		    ConvolutionFilter t = new ConvolutionFilter(ConvolutionFilter.SOBEL);
		    parentFrame.doTransform(t);
		}});
	jmiPrewitt.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) { 
		    ConvolutionFilter t = new ConvolutionFilter(ConvolutionFilter.PREWITT);
		    parentFrame.doTransform(t);
		}});
	jmiKirch.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) { 
		    ConvolutionFilter t = new ConvolutionFilter(ConvolutionFilter.KIRSCH);
		    parentFrame.doTransform(t);
		}});
	jmiLaplace4.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) { 
		    ConvolutionFilter t = new ConvolutionFilter(ConvolutionFilter.LAPLACE4);
		    parentFrame.doTransform(t);
		}});
	jmiLaplace8.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) { 
		    ConvolutionFilter t = new ConvolutionFilter(ConvolutionFilter.LAPLACE8);
		    parentFrame.doTransform(t);
		}});
	jmiLaplace4Z.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) { 
		    ConvolutionFilter t = new ConvolutionFilter(ConvolutionFilter.LAPLACE4_ZEROS);
		    parentFrame.doTransform(t);
		}});
	jmiLaplace8Z.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) { 
		    ConvolutionFilter t = new ConvolutionFilter(ConvolutionFilter.LAPLACE8_ZEROS);
		    parentFrame.doTransform(t);
		}});
	jmiHuertasMedioni.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) { 
		    OtherEdgeTr tr = new OtherEdgeTr(OtherEdgeTr.HUERTAS_MEDIONI);
		    parentFrame.doTransform(tr);
		}});
	jmiDeriche.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) { 
		    JOptionPane.showMessageDialog(parentFrame,"Sorry, not yet implemented.");}});

	// Deriche
	jmiDericheLissage.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) { 
		    DericheTransform tr = new DericheTransform(parentFrame, DericheTransform.LISSAGE);
		    parentFrame.doNTransform(tr);
		}});
	jmiDericheDerivX.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) { 
		    DericheTransform tr = new DericheTransform(parentFrame, DericheTransform.DERIV_X);
		    parentFrame.doNTransform(tr);
		}});
	jmiDericheDerivY.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) { 
		    DericheTransform tr = new DericheTransform(parentFrame, DericheTransform.DERIV_Y);
		    parentFrame.doNTransform(tr);
		}}); 
	jmiDericheLaplace1.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) { 
		    DericheTransform tr = new DericheTransform(parentFrame, DericheTransform.LAPLACE_1);
		    parentFrame.doNTransform(tr);
		}});
	jmiDericheLaplace2.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) { 
		    DericheTransform tr = new DericheTransform(parentFrame, DericheTransform.LAPLACE_2);
		    parentFrame.doNTransform(tr);
		}});
	jmiDericheDerivee.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) { 
		    DericheTransform tr = new DericheTransform(parentFrame, DericheTransform.DERIVEE);
		    parentFrame.doNTransform(tr);
		}});
	jmiDericheLaplace.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) { 
		    DericheTransform tr = new DericheTransform(parentFrame, DericheTransform.LAPLACE);
		    parentFrame.doNTransform(tr);
		}});
	jmiDericheLaplaceSign.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) { 
		    DericheTransform tr = new DericheTransform(parentFrame, DericheTransform.LAPLACE_SIGN);
		    parentFrame.doNTransform(tr);
		}});
	jmiDericheLaplaceZeros.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) { 
		    DericheTransform tr = new DericheTransform(parentFrame, DericheTransform.LAPLACE_ZEROS);
		    parentFrame.doNTransform(tr);
		}});

	// jmiCustom
	jmiCustom.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) { customFilter(); } });


	// jmMorphoOp
	JMenuItem [] morpho = new JMenuItem[6];
	for(int i = 0; i < morpho.length; i++) {
	    morpho[i] = new JMenuItem(MorphoTransform.NAMES[i]);
	    jmMorphoOp.add(morpho[i]);
	    morpho[i].addActionListener(new MorphoActionListener(parentFrame,i));
	}

	// jmRegionSegmentation

	// jmNoise
	JMenuItem jmiGaussian  = new JMenuItem("Gaussian additive noise");
	JMenuItem jmiSaltAndPepper = new JMenuItem("Salt and Pepper noise");
	jmNoise.add(jmiGaussian);
	jmNoise.add(jmiSaltAndPepper);
	jmiGaussian.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) { gaussianNoise(); } });
	jmiSaltAndPepper.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) { saltAndPepperNoise(); } });

	// jmRestauration
	JMenuItem jmiMeanFilter  = new JMenuItem("Mean Filter");
	JMenuItem jmiGaussianFilter  = new JMenuItem("Gaussian Filter");
	JMenuItem jmiConservativeFilter  = new JMenuItem("Conservative Filter");
	JMenuItem jmiMedianFilter  = new JMenuItem("Median Filter");
	JMenuItem jmiSharpeningFilter  = new JMenuItem("Sharpening Filter");
	JMenuItem jmiPureGaussian = new JMenuItem("Evolutive Gaussian without blur");
	JMenuItem jmiPureGaussianB = new JMenuItem("Evolutive Gaussian with blur");
	JMenuItem jmiPeronaMalik = new JMenuItem("Perona Malik without blur");
	JMenuItem jmiPeronaMalikB = new JMenuItem("Perona Malik with blur");

	jmRestauration.add(jmiMeanFilter);
	jmiMeanFilter.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) { selectMeanFilter(); }});
	jmRestauration.add(jmiGaussianFilter);
	jmiGaussianFilter.add(jmiSharpeningFilter);
	jmiGaussianFilter.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) { selectGaussianFilter(); }});

	jmRestauration.add(jmiConservativeFilter);
	jmiConservativeFilter.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) { 
		    selectConservativeMedianFilter(RestaurationFilters.CONSERVATIVE);	}});
	jmRestauration.add(jmiMedianFilter);
	jmiMedianFilter.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) { 
		    selectConservativeMedianFilter(RestaurationFilters.MEDIAN);	}});
	jmRestauration.add(jmiMedianFilter);
	
	jmRestauration.add(new JSeparator());
	jmRestauration.add(jmiSharpeningFilter);
	jmiSharpeningFilter.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) { selectSharpeningFilter(); }});
	jmRestauration.add(new JSeparator());
	jmRestauration.add(jmiPureGaussian);
	jmiPureGaussian.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) { 
		    PeronaMalikEvolution tr = new PeronaMalikEvolution(parentFrame,PeronaMalikEvolution.PURE_G);
		    parentFrame.doTransform(tr); }});
	jmRestauration.add(jmiPureGaussianB);
	jmiPureGaussianB.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) { 
		    PeronaMalikEvolution tr = new PeronaMalikEvolution(parentFrame,PeronaMalikEvolution.PURE_G_B);
		    parentFrame.doTransform(tr); }});
	jmRestauration.add(jmiPeronaMalik);
	jmiPeronaMalik.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) { 
		    PeronaMalikEvolution tr = new PeronaMalikEvolution(parentFrame,PeronaMalikEvolution.PM);
		    parentFrame.doTransform(tr); }});
	jmRestauration.add(jmiPeronaMalikB);
	jmiPeronaMalikB.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) { 
		    PeronaMalikEvolution tr = new PeronaMalikEvolution(parentFrame,PeronaMalikEvolution.PM_B);
		    parentFrame.doTransform(tr); }});
	return ut;
    }

    protected void selectConservativeMedianFilter(int type) {
	Object[] possibilities = {"3x3","5x5","7x7","9x9"};  

	String s = (String)JOptionPane.showInputDialog(
                    this,
                    "Select the size of your filter : ",
		    "Mean filter",
		    JOptionPane.PLAIN_MESSAGE,
		    null,
		    possibilities,
		    "3x3");
 
	if ((s != null) && (s.length() > 0)) {
	    int size = (int) ( s.charAt(0)-'0');

	    Object[] voisinages = {"cross","diamand","square"};
	    String s2 = (String)JOptionPane.showInputDialog(
                    this,"Select the type of neighborough size of your mean filter : ",
		    "Neighborough", JOptionPane.PLAIN_MESSAGE, null,
		    voisinages, "square");
	    if ((s2 != null) && (s2.length() > 0)) {
		int nh = RestaurationFilters.SQUARE;
		switch(s2.length()) {
		case 5: /*cross*/ nh = RestaurationFilters.CROSS; break;
		case 6: /*square*/ nh = RestaurationFilters.SQUARE; break;
		case 7: /*diamand*/ nh = RestaurationFilters.DIAMAND; break;
		}
		RestaurationFilters rf = new RestaurationFilters(type,nh,size);
		parentFrame.doNTransform(rf);
	    }
	}
    }
     protected void selectMeanFilter() {
	Object[] possibilities = {"3x3","5x5","7x7","9x9"};  

	String s = (String)JOptionPane.showInputDialog(
                    this,
                    "Select the size of your mean filter : ",
		    "Mean filter",
		    JOptionPane.PLAIN_MESSAGE,
		    null,
		    possibilities,
		    "3x3");
 
	if ((s != null) && (s.length() > 0)) {
	    int size = (int) ( s.charAt(0)-'0');
	    int size2 = size*size;
	    System.out.println(s + " = " + size + "x"+size);
	    double [][] coeff = new double[size][size];
	    for(int i = 0; i < size; i++ )
		for(int j = 0; j < size; j++ )
		    coeff[i][j] = 1.0/size2;
	    ConvolutionFilter t = new ConvolutionFilter(coeff,"mean filter");
	    parentFrame.doNTransform(t);
	}
    }
     protected void selectSharpeningFilter() {
	 //0,-1,0,-1,10,-1,0,-1,0  
	 //1,-2,1,-2,5,-2,1,-2,1
	 //-1,-1,-1,-1,9,-1,-1,-1,-1
	Object[] possibilities = {"1: [0,-1,0][-1,10,-1][0,-1,0]","2:[1,-2,1][-2,5,-2][1,-2,1]","3:[-1,-1,-1][-1,9,-1][-1,-1,-1]"};
	String s = (String)JOptionPane.showInputDialog(
                    this,
                    "Select your sharpening filter : ",
		    "Sharpening filter",
		    JOptionPane.PLAIN_MESSAGE,
		    null,
		    possibilities,
		    "1: [0,-1,0][-1,10,-1][0,-1,0]");
 
	if ((s != null) && (s.length() > 0)) {
	    int size = (int) ( s.charAt(0)-'0'-1);
	    ConvolutionFilter t = null;
	    switch(size) {
	    case 0: t = new ConvolutionFilter(ConvolutionFilter.SHARPEN3a); break;
	    case 1: t = new ConvolutionFilter(ConvolutionFilter.SHARPEN3b); break;
	    case 2: t = new ConvolutionFilter(ConvolutionFilter.SHARPEN3c); break;
	    }
	    parentFrame.doNTransform(t);
	}
     }
    protected void selectGaussianFilter() {
	//1,2,1,2,4,2,1,2,1
	//1,1,2,1,1, 1,2,4,2,1, 2,4,8,4,2, 1,2,4,2,1, 1,1,2,1,1
	//1,1,2,2,2,1,1, 1,2,2,4,2,2,1, 2,2,4,8,4,2,2, 2,4,8,16,8,4,2, 2,2,4,8,4,2,2, 1,2,2,4,2,2,1, 1,1,2,2,2,1,1
	Object[] possibilities = {"3x3","5x5","7x7"};
	String s = (String)JOptionPane.showInputDialog(
                    this,
                    "Select the size of your gaussian filter : ",
		    "Gaussian filter",
		    JOptionPane.PLAIN_MESSAGE,
		    null,
		    possibilities,
		    "3x3");
 
	if ((s != null) && (s.length() > 0)) {
	    ConvolutionFilter t = null;
	    int size = (int) ( s.charAt(0)-'0');
	    switch(size) {
	    case 3: t = new ConvolutionFilter(ConvolutionFilter.GAUSSIAN3); break;
	    case 5: t = new ConvolutionFilter(ConvolutionFilter.GAUSSIAN5); break;
	    case 7: t = new ConvolutionFilter(ConvolutionFilter.GAUSSIAN7); break;
	    }
	    parentFrame.doNTransform(t);
	}
     }
        
   protected void customFilter() {
	Object[] possibilities = {"3x3","5x5","7x7","9x9"};
	String s = (String)JOptionPane.showInputDialog(
                    this,
                    "Select the size of your filter : ",
		    "Custom filter",
		    JOptionPane.PLAIN_MESSAGE,
		    null,
		    possibilities,
		    "3x3");
	if ((s != null) && (s.length() > 0)) {
	    int size = (int) ( s.charAt(0)-'0');
	    System.out.println(s + " = " + size + "x"+size);
	    //continue by asking coefficients
	    CustomizedFilter cf = new CustomizedFilter(parentFrame,size);
	    double [][] coeff = cf.getFilter();
	    
	    // then perform the filter ..
	    for(int i = 0; i < size; i ++) {
		for(int j = 0; j < size; j ++) {
		    System.out.print(coeff[i][j] + " ");
		}
		System.out.println();
	    }
	    ConvolutionFilter t = new ConvolutionFilter(coeff);
	    parentFrame.doNTransform(t);
	}
    }

    protected void quantif() {
	QuantifParamDialog qpd = new QuantifParamDialog(parentFrame);
	int [] qparams = qpd.getParams();
	if(qparams == null)
	    return;
	System.out.println("mode " + qparams[0] + ", red "+ qparams[1] +" bits, green "+ qparams[2] +" bits, blue "+ qparams[3] +" bits.");
	QuantificationTransform qt = new QuantificationTransform(qparams);
	parentFrame.doTransform(qt);
    }

    protected void rotate() {
	String res = JOptionPane.showInputDialog(parentFrame, "Rotation angle (in degrees)","Rotation", JOptionPane.QUESTION_MESSAGE);
	// if cancel is pressed, null is returned
	if(res == null)
	    return;
	double theta = 0.0;;
	boolean bool = false;
	while(!bool) {
	    try {
		theta = Double.parseDouble(res);
		bool=true;
	    }
	    catch(Exception e) {
		res = JOptionPane.showInputDialog(parentFrame, "Rotation angle (in degrees)","Rotation", JOptionPane.ERROR_MESSAGE);
		if(res == null) return;
		bool=false;
	    }
	}
	if(bool) {
	    TwoDTransform tr = new TwoDTransform(parentFrame.fparams,TwoDTransform.ROTATION,theta);
	    parentFrame.doNTransform(tr);
	}
    }
    protected void rotateXY() {
	RotateZoomDialog rd = new RotateZoomDialog(parentFrame, "Angle of Rotation (in degrees)","Rotation");
	double [] params = rd.getParams();
	if(params != null) {
	    TwoDTransform tr = new TwoDTransform(parentFrame.fparams,TwoDTransform.ROTATION,params[0],params[1],params[2]);
	    parentFrame.doNTransform(tr);
	}
    }
    protected void zoomXY() {
	RotateZoomDialog rd = new RotateZoomDialog(parentFrame, "Zoom factor","Zoom");
	double [] params = rd.getParams();
	if(params != null) {
	    TwoDTransform tr = new TwoDTransform(parentFrame.fparams,TwoDTransform.ZOOM,params[0],params[1],params[2]);
	    parentFrame.doNTransform(tr);
	}
    }

    protected void translate() {
	String res = JOptionPane.showInputDialog(parentFrame, "Translation vector in form: tx ty", "Translation", JOptionPane.QUESTION_MESSAGE);
	if(res == null)
	    return;
	int b;
	double tx = 0.0, ty = 0.0;
	boolean bool = false;
	while(!bool) {
	    try {
		b = res.indexOf(' ', 0);
		tx = Double.parseDouble(res.substring(0,b));
		ty = Double.parseDouble(res.substring(b+1,res.length()));
		bool=true;
	    }
	    catch(Exception e) {
		res = JOptionPane.showInputDialog(parentFrame, "Translation vector in form: tx ty", "Translation", JOptionPane.ERROR);	
		if(res == null)
		    return;
		bool = false;
	    }
	}
	if(bool) {
	    TwoDTransform tr = new TwoDTransform(parentFrame.fparams,TwoDTransform.TRANSLATION,tx,ty);
	    parentFrame.doNTransform(tr);
	}
    }

    protected void zoom(boolean changeSize) {
	String res = JOptionPane.showInputDialog(parentFrame, "Zoom factor", "Zoom", JOptionPane.QUESTION_MESSAGE );
	if(res == null)
	    return;
	double s = 1.0;
	boolean bool = false;
	while(!bool) {
	    try {
		s = Double.parseDouble(res);
		bool=true;
	    }
	    catch(Exception e) {
		res = JOptionPane.showInputDialog(parentFrame, "Zoom factor", "Zoom", JOptionPane.ERROR);
		if(res == null)
		    return;
		bool = false;
	    }
	}
	if(bool) {
	    TwoDTransform tr;
	    if(changeSize)
		tr = new TwoDTransform(parentFrame.fparams,TwoDTransform.SCALE,s);
	    else
		tr = new TwoDTransform(parentFrame.fparams,TwoDTransform.ZOOM,s);
	    parentFrame.doNTransform(tr);
	}
    }

    protected void gaussianNoise() {
	String res = JOptionPane.showInputDialog(parentFrame, "variance sigma2 of noise", "Gaussian Noise", JOptionPane.QUESTION_MESSAGE );
	if(res == null)
	    return;
	double sigma2 = 0.0;
	boolean bool = false;
	while(!bool) {
	    try {
		sigma2 = Double.parseDouble(res);
		bool=true;
	    }
	    catch(Exception e) {
		res = JOptionPane.showInputDialog(parentFrame, "variance sigma2 of noise", "Gaussian Noise", JOptionPane.ERROR_MESSAGE );
		if(res == null)
		    return;
		bool = false;
	    }
	}
	if(bool) {
	    GaussianNoiseTr tr = new GaussianNoiseTr(Math.sqrt(sigma2));
	    parentFrame.doNTransform(tr);
	}
	   
    }

    protected void saltAndPepperNoise() {
	String res = JOptionPane.showInputDialog(parentFrame, "percentage of noise", "Salt and Pepper Noise", JOptionPane.QUESTION_MESSAGE );
	if(res == null)
	    return;
	int p = 0;
	boolean bool = false;
	while(!bool) {
	    try {
		p = Integer.parseInt(res);
		bool=true;
	    }
	    catch(Exception e) {
		res = JOptionPane.showInputDialog(parentFrame, "percentage of noise", "Salt and Pepper Noise", JOptionPane.ERROR_MESSAGE );
		if(res == null)
		    return;
		bool = false;
	    }
	}
	if(bool) {
	    SaltAndPepperNoiseTr tr = new SaltAndPepperNoiseTr(p);
	    parentFrame.doNTransform(tr);
	}
    }

    protected JMenu createBinaryTransformsMenu() {
	JMenu bt = new JMenu("Binary Transforms");

	JMenuItem jmiSub = new JMenuItem("Substract 2 images");
	JMenuItem jmiAdd = new JMenuItem("Add 2 images");
	JMenuItem jmiHoughPolar = new JMenuItem("Hough Polar line detection");
	JMenuItem jmiHoughCartesian = new JMenuItem("Hough Cartesian line detection");
	bt.add(jmiSub);
	bt.add(jmiAdd);
	bt.add(jmiHoughPolar);
	bt.add(jmiHoughCartesian);
	jmiSub.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    BinaryTransforms ht = new BinaryTransforms(parentFrame.imageTD.getSelected(),BinaryTransforms.SUB);
		    parentFrame.doTransform(ht);
		}});
	jmiAdd.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    BinaryTransforms ht = new BinaryTransforms(parentFrame.imageTD.getSelected(),BinaryTransforms.ADD);
		    parentFrame.doTransform(ht);
		}});
	jmiHoughPolar.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    HoughTransform ht = new HoughTransform(HoughTransform.POLAR);
		    parentFrame.doTransform(ht);
		}});
	jmiHoughCartesian.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    HoughTransform ht = new HoughTransform(HoughTransform.CARTESIAN);
		    parentFrame.doTransform(ht);
		}});

	return bt;
    }

    protected JMenu createHelpMenu() {
	JMenu hm = new JMenu("Help");
     
	JMenuItem jmiHistory = new JMenuItem("historic");
	JMenuItem jmiParams = new JMenuItem("parameters of this window");
	JMenuItem jmiHelp = new JMenuItem("help");
	JMenuItem jmiCredit = new JMenuItem("credit");
	hm.add(jmiHistory);
	jmiHistory.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {JOptionPane.showMessageDialog(parentFrame,parentFrame.fparams.historic);}});
	hm.add(jmiParams);
	jmiParams.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {JOptionPane.showMessageDialog(parentFrame,parentFrame.fparams.toString());}});
	hm.add(jmiHelp);
	jmiHelp.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {JOptionPane.showMessageDialog(parentFrame,"read your book :-) ");}});
	hm.add(jmiCredit);
	jmiCredit.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {JOptionPane.showMessageDialog(parentFrame,"Credit :\n Diane Lingrand (lingrand@polytech.unice.fr), 2003");}});
	return hm;
    }

    protected void openImage(int type) {
	int w = parentFrame.imageCanvas.getWidth();
	int h = parentFrame.imageCanvas.getHeight();
	BufferedImage bi;
	int col;

	switch(type) {
	case GREY:
	    bi = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
	    for(int i = 0; i < w; i++) {
		col = (i*255/w)*65793; // 65793 = 1 + 256 ( 1 + 256)
		for(int j = 0; j < h; j++)
		    bi.setRGB(i,j,col);
	    }
	    break;
	case RED:
	    bi = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
	    for(int i = 0; i < w; i++) {
		col = (i*255/w)*65536; // 65536 = 256*256
		for(int j = 0; j < h; j++)
		    bi.setRGB(i,j,col);
	    }
	    break;
	case GREEN:
	    bi = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
	    for(int i = 0; i < w; i++) {
		col = (i*255/w)*256; // 255*256
		for(int j = 0; j < h; j++)
		    bi.setRGB(i,j,col);
	    }
	    break;
	case BLUE:
	    bi = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
	    for(int i = 0; i < w; i++) {
		col = i*255/w;
		for(int j = 0; j < h; j++)
		    bi.setRGB(i,j,col);
	    }
	    break;
	case MACH:
	    bi = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
	    for(int i = 0; i < w/3; i++) 
		for(int j = 0; j < h; j++)
		    bi.setRGB(i,j,0);
	    for(int i = w/3; i < 2*w/3; i++) {
		col = ((i-w/3)*255*3/w)*65793;
		for(int j = 0; j < h; j++)
		    bi.setRGB(i,j,col);
	    }
	    for(int i = 2*w/3; i < w; i++)
		for(int j = 0; j < h; j++)
		    bi.setRGB(i,j,16777215);
	    break;
	case RGB3:
	    bi = new BufferedImage(3,1,BufferedImage.TYPE_INT_RGB);
	    bi.setRGB(0,0,16711680);  //255*256*256
	    bi.setRGB(1,0,65280);     //255*256
	    bi.setRGB(2,0,255);
	    break;
	case RGB5:
	    bi = new BufferedImage(5,1,BufferedImage.TYPE_INT_RGB);
	    bi.setRGB(0,0,16711680);  //255*256*256
	    bi.setRGB(1,0,16711680);  //255*256*256
	    bi.setRGB(2,0,65280);     //255*256
	    bi.setRGB(3,0,65280);     //255*256
	    bi.setRGB(4,0,255);
	    break;
	default:
	    bi = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
	    for(int i = 0; i < w; i++) {
		col = i*255/w;
		for(int j = 0; j < h; j++)
		    bi.setRGB(i,j,col);
	    }
	}
	parentFrame.fparams.historic += "open synthetic image";
	parentFrame.imageCanvas.setImage(bi);
	parentFrame.repaint();
    }
    
    protected void loadImage() {
	JFileChooser chooser = new JFileChooser(curDir);
	chooser.addChoosableFileFilter(new ImageFilterPPM());
	chooser.addChoosableFileFilter(new ImageFilterJava2D());
	chooser.addChoosableFileFilter(new ImageFilter());
	int returnVal = chooser.showOpenDialog(this);
	if(returnVal == JFileChooser.APPROVE_OPTION) {
	    curDir = chooser.getSelectedFile().getAbsolutePath();
	    File fileSelected = chooser.getSelectedFile();
	    System.out.println("file to be opened :" + fileSelected);
	    try {
		BufferedImage bio = ImageIO.read(fileSelected);
		parentFrame.imageCanvas.setImage(bio);
		parentFrame.repaint();
	    }
	    catch(IOException e) { System.out.println("Unable to open file " +fileSelected); }
	}
    }
    protected void saveImage() {
	JFileChooser chooser = new JFileChooser(curDir);
	chooser.addChoosableFileFilter(new SaveImageFilterJPG());
	chooser.addChoosableFileFilter(new SaveImageFilterPPMascii());
	chooser.addChoosableFileFilter(new SaveImageFilterPPMbinary());
	chooser.addChoosableFileFilter(new SaveImageFilterPNG());

	chooser.setVisible(true);

	int returnVal = chooser.showSaveDialog(this);
	if(returnVal == JFileChooser.APPROVE_OPTION) {
	    curDir = chooser.getSelectedFile().getAbsolutePath();
	    File fileSelected = chooser.getSelectedFile();
	    System.out.println("file to be saved :" + fileSelected);
	    System.out.println("you've choosed the format : " + chooser.getFileFilter().getDescription());

	    Dimension d = parentFrame.imageCanvas.getPreferredSize();
	    BufferedImage bi = new BufferedImage((int) d.getWidth(),(int) d.getHeight(),BufferedImage.TYPE_INT_RGB);
	    System.out.println("Dimensions " + d.getWidth() + " ; " + d.getHeight());
	    parentFrame.imageCanvas.cropPaint(bi.getGraphics());		
	    try{
		ImageIO.write(bi,FORMAT[((SaveImageFilter) chooser.getFileFilter()).getFormat()],fileSelected);
	    }
	    catch(IOException e) {System.out.println("Unable to write image in " + fileSelected); }
	    
	}
    }
}

/**
 * ImageFilterJava2D is a @link{FileFilter} for images in JPEG, GIF and PNG formats
 **/
class ImageFilterJava2D extends FileFilter {
     public boolean accept(File f) {
        if (f.isDirectory())
            return true;
                                                                                                  
        String s = f.getName();
        int i = s.lastIndexOf('.');
        if (i > 0 &&  i < s.length() - 1) {
            String extension = s.substring(i+1).toLowerCase();
            if (extension.equals("jpeg") ||
                extension.equals("jpg") ||
                extension.equals("gif") ||
                extension.equals("png"))
                return true;
        }
        return false;
    }

    //The description of this filter
    public String getDescription() {
        return "Image file (jpeg,gif,png)";
    }
}
/**
 * ImageFilterPPM is a @link{FileFilter} for images in PBM, PGM and PPM formats
 **/
class ImageFilterPPM extends FileFilter {
     public boolean accept(File f) {
        if (f.isDirectory())
            return true;
                                                                                                  
        String s = f.getName();
        int i = s.lastIndexOf('.');
        if (i > 0 &&  i < s.length() - 1) {
            String extension = s.substring(i+1).toLowerCase();
            if (extension.equals("pbm") ||
                extension.equals("pgm") ||
                extension.equals("ppm"))
                return true;
        }
        return false;
    }

    //The description of this filter
    public String getDescription() {
        return "Image file (pbm,pgm,ppm)";
    }
}

/**
 * ImageFilter is a @link{FileFilter} for images in JPEG, GIF, PNG, PBM, PGM and PPM formats
 **/
class ImageFilter extends FileFilter {
     public boolean accept(File f) {
        if (f.isDirectory())
            return true;
                                                                                                  
        String s = f.getName();
        int i = s.lastIndexOf('.');
        if (i > 0 &&  i < s.length() - 1) {
            String extension = s.substring(i+1).toLowerCase();
            if (extension.equals("jpeg") ||
                extension.equals("jpg") ||
                extension.equals("gif") ||
                extension.equals("png") ||
                extension.equals("pbm") ||
                extension.equals("pgm") ||
                extension.equals("ppm"))
		return true;
        }
        return false;
    }

    //The description of this filter
    public String getDescription() {
        return "Image file (jpeg,gif,png,pbm,pgm,ppm)";
    }
}

abstract class SaveImageFilter extends FileFilter {
    public abstract int getFormat();
}

/** 
 * class SaveImageFilterJPG is a @link{FileFilter}
 * used to filter ".jpg" and ".jpeg" files
 **/
class SaveImageFilterJPG extends SaveImageFilter {
     public boolean accept(File f) { 
	 if (f.isDirectory())
	     return true;
                                                                                                  
        String s = f.getName();
        int i = s.lastIndexOf('.');
        if (i > 0 &&  i < s.length() - 1) {
            String extension = s.substring(i+1).toLowerCase();
            if (extension.equals("jpeg") ||
                extension.equals("jpg"))
		return true;
        }
        return false;
    }

    //The description of this filter
    public String getDescription() {
        return ImageMenuBar.FORMAT[ImageMenuBar.JPG] + " format";
    }
    public int getFormat() {
	return ImageMenuBar.JPG;
    }
}
/** 
 * class SaveImageFilterPNG is a @link{FileFilter}
 * used to filter ".png" files
 **/
class SaveImageFilterPNG extends SaveImageFilter {
     public boolean accept(File f) { 
	 if (f.isDirectory())
	     return true;
                                                                                                  
        String s = f.getName();
        int i = s.lastIndexOf('.');
        if (i > 0 &&  i < s.length() - 1) {
            String extension = s.substring(i+1).toLowerCase();
            if (extension.equals("png"))
		return true;
        }
        return false;
    }

    //The description of this filter
    public String getDescription() {
        return ImageMenuBar.FORMAT[ImageMenuBar.PNG] + " format";
    }
    public int getFormat() {
	return ImageMenuBar.PNG;
    }
}

/** 
 * class SaveImageFilterPPMbinary is a @link{FileFilter}
 * used to filter ".ppm" files for saving ASCII PPM files
 **/
class SaveImageFilterPPMascii extends SaveImageFilter {
     public boolean accept(File f) { 
	 if (f.isDirectory())
	     return true;
                                                                                                  
        String s = f.getName();
        int i = s.lastIndexOf('.');
        if (i > 0 &&  i < s.length() - 1) {
            String extension = s.substring(i+1).toLowerCase();
            if (extension.equals("ppm"))
		return true;
        }
        return false;
    }

    //The description of this filter
    public String getDescription() {
        return ImageMenuBar.FORMAT[ImageMenuBar.PPM_ascii] + " format";
    }
    public int getFormat() {
	return ImageMenuBar.PPM_ascii;
    }
}
/** 
 * class SaveImageFilterPPMbinary is a @link{FileFilter}
 * used to filter ".ppm" files for saving BINARY PPM files
 **/
class SaveImageFilterPPMbinary extends SaveImageFilter {
     public boolean accept(File f) { 
	 if (f.isDirectory())
	     return true;
                                                                                                  
        String s = f.getName();
        int i = s.lastIndexOf('.');
        if (i > 0 &&  i < s.length() - 1) {
            String extension = s.substring(i+1).toLowerCase();
            if (extension.equals("ppm"))
		return true;
        }
        return false;
    }

    //The description of this filter
    public String getDescription() {
        return ImageMenuBar.FORMAT[ImageMenuBar.PPM_binary] + " format";
    }

    public int getFormat() {
	return ImageMenuBar.PPM_binary;
    }
}
/** 
 * class CustomizedFilter is a @link{JDialog}
 **/
class CustomizedFilter extends JDialog {
    private double [][] filter;
    private int size;
    private JButton buttonOK, buttonCancel;
    private JTextField [][] tabText;
    private JTextField jspTot;

    public CustomizedFilter(Frame f, int s) {
	super(f,"set the coefficients",true);
	size = s;
	filter = new double[size][size];
	
	setFont(new Font("Times",Font.PLAIN,24));
	JPanel jp1 = new JPanel();
	GridLayout gl = new GridLayout(size,size);
	jp1.setLayout(gl);
	tabText = new JTextField[size][size];
	for(int j = 0; j < size; j ++)
	    for(int i = 0; i < size; i ++) {
		tabText[i][j] = new JTextField("1");
		jp1.add(tabText[i][j]);
	    }
	getContentPane().add(jp1,BorderLayout.NORTH);
	JPanel jp2 = new JPanel();
	buttonOK = new JButton("OK");
	buttonOK.setDefaultCapable(true);
	buttonOK.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    double denom = Double.parseDouble(jspTot.getText());
		    for(int i = 0; i < size; i ++) 
			for(int j = 0; j < size; j ++) {
			    filter[i][j] = Double.parseDouble(tabText[i][j].getText())/denom;
			}
		    dispose();
		}});
	buttonCancel = new JButton("Cancel");
	buttonCancel.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {dispose();}});
	jp2.add(buttonOK,BorderLayout.WEST);
	jp2.add(buttonCancel,BorderLayout.EAST);
	getContentPane().add(jp2,BorderLayout.SOUTH);
	JPanel jp3 = new JPanel();
	jp3.add(new JLabel(" / "),BorderLayout.CENTER);
	jspTot = new JTextField("1.0");
	jp3.add(jspTot,BorderLayout.EAST);
	getContentPane().add(jp3,BorderLayout.EAST);
	pack();
	setVisible(true);
    }

    public double [][] getFilter() { 
	return filter;
    }

}
    
/** 
 * class QuantifParamDialog is a @link{JDialog}
 **/
class QuantifParamDialog extends JDialog implements ActionListener {
    private static final String [] BITS_STRINGS = {"1 bit","2 bits","3 bits","4 bits","5 bits","6 bits","7 bits","8 bits"};
    private static final String [] MODE_STRINGS = {"Uniform quantication", "Exponentiel quantification"};
    private JComboBox<String> redBits;
    private JComboBox<String> greenBits;
    private JComboBox<String> blueBits;
    private JComboBox<String> modeChoice;
    private int [] params;

    public QuantifParamDialog(Frame f) {
	super(f,"Quantification parameteres",true);
	setFont(new Font("Times",Font.PLAIN,24));
	GridBagLayout gridbag = new GridBagLayout();
	GridBagConstraints c = new GridBagConstraints();
	getContentPane().setLayout(gridbag);

	c.insets = new Insets(10,10,10,10);
	c.weightx = 1 ;
	addComponent(this, new JLabel("Quantification properties :"),gridbag,c);
	c.gridwidth = GridBagConstraints.REMAINDER;
	addComponent(this, new JLabel("Number of bits for each component:"),gridbag,c);

	c.gridwidth = 1;
	modeChoice = new JComboBox<String>(MODE_STRINGS);
	addComponent(this, modeChoice,gridbag,c);
	redBits = new JComboBox<String>(BITS_STRINGS); redBits.setSelectedIndex(7);
	greenBits = new JComboBox<String>(BITS_STRINGS); greenBits.setSelectedIndex(7);
	blueBits = new JComboBox<String>(BITS_STRINGS); blueBits.setSelectedIndex(7);
	addComponent(this, redBits,gridbag,c);
	addComponent(this, greenBits,gridbag,c);
	c.gridwidth = GridBagConstraints.REMAINDER;
	addComponent(this, blueBits,gridbag,c);

	JButton buttonOK = new JButton("OK");
	buttonOK.setDefaultCapable(true);
	JButton buttonCancel = new JButton("Cancel");
	c.gridwidth = 1;
	addComponent(this, buttonOK ,gridbag,c);
	c.gridwidth = GridBagConstraints.REMAINDER;
	addComponent(this, buttonCancel ,gridbag,c);

	buttonOK.addActionListener(this);
	buttonCancel.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    params = null;
		    dispose();}});

	pack();
	setVisible(true);

    }
    private void addComponent(JDialog j, Component c, GridBagLayout gbl, GridBagConstraints gbc) {
	gbl.setConstraints(c,gbc);
	j.getContentPane().add(c);
    }

    public void actionPerformed(ActionEvent e) {
	params = new int[4];
	params[0] = modeChoice.getSelectedIndex();
	params[1] = redBits.getSelectedIndex()+1;
	params[2] = greenBits.getSelectedIndex()+1;
	params[3] = blueBits.getSelectedIndex()+1;
	dispose();
    } 
    public int [] getParams() { return params; }
}

/** 
 * class Order3Interpolation is a @link{JDialog}
 **/
class Order3Interpolation extends JDialog {
    JTextField jB;
    JTextField jC;
    double [] params;
    
    public Order3Interpolation(Frame f) {
	super(f,"Interpolation of Order 3 : Mitchell's coefficients",true);
	setFont(new Font("Times",Font.PLAIN,24));
	getContentPane().setLayout(new BorderLayout());

	JPanel jp1 = new JPanel();
	JPanel jp2 = new JPanel();
	JPanel jp3 = new JPanel();
	getContentPane().add(jp1, BorderLayout.NORTH);
	getContentPane().add(jp2, BorderLayout.CENTER);
	getContentPane().add(jp3, BorderLayout.SOUTH);

	jp1.add(new JLabel("Enter the B coefficient : "));
	jB = new JTextField(3);
	jp1.add(jB);
	jp2.add(new JLabel("Enter the C coefficient : "));
	jC = new JTextField(3);
	jp2.add(jC);   
	
	JButton buttonOK = new JButton("OK");
	jp3.add(buttonOK);
	buttonOK.setDefaultCapable(true);
	buttonOK.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    params = new double[2];
		    params[0] = Double.parseDouble(jB.getText());
		    params[1] = Double.parseDouble(jC.getText());
		    dispose();}});
	JButton buttonCancel = new JButton("Cancel");
	jp3.add(buttonCancel);
	buttonCancel.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    params = null;
		    dispose();}});

	pack();
	setVisible(true);
    }

    public double [] getParams() {
	return params;
    }
}
/** 
 * class RotateZoomDialog is a @link{JDialog}
 **/
class RotateZoomDialog extends JDialog {
    protected double [] params;
    protected JTextField ja,jcx,jcy;

    public RotateZoomDialog(Frame f, String title1, String title2) {
	super(f,title2,true);
	setFont(new Font("Times",Font.PLAIN,24));

	JPanel jp1 = new JPanel();
	JPanel jp2 = new JPanel();
	JPanel jp3 = new JPanel();
	getContentPane().add(jp1, BorderLayout.NORTH);
	getContentPane().add(jp2, BorderLayout.CENTER);
	getContentPane().add(jp3, BorderLayout.SOUTH);


	jp1.add(new JLabel(title1));
	ja = new JTextField(5);
	jp1.add(ja);

	jp2.add(new JLabel("Center of displacement:"));
	jp2.add(new JLabel("X coord :"));
	jcx = new JTextField(5);
	jp2.add(jcx);
	jp2.add(new JLabel("Y coord :"));
	jcy = new JTextField(5);
	jp2.add(jcy);
	
	JButton buttonOK = new JButton("OK");
	buttonOK.setDefaultCapable(true);
	buttonOK.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    params = new double[3];
		    params[0] = Double.parseDouble(ja.getText());
		    params[1] = Double.parseDouble(jcx.getText());
		    params[2] = Double.parseDouble(jcy.getText());
		    dispose();
		}});
	jp3.add(buttonOK);
	JButton buttonCancel = new JButton("Cancel");
	buttonCancel.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    params = null;
		    dispose();
		}});
	jp3.add(buttonCancel);
	pack();
	setVisible(true);
    }
    public double [] getParams() {
	return params;
    }
}
/**
 *
 **/
class ColorActionListener implements ActionListener {
    protected int type;
    protected ImageFrame parentFrame;

    public ColorActionListener(ImageFrame pf, int i) {
	super();
	type = i;
	parentFrame = pf;
    }

    public void actionPerformed(ActionEvent e) {
	ColorTransform ct = new ColorTransform(type);
	parentFrame.doTransform(ct);
    }
}

/**
 *
 **/
class InterpolationAL implements ActionListener {
    protected int type;
    protected ImageFrame parentFrame;

    public InterpolationAL(ImageFrame pf, int type) {
	super();
	this.type = type;
	parentFrame = pf;
    }

    public void actionPerformed(ActionEvent e) {
	parentFrame.setInterpolationMode(type);
    }
}

/**
 *
 **/
class MorphoActionListener implements ActionListener {
    protected int type;
    protected ImageFrame parentFrame;
    
    public MorphoActionListener(ImageFrame pf, int q) {
	super();
	type = q;
	parentFrame = pf;
    }

    public void actionPerformed(ActionEvent e) {
	MorphoTransform mt = new MorphoTransform(type,parentFrame.fparams.morphoKernel);
	parentFrame.doNTransform(mt);
    }
}

