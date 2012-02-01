package de.boomboxbeilstein.android;

import java.io.IOException;
import java.util.Random;

import org.apache.http.client.ClientProtocolException;
import org.joda.time.Duration;

import android.util.Log;

import com.google.gson.JsonParseException;

import de.boomboxbeilstein.android.utils.Exceptions;
import de.boomboxbeilstein.android.utils.GsonFactory;
import de.boomboxbeilstein.android.utils.Web;

public class InfoProvider {
	//public static final String URL = "http://quick/~jan/bbb/";
	public static final String URL = "http://yogu.square7.net/bbb/";
	public static final Duration UPDATE_INTERVAL = Duration.standardSeconds(5);
	
	private static String lastHash = "";
	private static boolean isRunning = false;
	private static PlayerInfo lastInfo;
	private static PlayerInfo currentInfo;
	private static Runnable updatedHandler;
	
	public static PlayerInfo getInfo() throws ClientProtocolException, IOException, UnexcpectedResponseException {
		try {
			String json = Web.get(getCurrentURL());
			PlayerInfo overview = GsonFactory.createGson().fromJson(json, PlayerInfo.class);
			if (overview == null)
				throw new UnexcpectedResponseException(
					"Unable to parse the response as PlayerInfo json");
			return overview;
		} catch (JsonParseException e) {
			throw new UnexcpectedResponseException(e);
		}
	}
	
	public static void startUpdating() {
		if (!isRunning) {
			Thread thread = new Thread(new Runnable() {
				public void run() {
					long lastRun = System.currentTimeMillis();
					while (isRunning) {
						try {
							PlayerInfo newInfo = getInfo();
							if (newInfo.getPlays() == null)
								newInfo.setPlays(currentInfo.getPlays());
							lastInfo = currentInfo;
							currentInfo = newInfo;
							if (currentInfo.getPlays() != null && currentInfo.getPlays().size() > 0) {
								Play lastPlay = currentInfo.getPlays().get(currentInfo.getPlays().size() - 1);
								if (lastPlay != null && lastPlay.getTrack() != null)
									lastHash = lastPlay.getTrack().getHash();
							}
 							if (updatedHandler != null)
								updatedHandler.run();
							
							long sleepTime = UPDATE_INTERVAL.getMillis() - (System.currentTimeMillis() - lastRun);
							if (sleepTime > 0)
								Thread.sleep(sleepTime);
							lastRun = System.currentTimeMillis();
						} catch (InterruptedException e) {
							break;
						} catch (Exception e) {
							Log.e(getClass().getSimpleName(), Exceptions.formatException(e));
						}
					}
				}
			});
			isRunning = true;
			thread.start();
		}
	}
	
	public static void stopUpdating() {
		isRunning = false;
	}
	
	public static PlayerInfo getCurrentInfo() {
		return currentInfo;
	}
	
	public static PlayerInfo getLastInfo() {
		return lastInfo;
	}
	
	public static void setUpdatedHandler(Runnable handler) {
		updatedHandler = handler;
	}
	
	private static String getCurrentURL() {
		Random random = new Random();
		
		return URL + "ajax.php?action=current&hash=" + lastHash + "&foo0=" + random.nextInt(1000000);
	}
}
