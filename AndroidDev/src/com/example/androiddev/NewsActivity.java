package com.example.androiddev;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class NewsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ArrayList<Integer> colors=new ArrayList<Integer>();
		setContentView(R.layout.activity_news);
		colors.add(getResources().getColor(R.color.SchoolColor1));
		colors.add(getResources().getColor(R.color.SchoolColor2));
		colors.add(getResources().getColor(R.color.randomColor));
		SelectionView myView=(SelectionView)findViewById(R.id.SelectionView);
		myView.addColors(colors);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.news, menu);
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
	public void getSelectedColors(View view)
	{
		TextView myText=(TextView)findViewById(R.id.myTextView);
		ArrayList<Integer> myColors=((SelectionView)findViewById(R.id.SelectionView)).getSelectedColors();
		String s="";
		for(int i=0; i<myColors.size(); i++)
		{
			if(i!=0)
				s+=", ";
			s+=myColors.get(i);
		}
		myText.setText(s);
	}

}
