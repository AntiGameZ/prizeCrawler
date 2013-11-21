package com.ruyicai.prizecrawler.consts;

import java.math.BigDecimal;

public enum ShortNameType {

	BEIDAN_LEAGUE(0, "北单联赛"),
	BEIDAN_TEAM(1, "北单球队"),
	JINGCAI_LQ_LEAGUE(3, "竞彩篮球联赛"),
	JINGCAI_ZQ_LEAGUE(4, "竞彩足球联赛"),
	JINGCAI_LQ_TEAM(5, "竞彩篮球球队"),
	JINGCAI_ZQ_TEAM(6, "竞彩足球球队"),
	ZUCAI_LEAGUE(7, "足彩联赛"),
	ZUCAI_TEAM(8, "足彩球队");
	
	
	
	public BigDecimal value;
	
	public String memo;
	
	public int intValue;
	
	private ShortNameType(int value, String memo) {
		this.intValue = value;
		this.value = new BigDecimal(value);
		this.memo = memo;
	}
}
