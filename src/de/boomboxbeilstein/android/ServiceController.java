package de.boomboxbeilstein.android;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;
import de.boomboxbeilstein.android.utils.Exceptions;
import de.boomboxbeilstein.android.utils.Web;

public class ServiceController {
	private Context context;
	private boolean isStarted = false;
	private RemoteServiceConnection remoteServiceConnection;
	private BBSInterface remoteService;
	private boolean isToStop = false;

	private static String streamURL = null;
	private static boolean isPlaying = false;
	private static boolean isBusy = false;

	private static final String SERVICE_PACKAGE_NAME = "de.boomboxbeilstein.android";
	private static final String SERVICE_NAME = SERVICE_PACKAGE_NAME + ".BBService";
	private static final String STREAM_URL_URL = "http://boomboxbeilstein.de/StreamUri.txt";

	private class RemoteServiceConnection implements ServiceConnection {
		public void onServiceConnected(ComponentName className, IBinder boundService) {
			remoteService = BBSInterface.Stub.asInterface((IBinder) boundService);
			if (isToStop)
				stopService();
			else
				play();
		}

		private void play() {
			String url = getStreamURL();
			if (url == null)
				showError();
			try {
				remoteService.play(url);
				isPlaying = true;
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		public void onServiceDisconnected(ComponentName className) {
			remoteService = null;
			remoteServiceConnection = null;
		}
	}

	public ServiceController(Context context) {
		this.context = context;
	}

	public void play() {
		if (!isPlaying()) {
			isPlaying = true;
			isToStop = false;
			bindService();
		}
	}

	public void stop() {
		if (isPlaying()) {
			isPlaying = false;
			if (remoteService != null) {
				stopService();
			} else {
				isToStop = true;
				bindService(); // will stop the service after being bound
			}
		}
	}
	
	private void stopService() {
		try {
			remoteService.stopService();
			isPlaying = false;
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		if (remoteServiceConnection != null)
			context.unbindService(remoteServiceConnection);
		remoteServiceConnection = null;
	}

	public boolean isPlaying() {
		return isPlaying && isServiceRunning();
	}

	private void showError() {
		Toast.makeText(context, context.getResources().getString(R.string.playback_error),
				Toast.LENGTH_SHORT);
	}

	private void bindService() {
		if (remoteServiceConnection == null) {
			remoteServiceConnection = new RemoteServiceConnection();
			Intent intent = new Intent();
			intent.setClassName(SERVICE_PACKAGE_NAME, SERVICE_NAME);
			context.bindService(intent, remoteServiceConnection, Context.BIND_AUTO_CREATE);

			Log.d(ServiceController.class.getSimpleName(), "Service bound");
		}
	}

	private boolean isServiceRunning() {
		ActivityManager manager = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (SERVICE_NAME.equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets the recent URL from the server where it is stored under
	 * "www.boomboxbeilstein.de/StreamUri.txt".
	 * 
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	private static String getStreamURL() {
		if (streamURL == null) {
			try {
				streamURL = Web.get(STREAM_URL_URL);
			} catch (ClientProtocolException e) {
				Log.e(ServiceController.class.getSimpleName(), Exceptions.formatException(e));
			} catch (IOException e) {
				Log.e(ServiceController.class.getSimpleName(), Exceptions.formatException(e));
			}
		}
		return streamURL;
	}

}
