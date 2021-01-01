package com.packtpub.hibernatesearch.servlet;

import java.io.IOException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.packtpub.hibernatesearch.domain.App;
import com.packtpub.hibernatesearch.util.StartupDataLoader;

/**
 * A controller/model servlet that processes a search, and renders the search result JSP/JSTL view.
 * 
 * The Servlet 3.0 annotation @WebServlet maps this servlet to the URL "search" (e.g. "http://localhost:8080/search").  With 
 * earlier versions of the Servlet spec, this configuration would belong in the "web.xml" file.  The basic logic of this 
 * search operation could be refactored for an application using Spring, JSF, or any other Java-based application framework. 
 */
@SuppressWarnings("serial")
@WebServlet("search")
public class SearchServlet extends HttpServlet {

	/**
	 * This method contains the primary search functionality for this servlet, and is automatically invoked once for every HTTP
	 * POST to the mapped URL. 
	 */
	@SuppressWarnings("unchecked")
	@Override	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Logger logger = LoggerFactory.getLogger(SearchServlet.class);
		
		// Get the user's search keyword(s).  Get optional parameters for sorting and pagination, or apply default values if those parameters weren't passed.
		String searchString = request.getParameter("searchString") != null ? request.getParameter("searchString").trim() : "";
		String sortField = request.getParameter("sortField") != null ? request.getParameter("sortField").trim() : "relevance";
		int firstResult = request.getParameter("firstResult") != null ? Integer.parseInt(request.getParameter("firstResult")) : 0;
		logger.info("Received searchString [" + searchString + "], sortField [" + sortField + "], and firstResult [" +  firstResult + "]");

		// Start a JPA entity manager.
		EntityManager entityManager = StartupDataLoader.createEntityManager();

		// Create a Hibernate Search wrapper around the vanilla Hibernate session
		FullTextEntityManager fullTextEntityManager = org.hibernate.search.jpa.Search.getFullTextEntityManager(entityManager);

		// Begin a transaction.  This may not be strictly necessary, but is a good practice in general.
		entityManager.getTransaction().begin();

		// Create a Hibernate Search QueryBuilder for the appropriate Lucene index (i.e. the index for "App" in this case)
		QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity( App.class ).get();
		
		// Use the QueryBuilder to construct a Lucene query... matching the user's search keywords against the "name" 
		// and "description" fields of App, as well as "name" field of associated Device entities, and the "comments" field of
		// embedded CustomerReview objects.
		org.apache.lucene.search.Query luceneQuery = null;
		if(searchString.length() > 2 && searchString.startsWith("\"") && searchString.endsWith("\"")) {
			
			// If the user's search string is surrounded by double-quotes, then use a phrase search
			String unquotedSearchString = searchString.substring(1, searchString.length() - 1);
			luceneQuery = queryBuilder
					.phrase()
					.onField("name").andField("description").andField("supportedDevices.name").andField("customerReviews.comments")
					.sentence(unquotedSearchString)
					.createQuery();			
		} else {
			
			// If the user's search string is not quoted, then use a fuzzy keyword search
			luceneQuery = queryBuilder
					.keyword()
					.fuzzy()
					.withThreshold(0.7f)					
					.onFields("name", "description", "supportedDevices.name", "customerReviews.comments")
					.matching(searchString)
					.createQuery();			
		}
		FullTextQuery jpaQuery = fullTextEntityManager.createFullTextQuery(luceneQuery, App.class);  // could be cast to "javax.persistence.Query"

		// Apply optional sort criteria, if not the default sort-by-relevance
		if(sortField.equals("name")) {
			Sort sort = new Sort(new SortField("sorting_name", SortField.STRING));
			jpaQuery.setSort(sort);
		} else if(sortField.equals("name-reverse")) {
			Sort sort = new Sort(new SortField("sorting_name", SortField.STRING, true));
			jpaQuery.setSort(sort);
		}
		
		/*
		 * In the next few steps, some actions are performed on the "org.hibernate.search.jpa.FullTextQuery" object that are 
		 * specific to Hibernate Search, and would not be possible if that object were cast to "javax.persistence.Query" for a
		 * "pure JPA" approach.
		 * 
		 * There are advantages to sticking with shared standards.  However, the JPA specification is more "aspirational" than 
		 * strictly dogmatic.  All of the major JPA implementations offer their own proprietary extensions, which make it difficult 
		 * or impossible to migrate.  Most most non-trivial applications inevitably use some of these extensions.  Hibernate Search 
		 * itself is an example, as there are no comparable Lucene wrappers being actively maintained for EclipseLink or OpenJPA.
		 * 
		 * There is definitely a strong argument to be made for using JPA as much as possible.  Even if you work with different 
		 * implementations on different projects, 90% or more of your skillset will transfer.  However, there is no need to fear 
		 * using extensions offered by a particular implementation... for functionality that is not included in the JPA specification 
		 * and which would be wasteful to code from scratch.
		 */

		// Get the estimated number of results (NOTE: not 100% accurate, but doesn't require a database hit that 
		// might be resource-intensive for large data sets).
		int resultSize = jpaQuery.getResultSize();

		// Perform search... limited to a batch of 5 results, and starting where the last page left off (default to beginning)
		jpaQuery.setFirstResult(firstResult);
		jpaQuery.setMaxResults(5);
		List<App> apps = jpaQuery.getResultList();
		
		// Detach the results from the Hibernate session (to prevent unwanted interaction between the view layer 
		// and Hibernate when associated devices or embedded customer reviews are referenced)
		fullTextEntityManager.clear();

		// Put the search results on the HTTP reqeust object, along with sorting and pagination related paramaters		
		request.setAttribute("searchString", searchString);
		request.setAttribute("sortField", sortField);
		request.setAttribute("apps", apps);
		request.setAttribute("resultSize", resultSize);
		request.setAttribute("firstResult", firstResult);

		// Close and clean up the JPA entity manager
		entityManager.getTransaction().commit();
		entityManager.close();
		
		// Forward the request object (including the search results) to the JSP/JSTL view for rendering
		getServletContext().getRequestDispatcher("/WEB-INF/pages/search.jsp").forward(request, response);
	}

	/**
	 * This method is automatically invoked once for every HTTP GET to the mapped URL.  For this servlet, the same code should 
	 * be executed regardless of whether the request is a POST or GET... so this method simply forwards the request to "doPost()".
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {	
		this.doPost(request, response);
	}
	
}
