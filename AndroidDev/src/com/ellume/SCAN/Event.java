package com.ellume.SCAN;

import java.util.Calendar;
import java.util.Date;

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
	// TODO Auto-generated constructor stub
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