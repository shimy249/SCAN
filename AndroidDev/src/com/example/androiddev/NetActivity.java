package com.example.androiddev;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class NetActivity extends ActionBarActivity {
	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_agreement_main);
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
		mViewPager=(ViewPager)findViewById(R.id.AgreementPage);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setCurrentItem(0);
		this.getSupportActionBar().hide();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.net, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	public void netSigned(View view){

		SharedPreferences myPrefs=this.getSharedPreferences(MainActivity.netFile, 0);
		SharedPreferences.Editor editor=myPrefs.edit();
		editor.putBoolean(MainActivity.netSigned,true);
		editor.commit();
		this.finish();
	}
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			if(position==0)
				return RightsFragment.newInstance();
			else 
				return NetFragment.newInstance();

		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return null;
		}
	}
	public static class NetFragment extends Fragment{
		public NetFragment(){}
		public static NetFragment newInstance()
		{
			NetFragment frag=new NetFragment();
			return frag;
		}
		public View onCreateView(LayoutInflater inflator, ViewGroup container, Bundle savedInstanceState){
			return inflator.inflate(R.layout.activity_net, container, false);
		}
	}
	public static class RightsFragment extends Fragment{
		public RightsFragment(){}
		public static RightsFragment newInstance()
		{
			RightsFragment frag=new RightsFragment();
			return frag;
		}
		public View onCreateView(LayoutInflater inflator, ViewGroup container, Bundle savedInstanceState){
			return inflator.inflate(R.layout.activity_rights_agreement, container, false);
		}
	}
}
