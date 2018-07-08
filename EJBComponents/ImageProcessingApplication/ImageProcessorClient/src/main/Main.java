package main;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import classes.Image;
import processingUnits.ImageSplit;

public class Main {
	
	public static void main(String[] args) {
		String fileName = "balloon_5";
		int numOfSubImages = 4;
		ImageSplit splitter = new ImageSplit(numOfSubImages, fileName);
		Processor processor = new Processor(splitter);
		Image[]	results = processor.process();
		BufferedImage[] images = new BufferedImage[results.length];
		for(int index = 0; index < results.length; index++) {
			images[index] = results[index].getImage();
		}
		BufferedImage result = splitter.join(images);
		String resultFileName = "." + File.separator + "output" + File.separator + fileName + ".jpg";
		try {
			File resultFile = new File(resultFileName);
			ImageIO.write(result, "jpg", resultFile);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

}
