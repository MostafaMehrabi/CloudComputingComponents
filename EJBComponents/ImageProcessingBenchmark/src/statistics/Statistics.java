package statistics;

import java.io.File;
import java.io.PrintWriter;

import classes.Image;

public class Statistics {
	private String[] benchmarkModes = null;
	private String statsFolderName = null;
	private String nameOfTest = null;
	public Statistics(String nameOfTest) {
		benchmarkModes = new String[3];
		benchmarkModes[0] = "Local"; benchmarkModes[1] = "RemoteOneNode"; benchmarkModes[2] = "RemoteTwoNodes";
		statsFolderName = "stats";
		this.nameOfTest = nameOfTest;
		createBaseFolders();	
	}
	
	private void createBaseFolders() {
		for(int index = 0; index < benchmarkModes.length; index++) {
			for(int subImageIndex = 1; subImageIndex <=16; subImageIndex++) {
				String subFolderName = getSubFolderPath(index, subImageIndex);
				try {
					File folder = new File(subFolderName);
					if(!folder.exists())
						folder.mkdirs();
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private String getHeadFolderPath(int mode) {
		String headFolderPath =  "." + File.separator + statsFolderName + File.separator + nameOfTest + File.separator + benchmarkModes[mode];
		return headFolderPath;
	}
	
	private String getSubFolderPath(int mode, int numberOfSubImages) {
		String subFolderPath = getHeadFolderPath(mode) + File.separator + "Threads_" + numberOfSubImages;
		return subFolderPath;
	}
	
	public void recordStatisticsForImage(int mode, int numberOfSubImages, int imageIndex, Image[] subImages) {
		String logFilePath = getSubFolderPath(mode, numberOfSubImages) + File.separator + "Image" + (imageIndex+1) + "_logFile.txt";
		try {
			PrintWriter writer = new PrintWriter(logFilePath);
			for(Image subImage : subImages) {
				writer.println(subImage.getMessage() + "\n");
			}
			writer.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void recordOverallBenchmarkTime(int mode, int numberOfSubImages, String overallLog) {
		String logFilePath = getSubFolderPath(mode, numberOfSubImages) + File.separator + "benchmarkLog.txt";
		try {
			PrintWriter writer = new PrintWriter(logFilePath);
			writer.println(overallLog);
			writer.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
