package benchmark;

import java.awt.image.BufferedImage;
import java.util.Calendar;

import classes.Image;

public class ImageFactory {
	public ImageFactory() {
		
	}
	
	public Image prepare(BufferedImage bufferedImage, int imageNumber) {
		int imageWidth = bufferedImage.getWidth();
		int imageHeight = bufferedImage.getHeight();
		Image image = null;
		long start = System.currentTimeMillis();
		//simulate the process of creating image from sensory data
		for(int row = 0; row < imageHeight; row++) {
			for(int column = 0; column < imageWidth; column++) {
				simulateProcess();
			}
		}
		long end = System.currentTimeMillis();
		String initialMessage = (end-start) + " ";
		image = new Image(bufferedImage, imageNumber);
		image.setMessage(initialMessage);
		return image;
	}
	
	private long simulateProcess() {
		//for one thread, takes almost two minutes to 
		//prepare a 972 x 718 picture
		long count = 0;
		for(int index = 0; index < 10; index++) {
			while(count < 6000) {
				count ++;
			}
		}
		return count;
	}
	
	public String getTime() {
		Calendar calendar = Calendar.getInstance();
		String time = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND);
		return time;
	}
}

