package benchmark;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import beans.RemoteFilter;
import classes.Image;
import pt.functionalInterfaces.FunctorOneArgWithReturn;
import pt.functionalInterfaces.FunctorTwoArgsWithReturn;
import pt.runtime.CloudTaskOneArg;
import pt.runtime.ParaTask;
import pt.runtime.ParaTask.TaskType;
import pt.runtime.TaskID;
import pt.runtime.TaskIDGroup;
import pt.runtime.TaskInfoOneArg;
import pt.runtime.TaskInfoTwoArgs;

public class Engine {
	private int numberOfSubImages = 1;
	private String imageName = "";
	private String namingFactory = null;
	
	public enum Mode {Local, Remote};
	private Mode processingMode = null;
	private String userName = null;
	private String nodeOnePassword = null;
	private String nodeOneIP = null;
	private String httpPort = null;
	
	public Engine(int numberOfSubImages, String imageName, Mode processingMode) {
		this.numberOfSubImages = numberOfSubImages;
		this.imageName = imageName;
		this.processingMode = processingMode;
		this.namingFactory = "weblogic.jndi.WLInitialContextFactory";
		this.userName = "weblogic";
		this.nodeOneIP = "144.21.81.76";
		this.nodeOnePassword = "Khoda110";
		this.httpPort = "80";
	}
	
	public TaskIDGroup<Image> process(){
		List<Image> images = prepareSubImages(imageName);
		TaskIDGroup<Image> futureGroup = processSubImages(images);
		return futureGroup;
	}
	
	@SuppressWarnings("unchecked")
	private List<Image> prepareSubImages(String imageName){
		long start = 0;
		TaskIDGroup<Image> preparationTasks = new TaskIDGroup<>();
		List<Image> images = new ArrayList<>();
		
		try {
			String fileName = "." + File.separator + "images" + File.separator + imageName;
			ImageSplit splitter = new ImageSplit(numberOfSubImages, fileName);
			BufferedImage[] bufferedImages = splitter.split();
			ImageFactory imageFactory = new ImageFactory();
			start =  System.currentTimeMillis();
			for(int imageNumber = 0; imageNumber < bufferedImages.length; imageNumber++) {
				BufferedImage bufferedImage = bufferedImages[imageNumber];
				TaskInfoTwoArgs<Image, BufferedImage, Integer> preparationTask = (TaskInfoTwoArgs<Image, BufferedImage, Integer>) ParaTask.asTask(TaskType.ONEOFF, 
						(FunctorTwoArgsWithReturn<Image, BufferedImage, Integer>)(x, y)->imageFactory.prepare(x, y));
				
				TaskID<Image> taskId = preparationTask.start(bufferedImage, imageNumber);
				preparationTasks.addInnerTask(taskId);
			}
			
			preparationTasks.waitTillFinished();
			Image[] imageArray = new Image[numberOfSubImages];
			imageArray = preparationTasks.getResultsAsArray(imageArray);
			
			for(Image image : imageArray) {
				images.add(image);
			}
			
			long end = System.currentTimeMillis();
			String log = Long.toString(end - start);
			Main.addToLog(log);
			System.out.println(log);
			return images;
		}catch(Exception e) {
			System.err.println("ERROR ENCOUNTERED WHILE PROCESSING IMAGE " + imageName);
			e.printStackTrace();
			return null;
		}
	}
	
	private TaskIDGroup<Image> processSubImages(List<Image> images){
		if(processingMode.equals(Mode.Local))
			return processLocal(images);
		else if(processingMode.equals(Mode.Remote)) {
			ParaTask.setCloudMode(true);
			return processRemoteOneNode(images);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private TaskIDGroup<Image> processLocal(List<Image> images){
		TaskIDGroup<Image> processedImages = new TaskIDGroup<>();
		Filter filter = new Filter();
		
		for(Image image : images) {
			TaskInfoOneArg<Image, Image> filterTaskInfo = (TaskInfoOneArg<Image, Image>) ParaTask.asTask(TaskType.ONEOFF, 
					(FunctorOneArgWithReturn<Image, Image>)((x) -> filter.filter(x)));
			TaskID<Image> filterTask = filterTaskInfo.start(image);
			
			processedImages.addInnerTask(filterTask);
		}
		
		return processedImages;
	}
	
	private TaskIDGroup<Image> processRemoteOneNode(List<Image> images){
		TaskIDGroup<Image> processedImages = new TaskIDGroup<>();
		try {
			for(Image image : images) {
				CloudTaskOneArg<Image, Image> filterTaskInfo = new CloudTaskOneArg<>(false, nodeOneIP, httpPort, userName, nodeOnePassword, namingFactory, RemoteFilter.class, 
						RemoteFilter.class.getMethod("filter", Image.class));
				filterTaskInfo.setEJB("ImageProcessors", "ImageEJBs", "Filter", "beans.RemoteFilter");
				TaskID<Image> filterTask = filterTaskInfo.start(image);
								
				processedImages.addInnerTask(filterTask);
			}
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}		
		return processedImages;
	}	
}
