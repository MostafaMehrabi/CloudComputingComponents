package serialization;

import java.io.Serializable;

public class ClassOne implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1412980828488878012L;

	int value = 0;
	
	public ClassOne(int value) {
		this.value = value;
	}
	
	public void setValue(int val) {
		value = val;
	}
	
	public int getValue() {
		return value;
	}
}
