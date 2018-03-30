package tdim;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static java.lang.Math.abs;
import static java.lang.Math.max;

public class ConvolutionFilter extends ImageTransform {
    private static double unTiers = 1.0 / 3.0;
    protected static final int NAIVE_X = 0;
    protected static final int NAIVE_Y = 1;
    protected static final int NAIVE = 2;
    protected static final int ROBERTS_X = 3;
    protected static final int ROBERTS_Y = 4;
    protected static final int ROBERTS = 5;
    protected static final int SOBEL_X = 6;
    protected static final int SOBEL_Y = 7;
    protected static final int SOBEL = 8;
    protected static final int PREWITT = 9;
    protected static final int KIRSCH = 10;
    protected static final int LAPLACE4 = 11;
    protected static final int LAPLACE8 = 12;
    protected static final int GAUSSIAN3 = 13;
    protected static final int GAUSSIAN5 = 14;
    protected static final int GAUSSIAN7 = 15;
    protected static final int SHARPEN3a = 16;
    protected static final int SHARPEN3b = 17;
    protected static final int SHARPEN3c = 18;
    protected static final int LAPLACE4_ZEROS = 19;
    protected static final int LAPLACE8_ZEROS = 20;

    protected static final double[][] GAUSSIAN3_FILTER = {{1.0 / 16, 2.0 / 16, 1.0 / 16}, {2.0 / 16, 4.0 / 16, 2.0 / 16}, {1.0 / 16, 2.0 / 16, 1.0 / 16}};
    protected static final double[][] GAUSSIAN5_FILTER = {{1.0 / 52, 1.0 / 52, 2.0 / 52, 1.0 / 52, 1.0 / 52}, {1.0 / 52, 2.0 / 52, 4.0 / 52, 2.0 / 52, 1.0 / 52}, {2.0 / 52, 4.0 / 52, 8.0 / 52, 4.0 / 52, 2.0 / 52}, {1.0 / 52, 2.0 / 52, 4.0 / 52, 2.0 / 52, 1.0 / 52}, {1.0 / 52, 1.0 / 52, 2.0 / 52, 1.0 / 52, 1.0 / 52}};
    protected static final double[][] GAUSSIAN7_FILTER = {{1.0 / 140, 1.0 / 140, 2.0 / 140, 2.0 / 140, 2.0 / 140, 1.0 / 140, 1.0 / 140}, {1.0 / 140, 2.0 / 140, 2.0 / 140, 4.0 / 140, 2.0 / 140, 2.0 / 140, 1.0 / 140}, {2.0 / 140, 2.0 / 140, 4.0 / 140, 8.0 / 140, 4.0 / 140, 2.0 / 140, 2.0 / 140}, {2.0 / 140, 4.0 / 140, 8.0 / 140, 16.0 / 140, 8.0 / 140, 4.0 / 140, 2.0 / 140}, {2.0 / 140, 2.0 / 140, 4.0 / 140, 8.0 / 140, 4.0 / 140, 2.0 / 140, 2.0 / 140}, {1.0 / 140, 2.0 / 140, 2.0 / 140, 4.0 / 140, 2.0 / 140, 2.0 / 140, 1.0 / 140}, {1.0 / 140, 1.0 / 140, 2.0 / 140, 2.0 / 140, 2.0 / 140, 1.0 / 140, 1.0 / 140}};
    protected static final double[][] SHARPEN3a_FILTER = {{0, -1 / 6.0, 0}, {-1 / 6.0, 10 / 6.0, -1 / 6.0}, {0, -1 / 6.0, 0}};
    protected static final double[][] SHARPEN3b_FILTER = {{1, -2, 1}, {-2, 5, -2}, {1, -2, 1}};
    protected static final double[][] SHARPEN3c_FILTER = {{-1, -1, -1}, {-1, 9, -1}, {-1, -1, -1}};

    public static final String[] NAMES = {"Naive X", "Naive Y", "Naive", "Roberts X", "Roberts Y", "Roberts", "Sobel X", "Sobel Y", "Sobel", "Prewitt", "Kirsch", "Laplace connexity 4", "Laplace connexity 8", "Gaussian 3x3", "Gaussian 5x5", "Gaussian 7x7", "Sharpen type 1", "Sharpen type 2", "Sharpen type 3", "Zeros of Laplace 4", "Zeros of Laplace 8"};

    protected ArrayList<double[][]> filters; // list of convolution kernel

    protected boolean isLaplacian = false;

    public ConvolutionFilter(double[][] f, String n) {
        super();
        filters = new ArrayList<double[][]>();
        filters.add(f);
        name = n;
    }

