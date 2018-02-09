package serialization;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Serialization implements Serializable{
	
	private static final long serialVersionUID = 3000707438183214600L;
	ClassOne classOne = null;
	ClassTwo classTwo = null;
	List<ClassOne> listOne;
	List<ClassTwo> listTwo;
	
	public Serialization(ClassOne classOne, ClassTwo classTwo) {
		this.classOne = classOne;
		this.classTwo = classTwo;
		listOne = new ArrayList<>();
		listTwo = new ArrayList<>();
		listOne.add(new ClassOne(345)); listOne.add(new ClassOne(5643)); listOne.add(new ClassOne(563)); listOne.add(new ClassOne(987));
		listTwo.add(new ClassTwo("Hane", 30)); listTwo.add(new ClassTwo("Sean", 89)); listTwo.add(new ClassTwo("Mike", 38)); listTwo.add(new ClassTwo("Fiona", 27));
	}
	
	public void methodNoReturn(int varOne, int varTwo) {
		int sum = varOne + varTwo +  classOne.getValue();
		System.out.println("the result is: " + sum);
	}
	
	public void methodWithReturn(int varOne, ClassTwo varTwo) {
		String st1 = "this new string is: ";
		st1 += classTwo.getValue() + "_" + varOne + "_" + varTwo.getValue();
		System.out.println("the result is: " + st1); 
	}
	
	public void methodPrintList() {
		for(ClassOne element : listOne)
			System.out.println(element.getValue() + ", ");
		
		for(ClassTwo element : listTwo)
			System.out.println(element.getValue() + ", ");
	}
//	
//	private void writeObject(final ObjectOutputStream out) throws IOException{
//		try {
//			System.out.println("Write object called");
//			out.defaultWriteObject();
//			out.writeObject(classOne);
//			out.writeObject(classTwo);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	
//	private void readObject(final ObjectInputStream in) throws IOException{
//		try {
//			in.defaultReadObject();
//			classOne = (ClassOne) in.readObject();
//			classTwo = (ClassTwo) in.readObject();
//		}catch(Exception e) {
//			e.printStackTrace();
//		}
//	}
}
