package main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import beans.RemoteErroder;
import beans.RemoteGrayScaler;
import classes.Image;
import localpt.LocalErroder;
import localpt.LocalGrayScaler;
import processingUnits.ImageSplit;
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
	private List<String> imageNames = null;
	private List<TaskIDGroup<Image>> futureGroups = null;
	private String namingFactory = null;
	
	public enum Mode {Local, RemoteOneNode, RemoteTwoNodes};
	private Mode processingMode = null;
	private String userName = null;
	private String nodeOnePassword = null;
	private String nodeTwoPassword = null;
	private String nodeOneIP = null;
	private String nodeTwoIP = null;
	private String httpPort = null;
	
	public Engine(int numberOfSubImages, List<String> imageNames, Mode processingMode) {
		this.numberOfSubImages = numberOfSubImages;
		this.imageNames = imageNames;
		this.processingMode = processingMode;
		this.futureGroups = new ArrayList<>();
		this.namingFactory = "weblogic.jndi.WLInitialContextFactory";
		this.userName = "jcsAdmin";
		this.nodeOneIP = "129.154.72.130";
		this.nodeTwoIP = "129.154.70.244";
		this.nodeOnePassword = "XGulvCkb6_";
		this.nodeTwoPassword = "XyvFHUwm4_";
		this.httpPort = "80";
	}
	
	public List<TaskIDGroup<Image>> process(){
		for(String imageName : imageNames) {
			List<Image> images = prepareSubImages(imageName);
			TaskIDGroup<Image> futureGroup = processSubImages(images);
			futureGroups.add(futureGroup);
		}
		return futureGroups;
	}
	
	@SuppressWarnings("unchecked")
	private List<Image> prepareSubImages(String imageName){
		long start = System.currentTimeMillis();
		TaskIDGroup<Image> preparationTasks = new TaskIDGroup<>();
		List<Image> images = new ArrayList<>();
		
		try {
			String fileName = "." + File.separator + "images" + File.separator + imageName + ".jpg";
			ImageSplit splitter = new ImageSplit(numberOfSubImages, fileName);
			BufferedImage[] bufferedImages = splitter.split();
			ImageFactory imageFactory = new ImageFactory();
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
//			for(int index = 0; index < numberOfSubImages; index++) {
//				System.out.println("waiting for the " + index + "th sub-image.");
//				imageArray[index] = preparationTasks.getInnerTaskResult(index);
//				System.out.println("the " + index + "th sub-image retrieved.");
//			}
			
			for(Image image : imageArray) {
				images.add(image);
			}
			
			long end = System.currentTimeMillis();
			String log = "sub-images preparation for " + imageName + " took: " + (end - start);
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
		else if(processingMode.equals(Mode.RemoteOneNode)) {
			ParaTask.setCloudMode(true);
			return processRemoteOneNode(images);
		}else if(processingMode.equals(Mode.RemoteTwoNodes)) {
			ParaTask.setCloudMode(true);
			return processRemoteTwoNodes(images);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private TaskIDGroup<Image> processLocal(List<Image> images){
		TaskIDGroup<Image> processedImages = new TaskIDGroup<>();
		LocalGrayScaler grayScaler = new LocalGrayScaler();
		LocalErroder erroder = new LocalErroder();
		
		for(Image image : images) {
			TaskInfoOneArg<Image, Image> grayTaskInfo = (TaskInfoOneArg<Image, Image>) ParaTask.asTask(TaskType.ONEOFF, (FunctorOneArgWithReturn<Image, Image>)((x) -> grayScaler.convert(x)));
			TaskID<Image> grayTask = grayTaskInfo.start(image);
			
			TaskInfoOneArg<Image, TaskID<Image>> errosionTaskInfo = (TaskInfoOneArg<Image, TaskID<Image>>) ParaTask.asTask(TaskType.ONEOFF, (FunctorOneArgWithReturn<Image, TaskID<Image>>)
																																			((x) -> erroder.errode(x.getReturnResult())));
			ParaTask.registerDependences(errosionTaskInfo, grayTask);
			TaskID<Image> errosionTask = errosionTaskInfo.start(grayTask);

			processedImages.addInnerTask(errosionTask);
		}
		return processedImages;
	}
	
	private TaskIDGroup<Image> processRemoteOneNode(List<Image> images){
		TaskIDGroup<Image> processedImages = new TaskIDGroup<>();
		try {
			for(Image image : images) {
				CloudTaskOneArg<Image, Image> grayCloudTask = new CloudTaskOneArg<>(false, nodeOneIP, httpPort, userName, nodeOnePassword, namingFactory, RemoteGrayScaler.class, 
						RemoteGrayScaler.class.getMethod("convert", Image.class));
				grayCloudTask.setEJB("ImageProcessors", "ImageEJBs", "GrayScaler", "beans.RemoteGrayScaler");
				TaskID<Image> grayTask = grayCloudTask.start(image);
				
				CloudTaskOneArg<Image, TaskID<Image>> errosionTaskInfo = new CloudTaskOneArg<>(false, nodeOneIP, httpPort, userName, nodeOnePassword, namingFactory, RemoteErroder.class,
						RemoteErroder.class.getMethod("errode", Image.class));
				errosionTaskInfo.setEJB("ImageProcessors", "ImageEJBs", "Erroder", "beans.RemoteErroder");
				ParaTask.registerDependences(errosionTaskInfo, grayTask);
				TaskID<Image> errosionTask = errosionTaskInfo.start(grayTask);
				
				processedImages.addInnerTask(errosionTask);
			}
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return processedImages;
	}
	
	private TaskIDGroup<Image> processRemoteTwoNodes(List<Image> images){
		TaskIDGroup<Image> processedImages = new TaskIDGroup<>();
		try {
			int threshold = images.size() / 2;
			for(int index = 0; index < images.size(); index++) {
				Image image = images.get(index);
				String tempIP = (index <= threshold) ? nodeOneIP : nodeTwoIP;
				String tempPassword = (index <= threshold) ? nodeOnePassword : nodeTwoPassword;
				
				CloudTaskOneArg<Image, Image> grayCloudTask = new CloudTaskOneArg<>(false, tempIP, httpPort, userName, tempPassword, namingFactory, RemoteGrayScaler.class, 
						RemoteGrayScaler.class.getMethod("convert", Image.class));
				grayCloudTask.setEJB("ImageProcessors", "ImageEJBs", "GrayScaler", "beans.RemoteGrayScaler");
				TaskID<Image> grayTask = grayCloudTask.start(image);
				
				CloudTaskOneArg<Image, TaskID<Image>> errosionTaskInfo = new CloudTaskOneArg<>(false, tempIP, httpPort, userName, tempPassword, namingFactory, RemoteErroder.class,
						RemoteErroder.class.getMethod("errode", Image.class));
				errosionTaskInfo.setEJB("ImageProcessors", "ImageEJBs", "Erroder", "beans.RemoteErroder");
				ParaTask.registerDependences(errosionTaskInfo, grayTask);
				TaskID<Image> errosionTask = errosionTaskInfo.start(grayTask);
				
				processedImages.addInnerTask(errosionTask);
			}
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return processedImages;
	}
}
