package com.ellume.SCAN;

import java.util.Calendar;







import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;


public class MainActivity extends Activity {
	public static final String netFile="NET_AGREE_FILE";
	public static final String netSigned="NET_AGREE_BOOLEAN";
	private static final int REVIEW_AGREEMENT = 156;
	public static String rightSigned="RIGHTS_AGREE_BOOLEAN";
	public static int REQUEST_AGREEMENT = 250;
	private int smiley;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences myPrefs=this.getSharedPreferences(netFile, 0);
		boolean netSignedBool=myPrefs.getBoolean(netSigned, false);
		boolean rightSignedBool = myPrefs.getBoolean(rightSigned, false);
		if((!netSignedBool)&&(!rightSignedBool))
		{
			try{
				Intent intent=new Intent(this, NetActivity.class);
				startActivityForResult(intent, REQUEST_AGREEMENT);
			}
			catch(Exception e){}
		}
		setContentView(R.layout.activity_main);
		smiley=0;
		((ImageView)findViewById(R.id.trojanGuy)).setSoundEffectsEnabled(false);;
		
	}
	protected void onResume(){
		super.onResume();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			SharedPreferences myPrefs=this.getSharedPreferences(MainActivity.netFile, 0);
			SharedPreferences.Editor editor=myPrefs.edit();
			editor.putBoolean(MainActivity.netSigned,false);
			editor.putBoolean(MainActivity.rightSigned, false);
			editor.commit();
			finish();
			return true;
		}
		else if(id == R.id.review){
			Intent i = new Intent();
			i.setClass(this, NetActivity.class);
			startActivityForResult(i, REVIEW_AGREEMENT);
		}
		return super.onOptionsItemSelected(item);
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
		intent.putExtra(CalendarV.COLOR, getResources().getColor(R.color.Blue_Event));
		intent.putExtra(CalendarV.DESCRIPTION, "Take your date on a romantic night to Prom. Make sure not to drink because that would be bad, and try to stay out of trouble. Oh, you kids, always so full of life.");
		intent.putExtra(CalendarV.TITLE, "Prom");
		intent.putExtra(CalendarV.ENDDATE, CalendarConversion.CalendarToString(Calendar.getInstance()));
		intent.putExtra(CalendarV.STARTDATE, CalendarConversion.CalendarToString(Calendar.getInstance()));
		startActivity(intent);
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if (requestCode == REQUEST_AGREEMENT){
			if(resultCode == RESULT_OK){
				
			}
			else if(resultCode == RESULT_CANCELED){
				notifyUser();
			}
		}
		else if(requestCode == REVIEW_AGREEMENT){
			
		}
	}
	private void notifyUser() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("App cannot be used without agreeing to the rules!").setTitle("Hold On!");
		builder.setPositiveButton(R.string.retry, new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				Intent i = new Intent();
				i.setClass(MainActivity.this, NetActivity.class);
				startActivityForResult(i, REQUEST_AGREEMENT);
			}
			
		});
		builder.setNegativeButton("I Quit", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
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
