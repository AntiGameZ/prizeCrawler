package com.ruyicai.prizecrawler.lottype.twentytwocfive;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ruyicai.prizecrawler.AbstractLottype;
import com.ruyicai.prizecrawler.IAgency;
import com.ruyicai.prizecrawler.consts.SystemCode;
import com.ruyicai.prizecrawler.domain.AgencyPrizeCode;
import com.ruyicai.prizecrawler.domain.PrizeInfo;
import com.ruyicai.prizecrawler.enums.CheckStateType;
import com.ruyicai.prizecrawler.enums.CrawkStateType;
import com.ruyicai.prizecrawler.producer.CheckCodeProducer;
import com.ruyicai.prizecrawler.service.AgencyPrizeCodeService;
import com.ruyicai.prizecrawler.service.LotCheckSwitchService;

@Service("T01013")
public class Twentytwocfive extends AbstractLottype {

	private static Logger logger = LoggerFactory
			.getLogger(Twentytwocfive.class);
	private static final long WAITTIME = Long.parseLong(ResourceBundle
			.getBundle("waittime").getString("waittime.T01013"));

	@Resource
	private AgencyPrizeCodeService agencyPrizeCodeService;

	@Resource
	private CheckCodeProducer checkCodeProducer;

	@Resource
	private LotCheckSwitchService lotCheckSwitchService;
	
	private Map<String,IAgency> map = new HashMap<String,IAgency>();
	{
		map.put("I10004", new TwentyC5Jincaizi());
		map.put("I10001", new TwentyC5500wan());
	}

	public void crawl(AgencyPrizeCode agencyPrizeCode) {
		logger.info("<<<<<<<<<<开始抓取一条开奖：lotno=" + agencyPrizeCode.getId().getLotno() + " batchcode="
				+ agencyPrizeCode.getId().getBatchcode()+"agencyno="+agencyPrizeCode.getId().getAgencyno()+"times="+agencyPrizeCode.getCrawltimes());
		try {
			if (agencyPrizeCode.getCrawltimes() >= SystemCode.DAPAN_CRAWLTIME) {
				agencyPrizeCode.setCrawlstate(CrawkStateType.FAIL.value);
				agencyPrizeCodeService.mergeIfAgencyExist(agencyPrizeCode);
				return;
			}
			
			if (System.currentTimeMillis()-agencyPrizeCode.getCreatedate().getTime() < WAITTIME) {
				logger.info("<<<<<<<<<<未到抓取时间，本次跳过");
				return;
			}
			
			IAgency iagency = map.get(agencyPrizeCode.getId().getAgencyno());
			
			PrizeInfo prizeInfo = iagency.crawlFromAgency("T01013", agencyPrizeCode.getId().getBatchcode());
			
			if(prizeInfo.getWinbasecode()!=null&&prizeInfo.getWinbasecode().trim().length()==14) {
				agencyPrizeCode.setWincode(prizeInfo.getWinbasecode());
				agencyPrizeCode.setCrawlstate(CrawkStateType.SUCCESS.value);
				agencyPrizeCode.setCrawltimes(agencyPrizeCode.getCrawltimes()+1);
				agencyPrizeCode.setCheckstate(CheckStateType.WAIT.value);
				agencyPrizeCode.setCodedate(new Date());
				boolean flag = agencyPrizeCodeService.mergeIfAgencyExist(agencyPrizeCode);
				if(flag) {
					checkCodeProducer.send(agencyPrizeCode.getId().getAgencyno(), agencyPrizeCode.getId().getLotno(), agencyPrizeCode.getId().getBatchcode(), agencyPrizeCode.getWincode());
				}
			}else {
				agencyPrizeCode.setCrawltimes(agencyPrizeCode.getCrawltimes()+1);
				agencyPrizeCodeService.mergeIfAgencyExist(agencyPrizeCode);
			}
			
			logger.info("<<<<<<<<<<抓取一条开奖结束：lotno=" + agencyPrizeCode.getId().getLotno() + " batchcode="
					+ agencyPrizeCode.getId().getBatchcode() +"times="+agencyPrizeCode.getCrawltimes());
		} catch (Exception e) {
			logger.info("<<<<<<<<<<抓取一条开奖异常", e);
		}
	}

	@Override
	public boolean isCrawl() {

		return true;
	}

	@Override
	public boolean isFastOpening() {
		return false;
	}

	@Override
	public String[] getSplitWincode(String wincode) {
		return new String[] { wincode, "" };
	}

	@Override
	public String getWincode(String winbasecode, String winspecialcode) {
		return winbasecode;
	}

	@Override
	public boolean isCheck() {
		return lotCheckSwitchService.isCheck("T01013");
	}
}
