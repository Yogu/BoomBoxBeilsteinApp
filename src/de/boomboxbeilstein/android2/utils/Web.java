package de.boomboxbeilstein.android2.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.loopj.android.http.PersistentCookieStore;

public class Web {
	public final static int TIMEOUT_MILLISEC = 10000; // = 10 seconds
	
	private final static String USER_AGENT = "Mozilla/5.0 (Android App, Version %s)";
	private static String userAgent = USER_AGENT;

	private static HttpContext context;
	private static PersistentCookieStore cookieStore;
	private static HttpParams httpParams;
	private static Object lock = new Object();

	private Web() {
	}

	private static String inputStreamToString(InputStream is) throws IOException {
		String line = "";
		StringBuilder total = new StringBuilder();

		// Wrap a BufferedReader around the InputStream
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));

		// Read response until the end
		while ((line = rd.readLine()) != null) {
			total.append(line);
		}

		// Return full string
		return total.toString();
	}

	private static HttpResponse execute(HttpUriRequest request) throws ClientProtocolException,
			IOException {
		if (httpParams == null) {
			httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
			HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
			HttpProtocolParams.setUserAgent(httpParams, userAgent);
		}
		
		createContext();

		HttpClient client = new DefaultHttpClient(httpParams);
		return client.execute(request, context);
	}

	private static void createContext() {
		synchronized (lock) {
			if (context == null) {
				context = new BasicHttpContext();
				cookieStore = new PersistentCookieStore();
				context.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
			}
		}
	}
	
	public static String post(String url, List<NameValuePair> postData)
			throws ClientProtocolException, IOException {
		HttpPost request = new HttpPost(url);
		try {
			request.setEntity(new UrlEncodedFormEntity(postData));
		} catch (UnsupportedEncodingException e) {
		}
		HttpResponse response = execute(request);
		return inputStreamToString(response.getEntity().getContent());
	}

	public static String post(String url) throws IOException {
		return post(url, new ArrayList<NameValuePair>());
	}

	public static String get(String url) throws ClientProtocolException, IOException {
		HttpGet request = new HttpGet(url);
		HttpResponse response = execute(request);
		return inputStreamToString(response.getEntity().getContent());
	}
	
	public static void getToStream(String url, OutputStream stream) throws ClientProtocolException, IOException {
		HttpGet request = new HttpGet(url);
		HttpResponse response = execute(request);
		response.getEntity().writeTo(stream);
	}

	public static Bitmap loadImage(String url) throws IOException {
		return loadImage(url, new BitmapFactory.Options());
	}

	public static Bitmap loadImage(String url, BitmapFactory.Options options) throws IOException {
		InputStream in = openHttpConnection(url);
		if (in == null)
			return null;
		
		Bitmap bitmap = BitmapFactory.decodeStream(in, null, options);
		in.close();
		return bitmap;
	}
	
	public static void saveCookies(Context context) {
		createContext();
		cookieStore.save(context);
	}
	
	public static void loadCookies(Context context) {
		createContext();
		cookieStore.load(context);
		userAgent = String.format(USER_AGENT, AppInfo.getFullVersion(context));
	}

	private static InputStream openHttpConnection(String url) throws IOException {
		URL theURL = new URL(url);
		URLConnection conn = theURL.openConnection();

		HttpURLConnection httpConn = (HttpURLConnection) conn;
		httpConn.setRequestMethod("GET");
		httpConn.connect();

		if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK)
			return httpConn.getInputStream();
		else
			return null;
	}
}
