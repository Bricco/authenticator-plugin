package talentum.escenic.plugins.authenticator.agreements;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;

import neo.xredsys.api.Section;
import neo.xredsys.content.agreement.AgreementConfig;
import neo.xredsys.content.agreement.AgreementPartner;
import neo.xredsys.content.agreement.AgreementRequest;
import neo.xredsys.content.agreement.AgreementResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import talentum.escenic.plugins.authenticator.AuthenticatorManager;
import talentum.escenic.plugins.authenticator.authenticators.AuthenticatedUser;

/**
 * Implementation of Escenic agreement partner interface. It is used by adding
 * the partner to the AgreementManager and then adding the chosen partner name
 * to a section.
 * 
 * @author stefan.norman
 * 
 */
public abstract class DefaultAgreement implements AgreementPartner {

	private static Log log = LogFactory.getLog(DefaultAgreement.class);

	AgreementConfig config;

	private HashMap urlMap;
	
	private String freemiumRole;
	private int freemiumNoOfFreeArticles;
	private int freemiumDaysAsLoggedIn;

	/**
	 * Constructor. It sets up the agreement configuration.
	 * 
	 */
	public DefaultAgreement() {
		urlMap = new HashMap();
		config = new AgreementConfig();
		config.setAuthentication(true);
		String[] cookieNames = AuthenticatorManager.getInstance()
				.getCookieNames();
		for (int i = 0; i < cookieNames.length; i++) {
			config.addCookieName(cookieNames[i]);
		}

		config.addRequestAttributeName("com.escenic.publication.name");
		config.addRequestAttributeName("com.escenic.context");
		config.addRequestAttributeName("com.escenic.context.article");
		config.addRequestAttributeName("authenticatedUser");
		config.addRequestParameterName("showPopup");
	}

	public AgreementConfig getAgreementConfig() {
		return config;
	}

	public void addUrl(String pName, String pUrl) {
		urlMap.put(pName, pUrl);
	}

	public String getFreemiumRole() {
		return freemiumRole;
	}

	public void setFreemiumRole(String freemiumRole) {
		this.freemiumRole = freemiumRole;
	}

	public int getFreemiumNoOfFreeArticles() {
		return freemiumNoOfFreeArticles;
	}

	public void setFreemiumNoOfFreeArticles(int freemiumNoOfFreeArticles) {
		this.freemiumNoOfFreeArticles = freemiumNoOfFreeArticles;
	}

	public int getFreemiumDaysAsLoggedIn() {
		return freemiumDaysAsLoggedIn;
	}

	public void setFreemiumDaysAsLoggedIn(int freemiumDaysAsLoggedIn) {
		this.freemiumDaysAsLoggedIn = freemiumDaysAsLoggedIn;
	}

