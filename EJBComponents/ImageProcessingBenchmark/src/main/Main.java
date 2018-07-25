package main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.imageio.ImageIO;

import classes.Image;
import main.Engine.Mode;
import processingUnits.ImageSplit;
import pt.runtime.ParaTask;
import pt.runtime.TaskIDGroup;
import statistics.Statistics;

public class Main {
	private static 	int numberOfSubImages = 16;
	private static List<TaskIDGroup<Image>> futureGroups = new ArrayList<>();
	private static List<String> imageNames = new ArrayList<>();
	private static String benchmarkName = "fourPhysicalCoresOnly";
	private static Statistics statistics = null;
	private static Mode benchmarkMode = null;
	private static String overallLog = "";
	public static void main(String[] args) {
	
		if(args.length != 0) {
			numberOfSubImages = Integer.parseInt(args[0]);
		}
	
		ParaTask.init(4);
		imageNames.add("image1");
		imageNames.add("image2");
		imageNames.add("image3");
		imageNames.add("image4");
		imageNames.add("image5");
//		imageNames.add("image6"); too large
		
		statistics = new Statistics(benchmarkName);

		//for now we only have the two modes, third mode must be implemented.
		for(int modeNumber = 0; modeNumber < 2; modeNumber++) {
			
			if(modeNumber == 0)
				benchmarkMode = Mode.Local;
			else if(modeNumber == 1 )
				benchmarkMode = Mode.RemoteOneNode;
			else if (modeNumber == 2)
				benchmarkMode = Mode.RemoteTwoNodes;
			
			overallLog = "";
			
			System.out.println("Benchmark started in mode " + benchmarkMode.toString() + " with " + numberOfSubImages + " sub-images.");
			String startTime = getTime();
			Engine engine = new Engine(numberOfSubImages, imageNames, benchmarkMode);
			futureGroups = engine.process();
			System.out.println("received future groups");
			System.out.println("now joining them");
			joinImages(modeNumber);
			String endTime = getTime();
			addToLog("benchmark started: " + startTime);
			addToLog("benchmark ended: " + endTime);
			statistics.recordOverallBenchmarkTime(modeNumber, numberOfSubImages, overallLog);
			System.out.println("Benchmark finished in mode " + benchmarkMode.toString() + " with " + numberOfSubImages + " sub-image.");
		}
	}
	
	private static void joinImages(int mode) {
		for(int index = 0; index < futureGroups.size(); index++) {
			TaskIDGroup<Image> futureGroup = futureGroups.get(index);
			Image[] subImages = joinSubImages(futureGroup, imageNames.get(index));
			statistics.recordStatisticsForImage(mode, numberOfSubImages, index, subImages);
		}
	}
	
	public static void addToLog(String log) {
		overallLog += (log + "\n");
	}
	
	private static Image[] joinSubImages(TaskIDGroup<Image> futureGroup, String imageName) {
		String originalFileName = "." + File.separator + "images" + File.separator + imageName + ".jpg";
		String outputFolderName = "." + File.separator + "output" + File.separator + benchmarkName + File.separator 
																		+ benchmarkMode.toString() + File.separator + numberOfSubImages;
		try {
			File outputFolder = new File(outputFolderName);
			if(!outputFolder.exists()) {
				outputFolder.mkdirs();
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		String outputName = outputFolderName + File.separator + imageName + ".jpg";
		Image[] images = new Image[numberOfSubImages];

		BufferedImage jointImage = null;
		
		try {
			futureGroup.waitTillFinished();
//			for(int index = 0; index < numberOfSubImages; index++ ) {
//				images[index] = futureGroup.getInnerTaskResult(index);
//			}
			images = futureGroup.getResultsAsArray(images);
			ImageSplit joiner = new ImageSplit(numberOfSubImages, originalFileName);
			joiner.split(); //sets up the parameters of the component.
			BufferedImage[] bufferedImages = new BufferedImage[images.length];
			
			for(int index = 0; index < images.length; index++) {
				//record the messages from these images in their corresponding folder under statistics. 
				//the folders under statistics are named based on the name of the test. 
				bufferedImages[index] = images[index].getImage();
			}
			
			jointImage = joiner.join(bufferedImages);
			
			File outputFile = new File(outputName);
			ImageIO.write(jointImage, "jpg", outputFile);
		}catch(Exception e) {
			e.printStackTrace();
		}		
		return images;
	}
	
	private static String getTime() {
		Calendar calendar = Calendar.getInstance();
		String time = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND);
		return time;
	}
}
