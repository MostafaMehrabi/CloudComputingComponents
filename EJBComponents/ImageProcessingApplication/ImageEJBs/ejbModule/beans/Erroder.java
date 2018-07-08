package beans;

import classes.Image;
import classes.ImageStatus;
import classes.StructureElement;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Remote;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class Erroder
 */
@Stateless
@Remote(RemoteErroder.class)
@Asynchronous
public class Erroder implements RemoteErroder {

    public Erroder() {

    }

	/**
     * @see RemoteErroder#errode(Image)
     */
    public Future<Image> errode(Image image) {
    	
    	List<StructureElement> structureElements = new ArrayList<>();
       	for(int x = -1; x <= 1; x++) {
    		for (int y = -1; y <= 1; y++) {
    			StructureElement element = new StructureElement(x, y);
    			structureElements.add(element);
    		}
    	}
    	
    	//In addition to the converted image, the bean also provides a comma-separated message
    	//that is sent back to the user with the following format:
    	//imageNo,startingTime,endingTime,threadNo,description
    	String description = "";
    	String startTime = getTime();
    	
    	BufferedImage originalImage = image.getImage();
    	int type = originalImage.getType();
    	int originalWidth = originalImage.getWidth();
    	int originalHight = originalImage.getHeight();
    	
    	BufferedImage errodedImage = new BufferedImage(originalWidth, originalHight, type);
    	
    	try {
    		
    		//simulate a 10 millisecond processing time for each pixel
    		//Thread.sleep(10);
    		
    		for(int row = 0; row < originalHight; row++) {
    			for(int column = 0; column < originalWidth; column++) {
    				int errodedPixelValue = errodePixel(column, row, originalImage, structureElements);
    				errodedImage.setRGB(column, row, errodedPixelValue);
    			}
    		}
    		
    		image.setImage(errodedImage);
    		description = "image " + image.getImageNO() + " was converted successfully in Errosion EJB.";
    		image.setStatus(ImageStatus.SUCCESS);
    	}catch(Exception e) {
    		description = "image " + image.getImageNO() + " encountered an error in Errosion EJB.\n" + e.getMessage();
    		image.setStatus(ImageStatus.FAILED);
    	}
    	
    	String endTime = getTime();
    	image.setMessage(description);
		image.setMessage(image.getImageNO() + "," + startTime + "," + endTime + "," + Thread.currentThread().getId() + "," + description);

    	return new AsyncResult<Image>(image);
    }
    
    private int errodePixel(int column, int row, BufferedImage originalImage, List<StructureElement> structureElements) {
    	
    	int firstThreshold = 0;
    	int secondThreshold = 64;
    	int thirdThreshold = 128;
    	int fourthThreshold = 192;
    	int highestThreshold = 225;
    	
    	int minimumGray = Integer.MAX_VALUE;
		int alpha = 0;
		
		for(StructureElement coordinate : structureElements) {
			int inspectedX = getInspectedValue(column, coordinate.getX(), 0, originalImage.getWidth()-1);
			int inspectedY = getInspectedValue(row, coordinate.getY(), 0, originalImage.getHeight()-1);
			int[] values = getGrayValues(originalImage.getRGB(inspectedX, inspectedY));
			
			int grayValue = values[0];
			
			if(grayValue < minimumGray) {
				minimumGray = grayValue;
				alpha = values[1];
			}
		}
    	
		if(minimumGray >= firstThreshold && minimumGray < secondThreshold) {
			minimumGray = firstThreshold;
		}
		else if (minimumGray >= secondThreshold && minimumGray < thirdThreshold) {
			minimumGray = secondThreshold;
		}
		else if (minimumGray >= thirdThreshold && minimumGray < fourthThreshold) {
			minimumGray = thirdThreshold;
		}
		else if (minimumGray >= fourthThreshold && minimumGray < highestThreshold) {
			minimumGray = fourthThreshold;
		}
		else { //this step is unnecessary, but for safety!
			minimumGray = highestThreshold;
		}
		
		int errodedPixelValue = (alpha << 24) | (minimumGray << 16) | (minimumGray << 8) | (minimumGray);
    	return errodedPixelValue;
    }
    
    private int getInspectedValue(int value, int coordinateValue, int minValue, int maxValue) {
		value += coordinateValue;
		value = (value > minValue) ? value : minValue;
		value = (value < maxValue) ? value : maxValue;
		return value;
	}
    
    /*
	 * Get the gray value for the pixel with the following column and row values. 
	 * If the pixel is not already a grayscale (i.e., red and green and blue) have
	 * different values, then calculate the average, and send the average, otherwise,
	 * just send the value for red.
	 */
    private int[] getGrayValues(int argb) {
    	int[] values = new int[2];

    	int alpha = (argb >> 24) & 0xff;
    	int red   = (argb >> 16) & 0xff;
    	int green = (argb >> 8 ) & 0xff;
    	int blue  = (argb)       & 0xff;
    	
    	//if the pixel is gray, then red, green and blue are the same values,
    	//so the average will still be gray, otherwise, the gray value is 
    	//calculated. 
    	int gray = (red + green + blue) / 3;
    	
    	values[0] = gray; values[1] = alpha;
    	return values;
    }
    
    private String getTime() {
    	Calendar calendar = Calendar.getInstance();
    	String time = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND);
    	return time;
    }
}
