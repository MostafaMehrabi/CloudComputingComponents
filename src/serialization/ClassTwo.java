package serialization;

import java.io.Serializable;

public class ClassTwo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2794681151145124023L;
	
	String value = null;
	
	public ClassTwo(String value) {
		this.value = value;
	}
	
	public void setValue(String val) {
		value = val;
	}
	
	public String getValue() {
		return value;
	}
}
