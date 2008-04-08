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
import talentum.escenic.plugins.authenticator.taglib.CookieUtil;

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

		Cookie userDataCookie = null;
		Cookie autologinCookie = null;

		Cookie[] allCookies = request.getCookies();
		for (int i = 0; i < allCookies.length; i++) {
			if (allCookies[i].getName().equals(
					AuthenticatorManager.getInstance().getCookieName())) {
				userDataCookie = allCookies[i];
			} else if (allCookies[i].getName().equals(
					AuthenticatorManager.AUTOLOGIN_COOKIE)) {
				autologinCookie = allCookies[i];
			}
		}
		if (userDataCookie != null) {
			if (log.isInfoEnabled()) {
				log.info("User with token " + userDataCookie.getValue()
						+ " logging out");
			}
			AuthenticatorManager.getInstance().evictUser(
					userDataCookie.getValue());

			response.addCookie(CookieUtil.getRemovalCookie(AuthenticatorManager
					.getInstance().getCookieName(), AuthenticatorManager
					.getInstance().getCookieDomain()));
		}
		if (autologinCookie != null) {
			response.addCookie(CookieUtil.getRemovalCookie(autologinCookie
					.getName(), AuthenticatorManager.getInstance()
					.getCookieDomain()));
		}

		return mapping.findForward("not-authenticated");
	}
}
