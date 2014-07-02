package com.ellume.scan;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.ScrollView;
import android.widget.TextView;

public class DetailActivity extends Activity {

	RSSFeed feed;
	TextView title;
	WebView desc;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail);

		// Enable the vertical fading edge (by default it is disabled)
		ScrollView sv = (ScrollView) findViewById(R.id.sv);
		sv.setVerticalFadingEdgeEnabled(true);

		// Get the feed object and the position from the Intent
		feed = (RSSFeed) getIntent().getExtras().get("feed");
		int pos = getIntent().getExtras().getInt("pos");

		// Initialize the views
		title = (TextView) findViewById(R.id.title);
		desc = (WebView) findViewById(R.id.desc);

		// set webview properties
		WebSettings ws = desc.getSettings();
		ws.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		ws.setJavaScriptEnabled(true);
		ws.setBuiltInZoomControls(true);

		// Set the views
		title.setText(feed.getItem(pos).getTitle());
		String link = feed.getItem(pos).getLink();
		desc.loadUrl(link);
//		desc.loadDataWithBaseURL(link, feed
//				.getItem(pos).getSummary(), "text/html", "UTF-8", null);
	}

}

