package com.ellume.scan;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class EventActivity extends Activity {
	private TextView title,description, startTime, endTime;
	private String titleString, desc, start, end;
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
		titleString=intent.getStringExtra(CalendarV.TITLE);
		desc=intent.getStringExtra(CalendarV.DESCRIPTION);
		start=intent.getStringExtra(CalendarV.STARTDATE);
		end=intent.getStringExtra(CalendarV.ENDDATE);
		color=intent.getIntExtra(CalendarV.COLOR, -1);
	}
	public void setTextValues()
	{
		
		startTime.setText("Start Date: "+start);
		if(start.equals(end))
			startTime.setVisibility(TextView.GONE);
		endTime.setText("End Date: "+end);
		title.setText(titleString);
		description.setText(desc);
	}
	public void setColorValues()
	{
		title.setBackgroundColor(color);
		description.setBackgroundColor(brighten(color));
		startTime.setBackgroundColor(brighten(color));
		endTime.setBackgroundColor(brighten(color));
		getWindow().getDecorView().setBackgroundColor(brighten(color));
	}
	private int brighten(int color)
	{
		float[] hsv=new float[3];
		Color.RGBToHSV(color/256/256%256, color/256%256, color%256, hsv);
		hsv[1]=.2f;
		hsv[2]*=1.2;
		if(hsv[2]>.9998f)
			hsv[2]=1;
		return Color.HSVToColor(hsv);
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

}
