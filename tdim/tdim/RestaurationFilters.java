package tdim;

import java.awt.image.BufferedImage;
import java.util.Arrays;

public class RestaurationFilters extends ImageTransform {
    protected static final int CONSERVATIVE = 0;
    protected static final int MEDIAN = 1;

    protected static final int CROSS = 0;
    protected static final int DIAMAND = 1;
    protected static final int SQUARE = 2;

    protected static final String [] NAMES = {"Conservative","Median"};
    protected static final String [] NAMES_NHB = {"Cross", "Diamand", "Square"};

    protected int type; // filter type
    protected int nhb;  // neighborough type
    protected int size; // filter size (sizexsize) where size=2p+1
    protected int [][] nmask;
    protected int nbUn; // number of neighbours

    public RestaurationFilters(int t, int n, int s) {
	super();
	type = t;
	nhb = n;
	size = s;
	name = NAMES[type] + " filter with " + NAMES_NHB[nhb] + " neighborough of size " + size;
	nbUn = 0;
	// construction of the neighborouh mask
	nmask = new int[size][size];
	switch(nhb) {
	case CROSS:
	    for(int i = 0; i < size; i ++) {
		nmask[i][size/2] = 1;
		nmask[size/2][i] = 1;
		nbUn += 2;
	    }
	    break;
	case DIAMAND:
	    for(int i = 0; i <= size/2; i ++)
		for(int j = size/2-i ; j <= size/2+i; j++) {
		    nmask[i][j] = 1;
		    nmask[size-i-1][j] = 1;
		    if(i==size-i-1) nbUn++;
		    else nbUn += 2;
		}
	    break;

	case SQUARE:
	    for(int i = 0; i < size; i ++)
		for(int j = 0 ; j < size; j++) {
		    nmask[i][j] = 1;
		    nbUn ++;
		}
	    break;
	}
    }

    public BufferedImage filter(BufferedImage bin) {

	int w = bin.getWidth();
	int h = bin.getHeight();
	BufferedImage bout = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);

	
	if(type == CONSERVATIVE) {
		for (int i = 0; i < w - 1; i++) {
			for (int j = 0; j < h - 1; j++) {
				if (i+1<=w && j+1<=h){
					nmask[0][0] = bin.getRGB(i-1, j-1);
					nmask[0][1] = bin.getRGB(i, j-1);
					nmask[0][2] = bin.getRGB(i+1, j-1);
					nmask[1][0] = bin.getRGB(i-1, j);
					nmask[1][1] = bin.getRGB(i, j);
					nmask[1][2] = bin.getRGB(i+1, j);
					nmask[2][0] = bin.getRGB(i-1, j+1);
					nmask[2][1] = bin.getRGB(i, j+1);
					nmask[2][2] = bin.getRGB(i+1, j+1);

					int[][] sortnmask = nmask;
					Arrays.sort(sortnmask);
					nmask[1][1] = nmask[1][1]<=sortnmask[2][2] && nmask[1][1]>=sortnmask[0][0]?nmask[1][1]: sortnmask[0][0];
					bout.setRGB(i, j, nmask[1][1]);
				}
			}
		}
	}
	else { // MEDIAN
	    int[] mask = {};
        for (int i = 0; i < w - 1; i++) {
            for (int j = 0; j < h - 1; j++) {
                if (i+1<=h && j+1<=h){
                    nmask[0][0] = bin.getRGB(i-1, j-1);
                    nmask[0][1] = bin.getRGB(i, j-1);
                    nmask[0][2] = bin.getRGB(i+1, j-1);
                    nmask[1][0] = bin.getRGB(i-1, j);
                    nmask[1][1] = bin.getRGB(i, j);
                    nmask[1][2] = bin.getRGB(i+1, j);
                    nmask[2][0] = bin.getRGB(i-1, j+1);
                    nmask[2][1] = bin.getRGB(i, j+1);
                    nmask[2][2] = bin.getRGB(i+1, j+1);

                    for (int k = 0; k < nmask.length; k++) {
                        for (int l = 0; l < nmask[0].length; l++) {
                            for (int m = 0; m < nmask.length + nmask[0].length; m++) {
                                mask[k] = nmask[l][m];
                            }
                        }
                    }

                    Arrays.sort(mask);
                    bout.setRGB(i, j, mask[mask.length/2]);
                }
            }
        }

	}
	return bout;

    }
    
}
