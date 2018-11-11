package classes;

public enum ImageStatus {
	SUCCESS("SUCCESS"), FAILED("FAILED");
	private final String value;
	
	ImageStatus(String status) {
		this.value = status;
	}
	
	public static ImageStatus fromValue(String value) {
		if(value != null) {
			for(ImageStatus status : values()) {
				if(status.value.equals(value)) {
					return status;
				}
			}
		}
		
		return ImageStatus.FAILED;
	}
	
	public String getValue() {
		return value;
	}
}
