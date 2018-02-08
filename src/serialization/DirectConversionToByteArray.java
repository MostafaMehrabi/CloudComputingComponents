package serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class DirectConversionToByteArray {
	public DirectConversionToByteArray() {
		
	}
	
	public Serialization createObject() {
		Serialization ser = new Serialization(new ClassOne(52), new ClassTwo("Example"));
		return ser;
	}
	
	public byte[] covertToByteArray(Serialization ser) {
		byte[] byteArray = null;
		try {
			ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteOutputStream);
			objectOutputStream.writeObject(ser);
			objectOutputStream.flush();
			byteArray = byteOutputStream.toByteArray();
			byteOutputStream.close();
			objectOutputStream.close();
		}catch(Exception e) {
			System.err.println("ERROR WHEN SERIALIZING BYTE ARRAY");
			e.printStackTrace();
		}
		return byteArray;
	}
	
	public Serialization deSerialize(byte[] bytes) {
		Serialization ser = null;
		try {
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
			ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
			ser = (Serialization)objectInputStream.readObject();
		}catch(Exception e) {
			System.err.println("ERROR WHEN DE-SERIALIZING BYTE ARRAY");
			e.printStackTrace();
		}
		return ser;
	}
	
	public static void main(String[] args) {
		DirectConversionToByteArray dconv = new DirectConversionToByteArray();
		Serialization ser = dconv.createObject();
		byte[] bytes = dconv.covertToByteArray(ser);
		Serialization serial = dconv.deSerialize(bytes);
		serial.methodNoReturn(54, 22);
		serial.methodWithReturn(12, new ClassTwo("Please work"));
	}
}
