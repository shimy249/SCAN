package com.ellume.SCAN;


import java.util.Calendar;



import android.graphics.Color;
import android.util.Log;

public class Event implements Comparable<Event> {
	private String name;
	private String summary;
	private Calendar startDate;
	private Calendar endDate;
	//Color corresponds to category.
	private String id;
	private String calendarName;
	private String calId;
	private int colorNumber;
public Event(String n, String s, Calendar start, Calendar end,String $calendarName, int $colorNumber){
	name = n;
	summary = s;
	startDate = start;
	endDate = end;
	Log.v("tag" ,end.toString());
	
	calendarName=$calendarName;
	colorNumber=$colorNumber;
}
public int getColorNumber()
{
	return colorNumber;
}
public Event(com.google.api.services.calendar.model.Event current, int $colorNumber) {
	name = current.getSummary();
	summary = current.getDescription();
	Calendar calS = Calendar.getInstance();
	Calendar calE = Calendar.getInstance();
	colorNumber=$colorNumber;
	//cal.setTimeInMillis(current.getStart().getDateTime().getValue());
	//startDate = cal;
	if(current.getStart().getDate()!=null){
		String[] date = current.getStart().getDate().toString().split("-");
		calS.set(Integer.parseInt(date[0]), Integer.parseInt(date[1])-1, Integer.parseInt(date[2]));
		date = current.getEnd().getDate().toString().split("-");
		calE.set(Integer.parseInt(date[0]), Integer.parseInt(date[1])-1, Integer.parseInt(date[2]));
		startDate = calS;
		endDate = calE;
	}
	else{
		String date = current.getStart().getDateTime().toString();
		
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
		
		calS.set(Integer.parseInt(dateC[0]), Integer.parseInt(dateC[1])-1, Integer.parseInt(dateC[2]), Integer.parseInt(timeC[0]), Integer.parseInt(timeC[1]), Integer.parseInt(timeC[2]));
		
		date = current.getEnd().getDateTime().toString();
		timeCutoff = date.indexOf(".");
		dateCutoff = date.indexOf("T");
		
		
		 //to remove nanoseconds
		if(timeCutoff == -1)  //if nanoseconds not set remove timezone shift
			timeCutoff = date.indexOf("-");
		if(timeCutoff == -1) //if timezone shift not set isolate time only
			timeCutoff= date.indexOf("Z");
		
		time = date.substring(dateCutoff+1, timeCutoff);
		String[] timeE = time.split(":");
		
		date = date.substring(0, dateCutoff);
		dateC = date.split("-");
		calE.set(Integer.parseInt(dateC[0]), Integer.parseInt(dateC[1])-1, Integer.parseInt(dateC[2]),Integer.parseInt(timeE[0]), Integer.parseInt(timeE[1]), Integer.parseInt(timeE[2]));
		
		startDate = calS;
		endDate = calE;
		
	}
	
	calId = current.getOrganizer().getEmail();
	id = current.getId();
	calendarName = current.getOrganizer().getDisplayName();
	//cal.setTimeInMillis(current.getEnd().getDateTime().getValue());
	//endDate = cal;
}

public Event(String n, String s, String start, String end, String i, String cn, String ci, String cN){
	name = n;
	summary = s;
	
	Calendar calS = Calendar.getInstance();
	Calendar calE = Calendar.getInstance();
	String date = start;
	
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
	
	calS.set(Integer.parseInt(dateC[0]), Integer.parseInt(dateC[1])-1, Integer.parseInt(dateC[2]), Integer.parseInt(timeC[0]), Integer.parseInt(timeC[1]), Integer.parseInt(timeC[2]));
	
	date = end;
	timeCutoff = date.indexOf(".");
	dateCutoff = date.indexOf("T");
	
	
	 //to remove nanoseconds
	if(timeCutoff == -1)  //if nanoseconds not set remove timezone shift
		timeCutoff = date.indexOf("-");
	if(timeCutoff == -1) //if timezone shift not set isolate time only
		timeCutoff= date.indexOf("Z");
	
	time = date.substring(dateCutoff+1, timeCutoff);
	String[] timeE = time.split(":");
	
	date = date.substring(0, dateCutoff);
	dateC = date.split("-");
	calE.set(Integer.parseInt(dateC[0]), Integer.parseInt(dateC[1])-1, Integer.parseInt(dateC[2]),Integer.parseInt(timeE[0]), Integer.parseInt(timeE[1]), Integer.parseInt(timeE[2]));
	
	startDate = calS;
	endDate = calE;
	
	id = i;
	calendarName = cn;
	calId = ci;
	colorNumber = Integer.parseInt(cN);
}


public Calendar getStartDate()
{
	return startDate;
}
public Calendar getEndDate()
{
	return endDate;
}
public String getTitle()
{
	return name;
}
public String getSummary()
{
	return summary;
}

public String toString()
{
	return getTitle()+id;
}
public String getId() {
	return id;
}
public String getCalId(){
	return calId;
}
public String getCalendarName(){
	return calendarName;
}

@Override

public int compareTo(Event arg0) {
	Event other =  arg0;
	Calendar otherCal =Calendar.getInstance();
	otherCal.setTimeInMillis(other.getStartDate().getTimeInMillis());
	Calendar thisCal = Calendar.getInstance();
	thisCal.setTimeInMillis(startDate.getTimeInMillis());
	otherCal.set(Calendar.HOUR, 0);
	otherCal.set(Calendar.MINUTE, 0);
	otherCal.set(Calendar.SECOND, 0);
	otherCal.set(Calendar.MILLISECOND, 0);
	thisCal.set(Calendar.HOUR, 0);
	thisCal.set(Calendar.MINUTE, 0);
	thisCal.set(Calendar.SECOND, 0);
	thisCal.set(Calendar.MILLISECOND, 0);
	return thisCal.compareTo(otherCal);
}
}