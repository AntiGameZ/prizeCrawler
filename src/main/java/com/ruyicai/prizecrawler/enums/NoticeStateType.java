package com.ruyicai.prizecrawler.enums;

public enum NoticeStateType {

	SUCCESS(1, "成功"),
	FAIL(-1, "失败"),
	WAIT(0, "等待");
	
	public int value;
	
	public String memo;
	
	NoticeStateType(int value, String memo) {
		
		this.value = value;
		this.memo = memo;
	}
}
