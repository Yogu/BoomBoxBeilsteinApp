package de.boomboxbeilstein.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.Toast;

public class BoomBoxBeilsteinAppActivity extends Activity {

	ImageButton playButton;
	ImageButton nextButton;
	ImageButton stopButton;

	boolean isLive = true;

	OnClickListener playListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			try {
				if (conn != null && remoteService.isPlaying()) {
					remoteService.pause();
					playButton.setBackgroundResource(R.drawable.playbutton);
				} else if (conn != null && remoteService.isPlaying() == false) {
					remoteService.play(getStreamURL());
					playButton.setBackgroundResource(R.drawable.pausebutton);
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				Log.d(getClass().getSimpleName(), e.getMessage());
			}
		}
	};
	OnClickListener skipListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			try {
				if (conn != null) {
					Toast toast = Toast.makeText(getApplicationContext(),
							"Spule vor bis Live, kleinen Moment bitte...", Toast.LENGTH_LONG);
					toast.show();
					remoteService.skipForward(getStreamURL());

					Animation exitAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
							R.anim.moveanimation);
					nextButton.startAnimation(exitAnimation);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.d(getClass().getSimpleName(), e.getMessage());
			}
		}
	};
	OnClickListener stopListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			try {
				if (conn != null) {
					playButton.setBackgroundResource(R.drawable.playbutton);
					Toast toast = Toast.makeText(getApplicationContext(), "Stoppe Livestream...",
							Toast.LENGTH_LONG);
					toast.show();
					remoteService.stop();
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				Log.d(getClass().getSimpleName(), e.getMessage());
			}
		}
	};

	/**
	 * Gets the recent URL from the server where it is stored under
	 * "www.boomboxbeilstein.de/StreamUri.txt".
	 */
	public String getStreamURL() {
		BufferedReader in = null;
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();
			request.setURI(new URI("http://boomboxbeilstein.de/StreamUri.txt"));
			HttpResponse response = client.execute(request);

			in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuffer sb = new StringBuffer("");
			String line = "";

			while ((line = in.readLine()) != null) {
				sb.append(line);
			}
			in.close();

			String page = sb.toString();

			return page;
		} catch (Exception ex) {
			Log.d("StreamUriGet", ex.getMessage());
			return null;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Starting and binding Service
		startService();
		bindService();

		playButton = (ImageButton) findViewById(R.id.playButton);
		playButton.setOnClickListener(playListener);

		nextButton = (ImageButton) findViewById(R.id.nextButton);
		nextButton.setOnClickListener(skipListener);

		stopButton = (ImageButton) findViewById(R.id.stopButton);
		stopButton.setOnClickListener(stopListener);

		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
	}

	// All the service stuff
	private boolean ServiceStarted = false;
	private BBSInterface remoteService;
	private RemoteServiceConnection conn;

	/** Starts the Service if not already started. */
	private void startService() {
		if (!ServiceStarted) {
			Intent i = new Intent();
			i.setClassName("de.gaglepinj.boombox", "de.gaglepinj.boombox.BBService");
			startService(i);
			ServiceStarted = true;

			Log.d(getClass().getSimpleName(), "startService() done");
		}
	}

	/** Bind the service. */
	private void bindService() {
		if (conn == null) {
			conn = new RemoteServiceConnection();
			Intent i = new Intent();
			i.setClassName("de.gaglepinj.boombox", "de.gaglepinj.boombox.BBService");
			bindService(i, conn, Context.BIND_AUTO_CREATE);

			Log.d(getClass().getSimpleName(), "bindService() done");
		} else {
			Log.d(getClass().getSimpleName(), "Cannot bind - service already bound");
		}
	}

	class RemoteServiceConnection implements ServiceConnection {
		public void onServiceConnected(ComponentName className, IBinder boundService) {
			remoteService = BBSInterface.Stub.asInterface((IBinder) boundService);

			try {
				if (remoteService.isPlaying()) {
					playButton.setBackgroundResource(R.drawable.pausebutton);
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void onServiceDisconnected(ComponentName className) {
			remoteService = null;
		}
	};

	// TODO: Anstatt Forward Button einen "go live" button
	// TODO: Cover bilder zu den Liedtiteln einblenden
	// TODO: ev. M�glichkeit zum aufnehmen des live streams
}