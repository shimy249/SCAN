package com.ellume.SCAN;

import java.util.ArrayList;
import java.util.Calendar;





import java.util.Collections;
import java.util.List;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.CalendarScopes;

import android.app.ActionBar;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.CalendarContract.Events;
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

public class CalActivity extends ActionBarActivity {
	CalendarFragmentAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;
	private Refresher refresher;
	
	//calendar ASync stuff
	
	private com.google.api.services.calendar.Calendar client;
	
	private GoogleAccountCredential credential;
	public static final String PREF_NAME = "prefFile";
	public static final String PREF_ACCOUNT_NAME = "accountName";
	private HttpTransport httpTransport = new NetHttpTransport();
	private JacksonFactory jsonFactory = new JacksonFactory();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calendar);
		((CalendarV)findViewById(R.id.mainCalendar)).setShowWeekNumbers(false);
		ArrayList<Event> e=new ArrayList<Event>();
		Calendar s=Calendar.getInstance();
		Calendar c=Calendar.getInstance();
		c.set(Calendar.DATE, 20);
		e.add(new Event("Bobsledding","Fun",s,s, getResources().getColor(R.color.Blue_Event)));
		e.add(new Event("Not Bobsledding","Not Fun",s,s,getResources().getColor(R.color.Blue_Event)));
		e.add(new Event("Read Myth of Sisyphus","The Myth of Sisyphus is a philosophical "
				+ "essay by Albert Camus. It comprises about 119 pages and was published originally in "
				+ "1942 in French as Le Mythe de Sisyphe; the English translation"
				+ " by Justin O'Brien followed in 1955."
				+ "In the essay, Camus introduces his philosophy of the absurd: man\'s futile "
				+ "search for meaning, unity, "
				+ "and clarity in the face of an unintelligible world devoid"
				+ " of God and eternal truths or values. Does the realization of the "
				+ "absurd require suicide? Camus answers: \"No. It requires "
				+ "revolt.\" He then outlines several approaches to the absurd life. "
				+ "The final chapter compares the absurdity of man's life with the "
				+ "situation of Sisyphus, a figure of Greek mythology who was condemned "
				+ "to repeat forever the same meaningless task of pushing a boulder up a "
				+ "mountain, only to see it roll down again. The essay concludes, \"The struggle "
				+ "itself [...] is enough to fill a man's heart. One must imagine Sisyphus happy."
				+ "The work can be seen in relation to other absurdist works by Camus: the novel The Stranger (1942), the plays The Misunderstanding (1942) and Caligula (1944), and especially the essay The Rebel (1951).",s,s,getResources().getColor(R.color.randomColor)));
		e.add(new Event("Random Bobsledding","Blah",s,s,getResources().getColor(R.color.randomColor)));
		e.add(new Event("More Bobsledding Too","Blah",s,s,0xff000000));
		e.add(new Event("Sex on the Beach","Yum",c,c,0xff000000));
		
		//ASyncTask
		
		credential = GoogleAccountCredential.usingOAuth2(this, Collections.singleton(CalendarScopes.CALENDAR));
		 
		 SharedPreferences settings = this.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
		 credential.setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));
		
		client = new com.google.api.services.calendar.Calendar.Builder(httpTransport, jsonFactory, credential).setApplicationName("SCAN/1.0").build();
		
		new AsyncTask<String, Void, ArrayList<Event>>(){
		
			ArrayList<Event> result = new ArrayList<Event>();

			@Override
			protected ArrayList<Event> doInBackground(String... params) {
				for(int i = 0; i < arg0.length; i++){
					try {
						com.google.api.services.calendar.model.Events feed = client.events().list(params[i]).setFields(Event.FIELDS).execute();
						
						 List<com.google.api.services.calendar.model.Event> events =  feed.getItems();
						 Log.v("result", events.toString());
						 for(int j = 0; j < events.size(); j++){
							 com.google.api.services.calendar.model.Event current = events.get(j);
							 result.add(new Event(current));
							 
						 }
						 return  result;
			} catch()
					
			
		};
		
		((CalendarV)findViewById(R.id.mainCalendar)).addEvents(e);
		//getResources().getConfiguration();
		//if(getResources().getConfiguration().orientation==Configuration.ORIENTATION_PORTRAIT)
		//{
		//	getActionBar().hide();
		//}
		//mSectionsPagerAdapter = new CalendarFragmentAdapter(getSupportFragmentManager()); 
		//mViewPager=(ViewPager)findViewById(R.id.CalendarPage);
		//mViewPager.setAdapter(mSectionsPagerAdapter);
		//mViewPager.setCurrentItem(1);
		
		
		
	}
	
	protected void onResume(){
		super.onResume();
		Intent i = new Intent(this, Refresher.class);
		bindService(i, conn, Context.BIND_AUTO_CREATE);
	}
	
	protected void onPause(){
		super.onPause();
		unbindService(conn);
	}
	
	private ServiceConnection conn = new ServiceConnection(){
		public void onServiceConnected(ComponentName className, IBinder binder){
			Refresher.MyBinder b = (Refresher.MyBinder) binder;
			refresher = b.getService();
		}
		public void onServiceDisconnected(ComponentName className){
			refresher = null;
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		this.getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		this.getActionBar().setCustomView(R.layout.menu_cal);
		getMenuInflater().inflate(R.menu.cal, menu);
		return true;
	}
	public class CalendarFragmentAdapter extends FragmentPagerAdapter{

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
	public static class monthCalendarFragment extends Fragment{
		public static monthCalendarFragment newInstance()
		{
			monthCalendarFragment frag=new monthCalendarFragment();
			return frag;
		}
		public View onCreateView(LayoutInflater inflator, ViewGroup container, Bundle savedInstanceState){
			return inflator.inflate(R.layout.calendar, container, false);
		}
	}
	public static class yearCalendarFragment extends Fragment{
		public yearCalendarFragment(){}
		public yearCalendarFragment newInstance(){
			yearCalendarFragment myfrag=new yearCalendarFragment();
			return myfrag;
		}
		public View onCreateView(LayoutInflater inflator, ViewGroup container, Bundle savedInstanceState){
			return inflator.inflate(R.layout.year_layout, container, false);
		}
	}
}
