package com.packtpub.hibernatesearch.util;

import java.util.Calendar;

import org.hibernate.search.store.Workspace;
import org.hibernate.search.store.optimization.impl.IncrementalOptimizerStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NightlyOptimizerStrategy extends IncrementalOptimizerStrategy {

	Logger logger = LoggerFactory.getLogger(NightlyOptimizerStrategy.class); 

	@Override
	public void optimize(Workspace workspace) {
		Calendar calendar = Calendar.getInstance();
		int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
		if(hourOfDay >= 0 && hourOfDay <= 6) {
			super.optimize(workspace);
		}
	}
	
}
