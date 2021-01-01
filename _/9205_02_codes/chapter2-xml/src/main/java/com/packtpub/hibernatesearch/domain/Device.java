package com.packtpub.hibernatesearch.domain;

import java.io.Serializable;
import java.util.Set;

import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.Field;

/**
 * A simple entity class representing a hardware device... with a name, a manufacturer name, and a collection of 
 * software apps supported on the device.
 * 
 * This variant of Device is mapped to the database by core Hibernate using the classic XML-based approach.  
 * In the Maven project structure, all of the XML files are located under "src/main/resources".  Core Hibernate 
 * automatically examines "hibernate.cfg.xml", which in turn references "com/packtpub/hibernatesearch/domain/Device.hbm.xml"
 * to load the mapping information for this entity.  All of this produces the same end result as @Entity annotation 
 * used in the main version of this application.
 * 
 * Because this class is NOT annotated with @Indexed, Hibernate Search will not create a Lucene index just for it.  
 * Any information indexed for this class will be stored in the Lucene index of a containing entity (i.e. App).
 */
public class Device implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * A primary key configured for automatic generation in "Device.hbm.xml", so there is no need to populate it when creating 
	 * new instances of this class.
	 * 
	 * Because this entity class will not have its own Lucene index, the Hibernate Search @DocumentId annotation is not necessary.
	 */
    private Long id;
	
	/**
	 * The name of the device manufacturer.  The @Column annotation is replaced by information in "Device.hbm.xml", but 
	 * the @Field annotation is still used to tell Hibernate Search to map it as a searchable field in the Lucene index
	 * of any associated entity (e.g. App).
	 */	
	@Field
    private String manufacturer;
	
	/**
	 * The name of the device itself.  The @Column annotation is replaced by information in "Device.hbm.xml", but 
	 * the @Field annotation is still used to tell Hibernate Search to map it as a searchable field in the Lucene index
	 * of any associated entity (e.g. App).
	 */	
	@Field
    private String name;
	
	/**
	 * A collection of associated App entities, representing the software apps supported on this device.  
	 * 
	 * The core Hibernate mapping takes place in "Device.hbm.xml" rather than through annotations, using a "<many-to-many>"
	 * XML element rather than @ManyToMany.  However, the same information about fetching and cascading that is discussed 
	 * in the "chapter2" comments is applicable here also.
	 * 
	 * The Hibernate Search @ContainedIn annotation serves as the counterpart to @IndexedEmbedded (applied 
	 * to App.supportedDevices).  This tells Hibernate Search to include information about contained devices in the 
	 * Lucene index for App. 
	 */
	@ContainedIn
	private Set<App> supportedApps;

	/**
	 * Default empty constructor.
	 */	
	public Device() {
	}
	
	/**
	 * A convenience constructor for setting an instance's fields in one step.  
	 */
	public Device(String manufacturer, String name, Set<App> supportedApps) {
		this.manufacturer = manufacturer;
		this.name = name;
		this.supportedApps = supportedApps;
	}
	
	//
	// GETTERS AND SETTERS
	//
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<App> getSupportedApps() {
		return supportedApps;
	}

	public void setSupportedApps(Set<App> supportedApps) {
		this.supportedApps = supportedApps;
	}

}
