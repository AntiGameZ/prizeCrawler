package com.ruyicai.prizecrawler.consts;

public enum ErrorCode {
	
	OK("0", "正确"), ERROR("500", "错误"),PRIZEINFONULL("100001","开奖为空"),
	
	RESULT_NOT_EXISTS("100002", "赛果不存在"),EXIST("200001","已经存在"),;
	
	public String value;
	
	public String memo;
	
	ErrorCode(String value, String memo) {
		
		this.value = value;
		this.memo = memo;
	}
}
