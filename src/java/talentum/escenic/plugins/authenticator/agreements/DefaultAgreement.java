package talentum.escenic.plugins.authenticator.agreements;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
	}

	public AgreementConfig getAgreementConfig() {
		return config;
	}

	public void addUrl(String pName, String pUrl) {
		urlMap.put(pName, pUrl);
	}

	/**
	 * Handles agreement requests.
	 */
	public void service(AgreementRequest request, AgreementResponse response) {

		String requestedRole = request.getAgreementText();

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

		// get the user that was set in the filter
		AuthenticatedUser user = (AuthenticatedUser) request
				.getRequestAttribute("authenticatedUser");

		if (user == null) {

			String publicationName = (String) request
					.getRequestAttribute("com.escenic.publication.name");

			// get the token from the cookie and check if user has been evicted
			String token = request.getCookie(AuthenticatorManager.getInstance()
					.getCookieName(publicationName));
			if (token != null
					&& AuthenticatorManager.getInstance().userHasBeenEvicted(
							token)) {
				if (log.isDebugEnabled()) {
					log.debug("User " + user
							+ " was rejected, another user was logged in");
				}
				// if the user has been evicted and another user is using the
				// account
				response.setRedirect(getContextPath(request)
						+ urlMap.get("rejected"));
			} else {
				if (log.isDebugEnabled()) {
					log.debug("User " + user + " not found");
				}
				// if the user is not found redirect to login page
				response.setRedirect(getContextPath(request)
						+ urlMap.get("loginform") + "?redirectToURL="
						+ encodeForUrl(request.getUrl()));
			}

		} else if (requestedRole != null && !user.hasRole(splitCommaSeparatedString(requestedRole))) {

			if (log.isDebugEnabled()) {
				log.debug("User " + user + " not authorized for role(s) "
						+ requestedRole);
			}
			// if user is logged in but not authorized send to unauthorized page
			response.setRedirect(getContextPath(request)
					+ urlMap.get("unauthorized"));

		} else if (requestedRole != null
				&& user.hasPassiveStatusForRole(splitCommaSeparatedString(requestedRole))) {

			if (log.isDebugEnabled()) {
				log.debug("User " + user + " has passive status for role "
						+ requestedRole);
			}
			// if user is logged in but has status passive send to passive page
			response.setRedirect(getContextPath(request)
					+ urlMap.get("passive"));

		}

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
	private String[] splitCommaSeparatedString(String s){
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