package talentum.escenic.plugins.authenticator.agreements;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;

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
 * Implementation of Escenic agreement parther interface. It is used by adding
 * the partner to the AgreementManager and then adding the chosen partner name
 * to a section.
 * 
 * @author stefan.norman
 * 
 */
public class DefaultAgreement implements AgreementPartner {

	private static Log log = LogFactory.getLog(DefaultAgreement.class);

	AgreementConfig config;

	private HashMap urlMap;

	int allowPublishedBeforeDays = 0;

	int allowPublishedBeforeWeekday = 0;

	String allowPublishedBeforeTime = "23:59";

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
		config.addRequestAttributeName("com.escenic.context.article");
		config.addRequestAttributeName("authenticatedUser");
	}

	public AgreementConfig getAgreementConfig() {
		return config;
	}

	public void addUrl(String pName, String pUrl) {
		urlMap.put(pName, pUrl);
	}

	public int getAllowPublishedBeforeDays() {
		return allowPublishedBeforeDays;
	}

	public void setAllowPublishedBeforeDays(int allowPublishedBeforeDays) {
		this.allowPublishedBeforeDays = allowPublishedBeforeDays;
	}

	public int getAllowPublishedBeforeWeekday() {
		return allowPublishedBeforeWeekday;
	}

	public void setAllowPublishedBeforeWeekday(int allowPublishedBeforeWeekday) {
		this.allowPublishedBeforeWeekday = allowPublishedBeforeWeekday;
	}

	public String getAllowPublishedBeforeTime() {
		return allowPublishedBeforeTime;
	}

	public void setAllowPublishedBeforeTime(String allowPublishedBeforeTime) {
		this.allowPublishedBeforeTime = allowPublishedBeforeTime;
	}

	/**
	 * Handles agreement requests.
	 */
	public void service(AgreementRequest request, AgreementResponse response) {

		String requestedRole = request.getAgreementText();

		if (log.isDebugEnabled()) {
			log.debug("requested role " + requestedRole);
			log.debug("article =  "
					+ request
							.getRequestAttribute("com.escenic.context.article"));
		}

		// If the request is for an article we allow the request and bypass the
		// login.
		Object article = request
				.getRequestAttribute("com.escenic.context.article");
		if (articleIsAllowed(article)) {
			return;
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

		} else if (requestedRole != null && !user.hasRole(requestedRole)) {

			if (log.isDebugEnabled()) {
				log.debug("User " + user + " not authorized for role "
						+ requestedRole);
			}
			// if user is logged in but not authorized send to unauthorized page
			response.setRedirect(getContextPath(request)
					+ urlMap.get("unauthorized"));

		} else if (requestedRole != null
				&& user.hasPassiveStatusForRole(requestedRole)) {

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
	 * Check an article and its publishing date is before last publishing
	 * weekday. Also check field override.
	 * 
	 * @param article
	 *            Object Using reflection to get by problem in ECE 5 where the
	 *            PresentationArticle class is not available in the shared class
	 *            loader
	 * @return boolean true if checks for publish date and override field pass
	 */
	public boolean articleIsAllowed(Object article) {
		if (article != null) {

			if (getAllowPublishedBeforeDays() > 0) {

				Calendar cal = Calendar.getInstance();
				// first set time
				StringTokenizer tokenizer = new StringTokenizer(
						getAllowPublishedBeforeTime(), ":");
				cal.set(Calendar.HOUR_OF_DAY,
						Integer.parseInt(tokenizer.nextToken()));
				cal.set(Calendar.MINUTE,
						Integer.parseInt(tokenizer.nextToken()));
				// roll calendar back preferred days
				cal.add(Calendar.DATE, (0 - getAllowPublishedBeforeDays()));
				// if configured, roll calendar back to the closest matching
				// weekday
				if (getAllowPublishedBeforeWeekday() > 0) {
					while (cal.get(Calendar.DAY_OF_WEEK) != getAllowPublishedBeforeWeekday()) {
						cal.add(Calendar.DATE, -1);
					}
				}

				Date publishDate = null;
				try {
					publishDate = (Date) article.getClass()
							.getMethod("getPublishedDateAsDate", null)
							.invoke(article, null);
				} catch (Exception e) {
					log.error("Method invocation failed", e);
				}
				if (log.isDebugEnabled()) {
					log.debug("article publishing date: " + publishDate);
					log.debug("configured edition publishing date: "
							+ cal.getTime());
				}
				if (publishDate == null || publishDate.before(cal.getTime())) {
					return true;
				}
			}
			// Check "override_agreement" field. Users can allow an article to
			// pass
			// agreement by checking a field in the article.
			String fieldValue = null;
			try {
				fieldValue = (String) article
						.getClass()
						.getMethod("getFieldElement",
								new Class[] { String.class })
						.invoke(article, new Object[] { "override_agreement" });
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
}