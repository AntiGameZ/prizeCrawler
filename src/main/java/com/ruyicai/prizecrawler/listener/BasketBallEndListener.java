package com.ruyicai.prizecrawler.listener;

import java.util.Map;

import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.activemq.command.ActiveMQMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruyicai.prizecrawler.service.TjingcaiScoreService;

@Service("basketBallEndListener")
public class BasketBallEndListener implements MessageListener {
	private static Logger logger = LoggerFactory
			.getLogger(BasketBallEndListener.class);

	@Autowired
	private TjingcaiScoreService jingcaiScoreService;

	@Override
	public void onMessage(Message message) {
		try {
			logger.info("reciveQueue-basketballend");
			Map<String, Object> map = ((ActiveMQMessage) message)
					.getProperties();
			String event = map.get("EVENT").toString();

			jingcaiScoreService.findAndPersist(event);

			logger.info("basketballend event=" + event);

		} catch (Exception e) {
			logger.info("basketballend err", e);
		}

	}

}
