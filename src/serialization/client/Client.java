package serialization.client;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import serialization.ClassTwo;
import serialization.CustomizedCloudTask;

public class Client {
	private final int portNumber = 8080;
	
	public Client() {
		
	}
	
	public void connectWithServer() {
		try {
			Socket socket = new Socket("localhost", portNumber);
			ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
			
			//create a cloud task and turn it into byte array
			CustomizedCloudTask cloudTask = new CustomizedCloudTask(new ClassTwo("william", 32), new ClassTwo("Mark", 25), 13);
			ByteArrayOutputStream byteArrayOutput = new ByteArrayOutputStream();
			ObjectOutputStream objectReader = new ObjectOutputStream(byteArrayOutput);
			objectReader.writeObject(cloudTask);
			objectReader.flush();
			byte[] taskBytes = byteArrayOutput.toByteArray();
			byteArrayOutput.flush();
			objectReader.close();
			byteArrayOutput.close();
			
			//send the byte array to server.
			output.writeObject(cloudTask);
			output.flush();
			output.close();
			socket.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Client client = new Client();
		client.connectWithServer();
	}
}
