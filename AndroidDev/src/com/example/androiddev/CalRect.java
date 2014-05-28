package com.example.androiddev;

import java.util.Calendar;

import android.graphics.RectF;

public class CalRect extends RectF{
	private int day, month, year;
	private static Calendar bufferCalendar;
	int color;
	private int numEvents;
	public CalRect(){
		super();
		numEvents=0;
	}
	public void incrementEvents()
	{
		numEvents++;
	}
	public int getNumEvents()
	{
		return numEvents;
	}
	public void clearEvents()
	{
		numEvents=0;
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
		day=$day; 
		month=$month; 
		year=$year;
	}
	public int getDay()
	{
		return day;
	}
	public int getMonth()
	{
		return month;
	}
	public int getYear()
	{
		return year;
	}
	public int getWeek()
	{
		bufferCalendar=Calendar.getInstance();
		bufferCalendar.set(year, month,day);
		return bufferCalendar.get(Calendar.WEEK_OF_MONTH);
	}
	
}
