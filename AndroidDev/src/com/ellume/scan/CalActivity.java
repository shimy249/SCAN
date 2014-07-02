package com.ellume.scan;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.ActionBar;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.CalendarContract.Events;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

public class CalActivity extends ActionBarActivity {
	CalendarFragmentAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;
	private Refresher refresher;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calendar);
		((CalendarV)findViewById(R.id.mainCalendar)).setShowWeekNumbers(false);
		ArrayList<Event> e=new ArrayList<Event>();
		Date s=new Date();
		s.setTime(Calendar.getInstance().getTimeInMillis());
		
		e.add(new Event("Bobsledding","Fun",s,s, getResources().getColor(R.color.Blue_Event)));
		e.add(new Event("Not Bobsledding","Not Fun",s,s,getResources().getColor(R.color.Blue_Event)));
		e.add(new Event("More Bobsledding","More Fun",s,s,getResources().getColor(R.color.randomColor)));
		e.add(new Event("Random Bobsledding","Blah",s,s,getResources().getColor(R.color.randomColor)));
		e.add(new Event("More Bobsledding Too","Blah",s,s,0));
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
