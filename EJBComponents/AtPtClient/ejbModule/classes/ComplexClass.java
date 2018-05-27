package classes;

import java.io.Serializable;

public class ComplexClass implements Serializable {

	private static final long serialVersionUID = 7357239848275470691L;
	
	private ClassOne myClass;
	
	public ComplexClass(ClassOne clas) {
		this.myClass = clas;
	}

	public ClassOne getClassOne() {
		return this.myClass;
	}
	
	public void setClassOne(ClassOne clas) {
		this.myClass = clas;
	}
}
