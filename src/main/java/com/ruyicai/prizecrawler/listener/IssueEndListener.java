package com.ruyicai.prizecrawler.listener;

import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.activemq.command.ActiveMQMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruyicai.prizecrawler.Lottype;
import com.ruyicai.prizecrawler.domain.PrizeInfo;
import com.ruyicai.prizecrawler.service.AgencyPrizeCodeService;
import com.ruyicai.prizecrawler.service.AgencyPrizeInfoService;
import com.ruyicai.prizecrawler.service.PrizeInfoService;

@Service("issueEndListener")
public class IssueEndListener implements MessageListener{
	private static Logger logger = LoggerFactory.getLogger(IssueEndListener.class);
	@Resource
	private PrizeInfoService PrizeInfoService;
	
	@Resource
	private AgencyPrizeCodeService agencyPrizeCodeService;
	
	@Resource
	private AgencyPrizeInfoService agencyPrizeInfoService;
	
	@Autowired
	private Map<String,Lottype> lottypes;

	@Override
	public void onMessage(Message message) {
		try {
			logger.info("reciveQueue-EndIssue");
			Map<String,Object> map = ((ActiveMQMessage)message).getProperties();
			
			if(lottypes.get(map.get("LOTNO").toString())==null) {
				logger.info("本次节期彩种为"+map.get("LOTNO").toString()+"该彩种尚未提供");
				return;
			}
			
			
			logger.info("lotno="+map.get("LOTNO").toString()+" batchcode="+map.get("BATCHCODE").toString());
			if(lottypes.get(map.get("LOTNO").toString()).isFastOpening()) {
				logger.info(map.get("LOTNO").toString()+"是高频彩，采取单纯网页抓取策略开奖");
				
				if(!lottypes.get(map.get("LOTNO").toString()).isCrawl()) {
					logger.info("本次节期彩种为"+map.get("LOTNO").toString()+"不需要抓取");
					return;
				}
				logger.info("保存prizeInfo信息");
				PrizeInfo prizeInfo = new PrizeInfo();
				prizeInfo.setBatchcode(map.get("BATCHCODE").toString());
				prizeInfo.setCrawlnum(0);
				prizeInfo.setCrawlState(0);
				prizeInfo.setCreatedate(new Date((Long)map.get("ENDTIME")));
				prizeInfo.setLotno(map.get("LOTNO").toString());
				prizeInfo.setNoticenum(0);
				prizeInfo.setNoticeState(0);
				PrizeInfoService.persist(prizeInfo);
				logger.info("保存prizeInfo信息结束");
			}else {
				logger.info(map.get("LOTNO").toString()+"非高频彩，采取单纯网页抓取加权重策略开奖");
				if(!lottypes.get(map.get("LOTNO").toString()).isCheck()) {
					logger.info("本次节期彩种为"+map.get("LOTNO").toString()+"已经关闭抓取比较");
					return;
				}
				
				logger.info("创建所有渠道的开奖信息");
				agencyPrizeCodeService.createAllAgencyPrizeCode(map.get("LOTNO").toString(), map.get("BATCHCODE").toString());
				agencyPrizeInfoService.createAllAgencyPrizeInfo(map.get("LOTNO").toString(), map.get("BATCHCODE").toString());
				logger.info("创建所有渠道的开奖信息结束");
			}
			
		} catch (Exception e) {
			logger.info("IssueEnd err",e);
		} 
		
	}
	

}
