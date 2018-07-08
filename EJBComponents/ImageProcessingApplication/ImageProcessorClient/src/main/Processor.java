package main;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;

import beans.RemoteErroder;
import beans.RemoteGrayScaler;
import classes.Image;
import processingUnits.ImageSplit;

public class Processor {
	private Connector connector = null;
	private RemoteGrayScaler grayScaler = null;
	private RemoteErroder erroder = null;
	private ImageSplit splitter = null;
	private int numberOfSubImages = 1;
	
	public Processor(ImageSplit imageSplit) {
		this.splitter = imageSplit;
		this.connector = new Connector();
	}
	
	public Image[] process() {
		try {
			BufferedImage[] bImages = splitter.split();
			numberOfSubImages = bImages.length;
			System.out.println("no of sub images: " + numberOfSubImages);
			Image[] images = new Image[numberOfSubImages];
			
			for(int index = 0; index < numberOfSubImages; index++) {
				Image image = new Image(bImages[index], index);
				images[index] = image;
			}
			
			grayScaler = (RemoteGrayScaler) connector.lookup("GrayScaler", "RemoteGrayScaler");
			erroder = (RemoteErroder) connector.lookup("Erroder", "RemoteErroder");		

			Future<Image>[] futureGrays = processGrayScale(images);
			System.out.println("waiting for the results of gray scalse");
			Image[] results = waitForFutures(futureGrays);
			Future<Image>[] futureErrosions = processErrosion(results);
			System.out.println("waiting for the results of errosions");
			Image[] errosions = waitForFutures(futureErrosions);
			return errosions;
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	private Future<Image>[] processGrayScale(Image[] images){
		Future<Image>[] futureGrays = new Future[numberOfSubImages];
		for(int index = 0; index < numberOfSubImages; index++) {
			Future<Image> futureGray = grayScaler.convert(images[index]);
			futureGrays[index] = futureGray;
		}
		return futureGrays;
	}

	@SuppressWarnings("unchecked")
	private Future<Image>[] processErrosion(Image[] images){
		Future<Image>[] futureErrosions = new Future[numberOfSubImages];
		for(int index = 0; index < numberOfSubImages; index++) {
			Future<Image> futureErrosion = erroder.errode(images[index]);
			futureErrosions[index] = futureErrosion;
		}
		return futureErrosions;
	}
	
	private Image[] waitForFutures(Future<Image>[] images) {
		int size = images.length;
		Image[] results = new Image[size];
		List<Future<Image>> futureList = new ArrayList<>();
		for(Future<Image> image : images) {
			futureList.add(image);
		}
		int count = 0;
		while(count < size) {
			Iterator<Future<Image>> iterator = futureList.iterator();
			while(iterator.hasNext()) {
				try {
					Future<Image> image = iterator.next();
					if(image.isDone()) {
						System.out.print(image.get().getMessage());
						System.out.println(", " + image.get().getStatus());
						results[count++] = image.get();
						iterator.remove();
					}else {
						Thread.sleep(10);
					}
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		return results;
	}
}
