package de.gaglepinj.boombox;

import java.net.URISyntaxException;

import de.gaglepinj.boombox.utils.Images;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
		
		TextView view = (TextView) findViewById(R.id.title);
		Track track = info.getLastPlay().getTrack();
		view.setText(track.getArtist() + " - " + track.getTitle());

		ImageView cover = (ImageView) findViewById(R.id.cover);
		if (track.getCoverURL() != "")
			Images.loadImageAsynchronously(track.getCoverURL(), cover, this);
		else
			cover.setImageBitmap(null);
	}

	public void onPause() {
		super.onPause();
		InfoProvider.stopUpdating();
	}
}
