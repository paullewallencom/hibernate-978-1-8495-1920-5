package com.packtpub.hibernatesearch.util;

import java.util.Arrays;
import java.util.HashSet;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.packtpub.hibernatesearch.domain.App;
import com.packtpub.hibernatesearch.domain.CustomerReview;
import com.packtpub.hibernatesearch.domain.Device;

/**
 * A helper class, used to populate the embedded H2 database with test data at startup.  Because of the 
 * Servlet 3.0 @WebListener annotation, the "contextInitialized" method will be invoked automatically when 
 * the servlet container launches.
 */
@WebListener
public class StartupDataLoader implements javax.servlet.ServletContextListener {
	
	Logger logger = LoggerFactory.getLogger(StartupDataLoader.class);

	/**
	 * Should not be accessed directly.  Use openSession().
	 */
	private static EntityManagerFactory entityManagerFactory;
	
	/**
	 * Constructing a new JPA EntityManagerFactory for every request would cause very poor performance.  However, 
	 * Java servlets must be thread-safe, so we can't use a EntityManagerFactory as an instance variable.  This method provides 
	 * thread-safe access to a EntityManagerFactory, so the startup data loader and the search servlet can open JPA entity
	 * managers more efficiently.
	 * 
	 * @return Session
	 */
	public static synchronized EntityManager createEntityManager() {
		if(entityManagerFactory == null) {
			entityManagerFactory = Persistence.createEntityManagerFactory("com.packtpub.hibernatesearch.jpa");
		}
		return entityManagerFactory.createEntityManager();
	}
	
