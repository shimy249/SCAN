package com.example.androiddev;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.provider.CalendarContract;

public class Refresher extends Service {
	
	

	public static final String[] EVENT_PROJECTION = new String[]{
        CalendarContract.Calendars._ID,
        CalendarContract.Calendars.ACCOUNT_NAME,
        CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
        CalendarContract.Calendars.OWNER_ACCOUNT};

    //array indexes for above array, i dont like to remember
    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;
    private Event[] eventArray = null;


    public final BroadcastReceiver receiver = new BroadcastReceiver(){
        public void onReceive(Context context, Intent intent){
            checkCalendar();
        }


    };

    private void checkCalendar() {
        new AsyncTask<String, Void, Event[]>(){
            public Event[] doInBackground(String... string){

                ArrayList<Long> resultIDs = new ArrayList<Long>();
                ArrayList<Event> events = new ArrayList<Event>();

                //find the calendar
                Cursor cur = null;
                ContentResolver cr = getContentResolver();
                Uri uri = CalendarContract.Calendars.CONTENT_URI;
                String selection = "(("+ CalendarContract.Calendars.ACCOUNT_NAME+ " = ?) AND ("+
                        CalendarContract.Calendars.ACCOUNT_TYPE + " =?) AND" +
                        CalendarContract.Calendars.OWNER_ACCOUNT +" = ?)) ";
                String[] selectionArgs = new String[]{"account name", "account type", "owner"};
                cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);

                long calID = 0; //the unique ID of the calendar

                while(cur.moveToNext()){
                     calID = cur.getLong(PROJECTION_ID_INDEX);
                    String displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
                    String accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
                    String ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);

                    //find which calendar you want in here and break the loop with the correct calID

                }

                //look at events
                Cursor cursor = cr.query(Uri.parse("content://calendar/events"), new String[]{"caldendar_id", "title", "description", "dtstart", "dtend", "original_id"}, null, null, null);
                cursor.moveToFirst();

                for(int i = 0; i < cursor.getCount(); i++){
                    if (cursor.getInt(0)==calID){   //make sure it is right calendar
                        if(cursor.getInt(4) >= Calendar.getInstance().getTimeInMillis()){   //get events that happens in future
                            events.add(new Event(cursor.getString(1), cursor.getString(2), new Date(cursor.getLong(3)), new Date(cursor.getLong(4)), 0));//need to implement color
                        }

                    }
                }

                return (Event[]) events.toArray();
            }
            public void onPostExecute(Event[] result){
                eventArray = result;
            }
        };
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
