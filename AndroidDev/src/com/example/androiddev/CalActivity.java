package com.example.androiddev;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.ActionBar;
import android.os.Bundle;
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
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calendar);
		((CalendarV)findViewById(R.id.mainCalendar)).setShowWeekNumbers(false);
		ArrayList<Event> e=new ArrayList<Event>();
		Calendar s=Calendar.getInstance();
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
