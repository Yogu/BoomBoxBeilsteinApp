package de.boomboxbeilstein.android.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.joda.time.Duration;
import org.joda.time.Instant;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

public class Images {
	private static final Duration CACHE_DURATION = Duration.standardMinutes(5);
	private static Instant lastCacheCheckTime = Instant.now();

	private static class CacheEntry {
		private Instant lastUseTime;
		private String url;
		private Bitmap bitmap;

		public CacheEntry(String url, Bitmap bitmap) {
			this.url = url;
			this.bitmap = bitmap;
			this.lastUseTime = Instant.now();
		}

		@SuppressWarnings("unused")
		public String getURL() {
			return url;
		}

		public Bitmap getBitmap() {
			return bitmap;
		}

		public Instant getLastUseTime() {
			return lastUseTime;
		}

		public void markUsed() {
			lastUseTime = Instant.now();
		}
	}

	private static Map<String, CacheEntry> cache = new HashMap<String, CacheEntry>();

	public static void loadImageAsynchronously(final String url, final ImageView view,
			final Activity activity) {
		final CacheEntry entry;
		synchronized (cache) {
			entry = cache.get(url);
		}
		if (entry != null) {
			entry.markUsed();
			view.setImageBitmap(entry.getBitmap());
		}

		new Thread(new Runnable() {
			public void run() {
				final Bitmap bitmap;
				try {
					bitmap = Web.loadImage(url);
				} catch (IOException e) {
					Log.e(getClass().getSimpleName(), Exceptions.formatException(e));
					return;
				}

				synchronized (cache) {
					CacheEntry entry = new CacheEntry(url, bitmap);
					cache.put(url, entry);
				}
				cleanCache();

				activity.runOnUiThread(new Runnable() {
					public void run() {
						view.setImageBitmap(bitmap);
					}
				});
			}
		}).start();
	}

	private static void cleanCache() {
		if (Instant.now().isBefore(lastCacheCheckTime.plus(CACHE_DURATION)))
			return;

		synchronized (cache) {
			Iterator<CacheEntry> iterator = cache.values().iterator();
			while (iterator.hasNext()) {
				CacheEntry entry = iterator.next();
				if (Instant.now().isAfter(entry.getLastUseTime().plus(CACHE_DURATION)))
					iterator.remove();
			}
		}
	}
}
