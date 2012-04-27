package de.boomboxbeilstein.android2;

public class ServerStatus {
	private boolean isDown;
	private String downTitle;
	private String downMessage;
	private String upAgainTitle;
	private String upAgainMessage;
	
	public boolean isDown() {
		return isDown;
	}
	
	public String getDownTitle() {
		return downTitle != null ? downTitle : "";
	}
	
	public String getDownMessage() {
		return downMessage != null ? downMessage : "";
	}
	
	public String getUpAgainTitle() {
		return upAgainTitle != null ? upAgainTitle : "";
	}
	
	public String getUpAgainMessage() {
		return upAgainMessage != null ? upAgainMessage : "";
	}
}
