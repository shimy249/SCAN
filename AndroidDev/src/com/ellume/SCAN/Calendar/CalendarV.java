package com.ellume.SCAN.Calendar;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.ellume.SCAN.Event;
import com.ellume.SCAN.MergeSort;
import com.ellume.SCAN.R;
import com.ellume.SCAN.Settings.CalendarStyles;
import com.ellume.SCAN.Settings.SettingsFragment;

@SuppressLint("ClickableViewAccessibility")
public class CalendarV extends View{
	//-----------------------------------Constants for Calls from Events:------------------
	public static float BOX_HEIGHT=60;
	//-----------------------------------Constants-----------------------------------------
	protected int alignX, alignY;
	public static final int ALIGN_TOP=0;
	public static final int ALIGN_BOTTOM=2;
	public static final int ALIGN_CENTER=1;
	public static final int ALIGN_LEFT=0;
	public static final int ALIGN_RIGHT=2;
	private static final String TAG = "CalendarV";
	public final String[] myDayLabelsNames={"","Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
	public final String[] monthNames={"January","February","March","April","May","June","July","August","September","October","November","December"};
	ArrayList<Integer> selected_Calendar_Colors;
	//-------------------------------------------------------------------------------------

	//------------------------------------Settings-----------------------------------------
	private final String CURRENT_DAY_INDICATOR_SHAPE;
	private final String EVENT_INDICATOR_SHAPE;
	private final boolean ROUND_DETAIL_EVENT_CORNERS;
	//-------------------------------------------------------------------------------------

	//---------------------------------Pre-Allocated_Calendar:-----------------------------
	protected final  CalRect[] mySquares=new CalRect[49];
	protected final static RectF[] myDetailedEvents=new RectF[4];
	protected final static RectF[] myDayLabels=new CalRect[8];
	protected final static RectF[] myWeekNumbers=new CalRect[7];
	protected RectF myMonthLabel;
	private final RectF bufferRect;
	//-------------------------------------------------------------------------------------

	//--------------------------------Pre-Allocated_Events:--------------------------------

	private static ArrayList<Event> mEvents;	//List of the Events
	//-------------------------------------------------------------------------------------

	//--------------------------------Helpful Indices and Calculated Variables:------------

	private float translationFactor;	//Used in motion of opening the DetailedDayBoxes
	private float intervalX, intervalY;	//Height and Width of boxes, used for iteration
	private static SelectedBox selectedBox;	//The Box currently selected by the user
	//-------------------------------Painters:---------------------------------------------
	private Paint textPainter, linePainter, boxPainter,ovalPainter;

	//-------------------------------Calendars:--------------------------------------------
	protected Calendar myCalendar;
	protected static Calendar bufferCalendar;
	private static Calendar secondaryBuffer;
	protected GestureDetector mDetector;
	//------------------------------Variables Manipulated in Settings----------------------
	boolean mShowWeekNumbers;
	boolean mDrawSquares;
	/**
	 * @author ajive_000
	 * @param context Populated By Android System on Instantiation. In this case Context refers to CalActivity.
	 * @param attrs Populated By Android System on Instantiation. Contains Attributes specified in XML files.
	 */
	private class SelectedBox{
		Calendar date;
		int index;
	}
	public CalendarV(Context context, AttributeSet attrs){
		super(context, attrs);

		SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(context);
		this.EVENT_INDICATOR_SHAPE=pref.getString(getResources().getString(R.string.Event_Indicator_Shapes), SettingsFragment.EVENT_SHAPES_CIRCLE);
		this.CURRENT_DAY_INDICATOR_SHAPE=pref.getString(getResources().getString(R.string.Current_Day_Indicator_Setting), SettingsFragment.CURRENT_DAY_INDICATOR_CIRCLE);
		this.ROUND_DETAIL_EVENT_CORNERS=pref.getBoolean(getResources().getString(R.string.Detailed_Events_Shape), false);

		String filename=pref.getString(getResources().getString(R.string.Currently_Selected_Theme), "");
		File file=new File(context.getFilesDir(),"Styles");
		this.selected_Calendar_Colors=new ArrayList<Integer>();
		this.selected_Calendar_Colors.add(0xff000000);
		if(file.isDirectory()){
			File file2=new File(file, filename);
			if(file2.isFile())
				this.selected_Calendar_Colors=CalendarStyles.readCalendarColors(file2);
		}
		bufferRect=new RectF();
		TypedArray a=context.getTheme().obtainStyledAttributes(attrs, R.styleable.CalendarV, 0, 0);
		try{
			mShowWeekNumbers=false;
		}
		finally{
			a.recycle(); 
		}
		initRect();
		BOX_HEIGHT=30*this.getResources().getDisplayMetrics().density;
		initPaint();
		initCal();
		mDetector=new GestureDetector(this.getContext(),new GestureListener(this));
	}
	/**
	 * @author ajive_000
	 * Public function that allows week numbers to be shown along the side of the Calendar
	 * @param val
	 */
	public void setShowWeekNumbers(boolean val)
	{
		mShowWeekNumbers=val;
		invalidate();
		requestLayout();
	}
	/**
	 * @author ajive_000
	 * Initializes Calendar Objects and Recreates Primary Calendar.
	 */
	public void initCal(){
		if(secondaryBuffer!=null){
			myCalendar=secondaryBuffer;
		}
		else if(bufferCalendar==null)
		{
			myCalendar=GregorianCalendar.getInstance();
		}
		else{
			myCalendar=new GregorianCalendar();
			myCalendar.set(bufferCalendar.get(Calendar.YEAR), bufferCalendar.get(Calendar.MONTH), bufferCalendar.get(Calendar.DAY_OF_MONTH));
		}
		bufferCalendar=new GregorianCalendar(myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
		secondaryBuffer=new GregorianCalendar(myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH));
		setCurrentDay(false);

	}
	/**
	 * Wrapper function to be called by API
	 */
	public void setCurrentDay()
	{
		setCurrentDay(true);
	}
	/**
	 * Sets the selected day to the current day. 
	 * @param a --If true, invalidates and redraws.
	 */
	private void setCurrentDay(boolean a)
	{
		int day=myCalendar.get(Calendar.DAY_OF_MONTH);
		myCalendar.set(Calendar.DAY_OF_MONTH, 1);
		if(selectedBox==null){
			selectedBox=new SelectedBox();
			selectedBox.date=Calendar.getInstance();
			selectedBox.date.set(Calendar.HOUR_OF_DAY, 0);
			selectedBox.index=day+myCalendar.get(Calendar.DAY_OF_WEEK)-2;
		}
		myCalendar.set(Calendar.DAY_OF_MONTH, day);
		if(a)
		{
			invalidate();
			requestLayout();
		}
	}
	/**
	 * Allows the Calendar to be set to a certain date.
	 * @param c
	 */
	public void setCal(Calendar c){
		myCalendar=c;
		invalidate();
		requestLayout();
	}
	/**
	 * Called at the beginning of creation and when the screen is rotated.
	 * It gives the widths and heights that the view has to work with,
	 * and fits the view to the views given size.
	 * @param w -- the current width of the view
	 * @param h -- the current height of the view
	 * @param oldw -- the old width of the view (not used)
	 * @param oldh -- the old height of the view (not used)
	 * @note Should not be called from API, should only be used by Android system
	 */
	public void onSizeChanged(int w, int h, int oldw, int oldh){
		float monthLabelH=0;
		myMonthLabel.set(0, 0, w, monthLabelH);
		myWeekNumbers[0]=myDayLabels[0];
		float dayLabelH=40;
		float weekLabelW;
		if(mShowWeekNumbers)
			weekLabelW=50;
		else
			weekLabelW=0;
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
		preAllocSquares();
	}
	/**
	 * Android prefers preallocation of data during the draw sequence. 
	 * Preallocates and prepares squares for use.
	 * 
	 */
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
		addEventsToDays(0, mySquares.length);
		calibrateSelectedDay();
	}
	private void calibrateSelectedDay()
	{
		selectedBox.index=0;
		long day0=mySquares[0].getCal().getTimeInMillis();
		long myDay=selectedBox.date.getTimeInMillis();
		selectedBox.index+=(myDay-day0)/86400000;
	}
	/**
	 * Adds events from events array into their square.
	 * @param index
	 * @param end
	 */
	private void addEventsToDays(int index, int end)
	{

		int m=0;
		int j=0;
		for(int i=index; i<end; i++)
		{
			mySquares[i].getEvents().clear();
		}
		for(int i=index; i<end; i++){

			for(j=m; j<mEvents.size();j++)
			{
				Event event=mEvents.get(j);
				Calendar c=event.getStartDate();
				if(c.get(Calendar.YEAR)>mySquares[i].getYear() || 
						(c.get(Calendar.YEAR)==mySquares[i].getYear() && c.get(Calendar.MONTH)>mySquares[i].getMonth()) || 
						(c.get(Calendar.YEAR)==mySquares[i].getYear() && c.get(Calendar.MONTH)==mySquares[i].getMonth() && c.get(Calendar.DATE)>mySquares[i].getDay()))
				{
					m=j;
					break;
				}

				else if(c.get(Calendar.DATE)==mySquares[i].getDay()
						&& c.get(Calendar.MONTH)==mySquares[i].getMonth()
						&& c.get(Calendar.YEAR)==mySquares[i].getYear())
					mySquares[i].getEvents().add(event);

			}
			m=j;
		}
	}
	/**
	 * @author ajive_000
	 * Initializes space for Rectangle objects.
	 */
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
		for(int i=0; i<myDetailedEvents.length;i++)
		{
			myDetailedEvents[i]=new RectF();
		}
		myMonthLabel=new CalRect();
	}
	/**
	 * @author ajive_000
	 * Initializes multiple paint objects and populates their settings
	 */
	public void initPaint(){
		textPainter=new Paint(Paint.ANTI_ALIAS_FLAG);
		linePainter=new Paint(Paint.ANTI_ALIAS_FLAG);
		boxPainter=new Paint(0);
		ovalPainter=new Paint(Paint.ANTI_ALIAS_FLAG);
	}
	/**
	 * @author ajive_000
	 * Wrapper function to draw a box with the selected color.
	 * @param box
	 * @param canvas
	 * @param color
	 */
	public void drawBox(RectF box, Canvas canvas, int color){
		int prevColor=boxPainter.getColor();
		boxPainter.setColor(color);
		canvas.drawRect(box, boxPainter);
		boxPainter.setColor(prevColor);
	}
	/**
	 * @author ajive_000
	 * Wrapper function to draw a box with the selected color and the desired Paint object.
	 * @param box -- The coordinates that will be drawn
	 * @param canvas -- The canvas supplied by the Android system
	 * @param color -- A color specified by the Colors.xml
	 * @param p -- Paint object used to paint the box.
	 */
	public void drawBox(RectF box, Canvas canvas, int color, Paint p){
		int prevColor=p.getColor();
		p.setColor(color);
		canvas.drawRect(box, p);
		p.setColor(prevColor);
	}
	/**
	 * @author ajive_000
	 * Wrapper function used to draw text
	 * @param box -- Coordinates that will be drawn
	 * @param canvas -- The Canvas object supplied by the Android System
	 * @param message -- The String message that will be drawn
	 * @param color -- The color of the text drawn
	 * @param $textSize -- The text size (in dp) to be drawn
	 */
	public void drawText(RectF box, Canvas canvas, int color, String message, float $textSize)
	{
		drawText(box,canvas,message,color,$textSize);
	}
	public void drawText(RectF box, Canvas canvas, String message, int color, float $textSize)
	{
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
	/**
	 * @author ajive_000
	 * Helper function to get recommended text size for certain boxes.
	 * @param box -- The box where the text will be drawn.
	 * @return Returns the recommended text size
	 */
	public float getRecommendedSize(RectF box){
		return ((float)(Math.sqrt(box.width()*box.height())/7)*2);
	}
	/**
	 * @author ajive_000
	 * Helper function to get recommended text size for certain boxes.
	 * @param box -- The box where the text will be drawn.
	 * @param factor -- A factor to multiply the text size by.
	 * @return
	 */
	public float getRecommendedSize(RectF box, float factor){
		return ((float)(Math.sqrt(box.width()*box.height()))*factor);
	}
	/**
	 * @author ajive_000
	 * Draws the month Label in the ActionBar
	 * 
	 */
	private void drawMonthLabel(){
		alignX=ALIGN_CENTER;
		alignY=ALIGN_CENTER;
		TextView myText=(TextView)((CalActivity)getContext()).findViewById(R.id.CalendarTitle);
		myText.setTextSize(30);
		myText.setText(""+monthNames[myCalendar.get(Calendar.MONTH)]+" - "+myCalendar.get(Calendar.YEAR));
	}
	/**
	 * @author ajive_000
	 *  Draws labels for the Day titles, ie. Sun, Mon, Tue, Wed, Thurs, Fri, Sat
	 * @param canvas -- Canvas object supplied by the Android System.
	 */
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
				drawText(myDayLabels[i],canvas,getResources().getColor(R.color.TextColor),myDayLabelsNames[i].substring(0,3),(3*myDayLabels[i].height()/4));
		}
	}
	/**
	 * @author ajive_000
	 * Draws the week numbers alongside the weeks in the left hand collumn.
	 * @note -- Only called if mShowWeekLabels is set to true.
	 * @param canvas -- Canvas object supplied by the Android System.
	 */
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
			drawText(myWeekNumbers[i],canvas,""+(weekno++),getResources().getColor(R.color.TextColor),this.getRecommendedSize(myWeekNumbers[i], 2f/7f));
	}
	/**
	 * 
	 */
	public void reDrawEvents()
	{
		this.preAllocSquares();
	}
	/**
	 * @author ajive_000
	 * Draws the squares as they were allocated. 
	 * @param canvas
	 */
	private void drawSquares(Canvas canvas){
		//----Draw Backgrounds---------------------
		for(int i=0; i<mySquares.length; i++)
		{
			drawBox(mySquares[i],canvas, mySquares[i].getColor());
		}
		drawSelectedSquare(canvas);
		alignX=ALIGN_LEFT;
		alignY=ALIGN_TOP;
		for(int i=0; i<mySquares.length; i++)
		{
			if(myCalendar.get(Calendar.MONTH)==((CalRect)mySquares[i]).getMonth())
				drawText(mySquares[i],canvas,""+((CalRect)(mySquares[i])).getDay(),getResources().getColor(R.color.TextColor),this.getRecommendedSize(mySquares[i],2f/7f));
			else
				drawText(mySquares[i],canvas,""+((CalRect)(mySquares[i])).getDay(),getResources().getColor(R.color.AlternateTextColor),this.getRecommendedSize(mySquares[i], 2f/7f));
		} 
	} 
	/**
	 * @author ajive_000
	 * Draws the square selected by the user a different color. By default it is the current day.
	 * @param c -- Canvas supplied by the Android System.
	 */
	public void drawSelectedSquare(Canvas c){
		if(selectedBox.index>=0 && selectedBox.index<=48)
			drawBox(mySquares[selectedBox.index],c,getResources().getColor(R.color.SelectedColor),boxPainter);
	}
	/**
	 * @author ajive_000
	 * Used to move the squares to directly above the top row. 
	 * All moves are then done with the assumption that the squares are directly below each other
	 */
	private void reReference()
	{
		for(int i=7; i<mySquares.length;i++)
		{
			mySquares[i].offset(0, mySquares[i-7].bottom-mySquares[i].top);
		}
	}
	/**
	 * @author ajive_000
	 * Called by the Android System
	 * @note -- This function should not be called by the API
	 * @param canvas -- Canvas object supplied by the Android System. Used for drawing to the screen
	 */
	public void onDraw(Canvas canvas){
		reReference();
		translate(translationFactor, 0, mySquares.length);

		if(checkForEvents())
		{
			calcDetailedEvents(mySquares[selectedBox.index].bottom);
			drawDetailedEvents(canvas);
			translate(calcTransDistance(),nextLine(selectedBox.index),mySquares.length);
		}
		boolean first=isFirstLayerVisible();
		boolean second=isUpperLayerShowing();
		if(!first)
		{
			moveLayerUp();
		}
		if(second)
			moveLayerDown();
		drawSquares(canvas);
		drawDecals(canvas);
		drawMonthLabel();
		drawDayLabels(canvas);
		if(mShowWeekNumbers)
			drawWeekLabels(canvas);

	}
	private float calcTransDistance()
	{

		int transDistance=mySquares[selectedBox.index].getEvents().size();
		if(transDistance>4) transDistance=4;
		return ((transDistance+1)/2)*BOX_HEIGHT;
	}
	/**
	 * @author ajive_000
	 * Draws certain details and handles when the details are drawn. Ex. The current day bubble.
	 * @param canvas
	 * 
	 * @todo -- Need to update to draw the bubbles for all days with Events in them.
	 */
	public void drawDecals(Canvas canvas)
	{
		//-----------------------Determines which Current Day Indicator to draw, then draws it---------
		if(this.CURRENT_DAY_INDICATOR_SHAPE.equals(SettingsFragment.CURRENT_DAY_INDICATOR_CIRCLE))
			drawCurrentDayDecalCircle(canvas);
		else if(this.CURRENT_DAY_INDICATOR_SHAPE.equals(SettingsFragment.CURRENT_DAY_INDICATOR_SQUARE))
			drawCurrentDayDecalSquare(canvas);
		else if(this.CURRENT_DAY_INDICATOR_SHAPE.equals(SettingsFragment.CURRENT_DAY_INDICATOR_BOX))
			drawCurrentDayDecalBox(canvas);
		else
			drawCurrentDayDecalCircle(canvas);

		//-----------------------Determines which Event shape to draw, then draws it--------------------
		if(this.getHeight()>this.getWidth()){
			if(this.EVENT_INDICATOR_SHAPE.equals(SettingsFragment.EVENT_SHAPES_CIRCLE))
				drawEventCirclesVertical(canvas);
			else if(this.EVENT_INDICATOR_SHAPE.equals(SettingsFragment.EVENT_SHAPES_SQUARE))
				drawEventSquares(canvas);
			else
				drawEventCirclesVertical(canvas);
		}
		else
		{
			drawEventSquares(canvas);
		}

	}
	private void drawEventSquares(Canvas canvas)
	{
		for(int i=0; i<mySquares.length; i++)
		{
			int variance=11;

			float topX=mySquares[i].right;
			float topY=mySquares[i].top;
			float height=mySquares[i].height();
			topX-=2*this.getContext().getResources().getDisplayMetrics().density;
			topY+=2*this.getContext().getResources().getDisplayMetrics().density;
			topX-=height/variance;
			int numMyEvents=mySquares[i].getEvents().size();
			for(int j=0; j<numMyEvents && j<4; j++)
			{
				if(!(j==3 && mySquares[i].getEvents().size()>4))
				{
					bufferRect.set(topX,topY,topX+height/variance,topY+height/variance);
					int color=this.selected_Calendar_Colors.get(mySquares[i].getEvents().get(j).getColorNumber()%selected_Calendar_Colors.size());
					boxPainter.setColor(color);
					topY+=height/variance+2;
					canvas.drawRect(bufferRect, boxPainter);
				}
			}
			if(numMyEvents>4)
			{
				int num=2;
				int pow=1;

				topX=bufferRect.centerX();
				for(int j=3; j<6 && j<numMyEvents; j++)
				{	

					float offset=(height/(variance+(int)Math.pow(num, pow))/2);

					bufferRect.set(topX-offset, topY, topX+offset, topY+2*offset);
					this.boxPainter.setColor(selected_Calendar_Colors.get(mySquares[i].getEvents().get(j).getColorNumber()%selected_Calendar_Colors.size()));
					topY+=2*offset+1;
					pow++;
					if(j==5 || j==numMyEvents-1){
						ovalPainter.setColor(selected_Calendar_Colors.get(mySquares[i].getEvents().get(j).getColorNumber()%selected_Calendar_Colors.size()));
						canvas.drawOval(bufferRect, ovalPainter);
					}
					else
						canvas.drawRect(bufferRect, boxPainter);
				}
			}
		}
	}
	private void drawEventCirclesVertical(Canvas c)
	{
		for(int i=0; i<mySquares.length; i++)
		{
			float topX=mySquares[i].right;
			float topY=mySquares[i].top;
			float height=mySquares[i].height();
			topX-=height/16;
			topY+=height/16;
			int num=mySquares[i].getEvents().size();
			for(int j=0; j<num && j<4; j++)
			{
				if(!(j==3 && num>4)){
					bufferRect.set(topX-height/9, topY, topX, topY+height/9);
					ovalPainter.setColor(this.selected_Calendar_Colors.get(mySquares[i].getEvents().get(j).getColorNumber()%
							this.selected_Calendar_Colors.size()));
					c.drawOval(bufferRect, ovalPainter);
					topY+=height/8;
				}
			}
			if(num>4)
			{

				topX-=height/18;
				float factor=30;
				for(int j=3; j<=num-1 && j<6; j++){
					float left=topX-height/factor;
					float right=topX+height/factor;
					bufferRect.set(left,topY,right,topY+height/(factor/2));
					ovalPainter.setColor(this.selected_Calendar_Colors.get(mySquares[i].getEvents().get(j).getColorNumber()%
							this.selected_Calendar_Colors.size()));
					c.drawOval(bufferRect, ovalPainter);
					topY+=height/(factor/2-3);
					factor=factor/2*3;
				}
			}
		}
	}
	private void drawCurrentDayDecalBox(Canvas canvas){
		int index=-1;
		for(int i=0; i<mySquares.length; i++)
			if(((CalRect)mySquares[i]).getDay()==Calendar.getInstance().get(Calendar.DAY_OF_MONTH) && ((CalRect)mySquares[i]).getMonth()==Calendar.getInstance().get(Calendar.MONTH) && ((CalRect)mySquares[i]).getYear()==Calendar.getInstance().get(Calendar.YEAR))
				index=i;
		if(index>=0 && index<mySquares.length)
		{
			CalRect rect=mySquares[index];
			float bottom=rect.bottom;
			float top=rect.top;
			float left=rect.left;
			float right=rect.right;
			linePainter.setStyle(Paint.Style.STROKE);
			linePainter.setColor(getResources().getColor(R.color.CurrentDayColor));
			linePainter.setStrokeWidth(2*this.getContext().getResources().getDisplayMetrics().density);
			canvas.drawLine(left, bottom, right, bottom, linePainter);
			canvas.drawLine(left,top,right,top,linePainter);
			canvas.drawLine(left, bottom, left, top, linePainter);
			canvas.drawLine(right, bottom, right, top, linePainter);
		}
	}
	private void drawCurrentDayDecalSquare(Canvas canvas)
	{
		int index=-1;
		for(int i=0; i<mySquares.length; i++)
			if(((CalRect)mySquares[i]).getDay()==Calendar.getInstance().get(Calendar.DAY_OF_MONTH) && ((CalRect)mySquares[i]).getMonth()==Calendar.getInstance().get(Calendar.MONTH) && ((CalRect)mySquares[i]).getYear()==Calendar.getInstance().get(Calendar.YEAR))
				index=i;
		if(index!=-1){
			bufferRect.set(mySquares[index].centerX()-10, mySquares[index].bottom-30, mySquares[index].centerX()+10, mySquares[index].bottom-10);
			int prevColor=textPainter.getColor();
			if(selectedBox.index!=index)
				textPainter.setColor(getResources().getColor(R.color.CurrentDayColor));
			else
				textPainter.setColor(getResources().getColor(R.color.White));
			//if(bufferRect.top>myDayLabels[0].bottom)
			canvas.drawRect(bufferRect, textPainter);
			textPainter.setColor(prevColor);
		}
	}

	/**
	 * @author ajive_000
	 * Draws the current day bubble.
	 * @param canvas -- Canvas object as supplied by the Android System.
	 */
	private void drawCurrentDayDecalCircle(Canvas canvas)
	{
		int index=-1;
		for(int i=0; i<mySquares.length; i++)
			if(((CalRect)mySquares[i]).getDay()==Calendar.getInstance().get(Calendar.DAY_OF_MONTH) && ((CalRect)mySquares[i]).getMonth()==Calendar.getInstance().get(Calendar.MONTH) && ((CalRect)mySquares[i]).getYear()==Calendar.getInstance().get(Calendar.YEAR))
				index=i;
		if(index!=-1){
			bufferRect.set(mySquares[index].centerX()-10, mySquares[index].bottom-30, mySquares[index].centerX()+10, mySquares[index].bottom-10);
			int prevColor=textPainter.getColor();
			if(selectedBox.index!=index)
				textPainter.setColor(getResources().getColor(R.color.CurrentDayColor));
			else
				textPainter.setColor(getResources().getColor(R.color.White));
			//if(bufferRect.top>myDayLabels[0].bottom)
			canvas.drawOval(bufferRect, textPainter);
			textPainter.setColor(prevColor);
		}
	}
	/**
	 * @author ajive_000
	 * Called when the screen is touched.
	 *
	 */
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
	/**
	 * @author ajive_000
	 * Selects the box that the user touches
	 * @param x -- the x-coordinate where the screen was touched.
	 * @param y -- the y-coordinate where the screen was touched.
	 * @TODO Get rid of indexes altogether and use the events in the CalRects
	 */
	public void selectDate(float x, float y)
	{
		for(int i=0; i<mySquares.length;i++)
		{
			if(mySquares[i].contains(x, y))
			{
				selectedBox.index=i;
				selectedBox.date=mySquares[selectedBox.index].getCal();
				invalidate();
				requestLayout();
				return;
			}
		}
		if(checkForEvents()){
			ArrayList<Event> events=mySquares[selectedBox.index].getEvents();
			for(int i=0; i<mySquares[selectedBox.index].getEvents().size() && i<myDetailedEvents.length;i++)
			{
				if(myDetailedEvents[i].contains(x,y))
				{
					if(i==3 && mySquares[selectedBox.index].getEvents().size()>4)
					{
						Intent intent=new Intent(getContext(), ListEventsActivity.class);
						ListEventsActivity.putEvents(mySquares[selectedBox.index].getEvents());
						ListEventsActivity.putCalendar(mySquares[selectedBox.index].getCal());
						getContext().startActivity(intent);

						return;
					}
					else
					{
						Intent intent=new Intent(this.getContext(), EventActivity.class);
						intent.putExtra(EventActivity.TITLE, events.get(i).getTitle());
						intent.putExtra(EventActivity.DESCRIPTION,events.get(i).getSummary());
						intent.putExtra(EventActivity.STARTDATE, events.get(i).getStartDate().getTimeInMillis());
						intent.putExtra(EventActivity.ENDDATE, events.get(i).getEndDate().getTimeInMillis());
						intent.putExtra(EventActivity.COLOR, this.selected_Calendar_Colors.get(events.get(i).getColorNumber()%
								this.selected_Calendar_Colors.size()));
						this.getContext().startActivity(intent);
						//Log.v("Button Press:",mEvents.get(i).getTitle());
						return;
					}
				}
			}
		}
	}
	/**
	 * @author ajive_000
	 * @param i
	 * 
	 * @todo -- Implement
	 */
	public void fade(int i)
	{
		/*	ObjectAnimator mFadeAnimator = ObjectAnimator.ofInt(this, "AlphaLevel", getResources().getColor(R.color.SelectedColor));
		mFadeAnimator.setIntValues(getResources().getColor(R.color.SchoolColor1));
		mFadeAnimator.setDuration(1000);
		mFadeAnimator.start();*/
	}
	/**
	 * @author ajive_000
	 * Called when the user swipes right to left -- Jumps to the next month
	 * @param prealloc
	 * @deprecated
	 */
	public void nextMonth(boolean prealloc){
		myCalendar.set(Calendar.MONTH, myCalendar.get(Calendar.MONTH)+1);
		secondaryBuffer=new GregorianCalendar(myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH));
		if(prealloc)
			preAllocSquares();
		invalidate();
		requestLayout();
	}
	/**
	 * @author ajive_000
	 * Called when the user swipes left to right -- Jumps to the previous month
	 * @param prealloc
	 * @deprecated Will use Year view instead.
	 */
	public void previousMonth(boolean prealloc){
		myCalendar.set(Calendar.MONTH, myCalendar.get(Calendar.MONTH)-1);
		secondaryBuffer=new GregorianCalendar(myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH));
		if(prealloc)
			preAllocSquares();
		invalidate();
		requestLayout();
	}
	/**
	 * @author ajive_000
	 * Helper function for finding the first index of the next line
	 * @param i
	 * @return -- the value of the first index on the next line
	 */
	public int nextLine(int i){
		return i/7*7+7;
	}
	/**
	 * 
	 * Used to move a square downwards before the draw phase.
	 * @param distanceY -- positive distance moves the box downwards
	 * @param index -- The starting index of the set of boxes to move -- inclusive [index, end)
	 * @param end -- The ending index of the set of boxes to move -- exclusive
	 */
	public void translate(float distanceY, int index, int end){
		for(int i=index; i<end; i++)
		{
			mySquares[i].offset(0, distanceY);;
		}
	}
	/**
	 * @author ajive_000
	 * Will be used to create a smooth transition for boxes to 'pop' in and out
	 * @param f
	 * @todo -- Implement
	 */
	public void setTranslationFactor(float f)
	{
		translationFactor=f;
		invalidate();
		requestLayout();
	}
	/**
	 * @author ajive_000
	 * @return True if the top layer of boxes is even somewhat visible on the screen, false otherwise
	 */
	private boolean isFirstLayerVisible()
	{
		if(checkForEvents() && selectedBox.index>=0 && selectedBox.index<7){
			if(mySquares[selectedBox.index].getEvents().size()<=4)
			{
				return myDetailedEvents[mySquares[selectedBox.index].getEvents().size()-1].bottom>myDayLabels[0].bottom;
			}
			else{
				return myDetailedEvents[3].bottom>myDayLabels[0].bottom;
			}
		}
		else
			return mySquares[0].bottom>myDayLabels[0].bottom;
	}
	/**
	 * @author ajive_000
	 * @return True if the top of the uppermost layer is visible
	 */
	private boolean isUpperLayerShowing()
	{
		return (mySquares[0].top>myDayLabels[0].bottom);
	}
	/**
	 * 
	 */
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
		this.addEventsToDays(42, mySquares.length);
		myCalendar.set(Calendar.MONTH, ((CalRect)mySquares[20]).getMonth());
		myCalendar.set(Calendar.YEAR, ((CalRect)mySquares[20]).getYear());
		secondaryBuffer=new GregorianCalendar(myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH));
		selectedBox.index-=7;
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
		this.addEventsToDays(0, 7);
		myCalendar.set(Calendar.MONTH, ((CalRect)mySquares[20]).getMonth());
		myCalendar.set(Calendar.YEAR, ((CalRect)mySquares[20]).getYear());
		secondaryBuffer=new GregorianCalendar(myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH));
		selectedBox.index+=7;
		if(checkForEvents() &&selectedBox.index<7 && selectedBox.index>=0)
		{

			compensateForShiftDown();
		}
	} 
	private void compensateForShiftDown()
	{
		calcDetailedEvents(mySquares[selectedBox.index].bottom);
		if(mEvents.size()>2)
		{
			translate(-(4+myDetailedEvents[2].bottom-myDetailedEvents[0].top),0,7);
		}
		else if(mEvents.size()>0)
		{
			translate(-(4+myDetailedEvents[0].bottom-myDetailedEvents[0].top),0,7);
		}
		else{}
	}
	public void addEvents(ArrayList<Event> events)
	{
		if(mEvents == null)
			mEvents = events;
		else
			mEvents.addAll(events);
		MergeSort.sortEvents(mEvents);
		addEventsToDays(0, mySquares.length);
		invalidate();
		requestLayout();
	}
	public ArrayList<Event> getEvents()
	{
		return mEvents;
	}
	public void addEvents(Event event)
	{
		if(mEvents==null)
		{
			mEvents=new ArrayList<Event>();
			mEvents.add(event);
		}
		else
			mEvents.add(event);
		MergeSort.sortEvents(mEvents);
		addEventsToDays(0,mySquares.length);
		invalidate();
		requestLayout();

	}
	/**
	 * @author ajive_000
	 * Populates the index array with indexes Events in the Events array
	 * @return True if there are Events on the current day
	 */
	public boolean checkForEvents()
	{
		return selectedBox.index<mySquares.length && selectedBox.index>=0 && mySquares[selectedBox.index].getEvents().size()>0;
	}
	public void calcDetailedEvents(float positionY){
		float boxHeight=BOX_HEIGHT;
		alignX=ALIGN_CENTER;
		alignY=ALIGN_CENTER;
		ArrayList<Event> events=mySquares[selectedBox.index].getEvents();
		for(int i=0; i<events.size() && i<4; i++)
		{
			myDetailedEvents[i].set((i%2)*getWidth()/2, mySquares[selectedBox.index].bottom+(i/2)*boxHeight, (1+(i%2))*getWidth()/2, mySquares[selectedBox.index].bottom+(1+(i/2))*boxHeight);
		}
		if(events.size()<3)
		{
			for(int i=2; i<myDetailedEvents.length; i++)
				myDetailedEvents[i].set(myDetailedEvents[i-2]);
		}
		for(int i=0; i<events.size() && i<myDetailedEvents.length; i++)
		{
			myDetailedEvents[i].inset(2, 2);
		}
	}
	public void drawDetailedEvents(Canvas canvas){
		adjustToCorrectTextSize(myDetailedEvents[0]);
		ArrayList<Event> events=mySquares[selectedBox.index].getEvents();
		for(int i=0; i<myDetailedEvents.length && i<events.size(); i++)
		{
			int color=this.selected_Calendar_Colors.get(events.get(i).getColorNumber()%
					this.selected_Calendar_Colors.size());
			boxPainter.setColor(color);
			if(this.ROUND_DETAIL_EVENT_CORNERS)
				canvas.drawRoundRect(myDetailedEvents[i], 15, 15, boxPainter);
			else
				canvas.drawRect(myDetailedEvents[i], boxPainter);
			String s=events.get(i).getTitle();
			if(textPainter.measureText(s)>myDetailedEvents[i].width()-70)
				s=cutString(s, myDetailedEvents[i].width());
			if(events.size()>4 && i==3)
				drawText(myDetailedEvents[i],canvas,"More...",getResources().getColor(R.color.DetailedEventColor),textPainter.getTextSize());
			else
				drawText(myDetailedEvents[i],canvas,s,getResources().getColor(R.color.DetailedEventColor),textPainter.getTextSize());
		}
	}
	/**
	 * Adjusts the size of the text in the boxes to the height of the box
	 * @param box
	 */
	private void adjustToCorrectTextSize(RectF box)
	{
		float maxSize;
		maxSize=box.bottom-box.top-10;
		if(maxSize<0)
			return;
		else
		{
			while(textPainter.descent()-textPainter.ascent()>maxSize)
			{
				textPainter.setTextSize(textPainter.getTextSize()-1);
			}
			while(textPainter.descent()-textPainter.ascent()<maxSize)
			{
				textPainter.setTextSize(textPainter.getTextSize()+1);
			}
		}
	}
	/**
	 * Cuts string to an appropriate length given the width of a box
	 * @param s the string to cut
	 * @param width the width of a box that the string will fit in
	 * @return returns the cut string ready to be painted inside the box
	 */
	private String cutString(String s, float width)
	{ 
		while(textPainter.measureText(s)>width)
		{
			s=s.substring(0, s.length()-1);
		}
		s=s.substring(0, s.length()-3)+"...";
		return s;
	}
}
