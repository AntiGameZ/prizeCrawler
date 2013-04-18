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

@Service("smsProducer")
public class SMSProducer {

	@Resource
	private JmsTemplate jmsTemplate;
	
	@Resource(name="errDrawSms")
	private Destination destination;
	
	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}
	
	public void send(final String type,final String msg){
		this.jmsTemplate.send(destination,new MessageCreator() {   
            public Message createMessage(Session session) throws JMSException {   

            	MapMessage message = session.createMapMessage();
            	message.setStringProperty("type", type);
            	message.setStringProperty("msg", msg);
                return (Message) message;
            }   
        });
	}
}
