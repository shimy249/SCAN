package com.example.androiddev;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.view.Menu;

public class CalActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cal);
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
