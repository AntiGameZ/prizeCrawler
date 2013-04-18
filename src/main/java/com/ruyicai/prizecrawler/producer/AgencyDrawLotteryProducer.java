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

@Service("agencyDrawLotteryProducer")
public class AgencyDrawLotteryProducer {

	@Resource
	private JmsTemplate jmsTemplate;
	
	@Resource(name="agencyDrawLottery")
	private Destination destination;
	
	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}
	
	public void send(final String agencyno,final String lotno,final String batchcode,final String winbasecode,final String winspecialcode,final String info){
		this.jmsTemplate.send(destination,new MessageCreator() {   
            public Message createMessage(Session session) throws JMSException {   

            	MapMessage message = session.createMapMessage();
            	message.setStringProperty("agencyno", agencyno);
            	message.setStringProperty("lotno", lotno);
            	message.setStringProperty("batchcode", batchcode);
            	message.setStringProperty("winbasecode", winbasecode);
            	message.setStringProperty("winspecialcode", winspecialcode);
            	message.setStringProperty("info", info);
                return (Message) message;
            }   
        });
	}
}
