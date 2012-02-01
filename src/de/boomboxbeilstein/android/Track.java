package de.boomboxbeilstein.android;

import org.joda.time.Duration;

public class Track {
	private int id;
	private String hash;
	private String artist;
	private String title;
	private String albumArtist;
	private String album;
	private Duration duration;
	private String coverURL;
	private String rawLyrics;
	
	public int getID() {
		return id;
	}
	
	public String getHash() {
		return hash;
	}
	
	public String getArtist() {
		return artist;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getAlbumArtist() {
		return albumArtist;
	}
	
	public String getAlbum() {
		return album;
	}
	
	public Duration getDuration() {
		return duration;
	}
	
	public String getCoverURL() {
		return coverURL;
	}
	
	public String getLyrics() {
		return rawLyrics;
	}
}
