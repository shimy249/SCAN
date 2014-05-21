package com.example.androiddev;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.TextView;

public class YearCalendar extends CalendarV {
	private Calendar myCal;
	private final RectF[] mySquares=new CalRect[49];
	int month;
	public YearCalendar(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a=context.getTheme().obtainStyledAttributes(attrs, R.styleable.CalendarV, 0, 0);
		try{
			month=a.getInt(R.styleable.YearCalendar_month, 0);
		}
		catch(Exception e){}
		finally{
			a.recycle();
		}
		myCal=(new GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR), (month), 1));
	}
	public int find(String s){
		for(int i=0; i<this.monthNames.length;i++)
			if(monthNames[i].equals(s))
				return i;
		return -1;
	}
	public void initRect(){
		for(int i=0; i<mySquares.length; i++)
		{
			mySquares[i]=new CalRect();
		}
		myMonthLabel=new CalRect();
	}
	public float getRecommendedSize(RectF box, float factor){
		return (float)Math.sqrt(box.height()*box.width())/2;
	} 
	public void translate(float distanceY, int index, int end){
	}
	public void drawMonthLabel(Canvas c){
		alignX=ALIGN_CENTER;
		alignY=ALIGN_CENTER;
		drawBox(myMonthLabel,c,getResources().getColor(R.color.SchoolColor2));
		drawText(myMonthLabel, c,"" + monthNames[month],getResources().getColor(R.color.White));
	}
	public void onDraw(Canvas canvas){
		canvas.drawColor(getResources().getColor(R.color.SchoolColor1));
		drawMonthLabel(canvas);
		drawDayLabels(canvas);
		drawText(canvas);
	}
	private void drawText(Canvas canvas){
		alignX=ALIGN_CENTER;
		alignY=ALIGN_CENTER;
		for(int i=0; i<mySquares.length; i++)
		{
			if(myCalendar.get(Calendar.MONTH)==((CalRect)mySquares[i]).getMonth())
				drawText(mySquares[i],canvas,""+((CalRect)(mySquares[i])).getDay(),getResources().getColor(R.color.White));
		}
	}
	public void drawText(RectF box, Canvas canvas, String message, int color)
	{
		float factor=2f/7f;
		float $textSize=getRecommendedSize(box,factor);
		textPainter.setTextSize($textSize);
		while(textPainter.measureText(message)>box.width())
		{
			factor-=1/28f;
			$textSize=getRecommendedSize(box,factor);
			textPainter.setTextSize($textSize);
		}
		if(message.length()>0){
			float prevTextSize=textPainter.getTextSize();
			textPainter.setTextSize($textSize);
			int prevColor=textPainter.getColor();
			textPainter.setColor(color);
			float penX, penY;
			penX=box.left;
			penY=box.top;
			switch(alignX){
			case ALIGN_LEFT:
				penX+=3;
				break;
			case ALIGN_RIGHT:
				penX+=box.width()-textPainter.measureText(message);
				break;
			case ALIGN_CENTER:
				penX+=box.width()/2-textPainter.measureText(message)/2;
				break;
			default:
				penX+=3;
				break;
			}
			switch(alignY){
			case ALIGN_TOP:
				penY+=(textPainter.descent()-textPainter.ascent());
				break;
			case ALIGN_CENTER:
				penY+=(box.height()/2)-(textPainter.ascent()+textPainter.descent())/2;
				break;
			case ALIGN_BOTTOM:
				penY+=(box.height()-3);
				break;
			default:
				penY+=(textPainter.descent()-textPainter.ascent());
			}
			canvas.drawText(message, penX, penY, textPainter);
			textPainter.setTextSize(prevTextSize);
			textPainter.setColor(prevColor);
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
	public void preAllocSquares()
	{
		for(int i=0; i<mySquares.length; i++)
		{
			if(i%2==0)
				((CalRect)(mySquares[i])).setColor(getResources().getColor(R.color.SchoolColor1));
		}
		bufferCalendar.set(myCal.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), 1);
		int day=bufferCalendar.get(Calendar.DAY_OF_WEEK)-1;
		int maxDay=bufferCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		for(int i=0; i<bufferCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);i++)
		{
			((CalRect)mySquares[i+day]).setDate(i+1, myCal.get(Calendar.MONTH), myCal.get(Calendar.YEAR));
		}
		for(int i=maxDay+day; i<mySquares.length; i++)
		{
			if(myCal.get(Calendar.MONTH)!=11)
				((CalRect)mySquares[i]).setDate(i-maxDay-day+1, myCal.get(Calendar.MONTH)+1, myCalendar.get(Calendar.YEAR));
			else
				((CalRect)mySquares[i]).setDate(i-maxDay-day+1, 0, myCal.get(Calendar.YEAR)+1);
		}
		if(bufferCalendar.get(Calendar.MONTH)!=0)
			bufferCalendar.set(Calendar.MONTH, bufferCalendar.get(Calendar.MONTH)-1);
		else
		{
			bufferCalendar.set(Calendar.MONTH, Calendar.DECEMBER);
			bufferCalendar.set(Calendar.YEAR, bufferCalendar.get(Calendar.YEAR)-1);
		}
		for(int i=day-1; i>=0; i--)
		{
			((CalRect)mySquares[i]).setDate(bufferCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)+i-day+1, bufferCalendar.get(Calendar.MONTH), bufferCalendar.get(Calendar.YEAR));
		}	
		firstDay=day;
	}
}
