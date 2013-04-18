package com.ruyicai.prizecrawler.producer;

import javax.annotation.Resource;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.ruyicai.prizecrawler.domain.PrizeInfo;

@Service("prizeInfoProducer")
public class PrizeInfoProducer {

	@Resource
	private JmsTemplate jmsTemplate;
	
	@Resource(name="prizecrawlerIssueEnd")
	private Destination destination;
	
	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}
	
	public void send(final PrizeInfo prize){
		this.jmsTemplate.send(destination,new MessageCreator() {   
            public Message createMessage(Session session) throws JMSException {   

            	MapMessage message = session.createMapMessage();
            	message.setStringProperty("LOTNO", prize.getLotno());
            	message.setStringProperty("BATCHCODE", prize.getBatchcode());
            	message.setLongProperty("ENDTIME", prize.getCreatedate().getTime());
                return (Message) message;   
            }   
        });
	}
}
