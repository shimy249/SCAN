package com.ellume.SCAN;


import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class EventActivity extends Activity {
	public static String TITLE="com.ellume.SCAN.CalendarV.TITLE";
	public static String DESCRIPTION="com.ellume.SCAN.CalendarV.DESCRIPTION";
	public static String STARTDATE="com.ellume.SCAN.CalendarV.STARTDATE";
	public static String ENDDATE="com.ellume.SCAN.CalendarV.ENDDATE";
	public static String COLOR="com.ellume.SCAN.CalendarV.COLOR";
	private TextView title,description, startTime, endTime;
	private String titleString, desc, start, end;
	private static Calendar startDate, endDate;
	private final int OFFSET=0;
	int color;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event);
		setVariables();
		setTextValues();
		setColorValues();
	}
	public void setVariables()
	{
		title=(TextView)findViewById(R.id.Title);
		description=(TextView)findViewById(R.id.Description);
		startTime=(TextView)findViewById(R.id.StartTime);
		endTime=(TextView)findViewById(R.id.EndTime);
		Intent intent=getIntent();
		titleString=intent.getStringExtra(TITLE);
		desc=intent.getStringExtra(DESCRIPTION);
		startDate=new GregorianCalendar();
		startDate.setTimeInMillis(intent.getLongExtra(STARTDATE, 0));
		endDate=new GregorianCalendar();
		endDate.setTimeInMillis(intent.getLongExtra(ENDDATE, 0));
		start=CalendarConversion.CalendarToString(startDate);
		end=CalendarConversion.CalendarToString(endDate);
		color=intent.getIntExtra(COLOR, -1);
	}
	public void setTextValues()
	{

		startTime.setText("Start Date: "+start);
		endTime.setText("End Date: "+end);
		title.setText(titleString);
		description.setText(desc);
	}
	public void setColorValues()
	{
		title.setBackgroundColor(color);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.event, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	public void setNotification(View v){
		NotificationCompat.Builder mBuilder =
			    new NotificationCompat.Builder(this)
			    .setSmallIcon(R.drawable.orhs_drawable)
			    .setContentTitle(titleString)
			    .setContentText(desc).setWhen(startDate.getTimeInMillis()-OFFSET)
			    .setAutoCancel(true);
		Intent resultIntent = new Intent(this, EventActivity.class);
		resultIntent.putExtra(TITLE, titleString);
		resultIntent.putExtra(DESCRIPTION, desc);
		resultIntent.putExtra(STARTDATE, start);
		resultIntent.putExtra(ENDDATE, end);
		resultIntent.putExtra(COLOR, color);
		PendingIntent myP=PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(myP);
		int notificationId=001;
		NotificationManager noted=(NotificationManager)this.getSystemService(this.NOTIFICATION_SERVICE);
		noted.notify(notificationId, mBuilder.build());

	}
	
}
