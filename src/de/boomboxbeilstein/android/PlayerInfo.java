package de.boomboxbeilstein.android;

import java.util.List;

public class PlayerInfo {
	private ShowInfo showInfo;
	private List<Play> plays;
	private Countdown countdown;
	
	public ShowInfo getShowInfo() {
		return showInfo;
	}
	
	public void setShowInfo(ShowInfo showInfo) {
		this.showInfo = showInfo;
	}
	
	public List<Play> getPlays() {
		return plays;
	}
	
	public void setPlays(List<Play> plays) {
		this.plays = plays;
	}
	
	public Play getLastPlay() {
		if (plays != null && plays.size() > 0)
			return plays.get(plays.size() - 1);
		else
			return null;
	}
	
	public Countdown getCountdown() {
		return countdown;
	}
}
