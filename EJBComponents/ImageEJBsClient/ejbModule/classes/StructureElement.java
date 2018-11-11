package classes;

import java.io.Serializable;

public class StructureElement implements Serializable {
	private static final long serialVersionUID = -3062512802285994975L;
	private int x;
	private int y;
	public StructureElement(int x, int y) {
		this.x  = x;
		this.y = y;
	}
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}
}
