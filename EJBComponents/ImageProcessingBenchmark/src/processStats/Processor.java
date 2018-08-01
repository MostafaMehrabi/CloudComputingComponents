package processStats;

public class Processor {

	public static void main(String[] args) {
		System.out.println("processing started");
		ProcessStats processor = new ProcessStats();
		processor.process();
		System.out.println("processing finished");
	}

}
