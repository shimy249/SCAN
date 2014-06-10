package com.ellume.scan;

import java.util.Date;

public class Event {
	private String name;
	private String summary;
	private Date startDate;
	private Date endDate;
	private int color; //Color corresponds to category.

public Event(String n, String s, Date start, Date end,int $color){
	name = n;
	summary = s;
	startDate = start;
	endDate = end;
	color=$color;
}
public Date getStartDate()
{
	return startDate;
}
public Date getEndDate()
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
}