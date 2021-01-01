package com.packtpub.hibernatesearch.util;

import java.lang.annotation.ElementType;

import org.hibernate.search.annotations.Factory;
import org.hibernate.search.cfg.SearchMapping;

import com.packtpub.hibernatesearch.domain.App;
import com.packtpub.hibernatesearch.domain.CustomerReview;
import com.packtpub.hibernatesearch.domain.Device;

public class SearchMappingFactory {
	
	@Factory
	public SearchMapping getSearchMapping() {
		SearchMapping searchMapping = new SearchMapping();
		
		searchMapping
		.entity(App.class)
			.indexed()
			.property("id", ElementType.METHOD).documentId()
			.property("name", ElementType.METHOD).field()
			.property("description", ElementType.METHOD).field()
			.property("supportedDevices", ElementType.METHOD).indexEmbedded().depth(1)
			.property("customerReviews", ElementType.METHOD).indexEmbedded().depth(1)
			
		.entity(Device.class)
			.property("manufacturer", ElementType.METHOD).field()
			.property("name", ElementType.METHOD).field()
			.property("supportedApps", ElementType.METHOD).containedIn()
		
		.entity(CustomerReview.class)
			.property("stars", ElementType.METHOD).field()
			.property("comments", ElementType.METHOD).field();
		
		return searchMapping;				
	}

}
