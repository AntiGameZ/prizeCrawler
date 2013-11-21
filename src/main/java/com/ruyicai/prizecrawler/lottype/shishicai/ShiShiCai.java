package com.ruyicai.prizecrawler.lottype.shishicai;

import java.util.Date;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ruyicai.prizecrawler.AbstractLottype;
import com.ruyicai.prizecrawler.consts.SystemCode;
import com.ruyicai.prizecrawler.domain.PrizeInfo;
import com.ruyicai.prizecrawler.service.PrizeInfoService;
import com.ruyicai.prizecrawler.timer.NoticeTime;

@Service("T01007")
public class ShiShiCai extends AbstractLottype{

	private static Logger logger = LoggerFactory.getLogger(ShiShiCai.class);
	private static final long WAITTIME = 120000;

	@Resource
	private PrizeInfoService prizeInfoService;
	
	@Resource
	private NoticeTime noticeTimer;

	private CQFuCai cqfucai = new CQFuCai();
	private ShiShiCaicn ssccn = new ShiShiCaicn();
	private Wubaiwan wubaiwan = new Wubaiwan();

	public void crawl(PrizeInfo prizeInfo) {
		logger.info("<<<<<<<<<<开始抓取一条开奖：lotno=" + prizeInfo.getLotno() + " batchcode="
				+ prizeInfo.getBatchcode()+"times="+prizeInfo.getCrawlnum());
		try {
			if (prizeInfo.getCrawlnum() >= SystemCode.CRAWLTIME) {
				prizeInfo.setCrawlState(-1);
				prizeInfoService.merge(prizeInfo);
				return;
			}
			System.out.println(System.currentTimeMillis()-prizeInfo.getCreatedate().getTime());
			if (System.currentTimeMillis()-prizeInfo.getCreatedate().getTime() < WAITTIME) {
				logger.info("<<<<<<<<<<未到抓取时间，本次跳过");
				return;
			}
			
			PrizeInfo p1 = cqfucai.carwlFromCQByBatchcode(prizeInfo
					.getBatchcode());
			PrizeInfo p2 = ssccn.carwlFromFromShiShiCaicnByBatchcode(prizeInfo
					.getBatchcode());
			PrizeInfo p3 = wubaiwan.carwlFromWubaiwanByBatchcode(prizeInfo
					.getBatchcode());
			if (p1.equals(p2)) {
				prizeInfo.setBatchcode(prizeInfo.getBatchcode());
				prizeInfo.setCrawlnum(prizeInfo.getCrawlnum() + 1);
				prizeInfo.setCrawlState(1);
				prizeInfo.setLotno("T01007");
				prizeInfo.setWinbasecode(p1.getWinbasecode());
				prizeInfo.setWinspecialcode(p1.getWinspecialcode());
				prizeInfo.setCrawldate(new Date());
			} else if (p2.equals(p3)) {
				prizeInfo.setBatchcode(prizeInfo.getBatchcode());
				prizeInfo.setCrawlnum(prizeInfo.getCrawlnum() + 1);
				prizeInfo.setCrawlState(1);
				prizeInfo.setLotno("T01007");
				prizeInfo.setWinbasecode(p2.getWinbasecode());
				prizeInfo.setWinspecialcode(p2.getWinspecialcode());
				prizeInfo.setCrawldate(new Date());
			} else if (p1.equals(p3)) {
				prizeInfo.setBatchcode(prizeInfo.getBatchcode());
				prizeInfo.setCrawlnum(prizeInfo.getCrawlnum() + 1);
				prizeInfo.setCrawlState(1);
				prizeInfo.setLotno("T01007");
				prizeInfo.setWinbasecode(p1.getWinbasecode());
				prizeInfo.setWinspecialcode(p1.getWinspecialcode());
				prizeInfo.setCrawldate(new Date());
			} else {
				prizeInfo.setCrawlnum(prizeInfo.getCrawlnum() + 1);
				prizeInfo.setCrawlState(0);
			}
			
			
			/**
			if(p1.getWinbasecode().length()==5) {
				prizeInfo.setBatchcode(prizeInfo.getBatchcode());
				prizeInfo.setCrawlnum(prizeInfo.getCrawlnum() + 1);
				prizeInfo.setCrawlState(1);
				prizeInfo.setLotno("T01007");
				prizeInfo.setWinbasecode(p1.getWinbasecode());
				prizeInfo.setWinspecialcode(p1.getWinspecialcode());
				prizeInfo.setCrawldate(new Date());
			}else {
				prizeInfo.setCrawlnum(prizeInfo.getCrawlnum() + 1);
				prizeInfo.setCrawlState(0);
			}*/
			
			prizeInfoService.merge(prizeInfo);
			logger.info("<<<<<<<<<<抓取一条开奖结束：lotno=" + prizeInfo.getLotno() + " batchcode="
					+ prizeInfo.getBatchcode() +"times="+prizeInfo.getCrawlnum());
			
			noticeTimer.noticeFashOpen();
			
		} catch (Exception e) {
			prizeInfo.setCrawlnum(prizeInfo.getCrawlnum() + 1);
			prizeInfo.setCrawlState(0);
			prizeInfoService.merge(prizeInfo);
			logger.info("<<<<<<<<<<抓取一条开奖异常", e);
		}
	}

	@Override
	public boolean isCrawl() {
		return configService.isCrawl("T01007");
	}

	@Override
	public boolean isFastOpening() {
		return true;
	}

}
