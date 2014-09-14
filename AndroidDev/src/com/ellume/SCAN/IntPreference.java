package com.ellume.SCAN;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.support.v4.app.Fragment.SavedState;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;

public class IntPreference extends DialogPreference {
	static NumberPicker myNums;
	int min,max;
	public IntPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setCustomAttrs(context,attrs);
		this.setDialogLayoutResource(R.layout.int_dialog_preference);
		this.setPositiveButtonText(android.R.string.ok);
		this.setNegativeButtonText(android.R.string.cancel);
		
		setDialogIcon(null);
		// TODO Auto-generated constructor stub
	}
	private void setCustomAttrs(Context context,AttributeSet attrs)
	{
		TypedArray a=context.getTheme().obtainStyledAttributes(attrs, R.styleable.IntPreference, 0, 0);
		try{
			min=a.getInt(R.styleable.IntPreference_min, 20);
			max=a.getInt(R.styleable.IntPreference_max, 40);
		}
		catch(Exception e)
		{
			min=20;
			max=40;
		}
	}
	protected Parcelable onSaveInstanceState() {
	    final Parcelable superState = super.onSaveInstanceState();
	    // Check whether this Preference is persistent (continually saved)
	    if (isPersistent()) {
	        // No need to save instance state since it's persistent,
	        // use superclass state
	        return superState;
	    }

	    // Create instance of custom BaseSavedState
	    final SavedState myState = new SavedState(superState);
	    // Set the state's value with the class member that holds current
	    // setting value
	    myState.value = myNums.getValue();
	    return myState;
	}
	protected void onRestoreInstanceState(Parcelable state) {
	    // Check whether we saved the state in onSaveInstanceState
	    if (state == null || !state.getClass().equals(SavedState.class)) {
	        // Didn't save the state, so call superclass
	        super.onRestoreInstanceState(state);
	        return;
	    }

	    // Cast state to custom BaseSavedState and pass to superclass
	    SavedState myState = (SavedState) state;
	    super.onRestoreInstanceState(myState.getSuperState());
	    
	    // Set this Preference's widget to reflect the restored state
	    myNums.setValue(myState.value);
	}
	public IntPreference(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		this.setCustomAttrs(context,attrs);
		this.setDialogLayoutResource(R.layout.int_dialog_preference);
		this.setPositiveButtonText(android.R.string.ok);
		this.setNegativeButtonText(android.R.string.cancel);
		setDialogIcon(null);
		
	}
	protected void onBindDialogView(View v)
	{
		myNums=(NumberPicker)v.findViewById(R.id.number_picker_dialog_int_preference);
		if(myNums!=null){
			
		myNums.setMinValue(min);
		myNums.setMaxValue(max);
		myNums.setWrapSelectorWheel(false);
		int val=this.getPersistedInt(30);
		myNums.setValue(val);
		}
	}
	protected void onSetInitialValue(boolean restorePersistedValue,Object defaultValue)
	{
		if(restorePersistedValue)
		{
			
		}
		else{
			myNums.setValue((Integer)defaultValue);
			persistInt(myNums.getValue());
		}
	}
	
	protected Object onGetDefaultValue(TypedArray a, int index) {
	    return a.getInteger(index, 25);
	}
	protected void onDialogClosed(boolean positiveResult) {
	    if (positiveResult) {
	    	int val=myNums.getValue();
	        persistInt(val);
	    }
	}

	
	
	private static class SavedState extends BaseSavedState {
	    // Member that holds the setting's value
	    // Change this data type to match the type saved by your Preference
	    int value;

	    public SavedState(Parcelable superState) {
	        super(superState);
	    }

	    public SavedState(Parcel source) {
	        super(source);
	        // Get the current preference's value
	        value = source.readInt();  // Change this to read the appropriate data type
	    }

	    @Override
	    public void writeToParcel(Parcel dest, int flags) {
	        super.writeToParcel(dest, flags);
	        // Write the preference's value
	        dest.writeInt(value);  // Change this to write the appropriate data type
	    }

	    // Standard creator object using an instance of this class
	    public static final Parcelable.Creator<SavedState> CREATOR =
	            new Parcelable.Creator<SavedState>() {

	        public SavedState createFromParcel(Parcel in) {
	            return new SavedState(in);
	        }

	        public SavedState[] newArray(int size) {
	            return new SavedState[size];
	        }
	    };
	}
}
