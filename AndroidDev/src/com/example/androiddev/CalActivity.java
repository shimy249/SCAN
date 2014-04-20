package com.example.androiddev;

import java.util.Collections;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.CalendarList;

import android.os.Bundle;
import android.accounts.AccountManager;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;

public class CalActivity extends Activity {

	protected static final int REQUEST_GOOGLE_PLAY_SERVICES = 0;
	private static final String PREF_ACCOUNT_NAME = "accountName";
	private static final int REQUEST_ACCOUNT_PICKER = 152;
	private GoogleAccountCredential credential;
	private JacksonFactory jsonFactory = new JacksonFactory();
	private HttpTransport httpTransport = new NetHttpTransport();
	
	
	private String clientId = "something"; //need to get from google
	private String clientSecret = "something"; //need to get from google
	private Calendar client;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cal);
		
		credential = GoogleAccountCredential.usingOAuth2(this, Collections.singleton(CalendarScopes.CALENDAR));
		SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
		credential.setSelectedAccountName (settings.getString(PREF_ACCOUNT_NAME, null));
		client = new com.google.api.services.calendar.Calendar.Builder(httpTransport, jsonFactory, credential).setApplicationName("SCAN").build();
	}
	
	private void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode){
		runOnUiThread(new Runnable(){
			public void run(){
				Dialog dialog = GooglePlayServicesUtil.getErrorDialog(connectionStatusCode, CalActivity.this, REQUEST_GOOGLE_PLAY_SERVICES);
				dialog.show();
			}
			
		});
	}
	
	protected void onResume(){
		super.onResume();
		if(checkGooglePlayServicesAvailable()){
			haveGooglePlayServices();
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode){
		case REQUEST_ACCOUNT_PICKER:
			if(resultCode == RESULT_OK && data != null && data.getExtras() != null){
				String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
				if(accountName != null){
					credential.setSelectedAccountName(accountName);
					SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = settings.edit();
					editor.putString(PREF_ACCOUNT_NAME, accountName);
					editor.commit();
					
					
				}
			}
		}
	}
	
	private void haveGooglePlayServices() {
		if (credential.getSelectedAccount() == null){
			startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
			
			
		}
		else{
			
		}
		
	}

	private boolean checkGooglePlayServicesAvailable() {
		final int connectionStatusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if(GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)){
			showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
			return false;
		}
		
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		this.getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		this.getActionBar().setCustomView(R.layout.menu_cal);
		getMenuInflater().inflate(R.menu.cal, menu);
		return true;
	}

}
