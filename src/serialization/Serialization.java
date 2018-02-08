package serialization;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

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
	
	private void writeObject(final ObjectOutputStream out) throws IOException{
		try {
			System.out.println("Write object called");
			out.defaultWriteObject();
			out.writeObject(classOne);
			out.writeObject(classTwo);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void readObject(final ObjectInputStream in) throws IOException{
		try {
			in.defaultReadObject();
			classOne = (ClassOne) in.readObject();
			classTwo = (ClassTwo) in.readObject();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
