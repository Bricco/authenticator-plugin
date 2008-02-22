package talentum.escenic.plugins.authenticator.struts;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import talentum.escenic.plugins.authenticator.AuthenticatorManager;
import talentum.escenic.plugins.authenticator.agreements.AFVAgreement;

/**
 * Struts action that performs a logout of the current user.
 * 
 * @author stefan.norman
 * 
 */
public class LogoutAction extends Action {
	private static Log log = LogFactory.getLog(LoginAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Cookie cookie = null;
		
		Cookie[] allCookies = request.getCookies();
		for (int i = 0; i < allCookies.length; i++) {
			if(allCookies[i].getName().equals(AFVAgreement.COOKIE_NAME)) {
				cookie = allCookies[i];
			}
		}
		if (cookie != null) {
			if (log.isInfoEnabled()) {
				log.info("User with token " + cookie.getValue() + " logging out");
			}
			AuthenticatorManager.getInstance().evictUser(cookie.getValue());
			
			cookie.setValue("");
			cookie.setMaxAge(0);
			response.addCookie(cookie);
		}

		return mapping.findForward("not-authenticated");
	}
}
