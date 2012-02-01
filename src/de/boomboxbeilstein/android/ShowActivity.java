package de.boomboxbeilstein.android;

import org.joda.time.format.DateTimeFormat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import de.boomboxbeilstein.android.utils.Images;
import de.boomboxbeilstein.android.views.MarqueeTextView;

public class ShowActivity extends BaseActivity {
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(android.R.style.Theme_Black_NoTitleBar);
		setContentView(R.layout.overall);
		LayoutInflater.from(this).inflate(R.layout.show, (ViewGroup) findViewById(R.id.content));
		
		updateUI();
	}

	@Override
	protected void updateUI() {
		updateShowUI();
	}
}
