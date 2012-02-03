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
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import de.boomboxbeilstein.android.ui.MainActivity;
import de.boomboxbeilstein.android.utils.Exceptions;

public class BBService extends Service {
	private MediaPlayer player;
	private boolean isStopped = false;
	private String lastHash;

	private static BBService currentService;
	private static Object lockObj = new Object();

	private static final int NOTIFY_ID = 1;

	public class ServiceBinder extends Binder {
		public BBService getService() {
			return BBService.this;
		}
	}

	public IBinder onBind(Intent arg0) {
		return new ServiceBinder();
	}

	public void onCreate() {
		player = new MediaPlayer();
		currentService = this;
	}

	public void onStart(Intent intent, int startId) {
		if (player.isPlaying())
			return;

		notifyBar(getResources().getString(R.string.loading_stream));

		if (InfoProvider.hasReceivedStreamURL()) {
			prepareMediaPlayer(InfoProvider.getStreamURL());
		} else {
			new Thread(new Runnable() {
				public void run() {
					String url = InfoProvider.getStreamURL();
					if (url != null)
						prepareMediaPlayer(url);
					else
						showError();
				}
			}).start();
		}
	}

	public void onDestroy() {
		isStopped = true;
		player.stop();
		unnotifyBar();
		synchronized (lockObj) {
			if (currentService == this)
				currentService = null;
		}
	}

	public static void stop() {
		synchronized (lockObj) {
			InfoProvider.clearServiceUpdatedHandler();
			if (currentService != null && currentService.player != null) {
				currentService.isStopped = true;
				currentService.player.stop();
			}
		}
	}

	private void prepareMediaPlayer(String soundURL) {
		try {
			player.setAudioStreamType(AudioManager.STREAM_MUSIC);
			player.setDataSource(soundURL);
			player.setOnPreparedListener(new OnPreparedListener() {
				public void onPrepared(MediaPlayer arg0) {
					if (!isStopped) {
						player.start();
						notifyBar(getResources().getString(R.string.playing_stream));

						startTrackFetcher();
					}
				}
			});
			player.setOnErrorListener(new OnErrorListener() {
				public boolean onError(MediaPlayer mp, int what, int extra) {
					if (!isStopped)
						showError();
					return false;
				}
			});
			player.prepareAsync();
		} catch (IOException e) {
			Log.e(getClass().getSimpleName(), Exceptions.formatException(e));
		} catch (IllegalArgumentException e) {
			Log.e(getClass().getSimpleName(), Exceptions.formatException(e));
		} catch (IllegalStateException e) {
			Log.e(getClass().getSimpleName(), Exceptions.formatException(e));
		}
	}

	private void startTrackFetcher() {
		InfoProvider.setServiceUpdatedHandler(new Runnable() {
			public void run() {
				if (isStopped)
					return;
				
				PlayerInfo info = InfoProvider.getCurrentInfo();
				if (info != null && info.getLastPlay() != null && info.getLastPlay().getTrack() != null) {
					Track track = info.getLastPlay().getTrack();
					if (lastHash == null || !lastHash.equals(track.getHash())) {
						notifyBar(track.getArtist() + " - " + track.getTitle());
					}
				}
			}
		});
	}

	private void showError() {
		notifyBar(getResources().getString(R.string.playback_error));
	}

	private void notifyBar(String tickerTextAndContent) {
		notifyBar(tickerTextAndContent, tickerTextAndContent, false);
	}

	private void notifyBar(String tickerText, String content, boolean vibrate) {
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);

		int icon = R.drawable.icon;
		long when = System.currentTimeMillis();

		Notification notification = new Notification(icon, tickerText, when);

		CharSequence contentTitle = getResources().getString(R.string.app_name);

		Intent notificationIntent = new Intent(this, MainActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
			Intent.FLAG_ACTIVITY_SINGLE_TOP);

		notification.setLatestEventInfo(getApplicationContext(), contentTitle, content, contentIntent);

		if (vibrate) {
			notification.defaults |= Notification.DEFAULT_VIBRATE;
			long[] vib = { 0, 100, 500, 100 };
			notification.vibrate = vib;
		}

		notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;

		mNotificationManager.notify(NOTIFY_ID, notification);
	}

	private void unnotifyBar() {
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);

		mNotificationManager.cancelAll();
	}
}