package com.packtpub.hibernatesearch.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
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
		
		// Get the user's search keyword(s) from CGI variables
		String searchString = request.getParameter("searchString");
		logger.info("Received searchString [" + searchString + "]");

		// Start a Hibernate session.
		Session session = StartupDataLoader.openSession();
		
		// Create a Hibernate Search wrapper around the vanilla Hibernate session
		FullTextSession fullTextSession = Search.getFullTextSession(session);

		// Begin a transaction.  This may not be strictly necessary, but is a good practice in general.
		fullTextSession.beginTransaction();

		// Create a Hibernate Search QueryBuilder for the appropriate Lucene index (i.e. the index for "App" in this case)
		QueryBuilder queryBuilder = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity( App.class ).get();
		
		// Use the QueryBuilder to construct a Lucene keyword query... matching the user's search keywords against the "name" 
		// and "description" fields of App, as well as "name" field of associated Device entities, and the "comments" field of
		// embedded CustomerReview objects.
		org.apache.lucene.search.Query luceneQuery = queryBuilder
			.keyword()
			.onFields("name", "description", "supportedDevices.name", "customerReviews.comments")
			.matching(searchString)
			.createQuery();
		org.hibernate.Query hibernateQuery = fullTextSession.createFullTextQuery(luceneQuery, App.class);
		
		List<App> apps = hibernateQuery.list();
		logger.info("Found " + apps.size() + " apps");

		// Detach the results from the Hibernate session (to prevent unwanted interaction between the view layer 
		// and Hibernate when associated devices or embedded customer reviews are referenced)
		fullTextSession.clear();

		// Put the search results on the HTTP reqeust object
		request.setAttribute("apps", apps);

		// Close and clean up the Hibernate session
		fullTextSession.getTransaction().commit();
		session.close();
		
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
