package com.ellume.SCAN;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class MainFragment extends Fragment{
	private int smiley;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		smiley = 0;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_main, container, false);
		return view;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		ImageView v = ((ImageView) this.getView().findViewById(R.id.trojanGuy));
		v.setSoundEffectsEnabled(false);
		v.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    	smileyCounter(v);
		    }
		});
		
	}
	
	public void smileyCounter(View view) {
		smiley++;
		if (smiley == 30) {
			ImageView trojan = (ImageView) this.getView().findViewById(R.id.trojanGuy);
			trojan.setImageResource(R.drawable.orhs_drawable_funny);
		}
	}	

}
