package de.boomboxbeilstein.android.ui;

import android.app.Activity;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import de.boomboxbeilstein.android.R;
import de.boomboxbeilstein.android.ServiceController;

public class BaseActivity extends Activity {
	private ServiceController service;
	
	public void onCreate(Bundle savedInstanceState, int layoutID) {
		super.onCreate(savedInstanceState);
		setTheme(android.R.style.Theme_Black_NoTitleBar);
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		setContentView(R.layout.overall);
		LayoutInflater.from(this).inflate(layoutID, (ViewGroup) findViewById(R.id.content));
		
		service = new ServiceController(this);
		initUI();
		updateServiceUI();
	}
	
	protected void onResume() {
		super.onResume();
		updateServiceUI();
	}
	
	private void initUI() {
		final ImageButton play = (ImageButton) findViewById(R.id.play_button);
		final ImageButton stop = (ImageButton) findViewById(R.id.stop_button);
		
		play.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				service.play();
				play.setVisibility(View.GONE);
				stop.setVisibility(View.VISIBLE);
			}
		});
		
		stop.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				service.stop();
				play.setVisibility(View.VISIBLE);
				stop.setVisibility(View.GONE);
			}
		});
	}
	
	private void updateServiceUI() {
		final ImageButton play = (ImageButton) findViewById(R.id.play_button);
		final ImageButton stop = (ImageButton) findViewById(R.id.stop_button);
		
		if (ServiceController.isPlaying()) {
			play.setVisibility(View.GONE);
			stop.setVisibility(View.VISIBLE);
		} else {
			play.setVisibility(View.VISIBLE);
			stop.setVisibility(View.GONE);
		}
	}
}
