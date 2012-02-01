package de.boomboxbeilstein.android;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

public class LyricsActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(android.R.style.Theme_Black_NoTitleBar);
		setContentView(R.layout.overall);
		LayoutInflater.from(this).inflate(R.layout.lyrics, (ViewGroup) findViewById(R.id.content));

		String lyrics = getIntent().getExtras().getString("lyrics");
		TextView view = (TextView)findViewById(R.id.lyrics);
		view.setText(lyrics);
	}
}
