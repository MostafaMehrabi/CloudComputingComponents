package processingUnits;

import java.awt.image.BufferedImage;
import java.util.Calendar;

import classes.Image;
import classes.ImageStatus;

public class GrayScaler {
	public GrayScaler() {
		
	}
	
	public Image grayScale(Image image) {
    	//In addition to the converted image, the bean also provides a comma-separated message
    	//that is sent back to the user with the following format:
    	//imageNo,startingTime,endingTime,threadNo,description
    	
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
		}   
    	
		return image;
    }
    
    public String getTime() {
    	Calendar calendar = Calendar.getInstance();
		String time = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND);
		return time;
    }
    
    private long simulateProcess() {
		//for one thread, takes almost two minutes to 
		//prepare a 972 x 718 picture
		long count = 0;
		for(int index = 0; index < 8; index++) {
			while(count < 8000) {
				count ++;
			}
		}
		return count;
	}
}
