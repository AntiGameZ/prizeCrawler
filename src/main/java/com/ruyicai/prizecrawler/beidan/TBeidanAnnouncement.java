package com.ruyicai.prizecrawler.beidan;

public class TBeidanAnnouncement {

	private int id;
	
	private String content;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public TBeidanAnnouncement(String content) {
		super();
		this.content = content;
	}

	public TBeidanAnnouncement() {
		super();
	}
	
	
	
	
}
