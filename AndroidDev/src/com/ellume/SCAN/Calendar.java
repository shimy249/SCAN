package com.ellume.SCAN;

public class Calendar {

	private String name;
	private String id; //Google Calendar ID
	private String color;
	private String subject;
	private String teacher;
	private boolean isSelected;

	public Calendar(String n, String i, String c, String s, String t, boolean selected){
		name = n;
		id = i;
		color = c;
		subject = s;
		teacher = t;
		isSelected = selected;
	}
	public String getName(){
		return name;
	}
	public String getId(){
		return id;
	}
	public String getColor(){
		return color;
	}
	public String getSubject(){
		return subject;
	}
	public String getTeacher(){
		return teacher;
	}
	public boolean isSelected(){
		return isSelected;
	}
	
}