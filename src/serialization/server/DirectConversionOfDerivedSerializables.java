package serialization.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import serialization.ClassTwo;
import serialization.CloudTask;
import serialization.CloudTaskWithoutReturn;
import serialization.CustomizedCloudTask;

public class DirectConversionOfDerivedSerializables {
	
	public static void main(String[] args) {
		DirectConversionOfDerivedSerializables direct = new DirectConversionOfDerivedSerializables();
		direct.startProcess();
	}
	
	public void startProcess(){
		ClassTwo ct = new ClassTwo("william", 32);
		ClassTwo vt = new ClassTwo("George", 42); 
		CustomizedCloudTask taskOne = new CustomizedCloudTask(ct, vt, 18);
		
		byte[] bytes = convertToByteArray(taskOne);
		CloudTask taskTwo = deserialize(bytes);
		taskTwo.doWork();
		//testing if the customized class makes deep copy or shallow copy of the passed objects.
		//the answer is deep copy
		ct = new ClassTwo("Michael", 78);
		vt = new ClassTwo("Ferdinand", 19);
		
		taskTwo.doWork();
	}
	
	public byte[] convertToByteArray(CustomizedCloudTask task) {
		byte[] bytes = null;
		try {
			ByteArrayOutputStream byteArrayOutput = new ByteArrayOutputStream();
			ObjectOutputStream objectOutput = new ObjectOutputStream(byteArrayOutput);
			objectOutput.writeObject(task);
			objectOutput.flush();
			bytes = byteArrayOutput.toByteArray();
			byteArrayOutput.flush();
			byteArrayOutput.close();
			objectOutput.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return bytes;
	}
	
	public CustomizedCloudTask deserialize(byte[] bytes) {
		CustomizedCloudTask task = null;
		try {
			if(bytes == null) {
				System.out.println("bytes is null");
				System.exit(0);
			}
			ByteArrayInputStream byteArrayInput = new ByteArrayInputStream(bytes);
			ObjectInputStream objectInput = new ObjectInputStream(byteArrayInput);
			task = (CustomizedCloudTask) objectInput.readObject();
//			byteArrayInput.close();
//			objectInput.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return task;
	}
}
