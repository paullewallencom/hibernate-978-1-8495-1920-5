package com.packtpub.hibernatesearch.startup;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is meant to be wrapped into a Thread and spawned by MasterNode Initializer... to monitor a JMS queue and 
 * process incoming Lucene update requests sent from slave nodes.  When a message is received, and instance of 
 * QueueController (inheriting from Hibernate Search's AbstractJMSHibernateSearchController) performs the actual 
 * Lucene work.
 * 
 * In a full JEE environment, you might wrap up the AbstractJMSHibernateSearchController subclass as a message-driven 
 * bean... or some similar mechanism built-in to your application framework of choice.  This vanilla Runnable is 
 * a home-grown substitute, which works just as well.
 */
public class QueueMonitor implements Runnable {
	
	Logger logger = LoggerFactory.getLogger(QueueMonitor.class);

	QueueController controller;
	
	QueueMonitor(QueueController controller) {
		this.controller = controller;
	}
	
	public void run() {
		try {
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost");
			Connection connection = connectionFactory.createConnection();
			connection.start();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Queue queue = session.createQueue("DEMO");
			MessageConsumer consumer = session.createConsumer(queue);
			while(true) {
				Message message = consumer.receive(60000);
				if(message != null) {
					logger.info("QueueMonitor is processing message: " + message.toString());
					controller.onMessage(message);
				}
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

}
