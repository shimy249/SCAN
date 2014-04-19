package com.example.androiddev;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

public class NetActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_net);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.net, menu);
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
	public void netSigned(View view){
		CheckBox myButton=(CheckBox)findViewById(R.id.netToggleButton);
		SharedPreferences myPrefs=this.getSharedPreferences(MainActivity.netFile, 0);
		SharedPreferences.Editor editor=myPrefs.edit();
		editor.putBoolean(MainActivity.netSigned, myButton.isChecked());
		editor.commit();
		if(myButton.isChecked())
		{
			Intent intent=new Intent(this, MainActivity.class);
			startActivity(intent);
		}
	}

}
