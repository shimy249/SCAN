package com.ellume.SCAN;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class LocalEventList {

	public static final String FILENAME = "events.json";
	private static final int NEED_VERSION = 1;
	public static LocalEventList list;

	private boolean isWriting = false;
	private boolean isReading = false;

	private boolean isOpen = false;
	private static Context context;

	private ArrayList<Event> events; 

	private Calendar lastUpdated;

	private static ArrayList<CalendarChangeListener> listeners = new ArrayList<CalendarChangeListener>();

	public static LocalEventList getInstance(CalendarChangeListener c, Context c2){
		if(list == null){
			list = new LocalEventList();

		}
		if(context == null)
		context = c2;
		if(c != null)
		listeners.add(c);
		return list;
	}

	private LocalEventList(){
		events = new ArrayList<Event>();
		open();
	}

	public void addEvents(ArrayList<Event> e){
		while(!isReading){
			isWriting = true;
			events.addAll(e);
			new FileIO().execute(events);
			isWriting = false;
		}
		notifyListeners();
	}
	public void addEvent(Event e){
		while(!isReading){
			isWriting = true;
			events.add(e);
			new FileIO().execute(events);
			isWriting = false;
		}
		notifyListeners();

	}

	private void notifyListeners() {
		for(int i = 0; i < listeners.size(); i++){
			listeners.get(i).onCalendarChange();
		}

	}

	public void sort(){
		while(!isReading && isWriting){
			isWriting = true;
			isReading = true;
			MergeSort.sortEvents(events);
			isWriting = false;
			isReading = false;
		}
	}

	public ArrayList<Event> getAllEvents(){
		ArrayList<Event> es = new ArrayList<Event>();
		while(!isWriting){
			isReading = true;
			es.addAll(events);
			isReading = false;

		}
		return es;
	}

	private void open(){
		new AsyncTask<Void, Void, Void>(){
			public Void doInBackground(Void... v){
				JSONParser parser = new JSONParser();
				Object o;
				try {
					
					o = parser.parse(new InputStreamReader(context.openFileInput(FILENAME)));
					JSONObject jobject = (JSONObject) o;

					int version = Integer.parseInt((String) jobject.get("version"));
					if(version != NEED_VERSION){
						Log.e("localeventlist", "wrong calendar file version IGNORING");
					}
					else{
						String date = (String) jobject.get("dateLstMod");

						int dateCutoff = date.indexOf("T");
						int timeCutoff = date.indexOf("."); //to remove nanoseconds
						if(timeCutoff == -1)  //if nanoseconds not set remove timezone shift
							timeCutoff = date.indexOf("-");
						if(timeCutoff == -1) //if timezone shift not set isolate time only
							timeCutoff= date.indexOf("Z");

						String time = date.substring(dateCutoff+1, timeCutoff);
						String[] timeC = time.split(":");


						date = date.substring(0, dateCutoff);
						String[] dateC = date.split("-");
						Calendar calS = Calendar.getInstance();
						calS.set(Integer.parseInt(dateC[0]), Integer.parseInt(dateC[1])-1, Integer.parseInt(dateC[2]), Integer.parseInt(timeC[0]), Integer.parseInt(timeC[1]), Integer.parseInt(timeC[2]));
						lastUpdated = calS;

						JSONArray eventA = (JSONArray) jobject.get("events");
						Iterator i = eventA.iterator();

						while(i.hasNext()){
							JSONObject event = (JSONObject) i.next();
							events.add(new Event(
									(String) event.get("name"),
									(String) event.get("summary"),
									(String) event.get("startDate"),
									(String) event.get("endDate"),
									(String) event.get("id"),
									(String) event.get("calendarName"),
									(String) event.get("calId"),
									(String) event.get("colorNumber")
									));
						}
					}
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;	
			}
		}.execute();
	}

	private class FileIO extends AsyncTask<ArrayList<Event>, Void, Void>{

		@Override
		protected Void doInBackground(ArrayList<Event>... arg0) {

			ArrayList<Event> eventA = new ArrayList<Event>();
			eventA.addAll(arg0[0]);
			JSONObject jobj = new JSONObject();
			jobj.put("version", (new Integer(NEED_VERSION)).toString());
			jobj.put("dateLastMod", new SimpleDateFormat("yyy-MM-dd'T'HH:mm:ssXXX").format(Calendar.getInstance()));
			JSONArray events = new JSONArray();

			for (int i =0; i < eventA.size(); i++){

				Event eventO = eventA.get(i);
				JSONObject event = new JSONObject();

				event.put("name", eventO.getTitle());
				event.put("summary", eventO.getSummary());
				event.put("startDate", new SimpleDateFormat("yyy-MM-dd'T'HH:mm:ssXXX").format(eventO.getStartDate()));
				event.put("endDate", new SimpleDateFormat("yyy-MM-dd'T'HH:mm:ssXXX").format(eventO.getEndDate()));
				event.put("id", eventO.getId());
				event.put("calendarName", eventO.getCalendarName());
				event.put("calId", eventO.getCalId());
				event.put("colorNumber", (new Integer(eventO.getColorNumber())).toString());

				events.add(event);
			}
			jobj.put("events", events);

			try{
				FileWriter jsonFileWriter = new FileWriter(FILENAME);
				jsonFileWriter.write(jobj.toJSONString());
				jsonFileWriter.flush();
				jsonFileWriter.close();


			} catch(IOException e){
				e.printStackTrace();
			
			}

			return null; 
		}

	}

}
