package statistics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import benchmark.Engine.Mode;
import classes.Image;

public class Statistics {
	private String[] benchmarkModes = null;
	private String statsFolderName = null;
	private String benchmarkName = null;
	private String overallTimeMinutes = "";
	private String overallTimeSeconds = "";
	private String[] recordsPerImage = null;
			
	public Statistics(String benchmarkName, String imageName) {
		
		String[] temp = {"Overall runtime in minutes", "Overall runtime in seconds", "Averge prep. time for each sub-image", 
				"Average filter time for each sub-image", "Start time", "End time"};
		
		int size = temp.length + 1;
		recordsPerImage = new String[size];
		
		recordsPerImage[0] = imageName;
		int index = 1;
		for(String element : temp) {
			recordsPerImage[index++] = element;
		}
		
		benchmarkModes = new String[2];
		benchmarkModes[0] = Mode.Local.toString(); 
		benchmarkModes[1] = Mode.Remote.toString();
		statsFolderName = "stats";
		this.benchmarkName = benchmarkName;
		createBaseFolders();	
	}
	
	private void createBaseFolders() {
		for(int index = 0; index < benchmarkModes.length; index++) {
			String logFolderName = getLogFolderName(index);
			try {
				File folder = new File(logFolderName);
				if(!folder.exists())
					folder.mkdirs();
				createLogFile(index);
			}catch(Exception e) {
				e.printStackTrace();
			}			
		}
	}
	
	private void createLogFile(int mode) {
		try {
			String logFileName = getLogFileName(mode);
			File logFile = new File(logFileName);
			if (logFile.exists())
				return;
			PrintWriter writer = new PrintWriter(logFileName);
			writer.println("sep=;");
			writer.flush();
			writer.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	private String getLogFolderName(int mode) {
		String headFolderPath =  "." + File.separator + statsFolderName + File.separator + benchmarkName + File.separator + benchmarkModes[mode];
		return headFolderPath;
	}
	
	private String getLogFileName(int mode) {
		String fileName = getLogFolderName(mode) + File.separator + "log.csv";
		return fileName;
	}	
	
	
	public void setOverallTime(long overallTime) {
		this.overallTimeMinutes = getTimeInString(overallTime);
		this.overallTimeSeconds = Long.toString(overallTime);
	}
	
	private String getTimeInString(long timeInMilliSeconds) {
		long minutes = timeInMilliSeconds / 60000;
		String strMins = (minutes > 9) ? Long.toString(minutes) : ("0" + Long.toString(minutes));
		
		long remainingMilliSeconds = timeInMilliSeconds % 60000;
		
		long seconds = remainingMilliSeconds / 1000;
		
		long secondsWRTMinutes = (seconds * 100) / 60;
		String strSecs = (secondsWRTMinutes > 9) ? Long.toString(secondsWRTMinutes) : ("0" + Long.toString(secondsWRTMinutes));
		
		String strTime = strMins + "." + strSecs;
		return strTime;
	}
	
	//complete this part
	private long[] getAverageTimes(List<Image> imageList) {
		int numberOfElements = imageList.size();
		long totalPrepTime = 0; long totalFilterTime = 0;
		
		for(Image image : imageList) {
			String imageMessage = image.getMessage();
			String[] times = imageMessage.split(" ");
			totalPrepTime += Long.parseLong(times[0].trim());
			totalFilterTime += Long.parseLong(times[1].trim());
		}
		
		long averagePrepTime = totalPrepTime/numberOfElements;
		long averageFilterTime = totalFilterTime/numberOfElements; 
		long[] averageTimes = new long[2];
		averageTimes[0] = averagePrepTime; averageTimes[1] = averageFilterTime;
		return averageTimes;
	}	
	
	//complete this part
	public void recordOverallBenchmarkLog(int mode, int imageIndex, int numberOfSubImages, List<Image> imageList, List<String> overallLog) {
		
		long[] averageTimes = getAverageTimes(imageList);
		String averagePrepTime = Long.toString(averageTimes[0]);
		String averageFilterTime = Long.toString(averageTimes[1]);

		String startTime = overallLog.get(0);
		String endTime = overallLog.get(1);

		int numberOfRecordsPerImage = recordsPerImage.length + 1; // extra one for an additional space before each image record
		int offset = (imageIndex - 1) * numberOfRecordsPerImage;
		
		String logFileName = getLogFileName(mode);
		List<String> records = new ArrayList<>();
		try {
			//read the content of the log file first, modify them, and then write them back to the log file
			FileReader fileReader = new FileReader(logFileName);
			BufferedReader reader = new BufferedReader(fileReader);
			
			String record = reader.readLine();
			records.add(record); //The first line is sep=; start from the next line
	
			for(int offsetIndex = 0; offsetIndex < offset; offsetIndex++) {
				//skip the records that have been made for previously processed images.
				record = reader.readLine();
				records.add(record);
			}				
			
			record = reader.readLine();
			if(record == null) { //this is the first time that the stats of this image are being recorded
				records.add("");
				//1. record the number of sub-images
				record = recordsPerImage[0] + ";" + numberOfSubImages + ";";
				records.add(record);
				
				//2. record the overall runtime in minutes
				record = recordsPerImage[1] + ";" + overallTimeMinutes + ";";
				records.add(record);
				
				//3. record the overall runtime in seconds
				record = recordsPerImage[2] + ";" + overallTimeSeconds + ";";
				records.add(record);
				
				//4. record the average prep. time for each sub-image of this image
				record = recordsPerImage[3] + ";" + averagePrepTime + ";";
				records.add(record);
				
				//5. record the average filtering time for each sub-image of this image
				record = recordsPerImage[4] + ";" + averageFilterTime + ";";
				records.add(record);
				
				//6. record the start time 
				record = recordsPerImage[5] + ";" + startTime + ";";
				records.add(record);
				
				//7. record the end time
				record = recordsPerImage[6] + ";" + endTime + ";";
				records.add(record);
			}
			else { // these are the information for subsequent sub-images.
				   // remember that the first line that has been read is a blank space.
				records.add(record);
				
				//1. record the number of sub-images
				record = reader.readLine(); 
				record += numberOfSubImages + ";";
				records.add(record);
				
				//2. record the overall time in minutes
				record = reader.readLine();
				record += overallTimeMinutes + ";";
				records.add(record);
				
				//3. record the overall time in seconds
				record = reader.readLine();
				record += overallTimeSeconds + ";";
				records.add(record);
				
				//4. record the average prep. time for each sub-image of this image
				record = reader.readLine();
				record += averagePrepTime + ";";
				records.add(record);
				
				//5. record the average filtering time for each sub-image of this image
				record = reader.readLine();
				record += averageFilterTime + ";"; 
				records.add(record);
				
				//6. record the start time
				record = reader.readLine();
				record += startTime + ";";
				records.add(record);
				
				//7. record the end time
				record = reader.readLine();
				record += endTime + ";";
				records.add(record);				
			}					
			reader.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		try {
			PrintWriter writer = new PrintWriter(logFileName);
			for(String record : records) {
				writer.println(record);
			}
			writer.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
