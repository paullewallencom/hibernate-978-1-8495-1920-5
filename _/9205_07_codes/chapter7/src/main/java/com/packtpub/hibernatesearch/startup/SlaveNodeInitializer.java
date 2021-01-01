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
 * A helper class for server instances intended to run as a "slave" node in a cluster.  The master node is the only one which 
 * may directly update the *overall* master Lucene index.  Slave nodes may only only update the overall master index by passing 
 * JMS messages, which the master node is responsible for receiving and processing.
 * 
 * As a ServletContextListener, the servlet container will automatically invoke the "contextInitialized" method upon startup.  This 
 * method loads and indexes about half of the test data set, which has not previously been loaded by the master node (i.e. to demonstrate 
 * updates originating from both node types).  
 * 
 * Previous versions of this class (named "StartupDataLoader") were annotated with @WebListener.  However, this version of the 
 * application relies on "web.xml" defining the appropriate listener... because an application instance should not load both 
 * MasterNodeInitializer *and* SlaveNodeInitializer together.
 */
public class SlaveNodeInitializer implements javax.servlet.ServletContextListener {
	
	Logger logger = LoggerFactory.getLogger(SlaveNodeInitializer.class);
	
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
			configuration.configure("/hibernate-slave.cfg.xml");
			ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
			sessionFactory = configuration.buildSessionFactory(serviceRegistry);
		}
		return sessionFactory.openSession();
	}
	
	public void contextInitialized(ServletContextEvent event) {
		
		event.getServletContext().setAttribute("mode", "slave");

		// For demonstration purposes, we will have about half the data created by the master node... and the other half created by 
		// the slave node.  After a few seconds, both nodes will refresh their local copies of the index using the overall master... and 
		// all of the App entities will be searchable from either node.  
		FullTextSession fullTextSession = Search.getFullTextSession( openSession() );
		fullTextSession.beginTransaction();
		
		//
		// Get references to the 5 devices, which should have already been populated in the database by the master node
		//
		Device xPhone = (Device) fullTextSession.createQuery( "from Device as device where device.name = ?" ).setString(0, "xPhone").uniqueResult();
		Device xTablet = (Device) fullTextSession.createQuery( "from Device as device where device.name = ?" ).setString(0, "xTablet").uniqueResult();
		Device solarSystem = (Device) fullTextSession.createQuery( "from Device as device where device.name = ?" ).setString(0, "Solar System Phone").uniqueResult();
		Device flame = (Device) fullTextSession.createQuery( "from Device as device where device.name = ?" ).setString(0, "Flame Book Reader").uniqueResult();
		Device pc = (Device) fullTextSession.createQuery( "from Device as device where device.name = ?" ).setString(0, "Personal Computer").uniqueResult();
		
		//
		// Create and persist the remaining 6 of 12 apps with devices and customer reviews
		//		
		App frustratedFlamingos = new App(
				"Frustrated Flamingos", 
				"flamingo.jpg", 
				"A fun little game app, where you throw large birds around for no apparent reason.  Why else do you think they're so frustrated?",
				"Games",
				0.99f);
		frustratedFlamingos.setSupportedDevices( new HashSet<Device>(Arrays.asList(new Device[] { xPhone, xTablet, solarSystem, flame, pc })) );
		CustomerReview frustratedFlamingosReview = new CustomerReview("BirdSlinger", 4, "LOL, I love catapulting the flamingos into the cows!  I hate how the advertisement banner hides part of the view, tho.");
		frustratedFlamingos.setCustomerReviews( new HashSet<CustomerReview>(Arrays.asList(new CustomerReview[] { frustratedFlamingosReview })) );
		fullTextSession.save(frustratedFlamingos);
		logger.info("Persisting " + frustratedFlamingos.getName());
		
		App grype = new App(
				"Grype Video Conferencing", 
				"laptop.jpg", 
				"Make free local and international calls, with video, using this app and your home Internet connection.  Better yet, make free calls using your employer's Internet connection!",
				"Internet",
				3.99f);
		grype.setSupportedDevices( new HashSet<Device>(Arrays.asList(new Device[] { xPhone, xTablet, solarSystem, pc })) );
		CustomerReview grypeReview = new CustomerReview("office.casual", 4, "I wish they had not added video to this app in the latest version.  I liked it much more back when I didn't have to get dressed.");
		grype.setCustomerReviews( new HashSet<CustomerReview>(Arrays.asList(new CustomerReview[] { grypeReview })) );
		fullTextSession.save(grype);
		logger.info("Persisting " + grype.getName());
		
		App eReader = new App(
				"E-Book Reader", 
				"book.jpg", 
				"Read books on your computer, or on the go from your mobile device with this powerful e-reader app.  We recommend \"Hibernate Search by Example\", from Packt Publishing.",
				"Media",
				1.99f);
		eReader.setSupportedDevices( new HashSet<Device>(Arrays.asList(new Device[] { xPhone, xTablet, solarSystem, flame, pc })) );
		CustomerReview eReaderReview = new CustomerReview("StevePerkins", 5, "This 'Hibernate Search by Example' book is brilliant!  Thanks for the recommendation!");
		eReader.setCustomerReviews( new HashSet<CustomerReview>(Arrays.asList(new CustomerReview[] { eReaderReview })) );
		fullTextSession.save(eReader);
		logger.info("Persisting " + eReader.getName());
		
		App domeBrowser = new App(
				"Dome Web Browser", 
				"orangeswirls.jpg", 
				"This amazing app allows us to track all of your online activity.  We can figure out where you live, what you had for breakfast this morning, or what your closest secrets are.  The app also includes a web browser.",
				"Internet",
				0);
		domeBrowser.setSupportedDevices( new HashSet<Device>(Arrays.asList(new Device[] { solarSystem, flame, pc })) );
		CustomerReview domeBrowserReview = new CustomerReview("TinFoilHat", 1, "I uninstalled this app.  If the government would fake a moon landing, then they would definately use my browser history to come after me.");
		domeBrowser.setCustomerReviews( new HashSet<CustomerReview>(Arrays.asList(new CustomerReview[] { domeBrowserReview })) );
		fullTextSession.save(domeBrowser);
		logger.info("Persisting " + domeBrowser.getName());
		
		App athenaRadio = new App(
				"Athena Internet Radio", 
				"jamming.jpg", 
				"Listen to your favorite songs on streaming Internet radio!  When you like a song, this app will play more songs similar to that one.  Or at least it plays more songs... to be honest, sometimes they're not all that similar.  :(",
				"Media",
				3.99f);
		athenaRadio.setSupportedDevices( new HashSet<Device>(Arrays.asList(new Device[] { xPhone, xTablet, solarSystem, flame, pc })) );
		CustomerReview athenaRadioReview = new CustomerReview("lskinner", 5, "I requested 'Free Bird', and this app played 'Free Bird'.  What's not to like?");
		athenaRadio.setCustomerReviews( new HashSet<CustomerReview>(Arrays.asList(new CustomerReview[] { athenaRadioReview })) );
		fullTextSession.save(athenaRadio);
		logger.info("Persisting " + athenaRadio.getName());
		
		App mapJourney = new App(
				"Map Journey", 
				"compass.jpg", 
				"Do you need directions to help you reach a destination?  This GPS app will definitely produce enough turn-by-turn directions to get you there!  Eventually.",
				"Travel",
				0.99f);
		mapJourney.setSupportedDevices( new HashSet<Device>(Arrays.asList(new Device[] { xPhone, solarSystem, pc })) );
		CustomerReview mapJourneyReview = new CustomerReview("LostInSpace", 3, "Not great... but still WAY better than Orange maps.");
		mapJourney.setCustomerReviews( new HashSet<CustomerReview>(Arrays.asList(new CustomerReview[] { mapJourneyReview })) );
		fullTextSession.save(mapJourney);
		logger.info("Persisting " + mapJourney.getName());
		
		//
		// Close and cleanup the Hibernate session
		//
		fullTextSession.getTransaction().commit();
		fullTextSession.close();
		
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
