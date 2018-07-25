package main;

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
		String startTime = getTime();
		//simulate the process of creating image from sensory data
		for(int row = 0; row < imageHeight; row++) {
			for(int column = 0; column < imageWidth; column++) {
				simulateProcess();
			}
		}
		String endTime = getTime();
		String initialMessage = "Sub-image " + imageNumber + " was initially processed between " + startTime + ", and " + endTime + "\n";
		image = new Image(bufferedImage, imageNumber);
		image.setMessage(initialMessage);
		return image;
	}
	
	private void simulateProcess() {
		//for one thread, takes almost two minutes to 
		//prepare a 972 x 718 picture
		for(int index = 0; index < 30; index++) {
			long count = 0;
			while(count < 10000) {
				count ++;
			}
		}
	}
	
	private String getTime() {
		Calendar calendar = Calendar.getInstance();
		String time = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND);
		return time;
	}
}

