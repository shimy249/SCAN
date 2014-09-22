package com.ellume.SCAN;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import android.accounts.AccountManager;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
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

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.CalendarScopes;

public class CalFragment extends Fragment {
	CalendarFragmentAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;
	private String[] calendarNames = { "ajives8208@gmail.com",
			"kq06vhhr2lhjq1sc2nm0il0qtk@group.calendar.google.com",
			"ko6l12v8i57e446gfh32ppf7cg@group.calendar.google.com",
			"lhandler@eduhsd.net" };
	// calendar ASync stuff

	private com.google.api.services.calendar.Calendar client;

	private GoogleAccountCredential credential;
	public static final String PREF_NAME = "prefFile";
	public static final String AUTHORIZED_ACCOUNT = "com.ellume.SCAN.IS_AUTHORIZED";
	public static final String PREF_ACCOUNT_NAME = "accountName";
	private static final int REQUEST_GOOGLE_PLAY_SERVICES = 10;
	private static final int REQUEST_AUTHORIZATION = 100;
	private static final int REQUEST_ACCOUNT_PICKER = 120;
	private HttpTransport httpTransport = new NetHttpTransport();
	private JacksonFactory jsonFactory = new JacksonFactory();
	boolean isAuthorized;
	public static CalendarV calendarView;
	private static boolean mEventFlag = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// ASyncTask
		SharedPreferences settings = this.getActivity().getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);
		credential = GoogleAccountCredential.usingOAuth2(this.getActivity(),
				Collections.singleton(CalendarScopes.CALENDAR));
		credential.setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME,
				null));
		client = new com.google.api.services.calendar.Calendar.Builder(
				httpTransport, jsonFactory, credential).setApplicationName(
				"SCAN/1.0").build();
		calendarView = ((CalendarV) getView().findViewById(R.id.mainCalendar));
		if (calendarView.getEvents() == null
				|| calendarView.getEvents().size() == 0)
			calendarView.addEvents(MainActivity.EVENTS);
	}
	
	

	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.calendar, container, false);
		((CalendarV) view.findViewById(R.id.mainCalendar)).setShowWeekNumbers(false);
		return view;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQUEST_GOOGLE_PLAY_SERVICES:
			if (resultCode == Activity.RESULT_OK) {
				haveGooglePlayServices();
			} else {
				checkGooglePlayServicesAvailable();
			}
			break;
		case REQUEST_AUTHORIZATION:
			if (resultCode == Activity.RESULT_OK) {
				if (!mEventFlag) {
					mEventFlag = !mEventFlag;
					getEvents();
				}
			} else {
				chooseAccount();
			}
			break;
		case REQUEST_ACCOUNT_PICKER:
			if (resultCode == Activity.RESULT_OK && data != null
					&& data.getExtras() != null) {
				String accountName = data.getExtras().getString(
						AccountManager.KEY_ACCOUNT_NAME);
				if (accountName != null) {
					credential.setSelectedAccountName(accountName);
					SharedPreferences settings = this.getActivity().getSharedPreferences(
							PREF_NAME, Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = settings.edit();
					editor.putString(PREF_ACCOUNT_NAME, accountName);
					editor.commit();
					if (!mEventFlag) {
						mEventFlag = !mEventFlag;
						getEvents();
					}
				}
			}
			break;
		}
	}

	private void chooseAccount() {
		startActivityForResult(credential.newChooseAccountIntent(),
				REQUEST_ACCOUNT_PICKER);

	}

	private boolean checkGooglePlayServicesAvailable() {
		final int connectionStatusCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this.getActivity());
		if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
			showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
			return false;
		}
		return true;

	}

	private void showGooglePlayServicesAvailabilityErrorDialog(
			final int connectionStatusCode) {
		this.getActivity().runOnUiThread(new Runnable() {
			public void run() {
				Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
						connectionStatusCode, CalFragment.this.getActivity(),
						REQUEST_GOOGLE_PLAY_SERVICES);
				dialog.show();
			}
		});

	}

	public void getEvents() {
		final ProgressDialog dialog = new ProgressDialog(this.getActivity());
		if (MainActivity.EVENTS == null || MainActivity.EVENTS.size() == 0){
            dialog.setMessage("Loading");
            dialog.show();
		}
		AsyncTask<String, Void, Void> task = new AsyncTask<String, Void, Void>() {
			@Override
			protected Void doInBackground(String... params) {
				for (int i = 0; i < params.length; i++) {
					try {
						com.google.api.services.calendar.model.Events feed = client
								.events().list(params[i]).execute();
						List<com.google.api.services.calendar.model.Event> events = feed
								.getItems();
						// Log.v("current", events.toString());
						int r = (int) (Math.random() * 256);
						int g = (int) (Math.random() * 256);
						int b = (int) (Math.random() * 256);

						for (int j = 0; j < events.size(); j++) {
							com.google.api.services.calendar.model.Event current = events
									.get(j);
							// Log.v("result", current.toString());
							Event event = new Event(current);
							event.setColor(Color.argb(170, r, g, b));
							MergeSort.sortEvents(MainActivity.EVENTS);
							MainActivity.EVENTS.add(event);
							this.onProgressUpdate();
						}

					} catch (final GooglePlayServicesAvailabilityIOException availabilityException) {
						mEventFlag = !mEventFlag;
						CalFragment.this
								.showGooglePlayServicesAvailabilityErrorDialog(availabilityException
										.getConnectionStatusCode());
						break;
					} catch (UserRecoverableAuthIOException userRecoverableException) {
						mEventFlag = !mEventFlag;
						CalFragment.this.startActivityForResult(
								userRecoverableException.getIntent(),
								CalFragment.REQUEST_AUTHORIZATION);

						break;
					} catch (IOException e) {
						if (e != null)
							e.printStackTrace();
						break;
					}
				}
				return null;

			}

			protected void onProgressUpdate(Void... e) {

			}

			protected void onPostExecute(Void e) {
				calendarView.reDrawEvents();
				calendarView.invalidate();
				dialog.dismiss();
			}
		};
		task.execute(calendarNames);

	}

	private void haveGooglePlayServices() {
		if (credential.getSelectedAccountName() == null) {

			chooseAccount();
		} else {
			// load calendars
			if (!mEventFlag) {
				mEventFlag = !mEventFlag;
				getEvents();

			}
		}

	}

	public void addEvent(Event e) {
		calendarView.addEvents(e);
	}

	public void onResume() {
		super.onResume();
		if (checkGooglePlayServicesAvailable()) {
			haveGooglePlayServices();

		}

	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		this.getActivity().getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		this.getActivity().getActionBar().setCustomView(R.layout.menu_cal);
		getActivity().getMenuInflater().inflate(R.menu.cal, menu);
		return true;
	}
}

class CalendarFragmentAdapter extends FragmentPagerAdapter {

	public CalendarFragmentAdapter(FragmentManager fm) {
		super(fm);
	}

	public Fragment getItem(int arg0) {
		if (arg0 == 0)
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

class monthCalendarFragment extends Fragment {
	public static monthCalendarFragment newInstance() {
		monthCalendarFragment frag = new monthCalendarFragment();
		return frag;
	}

	public View onCreateView(LayoutInflater inflator, ViewGroup container,
			Bundle savedInstanceState) {
		return inflator.inflate(R.layout.calendar, container, false);
	}
}

class yearCalendarFragment extends Fragment {
	public yearCalendarFragment() {
	}

	public yearCalendarFragment newInstance() {
		yearCalendarFragment myfrag = new yearCalendarFragment();
		return myfrag;
	}

	public View onCreateView(LayoutInflater inflator, ViewGroup container,
			Bundle savedInstanceState) {
		return inflator.inflate(R.layout.year_layout, container, false);
	}
}
