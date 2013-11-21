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

@Service("beidanResultProducer")
public class BeidanResultProducer {

	@Resource
	private JmsTemplate jmsTemplate;
	
	@Resource(name="beidanresult")
	private Destination destination;
	
	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}
	
	public void send(final String lotno,final String batchcode,final String no){
		this.jmsTemplate.send(destination,new MessageCreator() {   
            public Message createMessage(Session session) throws JMSException {   
            	MapMessage message = session.createMapMessage();
            	message.setStringProperty("lotno", lotno);
            	message.setStringProperty("batchcode", batchcode);
            	message.setStringProperty("no", no);
                return (Message) message;
            }   
        });
	}
}
