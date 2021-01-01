package com.packtpub.hibernatesearch.domain;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;

/**
 * A simple entity class representing a software application... with a name, full-length description, and 
 * the filename of a image to be used for display.  The class now has an "active" boolean flag, for toggling
 * whether or not the app should be live and searchable.  
 * 
 * The @Entity annotation tells Hibernate to map this class to a database table, while the @Indexed annotation 
 * tells Hibernate Search to map it to a Lucene index.  
 */
@Entity
@Indexed
public class App implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * A primary key configured for automatic generation, so there is no need to populate it when creating 
	 * new instances of this class.
	 */
	@Id
	@GeneratedValue
	private Long id;

	/**
	 * The human-readable name of the application.  The @Column annotation tells Hibernate to map this variable 
	 * to a database table column, while the @Field annotation tells Hibernate Search to map it as a searchable 
	 * field in the Lucene index.
	 */
	@Column
	@Field
	private String name;

	/**
	 * A longer detailed description of the application.  The @Column annotation tells Hibernate to map this variable 
	 * to a database table column, while the @Field annotation tells Hibernate Search to map it as a searchable 
	 * field in the Lucene index.
	 */
	@Column(length = 1000)
	@Field
	private String description;

	/**
	 * The filename of an image to be associated with this application.  The file is expected in exist 
	 * under "images/apps/<image>".  This variable is NOT annotated with @Field, because we don't intend to search 
	 * for App's by the image filenames.
	 */
	@Column
	private String image;
	
	/**
	 * A flag for toggling whether or not the app should be live and searchable.
	 */
	@Column
	private boolean active;

	/**
	 * A collection of associated Device entities, representing the devices on which this app is supported.  
	 * 
	 * The @ManyToMany annotation tells Hibernate that an app can be supported on multiple devices, and a device 
	 * can support multiple apps.  
	 * 
	 *      The "fetch" element is set to "eager" (rather than "lazy") so that all the relevant 
	 *      Device objects will be fetched at once while the Hibernate session is still open.  Otherwise, errors would 
	 *      occur when the view layer tries to access this field after the Hibernate session has been closed.  Eager fetching 
	 *      is less efficient than lazy fetching in most cases, so in a larger-scale application you might refactor this 
	 *      using DAO pattern or some other approach.
	 * 
	 *      The "cascade" element ensures that changes on either side of this App-Device association will properly 
	 *      update the Lucene indexes of both entities.
	 * 
	 * Finally, the @IndexedEmbedded annotation tells Hibernate Search to index this field of associated entities.  
	 * The Device class may or not get its own Lucene index (depending on whether that class is annotated with @Indexed), but 
	 * either way the Device instances associated with an App will have information stored in the Lucene index for App. 
	 */
	@ManyToMany(fetch=FetchType.EAGER, cascade = { CascadeType.ALL })
	@IndexedEmbedded(depth=1)
	private Set<Device> supportedDevices;
	
	/**
	 * A collection of embedded objects, representing customer reviews for this app.  Embedded objects differ from 
	 * associated entities in that the former have a lifecycle dependent upon their container.  In other words... when an 
	 * app is deleted its customer reviews are deleted as well, even though the app's supported devices remain in existence.
	 * 
	 * The @ElementCollection annotation tells Hibernate about this embedded relationship, in a manner similar to the @ManyToMany 
	 * annotation applied to "supportedDevices".  "Eager" fetching for the same reason as with that field.  
	 *
	 * The @Fetch annotation tells Hibernate to retrieve these embedded objects using multiple SELECT statements rather 
	 * than a single massive JOIN (the default).  This mode is inefficient with large data sets... but the number of customer 
	 * reviews per app isn't expected to be large, and this approach avoids a Hibernate quirk that would normally cause "eager" 
	 * fetching of embedded objects to return duplicates 
	 * (http://stackoverflow.com/questions/1093153/hibernate-collectionofelements-eager-fetch-duplicates-elements).  As 
	 * mentioned in the comments for "supportedDevices", in a larger-scale application you might refactor away from the need 
	 * to use "eager" fetching.
	 * 
	 * The @IndexEmbedded annotation here uses the optional "includePaths" element to restrict which embedded object fields
	 * will be indexed with the container entity.  In other words, "comments" will be the ONLY member variable of the CustomerReview 
	 * class that will be included in the Lucene index for App.  
	 */
	@ElementCollection(fetch=FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	@IndexedEmbedded(depth=1, includePaths = { "comments" })
	private Set<CustomerReview> customerReviews;

	/**
	 * Default empty constructor.
	 */
	public App() {
	}

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
