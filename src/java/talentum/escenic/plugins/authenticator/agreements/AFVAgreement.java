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
import talentum.escenic.plugins.authenticator.AuthorizationException;
import talentum.escenic.plugins.authenticator.OtherUserLoggedInException;
import talentum.escenic.plugins.authenticator.UserNotFoundException;

/**
 * Implementation of Escenic agreement parther interface.
 * It is used by adding the partner to the AgreementManager and
 * then adding the chosen parner name to a section.
 * 
 * @author stefan.norman
 *
 */
public class AFVAgreement implements AgreementPartner {

	private static Log log = LogFactory.getLog(AFVAgreement.class);

	AgreementConfig config;

	private HashMap urlMap;

	String allowPublishedRole = "T";

	int allowPublishedBeforeWeekday = 4;

	/**
	 * Constructor.
	 * It sets up the agreement configuration.
	 *
	 */
	public AFVAgreement() {
		urlMap = new HashMap();
		config = new AgreementConfig();
		config.setAuthentication(true);
		config
				.addCookieName(AuthenticatorManager.getInstance()
						.getCookieName());
		config.addRequestAttributeName("com.escenic.context.article");
	}

	public AgreementConfig getAgreementConfig() {
		return config;
	}

	public void addUrl(String pName, String pUrl) {
		urlMap.put(pName, pUrl);
	}

	public String getAllowPublishedRole() {
		return allowPublishedRole;
	}

	public void setAllowPublishedRole(String allowPublishedRole) {
		this.allowPublishedRole = allowPublishedRole;
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
		if (article != null && requestedRole != null && requestedRole.equals(getAllowPublishedRole())) {
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

		// get the token from the cookie
		String token = request.getCookie(AuthenticatorManager.getInstance()
				.getCookieName());
		try {
			AuthenticatorManager.getInstance().getVerifiedUser(token,
					requestedRole);

		} catch (UserNotFoundException e) {
			if(log.isDebugEnabled()) {
				log.debug("User not found", e);
			}
			// save url in session
			response.setSessionAttribute("redirectToURL", request.getUrl());
			// redirect to login page
			response.setRedirect(getContextPath(request)
					+ urlMap.get("loginform"));

		} catch (AuthorizationException e) {
			if(log.isDebugEnabled()) {
				log.debug("User not authorized", e);
			}
			// if user is logged in but is not authorized
			response.setRedirect(getContextPath(request)
					+ urlMap.get("unauthorized"));

		} catch (OtherUserLoggedInException e) {
			if(log.isDebugEnabled()) {
				log.debug("Other user is logged in", e);
			}
			// if the user has been evicted and another user is using the account
			response.setRedirect(getContextPath(request)
					+ urlMap.get("rejected"));
		}

	}

	private String getContextPath(AgreementRequest request) {

		Section sec = request.getSection();
		return sec.getUrl().substring(0,
				sec.getUrl().lastIndexOf(sec.getDirectoryPath()));
	}
}