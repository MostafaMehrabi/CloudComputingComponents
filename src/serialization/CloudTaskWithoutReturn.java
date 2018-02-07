package serialization;

public abstract class CloudTaskWithoutReturn implements CloudTask{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6292878660314161120L;

	@Override
	public void doWork() {
		method();
	}
	
	public abstract void method();
}
