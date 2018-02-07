package serialization;

import java.io.Serializable;

public abstract class CloudTaskWithReturn<R extends Serializable> implements CloudTask {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4836708634221450568L;
	R result;
	boolean resultIsReady;
	public CloudTaskWithReturn( ) {
		result = null;
		resultIsReady = false;
	}
	
	@Override
	public void doWork() {
		result = method();
		resultIsReady = true;
	}
	
	public abstract R method();
	
	/**
	 * Because sending boolean objects over network is faster, local runtime
	 * components can query if the result is ready, and only it retrieves the
	 * return result if it is ready for retrieval. 
	 * @return
	 */
	public boolean isResultReady() {
		return resultIsReady;
	}
	
	public R getResult() {
		return result;
	}
}
