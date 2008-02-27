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

/**
 * Struts action that performs a login. It uses the AuthenticatorManager
 * configured in
 * localconfig/talentum/escenic/plugins/authenticator/AuthenticatorManager.properties
 * 
 * @author stefan.norman
 * 
 */
public class LoginAction extends Action {

	private static Log log = LogFactory.getLog(LoginAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		LoginForm loginForm = (LoginForm) form;

		// perform the actual authentication
		Cookie cookie = AuthenticatorManager.getInstance().authenticate(
				loginForm.getUsername(), loginForm.getPassword());

		if (cookie != null) {

			response.addCookie(cookie);

			// user checked autologin
			if (loginForm.isAutologin()) {
				Cookie autoCookie = AuthenticatorManager.getInstance()
						.getAutologinCookie(loginForm.getUsername(),
								loginForm.getPassword());
				response.addCookie(autoCookie);
			}

			if (log.isInfoEnabled()) {
				log.info("User with token " + cookie.getValue() + " logged in");
			}

			// redirect to page found in session
			String redirectToURL = (String) request.getSession().getAttribute(
					"redirectToURL");
			request.getSession().removeAttribute("redirectToURL");

			if (loginForm.getRedirectToURL() != null
					&& loginForm.getRedirectToURL().trim().length() > 0) {
				redirectToURL = loginForm.getRedirectToURL();
			}
			if (redirectToURL != null) {
				return new ActionForward(redirectToURL, true);
			}

			return mapping.findForward("authenticated");
		}

		return mapping.findForward("not-authenticated");
	}
}
