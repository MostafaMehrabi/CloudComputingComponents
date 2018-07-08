package main;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import processingUnits.ImageErrosion;
import processingUnits.ImageGrayscale;
import processingUnits.ImageSplit;

public class Main {
	public static void main(String[] args) {
		String fileName = "balloon_1";
		ImageSplit splitter = new ImageSplit(12, fileName);
		BufferedImage[] images = null;
		try {
			images = splitter.split();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		for (int index = 0; index < images.length; index++) {
			String subImageName = "." + File.separator + "split" + File.separator + fileName + "-" + (index+1) + ".jpg";
			File outputFile = null;
			try {
				outputFile = new File(subImageName); 
				ImageIO.write(images[index], "jpg", outputFile);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("done with splitting");
		
		ImageGrayscale grayScaler = new ImageGrayscale();
		
		BufferedImage[] grayImages = new BufferedImage[images.length];
		for(int index = 0; index < images.length; index++) {
			BufferedImage grayImage = grayScaler.convert(images[index]);
			grayImages[index] = grayImage;
			String subImageName = "." + File.separator + "gray" + File.separator + fileName + "-" + (index+1) + ".jpg";
			File file = null;
			try {
				file = new File(subImageName);
				ImageIO.write(grayImage, "jpg", file);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("done with grayscale");
		
		BufferedImage[] errodedImages = new BufferedImage[images.length];
		for(int index = 0; index < images.length; index++) {
			ImageErrosion erroder = new ImageErrosion(grayImages[index]);
			BufferedImage errodedImage = erroder.errode();
			errodedImages[index] = errodedImage;
			String subImageName = "." + File.separator + "errosion" + File.separator + fileName + "-" + (index+1) + ".jpg";
			try {
				File file = new File(subImageName);
				ImageIO.write(errodedImage, "jpg", file);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("errosion done");
		
		BufferedImage joinImage = splitter.join(images);
		BufferedImage jointGrayImage = splitter.join(grayImages);
		BufferedImage errodedImage = splitter.join(errodedImages);
		
		String jointFileName = "." + File.separator + "joint" + File.separator + fileName + ".jpg";
		String jointGrayFileName = "." + File.separator + "joint" + File.separator + fileName + "-gray" + ".jpg";
		String jointErrodedFileName = "." + File.separator + "joint" + File.separator + fileName + "-errosion" + ".jpg";
		try {
			File jointImageFile = new File(jointFileName);
			File jointGrayFile = new File(jointGrayFileName);
			File jointErrodedFile = new File(jointErrodedFileName);
			ImageIO.write(joinImage, "jpg", jointImageFile);
			ImageIO.write(jointGrayImage, "jpg", jointGrayFile);
			ImageIO.write(errodedImage, "jpg", jointErrodedFile);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("done with joining");
	}	
}
