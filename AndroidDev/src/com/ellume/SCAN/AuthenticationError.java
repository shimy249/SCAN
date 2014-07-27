package com.ellume.SCAN;

import com.google.android.gms.common.GooglePlayServicesUtil;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

public class AuthenticationError extends Activity {

	public static final int REQUEST_GOOGLE_PLAY_SERVICES = 154;
	public static final int SHOW_GOOGLE_ERROR_DIALOG = 254;
	public static final int CHOOSE_ACCOUNT = 255;
	public static final int REQUEST_PERMISSION = 258;
	public static final int REQUEST_ACCOUNT = 456;
	
	private int requestType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestType = this.getIntent().getIntExtra("type", 0);
		if(requestType == REQUEST_PERMISSION){
			Intent activity = (Intent) this.getIntent().getSerializableExtra("intent");
			startActivityForResult(activity,REQUEST_PERMISSION);
		}
		else if(requestType == CHOOSE_ACCOUNT){
			Intent activity = (Intent) this.getIntent().getSerializableExtra("intent");
			startActivityForResult(activity, REQUEST_ACCOUNT);
		}
		else if(requestType == SHOW_GOOGLE_ERROR_DIALOG){
			showGooglePlayServicesAvailabilityErrorDialog(this.getIntent().getIntExtra("errorCode", -1));
		}
		
	}

	private void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode){
		runOnUiThread(new Runnable(){
			public void run(){
				Dialog dialog = GooglePlayServicesUtil.getErrorDialog(connectionStatusCode, AuthenticationError.this, REQUEST_GOOGLE_PLAY_SERVICES);
				dialog.show();
			}
			
		});
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode){
		case REQUEST_ACCOUNT:
			if(resultCode == RESULT_OK && data != null && data.getExtras() != null){
				String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
				if(accountName != null){
					
					SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = settings.edit();
					editor.putString(Refresher.PREF_ACCOUNT_NAME, accountName);
					editor.commit();
					//do shit here
					
				}
			}
		case REQUEST_PERMISSION:
			if(resultCode == RESULT_OK){
				//do shit here
			}
		}
	}
	

}
