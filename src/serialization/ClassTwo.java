package serialization;

import java.io.Serializable;

public class ClassTwo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2794681151145124023L;
	
	String name = null;
	int age = 0;
	
	public ClassTwo(String value, int age) {
		this.name = value;
		this.age = age;
	}
	
	public void setValue(String val, int age) {
		name = val;
		this.age = age;
	}
	
	public String getValue() {
		return name + " age: " + age;
	}
}
