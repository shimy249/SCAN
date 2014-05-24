package com.example.androiddev;

import android.animation.ValueAnimator;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Scroller;

public class GestureListener extends GestureDetector.SimpleOnGestureListener{
	private static String TAG="com.example.androiddev.GestureListener";
	final Scroller mScroller;
	 ValueAnimator mScrollerAnimator;
	CalendarV myView;
	public GestureListener(CalendarV view)
	{
		super();
		myView=view;
		mScroller=new Scroller(view.getContext());
		
	}
	public boolean onDown(MotionEvent e)
	{
		if(!mScroller.isFinished())
			mScroller.forceFinished(true);
		return true;

	}
	public  boolean onSingleTapConfirmed(MotionEvent e)
	{
		return false;
	}
	public boolean onSingleTapUp(MotionEvent e)
	{
		String s =e.toString();
		Log.v(TAG, "Single Tap Confirmed");
		myView.selectDate(e.getX(), e.getY());
		return true;
	}
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY){
		myView.setTranslationFactor(-distanceY);
		return true;
	}
	public boolean onFling(MotionEvent e1, MotionEvent e2, float dx, float dy){
		if(Math.abs(dx)>4000)
			if(dx<0 && Math.abs(dy)<Math.abs(dx))
			{
				myView.nextMonth(true);
				return true;
			}
			else if(Math.abs(dy)<Math.abs(dx))
			{
				myView.previousMonth(true);
				return true;
			}
		if(Math.abs(dy)>2000 && Math.abs(dy)>Math.abs(dx))
		{
			mScroller.fling(0, 0, (int)dx/4, (int)dy/2, -100, 100, -100000000, 100000000);
			
			myView.postInvalidate();
		
		mScrollerAnimator=ValueAnimator.ofFloat(dy/800,0);
		mScrollerAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			float latestOffset=0;
			public void onAnimationUpdate(ValueAnimator animation) {
				if(!mScroller.isFinished()) {
					mScroller.computeScrollOffset();
					float f =mScroller.getCurrY();
					Log.v(TAG, "Current Offset: "+(f-latestOffset));
					myView.setTranslationFactor(f-latestOffset);
					latestOffset=f;
				}				
				else
				{
					Log.v(TAG,"Scrolling Finished");
					mScrollerAnimator.cancel();
					onScrollFinished();
				}
			}
		});
		mScrollerAnimator.setDuration(10000);
		mScrollerAnimator.start();
		}
		return true;
	}
	public void onScrollFinished()
	{
			return;
	}

}
