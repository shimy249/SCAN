package com.ellume.scan;

import java.util.Date;

import android.util.Log;

import com.google.api.client.util.DateTime;

public class Event {
	public static final String FIELDS = "items(id,summary,description,start,end)";
	private String id;
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

public Event(com.google.api.services.calendar.model.Event current) {
	id = current.getId();
	name = current.getSummary(); //title
	summary = current.getDescription(); //description or summary whatever
	//startDate = new Date(current.getStart().getDateTime().getValue());
	//endDate = new Date(current.getEnd().getDateTime().getValue());
		
	//int startIndex = name.indexOf("[");
	//int endIndex = name.indexOf("]");
	//startIndex +=1;
	
	//String category = name.substring(startIndex,endIndex);
	
	
	
}

public String getId(){
	return id;
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