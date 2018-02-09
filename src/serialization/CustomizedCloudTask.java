package serialization;

public class CustomizedCloudTask extends CloudTaskWithoutReturn {
	private static final long serialVersionUID = 1L;
	ClassTwo classTwo;
	ClassTwo varTwo;
	int varOne;
	public CustomizedCloudTask(ClassTwo ct, ClassTwo vt, int vo) {
		classTwo = ct; varTwo = vt; varOne = vo;
	}
	
	@Override
	public void method() {
		String st1 = "this new string is: ";
		st1 += classTwo.getValue() + "_" + varOne + "_" + varTwo.getValue();
		System.out.println("the result is: " + st1); 
	}
}
