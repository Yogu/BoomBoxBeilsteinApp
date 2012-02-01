package de.boomboxbeilstein.android.ui;

import de.boomboxbeilstein.android.R;
import de.boomboxbeilstein.android.R.id;
import de.boomboxbeilstein.android.R.layout;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

public class LyricsActivity extends BaseActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.lyrics);

		String lyrics = getIntent().getExtras().getString("lyrics");
		TextView view = (TextView)findViewById(R.id.lyrics);
		view.setText(lyrics);
	}
}
