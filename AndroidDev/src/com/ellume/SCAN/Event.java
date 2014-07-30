package com.ellume.SCAN;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import android.util.Log;

public class Event {
	protected static final String FIELDS = null;
	private String name;
	private String summary;
	private Calendar startDate;
	private Calendar endDate;
	private int color; //Color corresponds to category.

public Event(String n, String s, Calendar start, Calendar end,int $color){
	name = n;
	summary = s;
	startDate = start;
	endDate = end;
	color=$color;
}
public Event(com.google.api.services.calendar.model.Event current) {
	name = current.getSummary();
	summary = current.getDescription();
	Calendar calS = Calendar.getInstance();
	Calendar calE = Calendar.getInstance();
	Log.v("tag", current.getStart().getDate().toString());
	//cal.setTimeInMillis(current.getStart().getDateTime().getValue());
	//startDate = cal;
	String[] date = current.getStart().getDate().toString().split("-");
	calS.set(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]));
	date = current.getEnd().getDate().toString().split("-");
	calE.set(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]));
	startDate = calS;
	endDate = calE;
	color = 0xff000000;
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
}