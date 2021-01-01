package com.packtpub.hibernatesearch.startup;

import java.util.Arrays;
import java.util.HashSet;

import javax.servlet.ServletContextEvent;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.packtpub.hibernatesearch.domain.App;
import com.packtpub.hibernatesearch.domain.CustomerReview;
import com.packtpub.hibernatesearch.domain.Device;

/**
 * A helper class for server instances intended to run as the "master" node in a cluster.  The master node is the only one which 
 * may directly update the *overall* master Lucene index.  Slave nodes may only only update the overall master index by passing 
 * JMS messages, which the master node is responsible for receiving and processing.
 * 
 * As a ServletContextListener, the servlet container will automatically invoke the "contextInitialized" method upon startup.  This 
 * method loads and indexes about half of the test data set (i.e. to demonstrate the "slave" node creating the other half).  This 
 * class spawns a separate thread to monitor a JMS queue for requests from slave nodes, and perform the Lucene updates requested 
 * in those messages.
 * 
 * Previous versions of this class (named "StartupDataLoader") were annotated with @WebListener.  However, this version of the 
 * application relies on "web.xml" defining the appropriate listener... because an application instance should not load both 
 * MasterNodeInitializer *and* SlaveNodeInitializer together.
 */
public class MasterNodeInitializer implements javax.servlet.ServletContextListener {
	
	Logger logger = LoggerFactory.getLogger(MasterNodeInitializer.class);
	
	/**
	 * Should not be accessed directly.  Use openSession().
	 */
	private static SessionFactory sessionFactory;
	
	/**
	 * Constructing a new Hibernate SessionFactory for every request would cause very poor performance.  However, 
	 * Java servlets must be thread-safe, so we can't use a SessionFactory as an instance variable.  This method provides 
	 * thread-safe access to a SessionFactory, so the startup data loader and the search servlet can open Hibernate sessions 
	 * more efficiently.
	 * 
	 * @return Session
	 */
	public static synchronized Session openSession() {
		if(sessionFactory == null) {
			Configuration configuration = new Configuration();
			configuration.configure();
			ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
			sessionFactory = configuration.buildSessionFactory(serviceRegistry);			
		}
		return sessionFactory.openSession();
	}
	
