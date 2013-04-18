package com.ruyicai.prizecrawler.listener;

import java.util.Map;

import javax.annotation.Resource;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.activemq.command.ActiveMQMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ruyicai.prizecrawler.service.PrizeInfoCheckService;

@Service("checkInfoListener")
public class CheckInfoListener implements MessageListener{
	private static Logger logger = LoggerFactory.getLogger(CheckInfoListener.class);
	
	@Resource
	private PrizeInfoCheckService prizeInfoCheckService;

	@Override
	public void onMessage(Message message) {
		try {
			logger.info("reciveQueue-checkInfoQueue");
			Map<String,Object> map = ((ActiveMQMessage)message).getProperties();
			String lotno = map.get("lotno").toString();
			String batchcode = map.get("batchcode").toString();
			String agencyno = map.get("agencyno").toString();
			String wininfo = map.get("info").toString();
			logger.info("checkInfoQueue lotno="+lotno+" batchcode="+batchcode+" agencyno="+agencyno+" info="+wininfo);
			
			prizeInfoCheckService.checkPrizeCode(agencyno, lotno, batchcode, wininfo);
			
		} catch (Exception e) {
			logger.info("checkInfoQueue err",e);
		} 
		
	}
	

}