    public ConvolutionFilter(double[][] f) {
        super();
        filters = new ArrayList<double[][]>();
        filters.add(f);
        name = "filter :";
        for (int i = 0; i < f.length; i++) {
            name += "[ ";
            for (int j = 0; j < f[0].length; j++)
                name += " " + f[i][j];
            name += " ]";
        }
        name += "\n";
    }

    public ConvolutionFilter(int type) {
        super();
        name = NAMES[type] + " filter";
        filters = new ArrayList<>();
        double[][] filter;
        switch (type) {
            case NAIVE_X:
                filter = new double[3][3];
                filter[1][0] = -1;
                filter[1][1] = 1;
                filters.add(filter);
                break;
            case NAIVE_Y:
                filter = new double[3][3];
                filter[0][1] = -1;
                filter[1][1] = 1;
                filters.add(filter);
                break;
            case ROBERTS_X:
                break;
            case ROBERTS_Y:
                break;
            case SOBEL_X:
                filter = new double[3][3];
                filter[0][0]=1.0/4;
                filter[0][2]=-1.0/4;
                filter[1][0]=2.0/4;
                filter[1][2]=-2.0/4;
                filter[2][0]=1.0/4;
                filter[2][2]=-1.0/4;
                filters.add(filter);
                break;
            case SOBEL_Y:
                filter = new double[3][3];
                filter[0][0] = -1.0/4;
                filter[0][1] = -2.0/4;
                filter[0][2] = -1.0/4;
                filter[2][0] = 1.0/4;
                filter[2][1] = 2.0/4;
                filter[2][2] = 1.0/4;
                filters.add(filter);
                break;
            case LAPLACE4_ZEROS:
            case LAPLACE4:
                break;
            case LAPLACE8_ZEROS:
            case LAPLACE8:
                break;
            case NAIVE:
                filter = new double[3][3];
                filter[1][0] = -1.0;
                filter[1][1] = 1.0;
                filters.add(filter);
                filter = new double[3][3];
                filter[0][1] = -1;
                filter[1][1] = 1;
                filters.add(filter);
                break;
            case ROBERTS:
                break;
            case SOBEL:
                filter = new double[3][3];
                filter[0][0]=1.0/4;
                filter[0][2]=-1.0/4;
                filter[1][0]=2.0/4;
                filter[1][2]=-2.0/4;
                filter[2][0]=1.0/4;
                filter[2][2]=-1.0/4;
                filters.add(filter);
                filter = new double[3][3];
                filter[0][0] = -1.0/4;
                filter[0][1] = -2.0/4;
                filter[0][2] = -1.0/4;
                filter[2][0] = 1.0/4;
                filter[2][1] = 2.0/4;
                filter[2][2] = 1.0/4;
                filters.add(filter);
                break;
            case PREWITT:
                filter = new double[3][3];
                filter[0][0]=1.0/3;
                filter[0][2]=-1.0/3;
                filter[1][0]=1.0/3;
                filter[1][2]=-1.0/3;
                filter[2][0]=1.0/3;
                filter[2][2]=-1.0/3;
                filters.add(filter);
                filter = new double[3][3];
                filter[0][0]=1.0/3;
                filter[0][1]=1.0/3;
                filter[1][0]=1.0/3;
                filter[1][2]=-1.0/3;
                filter[2][1]=-1.0/3;
                filter[2][2]=-1.0/3;
                filters.add(filter);
                filter = new double[3][3];
                filter[0][0]=1.0/3;
                filter[0][1]=1.0/3;
                filter[0][2]=1.0/3;
                filter[2][0]=-1.0/3;
                filter[2][1]=-1.0/3;
                filter[2][2]=-1.0/3;
                filters.add(filter);
                filter = new double[3][3];
                filter[0][1]=1.0/3;
                filter[0][2]=1.0/3;
                filter[1][0]=-1.0/3;
                filter[1][2]=-1.0/3;
                filter[2][0]=-1.0/3;
                filter[2][1]=-1.0/3;
                filters.add(filter);
                break;
            case KIRSCH:
                filter = new double[3][3];
                filter[0][0] = 5.0/15;
                filter[0][1] = -3.0/15;
                filter[0][2] = -3.0/15;
                filter[1][0] = 5.0/15;
                filter[1][2] = -3.0/15;
                filter[2][0] = 5.0/15;
                filter[2][1] = -3.0/15;
                filter[2][2] = -3.0/15;
                filters.add(filter);
                filter = new double[3][3];
                filter[0][0] = 5.0/15;
                filter[0][1] = 5.0/15;
                filter[0][2] = -3.0/15;
                filter[1][0] = 5.0/15;
                filter[1][2] = -3.0/15;
                filter[2][0] = -3.0/15;
                filter[2][1] = -3.0/15;
                filter[2][2] = -3.0/15;
                filters.add(filter);
                filter = new double[3][3];
                filter[0][0] = 5.0/15;
                filter[0][1] = 5.0/15;
                filter[0][2] = 5.0/15;
                filter[1][0] = -3.0/15;
                filter[1][2] = -3.0/15;
                filter[2][0] = -3.0/15;
                filter[2][1] = -3.0/15;
                filter[2][2] = -3.0/15;
                filters.add(filter);
                filter = new double[3][3];
                filter[0][0] = -3.0/15;
                filter[0][1] = 5.0/15;
                filter[0][2] = 5.0/15;
                filter[1][0] = -3.0/15;
                filter[1][2] = 5.0/15;
                filter[2][0] = -3.0/15;
                filter[2][1] = -3.0/15;
                filter[2][2] = -3.0/15;
                filters.add(filter);
                filter = new double[3][3];
                filter[0][0] = -3.0/15;
                filter[0][1] = -3.0/15;
                filter[0][2] = 5.0/15;
                filter[1][0] = -3.0/15;
                filter[1][2] = 5.0/15;
                filter[2][0] = -3.0/15;
                filter[2][1] = -3.0/15;
                filter[2][2] = 5.0/15;
                filters.add(filter);
                filter = new double[3][3];
                filter[0][0] = -3.0/15;
                filter[0][1] = -3.0/15;
                filter[0][2] = -3.0/15;
                filter[1][0] = -3.0/15;
                filter[1][2] = 5.0/15;
                filter[2][0] = -3.0/15;
                filter[2][1] = 5.0/15;
                filter[2][2] = 5.0/15;
                filters.add(filter);
                filter = new double[3][3];
                filter[0][0] = -3.0/15;
                filter[0][1] = -3.0/15;
                filter[0][2] = -3.0/15;
                filter[1][0] = -3.0/15;
                filter[1][2] = -3.0/15;
                filter[2][0] = 5.0/15;
                filter[2][1] = 5.0/15;
                filter[2][2] = 5.0/15;
                filters.add(filter);
                filter = new double[3][3];
                filter[0][0] = -3.0/15;
                filter[0][1] = -3.0/15;
                filter[0][2] = -3.0/15;
                filter[1][0] = 5.0/15;
                filter[1][2] = -3.0/15;
                filter[2][0] = 5.0/15;
                filter[2][1] = 5.0/15;
                filter[2][2] = -3.0/15;
                filters.add(filter);

                break;
            case GAUSSIAN3: //1,2,1,2,4,2,1,2,1 /16
                filters.add(GAUSSIAN3_FILTER);
                break;
            case GAUSSIAN5: //1,1,2,1,1, 1,2,4,2,1, 2,4,8,4,2, 1,2,4,2,1, 1,1,2,1,1 /52
                filters.add(GAUSSIAN5_FILTER);
                break;
            case GAUSSIAN7: //1,1,2,2,2,1,1, 1,2,2,4,2,2,1, 2,2,4,8,4,2,2, 2,4,8,16,8,4,2, 2,2,4,8,4,2,2, 1,2,2,4,2,2,1, 1,1,2,2,2,1,1 / 140
                filters.add(GAUSSIAN7_FILTER);
                break;
            case SHARPEN3a://0,-1,0,-1,10,-1,0,-1,0
                filters.add(SHARPEN3a_FILTER);
                break;
            case SHARPEN3b://1,-2,1,-2,5,-2,1,-2,1
                filters.add(SHARPEN3b_FILTER);
                break;
            case SHARPEN3c://-1,-1,-1,-1,9,-1,-1,-1,-1
                filters.add(SHARPEN3c_FILTER);
                break;

            default:
        }
    }

