package com.packtpub.hibernatesearch.domain;

import java.io.Serializable;
import java.util.Set;

import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;

/**
 * A simple entity class representing a software application... with a name, full-length description, and 
 * the filename of a image to be used for display.  The class now has an "active" boolean flag, for toggling
 * whether or not the app should be live and searchable.  
 * 
 * This variant of App is mapped to the database by core Hibernate using the classic XML-based approach.  
 * In the Maven project structure, all of the XML files are located under "src/main/resources".  Core Hibernate 
 * automatically examines "hibernate.cfg.xml", which in turn references "com/packtpub/hibernatesearch/domain/App.hbm.xml"
 * to load the mapping information for this entity.  All of this produces the same end result as @Entity annotation 
 * used in the main version of this application.
 * 
 * Although the core Hibernate @Entity annotation is not being used, the configuration of Hibernate Search is still
 * annotation-based.  The @Indexed annotation tells Hibernate Search to map it to a Lucene index.  
 */
@Indexed
public class App implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * A primary key configured for automatic generation in "App.hbm.xml", so there is no need to populate it when creating 
	 * new instances of this class.
	 * 
	 * The @DocumentId annotation tells Hibernate Search to use this as the primary key when indexing entities in Lucene.  
	 * This is not necessary when using annotations to configure the core Hibernate mapping, because Hibernate Search 
	 * can reference the @Id annotation.  However, when using XML to configure core Hibernate, is necessary to apply @DocumentId
	 * explicitly.
	 */
	@DocumentId
    private Long id;

	/**
	 * The human-readable name of the application.  The @Column annotation is replaced by information in "App.hbm.xml", but 
	 * the @Field annotation is still used to tell Hibernate Search to map it as a searchable field in the Lucene index.
	 */
	@Field
    private String name;	
	
	/**
	 * A longer detailed description of the application.  The @Column annotation is replaced by information in "App.hbm.xml", but 
	 * the @Field annotation is still used to tell Hibernate Search to map it as a searchable field in the Lucene index.
	 */
	@Field
    private String description;

	/**
	 * The filename of an image to be associated with this application.  The file is expected in exist 
	 * under "images/apps/<image>".  This variable is NOT annotated with @Field, because we don't intend to search 
	 * for App's by the image filenames.
	 */	
    private String image;

	/**
	 * A flag for toggling whether or not the app should be live and searchable.
	 */
    private boolean active;

	/**
	 * A collection of associated Device entities, representing the devices on which this app is supported.  
	 * 
	 * The core Hibernate mapping takes place in "App.hbm.xml" rather than through annotations, using a "<many-to-many>"
	 * XML element rather than @ManyToMany.  However, the same information about fetching and cascading that is discussed 
	 * in the "chapter2" comments is applicable here also.
	 * 
	 * The @IndexedEmbedded annotation is still used for telling Hibernate Search to index this field of associated entities.  
	 * The Device class may or not get its own Lucene index (depending on whether that class is annotated with @Indexed), but 
	 * either way the Device instances associated with an App will have information stored in the Lucene index for App. 
	 */
    @IndexedEmbedded(depth=1)
    private Set<Device> supportedDevices;

	/**
	 * A collection of embedded objects, representing customer reviews for this app.  Embedded objects differ from 
	 * associated entities in that the former have a lifecycle dependent upon their container.  In other words... when an 
	 * app is deleted its customer reviews are deleted as well, even though the app's supported devices remain in existence.
	 * 
	 * The core Hibernate mapping takes place in "App.hbm.xml" rather than through annotations, using a "<composite-element>"
	 * XML element rather than @ElementCollection.  However, the same information fetching and cascading that is discussed 
	 * in the "chapter2" comments is applicable here also.
	 * 
	 * The @IndexEmbedded Hibernate Search annotation here uses the optional "includePaths" element to restrict which embedded 
	 * object fields will be indexed with the container entity.  In other words, "comments" will be the ONLY member variable of 
	 * the CustomerReview class that will be included in the Lucene index for App.  
	 */
    @IndexedEmbedded(depth=1, includePaths = { "comments" })
	private Set<CustomerReview> customerReviews;

	/**
	 * Default empty constructor.
	 */
	public App() {}

	/**
	 * A convenience constructor for setting an instance's fields in one step.  
	 * 
	 * Although not taken as an input parameter, "active" is set to "true" by default.  That variable's setter method must be 
	 * used to make an App inactive. 
	 */
	public App(String name, String image, String description) {
		this.name = name;
		this.image = image;
		this.description = description;
		this.active = true;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Set<Device> getSupportedDevices() {
		return supportedDevices;
	}

	public void setSupportedDevices(Set<Device> supportedDevices) {
		this.supportedDevices = supportedDevices;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Set<CustomerReview> getCustomerReviews() {
		return customerReviews;
	}

	public void setCustomerReviews(Set<CustomerReview> customerReviews) {
		this.customerReviews = customerReviews;
	}

}
