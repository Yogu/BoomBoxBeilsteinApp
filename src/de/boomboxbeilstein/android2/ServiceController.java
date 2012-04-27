package de.boomboxbeilstein.android2;

import android.content.Context;
import android.content.Intent;

public class ServiceController {
	private Context context;
	private static boolean isPlaying = false;

	private static final String SERVICE_PACKAGE_NAME = "de.boomboxbeilstein.android";
	private static final String SERVICE_NAME = SERVICE_PACKAGE_NAME + ".BBService";

	public ServiceController(Context context) {
		this.context = context;
	}

	public void play() {
		if (!isPlaying) {
			isPlaying = true;
			startService();
		}
	}

	public void stop() {
		if (isPlaying) {
			isPlaying = false;
			stopService();
			BBService.stop();
		}
	}
	
	private void startService() {
		Intent intent = new Intent();
		intent.setClassName(SERVICE_PACKAGE_NAME, SERVICE_NAME);
		context.startService(intent);
	}
	
	private void stopService() {
		Intent intent = new Intent();
		intent.setClassName(SERVICE_PACKAGE_NAME, SERVICE_NAME);
		context.stopService(intent);
	}

	public static boolean isPlaying() {
		return isPlaying;
	}
}
