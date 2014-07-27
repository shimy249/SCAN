package com.ellume.SCAN;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class StartReciever extends BroadcastReceiver {

	@Override
	public void onReceive(Context arg0, Intent arg1) {

		
		
			Intent timer = new Intent(arg0, Timer.class);
			arg0.startService(timer);
		

	}

}
