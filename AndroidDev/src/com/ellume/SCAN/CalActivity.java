package com.ellume.SCAN;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.json.*;

import android.accounts.AccountManager;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.ellume.SCAN.R;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.CalendarScopes;

public class CalActivity extends ActionBarActivity {
	CalendarFragmentAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;
	private String[] calendarNames={"kq06vhhr2lhjq1sc2nm0il0qtk@group.calendar.google.com","ko6l12v8i57e446gfh32ppf7cg@group.calendar.google.com", "lhandler@eduhsd.net"};
	//calendar ASync stuff

	private com.google.api.services.calendar.Calendar client;

	private GoogleAccountCredential credential;
	public static final String PREF_NAME = "prefFile";
	public static final String AUTHORIZED_ACCOUNT="com.ellume.SCAN.IS_AUTHORIZED";
	public static final String PREF_ACCOUNT_NAME = "accountName";
	private static final int REQUEST_GOOGLE_PLAY_SERVICES = 10;
	private static final int REQUEST_AUTHORIZATION = 100;
	private static final int REQUEST_ACCOUNT_PICKER = 120;
	private File current_calendar_color_scheme;
	private final int[] colors={0xff2d49ff,0xff2567e8,0xff008aff,0xff27b6e8,0xff26f3ff};
	private HttpTransport httpTransport = new NetHttpTransport();
	private JacksonFactory jsonFactory = new JacksonFactory();
	boolean isAuthorized;
	public static CalendarV calendarView;
	private static boolean mEventFlag=false;
	private Calendar lastUpdated;
	public LocalEventList eventList;
	
	private String lastUpdatedx = null;
	private ArrayList<Event> events;

