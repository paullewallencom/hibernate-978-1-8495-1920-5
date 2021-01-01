package com.packtpub.hibernatesearch.util;

import org.hibernate.search.engine.BoostStrategy;

import com.packtpub.hibernatesearch.domain.CustomerReview;

/**
 * This class provides dynamic boosting for the CustomerReview persistent object, giving instances 
 * of that class a higher relevance weight when they represent five-star reviews.
 */
public class FiveStarBoostStrategy implements BoostStrategy {

	public float defineBoost(Object value) {
		if(value == null || !(value instanceof CustomerReview)) {
			return 1;
		}
		CustomerReview customerReview = (CustomerReview) value;
		if(customerReview.getStars() == 5) {
			return 1.5f;
		} else {
			return 1;
		}
	}

}
