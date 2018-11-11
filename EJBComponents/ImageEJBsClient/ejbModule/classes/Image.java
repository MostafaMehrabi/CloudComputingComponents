package classes;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.imageio.ImageIO;

public class Image implements Serializable {

	private static final long serialVersionUID = -5858904380155678246L;

	private transient BufferedImage image = null;
	private transient ImageStatus status = null;
	private String message = null;
	private int imageNo = 0;
	
	public Image(BufferedImage image, int number) {
		this.image = image;
		this.imageNo = number;
		this.status = ImageStatus.FAILED;
	}
	
	
	private void writeObject(ObjectOutputStream out) throws IOException{
		out.defaultWriteObject();
		String statusValue = status.getValue();
		out.writeInt(statusValue.length());
		out.writeChars(statusValue);
		ImageIO.write(image, "jpg", out);
	}
	
	private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
		in.defaultReadObject();
		int statusValueLength = in.readInt();
		char[] chars = new char[statusValueLength];
		for(int index = 0; index < statusValueLength; index++) {
			chars[index] = in.readChar();
		}
		String statusValue = new String(chars);
		status = ImageStatus.fromValue(statusValue);
		image = ImageIO.read(in);
	}
	
	public BufferedImage getImage() {
		return this.image;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public void setImage(BufferedImage image) {
		this.image = image;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public int getImageNO() {
		return this.imageNo;
	}
	
	public void setImageNo(int number) {
		this.imageNo = number;
	}
	
	public void setStatus(ImageStatus status) {
		this.status = status;
	}
	
	public ImageStatus getStatus() {
		return this.status;
	}
}
