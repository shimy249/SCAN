package com.ellume.scan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import java.util.List;

import com.google.android.gms.common.GooglePlayServicesUtil;
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
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;




public class Refresher extends Service {

	//private final IBinder mBinder = new MyBinder();
	private Calendar client;
	private Context context;
	private GoogleAccountCredential credential;
	public static final String PREF_NAME = "prefFile";
	public static final String PREF_ACCOUNT_NAME = "accountName";
	
	private final IBinder mBinder = new MyBinder();
	
	public int onStartCommand(Intent i, int flags, int startId){
		
	}


	public final BroadcastReceiver receiver = new BroadcastReceiver(){
		public void onReceive(Context c, Intent intent){
			context = c;

			final int connectionStatusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(Refresher.this);
			if(GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)){
				Intent i = new Intent(context, AuthenticationError.class);
				i.putExtra("type", AuthenticationError.SHOW_GOOGLE_ERROR_DIALOG);
				i.putExtra("errorCode", connectionStatusCode);
				context.startActivity(i);
				
			}
			else{
				if (credential.getSelectedAccount() == null){
					Intent i = new Intent(context, AuthenticationError.class);
					i.putExtra("type", AuthenticationError.CHOOSE_ACCOUNT);
					context.startActivity(i);
				}
				else
				checkCalendar();
			}
		


	}};

	public void checkCalendar() {
		HttpTransport httpTransport = new NetHttpTransport();
		JacksonFactory jsonFactory = new JacksonFactory();
		
		credential = GoogleAccountCredential.usingOAuth2(this, Collections.singleton(CalendarScopes.CALENDAR));
		 
		 SharedPreferences settings = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
		 credential.setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));
		
		client = new Calendar.Builder(httpTransport, jsonFactory, credential).setApplicationName("SCAN/1.0").build();
		new AsyncTask<String, Void, Event[]>(){

			@Override
			protected Event[] doInBackground(String... arg0) {
				
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
						 return  result.toArray(new Event[10]);
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
			protected void onPostExecute(Event[] events){
				for(int i =0; i < events.length; i++){
					Log.v("resultEvents", events[i].getId()+events[i].getSummary()+events[i].getTitle());
				}
			}

		}.execute("kevinshimy@gmail.com");//this is the list of calendars that come from somewhere
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




