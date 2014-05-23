package com.ellume.scan;

import java.util.ArrayList;
import java.util.Date;

public class EventSearch 
{
	public ArrayList eventSearch(String $q)
	{
		ArrayList<Event> query = new ArrayList();
		
		for(int i = 0; i<sendEventList().size();i++)
		{
			query.add(sendEventList().get(i));
		}
		
		return query;
		
	}
}
