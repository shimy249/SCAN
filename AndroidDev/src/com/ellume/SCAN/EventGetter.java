package com.ellume.SCAN;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Events;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class EventGetter extends AsyncTask<String, Void, ArrayList<Event>> {
	
	private Calendar client;
	
	private GoogleAccountCredential credential;
	public static final String PREF_NAME = "prefFile";
	public static final String PREF_ACCOUNT_NAME = "accountName";
	private HttpTransport httpTransport = new NetHttpTransport();
	private JacksonFactory jsonFactory = new JacksonFactory();

	public EventGetter(Context c){
		context = c;
	}
	
	@Override
	protected ArrayList<Event> doInBackground(String... arg0) {
		
		
		
		credential = GoogleAccountCredential.usingOAuth2(context, Collections.singleton(CalendarScopes.CALENDAR));
		 
		 SharedPreferences settings = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
		 credential.setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));
		
		client = new Calendar.Builder(httpTransport, jsonFactory, credential).setApplicationName("SCAN/1.0").build();
		
		ArrayList<Event> result = new ArrayList<Event>();
		
		for(int i = 0; i < arg0.length; i++){
			try {
				Events feed = client.events().list(arg0[i]).setFields(Event.FIELDS).execute();
				
				 List<com.google.api.services.calendar.model.Event> events =  feed.getItems();
				 Log.v("result", events.toString());
				 for(int j = 0; j < events.size(); j++){
					 com.google.api.services.calendar.model.Event current = events.get(j);
					 result.add(new Event(current));
					 
				 }
				 return  result;
			}catch (UserRecoverableAuthIOException e) {
				 
					NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
					.setContentTitle("SCAN needs your permission");
					
					Intent intent = new Intent();
					intent.setClass(context, AuthenticationError.class);
					intent.putExtra("type", AuthenticationError.REQUEST_PERMISSION);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
					intent.putExtra("intent", e);
					
					PendingIntent notifyIntent = PendingIntent.getActivity(	context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
					builder.setContentIntent(notifyIntent);
					
					NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
					nm.notify( 100, builder.build());
				
				} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		
		return null;
	}

}
