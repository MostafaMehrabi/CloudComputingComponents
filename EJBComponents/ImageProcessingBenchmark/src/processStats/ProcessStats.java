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
		try {
			for(int folderIndex = 0; folderIndex < benchmarkFolders.length; folderIndex++) {
				String writingPath = baseStatsFolder + File.separator + benchmarkFolders[folderIndex];
				
				for(int numOfSubImages = 2; numOfSubImages <= 16; numOfSubImages += 2) {
					String analysisFileName = writingPath + File.separator + "analysis-" + numOfSubImages + "-subImages.csv";
					
					//determine the list of sub-images that appears in front of each image
					String listOfSubImages = "";
					for(int subImageIndex = 0; subImageIndex < numOfSubImages; subImageIndex++) {
						listOfSubImages += "sub-image " + subImageIndex + delimiter;
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
								preparationLine += getPreparationString(reader.readLine());
								grayScalingLine += getGrayScalingString(reader.readLine());
								errosionLine    += getErrosionString(reader.readLine());
								
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
				}				
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private String getPreparationString(String line) {
		String[] words = line.split(" ");
	
		String[] temp = words[6].split(",");
		words[6] = temp[0];
		
		String preparationString = words[6] + " - " + words[8] + delimiter;
		return preparationString;
	}
	
	private String getGrayScalingString(String line) {
		String[] words = line.split(" ");
	
		String[] temp = words[6].split(",");
		words[6] = temp[0];
		
		String grayScalingString = words[6] + " - " + words[9] + " (" + words[12] + ")" + delimiter;
		return grayScalingString;
	}
	
	private String getErrosionString(String line) {
		String[] words = line.split(" ");

		String[] temp = words[6].split(",");
		words[6] = temp[0];
		
		String errosionString = words[6] + " - " + words[9] + " (" + words[12] + ")" + delimiter;
		return errosionString;
	}
}
