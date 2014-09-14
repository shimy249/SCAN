package com.ellume.SCAN;

import android.content.Context;
import android.content.SharedPreferences;

public class AuthPreferences {
	public final static String ACCOUNT_FILE="com.ellume.petri.WebEventsActivity.account_file";
	public final static String ACCOUNT_NAME="com.ellume.petri.WebEventsActivity.account_name";
	public final static String ACCOUNT_TOKEN="com.ellume.petri.WebEventsActivity.account_token";
	private SharedPreferences prefs;
	public AuthPreferences(Context c){
		prefs=c.getSharedPreferences(ACCOUNT_FILE, Context.MODE_PRIVATE);
		
	}
	public String getAccount()
	{
		return prefs.getString(ACCOUNT_NAME, null);
	}
	public String getToken(){
		return prefs.getString(ACCOUNT_TOKEN, null);
	}
	public void setToken(String token)
	{
		SharedPreferences.Editor edit=prefs.edit();
		edit.putString(ACCOUNT_TOKEN, token);
		edit.commit();
		return;
	}
	public void setAccount(String account_name)
	{
		SharedPreferences.Editor edit=prefs.edit();
		edit.putString(ACCOUNT_NAME, account_name);
		edit.commit();
		return;
	}
}
