package com.ellume.SCAN;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.ellume.SCAN.R;

public class AddThemeNameDialog extends Activity {
	public static final String NEW_THEME_NAME="new_theme_name_key";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_theme_name_dialog);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_theme_name_dialog, menu);
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
	public void exitToThemeEdit(View view)
	{
		Intent intent=new Intent();
		EditText title=(EditText)this.findViewById(R.id.new_theme_name);
		String filename=title.getText().toString();
		intent.putExtra(NEW_THEME_NAME, filename);
		this.setResult(RESULT_OK, intent);
		this.finish();
	}
}