	/**
	 * This method is invoked automatically when the servlet engine first starts.
	 */
	public void contextInitialized(ServletContextEvent event) {

		//
		// Create a JPA entity manager and begin a new database transaction
		//
		EntityManager entityManager = createEntityManager();
		entityManager.getTransaction().begin();
		
		//
		// Create 5 devices
		//
		Device xPhone = new Device("Orange", "xPhone", null);
		Device xTablet = new Device("Orange", "xTablet", null);
		Device solarSystem = new Device("Song-Sung", "Solar System Phone", null);
		Device flame = new Device("Jungle", "Flame Book Reader", null);
		Device pc = new Device(null, "Personal Computer", null);
		
		//
		// Create and persist 12 apps with devices and customer reviews
		//
		App theCloud = new App("The Cloud", "cloud.jpg", "The Cloud is a place of magic and wonder.  Businesses run smoothly in the Cloud.  Developers no longer need system administrators, or food and water for that matter.  You can watch television on your tablet device from the comfort of your own sofa, without having to look up at the television.  Download the Cloud app, from the Cloud, and harness this awesome power today!");
		theCloud.setSupportedDevices( new HashSet<Device>(Arrays.asList(new Device[] { xPhone, xTablet })) );
		CustomerReview theCloudReview1 = new CustomerReview("fanboy1984", 5, "This app makes my xPhone even more stylish and trendy!");
		CustomerReview theCloudReview2 = new CustomerReview("anti.hipster", 1, "I don't understand what 'The Cloud' means.  This seems like more of a catchphrase than a new technology or app...");
		theCloud.setCustomerReviews( new HashSet<CustomerReview>(Arrays.asList(new CustomerReview[] { theCloudReview1, theCloudReview2 })) );
		entityManager.persist(theCloud);
		logger.info("Persisting " + theCloud.getName());

		App salesCloser = new App("Sales Closer", "pointing.jpg", "A high-powered productivity app for high-powered sales professionals.  Track your high-powered leads, and manage your high-powered schedule.  When you are out on the town doing high-powered networking, you want to show your high-powered sales prospects that you are high-powered too.");
		salesCloser.setSupportedDevices( new HashSet<Device>(Arrays.asList(new Device[] { xPhone, solarSystem })) );
		CustomerReview salesCloserReview = new CustomerReview("ShowMeTheMoney", 5, "Great app!  If you have used 'Sales Commander 2000' before, then this interface will feel familiar.");
		salesCloser.setCustomerReviews( new HashSet<CustomerReview>(Arrays.asList(new CustomerReview[] { salesCloserReview })) );
		entityManager.persist(salesCloser);
		logger.info("Persisting " + salesCloser.getName());

		App football = new App("World Tournament Football", "ball.jpg", "This game app offers all the excitement of football (soccer), except that it's played on a touch screen rather than your feet.  So there isn't any of the kicking, or the running, or any of the physical exercise at all.  Other than that, it's pretty much the same.");
		football.setSupportedDevices( new HashSet<Device>(Arrays.asList(new Device[] { xTablet, flame })) );
		CustomerReview footballReview = new CustomerReview("RealAmerican", 2, "False advertising... I though this was supposed to be football, but it's a SOCCER game instead.");
		football.setCustomerReviews( new HashSet<CustomerReview>(Arrays.asList(new CustomerReview[] { footballReview })) );
		entityManager.persist(football);
		logger.info("Persisting " + football.getName());
		
		App crystal = new App("Yet Another Crystal Game", "brilliant.jpg", "A dazzling game app, in which you connect crystals of the same color to make them go away.  It's sort of like Tetris.  Actually, it's sort of like the other dozen or so other games today where you connect crystals of the same color.");
		crystal.setSupportedDevices( new HashSet<Device>(Arrays.asList(new Device[] { flame, pc })) );
		CustomerReview crystalReview = new CustomerReview("YetAnotherGamer", 3, "Why is this only supported on two devices?  The other dozen clones of this game are available on all devices.  You should really make this app inactive until more devices are supported...");
		crystal.setCustomerReviews( new HashSet<CustomerReview>(Arrays.asList(new CustomerReview[] { crystalReview })) );
		crystal.setActive(false);
		entityManager.persist(crystal);
		logger.info("Persisting " + crystal.getName());
		
		App pencilSharpener = new App("Pencil Sharpener", "pencil.jpg", "Sharpen your pencils, by sticking them into your phone's Bluetooth plug and pushing a button.  This app really pushes your phone's hardware to its limits!");
		pencilSharpener.setSupportedDevices( new HashSet<Device>(Arrays.asList(new Device[] { xPhone, solarSystem })) );
		CustomerReview pencilSharpenerReview1 = new CustomerReview("missing.digits", 1, "Ouch, this app is a menace!  I should sue.");
		CustomerReview pencilSharpenerReview2 = new CustomerReview("LawyerGuy", 5, "@missing.digits:  Private message me.  Let's talk...");
		pencilSharpener.setCustomerReviews( new HashSet<CustomerReview>(Arrays.asList(new CustomerReview[] { pencilSharpenerReview1, pencilSharpenerReview2 })) );
		entityManager.persist(pencilSharpener);
		logger.info("Persisting " + pencilSharpener.getName());
		
		App staplerTracker = new App("Stapler Tracker", "stapler.jpg", "Is someone always taking your stapler?  It's a common problem in many office spaces.  This business productivity app will help you manage your stapler at all times, so that you will never have to deal with a \"case of the Mondays\" again.");
		staplerTracker.setSupportedDevices( new HashSet<Device>(Arrays.asList(new Device[] { pc })) );
		CustomerReview staplerTrackerReview = new CustomerReview("mike.bolton", 3, "'PC LOAD LETTER'?  What does that mean?!?");
		staplerTracker.setCustomerReviews( new HashSet<CustomerReview>(Arrays.asList(new CustomerReview[] { staplerTrackerReview })) );
		entityManager.persist(staplerTracker);
		logger.info("Persisting " + staplerTracker.getName());
		
		App frustratedFlamingos = new App("Frustrated Flamingos", "flamingo.jpg", "A fun little game app, where you throw large birds around for no apparent reason.  Why else do you think they're so frustrated?");
		frustratedFlamingos.setSupportedDevices( new HashSet<Device>(Arrays.asList(new Device[] { xPhone, xTablet, solarSystem, flame, pc })) );
		CustomerReview frustratedFlamingosReview = new CustomerReview("BirdSlinger", 4, "LOL, I love catapulting the flamingos into the cows!  I hate how the advertisement banner hides part of the view, tho.");
		frustratedFlamingos.setCustomerReviews( new HashSet<CustomerReview>(Arrays.asList(new CustomerReview[] { frustratedFlamingosReview })) );
		entityManager.persist(frustratedFlamingos);
		logger.info("Persisting " + frustratedFlamingos.getName());
		
		App grype = new App("Grype Video Conferencing", "laptop.jpg", "Make free local and international calls, with video, using this app and your home Internet connection.  Better yet, make free calls using your employer's Internet connection!");
		grype.setSupportedDevices( new HashSet<Device>(Arrays.asList(new Device[] { xPhone, xTablet, solarSystem, pc })) );
		CustomerReview grypeReview = new CustomerReview("office.casual", 4, "I wish they had not added video to this app in the latest version.  I liked it much more back when I didn't have to get dressed.");
		grype.setCustomerReviews( new HashSet<CustomerReview>(Arrays.asList(new CustomerReview[] { grypeReview })) );
		entityManager.persist(grype);
		logger.info("Persisting " + grype.getName());
		
		App eReader = new App("E-Book Reader", "book.jpg", "Read books on your computer, or on the go from your mobile device with this powerful e-reader app.  We recommend \"Hibernate Search by Example\", from Packt Publishing.");
		eReader.setSupportedDevices( new HashSet<Device>(Arrays.asList(new Device[] { xPhone, xTablet, solarSystem, flame, pc })) );
		CustomerReview eReaderReview = new CustomerReview("StevePerkins", 5, "This 'Hibernate Search by Example' book is brilliant!  Thanks for the recommendation!");
		eReader.setCustomerReviews( new HashSet<CustomerReview>(Arrays.asList(new CustomerReview[] { eReaderReview })) );
		entityManager.persist(eReader);
		logger.info("Persisting " + eReader.getName());
		
		App domeBrowser = new App("Dome Web Browser", "orangeswirls.jpg", "This amazing app allows us to track all of your online activity.  We can figure out where you live, what you had for breakfast this morning, or what your closest secrets are.  The app also includes a web browser.");
		domeBrowser.setSupportedDevices( new HashSet<Device>(Arrays.asList(new Device[] { solarSystem, flame, pc })) );
		CustomerReview domeBrowserReview = new CustomerReview("TinFoilHat", 1, "I uninstalled this app.  If the government would fake a moon landing, then they would definately use my browser history to come after me.");
		domeBrowser.setCustomerReviews( new HashSet<CustomerReview>(Arrays.asList(new CustomerReview[] { domeBrowserReview })) );
		entityManager.persist(domeBrowser);
		logger.info("Persisting " + domeBrowser.getName());
		
		App athenaRadio = new App("Athena Internet Radio", "jamming.jpg", "Listen to your favorite songs on streaming Internet radio!  When you like a song, this app will play more songs similar to that one.  Or at least it plays more songs... to be honest, sometimes they're not all that similar.  :(");
		athenaRadio.setSupportedDevices( new HashSet<Device>(Arrays.asList(new Device[] { xPhone, xTablet, solarSystem, flame, pc })) );
		CustomerReview athenaRadioReview = new CustomerReview("lskinner", 5, "I requested 'Free Bird', and this app played 'Free Bird'.  What's not to like?");
		athenaRadio.setCustomerReviews( new HashSet<CustomerReview>(Arrays.asList(new CustomerReview[] { athenaRadioReview })) );
		entityManager.persist(athenaRadio);
		logger.info("Persisting " + athenaRadio.getName());
		
		App mapJourney = new App("Map Journey", "compass.jpg", "Do you need directions to help you reach a destination?  This GPS app will definitely produce enough turn-by-turn directions to get you there!  Eventually.");
		mapJourney.setSupportedDevices( new HashSet<Device>(Arrays.asList(new Device[] { xPhone, solarSystem, pc })) );
		CustomerReview mapJourneyReview = new CustomerReview("LostInSpace", 3, "Not great... but still WAY better than Orange maps.");
		mapJourney.setCustomerReviews( new HashSet<CustomerReview>(Arrays.asList(new CustomerReview[] { mapJourneyReview })) );
		entityManager.persist(mapJourney);
		logger.info("Persisting " + mapJourney.getName());
		
		//
		// Close and cleanup the JPA entity manager
		//
		entityManager.flush();
		entityManager.getTransaction().commit();
		entityManager.close();
	} 

	/**
	 * This method is invoked automatically when the servlet engine shuts down.  It closes the JPA EntityManagerFactory, if 
	 * it's still open.
	 */
	public void contextDestroyed(ServletContextEvent event) {
		if(entityManagerFactory.isOpen()) {
			entityManagerFactory.close();
		}
	}

}
