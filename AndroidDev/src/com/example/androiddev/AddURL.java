package com.example.androiddev;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.File;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import android.content.Context;
import android.content.ContextWrapper;



public class AddURL 
{
	private String[] fileSplit = this.readFromFile().split("\n");
	private Boolean[] fileCheck = new Boolean[fileSplit.length];
	
	private ArrayList getvalidURL()
	{
		ArrayList<String> finalURL = new ArrayList();
		for (int i = 0; i<fileSplit.length; i++)
		{
			if (fileCheck[i])
				finalURL.add(fileSplit[i]);
		}
		
		return finalURL;
	}
	
	private void writeNewURL(String URL$)
	{
		try
		{
			OutputStreamWriter outputURL = new OutputStreamWriter(openFileOutput("urlHolder.txt", Context.MODE_APPEND));
			/*openFileOutput is labeled as undefined because it is called from android.content.ContextWrapper.
			*When put into a class that extends activity, this call should work correctly.
			*From here, however, it needs to be called a different way. Do you want to call this method from
			*here or move it to a different class?
			*/
			outputURL.write(URL$ + "\n");
			outputURL.close();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private String readFromFile()
	{
		String ret = "";
		
		try 
		{
			InputStream addURL = openFileInput("urlHolder.txt");
			/*openFileInput is labeled as undefined because it is called from android.content.ContextWrapper.
			*When put into a class that extends activity, this call should work correctly.
			*From here, however, it needs to be called a different way. Do you want to call this method from
			*here or move it to a different class?
			*/
			
			if(addURL != null)
			{
				InputStreamReader pullURL = new InputStreamReader(addURL);
				BufferedReader buffPullURL = new BufferedReader(pullURL);
				String pullString = "";
				StringBuilder buildURL = new StringBuilder();
				
				while ((pullString = buffPullURL.readLine()) != null)
				{
					buildURL.append(pullString).append("\n");
				}
				
				addURL.close();
				ret = buildURL.toString();
				
			}
		}
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		/*
		 * FileNotFoundException is from openFileOutput and openFileInput. Marked as already handled because of the
		 * issue coming from calling this method. See comment under openFileInput or openFileOutput for more info.
		 */
		
		return ret;
		
	}
	
	private void clearURL()
	{
		try {
			OutputStreamWriter clearURL = new OutputStreamWriter(openFileOutput("urlHolder.txt", Context.MODE_PRIVATE));
			/*openFileOutput is labeled as undefined because it is called from android.content.ContextWrapper.
			*When put into a class that extends activity, this call should work correctly.
			*From here, however, it needs to be called a different way. Do you want to call this method from
			*here or move it to a different class?
			*/
			
			clearURL.write("");
			clearURL.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
