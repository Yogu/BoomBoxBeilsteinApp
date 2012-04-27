package de.boomboxbeilstein.android2.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import de.boomboxbeilstein.android2.MailSender;
import de.boomboxbeilstein.android2.R;

public class MailActivity extends BaseActivity {
	private View sendButton;
	private EditText messageView;
	private Spinner typeView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.mail);
		
		sendButton = findViewById(R.id.send);
		messageView = (EditText)findViewById(R.id.message);
		typeView = (Spinner)findViewById(R.id.type);
		
		sendButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				trySend();
			}
		});
		
		findViewById(R.id.cancel).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MailActivity.this, MainActivity.class));
			}
		});
		
		messageView.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				updateSendEnabled();
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			
			@Override
			public void afterTextChanged(Editable s) { }
		});
		typeView.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				updateSendEnabled();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				updateSendEnabled();
			}
		});
		updateSendEnabled();
	}
	
	private void updateSendEnabled() {
		sendButton.setEnabled(
			!messageView.getText().toString().trim().equals("")
			&& typeView.getSelectedItemPosition() > 0);
	}
	
	private void trySend() {
		final MailSender mail = new MailSender();
		mail.type = getResources().getStringArray(R.array.mailTypeValues)[typeView.getSelectedItemPosition()];
		EditText nameView = (EditText)findViewById(R.id.name);
		mail.name = nameView.getText().toString().trim();
		EditText subjectView = (EditText)findViewById(R.id.subject);
		mail.subject = subjectView.getText().toString().trim();
		mail.message = messageView.getText().toString().trim();
		
		final ProgressDialog progress = ProgressDialog.show(this, getResources().getString(R.string.please_wait), 
				getResources().getString(R.string.sending_mail), true, true, new OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						startActivity(new Intent(MailActivity.this, MainActivity.class));
					}
				});
		
		if (!mail.message.equals("")) {
			AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
				@Override
				protected Boolean doInBackground(Void... params) {
					return mail.send();
				}
				
				@Override
				protected void onPostExecute(final Boolean result) {
					MailActivity.this.runOnUiThread(new Runnable() {
						public void run() {
							progress.dismiss();
							if (result) {
								Toast.makeText(MailActivity.this, getResources().getString(R.string.mail_success), 
										Toast.LENGTH_SHORT).show();
								startActivity(new Intent(MailActivity.this, MainActivity.class));
							} else {
								Toast.makeText(MailActivity.this, getResources().getString(R.string.mail_failed), 
										Toast.LENGTH_LONG).show();
								sendButton.setEnabled(true);
							}
						}
					});
				}
			};
			sendButton.setEnabled(false);
			task.execute();
		}
	}
}
