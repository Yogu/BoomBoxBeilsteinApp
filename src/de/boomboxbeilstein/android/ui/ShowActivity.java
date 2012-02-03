package de.boomboxbeilstein.android.ui;

import android.os.Bundle;
import de.boomboxbeilstein.android.R;

public class ShowActivity extends LiveActivity {
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.show);
	}

	@Override
	protected void updateUI() {
		updateShowUI();
	}
}
