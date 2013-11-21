package com.ruyicai.prizecrawler;

import org.springframework.beans.factory.annotation.Autowired;

import com.ruyicai.prizecrawler.domain.AgencyPrizeCode;
import com.ruyicai.prizecrawler.domain.PrizeInfo;
import com.ruyicai.prizecrawler.service.SendEmailService;

public abstract class AbstractLottype implements Lottype {
	
	@Autowired
	protected SendEmailService configService;
	
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
