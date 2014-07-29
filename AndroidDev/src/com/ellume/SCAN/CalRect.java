package com.ellume.SCAN;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.graphics.RectF;

public class CalRect extends RectF{
	private Calendar date;
	int color;
	private ArrayList<Event> events;
	public CalRect(){
		super();
		events=new ArrayList<Event>();
	}
	
	public int getNumEvents()
	{
		return events.size();
	}
	public void clearEvents()
	{
		events.clear();
	}
	public void addEvent(Event e){
		events.add(e);
	}
	public ArrayList<Event> getEvents()
	{
		return events;
	}
	public CalRect( float left, float top, float right, float bottom){
		super(left,top,right,bottom);
	}
	public void setColor(int $colorID)
	{
		color=$colorID;
	}
	public int getColor()
	{
		return color;
	}
	public void setDate(int $day, int $month, int $year){
		date=new GregorianCalendar($year, $month, $day);
	}
	public void setDate(Calendar c)
	{
		date=c;
	}
	public int getDay()
	{
		return date.get(Calendar.DATE);
	}
	public int getMonth()
	{
		return date.get(Calendar.MONTH);
	}
	public int getYear()
	{
		return date.get(Calendar.YEAR);
	}
	public int getWeek()
	{
		return date.get(Calendar.WEEK_OF_MONTH);
	}
	public Calendar getCal()
	{
		return date;
	}
	public String toString()
	{
		return super.toShortString()+" "+getDay()+"/"+getMonth()+"/"+getYear()+"-"+events.size();
	}
}
