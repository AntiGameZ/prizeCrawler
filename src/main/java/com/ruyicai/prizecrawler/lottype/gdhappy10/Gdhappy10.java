package com.ruyicai.prizecrawler.lottype.gdhappy10;

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

@Service("T01015")
public class Gdhappy10 extends AbstractLottype{

	private static Logger logger = LoggerFactory.getLogger(Gdhappy10.class);

	private static final long WAITTIME = 230000;
	
	
	@Resource
	private PrizeInfoService prizeInfoService;

	@Resource
	private NoticeTime noticeTimer;
	
	private Gdhappy10Lehe lehe = new Gdhappy10Lehe();
	private Gdhappy10Lehe2 lehe2 = new Gdhappy10Lehe2();
	private Gdhappy10Lehe3 lehe3 = new Gdhappy10Lehe3();
	private Gdhappy10Caike caike = new Gdhappy10Caike();
	
	
	@Override
	public void crawl(PrizeInfo prizeInfo) {
		logger.info("<<<<<<<<<<开始抓取一条开奖：lotno=" + prizeInfo.getLotno()
				+ " batchcode=" + prizeInfo.getBatchcode() + "times="
				+ prizeInfo.getCrawlnum());
		try {
			if (prizeInfo.getCrawlnum() >= SystemCode.CRAWLTIME) {
				prizeInfo.setCrawlState(-1);
				prizeInfoService.merge(prizeInfo);
				return;
			}

			if (System.currentTimeMillis()
					- prizeInfo.getCreatedate().getTime() < WAITTIME) {
				logger.info("<<<<<<<<<<未到抓取时间，本次跳过");
				return;
			}

			PrizeInfo p1 = lehe.carwlFromHappy10Lehe(prizeInfo
					.getBatchcode());
			PrizeInfo p2 = lehe2.carwlFromHappy10Lehe(prizeInfo
					.getBatchcode());
			PrizeInfo p3 = lehe3.carwlFromHappy10Lehe(prizeInfo
					.getBatchcode());
			PrizeInfo p4 = caike.carwlFromHappy10Caike(prizeInfo.getBatchcode());

			if (p1.getWinbasecode()!=null&&p1.getWinbasecode().length()==23) {
				prizeInfo.setBatchcode(prizeInfo.getBatchcode());
				prizeInfo.setCrawlnum(prizeInfo.getCrawlnum() + 1);
				prizeInfo.setCrawlState(1);
				prizeInfo.setLotno("T01015");
				prizeInfo.setWinbasecode(p1.getWinbasecode());
				prizeInfo.setWinspecialcode(p1.getWinspecialcode());
				prizeInfo.setCrawldate(new Date());
			} else if(p2.getWinbasecode()!=null&&p2.getWinbasecode().length()==23) {
				prizeInfo.setBatchcode(prizeInfo.getBatchcode());
				prizeInfo.setCrawlnum(prizeInfo.getCrawlnum() + 1);
				prizeInfo.setCrawlState(1);
				prizeInfo.setLotno("T01015");
				prizeInfo.setWinbasecode(p2.getWinbasecode());
				prizeInfo.setWinspecialcode(p2.getWinspecialcode());
				prizeInfo.setCrawldate(new Date());
			} else if(p3.getWinbasecode()!=null&&p3.getWinbasecode().length()==23) {
				prizeInfo.setBatchcode(prizeInfo.getBatchcode());
				prizeInfo.setCrawlnum(prizeInfo.getCrawlnum() + 1);
				prizeInfo.setCrawlState(1);
				prizeInfo.setLotno("T01015");
				prizeInfo.setWinbasecode(p3.getWinbasecode());
				prizeInfo.setWinspecialcode(p3.getWinspecialcode());
				prizeInfo.setCrawldate(new Date());
			}else if(p4.getWinbasecode()!=null&&p4.getWinbasecode().length()==23) {
				prizeInfo.setBatchcode(prizeInfo.getBatchcode());
				prizeInfo.setCrawlnum(prizeInfo.getCrawlnum() + 1);
				prizeInfo.setCrawlState(1);
				prizeInfo.setLotno("T01015");
				prizeInfo.setWinbasecode(p4.getWinbasecode());
				prizeInfo.setWinspecialcode(p4.getWinspecialcode());
				prizeInfo.setCrawldate(new Date());
			} else {
				prizeInfo.setCrawlnum(prizeInfo.getCrawlnum() + 1);
				prizeInfo.setCrawlState(0);
			}
			prizeInfoService.merge(prizeInfo);
			logger.info("<<<<<<<<<<抓取一条开奖结束：lotno=" + prizeInfo.getLotno()
					+ " batchcode=" + prizeInfo.getBatchcode() + "times="
					+ prizeInfo.getCrawlnum());

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

		return configService.isCrawl("T01015");
	}

	@Override
	public boolean isFastOpening() {
		return true;
	}
}
