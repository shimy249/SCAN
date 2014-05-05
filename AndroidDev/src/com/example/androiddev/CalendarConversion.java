package com.example.androiddev;

import java.util.Calendar;

public class CalendarConversion {
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
}
