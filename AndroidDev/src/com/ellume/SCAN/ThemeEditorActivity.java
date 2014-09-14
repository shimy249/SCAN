package com.ellume.SCAN;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ellume.SCAN.R;
import com.ellume.SCAN.ColorPicker.OnColorChangedListener;

public class ThemeEditorActivity extends Activity implements OnColorChangedListener {
	ArrayList<Integer> colors;
	String filename;
	File myFile;
	ListView list;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_theme_editor);
		list=(ListView) findViewById(R.id.color_list);
		filename=this.getIntent().getStringExtra(AddThemeNameDialog.NEW_THEME_NAME);
		File file=new File(this.getFilesDir(),"Styles");
		colors=new ArrayList<Integer>();
		if(file.isDirectory())
		{
			myFile=new File(file,filename);
			if(myFile.isFile())
				colors=CalendarStyles.readCalendarColors(myFile);
		}
		list.setAdapter(new ColorAdapter(this,R.layout.activity_theme_editor_list_item,colors));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.theme_editor, menu);
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
		else if(id==R.id.add_color)
		{
			new ColorPicker(this,this,"",Color.BLACK,Color.WHITE).show();
		}
		return super.onOptionsItemSelected(item);
	}
	public class ColorAdapter extends ArrayAdapter<Integer>{
		LayoutInflater mInflate;
		ArrayList<Integer> myColors;
		public ColorAdapter(Context context, int resource,ArrayList<Integer> colors) {
			super(context, resource,colors);
			myColors=colors;
			mInflate=(LayoutInflater)context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
		}
		public class Texts{
			TextView redText,blueText,greenText;
			
		}
		public View getView(int position, View convertView, ViewGroup parent){
			if(position<colors.size())
			{
				int color=colors.get(position);
				char r=(char)Color.red(color);
				char g=(char)Color.green(color);
				char b=(char)Color.blue(color);
				if(convertView==null)
					convertView=mInflate.inflate(R.layout.activity_theme_editor_list_item, null);
				Texts myTexts=new Texts();
				myTexts.redText=(TextView)convertView.findViewById(R.id.Red);
				myTexts.blueText=(TextView)convertView.findViewById(R.id.Blue);
				myTexts.greenText=(TextView)convertView.findViewById(R.id.Green);
				if(myTexts.redText!=null)
					myTexts.redText.setText("Red: "+(int)r);
				if(myTexts.greenText!=null)
					myTexts.greenText.setText("Green: "+(int)g);
				if(myTexts.blueText!=null)
					myTexts.blueText.setText("Blue: "+(int)b);
				convertView.setBackgroundColor(color);
			}
			return convertView;
		}
	}
	@Override
	public void colorChanged(String key, int color) {
		
		
		((ColorAdapter)list.getAdapter()).add(color);
		if(colors.size()>0)
			CalendarStyles.setCalendarColors(colors, myFile);
		list.invalidate();
	}
}
