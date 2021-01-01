package com.packtpub.hibernatesearch.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

/**
 * A simple entity class representing a software application... with a name, full-length description, and 
 * the filename of a image to be used for display.  
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
	@Column(length=1000)
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
	 * Default empty constructor.
	 */
	public App() {}
	
	/**
	 * A convenience constructor for setting an instance's fields in one step.
	 */
	public App(String name, String image, String description) {
		this.name = name;
		this.image = image;
		this.description = description;
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
	
}
