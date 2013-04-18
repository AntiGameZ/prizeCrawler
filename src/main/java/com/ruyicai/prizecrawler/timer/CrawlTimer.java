package com.ruyicai.prizecrawler.timer;

import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruyicai.prizecrawler.Lottype;
import com.ruyicai.prizecrawler.consts.SystemCode;
import com.ruyicai.prizecrawler.domain.AgencyPrizeCode;
import com.ruyicai.prizecrawler.domain.PrizeInfo;
import com.ruyicai.prizecrawler.producer.EmailProducer;
import com.ruyicai.prizecrawler.service.AgencyPrizeCodeService;
import com.ruyicai.prizecrawler.service.PrizeInfoService;

@Service("crawlTimer")
public class CrawlTimer {

	private static Logger logger = LoggerFactory.getLogger(CrawlTimer.class);
	
	@Autowired
	private Map<String,Lottype> lottypes;
	
	@Resource
	private PrizeInfoService prizeInfoService;
	
	@Resource
	private AgencyPrizeCodeService agencyPrizeCodeService;
	
	@Resource
	private EmailProducer emailProducer;
	

	
	public void crawlFashOpen() {
		Random rdm = new Random();
		int rd = rdm.nextInt(100000)+100000;
		logger.info("高频彩开始进行抓取random="+rd+"<<<<<<<<<<");
		List<PrizeInfo> noCrawel = prizeInfoService.findAllNoCrawl();
		
		if(noCrawel.isEmpty()) {
			logger.info("高频彩没有需要进行抓取的开奖random="+rd+"<<<<<<<<<<");
			return;
		}
		
		for(PrizeInfo prizeInfo:noCrawel) {
			
			Lottype lottype = lottypes.get(prizeInfo.getLotno());
			if(lottype==null) {
				logger.info("高频彩抓奖lottype=null random="+rd+"<<<<<<<<<<");
				continue;
			}
			lottype.crawl(prizeInfo);
			emailProducer.send(prizeInfo.getLotno(), prizeInfo.getBatchcode(), prizeInfo.getCrawlnum(), SystemCode.TYPE_CRAWL);
		}
		
		logger.info("高频彩本次抓奖结束random="+rd+"<<<<<<<<<<");
	}
	
	
	
	public void crawlDaPan() {
		Random rdm = new Random();
		int rd = rdm.nextInt(100000)+100000;
		logger.info("大盘彩开始进行抓取random="+rd+"<<<<<<<<<<");
		List<AgencyPrizeCode> noCrawel = agencyPrizeCodeService.findNotCrawl();
		
		if(noCrawel.isEmpty()) {
			logger.info("大盘彩没有需要进行抓取的开奖random="+rd+"<<<<<<<<<<");
			return;
		}
		
		for(AgencyPrizeCode agencyPrizeCode:noCrawel) {
			Lottype lottype = lottypes.get(agencyPrizeCode.getId().getLotno());
			if(lottype==null) {
				logger.info("大盘彩抓奖lottype=null random="+rd+"<<<<<<<<<<");
				continue;
			}
			lottype.crawl(agencyPrizeCode);
		}
		
		logger.info("大盘彩本次抓奖结束random="+rd+"<<<<<<<<<<");
	}
}
