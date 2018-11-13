package benchmark;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.imageio.ImageIO;

import benchmark.Engine.Mode;
import classes.Image;
import pt.runtime.ParaTask;
import pt.runtime.TaskIDGroup;
import statistics.Statistics;

public class Main {
	private static 	int numberOfSubImages = 16;
	private static List<TaskIDGroup<Image>> futureGroups = new ArrayList<>();
	private static List<String> imageNames = new ArrayList<>();
	private static int numberOfThreads = 8;
	private static String benchmarkName = "";
	private static Statistics statistics = null;
	private static Mode benchmarkMode = null;
	private static List<String> overallLog = null;
	
	public static void main(String[] args) {
	
		if(args.length != 0) {
			numberOfSubImages = Integer.parseInt(args[0]);
		}
	
		benchmarkName = "Threads_" + numberOfThreads; 
		ParaTask.init(numberOfThreads);
		
		for(int index = 1; index <= 32; index++) {
			String imageName = "image" + index + ".jpg";
			imageNames.add(imageName);
		}
		
		statistics = new Statistics(benchmarkName, imageNames);

		//for now we only have the two modes, third mode must be implemented.
		for(int modeNumber = 0; modeNumber < 1; modeNumber++) {
			
			if(modeNumber == 0)
				benchmarkMode = Mode.Local;
			else if(modeNumber == 1 )
				benchmarkMode = Mode.Remote;		
			
			overallLog = new ArrayList<>();
			
			System.out.println("Benchmark started in mode " + benchmarkMode.toString() + " with " + numberOfSubImages + " sub-images.");
			long start = System.currentTimeMillis();
			String startTime = getTime();

			Engine engine = new Engine(numberOfSubImages, imageNames, benchmarkMode);
			futureGroups = engine.process();
			System.out.println("received future groups");
			System.out.println("now joining them");
			List<Image> imageList = joinImages();
			String endTime = getTime();

			long end = System.currentTimeMillis();
			statistics.setOverallTime(end - start);
			statistics.recordBenchmarkStatistics(modeNumber, numberOfSubImages, imageList);
			
			addToLog(startTime);
			addToLog(endTime);
			statistics.recordOverallBenchmarkLog(modeNumber, numberOfSubImages, overallLog);
			System.out.println("Benchmark finished in mode " + benchmarkMode.toString() + " with " + numberOfSubImages + " sub-image.");
		}
	}
	
	private static List<Image> joinImages( ) {
		List<Image> imageList = new ArrayList<>();
		for(int index = 0; index < futureGroups.size(); index++) {
			TaskIDGroup<Image> futureGroup = futureGroups.get(index);
			 List<Image> images = joinSubImages(futureGroup, imageNames.get(index));
			 imageList.addAll(images);
		}
		return imageList;
	}
	
	public static void addToLog(String log) {
		overallLog.add(log);
	}
	
	private static List<Image> joinSubImages(TaskIDGroup<Image> futureGroup, String imageName) {
		String originalFileName = "." + File.separator + "images" + File.separator + imageName;
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
		
		String outputName = outputFolderName + File.separator + imageName;
		Image[] images = new Image[numberOfSubImages];

		BufferedImage jointImage = null;
		
		try {
			futureGroup.waitTillFinished();
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
		List<Image> imageList = new ArrayList<>();
		for(Image image : images) {
			imageList.add(image);
		}
		return imageList;
	}
	
	private static String getTime() {
		Calendar calendar = Calendar.getInstance();
		String time = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND);
		return time;
	}
}
