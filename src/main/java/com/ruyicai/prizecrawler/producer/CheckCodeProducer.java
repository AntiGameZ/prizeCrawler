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

@Service("checkCodeProducer")
public class CheckCodeProducer {

	@Resource
	private JmsTemplate jmsTemplate;
	
	@Resource(name="checkCodeQueue")
	private Destination destination;
	
	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}
	
	public void send(final String agencyno,final String lotno,final String batchcode,final String wincode){
		this.jmsTemplate.send(destination,new MessageCreator() {   
            public Message createMessage(Session session) throws JMSException {   

            	MapMessage message = session.createMapMessage();
            	message.setStringProperty("agencyno", agencyno);
            	message.setStringProperty("lotno", lotno);
            	message.setStringProperty("batchcode", batchcode);
            	message.setStringProperty("wincode", wincode);
                return (Message) message;
            }   
        });
	}
}
