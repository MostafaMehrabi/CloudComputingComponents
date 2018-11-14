package statistics;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import benchmark.Engine.Mode;
import classes.Image;

public class Statistics {
	private String[] benchmarkModes = null;
	private String statsFolderName = null;
	private String benchmarkName = null;
	private String overallTime = "";
	
	public Statistics(String benchmarkName, List<String> fileNames) {
		benchmarkModes = new String[2];
		benchmarkModes[0] = Mode.Local.toString(); 
		benchmarkModes[1] = Mode.Remote.toString();
		statsFolderName = "stats";
		this.benchmarkName = benchmarkName;
		createBaseFolders(fileNames);	
	}
	
	private void createBaseFolders(List<String> fileNames) {
		for(int index = 0; index < benchmarkModes.length; index++) {
			String logFolderName = getLogFolderName(index);
			try {
				File folder = new File(logFolderName);
				if(!folder.exists())
					folder.mkdirs();
				createLogFile(index);
				createPrepTimeFile(index, fileNames);
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
			writer.println("Number of Sub-Images;Overall;Average Preparation;Average Filtering");
			writer.flush();
			writer.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void createPrepTimeFile(int mode, List<String> fileNames) {
		try {
			String prepFileName = getPrepFileName(mode);
			File prepFile = new File(prepFileName);
			if(prepFile.exists())
				return;
			PrintWriter writer = new PrintWriter(prepFileName);
			writer.println("sep=;");
			//remember that further writings must start from the second record. 
			writer.println("Preparation Time of Each Image for Num of Sub-Images;");
			for(String fileName : fileNames) {
				writer.println(fileName + ";");
			}
			writer.println("Benchmark Start;");
			writer.println("Benchmark End;");
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
	
	private String getPrepFileName(int mode) {
		String fileName = getLogFolderName(mode) + File.separator + "prepLog.csv";
		return fileName;
	}
	
	public void setOverallTime(long overallTime) {
		this.overallTime = getTimeInString(overallTime);
	}
	
	private String getTimeInString(long timeInMilliSeconds) {
		long minutes = timeInMilliSeconds / 60000;
		String strMins = (minutes > 9) ? Long.toString(minutes) : ("0" + Long.toString(minutes));
		
		long remainingMilliSeconds = timeInMilliSeconds % 60000;
		
		long seconds = remainingMilliSeconds / 1000;
		String strSecs = (seconds > 9) ? Long.toString(seconds) : ("0" + Long.toString(seconds));
		
		String strTime = strMins + "." + strSecs;
		return strTime;
	}
	
	//complete this part
	public void recordBenchmarkStatistics(int mode, int numberOfSubImages, List<Image> imageList) {
		String logFileName = getLogFileName(mode);
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
		
//		String averagePrepTime = getTimeInString(averagePrep);
//		String averageFilterTime = getTimeInString(averageFilter);
		
		try {
			FileWriter fileWriter = new FileWriter(logFileName, true);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			PrintWriter writer = new PrintWriter(bufferedWriter);
			String record = numberOfSubImages + ";" + overallTime + ";" + averagePrepTime + ";" + averageFilterTime;
			writer.println(record);
			writer.flush();
			writer.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}	
	
	//complete this part
	public void recordOverallBenchmarkLog(int mode, int numberOfSubImages, List<String> overallLog) {
		String logFileName = getPrepFileName(mode);
		List<String> records = new ArrayList<>();
		try {
			//read the content of the log file first, modify them, and then write them back to the log file
			FileReader fileReader = new FileReader(logFileName);
			BufferedReader reader = new BufferedReader(fileReader);
			String record = reader.readLine();
			records.add(record); //The first line is sep=; start from the next line
	
			record = reader.readLine(); 
			record += numberOfSubImages + ";";
			records.add(record);
			
			int overallLogIndex = 0;
			record = reader.readLine();
			while(record != null) {
				record += overallLog.get(overallLogIndex++) + ";";
				records.add(record);
				record = reader.readLine();
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
