package serialization;

public class Serialization {
	
	ClassOne classOne = null;
	ClassTwo classTwo = null;
	
	public Serialization(ClassOne classOne, ClassTwo classTwo) {
		this.classOne = classOne;
		this.classTwo = classTwo;
	}
	
	public void methodNoReturn(int varOne, int varTwo) {
		int sum = varOne + varTwo +  classOne.getValue();
		System.out.println("the result is: " + sum);
	}
	
	public void methodWithReturn(int varOne, ClassTwo varTwo) {
		String st1 = "this new string is: ";
		st1 += varOne + "_" + varTwo.getValue();
		System.out.println("the result is: " + st1); 
	}	
}
