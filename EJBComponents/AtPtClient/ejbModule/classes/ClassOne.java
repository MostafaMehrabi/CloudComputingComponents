package classes;

import java.io.Serializable;

public class ClassOne implements Serializable {

	private static final long serialVersionUID = 3136450107368147122L;
	
	private int myNum;
	private String message;
	
	public ClassOne(int num) {
		this.myNum = num;
		message = "";
	}
	
	public int getNum() {
		return this.myNum;
	}
	
	public void setNum(int num) {
		this.myNum = num;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return this.message;
	}
}
