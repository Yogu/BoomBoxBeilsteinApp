package de.boomboxbeilstein.android;

import org.joda.time.Instant;

public class Show {
	private String title;
	private String text;
	private String linkURL;
	private String imageURL;
	private Instant startTime;
	private Instant endTime;
	
	public String getTitle() {
		return title;
	}
	
	public String getText() {
		return text;
	}
	
	public String getLinkURL() {
		return linkURL;
	}
	
	public String getImageURL() {
		return imageURL;
	}
	
	public Instant getStartTime() {
		return startTime;
	}
	
	public Instant getEndTime() {
		return endTime;
	}
}