	public static final String FILENAME = "events.json";
	private static final int NEED_VERSION = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calendar);

		events = new ArrayList<Event>();
		SharedPreferences calPrefs=PreferenceManager.getDefaultSharedPreferences(this);
		File file=new File(this.getFilesDir(),"Styles");
		current_calendar_color_scheme=new File(file,calPrefs.getString(getResources().getString(R.string.Currently_Selected_Theme), "The Blues"));
		if(!current_calendar_color_scheme.isFile())
		{
			try {
				current_calendar_color_scheme.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			CalendarStyles.setCalendarColors(colors, current_calendar_color_scheme);
		}
		((CalendarV)findViewById(R.id.mainCalendar)).setShowWeekNumbers(false);

		//ASyncTask
		SharedPreferences settings = this.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
		credential = GoogleAccountCredential.usingOAuth2(this, Collections.singleton(CalendarScopes.CALENDAR));
		credential.setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));	
		client = new com.google.api.services.calendar.Calendar.Builder(httpTransport, jsonFactory, credential).setApplicationName("SCAN/1.0").build();
		calendarView=((CalendarV)findViewById(R.id.mainCalendar));

		new AsyncTask<Void,Event,ArrayList<Event>>(){



			@Override
			protected ArrayList<Event> doInBackground(Void... arg0) {
				Scanner scan = null;
				try {
					scan = new Scanner(openFileInput(FILENAME));
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				String jsonString = "";
				while(scan.hasNext()){
					jsonString+=scan.next();
				}
				JSONTokener parser = new JSONTokener(jsonString);


				try {
					ArrayList<Event> eventsm = new ArrayList<Event>();

					JSONObject jobject = new JSONObject(parser);

					int version = Integer.parseInt( jobject.getString("version"));
					if(version != NEED_VERSION){
						Log.e("localeventlist", "wrong calendar file version IGNORING");
					}
					else{
						String date = jobject.getString("dateLastMod");
						lastUpdatedx = date;



						JSONArray eventA = jobject.getJSONArray("events");

						int i = 0;
						while(!eventA.isNull(i)){
							JSONObject event = eventA.optJSONObject(i);
							Event c = new Event(
									event.getString("name"),
									event.getString("summary"),
									event.getString("startDate"),
									event.getString("endDate"),
									event.getString("id"),
									event.getString("calendarName"),
									event.getString("calId"),
									event.getString("colorNumber")
									);
							if(eventsm.size()>0&&eventsm.get(eventsm.size()-1).getCalId().equals(c.getCalId())){
								
							}
							else
								eventsm.add(c);
								publishProgress(c);
								
							i++;
						}
						
						scan.close();
						return eventsm;

					}
				} catch (NullPointerException e){
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					lastUpdatedx = null;
					if(checkGooglePlayServicesAvailable()){
						haveGooglePlayServices();

					}
				}
				return null;
			}
			protected void onProgressUpdated(Event e){
				calendarView.addEvents(e);
				calendarView.reDrawEvents();
				calendarView.invalidate();
			}
			protected void onPostExecute(ArrayList<Event> e){
				if(e!= null){
					
					events.addAll(e);
					if(checkGooglePlayServicesAvailable()){
						haveGooglePlayServices();

					}
				}
			}
		}.execute();
	}

	@Override

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQUEST_GOOGLE_PLAY_SERVICES:
			if (resultCode == Activity.RESULT_OK
					) {
				haveGooglePlayServices();
			} else {
				checkGooglePlayServicesAvailable();
			}
			break;
		case REQUEST_AUTHORIZATION:
			if (resultCode == Activity.RESULT_OK) {
				if(!mEventFlag){
					mEventFlag=!mEventFlag;
					getEvents();
				}
			} else {
				chooseAccount();
			}
			break;
		case REQUEST_ACCOUNT_PICKER:
			if (resultCode == Activity.RESULT_OK && data != null && data.getExtras() != null) {
				String accountName = data.getExtras().getString(AccountManager.KEY_ACCOUNT_NAME);
				if (accountName != null) {
					credential.setSelectedAccountName(accountName);
					SharedPreferences settings = getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = settings.edit();
					editor.putString(PREF_ACCOUNT_NAME, accountName);
					editor.commit();
					if(!mEventFlag){
						mEventFlag=!mEventFlag;
						getEvents();
					}
				}
			}
			break;
		}
	}

	private void chooseAccount() {
		startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);

	}

	private boolean checkGooglePlayServicesAvailable() {
		final int connectionStatusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
			showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
			return false;
		}
		return true;

	}

	private void showGooglePlayServicesAvailabilityErrorDialog(
			final int connectionStatusCode) {
		runOnUiThread(new Runnable() {
			public void run() {
				Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
						connectionStatusCode, CalActivity.this, REQUEST_GOOGLE_PLAY_SERVICES);
				dialog.show();
			}
		});

	}


	public void getEvents(){


		AsyncTask<String, Void, ArrayList<Event>> task=new AsyncTask<String, Void, ArrayList<Event>>(){

			@Override
			protected ArrayList<Event> doInBackground(String... params) {
				//ArrayList<Integer> colors=CalendarStyles.readCalendarColors(current_calendar_color_scheme);
				ArrayList<Event> es = new ArrayList<Event>();
				for(int i = 0; i < params.length; i++){
					try {
						SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
						fmt.setCalendar(Calendar.getInstance());
						String dateFormated = fmt.format(Calendar.getInstance().getTime());
						com.google.api.services.calendar.model.Events feed;
						if(lastUpdatedx!=null) 
							feed = client.events().list(params[i]).setUpdatedMin(DateTime.parseRfc3339(lastUpdatedx)).execute();
						else
							feed = client.events().list(params[i]).execute();
						List<com.google.api.services.calendar.model.Event> events =  feed.getItems();
						Log.v("current", events.toString());

						for(int j = 0; j < events.size(); j++){
							com.google.api.services.calendar.model.Event current = events.get(j);
							//	Log.v("result", current.toString());
							Event event = new Event(current,i);
							MergeSort.sortEvents(MainActivity.EVENTS);
							es.add(event);

						}



					} catch (final GooglePlayServicesAvailabilityIOException availabilityException) {
						mEventFlag=!mEventFlag;
						CalActivity.this.showGooglePlayServicesAvailabilityErrorDialog(
								availabilityException.getConnectionStatusCode());
						break;
					} catch (UserRecoverableAuthIOException userRecoverableException) {						
						mEventFlag=!mEventFlag;
						CalActivity.this.startActivityForResult(
								userRecoverableException.getIntent(), CalActivity.REQUEST_AUTHORIZATION);

						break;
					} catch (IOException e) {
						if(e!=null)
							e.printStackTrace();
						break;
					}
				}
				return es;	



			}
			protected void onProgressUpdate(Void... e)
			{


			}
			protected void onPostExecute(ArrayList<Event> e)
			{
				
				events.addAll(e);
				calendarView.addEvents(events);
				calendarView.reDrawEvents();
				calendarView.invalidate();
				new BackupIo().execute();
			}
		};
		task.execute(calendarNames);


	}

	class BackupIo extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			ArrayList<Event> eventA = new ArrayList<Event>();
			JSONObject jobj = new JSONObject();
			try {
				eventA.addAll(events);
				MergeSort.sortEvents(eventA);
				jobj.put("version", (new Integer(NEED_VERSION)).toString());
				SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
				fmt.setCalendar(Calendar.getInstance());
				jobj.put("dateLastMod", fmt.format(Calendar.getInstance().getTime()).toString());
				JSONArray events = new JSONArray();

				for (int i =0; i < eventA.size(); i++){

					Event eventO = eventA.get(i);
					JSONObject event = new JSONObject();

					event.put("name", eventO.getTitle());
					if (eventO.getSummary()==null)
						event.put("summary", "");
					else
						event.put("summary", eventO.getSummary());
					event.put("startDate", fmt.format(eventO.getStartDate().getTime()));
					event.put("endDate", fmt.format(eventO.getEndDate().getTime()));
					event.put("id", eventO.getId());
					event.put("calendarName", eventO.getCalendarName());
					event.put("calId", eventO.getCalId());
					event.put("colorNumber", (new Integer(eventO.getColorNumber())).toString());


					jobj.accumulate("events", event);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}



			try{
				FileOutputStream fos = openFileOutput(FILENAME,Context.MODE_PRIVATE);
				String j =jobj.toString();
				fos.write(j.getBytes());
				fos.close();



			} catch(IOException e){
				e.printStackTrace();

			}

			return null; 
		}

	}



	private void haveGooglePlayServices() {
		if (credential.getSelectedAccountName() == null) {

			chooseAccount();
		} else {
			// load calendars
			if(!mEventFlag){
				mEventFlag=!mEventFlag;
				getEvents();

			}
		}

	}
	//public void addEvent(Event e)
	//{
	//	calendarView.addEvents(e);
	//}

	protected void onResume(){
		super.onResume();
		//debugEvents();
		

	}
	/*private void debugEvents()
	{
		for(int i=0; i<100; i++)
		{
			Calendar c=Calendar.getInstance();
			c.set(Calendar.DATE, i/7);
			Event e = new Event("Test", "test", c, c, "Test", i/10);
			MainActivity.EVENTS.add(e);
		}
		MergeSort.sortEvents(MainActivity.EVENTS);
	}*/




	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		this.getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		this.getActionBar().setCustomView(R.layout.menu_cal);
		getMenuInflater().inflate(R.menu.cal, menu);
		return true;
	}
}
class CalendarFragmentAdapter extends FragmentPagerAdapter{

	public CalendarFragmentAdapter(FragmentManager fm) {
		super(fm);
	}

	public Fragment getItem(int arg0) {
		if(arg0==0)
			return new yearCalendarFragment();
		else
			return monthCalendarFragment.newInstance();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 2;
	}

}
class monthCalendarFragment extends Fragment{
	public static monthCalendarFragment newInstance()
	{
		monthCalendarFragment frag=new monthCalendarFragment();
		return frag;
	}
	public View onCreateView(LayoutInflater inflator, ViewGroup container, Bundle savedInstanceState){
		return inflator.inflate(R.layout.calendar, container, false);
	}
}
class yearCalendarFragment extends Fragment{
	public yearCalendarFragment(){}
	public yearCalendarFragment newInstance(){
		yearCalendarFragment myfrag=new yearCalendarFragment();
		return myfrag;
	}
	public View onCreateView(LayoutInflater inflator, ViewGroup container, Bundle savedInstanceState){
		return inflator.inflate(R.layout.year_layout, container, false);
	}
}

