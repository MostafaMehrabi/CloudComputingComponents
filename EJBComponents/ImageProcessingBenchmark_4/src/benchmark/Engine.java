package benchmark;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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
		return processImage(imageName);
	}
	
	@SuppressWarnings("unchecked")
	private TaskIDGroup<Image> processImage(String imageName){
		TaskIDGroup<Image> taskIDs = new TaskIDGroup<>();
		
		//first split the image
		String fileName = "." + File.separator + "images" + File.separator + imageName;
		ImageSplit splitter = new ImageSplit(numberOfSubImages, fileName);
		ImageFactory imageFactory = new ImageFactory();

		BufferedImage[] bufferedImages = null;
		try {
			bufferedImages = splitter.split();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//process each image separately
		int imageNumber = 0;
		for(BufferedImage bufferedImage : bufferedImages) {
			//local task for preparing each sub-image
			TaskInfoTwoArgs<Image, BufferedImage, Integer> preparationTask = (TaskInfoTwoArgs<Image, BufferedImage, Integer>) ParaTask.asTask(TaskType.ONEOFF, 
					(FunctorTwoArgsWithReturn<Image, BufferedImage, Integer>)(x, y)->imageFactory.prepare(x, y));
			
			TaskID<Image> preparationTaskID = preparationTask.start(bufferedImage, (imageNumber++));
			
			//based on the mode, process the filtering local or remotely
			TaskID<Image> filteringTaskID = null;
			
			if(processingMode.equals(Mode.Local)) {
				filteringTaskID = filterLocal(preparationTaskID);
			}else {
				filteringTaskID = filterRemote(preparationTaskID);
			}
			
			taskIDs.addInnerTask(filteringTaskID);
		}
		return taskIDs;
	}
	
	@SuppressWarnings("unchecked")
	private TaskID<Image> filterLocal(TaskID<Image> preparationTaskID){
		Filter filter = new Filter();
		TaskInfoOneArg<Image, TaskID<Image>> filterTaskInfo = (TaskInfoOneArg<Image, TaskID<Image>>) ParaTask.asTask(TaskType.ONEOFF, 
				(FunctorOneArgWithReturn<Image, TaskID<Image>>)((x) -> filter.filter(x.getReturnResult())));
		ParaTask.registerDependences(filterTaskInfo, preparationTaskID);
		TaskID<Image> filterTaskID = filterTaskInfo.start(preparationTaskID);
		return filterTaskID;
	}
	
	private TaskID<Image> filterRemote(TaskID<Image> preparationTaskID){
		CloudTaskOneArg<Image, TaskID<Image>> filterTaskInfo;
		try {
			filterTaskInfo = new CloudTaskOneArg<>(false, nodeOneIP, httpPort, userName, nodeOnePassword, namingFactory, RemoteFilter.class, 
					RemoteFilter.class.getMethod("filter", Image.class));
			filterTaskInfo.setEJB("ImageProcessors", "ImageEJBs", "Filter", "beans.RemoteFilter");
			ParaTask.registerDependences(filterTaskInfo, preparationTaskID);
			TaskID<Image> filterTaskID = filterTaskInfo.start(preparationTaskID);
			return filterTaskID;
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return null;
	}	
}