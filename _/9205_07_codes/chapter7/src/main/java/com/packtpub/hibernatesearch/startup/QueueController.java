package com.packtpub.hibernatesearch.startup;

import org.hibernate.Session;
import org.hibernate.search.backend.impl.jms.AbstractJMSHibernateSearchController;

/**
 * In a clustered environment with a JMS backend, slave nodes may only update the master Lucene by sending requests 
 * to a JMS queue.  The master node is responsible for monitoring this queue, and processing any request messages that 
 * it receives.  
 * 
 * To perform the actual index update, the master node should use a subclass of the Hibernate Search abstract 
 * class "AbstractJMSHibernateSearchController"... passing the JMS message to the "onMessage" method in this base class.
 * 
 * In a full JEE environment, you might wrap up the custom subclass as a message-driven bean... as suggested by the 
 * Hibernate Search documentation.  However, "AbstractJMSHibernateSearchController" itself does not require a full JEE 
 * stack.  As long as your chosen framework (or vanilla code) can monitor a JMS queue without blocking the application's 
 * main thread, then it can pass JMS messages to subclasses of this controller just fine.
 */
public class QueueController extends AbstractJMSHibernateSearchController {

	private Session session;
	
	public QueueController(Session session) {
		this.session = session;
	}
	
	@Override
	protected Session getSession() {
		return this.session;
	}

	@Override
	protected void cleanSessionIfNeeded(Session session) {
	}

}
