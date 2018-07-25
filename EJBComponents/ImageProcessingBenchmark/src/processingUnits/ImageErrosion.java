package processingUnits;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * This errosion class expects a grayscale image, and errodes that image using 
 * the structural element:
 * </br>
 * (-1, -1)   (0, -1)  (1, -1)</br>
 * (-1,  0)   (0,  0)  (1,  0)</br>
 * (-1,  1)   (0,  1)  (1,  1)</br>
 * 
 * @author Mostafa Mehrabi
 *
 */
public class ImageErrosion {
	
	private List<Coordinate> structureElement = null;
	private BufferedImage originalImage = null;
	private int firstThreshold = 0;
	private int secondThreshold = 64;
	private int thirdThreshold = 128;
	private int fourthThreshold = 192;
	private int highestThreshold = 255;
	
	public ImageErrosion(BufferedImage image) {
		this.originalImage = image;
		structureElement = new ArrayList<>();
		for(int x = -1; x <= 1; x++) {
			for(int y = -1; y <=1; y++) {
				Coordinate element = new Coordinate(x, y);
				this.structureElement.add(element);
			}
		}
	}
	
	public BufferedImage errode() {
		BufferedImage errodedImage = null;
	
		int originalWidth = originalImage.getWidth();
		int originalHeight = originalImage.getHeight();
		int type = originalImage.getType();
		errodedImage = new BufferedImage(originalWidth, originalHeight, type);
		
		for(int row = 0; row < originalHeight; row++) {
			for(int column = 0; column < originalWidth; column++) {
				int errodedPixelValue = errodePixel(column, row);
				errodedImage.setRGB(column, row, errodedPixelValue);
			}
		}
		
		return errodedImage;
	}
	
	private int errodePixel(int column, int row) {
		int minimumGray = Integer.MAX_VALUE;
		int alpha = 0;
		for(Coordinate coordinate : structureElement) {
			int inspectedX = getInspectedValue(column, coordinate.x, 0, originalImage.getWidth()-1);
			int inspectedY = getInspectedValue(row, coordinate.y, 0, originalImage.getHeight()-1);
			int[] values = getGrayValue(inspectedX, inspectedY);
			
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
	
	private int getInspectedValue(int value, int coordinateValue, int imageMin, int imageMax) {
		value += coordinateValue;
		value = (value > imageMin) ? value : imageMin;
		value = (value < imageMax) ? value : imageMax;
		return value;
	}
	
	/*
	 * Get the gray value for the pixel with the following column and row values. 
	 * If the pixel is not already a grayscale (i.e., red and green and blue) have
	 * different values, then calculate the average, and send the average, otherwise,
	 * just send the value for red.
	 */
	private int[] getGrayValue(int inspectedX, int inspectedY) {
		int[] values = new int[2];
		int argb = originalImage.getRGB(inspectedX, inspectedY);
		int alpha = (argb >> 24) & 0xff;
		int red = (argb >> 16) & 0xff;
		int green = (argb >> 8) & 0xff;
		int blue = (argb) & 0xff;
		
		//if the pixel is gray, then red, green and blue are the same values,
		//so the average will still be gray, otherwise, the gray value is 
		//calculated. 
		int gray = (red + green + blue) / 3;
		values[0] = gray; values[1] = alpha;
		return values;
	}
	
	
	private class Coordinate{
		Coordinate(int x, int y){
			ImageErrosion.Coordinate.this.x = x;
			ImageErrosion.Coordinate.this.y = y;
		}
		int x;
		int y;
	}
}
