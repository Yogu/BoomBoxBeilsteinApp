package de.boomboxbeilstein.android;

import org.joda.time.format.DateTimeFormat;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import de.boomboxbeilstein.android.utils.Images;
import de.boomboxbeilstein.android.views.MarqueeTextView;

public class MainActivity extends Activity {
	private String lastShowImagerURL = "";
	private String lastCoverURL = "";
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(android.R.style.Theme_Black_NoTitleBar);
		setContentView(R.layout.overview);
		

		updateUI();
	}

	public void onResume() {
		super.onRestart();
		InfoProvider.startUpdating();
		InfoProvider.setUpdatedHandler(new Runnable() {
			public void run() {
				runOnUiThread(new Runnable() {
					public void run() {
						updateUI();
					}
				});
			}
		});
	}

	private void updateUI() {
		PlayerInfo info = InfoProvider.getCurrentInfo();
		if (info == null)
			return;
		
		// Current Track
		Play play = info.getLastPlay();
		if (play != null) {
			Track track = play.getTrack();

			MarqueeTextView artist = (MarqueeTextView) findViewById(R.id.current_artist);
			artist.setTextLazily(track.getArtist());
			MarqueeTextView title = (MarqueeTextView) findViewById(R.id.current_title);
			title.setTextLazily(track.getTitle());
			MarqueeTextView album = (MarqueeTextView) findViewById(R.id.current_album);
			album.setTextLazily(track.getAlbum());

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
		
		// Show
		ShowInfo showInfo = info.getShowInfo();
		Show show = showInfo != null ? showInfo.getShow() : null;
		if (show != null) {
			View showWrap = findViewById(R.id.show_wrap);
			showWrap.setVisibility(View.VISIBLE);
			MarqueeTextView showHeader = (MarqueeTextView)findViewById(R.id.show_header);
			if (showInfo.isNext()) {
				String time = show.getStartTime().toString(DateTimeFormat.shortTime());
				showHeader.setTextLazily(getResources().getString(R.string.next_show, time));
			} else
				showHeader.setTextLazily(getResources().getString(R.string.current_show));
			
			MarqueeTextView showTitle = (MarqueeTextView) findViewById(R.id.show_title);
			showTitle.setTextLazily(show.getTitle());	
			MarqueeTextView showDescription = (MarqueeTextView) findViewById(R.id.show_description);
			showDescription.setTextLazily(show.getHTML());		
			
			if (!show.getImageURL().equals(lastShowImagerURL)) {
				ImageView showImage = (ImageView) findViewById(R.id.show_image);
				if (!show.getImageURL().equals(""))
					Images.loadImageAsynchronously(show.getImageURL(), showImage, this);
				else
					showImage.setImageBitmap(null);
				lastShowImagerURL = show.getImageURL();
			}
			
			MarqueeTextView showPreview = (MarqueeTextView)findViewById(R.id.show_preview);
			if (!showInfo.getPreviewTitle().equals("")) {
				showPreview.setTextLazily(getResources().getString(R.string.show_preview, showInfo.getPreviewTitle()));
				showPreview.setVisibility(View.VISIBLE);
			} else
				showPreview.setVisibility(View.GONE);
		} else {
			View showWrap = findViewById(R.id.show_wrap);
			showWrap.setVisibility(View.GONE);
		}
		
		Log.d(getClass().getSimpleName(), "Updated UI");
	}

	public void onPause() {
		super.onPause();
		InfoProvider.stopUpdating();
	}
}
