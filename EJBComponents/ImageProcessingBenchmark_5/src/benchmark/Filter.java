package benchmark;

import classes.Image;
import processingUnits.Erroder;
import processingUnits.GrayScaler;

public class Filter{
	public Image filter(Image image) {
		long start = System.currentTimeMillis();
    	String description = image.getMessage();
    	
    	GrayScaler grayScaler = new processingUnits.GrayScaler();
    	Erroder erroder = new processingUnits.Erroder();
    	Image result = grayScaler.grayScale(image);
    	result = erroder.errode(result);
    	
    	long end = System.currentTimeMillis();
    	long duration = end - start;
    	String newMessage = Long.toString(duration);
    	
    	description += newMessage;
    	result.setMessage(description);
    	return result;  
	}
}
