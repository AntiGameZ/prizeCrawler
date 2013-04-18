package com.ruyicai.prizecrawler.enums;

public enum CheckStateType {

	
	WAIT(0, "未审核"),
	PASS(1, "审核为成功"),
	NOTPASS(2, "审核为失败"),
	CREATE(3, "创建");
	
	
	public int value;
	
	public String memo;
	
	CheckStateType(int value, String memo) {
		
		this.value = value;
		this.memo = memo;
	}
}
