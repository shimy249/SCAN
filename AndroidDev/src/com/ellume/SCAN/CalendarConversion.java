package com.ellume.SCAN;

import java.util.Calendar;
import java.util.Date;

public class CalendarConversion {
	private static final String[] months={"January","February","March","April","May","June","July","August","September","October","November","December"};
	public static String CalendarToString(Calendar cal)
	{
		String s="";
		s+=cal.get(Calendar.MONTH)+"/"+cal.get(Calendar.DAY_OF_MONTH)+"/"+cal.get(Calendar.YEAR)+" at "+ cal.get(Calendar.HOUR)+":";
		if(cal.get(Calendar.MINUTE)<10)
		{
			s+="0";
		}
		s+=cal.get(Calendar.MINUTE);
		if(cal.get(Calendar.HOUR_OF_DAY)>12)
		{
			s+=" PM";
		}
		else
		{
			s+=" AM";
		}
		return s;
	}
	public static String Month__DD(Calendar cal)
	{
		String s="";
		s+=months[cal.get(Calendar.MONTH)]+" "+cal.get(Calendar.DAY_OF_MONTH);
		return s;
	}
	public static String Month__DD__YYYY(Calendar cal){
		String s=Month__DD(cal);
		s+=", "+cal.get(Calendar.YEAR);
		return s;
	}
	public static String CalendarToString(Date cal)
	{
		String s="";
		s+=cal.getMonth()+"/"+cal.getDate()+"/"+cal.getYear();
		if(cal.getMinutes()<10)
		{
			s+="0";
		}
		s+=cal.getMinutes();
		if(cal.getHours()>12)
		{
			s+=" PM";
		}
		else
		{
			s+=" AM";
		}
		return s;
	}
}
