package com.ruyicai.prizecrawler;

import com.ruyicai.prizecrawler.domain.AgencyPrizeCode;
import com.ruyicai.prizecrawler.domain.PrizeInfo;

public interface Lottype {

	public void crawl(PrizeInfo prizeInfo);
	
	public void crawl(AgencyPrizeCode agencyPrizeCode);
	
	public boolean isCrawl();
	
	public boolean isFastOpening();
	
	public String[] getSplitWincode(String wincode);
	
	public String getWincode(String winbasecode,String winspecialcode);
	
	public boolean isCheck();

}
