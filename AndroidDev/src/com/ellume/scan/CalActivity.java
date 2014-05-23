package com.ellume.scan;

import android.os.Bundle;
import android.os.IBinder;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.view.Menu;

public class CalActivity extends Activity {
		
	private Refresher s;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cal);
		getEvents();
	
	
	}
	
	protected void onResume(){
		super.onResume();
		Intent i = new Intent(this, Refresher.class);
		bindService(i, mConnection, Context.BIND_AUTO_CREATE);
	}
	
	protected void onPause(){
		super.onPause();
		unbindService(mConnection);
	}
	
	private ServiceConnection mConnection = new ServiceConnection() {

	    public void onServiceConnected(ComponentName className, 
	        IBinder binder) {
	    Refresher.MyBinder b = (Refresher.MyBinder) binder;
	      s = b.getService();
	      
	    }

	    public void onServiceDisconnected(ComponentName className) {
	      s = null;
	    }
	  };
	
	
	public void getEvents(){
		//use service method
		
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