    public BufferedImage filter(BufferedImage bin) {
        int w = bin.getWidth();
        int h = bin.getHeight();
        BufferedImage bout = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        int nbFilters = filters.size();
        int col,s,r,g,b;
        int rprime, gprime, bprime;
        Color c;

        switch (nbFilters) {
            case 0:
                return bout;
            case 1:
                double[][] filter = filters.get(0);
                s = filter.length/2;
                for (int i=s; i<w-s; i++){
                    for (int j=s; j<h-s; j++){
                        r = 0;
                        g = 0;
                        b = 0;
                        for (int u = -s; u<=s; u++){
                            for (int v = -s; v<=s; v++){
                                col = bin.getRGB(u+i, v+j);
                                c = new Color(col);
                                r += c.getRed()*filter[u+s][v+s];
                                g += c.getGreen()*filter[u+s][v+s];
                                b += c.getBlue()*filter[u+s][v+s];
                            }
                        }
                        r = abs(r);
                        g = abs(g);
                        b = abs(b);

                        rprime = (int) (r+0.5);
                        gprime = (int) (g+0.5);
                        bprime = (int) (b+0.5);

                        rprime = rprime>255?255:rprime;
                        gprime = gprime>255?255:gprime;
                        bprime = bprime>255?255:bprime;

                        col = bprime + 256*(gprime + 256*rprime);
                        bout.setRGB(i, j, col);
                    }
                }
                return bout;
            case 2:
                // TO BE DONE
                // retourne l'image bout resultat de la norme des vecteurs composes
                // de la convolution de l'image bin par chacun des 2 filtres. On utilisera
                // pour norme la norme euclidienne.
                double[][] filter1 = filters.get(0);
                double[][] filter2 = filters.get(1);
                int r1,g1,b1,r2,g2,b2;
                s = filter1.length/2;
                for (int i=s; i<w-s; i++){
                    for (int j=s; j<h-s; j++){
                        r1 = 0;
                        g1 = 0;
                        b1 = 0;
                        r2 = 0;
                        g2 = 0;
                        b2 = 0;
                        for (int u = -s; u<=s; u++){
                            for (int v = -s; v<=s; v++){
                                col = bin.getRGB(u+i, v+j);
                                c = new Color(col);
                                r1 += c.getRed()*filter1[u+s][v+s];
                                g1 += c.getGreen()*filter1[u+s][v+s];
                                b1 += c.getBlue()*filter1[u+s][v+s];
                                r2 += c.getRed()*filter2[u+s][v+s];
                                g2 += c.getGreen()*filter2[u+s][v+s];
                                b2 += c.getBlue()*filter2[u+s][v+s];
                            }
                        }
                        r1 = abs(r1);
                        g1 = abs(g1);
                        b1 = abs(b1);
                        r2 = abs(r2);
                        g2 = abs(g2);
                        b2 = abs(b2);

                        r = max(r1,r2);
                        g = max(g1,g2);
                        b = max(b1,b2);

                        rprime = (int) (r+0.5);
                        gprime = (int) (g+0.5);
                        bprime = (int) (b+0.5);

                        rprime = rprime>255?255:rprime;
                        gprime = gprime>255?255:gprime;
                        bprime = bprime>255?255:bprime;

                        col = bprime + 256*(gprime + 256*rprime);
                        bout.setRGB(i, j, col);
                    }
                }
                return bout;
            default:
                // TO BE DONE
                // dans le cas de + de 2 filtres, on retourne la norme des vecteurs composes
                // des convolutions de l'image bin par chaque filtre. On utilisera pour cela
                // la norme prenant la plus grande des valeurs absolues.
                int ra[] = new int[nbFilters];
                int ga[] = new int[nbFilters];
                int ba[] = new int[nbFilters];
                s = filters.get(0).length/2;
                for (int i=s; i<w-s; i++){
                    for (int j=s; j<h-s; j++){
                        r = 0;
                        g = 0;
                        b = 0;
                        for (int u = -s; u<=s; u++){
                            for (int v = -s; v<=s; v++){
                                col = bin.getRGB(u+i, v+j);
                                c = new Color(col);
                                for (int k = 0; k<nbFilters; k++){
                                    r += c.getRed()*filters.get(k)[u+s][v+s];
                                    g += c.getGreen()*filters.get(k)[u+s][v+s];
                                    b += c.getBlue()*filters.get(k)[u+s][v+s];
                                    ra[k] = abs(r);
                                    ga[k] = abs(g);
                                    ba[k] = abs(b);
                                }
                            }
                        }

                        r = getMaxArray(ra);
                        g = getMaxArray(ga);
                        b = getMaxArray(ba);

                        rprime = (int) (r+0.5);
                        gprime = (int) (g+0.5);
                        bprime = (int) (b+0.5);

                        rprime = rprime>255?255:rprime;
                        gprime = gprime>255?255:gprime;
                        bprime = bprime>255?255:bprime;

                        col = bprime + 256*(gprime + 256*rprime);
                        bout.setRGB(i, j, col);
                    }
                }
                return bout;
        }
    }

    private int getMaxArray(int[] array){
        int max = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i]>max){
                max = array[i];
            }
        }
        return max;
    }
}
