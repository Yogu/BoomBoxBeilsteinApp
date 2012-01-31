package de.gaglepinj.boombox.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Web {
	public final static int TIMEOUT_MILLISEC = 10000; // = 10 seconds

	private static HttpContext context;
	private static CookieStore cookieStore;
	private static HttpParams httpParams;

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
		}

		if (context == null) {
			context = new BasicHttpContext();
			cookieStore = new BasicCookieStore();
			context.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
		}

		HttpClient client = new DefaultHttpClient(httpParams);
		return client.execute(request, context);
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

	public static Bitmap loadImage(String url) throws IOException {
		return loadImage(url, new BitmapFactory.Options());
	}

	public static Bitmap loadImage(String url, BitmapFactory.Options options) throws IOException {
		Bitmap bitmap = null;
		InputStream in = null;
		in = openHttpConnection(url);
		bitmap = BitmapFactory.decodeStream(in, null, options);
		in.close();
		return bitmap;
	}

	private static InputStream openHttpConnection(String url) throws IOException {
		InputStream inputStream = null;
		URL theURL = new URL(url);
		URLConnection conn = theURL.openConnection();

		HttpURLConnection httpConn = (HttpURLConnection) conn;
		httpConn.setRequestMethod("GET");
		httpConn.connect();

		if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
			inputStream = httpConn.getInputStream();
		}
		return inputStream;
	}
}
