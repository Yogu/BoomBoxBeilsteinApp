package de.boomboxbeilstein.android;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.joda.time.Instant;

import android.util.Log;
import de.boomboxbeilstein.android.utils.AppInfo;
import de.boomboxbeilstein.android.utils.Exceptions;

// Thanks to http://stackoverflow.com/a/755151/880367
public class ReportingErrorHandler implements UncaughtExceptionHandler {
	private UncaughtExceptionHandler defaultUEH;
	private String fileName;

	private static final String TAG = "ReportingErrorHandler";
	private static final String URL = InfoProvider.URL + "appcrash.php";

	public ReportingErrorHandler(String fileName) {
		this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
		this.fileName = fileName;
	}

	public void uncaughtException(Thread t, Throwable e) {
		String stacktrace = Exceptions.getStackTrace(e);
		writeToFile(stacktrace, fileName);
		
		if (defaultUEH != null)
			defaultUEH.uncaughtException(t, e);
	}

	private void writeToFile(String stacktrace, String filename) {
		try {
			BufferedWriter bos = new BufferedWriter(new FileWriter(fileName));
			bos.write(stacktrace);
			bos.flush();
			bos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void sendToServer(String stacktrace, Instant time, String comment) {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(URL);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("version", AppInfo.getFullVersion()));
		nvps.add(new BasicNameValuePair("stacktrace", stacktrace));
		nvps.add(new BasicNameValuePair("time", time.toString()));
		nvps.add(new BasicNameValuePair("comment", comment));
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
			httpClient.execute(httpPost);
		} catch (IOException e) {
			Log.e(TAG, "Error reporting error log");
			e.printStackTrace();
		}
	}
}
