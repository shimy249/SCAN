package com.ellume.scan;

import java.util.Calendar;

import android.graphics.RectF;

public class CalRect extends RectF{
	private int day, month, year;
	private Calendar bufferCalendar;
	int color;
	public CalRect(){
		super();
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
