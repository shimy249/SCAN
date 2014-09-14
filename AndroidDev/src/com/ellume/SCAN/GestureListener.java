package com.ellume.SCAN;

import android.animation.ValueAnimator;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Scroller;

public class GestureListener extends GestureDetector.SimpleOnGestureListener{
	private static String TAG="com.ellume.SCAN.GestureListener";
	final Scroller mScroller;
	ValueAnimator mScrollerAnimator;
	CalendarV myView;
	private boolean act;
	public GestureListener(CalendarV view)
	{
		super();
		myView=view;
		mScroller=new Scroller(view.getContext());
		act=true;
	}
	public GestureListener(CalendarV view, boolean on)
	{
		super();
		myView=view;
		mScroller=new Scroller(view.getContext());
		act=on;
	}
	public boolean onDown(MotionEvent e)
	{
		if(act){
			if(!mScroller.isFinished())
				mScroller.forceFinished(true);
			return true;
		}
		return act;

	}
	public  boolean onSingleTapConfirmed(MotionEvent e)
	{
	
		return false;
	}
	public boolean onSingleTapUp(MotionEvent e)
	{
		if(act){
			myView.selectDate(e.getX(), e.getY());
			myView.setTranslationFactor(0);
			return true;
		}
		return act;
	}
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY){
		if(act){
			myView.setTranslationFactor(-distanceY);
			return true;
		}
		return act;
	}
	public boolean onFling(MotionEvent e1, MotionEvent e2, float dx, float dy){
		if(act){
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
							//Log.e(TAG, "Current Offset: "+(f-latestOffset));
							myView.setTranslationFactor(f-latestOffset);
							latestOffset=f;
						}				
						else
						{
							//Log.v(TAG,"Scrolling Finished");
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
		return act;
	}
	public void onScrollFinished()
	{
		return;
	}

}
