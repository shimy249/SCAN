package com.ellume.SCAN;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

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

public class CalActivity extends ActionBarActivity implements CalendarChangeListener {
	CalendarFragmentAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;
	private String[] calendarNames={"ajives8208@gmail.com","kq06vhhr2lhjq1sc2nm0il0qtk@group.calendar.google.com","ko6l12v8i57e446gfh32ppf7cg@group.calendar.google.com", "lhandler@eduhsd.net"};
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
	
	public LocalEventList eventList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calendar);
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
		
		eventList = LocalEventList.getInstance(this,this);
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
	
	@Override
	public void onCalendarChange(){
		calendarView.addEvents(eventList.getAllEvents());
		calendarView.reDrawEvents();
		calendarView.invalidate();
	}

	public void getEvents(){
		
		
		AsyncTask<String, Void, Void> task=new AsyncTask<String, Void, Void>(){
			
			@Override
			protected Void doInBackground(String... params) {
				ArrayList<Integer> colors=CalendarStyles.readCalendarColors(current_calendar_color_scheme);
				ArrayList<Event> es = new ArrayList<Event>();
				for(int i = 0; i < params.length; i++){
					try {
						com.google.api.services.calendar.model.Events feed = client.events().list(params[i]).setUpdatedMin(DateTime.parseRfc3339(new SimpleDateFormat("yyy-MM-dd'T'HH:mm:ssXXX").format(Calendar.getInstance()))).execute();
						List<com.google.api.services.calendar.model.Event> events =  feed.getItems();
						//Log.v("current", events.toString());

						for(int j = 0; j < events.size(); j++){
							com.google.api.services.calendar.model.Event current = events.get(j);
							//	Log.v("result", current.toString());
							Event event = new Event(current,i);
							MergeSort.sortEvents(MainActivity.EVENTS);
							es.add(event);
							
						}
						
						eventList.addEvents(es);

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
				return null;		

			}
			protected void onProgressUpdate(Void... e)
			{


			}
			protected void onPostExecute(Void e)
			{
				
			}
		};
		task.execute(calendarNames);


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
	public void addEvent(Event e)
	{
		calendarView.addEvents(e);
	}

	protected void onResume(){
		super.onResume();
		//debugEvents();
		if(checkGooglePlayServicesAvailable()){
			haveGooglePlayServices();

		}

	}
	private void debugEvents()
	{
		for(int i=0; i<100; i++)
		{
			Calendar c=Calendar.getInstance();
			c.set(Calendar.DATE, i/7);
			Event e = new Event("Test", "test", c, c, "Test", i/10);
			MainActivity.EVENTS.add(e);
		}
		MergeSort.sortEvents(MainActivity.EVENTS);
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

