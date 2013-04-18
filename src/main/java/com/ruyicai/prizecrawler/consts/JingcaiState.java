package com.ruyicai.prizecrawler.consts;

import java.math.BigDecimal;

public enum JingcaiState {

	Normal(0, "正常"),
	END(1, "已停售"),
	Resulted(2, "已出赛果"),
	ENCASH(3, "已派奖"),
	
	AUDIT(0, "已审核"),
	NOTAUDIT(1, "未审核");
	
	public BigDecimal value;
	
	public String memo;
	
	private JingcaiState(int value, String memo) {
		this.value = new BigDecimal(value);
		this.memo = memo;
	}
}
