package de.boomboxbeilstein.android;

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.joda.time.Duration;
import org.joda.time.Instant;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import de.boomboxbeilstein.android.ui.UpdateActivity;
import de.boomboxbeilstein.android.utils.AppInfo;
import de.boomboxbeilstein.android.utils.Web;

public class UpdateService extends Service {
	public static final String FILENAME = "updated-app.apk";
	
	private static final Duration MIN_INTERVAL = Duration.standardSeconds(15); //Duration.standardMinutes(30);
	private static String downloadURL;
	private static final int NOTIFY_ID = 2;
	private static Instant lastCheckTime;
	
	private static boolean isUpdateAvailable;

	public class ServiceBinder extends Binder {
		public UpdateService getService() {
			return UpdateService.this;
		}
	}

	public IBinder onBind(Intent arg0) {
		return new ServiceBinder();
	}
	
	public static void runIfNeccessary(Context context) {
		if (lastCheckTime == null || Instant.now().isAfter(lastCheckTime.plus(MIN_INTERVAL))) {
			lastCheckTime = Instant.now();
			Intent intent = new Intent();
			intent.setClassName(UpdateService.class.getPackage().getName(), UpdateService.class.getName());
			context.startService(intent);
		}
	}
	
	public static boolean isUpdateAvailable() {
		return isUpdateAvailable;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

		startDownload();
		Log.i(getClass().getSimpleName(), "Checking for updates");
	}

	private void startDownload() {
		new Thread(new Runnable() {
			public void run() {
				try {
					if (checkForUpdates())
						downloadLatest();
					else
						return;
				} catch (Exception e) {
					e.printStackTrace();
				}

				isUpdateAvailable = true;
				notifyBar();
			}
		}).start();
	}

	private boolean checkForUpdates() {
		GeneralInfo info = InfoProvider.getGeneralInfo();
		if (info != null) {
			downloadURL = info.getAppURL();
			return info.getAppVersion() > AppInfo.getVersion(this); 
		} else
			return false;
	}

	private void downloadLatest() throws ClientProtocolException, IOException {
		Log.i(getClass().getSimpleName(), "Update found, downloading new apk");
		
		FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_WORLD_READABLE);
		try {
			Web.getToStream(downloadURL, fos);
		} finally {
			fos.close();
		}
	}

	private void notifyBar() {
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);

		int icon = R.drawable.icon;
		long when = System.currentTimeMillis();
		String tickerText = getResources().getString(R.string.update_found_ticker);
		String content = getResources().getString(R.string.update_found);
		String contentTitle = getResources().getString(R.string.app_name);

		Notification notification = new Notification(icon, tickerText, when);
		Intent notificationIntent = new Intent(this, UpdateActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

		notification.setLatestEventInfo(getApplicationContext(), contentTitle, content, contentIntent);

		mNotificationManager.notify(NOTIFY_ID, notification);
	}
}
