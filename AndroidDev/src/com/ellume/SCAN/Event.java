package com.ellume.SCAN;


import java.util.Calendar;



import android.graphics.Color;
import android.util.Log;

public class Event implements Comparable {
	private String name;
	private String summary;
	private Calendar startDate;
	private Calendar endDate;
	private int color; //Color corresponds to category.
	private String id;
	private String calendarName;

public Event(String n, String s, Calendar start, Calendar end,int $color){
	name = n;
	summary = s;
	startDate = start;
	endDate = end;
	Log.v("tag" ,end.toString());
	color=$color;
}
public Event(com.google.api.services.calendar.model.Event current) {
	name = current.getSummary();
	summary = current.getDescription();
	Calendar calS = Calendar.getInstance();
	Calendar calE = Calendar.getInstance();
	
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
	
	color = Color.YELLOW;
	id = current.getId();
	calendarName = current.getOrganizer().getDisplayName();
	//cal.setTimeInMillis(current.getEnd().getDateTime().getValue());
	//endDate = cal;
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
public int getColor()
{
	return color;
}
public String toString()
{
	return getTitle();
}
public String getId() {
	// TODO Auto-generated method stub
	return null;
}
public void setColor(int color2) {
	// TODO Auto-generated method stub
	color = color2;
}
@Override

public int compareTo(Object arg0) {
	Event other = (Event) arg0;
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