	/**
	 * Handles agreement requests.
	 */
	public void service(AgreementRequest request, AgreementResponse response) {

		String requestedRole = request.getAgreementText();
		if(request.getRequestParameter("showPopup") !=null) {
			return;
		}
		if (log.isDebugEnabled()) {
			log.debug("requested role(s) " + requestedRole);
			log.debug("article =  "
					+ request
							.getRequestAttribute("com.escenic.context.article"));
		}

		// Check the context and call implementing subclasses
		String context = (String) request
				.getRequestAttribute("com.escenic.context");
		
		if (context.equalsIgnoreCase("art")) {
			Object article = request
					.getRequestAttribute("com.escenic.context.article");
			if (allowArticle(article)) {
				return;
			}
		} else if (context.equalsIgnoreCase("sec")) {
			if (allowSection()) {
				return;
			}
		}

//		// get the user that was set in the filter
//		AuthenticatedUser user = (AuthenticatedUser) request
//				.getRequestAttribute("authenticatedUser");
		
		try {
			URL url = new URL(request.getUrl());
		
			// set headers for Varnish
		
			// which roles are allowed
			response.setHeader("X-Paywall-Roles", requestedRole);
			// where should unauthorized users be redirected
			response.setHeader("X-Paywall-Denied-Url", url.getPath() + "?showPopup=" +
					encodeForUrl((String)urlMap.get("loginform")));
			// where should evicted users be redirected
			response.setHeader("X-Paywall-Rejected-Url", (String)urlMap.get("rejected"));
			// where should users with status passive be redirected
			response.setHeader("X-Paywall-Passive-Url", (String)urlMap.get("passive"));
			
			// if the configured freemium role is set and is required by the requested url,
			// add headers for metered. Only for articles.
//			if(context.equalsIgnoreCase("art") &&
//					getFreemiumRole() != null &&
//					Arrays.asList(splitCommaSeparatedString(requestedRole)).contains(getFreemiumRole())) {
//				// allowed views 
//				response.setHeader("X-Paywall-Metered-Allowed-Views", getFreemiumRole().trim());
//				// set the article ID as identifier
//				Object article = request
//						.getRequestAttribute("com.escenic.context.article");
//				// TODO !!!!
//				response.setHeader("X-Paywall-Metered-Identifier", getFreemiumRole().trim());
//				// TODO change name of header
//				response.setHeader("X-Paywall-Denied-Url", "");
//				// TODO implement 
//				response.setHeader("X-Paywall-Metered-Cookie-Expires", getFreemiumRole().trim());
//			}

		} catch (MalformedURLException e) {
			log.error("Could not create url object from request url.", e);
		}
		
//		if (user == null) {
//
//			String publicationName = (String) request
//					.getRequestAttribute("com.escenic.publication.name");
//
//			// get the token from the cookie and check if user has been evicted
//			String token = request.getCookie(AuthenticatorManager.getInstance()
//					.getCookieName(publicationName));
//			if (token != null
//					&& AuthenticatorManager.getInstance().userHasBeenEvicted(
//							token)) {
//				if (log.isDebugEnabled()) {
//					log.debug("User " + user
//							+ " was rejected, another user was logged in");
//				}
//				// if the user has been evicted and another user is using the
//				// account
//				response.setRedirect(getContextPath(request)
//						+ urlMap.get("rejected"));
//			} else {
//				if (log.isDebugEnabled()) {
//					log.debug("User " + user + " not found");
//				}
//				// if the user is not found redirect to login page
//				response.setRedirect(request.getUrl() + "?showPopup=" +
//						encodeForUrl((String)urlMap.get("loginform")));
//			}
//
//		} else if (requestedRole != null
//				&& !checkFreemium(user, requestedRole)) {
//			
//			if (log.isDebugEnabled()) {
//				log.debug("User " + user + " not authorized for freemium with role(s) "
//						+ requestedRole);
//			}
//			// if user is logged in but not authorized send to unauthorized page
//			response.setRedirect(getContextPath(request)
//					+ urlMap.get("unauthorized"));
//			
//			
//		} else if (requestedRole != null
//				&& user.matchingRoles(splitCommaSeparatedString(requestedRole)).length == 0) {
//
//			if (log.isDebugEnabled()) {
//				log.debug("User " + user + " not authorized for role(s) "
//						+ requestedRole);
//			}
//			// if user is logged in but not authorized send to unauthorized page
//			response.setRedirect(getContextPath(request)
//					+ urlMap.get("unauthorized"));
//
//		} else if (requestedRole != null
//				&& user.hasPassiveStatusForRole(splitCommaSeparatedString(requestedRole))) {
//
//			if (log.isDebugEnabled()) {
//				log.debug("User " + user + " has passive status for role "
//						+ requestedRole);
//			}
//			// if user is logged in but has status passive send to passive page
//			response.setRedirect(getContextPath(request)
//					+ urlMap.get("passive"));
//
//		}

	}

	/**
	 * Check if an article should skip authorization 
	 * 
	 * @param article the article to check
	 * 
	 * @return boolean true if no further authorization should be done
	 */
	public abstract boolean allowArticle(Object article);

	/**
	 * Check if a section should skip authorization 
	 * 
	 * @return boolean true if no further authorization should be done
	 */
	public abstract boolean allowSection();

	protected boolean checkArticleField(Object article, String field_name) {
		if (article != null) {

			// Check "override_agreement" field. Users can allow an article to
			// pass
			// agreement by checking a field in the article.
			String fieldValue = null;
			try {
				fieldValue = (String) article
						.getClass()
						.getMethod("getFieldElement",
								new Class[] { String.class })
						.invoke(article, new Object[] { field_name });
			} catch (Exception e) {
				log.error("Method invocation failed", e);
			}
			if (fieldValue != null) {
				if (Boolean.valueOf(fieldValue).booleanValue()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Check if user is has a valid freemium role
	 * @param user the User
	 * @param requestedRole the requested role(s)
	 * @return true if user has valid freemium role
	 */
	private boolean checkFreemium(AuthenticatedUser user, String requestedRole) {
		// if not freemiumRole is set
		if(getFreemiumRole() == null) {
			return true;
		}
		// if the requested roles does not contain the freemium role
		if(!Arrays.asList(splitCommaSeparatedString(requestedRole)).contains(getFreemiumRole())) {
			return true;
		}
		// if the user has the freemium role
		if(Arrays.asList(user.getRoles()).contains(getFreemiumRole())) {
//			if(getFreemiumDaysAsLoggedIn())
		}
		
		return false;
	}
	
	private String getContextPath(AgreementRequest request) {

		Section sec = request.getSection();
		return sec.getUrl().substring(0,
				sec.getUrl().lastIndexOf(sec.getDirectoryPath()));
	}

	private String encodeForUrl(String s) {
		try {
			s = URLEncoder.encode(s, "iso-8859-1");
		} catch (UnsupportedEncodingException e) {
			log.error("Could not encode string", e);
		}
		return s;
	}
	
	/**
	 * Create a string array
	 * @param s the string to split
	 * @return at least an empty array
	 */
	public static String[] splitCommaSeparatedString(String s){
	    String[] arr = new String[0];
	    if (s != null) {
	    	if(s.indexOf(',') > 0) {
	    		arr = s.trim().split(",");
	    	} else {
	    		arr = new String[] { s.trim() };
	    	}
	    }
	    return arr;
	}
}