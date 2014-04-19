package com.example.androiddev;

import java.util.Date;

public class Event {
	private String name;
	private String summary;
	private Date startDate;
	private Date endDate;

}
public Event(String n, String s, Date start, Date end){
	name = n;
	summary = s;
	startDate = start;
	endDate = end;
}
