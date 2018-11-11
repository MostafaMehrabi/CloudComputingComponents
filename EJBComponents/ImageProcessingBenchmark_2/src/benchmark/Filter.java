package benchmark;

import classes.Image;
import processingUnits.Erroder;
import processingUnits.GrayScaler;

public class Filter{
	public Image filter(Image image) {
		long start = System.currentTimeMillis();
		Erroder erroder = new Erroder();
		GrayScaler grayScaler = new GrayScaler();
		Image result = erroder.errode(image);
		result = grayScaler.grayScale(result);
		return result;
	}
}
