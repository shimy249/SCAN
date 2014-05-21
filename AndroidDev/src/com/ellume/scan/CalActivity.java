package com.ellume.scan;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;


import com.google.api.services.calendar.model.Events;

import android.os.AsyncTask;
import android.os.Bundle;
import android.accounts.AccountManager;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;

public class CalActivity extends Activity {

	protected static final int REQUEST_GOOGLE_PLAY_SERVICES = 0;
	public static final String PREF_ACCOUNT_NAME = "accountName";
	private static final int REQUEST_ACCOUNT_PICKER = 152;
	protected static final int REQUEST_AUTHORIZATION = 259;
	private GoogleAccountCredential credential;
	private JacksonFactory jsonFactory = new JacksonFactory();
	private HttpTransport httpTransport = new NetHttpTransport();
	
	
	private Calendar client;
	
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		
		
		
		
		
		
		 
		
		
	
	
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
					getEvents();
					
				}
			}
		}
	}
	
	private void haveGooglePlayServices() {
		if (credential.getSelectedAccount() == null){
			startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
			
			
		}
		else{
			getEvents();
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
	public void getEvents(){
		credential = GoogleAccountCredential.usingOAuth2(this, Collections.singleton(CalendarScopes.CALENDAR));
		 
		 SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
		 credential.setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));
		
		client = new Calendar.Builder(httpTransport, jsonFactory, credential).setApplicationName("SCAN/1.0").build();
		new AsyncTask<String, Void, Event[]>(){

			@Override
			protected Event[] doInBackground(String... arg0) {
				
				ArrayList<Event> result = new ArrayList<Event>();
				
				for(int i = 0; i < arg0.length; i++){
					try {
						Events feed = client.events().list(arg0[i]).setFields(Event.FIELDS).execute();
						Log.v("result", feed.toString());
						 List<com.google.api.services.calendar.model.Event> events =  feed.getItems();
						 for(int j = 0; j < events.size(); j++){
							 com.google.api.services.calendar.model.Event current = events.get(j);
							 result.add(new Event(current));
						 }
					}catch (UserRecoverableAuthIOException e) {
						  startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
						} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
				}
				return null;
			}

		}.execute("kevinshimy@gmail.com");//this is the list of calendars that come from somewhere
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
