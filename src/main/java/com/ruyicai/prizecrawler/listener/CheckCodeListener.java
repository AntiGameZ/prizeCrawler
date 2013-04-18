package com.ruyicai.prizecrawler.listener;

import java.util.Map;

import javax.annotation.Resource;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.activemq.command.ActiveMQMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ruyicai.prizecrawler.service.PrizeCodeCheckService;

@Service("checkCodeListener")
public class CheckCodeListener implements MessageListener{
	private static Logger logger = LoggerFactory.getLogger(CheckCodeListener.class);
	
	@Resource
	private PrizeCodeCheckService prizeCodeCheckService;

	@Override
	public void onMessage(Message message) {
		try {
			logger.info("reciveQueue-checkCodeQueue");
			Map<String,Object> map = ((ActiveMQMessage)message).getProperties();
			String lotno = map.get("lotno").toString();
			String batchcode = map.get("batchcode").toString();
			String agencyno = map.get("agencyno").toString();
			String wincode = map.get("wincode").toString();
			logger.info("checkCodeQueue lotno="+lotno+" batchcode="+batchcode+" agencyno="+agencyno+" wincode="+wincode);
			
			prizeCodeCheckService.checkPrizeCode(agencyno, lotno, batchcode, wincode);
			
		} catch (Exception e) {
			logger.info("checkCodeQueue err",e);
		} 
		
	}
	

}
