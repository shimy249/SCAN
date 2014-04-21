package com.example.androiddev;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Events;



import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;




public class Refresher extends Service {

	private final IBinder mBinder = new MyBinder();
	private Calendar client;
	private Context context;


	public final BroadcastReceiver receiver = new BroadcastReceiver(){
		public void onReceive(Context c, Intent intent){
			context = c;
			HttpTransport httpTransport = new NetHttpTransport();
			JacksonFactory jsonFactory = new JacksonFactory();
			GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(context, Collections.singleton(CalendarScopes.CALENDAR));

			SharedPreferences settings = context.getSharedPreferences("ServiceSettings", Context.MODE_PRIVATE);
			String accountName = settings.getString("accountName", null);
			if(accountName !=null){	
				
				credential.setSelectedAccountName(accountName);
				client = new Calendar.Builder(httpTransport, jsonFactory, credential).setApplicationName("SCAN").build();


				checkCalendar();
			}
		}


	};

	private void checkCalendar() {
		new AsyncTask<String, Void, Event[]>(){

			@Override
			protected Event[] doInBackground(String... arg0) {
				
				ArrayList<Event> result = new ArrayList<Event>();
				
				for(int i = 0; i < arg0.length; i++){
					try {
						Events feed = client.events().list(arg0[i]).setFields(Event.FIELDS).execute();
						 List<com.google.api.services.calendar.model.Event> events =  feed.getItems();
						 for(int j = 0; j < events.size(); j++){
							 com.google.api.services.calendar.model.Event current = events.get(j);
							 result.add(new Event(current));
						 }
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				return null;
			}

		}.execute("qsues94h802pe7h70ipa5dnbg0@group.calendar.google.com");//this is the list of calendars that come from somewhere
		
	}
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	public class MyBinder extends Binder {
		Refresher getService(){
			return Refresher.this;
		}
	}

}
