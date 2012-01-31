package de.gaglepinj.boombox.utils;

import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

public class Images {
	public static void loadImageAsynchronously(final String url, final ImageView view, final Activity activity) {
		new Thread(new Runnable() {
			public void run() {
				final Bitmap bitmap;
				try {
					bitmap = Web.loadImage(url);
				} catch (IOException e) {
					Log.e(getClass().getSimpleName(), Exceptions.formatException(e));
					return;
				}
				
				activity.runOnUiThread(new Runnable() {
					public void run() {
						view.setImageBitmap(bitmap);
					}
				});
			}
		}).start();
	}
}
