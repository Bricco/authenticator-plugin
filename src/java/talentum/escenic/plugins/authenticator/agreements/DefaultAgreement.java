package talentum.escenic.plugins.authenticator.agreements;

import java.util.Calendar;
import java.util.HashMap;

import neo.xredsys.api.Section;
import neo.xredsys.content.agreement.AgreementConfig;
import neo.xredsys.content.agreement.AgreementPartner;
import neo.xredsys.content.agreement.AgreementRequest;
import neo.xredsys.content.agreement.AgreementResponse;
import neo.xredsys.presentation.PresentationArticleImpl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import talentum.escenic.plugins.authenticator.AuthenticatorManager;
import talentum.escenic.plugins.authenticator.authenticators.AuthenticatedUser;

/**
 * Implementation of Escenic agreement parther interface. It is used by adding
 * the partner to the AgreementManager and then adding the chosen parner name to
 * a section.
 * 
 * @author stefan.norman
 * 
 */
public class DefaultAgreement implements AgreementPartner {

	private static Log log = LogFactory.getLog(DefaultAgreement.class);

	AgreementConfig config;

	private HashMap urlMap;

	String allowPublishedSection = "ece_tidningen";

	int allowPublishedBeforeWeekday = 4;

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

	public String getAllowPublishedSection() {
		return allowPublishedSection;
	}

	public void setAllowPublishedSection(String allowPublishedSection) {
		this.allowPublishedSection = allowPublishedSection;
	}

	public int getAllowPublishedBeforeWeekday() {
		return allowPublishedBeforeWeekday;
	}

	public void setAllowPublishedBeforeWeekday(int edtionPublishingWeekday) {
		this.allowPublishedBeforeWeekday = edtionPublishingWeekday;
	}

	/**
	 * Handles agreement requests.
	 */
	public void service(AgreementRequest request, AgreementResponse response) {

		String requestedRole = request.getAgreementText();

		if (log.isDebugEnabled()) {
			log.debug("requested role " + requestedRole);
			log
					.debug("article =  "
							+ request
									.getRequestAttribute("com.escenic.context.article"));
		}

		// if the request is for an article and its publishing date is before
		// last publishing weekday
		// we allow the request and bypass the login.
		PresentationArticleImpl article = (PresentationArticleImpl) request
				.getRequestAttribute("com.escenic.context.article");
		if (article != null
				&& article.getHomeSection() != null
				&& article.getHomeSection().getUniqueName().equals(
						getAllowPublishedSection())) {
			Calendar cal = Calendar.getInstance();
			while (cal.get(Calendar.DAY_OF_WEEK) != getAllowPublishedBeforeWeekday()) {
				cal.add(Calendar.DATE, -1);
			}
			if (log.isDebugEnabled()) {
				log.debug("article publishing date: "
						+ article.getPublishedDateAsDate());
				log.debug("last edition publishing date: " + cal.getTime());
			}
			if (article.getPublishedDateAsDate().before(cal.getTime())) {
				return;
			}
			// TODO check "override_agreement" field
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
				// if the user is not found save url in session and redirect to
				// login page
				response.setSessionAttribute("redirectToURL", request.getUrl());
				response.setRedirect(getContextPath(request)
						+ urlMap.get("loginform"));
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

	private String getContextPath(AgreementRequest request) {

		Section sec = request.getSection();
		return sec.getUrl().substring(0,
				sec.getUrl().lastIndexOf(sec.getDirectoryPath()));
	}
}