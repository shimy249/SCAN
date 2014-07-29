package com.ellume.SCAN;

import java.text.ParseException;
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
	name = current.getSummary();
	summary = current.getDescription();
	Calendar calS = Calendar.getInstance();
	Calendar calE = Calendar.getInstance();
	//cal.setTimeInMillis(current.getStart().getDateTime().getValue());
	//startDate = cal;
	try {
		calS.setTime(RFC3339Date.parseRFC3339Date(current.getStart().getDateTime().toStringRfc3339()));
		startDate = calS;
		calE.setTime(RFC3339Date.parseRFC3339Date(current.getEnd().getDateTime().toStringRfc3339()));
		startDate = calE;
	} catch (IndexOutOfBoundsException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
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