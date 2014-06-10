package com.ellume.scan;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class SelectionView extends View implements OnTouchListener{
	private float intervalY; 
	private ArrayList<Integer> mColors;
	private ArrayList<Integer>mSelectedColors;
	private ArrayList<RectF> mColorBoxes;
	private Paint mPaint;
	
	public SelectionView(Context context, AttributeSet attr){
		super(context, attr);
		this.setOnTouchListener(this);
		mColors=new ArrayList<Integer>();
		mSelectedColors=new ArrayList<Integer>();
		mColorBoxes=new ArrayList<RectF>();
		mPaint=new Paint();
	}
	public void addColors(ArrayList<Integer> colors)
	{
		for(int i=0; i<colors.size(); i++)
		{
			if(!mColors.contains(colors.get(i)))
				mColors.add(colors.get(i));
		}
		createBoxes();
		invalidate();
		this.requestLayout();
	}
	private void createBoxes(){
		mColorBoxes.clear();
		for(int i=0; i<mColors.size();i++)
		{
			RectF myRect=new RectF(0,i*intervalY, getWidth(),(i+1)*intervalY);
			mColorBoxes.add(myRect);
		}
	}
	public void onSizeChanged(int w, int h, int oldw, int oldh){
		super.onSizeChanged(w,h,oldw,oldh);
		intervalY=getHeight()/mColors.size();
		createBoxes();
	}
	public void drawBox(RectF box,int color, boolean isSelected, Canvas canvas){
		if(isSelected)
			mPaint.setColor(getResources().getColor(R.color.SelectedColor));
		else
			mPaint.setColor(getResources().getColor(R.color.White));
		canvas.drawRect(box, mPaint);
		mPaint.setColor(color);
		canvas.drawRect((box.left+2.5f),(box.top+2.5f), (box.right-2.5f), (box.bottom-2.5f), mPaint);
	}
	public void onDraw(Canvas canvas)
	{
		for(int i=0; i<mColors.size();i++)
		{
			if(mSelectedColors.contains(mColors.get(i)))
			{
				drawBox(mColorBoxes.get(i),mColors.get(i),true,canvas);
			}
			else
				drawBox(mColorBoxes.get(i),mColors.get(i),false,canvas);
		}
	}
	public boolean onTouch (View v, MotionEvent event) {
		 float x=event.getX();
		 float y=event.getY();
		 if(event.getAction()==MotionEvent.ACTION_DOWN)
		 for(int i=0; i<mColorBoxes.size();i++)
		 {
			 if(mColorBoxes.get(i).contains(x, y) && !mSelectedColors.contains(mColors.get(i)))
			 {
				 mSelectedColors.add(mColors.get(i));
				 this.invalidate((int)(mColorBoxes.get(i).left),(int)mColorBoxes.get(i).top, (int)mColorBoxes.get(i).right, (int)mColorBoxes.get(i).bottom);
				 
				 requestLayout();
				 requestChange();
				 return true;
			 }
			 else
				 if(mColorBoxes.get(i).contains(x, y) && mSelectedColors.contains(mColors.get(i)))
				 {
					 mSelectedColors.remove(mSelectedColors.indexOf(mColors.get(i)));
					 this.invalidate((int)(mColorBoxes.get(i).left),(int)mColorBoxes.get(i).top, (int)mColorBoxes.get(i).right, (int)mColorBoxes.get(i).bottom);
					
					 requestLayout();
					 requestChange();
					 return true;
				 }
			 
		 }
		 return false;
	 }
	public void requestChange()
	{
		NewsActivity host=(NewsActivity)getContext();
		host.getSelectedColors(null);
	}
	public ArrayList<Integer> getSelectedColors()
	{
		return mSelectedColors;
	}
	 
}
