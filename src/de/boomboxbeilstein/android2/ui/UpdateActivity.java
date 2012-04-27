package de.boomboxbeilstein.android2.ui;

import java.io.File;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import de.boomboxbeilstein.android2.R;
import de.boomboxbeilstein.android2.UpdateService;

public class UpdateActivity extends BaseActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.update);
		initUI();
	}
	
	private void initUI() {
		Button button = (Button)findViewById(R.id.install);
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				install();
			}
		});
	}
	
	private void install() {
		Uri uri = Uri.fromFile(new File(getFilesDir() + "/" + UpdateService.FILENAME));
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(uri, "application/vnd.android.package-archive");
		startActivity(intent);
	}
}
