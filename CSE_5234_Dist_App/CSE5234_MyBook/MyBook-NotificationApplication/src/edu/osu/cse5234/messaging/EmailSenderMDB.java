package edu.osu.cse5234.messaging;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue") })
public class EmailSenderMDB implements MessageListener {

	public EmailSenderMDB() {
	}

	public void onMessage(Message message) {
		try {
			System.out.println("MDB: " + 
                     ((TextMessage) message).getText());
		} catch (JMSException e) {
			e.printStackTrace();
		}

	}
}
