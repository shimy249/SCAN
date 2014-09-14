package com.ellume.SCAN;

import java.util.ArrayList;
import java.util.Calendar;

import com.ellume.SCAN.R;
import com.ellume.SCAN.R.id;
import com.ellume.SCAN.R.layout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ListEventsActivity extends Activity {
	private static ArrayList<Event> EVENTS;
	private EventAdapter mAdapter;
	private ArrayList<BundledEvent> myBundledEvents;

	private ArrayList<Integer> colors;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		colors=CalendarStyles.getCurrentStylesArray(this);
		setContentView(R.layout.activity_list_events);
		this.getActionBar().setTitle("Events:");
		if(EVENTS!=null){
			ArrayList<BundledEvent> bundledEvents=new ArrayList<BundledEvent>();
			for(int i=0; i<EVENTS.size();i++){
				Event e=EVENTS.get(i);
				int targetYear=(int)(Calendar.getInstance().getTimeInMillis()/31536000000l);
				int eventYear=(int)(e.getStartDate().getTimeInMillis()/31536000000l);
				if(eventYear<=targetYear+1	&& eventYear>=targetYear-1){
					if(i==0 || !isSameDay(EVENTS.get(i-1),EVENTS.get(i)))
					{
						BundledEvent myLabel=new BundledEvent();
						myLabel.event=null;
						myLabel.calendar=EVENTS.get(i).getStartDate();
						bundledEvents.add(myLabel);
					}
					BundledEvent b=new BundledEvent();
					b.calendar=null;
					b.event=EVENTS.get(i);
					bundledEvents.add(b);
				}
			}
			myBundledEvents=bundledEvents;
			mAdapter=new EventAdapter(this,R.layout.list_item,bundledEvents);
			ListView l=(ListView)findViewById(R.id.activity_list);
			l.setAdapter(mAdapter);
			l.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					BundledEvent bundle=myBundledEvents.get(position);
					if(bundle.event!=null)
					{
						Intent intent=new Intent(ListEventsActivity.this,EventActivity.class);
						intent.putExtra(EventActivity.COLOR, colors.get(
								bundle.event.getColorNumber()%colors.size()));
						intent.putExtra(EventActivity.DESCRIPTION, bundle.event.getSummary());
						intent.putExtra(EventActivity.ENDDATE, bundle.event.getEndDate().getTimeInMillis());
						intent.putExtra(EventActivity.STARTDATE, bundle.event.getStartDate().getTimeInMillis());
						intent.putExtra(EventActivity.TITLE, bundle.event.getTitle());
						startActivity(intent);
					}
					
				} 
				
			});
			TextView noEventNotify=(TextView)findViewById(R.id.noEventNotify);
			noEventNotify.setVisibility(TextView.GONE);
		}
	}
	private class BundledEvent{
		Event event;
		Calendar calendar;
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.list_events, menu);
		return true;
	}
	public static void putEvents(ArrayList<Event> e)
	{
		EVENTS=e;
	}

	public static void putCalendar(Calendar c)
	{
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
	public boolean isSameDay(Event a, Event b)
	{
		if(a!=null && b!=null)
			return a.getStartDate().get(Calendar.DATE)==b.getStartDate().get(Calendar.DATE)
			&& a.getStartDate().get(Calendar.MONTH)==b.getStartDate().get(Calendar.MONTH)
			&& a.getStartDate().get(Calendar.YEAR)==b.getStartDate().get(Calendar.YEAR);
		else
			return false;
	}
	private class EventAdapter extends ArrayAdapter<BundledEvent>{
		LayoutInflater mInflate;
		ArrayList<BundledEvent> myEvents;
		public EventAdapter(Context context, int resource,ArrayList<BundledEvent> bundledEvents) {
			super(context, resource,bundledEvents);
			mInflate=(LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			myEvents=bundledEvents;
		}
		private class List_Item_Holder{
			TextView title;
			TextView description;
			
		}
		public View getView(int position, View convertView, ViewGroup parent){
			BundledEvent b=myEvents.get(position);
			if(b.event!=null){
				List_Item_Holder holder=new List_Item_Holder();
				convertView=mInflate.inflate(R.layout.list_item_events, null);
				holder.title=(TextView)convertView.findViewById(R.id.list_item_title);
				if(holder.title!=null)
					holder.title.setText(b.event.getTitle());
				holder.description=(TextView)convertView.findViewById(R.id.list_item_description);
				if(holder.description!=null){
					if(b.event.getSummary()!=null){
						if(b.event.getSummary().length()>50)
							holder.description.setText(b.event.getSummary().substring(0, 47)+"...");
						else
							holder.description.setText(b.event.getSummary()+" ");
					}
					else
						holder.description.setText(" ");
				}
				convertView.setBackgroundColor(colors.get(b.event.getColorNumber()%colors.size()));
			}
			else if(b.calendar!=null)
			{
				convertView=mInflate.inflate(R.layout.list_header, null);
				TextView headerDate=(TextView)convertView.findViewById(R.id.list_header_date);
				if(headerDate!=null)
					headerDate.setText(CalendarConversion.Month__DD__YYYY(b.calendar));
			}
			//DO Stuff here with the layout items.

			return convertView;
		}
		public void add(BundledEvent object)
		{
			myEvents.add(object);
			this.notifyDataSetChanged();
		}
		public int getViewTypeCount()
		{
			return 2;
		}
	}
}
