package com.example.androiddev;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class CalendarV extends View{
	private static final String TAG = "CalendarV";
	private final static RectF[] mySquares=new CalRect[49];
	protected final static RectF[] myDayLabels=new CalRect[8];
	protected final static RectF[] myWeekNumbers=new CalRect[7];
	private final RectF bufferRect;
	int firstDay;
	int lastTouchEvent;
	boolean mTap; //Used to determine tap.
	protected RectF myMonthLabel;
	static float translationFactor;
	public final String[] myDayLabelsNames={"","Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
	public final String[] monthNames={"January","February","March","April","May","June","July","August","September","October","November","December"};
	private final float SPACING_OFFSET=20;
	private float intervalX, intervalY;
	private boolean showBackground;
	private float translationFactorEvents;
	public static final int ALIGN_TOP=0;
	public static final int ALIGN_BOTTOM=2;
	public static final int ALIGN_CENTER=1;
	public static final int ALIGN_LEFT=0;
	public static final int ALIGN_RIGHT=2;
	public ArrayList<Integer> indexes;
	private ArrayList<Event> mEvents;
	protected int alignX, alignY;
	Paint textPainter, linePainter, boxPainter,selectedBoxPainter;
	static int selectedBox;
	static Calendar myCalendar;
	static Calendar bufferCalendar;
	private GestureDetector mDetector;
	private Canvas mCanvasOverlay;
	private Bitmap overlayMap;
	public CalendarV(Context context, AttributeSet attrs){
		super(context, attrs);
		bufferRect=new RectF();
		TypedArray a=context.getTheme().obtainStyledAttributes(attrs, R.styleable.CalendarV, 0, 0);
		try{
			showBackground=a.getBoolean(R.styleable.CalendarV_showBackground, false);
		}
		finally{
			a.recycle();
		}
		initRect();
		initPaint();
		initCal();
		mDetector=new GestureDetector(this.getContext(),new GestureListener(this));
		mTap=false;
		mCanvasOverlay=new Canvas();
		indexes=new ArrayList<Integer>();
	}
	public void initCal(){
		if(myCalendar==null)
		{
			myCalendar=GregorianCalendar.getInstance();
			selectedBox=myCalendar.get(Calendar.DAY_OF_MONTH)+1;
		}
		bufferCalendar=new GregorianCalendar();
	}
	public void setCal(Calendar c){
		myCalendar=c;
		invalidate();
		requestLayout();
	}
	public void onSizeChanged(int w, int h, int oldw, int oldh){

		int padX=getPaddingLeft();
		int padY=getPaddingTop();
		float monthLabelH=h/6;
		myMonthLabel.set(0, 0, w, monthLabelH);
		myWeekNumbers[0]=myDayLabels[0];
		float dayLabelH=40;
		float weekLabelW=50;
		if(w>h){
			monthLabelH=0;
		}
		intervalY=(h-monthLabelH-dayLabelH)/6;
		intervalX=(w-weekLabelW)/7;
		myDayLabels[0].set(0, monthLabelH, weekLabelW, monthLabelH+dayLabelH);
		myWeekNumbers[0]=myDayLabels[0];
		for(int i=1; i<myDayLabels.length;i++)
		{
			myDayLabels[i].set(myDayLabels[i-1].right, myDayLabels[i-1].top, myDayLabels[i-1].right+intervalX, myDayLabels[i-1].bottom);
		}
		for(int i=1; i<myWeekNumbers.length; i++)
		{
			myWeekNumbers[i].set(myWeekNumbers[i-1].left, myWeekNumbers[i-1].bottom, myWeekNumbers[i-1].right, myWeekNumbers[i-1].bottom+intervalY);
		}
		for(int i=0; i<mySquares.length;i++)
		{
			mySquares[i].set(myDayLabels[i%7+1].left, myWeekNumbers[1].top+intervalY*(i/7), myDayLabels[i%7+1].right, myWeekNumbers[1].bottom+(intervalY*(i/7)));
		}
		overlayMap=Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		preAllocSquares();
	}
	public void preAllocSquares()
	{
		for(int i=0; i<mySquares.length; i++)
		{
			if(i%2==0)
				((CalRect)(mySquares[i])).setColor(getResources().getColor(R.color.SchoolColor1));
			else
				((CalRect)(mySquares[i])).setColor(getResources().getColor(R.color.SchoolColor2));
		}
		bufferCalendar.set(myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), 1);
		int day=bufferCalendar.get(Calendar.DAY_OF_WEEK)-1;
		int maxDay=bufferCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		for(int i=0; i<bufferCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);i++)
		{
			((CalRect)mySquares[i+day]).setDate(i+1, myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.YEAR));
		}
		for(int i=maxDay+day; i<mySquares.length; i++)
		{
			if(myCalendar.get(Calendar.MONTH)!=11)
				((CalRect)mySquares[i]).setDate(i-maxDay-day+1, myCalendar.get(Calendar.MONTH)+1, myCalendar.get(Calendar.YEAR));
			else
				((CalRect)mySquares[i]).setDate(i-maxDay-day+1, 0, myCalendar.get(Calendar.YEAR)+1);
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
	public void initRect(){
		for(int i=0; i<mySquares.length; i++)
		{
			mySquares[i]=new CalRect();
		}
		for(int i=0; i<myWeekNumbers.length;i++)
		{
			myWeekNumbers[i]=new CalRect();
		}
		for(int i=0; i<myDayLabels.length; i++)
		{
			myDayLabels[i]=new CalRect();
		}

		myMonthLabel=new CalRect();
	}
	public void initPaint(){
		textPainter=new Paint(Paint.ANTI_ALIAS_FLAG);
		linePainter=new Paint(Paint.ANTI_ALIAS_FLAG);
		boxPainter=new Paint(0);
		selectedBoxPainter=new Paint(0);
		selectedBoxPainter.setAlpha(100);
	}
	public void drawBox(RectF box, Canvas canvas, int color){
		int prevColor=boxPainter.getColor();
		boxPainter.setColor(color);
		canvas.drawRect(box, boxPainter);
		boxPainter.setColor(prevColor);
	}
	public void drawBox(RectF box, Canvas canvas, int color, Paint p){
		int prevColor=p.getColor();
		p.setColor(color);
		canvas.drawRect(box, p);
		p.setColor(prevColor);
	}
	public void drawText(RectF box, Canvas canvas, String message, int color, float $textSize)
	{
		if(message.length()>0){
			float prevTextSize=textPainter.getTextSize();
			textPainter.setTextSize($textSize);
			int prevColor=textPainter.getColor();
			textPainter.setColor(getResources().getColor(R.color.SchoolColor2));
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
				penY+=(box.height()/2);
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
	public float getRecommendedSize(RectF box){
		return ((float)(Math.sqrt(box.width()*box.height())/7)*2);
	}
	public float getRecommendedSize(RectF box, float factor){
		return ((float)(Math.sqrt(box.width()*box.height()))*factor);
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
	public void drawText(RectF box, Canvas canvas, int color, String message, double d)
	{
		if(message.length()>0){
			float prevTextSize=textPainter.getTextSize();
			int prevColor=textPainter.getColor();
			textPainter.setColor(color);
			textPainter.setTextSize((float) d);
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
				penY+=(box.height()/2-(textPainter.ascent()+textPainter.descent())/2);
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
			return;
		}
	}
	private void drawMonthLabel(Canvas c){
		alignX=ALIGN_CENTER;
		alignY=ALIGN_CENTER;
		if(getHeight()>getWidth()){
			TextView myText=(TextView)((CalActivity)getContext()).findViewById(R.id.CalendarTitle);
			myText.setTextSize(10);
			myText.setText("");
			drawBox(myMonthLabel,c,getResources().getColor(R.color.SchoolColor1));
			drawText(myMonthLabel, c,monthNames[myCalendar.get(Calendar.MONTH)]+" - "+myCalendar.get(Calendar.YEAR),getResources().getColor(R.color.White));
		}
		else{
			TextView myText=(TextView)((CalActivity)getContext()).findViewById(R.id.CalendarTitle);
			myText.setTextSize(30);
			myText.setText(""+monthNames[myCalendar.get(Calendar.MONTH)]+" - "+myCalendar.get(Calendar.YEAR));
		}
	}
	private void drawDayLabels(Canvas canvas){
		for(int i=0; i<myDayLabels.length; i++)
		{
			if(i%2==0)
			{
				drawBox(myDayLabels[i],canvas,getResources().getColor(R.color.SchoolColor1));

			}
			else{
				drawBox(myDayLabels[i],canvas,getResources().getColor(R.color.SchoolColor2));
			}
			if(myDayLabelsNames[i].length()>0)
				drawText(myDayLabels[i],canvas,getResources().getColor(R.color.White),myDayLabelsNames[i].substring(0,3),(3*myDayLabels[i].height()/4));
		}
	}
	private void drawWeekLabels(Canvas canvas){
		for(int i=0; i<myWeekNumbers.length;i++)
		{
			if(i%2==0)
				drawBox(myWeekNumbers[i],canvas,getResources().getColor(R.color.SchoolColor1));
			else
			{
				drawBox(myWeekNumbers[i],canvas,getResources().getColor(R.color.SchoolColor2));
			}
		}
		bufferCalendar.set(myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), 1);
		int weekno=bufferCalendar.get(Calendar.WEEK_OF_YEAR);
		for(int i=1; i<myWeekNumbers.length;i++)
			drawText(myWeekNumbers[i],canvas,""+(weekno++),getResources().getColor(R.color.White));
	}
	private void drawSquares(Canvas canvas){
		//----Draw Backgrounds---------------------
		for(int i=0; i<mySquares.length; i++)
		{
			drawBox(mySquares[i],canvas, ((CalRect)(mySquares[i])).getColor());
		}
		drawSelectedSquare(canvas);
		alignX=ALIGN_LEFT;
		alignY=ALIGN_TOP;
		for(int i=0; i<mySquares.length; i++)
		{
			if(myCalendar.get(Calendar.MONTH)==((CalRect)mySquares[i]).getMonth())
				drawText(mySquares[i],canvas,""+((CalRect)(mySquares[i])).getDay(),getResources().getColor(R.color.White));
			else
				drawText(mySquares[i],canvas,""+((CalRect)(mySquares[i])).getDay(),getResources().getColor(R.color.Grey));
		}
	}
	public void drawSelectedSquare(Canvas c){
		if(selectedBox>=0 && selectedBox<=48)
			drawBox(mySquares[selectedBox],c,getResources().getColor(R.color.SelectedColor),selectedBoxPainter);
	}
	public void onDraw(Canvas canvas){
		translate(translationFactor, 0, mySquares.length);
		if(isFirstLayerVisible())
		{
			moveLayerUp();
		}
		if(checkForEvents())
		{
			
		}
		if(this.isUpperLayerShowing())
			moveLayerDown();
		drawSquares(canvas);
		drawMonthLabel(canvas);
		drawDayLabels(canvas);
		drawWeekLabels(canvas);
		drawDecals(canvas);
	}
	public void drawDecals(Canvas canvas)
	{
		drawCurrentDayDecal(canvas);
	}
	private void drawCurrentDayDecal(Canvas canvas)
	{
		int index=-1;
		for(int i=0; i<mySquares.length; i++)
			if(((CalRect)mySquares[i]).getDay()==Calendar.getInstance().get(Calendar.DAY_OF_MONTH) && ((CalRect)mySquares[i]).getMonth()==Calendar.getInstance().get(Calendar.MONTH) && ((CalRect)mySquares[i]).getYear()==Calendar.getInstance().get(Calendar.YEAR))
				index=i;
		if(index!=-1){
			bufferRect.set(mySquares[index].centerX()-10, mySquares[index].bottom-30, mySquares[index].centerX()+10, mySquares[index].bottom-10);
			int prevColor=textPainter.getColor();
			textPainter.setColor(getResources().getColor(R.color.White));
			canvas.drawOval(bufferRect, textPainter);
			textPainter.setColor(prevColor);
		}
	}
	public boolean onTouchEvent(MotionEvent e)
	{
		boolean result=mDetector.onTouchEvent(e);
		/*if(!result)
		{
			if(e.getAction()==MotionEvent.ACTION_UP)
			{
				Log.v(TAG,"Action Recorded as Up");

				result=!result;
			}
		}*/
		return result;
	}
	public void selectDate(float x, float y)
	{
		for(int i=0; i<mySquares.length;i++)
		{
			if(mySquares[i].contains(x, y))
			{
				selectedBox=i;
				invalidate();
				requestLayout();

			}
		}
	}
	public void fade(int i)
	{
		/*	ObjectAnimator mFadeAnimator = ObjectAnimator.ofInt(this, "AlphaLevel", getResources().getColor(R.color.SelectedColor));
		mFadeAnimator.setIntValues(getResources().getColor(R.color.SchoolColor1));
		mFadeAnimator.setDuration(1000);
		mFadeAnimator.start();*/
	}
	public void setAlphaLevel(int i)
	{
		selectedBoxPainter.setColor(i);
		invalidate();
		requestLayout();
	}
	public void nextMonth(boolean prealloc){
		myCalendar.set(Calendar.MONTH, myCalendar.get(Calendar.MONTH)+1);
		if(prealloc)
			preAllocSquares();
		invalidate();
		requestLayout();
	}
	public void previousMonth(boolean prealloc){
		myCalendar.set(Calendar.MONTH, myCalendar.get(Calendar.MONTH)-1);
		if(prealloc)
			preAllocSquares();
		invalidate();
		requestLayout();
	}
	public int nextLine(int i){
		if(i<=6)
			return 7;
		else if(i<=13)
			return 14;
		else if(i<=20)
			return 21;
		else if(i<=27)
			return 28;
		else if(i<=34)
			return 35;
		else
			return 42;
	}
	public void translate(float distanceY, int index, int end){
		for(int i=index; i<end; i++)
		{
			mySquares[i].set(mySquares[i].left, mySquares[i].top+distanceY, mySquares[i].right, mySquares[i].bottom+distanceY);
		}
	}
	public void setTranslationFactor(float f)
	{
		translationFactor=f;
		invalidate();
		requestLayout();
	}
	private boolean isFirstLayerVisible()
	{
		return (mySquares[0].bottom<myDayLabels[0].bottom);
	}
	private boolean isUpperLayerShowing()
	{
		return (mySquares[0].top>myDayLabels[0].bottom);
	}
	private void moveLayerUp()
	{
		for(int i=0; i<mySquares.length-7; i++)
		{
			mySquares[i]=mySquares[i+7];
		}
		int month=((CalRect)mySquares[41]).getMonth();
		int day=((CalRect)mySquares[41]).getDay();
		int year=((CalRect)mySquares[41]).getYear();
		bufferCalendar.set(year, month,day);
		int maxDay=bufferCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		int j;
		if(((CalRect)mySquares[41]).getColor()==getResources().getColor(R.color.SchoolColor1))
			j=0;		
		else
			j=1;
		for(int i=42; i<mySquares.length; i++)
		{
			mySquares[i]=new CalRect();
			mySquares[i].set(mySquares[i-7].left,mySquares[i-7].bottom,mySquares[i-7].right,mySquares[i-7].bottom+intervalY);
			if((j++)%2==0)
				((CalRect)mySquares[i]).setColor(getResources().getColor(R.color.SchoolColor2));
			else
				((CalRect)mySquares[i]).setColor(getResources().getColor(R.color.SchoolColor1));
			if(day==maxDay)
			{
				day=0;
				if(month==11)
				{
					year+=1; month=0;
				}
				else
					month++;
				bufferCalendar.set(year, month,day);
				maxDay=bufferCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
			}

			((CalRect)mySquares[i]).setDate(++day, month, year);
		}
		myCalendar.set(Calendar.MONTH, ((CalRect)mySquares[20]).getMonth());
		myCalendar.set(Calendar.YEAR, ((CalRect)mySquares[20]).getYear());

		selectedBox-=7;
	}
	private void moveLayerDown()
	{
		for(int i=mySquares.length-1; i>=7; i--)
			mySquares[i]=mySquares[i-7];
		int month=((CalRect)mySquares[7]).getMonth();
		int day=((CalRect)mySquares[7]).getDay();
		int year=((CalRect)mySquares[7]).getYear();
		bufferCalendar.set(year, month,day);
		int maxDay=bufferCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		int j;
		if(((CalRect)mySquares[7]).getColor()==getResources().getColor(R.color.SchoolColor1))
			j=0;		
		else
			j=1;
		for(int i=6; i>=0; i--)
		{
			mySquares[i]=new CalRect();
			mySquares[i].set(mySquares[i+7].left,mySquares[i+7].top-intervalY,mySquares[i+7].right,mySquares[i+7].top);
			if((j++)%2==0)
				((CalRect)mySquares[i]).setColor(getResources().getColor(R.color.SchoolColor2));
			else
				((CalRect)mySquares[i]).setColor(getResources().getColor(R.color.SchoolColor1));
			if(--day==0)
			{

				if(bufferCalendar.get(Calendar.MONTH)==0)
				{
					bufferCalendar.set(Calendar.YEAR, bufferCalendar.get(Calendar.YEAR)-1);
					bufferCalendar.set(Calendar.MONTH, 11);
				}
				else
					bufferCalendar.set(Calendar.MONTH, bufferCalendar.get(Calendar.MONTH)-1);
				maxDay=bufferCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
				day=maxDay;
			}
			((CalRect)mySquares[i]).setDate(day, bufferCalendar.get(Calendar.MONTH), bufferCalendar.get(Calendar.YEAR));

		}
		myCalendar.set(Calendar.MONTH, ((CalRect)mySquares[20]).getMonth());
		myCalendar.set(Calendar.YEAR, ((CalRect)mySquares[20]).getYear());
		selectedBox+=7;
	}
	public void addEvents(ArrayList<Event> events)
	{
		mEvents=events;
		invalidate();
		requestLayout();
	}
	@SuppressWarnings("deprecation")
	public boolean checkForEvents()
	{
		indexes.clear();
		if(mEvents!=null)
			for(int j=0; j<mEvents.size(); j++)
			{
				if(mEvents.get(j).getStartDate().getDate()==((CalRect)mySquares[selectedBox]).getDay()
				   && mEvents.get(j).getStartDate().getMonth()==((CalRect)mySquares[selectedBox]).getMonth()
				   && mEvents.get(j).getStartDate().getYear()==((CalRect)mySquares[selectedBox]).getYear())
				{
					indexes.add(j);
				}
					
			}
		return indexes.size()!=0;
	}
}
