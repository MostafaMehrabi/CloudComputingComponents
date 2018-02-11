package serialization.server;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import serialization.CloudTask;

public class Server {
	
	private final int portNummber = 8080;
	
	public Server() {
		
	}
	
	public CloudTask startServer() {
		ServerSocket serverSocket = null;
		Socket clientSocket = null;
		try {
			serverSocket = new ServerSocket(portNummber);
			clientSocket = serverSocket.accept(); 
			InputStream input = clientSocket.getInputStream();
			ObjectInputStream objectInput = new ObjectInputStream(input);
			
//			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
//			byte[] data = new byte[1024];
//			int readData = 0;
//			while((readData = input.read(data, 0, data.length)) != -1) {
//				buffer.write(data, 0, readData);
//			}
//			buffer.flush();
//			byte[] readBytes = buffer.toByteArray();
//			ByteArrayInputStream inputByteArray = new ByteArrayInputStream(readBytes);
//			ObjectInputStream objectInputStream = new ObjectInputStream(inputByteArray);
			Object obj = objectInput.readObject();
			System.out.println(obj.getClass());
			CloudTask task = (CloudTask) obj;
			serverSocket.close();
			clientSocket.close();
			return task;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void doTask(CloudTask task) {
		task.doWork();
	}
	
	public static void main(String[] args) {
		System.out.println("Server started its job");
		Server server = new Server();
		CloudTask task = server.startServer();
		server.doTask(task);
		System.out.println("Server finished its job");
	}
}
