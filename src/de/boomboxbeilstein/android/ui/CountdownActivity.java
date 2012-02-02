package de.boomboxbeilstein.android.ui;

import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import de.boomboxbeilstein.android.InfoProvider;
import de.boomboxbeilstein.android.PlayerInfo;
import de.boomboxbeilstein.android.R;
import de.boomboxbeilstein.android.ServiceController;
import de.boomboxbeilstein.android.Show;

public class CountdownActivity extends LiveActivity {
	private Handler timer = new Handler();
	private ServiceController serviceController;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.countdown);
		
		serviceController = new ServiceController(this);
		
		// If still playing (maybe error occured), stop and show the play button
		serviceController.stop();
		
		ImageButton playButton = (ImageButton)findViewById(R.id.play_button);
		playButton.setEnabled(false);
		playButton.getBackground().setAlpha(128);
	}
	
	public void onResume() {
		super.onResume();
		updateTimerUI.run();
	}
	
	public void onPause() {
		super.onPause();
		timer.removeCallbacks(updateTimerUI);
	}
	
	private Runnable updateTimerUI = new Runnable() {
		public void run() {
			PlayerInfo info = InfoProvider.getCurrentInfo();
			if (info == null)
				return;
			Show show = info.getShowInfo() != null ? info.getShowInfo().getShow() : null;
			if (show == null)
				return;
			Instant time = show.getStartTime();
			if (time == null)
				return;
			
			if (Instant.now().isAfter(time))
				goToMain();
			else {
				Duration remaining = new Duration(Instant.now(), time);
				
	
				PeriodFormatter timeFormat = new PeriodFormatterBuilder()
					.minimumPrintedDigits(2)
			    .printZeroAlways()
			    .appendHours()
			    .appendLiteral(":")
			    .appendMinutes()
			    .appendLiteral(":")
			    .appendSeconds()
			    .toFormatter();
			
				TextView countdown = (TextView) findViewById(R.id.countdown);
				countdown.setText(remaining.toPeriod().toString(timeFormat));
			}

			timer.postAtTime(updateTimerUI, SystemClock.uptimeMillis() + 200);
		}
	};

	@Override
	protected void updateUI() {
		PlayerInfo info = InfoProvider.getCurrentInfo();
		if (info == null)
			return;
		
		if (info.getCountdown() == null)
			goToMain();
		else if (info.getShowInfo() != null && info.getShowInfo().getShow() != null) {
			View wrap = findViewById(R.id.wrap);
			wrap.setVisibility(View.VISIBLE);
			
			Show show = info.getShowInfo().getShow();
			
			TextView message = (TextView) findViewById(R.id.message);
			message.setText(info.getCountdown().getMessage());
			
			TextView details = (TextView) findViewById(R.id.details);
			DateTimeFormatter formatter = DateTimeFormat.shortTime().withZone(DateTimeZone.getDefault());
			String time = show.getStartTime().toString(formatter);
			String d = getResources().getString(R.string.countdown_details, time, show.getTitle());
			details.setText(d);
		}
	}
	
	private void goToMain() {
		serviceController.play();
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}
}
