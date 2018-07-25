package processingUnits;

import java.awt.image.BufferedImage;

public class ImageGrayscale {
	public ImageGrayscale() {
		
	}
	
	public BufferedImage convert(BufferedImage originalImage) {
		BufferedImage grayImage = null;
		try {
			int imageWidth = originalImage.getWidth();
			int imageHeight = originalImage.getHeight();
			int type = originalImage.getType();
			grayImage = new BufferedImage(imageWidth, imageHeight, type);
			
			for(int row = 0; row < imageHeight; row++) {
				for(int column = 0; column < imageWidth; column++) {
					int argb = originalImage.getRGB(column, row);
					int alpha = (argb >> 24) & 0xff;
					int red =   (argb >> 16) & 0xff;
					int green=  (argb >> 8)  & 0xff;
					int blue =  (argb)		 & 0xff;
					
					int average = (red + green + blue) / 3;
					int gray = (alpha << 24) | (average << 16) | (average << 8) | average;
					grayImage.setRGB(column, row, gray);
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return grayImage;
	}
}
