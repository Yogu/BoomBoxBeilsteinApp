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
import de.boomboxbeilstein.android.ui.MainActivity;
import de.boomboxbeilstein.android.utils.Exceptions;

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
				notifyBar(getResources().getString(R.string.loading_stream));
				prepareMediaPlayer(url);
			} else {
				player.start();
				notifyBar(getResources().getString(R.string.playing_stream));
			}
		}
		
		public void stopService() {
			if (player.isPlaying())
				player.stop();
			stopSelf();
			unnotifyBar();
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
		unnotifyBar();
	}

	public void notifyBar(String tickerTextAndContent) {
		notifyBar(tickerTextAndContent, tickerTextAndContent, false);
	}

	public void notifyBar(String tickerText, String content, boolean vibrate) {
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);

		int icon = R.drawable.icon;
		long when = System.currentTimeMillis();

		Notification notification = new Notification(icon, tickerText, when);

		CharSequence contentTitle = getResources().getString(R.string.app_name);

		Intent notificationIntent = new Intent(this, MainActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

		notification.setLatestEventInfo(getApplicationContext(), contentTitle, content, contentIntent);

		if (vibrate) {
			notification.defaults |= Notification.DEFAULT_VIBRATE;
			long[] vib = { 0, 100, 500, 100 };
			notification.vibrate = vib;
		}

		notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;

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
			notifyBar(getResources().getString(R.string.playing_stream));
		}
	};

	public void prepareMediaPlayer(String soundURL) {
		try {
			player.setAudioStreamType(AudioManager.STREAM_MUSIC);
			player.setDataSource(soundURL);
			player.prepareAsync();
			player.setOnPreparedListener(preparedListener);
		} catch (IOException e) {
			Log.e(getClass().getSimpleName(), Exceptions.formatException(e));
		} catch (IllegalArgumentException e) {
			Log.e(getClass().getSimpleName(), Exceptions.formatException(e));
		} catch (IllegalStateException e) {
			Log.e(getClass().getSimpleName(), Exceptions.formatException(e));
		}
	}
}