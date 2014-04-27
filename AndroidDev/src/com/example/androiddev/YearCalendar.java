package com.example.androiddev;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.GestureDetector;

public class YearCalendar extends CalendarV {
	private int month;
	public YearCalendar(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a=context.getTheme().obtainStyledAttributes(attrs, R.styleable.YearCalendar, 0, 0);
		try{
			month=a.getInt(R.styleable.YearCalendar_month, 1);
		}
		catch(Exception e){}
		finally{
			a.recycle();
		}
		mDetector=new GestureDetector(this.getContext(),new GestureListener(this,false));
		if(bufferCalendar==null){
		myCalendar=new GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR), month,1);
		
		}
		else{
			myCalendar=new GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR), month,1);
		}
	}
	public float getRecommendedSize(RectF box, float factor){
		return (float)Math.sqrt(box.height()*box.width())/2;
	} 
	public void drawMonthLabel(Canvas c){
		alignX=ALIGN_CENTER;
		alignY=ALIGN_CENTER;
		drawBox(myMonthLabel,c,getResources().getColor(R.color.SchoolColor2));
		String s="" + monthNames[month];
		drawText(myMonthLabel,c,s,getResources().getColor(R.color.White),super.getRecommendedSize(myMonthLabel));
	}
	public void onDraw(Canvas canvas){
		canvas.drawColor(getResources().getColor(R.color.SchoolColor1));
		drawMonthLabel(canvas);
		drawDayLabels(canvas);
		drawNumbers(canvas);
	}
	private void drawNumbers(Canvas canvas){
		alignX=ALIGN_CENTER;
		alignY=ALIGN_CENTER;
		myCalendar=new GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR), month,1);
		super.preAllocSquares();
		for(int i=0; i<mySquares.length; i++)
		{
			if(myCalendar.get(Calendar.MONTH)==((CalRect)mySquares[i]).getMonth())
				drawText(mySquares[i],canvas,""+((CalRect)(mySquares[i])).getDay(),getResources().getColor(R.color.White),getRecommendedSize(mySquares[i],0));
		}
	}

	private void drawDayLabels(Canvas canvas){
		for(int i=0; i<myDayLabels.length; i++)
		{
			drawBox(myDayLabels[i],canvas,getResources().getColor(R.color.SchoolColor1));

			if(myDayLabelsNames[i].length()>0)
				drawText(myDayLabels[i],canvas,getResources().getColor(R.color.White),myDayLabelsNames[i].substring(0,1),(3*myDayLabels[i].height()/4));
		}
	}
	public static void onDestroy()
	{
		bufferCalendar=null;
	}
}
