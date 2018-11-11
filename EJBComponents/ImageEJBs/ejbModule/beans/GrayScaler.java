package beans;

import classes.Image;
import classes.ImageStatus;

import java.awt.image.BufferedImage;
import java.util.Calendar;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Remote;
import javax.ejb.Stateless;

/**
 * Session Bean implementation class GrayScaler
 */
@Stateless
@Remote(RemoteGrayScaler.class)
@Asynchronous
public class GrayScaler implements RemoteGrayScaler {

    public GrayScaler() {

    }

	/**
     * @see RemoteGrayScaler#convert(Image)
     */
    public Future<Image> convert(Image image) {
    	//In addition to the converted image, the bean also provides a comma-separated message
    	//that is sent back to the user with the following format:
    	//imageNo,startingTime,endingTime,threadNo,description
    	String startTime = getTime();
    	String description = image.getMessage();
    	
    	try {
	    	//convert the image into gray scale
	    	BufferedImage mainImage = image.getImage();
	    	
	    	int imageWidth = mainImage.getWidth();
	    	int imageHeight = mainImage.getHeight();
	    	
	    	for(int row = 0; row < imageHeight; row++) {
	    		for(int column = 0; column < imageWidth; column++) {
	    			
	    			//for every pixel simulate a 10 millisecond processing time. 
	    			simulateProcess();
	    			
	    			int argb = mainImage.getRGB(column, row);
	    			
	    			int alpha 	= (argb >> 24) & 0xff;
	    			int red 	= (argb >> 16) & 0xff;
	    			int green	= (argb >> 8 ) & 0xff;
	    			int blue	= (argb)	   & 0xff;
	    			
	    			int average = (red + green + blue) / 3;
	    			int grayValue = (alpha << 24) | (average << 16) | (average << 8) | average;
	    			
	    			mainImage.setRGB(column, row, grayValue);
	    	    	image.setImage(mainImage);
	    		}
	    	}

	    	image.setStatus(ImageStatus.SUCCESS);
    	}catch(Exception e) {
    		image.setStatus(ImageStatus.FAILED);
			description += "image " + image.getImageNO() + " encountered an error in GrayScaler EJB.\n" + e.getMessage();
		}   
    	
		String endTime = getTime();
    	description += "Image " + image.getImageNO() + " remote gray-scaling started at " + startTime + ", ended at " + endTime + " by thread " + Thread.currentThread().getId() + ".\n";
		image.setMessage(description);

    	return new AsyncResult<Image>(image);
    }
    
    private String getTime() {
    	Calendar calendar = Calendar.getInstance();
		String time = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND);
		return time;
    }
    
    private void simulateProcess() {
		//for one thread, takes almost two minutes to 
		//prepare a 972 x 718 picture
		for(int index = 0; index < 30; index++) {
			long count = 0;
			while(count < 8000) {
				count ++;
			}
		}
	}
}
