package com.ellume.SCAN;

import java.util.ArrayList;

public class MergeSort {
	public MergeSort()
	{
		
	}
	public static void sortEvents(ArrayList<Event> events)
	{
		if(events.size()>1)
		{
			ArrayList<Event> split1=split(events, 0, events.size()/2);
			ArrayList<Event> split2=split(events,events.size()/2, events.size());
			sortEvents(split1);
			sortEvents(split2);
			merge(events,split1, split2);
		}
	}
	private static ArrayList<Event> split(ArrayList<Event> events, int start, int end)
	{
		ArrayList<Event> split=new ArrayList<Event>();
		for(int i=start; i<end; i++)
		{
			split.add(events.get(i));
		}
		return split;
	}
	private static void merge(ArrayList<Event> e, ArrayList<Event> l1, ArrayList<Event> l2)
	{
		int pos,pos1,pos2;
		pos=pos1=pos2=0;
		while(pos1<l1.size() && pos2<l2.size())
		{
			if(comp(l1.get(pos1),l2.get(pos2))<0)
				e.set(pos++, l1.get(pos1++));
			else
				e.set(pos++, l2.get(pos2++));
		}
		while(pos1<l1.size())
			e.set(pos++, l1.get(pos1++));
		while(pos2<l2.size())
			e.set(pos++, l2.get(pos2++));
	}
	private static int comp(Event a , Event b)
	{
		long date1=a.getStartDate().getTimeInMillis();
		long date2=b.getStartDate().getTimeInMillis();
		return (int)(date1/3600000-date2/3600000);
	}
}
