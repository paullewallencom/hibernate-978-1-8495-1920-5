package com.packtpub.hibernatesearch.domain;

import org.hibernate.search.annotations.Field;

/**
 * An embeddable object (or "component" in traditional Hibernate jargon), resenting a customer review for a software 
 * app.  A review consists of the username for the person submitting the review, the number of "stars" (out of a 
 * possible 5) the app was rated, and any comments from the reviewer.
 * 
 * This variant of App is mapped to the database by core Hibernate using the classic XML-based approach.  
 * In the Maven project structure, all of the XML files are located under "src/main/resources".  Core Hibernate 
 * automatically examines "hibernate.cfg.xml", which in turn references "com/packtpub/hibernatesearch/domain/App.hbm.xml"
 * to load the mapping information the App entity (which contains CustomerReview object instances).  Because CustomerReview 
 * is not a full entity in its own right, it does not have an "hbm.xml" file of its own.  Core Hibernate configures it using
 * a "<composite-element>" XML element in the mapping file of any container entity (e.g. "App.hbm.xml").  
 * 
 * The lifecycle of these objects is dependant on that of their containing entity.  In other words, when an app is deleted, 
 * all of its customer reviews will be deleted also.
 * 
 * Because this embeddable class is NOT annotated with @Indexed, Hibernate Search will not create a Lucene index 
 * just for it.  Any information indexed for this class will be stored in the Lucene index of a containing entity (i.e. App).
 */
public class CustomerReview {

	/**
	 * The username of the person submitting the review.  The Hibernate Search @Field annotation is used to map the field 
	 * in the Lucene index for any containing entity (e.g. App).
	 */	
	@Field
    private String username;

	/**
	 * The rating given, on a scale of five (e.g. "4 out of 5 stars").  This variable is not intended to be searchable 
	 * directly, so it doesn't need a @Field annotation.
	 */
	private int stars;
	
	/**
	 * Free-form comments about the software app.  The Hibernate Search @Field annotation is used to map the field in 
	 * the Lucene index for any containing entity (e.g. App).
	 */	
	@Field
	private String comments;

	/**
	 * Default empty constructor.
	 */
	public CustomerReview() {
	}
	
	/**
	 * A convenience constructor for setting an instance's fields in one step.  
	 */
	public CustomerReview(String username, int stars, String comments) {
		this.username = username;
		this.stars = stars;
		this.comments = comments;
	}

	//
	// GETTERS AND SETTERS
	//
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getStars() {
		return stars;
	}

	public void setStars(int stars) {
		this.stars = stars;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}
	
}
