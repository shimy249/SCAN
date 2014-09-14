package com.ellume.SCAN;

import java.io.File;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import com.ellume.SCAN.R;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsFragment extends PreferenceFragment {
	//-------------------------------------Current Day Indicators-------------------------------
	private static final String[] CURRENT_DAY_INDICATOR_OPTIONS={"Circle","Square","Boxed Day"};
	public static final String CURRENT_DAY_INDICATOR_CIRCLE=CURRENT_DAY_INDICATOR_OPTIONS[0];
	public static final String CURRENT_DAY_INDICATOR_SQUARE=CURRENT_DAY_INDICATOR_OPTIONS[1];
	public static final String CURRENT_DAY_INDICATOR_BOX=CURRENT_DAY_INDICATOR_OPTIONS[2];
	//------------------------------------Event Shape Indicators-------------------------------
	private static final String[] EVENT_SHAPES_OPTIONS={"Circle","Square"};
	public static final String EVENT_SHAPES_SQUARE=SettingsFragment.EVENT_SHAPES_OPTIONS[1];
	public static final String EVENT_SHAPES_CIRCLE=SettingsFragment.EVENT_SHAPES_OPTIONS[0];
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addPreferencesFromResource(R.xml.pref_general);
		ListPreference themes=(ListPreference)this.findPreference(getResources().getString(R.string.Currently_Selected_Theme));
		setThemePreferenceItems(themes);
	//	setThemeListPref();
		ListPreference current_day_indicator=(ListPreference)this.findPreference(getResources().getString(R.string.Current_Day_Indicator_Setting));
		setCurrentDayItems(current_day_indicator);
		ListPreference event_shapes=(ListPreference)this.findPreference(getResources().getString(R.string.Event_Indicator_Shapes));
		setEventShapesItems(event_shapes);
	}
	private void setEventShapesItems(ListPreference list){
		list.setEntries(SettingsFragment.EVENT_SHAPES_OPTIONS);
		list.setEntryValues(SettingsFragment.EVENT_SHAPES_OPTIONS);
	}
	private void setCurrentDayItems(ListPreference list){
		list.setEntries(SettingsFragment.CURRENT_DAY_INDICATOR_OPTIONS);
		list.setEntryValues(SettingsFragment.CURRENT_DAY_INDICATOR_OPTIONS);
	}
	public void setThemePreferenceItems(ListPreference themes)
	{
		File file=new File(this.getActivity().getFilesDir(),CalendarStyles.STYLES_FOLDER); 
		themes.setEntries(file.list());
		themes.setEntryValues(file.list());
	}
	private void setThemeListPref()
	{
		Preference pref=(Preference)this.findPreference("theme_list_pref");
		pref.getIntent().setClassName(this.getActivity(), ListThemesActivity.class.getName());
	}
}
