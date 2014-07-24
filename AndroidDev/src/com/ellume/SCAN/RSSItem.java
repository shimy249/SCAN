package com.ellume.scan;

import java.io.Serializable;

public class RSSItem implements Serializable {

	private static final long serialVersionUID = 1L;
	private String title = null;
	private String summary = null;
	private String link = null;
	private String pubDate = null;
	private String image = null;

    public RSSItem(String title, String summary, String link, String pubDate) {
        this.title = title;
        this.summary = summary;
        this.link = link;
        this.pubDate = pubDate;
    }
    
	void setTitle(String title) {
		this.title = title;
	}

	void setSummary(String summary) {
		this.summary = summary;
	}

	void setDate(String pubdate) {
		this.pubDate = pubdate;
	}

	void setImage(String image) {
		this.image = image;
	}
	
	void setLink(String link) {
		this.link = link;
	}

	public String getTitle() {
		return this.title;
	}

	public String getSummary(){
		return this.summary;
	}

	public String getDate() {
		return this.pubDate;
	}

	public String getImage() {
		return this.image;
	}
	
	public String getLink() {
		return this.link;
	}

}


