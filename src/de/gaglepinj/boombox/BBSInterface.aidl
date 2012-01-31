package de.gaglepinj.boombox;

interface BBSInterface 
{
	void play(in String url);
	void pause();
	void stop();
	void skipForward(in String url);
	boolean isPlaying();
	
} 