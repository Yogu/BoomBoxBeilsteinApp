package de.boomboxbeilstein.android2.ui;

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
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import de.boomboxbeilstein.android2.InfoProvider;
import de.boomboxbeilstein.android2.PlayerInfo;
import de.boomboxbeilstein.android2.R;
import de.boomboxbeilstein.android2.ServiceController;
import de.boomboxbeilstein.android2.Show;
import de.boomboxbeilstein.android2.utils.Strings;

public class CountdownActivity extends LiveActivity {
	private Handler timer = new Handler();
	private ServiceController serviceController;

	private static final String TAG = "CountdownActivity";

	private static final PeriodFormatter countdownFormat = new PeriodFormatterBuilder()
			.minimumPrintedDigits(2).printZeroAlways().appendHours().appendLiteral(":").appendMinutes()
			.appendLiteral(":").appendSeconds().toFormatter();

	private static final DateTimeFormatter showTimeFormat = DateTimeFormat.shortTime().withZone(
			DateTimeZone.getDefault());

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.countdown);

		serviceController = new ServiceController(this);

		// If still playing (maybe error occured), stop and show the play button
		serviceController.stop();

		ImageButton playButton = (ImageButton) findViewById(R.id.play_button);
		playButton.setEnabled(false);
		playButton.getBackground().setAlpha(128);
	}

	public void onResume() {
		super.onResume(); // calls updateUI()
		updateCallback.run();
	}

	public void onPause() {
		super.onPause();
		timer.removeCallbacks(updateCallback);

	}

	private Runnable updateCallback = new Runnable() {
		public void run() {
			try {
				try {
					updateTimerUI();
				} catch (Exception e) {
					Log.e(TAG, "Error updating timer");
					e.printStackTrace();
				}
			} finally {
				timer.postAtTime(updateCallback, SystemClock.uptimeMillis() + 200);
			}
		}
	};

	private void updateTimerUI() {
		PlayerInfo info = InfoProvider.getCurrentInfo();
		if (info == null)
			return;
		Show show = info.getShowInfo() != null ? info.getShowInfo().getShow() : null;
		if (show == null)
			return;
		Instant time = show.getStartTime();
		if (time == null)
			return;

		Duration remaining;
		if (Instant.now().isBefore(time))
			remaining = new Duration(Instant.now(), time);
		else
			remaining = Duration.ZERO;
		TextView countdown = (TextView) findViewById(R.id.countdown);
		countdown.setText(remaining.toPeriod().toString(countdownFormat));
	}

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

			if (!Strings.isEmpty(info.getCountdown().getMessage())) {
				TextView message = (TextView) findViewById(R.id.message);
				message.setText(info.getCountdown().getMessage());
			}

			Show show = info.getShowInfo().getShow();
			if (show != null && !Strings.isEmpty(show.getTitle())) {
				TextView details = (TextView) findViewById(R.id.details);
				String time = show.getStartTime().toString(showTimeFormat);
				String d = getResources().getString(R.string.countdown_details, time, show.getTitle());
				details.setText(d);
			}
		}
	}

	private void goToMain() {
		serviceController.play();
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}
}
