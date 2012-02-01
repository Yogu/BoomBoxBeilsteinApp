package de.boomboxbeilstein.android;

import java.io.IOException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.DeadObjectException;
import android.os.IBinder;
import android.util.Log;

public class BBService extends Service {

	private MediaPlayer player;
	private boolean prepared = false;

	private static final int NOTIFY_ID = 1;

	public IBinder onBind(Intent arg0) {
		return mBinder;
	}

	private final BBSInterface.Stub mBinder = new BBSInterface.Stub() {

		public void play(String url) throws DeadObjectException {
			if (!prepared) {
				notifyBar("Loading stream, please wait while buffering...", "Buffering stream...", false);
				prepareMediaPlayer(url);
			} else {
				player.start();
				notifyBar("Stream is playing", "Stream is playing, click to pause or stop", false);
			}
		}

		public void skipForward(String url) throws DeadObjectException {
			if (prepared) {
				player.stop();
				player = new MediaPlayer();
				prepareMediaPlayer(url);
			}
		}

		public void pause() throws DeadObjectException {
			if (player.isPlaying()) {
				player.pause();
				unnotifyBar();
			}
		}

		public void stop() throws DeadObjectException {
			if (player.isPlaying()) {
				player.stop();
				player.release();
				player = new MediaPlayer();

				prepared = false;
				unnotifyBar();
			}
		}

		public boolean isPlaying() throws DeadObjectException {
			if (prepared) {
				return player.isPlaying();
			}

			return false;
		}
	};

	public void onCreate() {
		player = new MediaPlayer();
	}

	public void onStart(Intent intent, int startId) {
	}

	public void onDestroy() {
		if (player.isPlaying()) {
			player.stop();
			player = null;
		}
	}

	public void notifyBar(String tickerText, String content, boolean vibrate) {
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);

		int icon = R.drawable.icon;
		long when = System.currentTimeMillis();

		Notification notification = new Notification(icon, tickerText, when);

		CharSequence contentTitle = "Boom Box Beilstein";

		Intent notificationIntent = new Intent(this, BoomBoxBeilsteinAppActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

		notification.setLatestEventInfo(getApplicationContext(), contentTitle, content, contentIntent);

		if (vibrate) {
			notification.defaults |= Notification.DEFAULT_VIBRATE;
			long[] vib = { 0, 100, 500, 100 };
			notification.vibrate = vib;
		}

		notification.flags |= Notification.FLAG_NO_CLEAR;

		mNotificationManager.notify(NOTIFY_ID, notification);
	}

	public void unnotifyBar() {
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);

		mNotificationManager.cancelAll();
	}

	OnPreparedListener preparedListener = new OnPreparedListener() {
		@Override
		public void onPrepared(MediaPlayer arg0) {
			prepared = true;
			player.start();
			notifyBar("Stream is playing", "Stream is playing, click to pause or stop", true);
		}
	};

	public void prepareMediaPlayer(String soundURL) {
		try {
			player.setAudioStreamType(AudioManager.STREAM_MUSIC);
			player.setDataSource(soundURL);
			player.prepareAsync();
			player.setOnPreparedListener(preparedListener);
		} catch (IOException e) {
			Log.d(getClass().getSimpleName(), e.getMessage());
		} catch (IllegalArgumentException e) {
			Log.d(getClass().getSimpleName(), e.getMessage());
		} catch (IllegalStateException e) {
			Log.d(getClass().getSimpleName(), e.getMessage());
		}
	}
}