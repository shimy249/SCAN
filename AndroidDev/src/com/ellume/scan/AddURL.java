package com.ellume.scan;

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


//AddURL - Class detailing methods to read, write, and analyze 
public class AddURL 
{
	//fileSplit - An array of strings containing one URL per cell; writes to a new cell when it encounters a new line (new URL).
	private String[] fileSplit = this.readFromFile().split("\n");
	
	/*fileCheck - An array of boolean values the same length as the fileSplit array.
	 * 			  Intended for use with the getvalidURL method to determine which cells contain a true or false value. */
	private Boolean[] fileCheck = new Boolean[fileSplit.length];
	
	//getvalidURL - returns an ArrayList of strings, each of which are a URL from the list that has been written to file.
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
	
	/*writeNewURL - Writes a URL and saves it to a file in the default location.
	 * 				Each time this method is called, one URL is written to a line, then it drops down a line for the next URL. */
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
	
	//readFromFile - Returns a string containing all the text content of a file.
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
	
	//clearURL - Deletes all the textual content of a file. Does not delete the file itself.
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