	public void contextInitialized(ServletContextEvent event) {
		
		event.getServletContext().setAttribute("mode", "master");

		// For demonstration purposes, we will have about half the data created by the master node... and the other half created by 
		// the slave node.  After a few seconds, both nodes will refresh their local copies of the index using the overall master... and 
		// all of the App entities will be searchable from either node.  
		FullTextSession fullTextSession = Search.getFullTextSession( openSession() );
		fullTextSession.beginTransaction();
		
		//
		// Create 5 devices
		//
		Device xPhone = new Device("Orange", "xPhone", null);
		Device xTablet = new Device("Orange", "xTablet", null);
		Device solarSystem = new Device("Song-Sung", "Solar System Phone", null);
		Device flame = new Device("Jungle", "Flame Book Reader", null);
		Device pc = new Device(null, "Personal Computer", null);
		
		//
		// Create and persist 6 of 12 apps with devices and customer reviews
		//
		App theCloud = new App(
				"The Cloud", 
				"cloud.jpg", 
				"The Cloud is a place of magic and wonder.  Businesses run smoothly in the Cloud.  Developers no longer need system administrators, or food and water for that matter.  You can watch television on your tablet device from the comfort of your own sofa, without having to look up at the television.  Download the Cloud app, from the Cloud, and harness this awesome power today!",
				"Business",
				7.99f);
		theCloud.setSupportedDevices( new HashSet<Device>(Arrays.asList(new Device[] { xPhone, xTablet })) );
		CustomerReview theCloudReview1 = new CustomerReview("fanboy1984", 5, "This app makes my <span style=\"font-weight:bold\">xPhone</span> even more stylish and trendy!");
		CustomerReview theCloudReview2 = new CustomerReview("anti.hipster", 1, "I don't understand what 'The Cloud' means.  This seems like more of a catchphrase than a new technology or app...");
		theCloud.setCustomerReviews( new HashSet<CustomerReview>(Arrays.asList(new CustomerReview[] { theCloudReview1, theCloudReview2 })) );
		fullTextSession.save(theCloud);
		logger.info("Persisting " + theCloud.getName());

		App salesCloser = new App(
				"Sales Closer", 
				"pointing.jpg", 
				"A high-powered productivity app for high-powered sales professionals.  Track your high-powered leads, and manage your high-powered schedule.  When you are out on the town doing high-powered networking, you want to show your high-powered sales prospects that you are high-powered too.",
				"Business",
				5.99f);
		salesCloser.setSupportedDevices( new HashSet<Device>(Arrays.asList(new Device[] { xPhone, solarSystem })) );
		CustomerReview salesCloserReview = new CustomerReview("ShowMeTheMoney", 5, "Great app!  If you have used 'Sales Commander 2000' before, then this interface will feel familiar.");
		salesCloser.setCustomerReviews( new HashSet<CustomerReview>(Arrays.asList(new CustomerReview[] { salesCloserReview })) );
		fullTextSession.save(salesCloser);
		logger.info("Persisting " + salesCloser.getName());

		App football = new App(
				"World Tournament Football", 
				"ball.jpg", 
				"This game app offers all the excitement of football (soccer), except that it's played on a touch screen rather than your feet.  So there isn't any of the kicking, or the running, or any of the physical exercise at all.  Other than that, it's pretty much the same.",
				"Games",
				1.99f);
		football.setSupportedDevices( new HashSet<Device>(Arrays.asList(new Device[] { xTablet, flame })) );
		CustomerReview footballReview = new CustomerReview("RealAmerican", 2, "False advertising... I though this was supposed to be football, but it's a SOCCER game instead.");
		football.setCustomerReviews( new HashSet<CustomerReview>(Arrays.asList(new CustomerReview[] { footballReview })) );
		fullTextSession.save(football);
		logger.info("Persisting " + football.getName());
		
		App crystal = new App(
				"Yet Another Crystal Game", 
				"brilliant.jpg", 
				"A dazzling game app, in which you connect crystals of the same color to make them go away.  It's sort of like Tetris.  Actually, it's sort of like the other dozen or so other games today where you connect crystals of the same color.",
				"Games",
				0.99f);
		crystal.setSupportedDevices( new HashSet<Device>(Arrays.asList(new Device[] { flame, pc })) );
		CustomerReview crystalReview = new CustomerReview("YetAnotherGamer", 3, "Why is this only supported on two devices?  The other dozen clones of this game are available on all devices.  You should really make this app inactive until more devices are supported...");
		crystal.setCustomerReviews( new HashSet<CustomerReview>(Arrays.asList(new CustomerReview[] { crystalReview })) );
		crystal.setActive(false);
		fullTextSession.save(crystal);
		logger.info("Persisting " + crystal.getName());
		
		App pencilSharpener = new App(
				"Pencil Sharpener", 
				"pencil.jpg", 
				"Sharpen your pencils, by sticking them into your phone's Bluetooth plug and pushing a button.  This app really pushes your phone's hardware to its limits!",
				"Business",
				2.99f);
		pencilSharpener.setSupportedDevices( new HashSet<Device>(Arrays.asList(new Device[] { xPhone, solarSystem })) );
		CustomerReview pencilSharpenerReview1 = new CustomerReview("missing.digits", 1, "Ouch, this app is a menace!  I should sue.");
		CustomerReview pencilSharpenerReview2 = new CustomerReview("LawyerGuy", 5, "@missing.digits:  Private message me.  Let's talk...");
		pencilSharpener.setCustomerReviews( new HashSet<CustomerReview>(Arrays.asList(new CustomerReview[] { pencilSharpenerReview1, pencilSharpenerReview2 })) );
		fullTextSession.save(pencilSharpener);
		logger.info("Persisting " + pencilSharpener.getName());
		
		App staplerTracker = new App(
				"Stapler Tracker", 
				"stapler.jpg", 
				"Is someone always taking your stapler?  It's a common problem in many office spaces.  This business productivity app will help you manage your stapler at all times, so that you will never have to deal with a \"case of the Mondays\" again.",
				"Business",
				0.99f);
		staplerTracker.setSupportedDevices( new HashSet<Device>(Arrays.asList(new Device[] { pc })) );
		CustomerReview staplerTrackerReview = new CustomerReview("mike.bolton", 3, "'PC LOAD LETTER'?  What does that mean?!?");
		staplerTracker.setCustomerReviews( new HashSet<CustomerReview>(Arrays.asList(new CustomerReview[] { staplerTrackerReview })) );
		fullTextSession.save(staplerTracker);
		logger.info("Persisting " + staplerTracker.getName());
		
		//
		// Close and cleanup the Hibernate session
		//
		fullTextSession.getTransaction().commit();
		fullTextSession.close();
		
		//
		// Spawn a thread to monitor the JMS queue for updates.  The "QueueMonitor" class is responsible for monitoring 
		// and receiving messages, while the "QueueController" class is responsible for performing the Lucene updates 
		// based on the message data.
		//
		QueueController queueController = new QueueController(sessionFactory.openSession());
		Thread queueMonitor = new Thread(new QueueMonitor(queueController)); 
		queueMonitor.start();
			
	} 

	/**
	 * This method is invoked automatically when the servlet engine shuts down.  It closes the Hibernate SessionFactory, if 
	 * it's still open.
	 */
	public void contextDestroyed(ServletContextEvent event) {
		if(!sessionFactory.isClosed()) {
			sessionFactory.close();
		}
	}
	
}
