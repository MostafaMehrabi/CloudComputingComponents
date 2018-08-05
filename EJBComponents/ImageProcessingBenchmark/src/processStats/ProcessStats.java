package processStats;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;


public class ProcessStats {
	
	private String baseStatsFolder = null;
	private String[] benchmarkFolders = null;
	private String[] benchmarkSubFolders = null;
	private String[] imageLogFileNames = null;
	private String delimiter = null;
	private String preparationStartTime = null;
	private String preparationEndTime = null;
	private String grayscalingStartTime = null;
	private String grayScalingEndTime = null;
	private String errosionStartTime = null;
	private String errosionEndTime = null;
	
	public ProcessStats() {
		this.baseStatsFolder = "." + File.separator + "stats";
		
		this.delimiter = ";";
		
		this.benchmarkFolders = new String[3];
		benchmarkFolders[0]= "resourceContention"; benchmarkFolders[1] = "fourPhysicalCoresOnly"; benchmarkFolders[2] = "eightHyperThreads";
		
		this.benchmarkSubFolders = new String[2];
		benchmarkSubFolders[0] = "Local";  benchmarkSubFolders[1] = "RemoteOneNode";
	
		this.imageLogFileNames = new String[5];
		imageLogFileNames[0] = "Image1_LogFile.txt"; imageLogFileNames[1] = "Image2_LogFile.txt"; imageLogFileNames[2] = "Image3_LogFile.txt"; 
		imageLogFileNames[3] = "Image4_LogFile.txt"; imageLogFileNames[4] = "Image5_LogFile.txt";
	}

	public void process() {		
			for(int folderIndex = 0; folderIndex < benchmarkFolders.length; folderIndex++) {
				String writingPath = baseStatsFolder + File.separator + benchmarkFolders[folderIndex];
				processSubImage(writingPath, folderIndex, 1);
				for(int numOfSubImages = 2; numOfSubImages <= 16; numOfSubImages += 2) {
					processSubImage(writingPath, folderIndex, numOfSubImages);
				}				
			}		
	}
	
