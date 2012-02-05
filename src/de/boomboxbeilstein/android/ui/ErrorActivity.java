package de.boomboxbeilstein.android.ui;

import org.joda.time.Instant;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import de.boomboxbeilstein.android.R;
import de.boomboxbeilstein.android.ReportingErrorHandler;

public class ErrorActivity extends Activity {
	private static boolean clicked = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.error);
		
		final String stacktrace = getIntent().getExtras().getString("stacktrace");
		final long timestamp = getIntent().getExtras().getLong("time");
		
		Button send = (Button)findViewById(R.id.send);
		send.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (clicked)
					return;
				clicked = true;
				
				EditText c = (EditText)findViewById(R.id.comment);
				final String comment = c.getText().toString();
				
				new Thread(new Runnable() {
					public void run() {
						ReportingErrorHandler.sendToServer(stacktrace, new Instant(timestamp), comment);
					}
				}).start();
				Toast.makeText(ErrorActivity.this, getResources().getString(R.string.error_thankyou), 
						Toast.LENGTH_SHORT).show();
				finish();
			}
		});

		Button close = (Button)findViewById(R.id.close);
		close.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
	}
}
