package de.boomboxbeilstein.android2.ui;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import de.boomboxbeilstein.android2.BBService;
import de.boomboxbeilstein.android2.InfoProvider;
import de.boomboxbeilstein.android2.Play;
import de.boomboxbeilstein.android2.PlayerInfo;
import de.boomboxbeilstein.android2.R;
import de.boomboxbeilstein.android2.ServerStatus;
import de.boomboxbeilstein.android2.Track;
import de.boomboxbeilstein.android2.UpdateService;
import de.boomboxbeilstein.android2.utils.Images;
import de.boomboxbeilstein.android2.utils.Strings;
import de.boomboxbeilstein.android2.views.MarqueeTextView;

public class MainActivity extends LiveActivity {
	private String lastCoverURL = "";
	
	private static final String HOMEPAGE_URL = "http://www.boomboxbeilstein.de/";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.overview);
		initUI();
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

		findViewById(R.id.link).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(HOMEPAGE_URL));
				startActivity(intent);
			}
		});

		findViewById(R.id.update).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, UpdateActivity.class);
				startActivity(intent);
			}
		});

		findViewById(R.id.mail).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, MailActivity.class);
				startActivity(intent);
			}
		});

		final View upView = findViewById(R.id.server_up);
		upView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				upView.setVisibility(View.GONE);
			}
		});
	}

	protected void updateUI() {
		PlayerInfo info = InfoProvider.getCurrentInfo();
		if (info == null)
			return;
		
		if (info.getCountdown() != null) {
			Intent intent = new Intent(this, CountdownActivity.class);
			intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(intent);
			finish();
			return;
		}
		
		View currentWrap = findViewById(R.id.current_wrap);
		currentWrap.setVisibility(View.VISIBLE);
		View loading = findViewById(R.id.loading);
		loading.setVisibility(View.GONE);

		// Current Track
		Play play = info.getLastPlay();
		if (play != null) {
			Track track = play.getTrack();
			if (track != null) {
				MarqueeTextView title = (MarqueeTextView) findViewById(R.id.current_title);
				title.setTextLazily(track.getArtist() + " – " + track.getTitle());
	
				if (lastCoverURL == null || !lastCoverURL.equals(track.getCoverURL())) {
					ImageView cover = (ImageView) findViewById(R.id.current_cover);
					if (!Strings.isEmpty(track.getCoverURL()))
						Images.loadImageAsynchronously(track.getCoverURL(), cover, this);
					else
						cover.setImageBitmap(null);
					lastCoverURL = track.getCoverURL();
				}
	
				View lyricsWrap = findViewById(R.id.lyrics_wrap);
				TextView lyrics = (TextView) findViewById(R.id.lyrics);
				if (!Strings.isEmpty(track.getLyrics())) {
					lyrics.setText(track.getLyrics());
					lyricsWrap.setVisibility(View.VISIBLE);
				} else
					lyricsWrap.setVisibility(View.GONE);
			}
		}
		
		if (UpdateService.isUpdateAvailable()) {
			View update = findViewById(R.id.update);
			update.setVisibility(View.VISIBLE);
		}
		
		View mailButton = findViewById(R.id.mail);
		if (info.getMail() != null && info.getMail().isEnabled())
			mailButton.setVisibility(View.VISIBLE);
		else
			mailButton.setVisibility(View.GONE);
		
		ServerStatus serverStatus = info.getServerStatus();
		View downView = findViewById(R.id.server_down);
		View upView = findViewById(R.id.server_up);
		if (serverStatus != null && serverStatus.isDown()) {
			TextView downTitle = (TextView)findViewById(R.id.server_down_title);
			TextView downMessage = (TextView)findViewById(R.id.server_down_message);
			downTitle.setText(serverStatus.getDownTitle());
			downMessage.setText(serverStatus.getDownMessage());
			downView.setVisibility(View.VISIBLE);
			upView.setVisibility(View.GONE);
		} else if (downView.getVisibility() == View.VISIBLE) {
			downView.setVisibility(View.GONE);
			if (serverStatus != null) {
				TextView upTitle = (TextView)findViewById(R.id.server_up_title);
				TextView upMessage = (TextView)findViewById(R.id.server_up_message);
				upTitle.setText(serverStatus.getUpAgainTitle());
				upMessage.setText(serverStatus.getUpAgainMessage());
				upView.setVisibility(View.VISIBLE);
				BBService.retry();
			}
		}
		
		updateShowUI();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
	    return true;
	}

	// This method is called once the menu is selected
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    // We have only one menu option
	    case R.id.show_version:
	    	final Dialog dialog = new Dialog(this);
	    	dialog.setContentView(R.layout.info);
	    	dialog.setTitle(getResources().getString(R.string.app_title));
	    	dialog.setCancelable(true);
	    	dialog.findViewById(R.id.close).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				dialog.show();
				break;
	    }
	    return true;
	}
}