	private void processSubImage(String writingPath, int folderIndex, int numOfSubImages) {
		try {
			String analysisFileName = writingPath + File.separator + "analysis-" + numOfSubImages + "-subImages.csv";
			
			//determine the list of sub-images that appears in front of each image
			String listOfSubImages = "";
			for(int subImageIndex = 0; subImageIndex < numOfSubImages; subImageIndex++) {
				listOfSubImages += "sub-image " + subImageIndex + delimiter + "duration" + delimiter + "waiting for next stage" + delimiter;
			}
			
			PrintWriter writer = new PrintWriter(analysisFileName); 
			writer.println("sep=" + delimiter);
			
			//go through the sub-folders "Local" and "RemoteOneNode"
			for(int subFolderIndex = 0; subFolderIndex < benchmarkSubFolders.length; subFolderIndex++) {
				String readFolder = baseStatsFolder + File.separator + benchmarkFolders[folderIndex] + File.separator + benchmarkSubFolders[subFolderIndex] + File.separator + "Threads_" + numOfSubImages;
				String title = numOfSubImages + "sub-images - time duration for each phase and the corresponding threads - " + benchmarkSubFolders[subFolderIndex];
				writer.println(title);
				
				//we are now in the related number of threads folder, now open the image log files. 
				for(int imageIndex = 0; imageIndex < imageLogFileNames.length; imageIndex++) {
					
					String subTitle = "image" + (imageIndex+1) + delimiter + listOfSubImages;
					writer.println(subTitle);
					
					String logFileName = readFolder + File.separator + imageLogFileNames[imageIndex];
					BufferedReader reader = new BufferedReader(new FileReader(logFileName));
					
					String preparationLine = "preparation" + delimiter;
					String grayScalingLine = "grayscaling" + delimiter;
					String errosionLine = "errosion" + delimiter;
					
					for(int subImageIndex = 0; subImageIndex < numOfSubImages; subImageIndex++) {
						String preparationString = getPreparationString(reader.readLine());
						String grayScalingString = getGrayScalingString(reader.readLine());
						String errosionString = getErrosionString(reader.readLine());
						
						preparationString += delimiter + getTimeDifference(preparationEndTime, grayscalingStartTime) + delimiter;
						grayScalingString += delimiter + getTimeDifference(grayScalingEndTime, errosionStartTime) + delimiter;
						errosionString += delimiter + delimiter;
						
						preparationLine += preparationString; 
						grayScalingLine += grayScalingString;
						errosionLine    += errosionString;
						
						if(subImageIndex != (numOfSubImages-1)) {
							//skip the next two lines.
							reader.readLine();
							reader.readLine();
						}
					}
	
					reader.close();
					writer.println(preparationLine);
					writer.println(grayScalingLine);
					writer.println(errosionLine);
					writer.println("\n");
				}
				
			}
			writer.flush();
			writer.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private String getPreparationString(String line) {
		String[] words = line.split(" ");
	
		String[] temp = words[6].split(",");
		preparationStartTime = temp[0];
		preparationEndTime = words[8];
		
		String preparationString = preparationStartTime + " - " + preparationEndTime + delimiter + getTimeDifference(preparationStartTime, preparationEndTime);
		return preparationString;
	}
	
	private String getGrayScalingString(String line) {
		String[] words = line.split(" ");
	
		String[] temp = words[6].split(",");
		grayscalingStartTime = temp[0];
		grayScalingEndTime = words[9];
		
		temp = words[12].split("\\.");
		String threadNumber = temp[0];
		
		String grayScalingString = grayscalingStartTime + " - " + grayScalingEndTime + " (" + threadNumber + ")" + delimiter + getTimeDifference(grayscalingStartTime, grayScalingEndTime);
		return grayScalingString;
	}
	
	private String getErrosionString(String line) {
		String[] words = line.split(" ");

		String[] temp = words[6].split(",");
		errosionStartTime = temp[0];
		errosionEndTime = words[9];
		
		temp = words[12].split("\\.");
		String threadNumber = temp[0];
		
		String errosionString = errosionStartTime + " - " + errosionEndTime + " (" + threadNumber + ")" + delimiter + getTimeDifference(errosionStartTime, errosionEndTime);
		return errosionString;
	}
	
	private int getTimeDifference(String timeOne, String timeTwo) {
		String[] timeElements = timeOne.split(":");
		
		int hourOne = Integer.parseInt(timeElements[0]);
		int minuteOne = Integer.parseInt(timeElements[1]);
		int secondOne = Integer.parseInt(timeElements[2]);

		int timeOneInSeconds = getTimeInSeconds(hourOne, minuteOne, secondOne);
		
		timeElements =  timeTwo.split(":");
		int hourTwo = Integer.parseInt(timeElements[0]);
		int minuteTwo = Integer.parseInt(timeElements[1]);
		int secondTwo = Integer.parseInt(timeElements[2]);
		
		int timeTwoInSeconds = getTimeInSeconds(hourTwo, minuteTwo, secondTwo);
		
		int timeDifference = Math.abs (timeTwoInSeconds - timeOneInSeconds); 
		
		return timeDifference;
	}
	
	private String getTimeDifferenceInTimeFormat(int timeDifference) {
		return getSecondsInTime(timeDifference);
	}
	
	private int getTimeInSeconds(int hours, int minutes, int seconds) {
		//the time in remote system may be recorded in 24 hours while the local machine in 12 hours, and vice versa
		hours = (hours > 12) ? (hours - 12) : hours; 
		return ((hours * 3600) + (minutes * 60) + seconds);
	}
	
	private String getSecondsInTime(int totalSeconds) {
		int hours = totalSeconds / 3600;
		int remainder = totalSeconds % 3600;
		int minutes = remainder / 60;
		int seconds = remainder % 60;

		String hoursString = (hours > 0) ? (String.valueOf(hours) + ":") : "";
		String timeInString = hoursString  + String.valueOf(minutes) + ":" + String.valueOf(seconds);
		return timeInString;
	}
}
