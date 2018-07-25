package processingUnits;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import main.Main;

public class ImageSplit {
	
	private int numOfSplits = 1;
	private String fileName = null;
	private int totalColumnSplit = 1;
	private int totalRowSplit = 1;
	private boolean totalSet = false;
	
	public ImageSplit(int num, String fileName) {
		this.numOfSplits = num;
		this.fileName = fileName;
		

		if(numOfSplits == 1) {
			totalColumnSplit = 1;
			totalRowSplit = 1;
			totalSet = true;
		}
		
		else {
			if((numOfSplits % 2) != 0) {
				throw new RuntimeException("The number of splits for image must a multiply of 2");
			}
					
//			else if (numOfSplits == 2) {
//				totalColumnSplit = 2;
//				totalRowSplit = 1;
//			}
//			
//			else {
//				totalColumnSplit = numOfSplits/2;
//				totalRowSplit = numOfSplits/2;
//			}			
		}
	}	
	
	
	public BufferedImage[] split() throws IOException {
		BufferedImage[] images = new BufferedImage[numOfSplits];
		
		File imageFile = new File(fileName);
		BufferedImage mainImage = ImageIO.read(imageFile);
		
		int mainImageWidth = mainImage.getWidth();
		int mainImageHeight = mainImage.getHeight();
		
		String log = "main image size of " + fileName + ": " + mainImageWidth + " x " + mainImageHeight;
		System.out.println(log);
		
		if(!totalSet) {
			int minimum = Integer.MAX_VALUE;
			List<Elements> elements = inspectDivivsors();
			for(Elements element : elements) {
				if(element.difference < minimum) {
					minimum = element.difference;
					int biggerValue = (element.divisor > element.quotient) ? element.divisor : element.quotient;
					int smallerValue = (element.divisor < element.quotient) ? element.divisor : element.quotient;
					totalColumnSplit = (mainImageWidth > mainImageHeight) ? biggerValue : smallerValue;
					totalRowSplit = (mainImageHeight > mainImageWidth) ? biggerValue : smallerValue;
				}
			}
		}
		
		//num of splits always needs to be higher or equal to 2.		
		int widthPerImage = mainImageWidth / totalColumnSplit;
		int heightPerImage = mainImageHeight / totalRowSplit;
		int imageIndex = 0;
		
		for(int row = 0; row < totalRowSplit; row++) {
			
			int subImageHeight = heightPerImage;
			int subImageY = heightPerImage * row;
			
			if(row == (totalRowSplit-1)) {
				if((subImageY + subImageHeight) != mainImageHeight)
					subImageHeight = mainImageHeight - subImageY;
			}
			
			for(int column = 0; column < totalColumnSplit; column++) {
				int subImageWidth = widthPerImage;
				int subImageX = widthPerImage * column;
							
				if(column == (totalColumnSplit-1)) {
					if((subImageX + subImageWidth) != mainImageWidth) {
						subImageWidth = mainImageWidth - subImageX;
					}
				}
				
				BufferedImage subImage = mainImage.getSubimage(subImageX, subImageY, subImageWidth, subImageHeight);
				images[imageIndex++] = subImage;			
			}
		}
		return images;
	}
	
	public BufferedImage join(BufferedImage[] images) {
		BufferedImage jointImage = null;
		BufferedImage[][] imageArray = new BufferedImage[totalColumnSplit][totalRowSplit];
			
		for(int rowIndex = 0; rowIndex < totalRowSplit; rowIndex++) {
			for(int columnIndex = 0; columnIndex < totalColumnSplit; columnIndex++) {
				int index = (rowIndex * totalColumnSplit) + columnIndex;
				imageArray[columnIndex][rowIndex] = images[index];
			}
		}
				
		int jointImageWidth = ((totalColumnSplit - 1) * imageArray[0][0].getWidth()) + imageArray[totalColumnSplit-1][0].getWidth();
		int jointImageHeight = ((totalRowSplit - 1) * imageArray[0][0].getHeight()) + imageArray[0][totalRowSplit-1].getHeight();
		int type = imageArray[0][0].getType();
		String log = "Joint image of " + fileName + " is size: " + jointImageWidth + " x " + jointImageHeight;
		Main.addToLog(log);
		System.out.println(log);
		
		try {
			jointImage = new BufferedImage(jointImageWidth, jointImageHeight, type);
			Graphics2D graphics = jointImage.createGraphics();
		
			//joining images
			for(int rowIndex = 0; rowIndex < totalRowSplit; rowIndex++) {
				int subImageY = rowIndex * imageArray[0][0].getHeight();
				for(int columnIndex = 0; columnIndex < totalColumnSplit; columnIndex++) {
					int subImageX = columnIndex * imageArray[0][0].getWidth();
					
					int subImageWidth = imageArray[columnIndex][rowIndex].getWidth();
					int subImageHeight = imageArray[columnIndex][rowIndex].getHeight();							
					graphics.drawImage(imageArray[columnIndex][rowIndex], subImageX, subImageY, subImageWidth, subImageHeight, null);
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return jointImage;
	}
	
	private List<Elements> inspectDivivsors(){
		List<Elements> elements = new ArrayList<ImageSplit.Elements>();
		for(int divisor = 1; divisor <= (numOfSplits/2); divisor++) {
			if( (numOfSplits % divisor) == 0) {
				Elements element = new Elements();
				element.divisor = divisor;
				element.quotient = numOfSplits/divisor;
				element.difference = Math.abs(element.quotient - element.divisor);
				elements.add(element);
			}
		}
		return elements;
	}
	
	private class Elements{
		int difference;
		int divisor;
		int quotient;
	}
}
