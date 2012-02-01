package de.boomboxbeilstein.android;

import org.joda.time.Instant;

public class Play {
	private int id;
	private Track track;
	private Instant time;
	
	public int getID() {
		return id;
	}
	
	public Track getTrack() {
		return track;
	}
	
	public Instant getTime() {
		return time;
	}
}
