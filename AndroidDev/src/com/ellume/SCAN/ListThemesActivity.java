package com.ellume.SCAN;

import java.io.File;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.ellume.SCAN.R;


public class ListThemesActivity extends Activity {
	public static int REQUEST_NEW_THEME_NAME=10001;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_list_themes);
		ListView myList=(ListView)findViewById(R.id.theme_selector);
		myList.setAdapter(new FileAdapter(this,R.layout.theme_item,CalendarStyles.getCalendarStyles(this)));
		myList.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					TextView title=(TextView)view.findViewById(R.id.title_theme_item);
					String filename=title.getText().toString();
					Intent intent=new Intent(ListThemesActivity.this,ThemeEditorActivity.class);
					intent.putExtra(AddThemeNameDialog.NEW_THEME_NAME, filename);
					startActivity(intent);
				} 
				
			});
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode==RESULT_OK)
			if(requestCode==REQUEST_NEW_THEME_NAME)
			{
				if(data!=null && data.getStringExtra(AddThemeNameDialog.NEW_THEME_NAME)!=null)
				{
					String themeName=data.getStringExtra(AddThemeNameDialog.NEW_THEME_NAME);
					Intent intent=new Intent(this, ThemeEditorActivity.class);
					intent.putExtra(AddThemeNameDialog.NEW_THEME_NAME, themeName);
					startActivity(intent);
				}
			}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_themes, menu);
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
		else if(id==R.id.add_theme){
			Intent intent=new Intent(this, AddThemeNameDialog.class);
			this.startActivityForResult(intent, REQUEST_NEW_THEME_NAME);

			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	private class FileAdapter extends ArrayAdapter<File>
	{
		File[] files;

		LayoutInflater mInflate;
		public FileAdapter(Context context, int resource, File[] objects) {
			super(context, resource, objects);
			mInflate=(LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
			files=objects;
		}
		public View getView(int position, View convertView, ViewGroup parent){
			File file=files[position];
			if(file!=null)
			{
				convertView=mInflate.inflate(R.layout.theme_item, null);
				if(convertView!=null)
				{
					TextView title=(TextView)convertView.findViewById(R.id.title_theme_item);
					if(title!=null)
						title.setText(file.getName());
					TextView subTitle=(TextView)convertView.findViewById(R.id.subtitle_theme_item);
					if(subTitle!=null)
						subTitle.setText("Number of Colors: "+CalendarStyles.readCalendarColors(file).size());
				}

			}
			return convertView;
		}
	}
}
