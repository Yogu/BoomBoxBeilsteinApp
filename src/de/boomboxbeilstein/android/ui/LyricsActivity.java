package de.boomboxbeilstein.android.ui;

import android.os.Bundle;
import android.widget.TextView;
import de.boomboxbeilstein.android.R;

public class LyricsActivity extends BaseActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.lyrics);

		String lyrics = getIntent().getExtras().getString("lyrics");
		TextView view = (TextView)findViewById(R.id.lyrics);
		view.setText(lyrics);
	}
}
