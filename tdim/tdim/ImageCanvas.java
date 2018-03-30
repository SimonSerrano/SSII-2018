package tdim;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.BufferedWriter;

public class ImageCanvas extends JPanel implements MouseMotionListener{
    protected static final int FAT_SIZE = 20;

    private JLabel label;
    protected BufferedImage bi = null;
    private ImageFrame parentFrame;
    private int width = 200;
    private int height = 100;

    public ImageCanvas(ImageFrame f) {
	super();
	parentFrame = f;
	this.label = f.label;
	setPreferredSize(new Dimension(width,height));
	addMouseMotionListener(this);
    }

    public ImageCanvas(ImageFrame f, BufferedImage b) {
	this(f);
	setImage(b);
    }

    public void setImage(BufferedImage b) { 
	bi = b; 
	width = bi.getWidth();
	height = bi.getHeight();
	if(parentFrame.isFatBits())
	    setPreferredSize(new Dimension(FAT_SIZE*width,FAT_SIZE*height));
	else
	    setPreferredSize(new Dimension(width,height));
	parentFrame.scrollPane.updateUI();
    }
    
    public void setFatBits(boolean b) {
	if(bi == null)
	    return;
	if(b) 
	    setPreferredSize(new Dimension(FAT_SIZE*bi.getWidth(),FAT_SIZE*bi.getHeight()));
	else
	    setPreferredSize(new Dimension(bi.getWidth(),bi.getHeight()));
	parentFrame.scrollPane.updateUI();
    }

    // implements MouseMotionListener
    public void mouseDragged(MouseEvent e) {}

    public void mouseMoved(MouseEvent e) {
	int x = e.getX();
	int y = e.getY();

	if(bi == null) {
	    label.setText("no image loaded");
	    return;
	}
	
	if(parentFrame.isFatBits()) {
	    x /= FAT_SIZE;
	    y /= FAT_SIZE;
	}

	if( (x<0) || (x>=width) || (y<0) || (y>=height) )  {
	    label.setText("(" + x + ";" + y + ") : outside image");
	    return;
	}
	int col = bi.getRGB(x,y);
	Color cm = new Color(col);
	label.setText("(" + x + ";" + y + ") = " + cm.getRed() + " ; " + cm.getGreen() + " ; " + cm.getBlue());
    }

    public void paint(Graphics g) {
	// we need to paint the background in order to avoid bad repaints on menus and menuitems
	super.paint(g);
	cropPaint(g);
    }

    // when printing in svg file, we do not need the background
    public void cropPaint(Graphics g) {
	if(bi != null) {
	    if(parentFrame.fparams.fatBits) {
		int w = bi.getWidth();
		int h = bi.getHeight();
		for(int i = 0; i < w; i ++)
		    for(int j = 0; j < h; j ++) {
			g.setColor(new Color(bi.getRGB(i,j)));
			g.fillRect(i*FAT_SIZE,j*FAT_SIZE,FAT_SIZE,FAT_SIZE);
		    }

		int fw = FAT_SIZE*width;
		int fh = FAT_SIZE*height;
		
		g.setColor(Color.RED);
		for(int i = 0; i <= fw; i+= FAT_SIZE)
		    g.drawLine(i,0, i,fh);
		for(int j = 0; j <= fh; j+= FAT_SIZE)
		    g.drawLine(0,j, fw,j);
		
	    }
	    else
		g.drawImage(bi,0,0,null);
	}
    }

    public void toStringPS(BufferedWriter bw) {
	if(bi == null)
	    return ;

	int w = bi.getWidth();
	int h = bi.getHeight();

	int yoffset = FAT_SIZE*h;//770;
	try{
	    bw.write("%!\n");
	    bw.write("%%BoundingBox: 0 0 " + (FAT_SIZE*w+1) + " " + (FAT_SIZE*h+1) + "\n");
	    // fat bits (pixels)
	    for(int i = 0; i < w; i ++)
		for(int j = 0; j < h; j ++) {
		    Color col = new Color(bi.getRGB(i,j));
		    double r = col.getRed()/255.0;
		    double g = col.getGreen()/255.0;
		    double b = col.getBlue()/255.0;
		    bw.write( r + " " + g + " " + b + "  setrgbcolor\n");
		    bw.write("newpath\n");
		    bw.write( i*FAT_SIZE + " " + (yoffset-j*FAT_SIZE) +" moveto\n");
		    bw.write( FAT_SIZE + " " + 0 + " rlineto\n");
		    bw.write( 0 + " " + FAT_SIZE + " rlineto\n");
		    bw.write( -FAT_SIZE + " " + 0 + " rlineto\n");
		    bw.write( "closepath\n");
		    bw.write( "fill\n");
		}
	    // red lines
	    bw.write("1.000 0.000 0.000 setrgbcolor\n");
	    int fw = FAT_SIZE*w;
	    int fh = FAT_SIZE*h;
	    for(int i = 0; i <= fw; i+= FAT_SIZE) {
		bw.write("newpath\n");
		bw.write( i + " " + (FAT_SIZE+yoffset) +" moveto\n");
		bw.write( "0 " + -fh + " rlineto\n");
		bw.write( "stroke\n");
	    }
	    for(int j = 0; j <= fh; j+= FAT_SIZE) {
		bw.write("newpath\n");
		bw.write( "0 " + (FAT_SIZE+yoffset - j) + " moveto\n");
		bw.write( fw + " 0 rlineto\n");
		bw.write( "stroke\n");
	    }
	    
	    bw.write( "showpage\n");
	}
	catch(java.io.IOException e) { System.out.println("unable to write in the buffer" ); }
    }
}
