package com.ruyicai.prizecrawler.listener;

import java.util.Map;

import javax.annotation.Resource;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.activemq.command.ActiveMQMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ruyicai.prizecrawler.service.SendEmailService;

@Service("sendEmailListener")
public class SendEmailListener implements MessageListener{
	private static Logger logger = LoggerFactory.getLogger(SendEmailListener.class);
	
	@Resource
	private SendEmailService sendEmailService;

	@Override
	public void onMessage(Message message) {
		try {
			logger.info("reciveQueue-checkInfoQueue");
			Map<String,Object> map = ((ActiveMQMessage)message).getProperties();
			String lotno = map.get("lotno").toString();
			String batchcode = map.get("batchcode").toString();
			String type = map.get("type").toString();
			int times = (Integer)map.get("times");
			logger.info("sendemail lotno="+lotno+" batchcode="+batchcode+" times="+times);
			
			sendEmailService.sendEmail(lotno, times, batchcode,type);
			
		} catch (Exception e) {
			logger.info("checkInfoQueue err",e);
		} 
		
	}
	

}
