package com.packtpub.hibernatesearch.domain.test;

import java.util.Scanner;

import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.plus.webapp.EnvConfiguration;
import org.eclipse.jetty.plus.webapp.PlusConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.FragmentConfiguration;
import org.eclipse.jetty.webapp.JettyWebXmlConfiguration;
import org.eclipse.jetty.webapp.MetaInfConfiguration;
import org.eclipse.jetty.webapp.TagLibConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebInfConfiguration;
import org.eclipse.jetty.webapp.WebXmlConfiguration;
import org.junit.Test;

/**
 * This class boostraps a two-node cluster of Jetty servers, each loading the built contents of this project, and basically 
 * serves as an alternative to the single-node Maven Jetty plugin used in the previous chapters.  
 * 
 * The primary "testCluster()" method is structured as a JUnit 4 test, so that Maven will invoke it automatically when launched 
 * with the goals "clean compile war:exploded test".
 * 
 * The "master" and "slave" nodes will use separate deployment descriptors ("web.xml" and "web-slave.xml", respectively).
 * Each deployment descriptor declares a servlet context listener of the appropriate type (i.e. MasterNodeInitializer or 
 * SlaveNodeInitializer)... which in turn builds a Hibernate SessionFactory using the appropriate Hibernate config 
 * file (i.e. "hibernate.cfg.xml" or "hibernate-slave.cfg.xml").
 * 
 * Depending on the framework you are using (e.g. Spring, JEE, etc), you might use any number of dependency-injection 
 * techniques or other patterns to configure nodes for "master" or "slave" status.  The important thing to know is that, generally 
 * speaking, "hibernate.cfg.xml" (or "persistence.xml" for JPA) drives the differences that Hibernate Search cares about.
 */
public class ClusterTest {

	@Test
	public void testCluster() throws Exception {
		
		String projectBaseDirectory = System.getProperty("user.dir");
		
		//
		// Create master node
		//
		Server masterServer = new Server(8080);

		WebAppContext masterContext = new WebAppContext();
		masterContext.setDescriptor(projectBaseDirectory + "/target/vaporware/WEB-INF/web.xml");
		masterContext.setResourceBase(projectBaseDirectory + "/target/vaporware");
		masterContext.setContextPath("/");
		masterContext.setConfigurations(
				new Configuration[] { 
						new WebInfConfiguration(),
						new WebXmlConfiguration(),
						new MetaInfConfiguration(), 
						new FragmentConfiguration(),
						new EnvConfiguration(),
						new PlusConfiguration(),
						new AnnotationConfiguration(), 
						new JettyWebXmlConfiguration(),
						new TagLibConfiguration()
				}
		);
		masterContext.setParentLoaderPriority(true);

		masterServer.setHandler(masterContext);
		masterServer.start();
		//masterServer.join();

		//
		// Create slave node
		//
		Server slaveServer = new Server(8181);

		WebAppContext slaveContext = new WebAppContext();
		slaveContext.setDescriptor(projectBaseDirectory + "/target/vaporware/WEB-INF/web-slave.xml");
		slaveContext.setResourceBase(projectBaseDirectory + "/target/vaporware");
		slaveContext.setContextPath("/");
		slaveContext.setConfigurations(
				new Configuration[] { 
						new WebInfConfiguration(),
						new WebXmlConfiguration(),
						new MetaInfConfiguration(), 
						new FragmentConfiguration(),
						new EnvConfiguration(),
						new PlusConfiguration(),
						new AnnotationConfiguration(), 
						new JettyWebXmlConfiguration(),
						new TagLibConfiguration()
				}
		);
		slaveContext.setParentLoaderPriority(true);

		slaveServer.setHandler(slaveContext);
		slaveServer.start();
		//slaveServer.join();
		
		// Try to let the user terminate the Jetty server instances gracefully.  This won't work in an environment like Eclipse, if 
		// console input can't be received.  However, even in that that case you will be able to kill the Maven process without 
		// a Java process lingering around (as would be the case if you used "Sever.join()" to pause execution of this thread).
		System.out.println("PRESS <ENTER> TO HALT SERVERS (or kill the Maven process)...");
		Scanner scanner = new Scanner(System.in);
		String line = scanner.nextLine();
		System.out.println(line);
		scanner.close();
		masterServer.stop();
		slaveServer.stop();
		System.out.println("Servers halted");
		
	}

}
