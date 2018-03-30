package tdim;

import java.awt.image.BufferedImage;

public abstract class ImageTransform {
    protected String name = "not named";

    public abstract BufferedImage filter(BufferedImage b);

    public String toString() {
	return name;
    }
}
