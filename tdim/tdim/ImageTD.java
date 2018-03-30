package tdim;

import java.awt.image.BufferedImage;

public class ImageTD {
    private BufferedImage biSelected;

    public ImageTD() {
	biSelected = null;
    }

    public BufferedImage getSelected() {
	return biSelected;
    }

    public void setSelected(BufferedImage bi) {
	biSelected = bi;
    }

    public static void main( String [] args ) {
	ImageTD monTD = new ImageTD();

	if(args.length > 0) {
	    for(int i = 0; i < args.length ; i++) {
		ImageFrame f = new ImageFrame(monTD,"Image Processing",args[i]);
		f.pack();
		f.setVisible(true);
	    }
	}
	else {
	    ImageFrame myFrame = new ImageFrame(monTD,"Image Processing");
	    myFrame.pack();
	    myFrame.setVisible(true);
	}
    }
}
