package com.ruyicai.prizecrawler;

import com.ruyicai.prizecrawler.domain.AgencyPrizeCode;
import com.ruyicai.prizecrawler.domain.PrizeInfo;

public abstract class AbstractLottype implements Lottype {

	@Override
	public void crawl(PrizeInfo prizeInfo) {

	}

	@Override
	public void crawl(AgencyPrizeCode agencyPrizeCode) {

	}

	@Override
	public boolean isCrawl() {
		return false;
	}

	@Override
	public boolean isFastOpening() {
		return false;
	}
	
	@Override
	public String[] getSplitWincode(String wincode) {
		return new String[]{wincode,""};
	}
	
	@Override
	public String getWincode(String winbasecode,String winspecialcode) {
		return "";
	}
	
	@Override
	public boolean isCheck() {
		return false;
	}

}
