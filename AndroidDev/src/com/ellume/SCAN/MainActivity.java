package com.ellume.SCAN;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;


public class MainActivity extends Activity {
	public static ArrayList<Event> EVENTS=new ArrayList<Event>();
	public static final String netFile="NET_AGREE_FILE";
	public static final String netSigned="NET_AGREE_BOOLEAN";
	private int smiley;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		smiley=0;
		((ImageView)findViewById(R.id.trojanGuy)).setSoundEffectsEnabled(false);;
	}
	protected void onResume(){
		super.onResume();
		SharedPreferences myPrefs=this.getSharedPreferences(netFile, 0);
		boolean netSignedBool=true;
		if(!netSignedBool)
		{
			try{
				Intent intent=new Intent(this, NetActivity.class);
				startActivity(intent);
			}
			catch(Exception e){}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	public void toCalendar(View view){
		Intent intent=new Intent(this, CalActivity.class);
		startActivity(intent);
	}
	public void toNewsFeed(View view){
		Intent intent=new Intent(this, NewsActivity.class);
		startActivity(intent);
	}
	public void toYearView(View view){
		Intent intent=new Intent(this, EventActivity.class);
		intent.putExtra(EventActivity.COLOR, getResources().getColor(R.color.Blue_Event));
		intent.putExtra(EventActivity.DESCRIPTION, "Take your date on a romantic night to Prom. Make sure not to drink because that would be bad, and try to stay out of trouble. Oh, you kids, always so full of life.");
		intent.putExtra(EventActivity.TITLE, "Prom");
		intent.putExtra(EventActivity.ENDDATE, Calendar.getInstance().getTimeInMillis());
		intent.putExtra(EventActivity.STARTDATE, Calendar.getInstance().getTimeInMillis());
		startActivity(intent);
	}
	public void smileyCounter(View view){
		smiley++;
		if(smiley==30)
		{
			ImageView trojan=(ImageView)findViewById(R.id.trojanGuy);
			trojan.setImageResource(R.drawable.orhs_drawable_funny);
		}
	}
}
