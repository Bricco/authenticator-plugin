package talentum.escenic.plugins.authenticator.agreements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

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
	List allowedRoles;
	int edtionPublishingWeekday = 4;

	public static final String COOKIE_NAME = "pressdata_user";
	public static final String ROLE_PAPER = "T";
	public static final String ROLE_WEB = "TWE";
	public static final String ROLE_REAL1 = "R1";
	public static final String ROLE_REAL2 = "R2";
	
	public AFVAgreement() {
		allowedRoles = new ArrayList();
		config = new AgreementConfig();
		config.setAuthentication(true);
		config.addCookieName(COOKIE_NAME);
		config.addRequestAttributeName("article");
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

	public void setAllowedRoles(String[] roles) {
		allowedRoles = Arrays.asList(roles);
    }

    public String[] getAllowedRoles() {
        if(allowedRoles == null)
            return new String[0];
        else
            return (String[]) allowedRoles.toArray(new String[allowedRoles.size()]);
    }

	public int getEdtionPublishingWeekday() {
		return edtionPublishingWeekday;
	}

	public void setEdtionPublishingWeekday(int edtionPublishingWeekday) {
		this.edtionPublishingWeekday = edtionPublishingWeekday;
	}

	public void service(AgreementRequest request, AgreementResponse response) {

		//TODO FIIXXXX
		//allowedRoles.contains(request.getAgreementText());
		String agreementText = request.getAgreementText();
		
		// if the request is for an article and its publishing date is before last publishing weekday
		// we allow the request and bypass the login.
		PresentationArticleImpl article = (PresentationArticleImpl)request.getRequestAttribute("article");
		if(article != null && agreementText.equals(ROLE_PAPER)) {
			Calendar cal = Calendar.getInstance();
			while(cal.get(Calendar.DAY_OF_WEEK) != getEdtionPublishingWeekday()) {
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
			if(log.isDebugEnabled()) {
				log.debug("body: "+article.getFieldElement("override_agreement"));
			}
		}

		// check the token from the cookie
		AuthenticatedUser user = AuthenticatorManager.getInstance().getVerifiedUser(request.getCookie(COOKIE_NAME));
		if (user == null) {
			if (loginURL == null) {
				log.error("Redirect URL not defined");
			} else {
				// save url in session
				response.setSessionAttribute("redirectToURL", request.getUrl());
				// redirect to login page
				response.setRedirect(getContextPath(request) + loginURL);
			}

		} else {
			// check if user has at least one of the required roles
			boolean authorized = false;
			String[] allowedRoles = getAllowedRoles();
			for (int i = 0; i < allowedRoles.length; i++) {
				if(user.hasRole(allowedRoles[i])) {
					authorized = true;
				}
			}
			// if user is logged in but is not authorized
			if(!authorized) {
				// redirect to unauthorized page
				response.setRedirect(getContextPath(request) + unauthorizedURL);
			}
			
		}
	}
	
	private String getContextPath(AgreementRequest request) {
		
		Section sec = request.getSection();
		return  sec.getUrl().substring(0, sec.getUrl().lastIndexOf(sec.getDirectoryPath()));
	}
}