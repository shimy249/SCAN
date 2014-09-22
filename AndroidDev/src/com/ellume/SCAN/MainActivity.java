package com.ellume.SCAN;

import java.util.ArrayList;
import java.util.Locale;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener {
	public static ArrayList<Event> EVENTS = new ArrayList<Event>();
	public static final String netFile = "NET_AGREE_FILE";
	public static final String netSigned = "NET_AGREE_BOOLEAN";
	private static final int REVIEW_AGREEMENT = 156;
	public static String rightSigned = "RIGHTS_AGREE_BOOLEAN";
	public static int REQUEST_AGREEMENT = 250;
	private static int NUMTABS = 4;
	private static final String STATE_TAB = "selected_tab";
	private Fragment[] frags = new Fragment[NUMTABS];
	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences myPrefs = getSharedPreferences(netFile, 0);
		boolean netSignedBool = myPrefs.getBoolean(netSigned, false);
		boolean rightSignedBool = myPrefs.getBoolean(rightSigned, false);
		Log.v("Prefs", String.valueOf(netSignedBool)+String.valueOf(rightSignedBool));
		if ((!netSignedBool) && (!rightSignedBool)) {
			try {
				Intent intent = new Intent(this, NetActivity.class);
				startActivityForResult(intent, REQUEST_AGREEMENT);
				Log.v("Intent", "started");
			} catch (Exception e) {
			}
		}
		//
		setContentView(R.layout.activity_main_action_bar);
		//
		populateFrags();

		// adds tab navigation mode
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// adapter to retrieve individual tabs
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// This is what adds the tabs. Yup.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}

	}

	protected void onResume() {
		super.onResume();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current tab position.
		if (savedInstanceState.containsKey(STATE_TAB)) {
			getActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_TAB));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current tab position.
		outState.putInt(STATE_TAB, getActionBar().getSelectedNavigationIndex());
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			SharedPreferences myPrefs = this.getSharedPreferences(
					MainActivity.netFile, 0);
			SharedPreferences.Editor editor = myPrefs.edit();
			editor.putBoolean(MainActivity.netSigned, false);
			editor.putBoolean(MainActivity.rightSigned, false);
			editor.commit();
			finish();
			return true;
		} else if (id == R.id.review) {
			Intent i = new Intent();
			i.setClass(this, NetActivity.class);
			startActivityForResult(i, REVIEW_AGREEMENT);
		}
		return super.onOptionsItemSelected(item);
	}

	private void populateFrags() {
		frags[0] = new MainFragment();
		frags[1] = new MainFragment();
		frags[2] = new MainFragment();
		frags[3] = new MainFragment();
	}

	/*
	 * public void toCalendar(View view) { Intent intent = new Intent(this,
	 * CalActivity.class); startActivity(intent); }
	 * 
	 * public void toNewsFeed(View view) { Intent intent = new Intent(this,
	 * NewsActivity.class); startActivity(intent); }
	 * 
	 * public void toYearView(View view) { Intent intent = new Intent(this,
	 * EventActivity.class); intent.putExtra(EventActivity.COLOR,
	 * getResources().getColor(R.color.Blue_Event)); intent.putExtra(
	 * EventActivity.DESCRIPTION,
	 * "Take your date on a romantic night to Prom. Make sure not to drink because that would be bad, and try to stay out of trouble. Oh, you kids, always so full of life."
	 * ); intent.putExtra(EventActivity.TITLE, "Prom");
	 * intent.putExtra(EventActivity.ENDDATE, Calendar.getInstance()
	 * .getTimeInMillis()); intent.putExtra(EventActivity.STARTDATE,
	 * Calendar.getInstance() .getTimeInMillis()); startActivity(intent); }
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_AGREEMENT) {
			if (resultCode == RESULT_OK) {

			} else if (resultCode == RESULT_CANCELED) {
				notifyUser();
			}
		} else if (requestCode == REVIEW_AGREEMENT) {

		}
	}

	private void notifyUser() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("App cannot be used without agreeing to the rules!")
				.setTitle("Hold On!");
		builder.setPositiveButton(R.string.retry,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						Intent i = new Intent();
						i.setClass(MainActivity.this, NetActivity.class);
						startActivityForResult(i, REQUEST_AGREEMENT);
					}

				});
		builder.setNegativeButton("I Quit",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						finish();
					}
				});
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return frags[position];
			/*
			 * switch (position) { case 0: return new MainFragment(); case 1:
			 * return new CalFragment(); case 2: return new CalFragment(); case
			 * 3: return new MainFragment(); } return null;
			 */
		}

		@Override
		public int getCount() {
			return NUMTABS;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.main).toUpperCase(l);
			case 1:
				return getString(R.string.Calendar).toUpperCase(l);
			case 2:
				return getString(R.string.NewsFeed).toUpperCase(l);
			case 3:
				return getString(R.string.More).toUpperCase(l);
			}
			return null;
		}
	}
}
