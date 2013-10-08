package talentum.escenic.plugins.authenticator.agreements;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

import neo.xredsys.api.Publication;
import neo.xredsys.content.agreement.AgreementConfig;
import neo.xredsys.content.agreement.AgreementPartner;
import neo.xredsys.content.agreement.AgreementRequest;
import neo.xredsys.content.agreement.AgreementResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import talentum.escenic.plugins.authenticator.AuthenticatorManager;

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
	private int freemiumDays;

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
		config.addRequestAttributeName("com.escenic.context");
		config.addRequestAttributeName("com.escenic.context.article");
		config.addRequestAttributeName("com.escenic.context.publication");
		config.addRequestAttributeName("authenticatedUser");
		config.addRequestParameterName("showPopup");
		config.addRequestParameterName("service");
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

	public int getFreemiumDays() {
		return freemiumDays;
	}

	public void setFreemiumDays(int freemiumDays) {
		this.freemiumDays = freemiumDays;
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

		try {
			URL url = new URL(request.getUrl());
				
			Publication publication = (Publication) request
					.getRequestAttribute("com.escenic.context.publication");

			// set headers for Varnish
		
			// which roles are allowed
			response.setHeader("X-Paywall-Roles", requestedRole);
			
			// where should unauthorized users be redirected
//			String mobile="";
//			if(request.getRequestParameter("service").equalsIgnoreCase("mobile")){
//				mobile="?service=mobile";
//			}
			
			
			// Alternative configuration - without popup, would be
			// NWT
			// loginUrl=/prenumerera/users/loginform/?redirectionUrl=
			// Talentum
			// loginUrl=?showPopup=/prenumerera/users/loginform/
			
			String loginUrl = (String)urlMap.get("loginform");
			loginUrl = loginUrl.replace("PUBNAME", publication.getName());
			log.debug("loginUrl: " + loginUrl);
			String urlPath = url.getPath();
			log.debug("urlPath1: " + urlPath);
			if(url.getQuery()!=null) {
				urlPath += url.getQuery();
				log.debug("urlPath2: " + urlPath);
			}
			
			// There might be request params in the loginUrl (from for instance BasicAgreement.properties) so we need to consider this for X-Paywall-Denied-Url
			String serviceParam = "";
			if(request.getRequestParameter("service") != null) {
				serviceParam = (loginUrl.indexOf('?') > 0 ? "&" : "?") + "service=" + request.getRequestParameter("service");
			}
			
			
			if(loginUrl.contains("showPopup")){
			  response.setHeader("X-Paywall-Denied-Url", urlPath + loginUrl + serviceParam);
			}
			else{
				String contextPath = "/" + publication.getName();
				// if context path is not empty string and not / remove it.
				// example: the url /nwt/kultur/ has context path /nwt so the url will be /kultur/
				if(contextPath.length() > 1 && urlPath.startsWith(contextPath)) {
					urlPath = urlPath.substring(contextPath.length());
				}
				log.debug("urlPath3: " + urlPath);
				response.setHeader("X-Paywall-Denied-Url", loginUrl + urlPath + serviceParam);	
			}
			
			// When it comes to X-Paywall-Rejected-Url and X-Paywall-Passive-Url we only consider the service param
			serviceParam = "";
			if(request.getRequestParameter("service") != null) {
				serviceParam = "?service=" + request.getRequestParameter("service");
			}

			
			// where should evicted users be redirected
			response.setHeader("X-Paywall-Rejected-Url", (String)urlMap.get("rejected") + serviceParam);
			// where should users with status passive be redirected
			response.setHeader("X-Paywall-Passive-Url", (String)urlMap.get("passive") + serviceParam);
			// where should we authorize users
			response.setHeader("X-Paywall-Authorization-Url", publication.getUrl() + "/authorize.do");
			
			// if the configured freemium role is set and is required by the requested url,
			// add headers for metered. Only for articles.
			if(context.equalsIgnoreCase("art") &&
					getFreemiumRole() != null &&
					Arrays.asList(splitCommaSeparatedString(requestedRole)).contains(getFreemiumRole())) {
				// set the article ID as identifier
				Object article = request
						.getRequestAttribute("com.escenic.context.article");
				int articleID = 0;
				try {
					articleID =((Integer) article
							.getClass()
							.getMethod("getArticleId", null).invoke(article, null)).intValue();
				} catch (Exception e) {
					log.error("Method invocation failed", e);
				}
				if(articleID > 0) {
					// allowed views 
					response.setHeader("X-Paywall-Metered-Allowed-Views", String.valueOf(getFreemiumNoOfFreeArticles()));
					// set articleId as identifier
					response.setHeader("X-Paywall-Metered-Identifier", String.valueOf(articleID));
					// set denied URL for redirecting user when he/she has no more clicks
					response.setHeader("X-Paywall-Metered-Denied-Url", url.getPath() + "?showPopup=" +
							(String)urlMap.get("freemium_no_more_clicks"));
					// set expires for metered cookie to configured number of days from now
					Calendar cal = Calendar.getInstance();
					// get number of days from config
					cal.add(Calendar.DATE, getFreemiumDays());
					DateFormat df = new SimpleDateFormat("EEE, d-MMM-yyyy HH:mm:ss z", java.util.Locale.UK);

					response.setHeader("X-Paywall-Metered-Cookie-Expires", df.format(cal.getTime()));
				}
			}

		} catch (MalformedURLException e) {
			log.error("Could not create url object from request url.", e);
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

//	private String encodeForUrl(String s) {
//		try {
//			s = URLEncoder.encode(s, "iso-8859-1");
//		} catch (UnsupportedEncodingException e) {
//			log.error("Could not encode string", e);
//		}
//		return s;
//	}
	
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