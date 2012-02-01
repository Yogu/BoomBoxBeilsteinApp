package de.boomboxbeilstein.android.ui;

import org.joda.time.format.DateTimeFormat;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import de.boomboxbeilstein.android.InfoProvider;
import de.boomboxbeilstein.android.PlayerInfo;
import de.boomboxbeilstein.android.R;
import de.boomboxbeilstein.android.Show;
import de.boomboxbeilstein.android.ShowInfo;
import de.boomboxbeilstein.android.R.id;
import de.boomboxbeilstein.android.R.string;
import de.boomboxbeilstein.android.utils.Images;
import de.boomboxbeilstein.android.views.MarqueeTextView;

public class LiveActivity extends BaseActivity {
	private String lastShowImagerURL = "";

	public void onResume() {
		super.onResume();
		InfoProvider.startUpdating();
		InfoProvider.setUpdatedHandler(new Runnable() {
			public void run() {
				runOnUiThread(new Runnable() {
					public void run() {
						updateUI();
					}
				});
			}
		});
	}

	public void onPause() {
		super.onPause();
		InfoProvider.stopUpdating();
	}
	
	protected void updateUI() {
		
	}

	protected void updateShowUI() {
		PlayerInfo info = InfoProvider.getCurrentInfo();
		if (info == null)
			return;
		
		// Show
		ShowInfo showInfo = info.getShowInfo();
		Show show = showInfo != null ? showInfo.getShow() : null;
		View showWrap = findViewById(R.id.show_wrap);
		if (show != null) {
			if (showWrap != null)
				showWrap.setVisibility(View.VISIBLE);
			MarqueeTextView showHeader = (MarqueeTextView)findViewById(R.id.show_header);
			if (showInfo.isNext()) {
				String time = show.getStartTime().toString(DateTimeFormat.shortTime());
				showHeader.setTextLazily(getResources().getString(R.string.next_show, time));
			} else
				showHeader.setTextLazily(getResources().getString(R.string.current_show));
			
			MarqueeTextView showTitle = (MarqueeTextView) findViewById(R.id.show_title);
			showTitle.setTextLazily(show.getTitle());	
			TextView showDescription = (TextView) findViewById(R.id.show_description);
			if (showDescription instanceof MarqueeTextView)
				((MarqueeTextView)showDescription).setTextLazily(show.getText());
			else
				showDescription.setText(show.getText());
			
			if (!show.getImageURL().equals(lastShowImagerURL)) {
				ImageView showImage = (ImageView) findViewById(R.id.show_image);
				if (!show.getImageURL().equals(""))
					Images.loadImageAsynchronously(show.getImageURL(), showImage, this);
				else
					showImage.setImageBitmap(null);
				lastShowImagerURL = show.getImageURL();
			}
			
			MarqueeTextView showPreview = (MarqueeTextView)findViewById(R.id.show_preview);
			if (!showInfo.getPreviewTitle().equals("")) {
				showPreview.setTextLazily(getResources().getString(R.string.show_preview, showInfo.getPreviewTitle()));
				showPreview.setVisibility(View.VISIBLE);
			} else
				showPreview.setVisibility(View.GONE);
		} else {
			if (showWrap != null)
				showWrap.setVisibility(View.GONE);
		}
	}
}
