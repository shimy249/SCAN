package com.ellume.SCAN;

import java.util.Calendar;
import java.util.Date;

public class Event {
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
}