package com.ruyicai.prizecrawler;

import com.ruyicai.prizecrawler.domain.PrizeInfo;

public interface IAgency {

	public PrizeInfo crawlFromAgency(String lotno,String batchcode);
}
