package talentum.escenic.plugins.authenticator.agreements;

import java.util.Calendar;

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

public class AFVAgreement implements AgreementPartner {

	private static Log log = LogFactory.getLog(AFVAgreement.class);

	AgreementConfig config;

	String loginURL;
	String unauthorizedURL;
	String allowPublishedRole = "T";
	int allowPublishedBeforeWeekday = 4;
	
	public AFVAgreement() {
		config = new AgreementConfig();
		config.setAuthentication(true);
		config.addCookieName(AuthenticatorManager.getInstance().getCookieName());
		config.addRequestAttributeName("com.escenic.context.article");
	}

	public AgreementConfig getAgreementConfig() {
		return config;
	}

	public String getLoginURL() {
		return loginURL;
	}

	public void setLoginURL(String loginURL) {
		this.loginURL = loginURL;
	}

	public String getUnauthorizedURL() {
		return unauthorizedURL;
	}

	public void setUnauthorizedURL(String unauthorizedURL) {
		this.unauthorizedURL = unauthorizedURL;
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

	public void service(AgreementRequest request, AgreementResponse response) {

		String requestedRole = request.getAgreementText();

		if(log.isDebugEnabled()) {
			log.debug("requested role " + requestedRole);
			log.debug("article =  " + request.getRequestAttribute("com.escenic.context.article"));
		}

		// if the request is for an article and its publishing date is before last publishing weekday
		// we allow the request and bypass the login.
		PresentationArticleImpl article = (PresentationArticleImpl)request.getRequestAttribute("com.escenic.context.article");
		if(article != null && requestedRole.equals(getAllowPublishedRole())) {
			Calendar cal = Calendar.getInstance();
			while(cal.get(Calendar.DAY_OF_WEEK) != getAllowPublishedBeforeWeekday()) {
				cal.add(Calendar.DATE, -1);
			}
			if(log.isDebugEnabled()) {
				log.debug("article publishing date: "+article.getPublishedDateAsDate());
				log.debug("last edition publishing date: "+cal.getTime());
			}
			if(article.getPublishedDateAsDate().before(cal.getTime())) {
				return;
			}
			// TODO check "override_agreement" field
		}

		// check the token from the cookie
		AuthenticatedUser user = AuthenticatorManager.getInstance()
				.getVerifiedUser(
						request.getCookie(AuthenticatorManager.getInstance()
								.getCookieName()));
		if (user == null) {
			if (loginURL == null) {
				log.error("Redirect URL not defined");
			} else {
				// save url in session
				response.setSessionAttribute("redirectToURL", request.getUrl());
				// redirect to login page
				response.setRedirect(getContextPath(request) + loginURL);
			}

		} else if(user.hasRole(requestedRole)) {
			// if user is logged in but is not authorized
			response.setRedirect(getContextPath(request) + unauthorizedURL);
			
		}
	}
	
	private String getContextPath(AgreementRequest request) {
		
		Section sec = request.getSection();
		return  sec.getUrl().substring(0, sec.getUrl().lastIndexOf(sec.getDirectoryPath()));
	}
}