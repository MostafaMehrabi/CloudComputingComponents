package statistics;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import benchmark.Engine.Mode;
import classes.Image;

public class Statistics {
	private String[] benchmarkModes = null;
	private String statsFolderName = null;
	private String benchmarkName = null;
	private long overallTime = 0;
	
	public Statistics(String benchmarkName) {
		benchmarkModes = new String[2];
		benchmarkModes[0] = Mode.Local.toString(); benchmarkModes[1] = Mode.Remote.toString();
		statsFolderName = "stats";
		this.benchmarkName = benchmarkName;
		createBaseFolders();	
	}
	
	private void createBaseFolders() {
		for(int index = 0; index < benchmarkModes.length; index++) {
			String logFileName = getLogFileName(index);
			try {
				File folder = new File(logFileName);
				if(!folder.exists())
					folder.mkdirs();
				PrintWriter writer = new PrintWriter(logFileName);
				writer.println("sep=;");
				writer.println("Number of Sub-Images;Average Preparation;Average Processing;Overall");
				writer.flush();
				writer.close();
			}catch(Exception e) {
				e.printStackTrace();
			}			
		}
	}
	
	private String getHeadFolderPath(int mode) {
		String headFolderPath =  "." + File.separator + statsFolderName + File.separator + benchmarkName + File.separator + benchmarkModes[mode];
		return headFolderPath;
	}
	
	private String getLogFileName(int mode) {
		String subFolderPath = getHeadFolderPath(mode) + File.separator + "log.csv";
		return subFolderPath;
	}
	
	//complete this part
	public void recordStatisticsForImage(int mode, int numberOfSubImages, int imageIndex, Image[] subImages) {
		String logFilePath = getLogFileName(mode);
		try {
			FileWriter fileWriter = new FileWriter(logFilePath, true);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			PrintWriter writer = new PrintWriter(bufferedWriter);
			writer.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setOverallTime(long overallTime) {
		this.overallTime = overallTime;
	}
	
	//complete this part
	public void recordOverallBenchmarkTime(int mode, int numberOfSubImages, String overallLog) {
		String logFilePath = getLogFileName(mode) + File.separator + "benchmarkLog.txt";
		try {
			PrintWriter writer = new PrintWriter(logFilePath);
			writer.println(overallLog);
			writer.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
