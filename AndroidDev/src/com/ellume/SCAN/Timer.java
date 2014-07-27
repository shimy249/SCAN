package com.ellume.SCAN;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Timer extends BroadcastReceiver {

	public static final long REFRESH_INTERVAL = 1000*60*60; // 1 hour

	@Override
	public void onReceive(Context context, Intent intent) {
		Calendar time = Calendar.getInstance();
		Intent refresher = new Intent(context, Refresher.class);
		PendingIntent pending = PendingIntent.getBroadcast(context, 100, refresher, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), REFRESH_INTERVAL, pending);

	}

}
