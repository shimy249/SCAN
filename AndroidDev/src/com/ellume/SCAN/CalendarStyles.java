package com.ellume.SCAN;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.ellume.SCAN.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public abstract class CalendarStyles {
	public static final String STYLES_FOLDER="Styles";
	public static void createStylesFolder(Context c)
	{
		File file=new File(c.getFilesDir(),STYLES_FOLDER);
		file.mkdir();
	}
	public static ArrayList<Integer> readCalendarColors(File file)
	{
		ArrayList<Integer> colors=new ArrayList<Integer>();
		try {
			FileInputStream stream=new FileInputStream(file);
			
			int i;
			do{
				byte[] buffer=new byte[4];
				i=stream.read(buffer);
				if(i!=-1)
					colors.add(toInt(buffer));
			}while(i!=-1);
			stream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			
			return null;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return colors;
	}
	public static void setCalendarColors(ArrayList<Integer> colors, File file) 
	{
		try {
			FileOutputStream outputStream=new FileOutputStream(file);
			for(int i: colors)
				outputStream.write(toBytes(i));
			outputStream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void setCalendarColors(int[] colors, File file) 
	{
		try {
			FileOutputStream outputStream=new FileOutputStream(file);
			for(int i: colors)
				outputStream.write(toBytes(i));
			outputStream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static byte[] toBytes(int i)
	{
		return new byte[] {
				(byte) ((i >> 24) & 0xFF),
				(byte) ((i >> 16) & 0xFF),   
				(byte) ((i >> 8) & 0xFF),   
				(byte) (i & 0xFF)
		};
	}
	private static int toInt(byte[] b)
	{
		return   b[3] & 0xFF |
				(b[2] & 0xFF) << 8 |
				(b[1] & 0xFF) << 16 |
				(b[0] & 0xFF) << 24;
	}
	public static File[] getCalendarStyles(Context context)
	{
		File file=new File(context.getFilesDir(),CalendarStyles.STYLES_FOLDER);
		return file.listFiles();
	}
	public static File getCurrentStyleFile(Context context)
	{	
		File f1=null;
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(context);
		File f=new File(context.getFilesDir(),"Styles");
		if(f.isDirectory())
		{
			f1=new File(f,
					prefs.getString(context.getResources().getString(R.string.Currently_Selected_Theme), ""));
			if(f1.isFile())
				return f1;
		}
		return f1;
	}
	public static ArrayList<Integer> getCurrentStylesArray(Context c)
	{
		return CalendarStyles.readCalendarColors(CalendarStyles.getCurrentStyleFile(c));
	}
}
