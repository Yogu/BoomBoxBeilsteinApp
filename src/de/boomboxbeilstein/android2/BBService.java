package de.boomboxbeilstein.android2;

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
import de.boomboxbeilstein.android2.ui.MainActivity;

public class BBService extends Service {
	private MediaPlayer player;
	private boolean isStopped = false;
	private String lastHash;

	private static BBService currentService;
	private static Object lockObj = new Object();

	private static final int NOTIFY_ID = 1;
	private static final String TAG = "BBService";

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
		load();
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
	
	public static void retry() {
		synchronized (lockObj) {
			if (currentService != null) {
				currentService.load();
			}
		}
	}
	
	private void load() {
		if (player.isPlaying())
			return;
		
		notifyBar(getResources().getString(R.string.loading_stream));

		if (InfoProvider.hasReceivedGeneralInfo()) {
			GeneralInfo info = InfoProvider.getGeneralInfo();
			if (info != null && info.getStreamURL() != null)
				prepareMediaPlayer(InfoProvider.getGeneralInfo().getStreamURL());
			else
				showError();
		} else {
			new Thread(new Runnable() {
				public void run() {
					GeneralInfo info = InfoProvider.getGeneralInfo();
					String url = info != null ? info.getStreamURL() : null;
					if (url != null)
						prepareMediaPlayer(url);
					else
						showError();
				}
			}).start();
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
		} catch (Exception e) {
			Log.e(TAG, "Error preparing stream");
			e.printStackTrace();
			showError();
		}
	}

	private void startTrackFetcher() {
		InfoProvider.setServiceUpdatedHandler(new Runnable() {
			public void run() {
				if (isStopped)
					return;
				
				UpdateService.runIfNeccessary(BBService.this);
				
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
		if (isStopped)
			return;
		
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);

		int icon = R.drawable.icon;
		long when = System.currentTimeMillis();

		Notification notification = new Notification(icon, tickerText, when);

		CharSequence contentTitle = getResources().getString(R.string.app_name);

		Intent notificationIntent = new Intent(this, MainActivity.class);
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		if (contentIntent == null) {
			Log.e(TAG, "PendingIntent.getActivity() returned null");
			return;
		}

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