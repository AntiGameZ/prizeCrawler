package com.ruyicai.prizecrawler.enums;

public enum NoticeType {

	PRIZECODE("code", "开奖号码"),
	PRIZEINFO("info", "开奖详情");
	
	public String value;
	
	public String memo;
	
	NoticeType(String value, String memo) {
		
		this.value = value;
		this.memo = memo;
	}
}
