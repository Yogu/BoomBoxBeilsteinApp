package de.boomboxbeilstein.android.ui;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import de.boomboxbeilstein.android.InfoProvider;
import de.boomboxbeilstein.android.Play;
import de.boomboxbeilstein.android.PlayerInfo;
import de.boomboxbeilstein.android.R;
import de.boomboxbeilstein.android.Track;
import de.boomboxbeilstein.android.R.id;
import de.boomboxbeilstein.android.R.layout;
import de.boomboxbeilstein.android.utils.Images;
import de.boomboxbeilstein.android.views.MarqueeTextView;

public class MainActivity extends LiveActivity {
	private String lastCoverURL = "";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.overview);
		initUI();
		updateUI();
	}

	private void initUI() {
		findViewById(R.id.lyrics_wrap).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				PlayerInfo info = InfoProvider.getCurrentInfo();
				if (info != null && info.getLastPlay() != null) {
					Track track = info.getLastPlay().getTrack();
					Intent intent = new Intent(MainActivity.this, LyricsActivity.class);
					intent.putExtra("lyrics", track.getLyrics());
					startActivity(intent);
				}
			}
		});

		findViewById(R.id.show_wrap).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, ShowActivity.class);
				startActivity(intent);
			}
		});
	}

	protected void updateUI() {
		PlayerInfo info = InfoProvider.getCurrentInfo();
		if (info == null)
			return;
		
		View currentWrap = findViewById(R.id.current_wrap);
		currentWrap.setVisibility(View.VISIBLE);
		View loading = findViewById(R.id.loading);
		loading.setVisibility(View.GONE);

		// Current Track
		Play play = info.getLastPlay();
		if (play != null) {
			Track track = play.getTrack();

			/*
			 * MarqueeTextView artist = (MarqueeTextView)
			 * findViewById(R.id.current_artist);
			 * artist.setTextLazily(track.getArtist()); MarqueeTextView title =
			 * (MarqueeTextView) findViewById(R.id.current_title);
			 * title.setTextLazily(track.getTitle()); MarqueeTextView album =
			 * (MarqueeTextView) findViewById(R.id.current_album);
			 * album.setTextLazily(track.getAlbum());
			 */
			MarqueeTextView title = (MarqueeTextView) findViewById(R.id.current_title);
			title.setTextLazily(track.getArtist() + " – " + track.getTitle());

			if (!lastCoverURL.equals(track.getCoverURL())) {
				ImageView cover = (ImageView) findViewById(R.id.current_cover);
				if (!track.getCoverURL().equals(""))
					Images.loadImageAsynchronously(track.getCoverURL(), cover, this);
				else
					cover.setImageBitmap(null);
				lastCoverURL = track.getCoverURL();
			}

			View lyricsWrap = findViewById(R.id.lyrics_wrap);
			TextView lyrics = (TextView) findViewById(R.id.lyrics);
			if (!track.getLyrics().equals("")) {
				lyrics.setText(track.getLyrics());
				lyricsWrap.setVisibility(View.VISIBLE);
			} else
				lyricsWrap.setVisibility(View.GONE);
		}

		updateShowUI();
	}
}
