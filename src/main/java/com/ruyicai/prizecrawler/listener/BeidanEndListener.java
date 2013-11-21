package com.ruyicai.prizecrawler.listener;

import java.util.Map;

import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.activemq.command.ActiveMQMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruyicai.prizecrawler.service.TbeidanScoreService;

@Service("beidanEndListener")
public class BeidanEndListener implements MessageListener {
	private static Logger logger = LoggerFactory
			.getLogger(BeidanEndListener.class);

	@Autowired
	private TbeidanScoreService beidanScoreService;

	@Override
	public void onMessage(Message message) {
		try {
			logger.info("reciveQueue-beidanend");
			Map<String, Object> map = ((ActiveMQMessage) message)
					.getProperties();
			String event = map.get("EVENT").toString();

			beidanScoreService.findAndPersist(event);

			logger.info("beidanend event=" + event);

		} catch (Exception e) {
			logger.info("beidanend err", e);
		}

	}

}
