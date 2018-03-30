package tdim;

import java.awt.image.BufferedImage;

public class OtherEdgeTr extends ImageTransform {
    private static final int [] G1 = {-1,-6,-17,-17,18,46,18,-17,-17,-6,-1};
    private static final int [] G2 = {0,1,5,17,36,46,36,17,5,1,0};
    private static final int G1_HALF_SIZE = 5;

    protected static final int HUERTAS_MEDIONI=0;
    protected static final int DERICHE=1;
    protected static final String [] NAMES = {"Huertas Medioni", "Deriche"};

    protected int type;

    public OtherEdgeTr(int t) {
	type = t;
	name = NAMES[type];
    }

    public BufferedImage filter(BufferedImage bin) {
	if(type == HUERTAS_MEDIONI) {
	    int w = bin.getWidth();
	    int h = bin.getHeight();
	    BufferedImage bout = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
	    
	    for(int ii = G1_HALF_SIZE ; ii < w-G1_HALF_SIZE; ii++)
		for(int jj = G1_HALF_SIZE; jj < h-G1_HALF_SIZE; jj++) {
		    int newR = 0;
		    int newG = 0;
		    int newB = 0;
		    for(int u=-G1_HALF_SIZE; u<G1_HALF_SIZE; u++)
			for(int v =-G1_HALF_SIZE; v<G1_HALF_SIZE; v++) {
			    int color = bin.getRGB(ii+u,jj+v);
			    int pond = G1[u+G1_HALF_SIZE]*G2[v+G1_HALF_SIZE] + G2[u+G1_HALF_SIZE]*G1[v+G1_HALF_SIZE];
			    newR += ((color >> 16) & 0x000000ff )* pond;
			    newG += ((color >> 8) & 0x000000ff)* pond;
			    newB += ((color ) & 0x000000ff) * pond;
			}
		    newR = Math.min(255,Math.max(0,newR));
		    newG = Math.min(255,Math.max(0,newG));
		    newB = Math.min(255,Math.max(0,newB));
		    bout.setRGB(ii,jj,256*(256*newR+newG)+newB);
		}	    
	    return bout;
	}
	return bin;
    }
}
