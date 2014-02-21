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
import talentum.escenic.plugins.authenticator.DuplicateUserNameException;
import talentum.escenic.plugins.authenticator.RegistrationException;
import talentum.escenic.plugins.authenticator.authenticators.AuthenticatedUser;

/**
 * Struts action that performs a login. It uses the AuthenticatorManager
 * configured in
 * localconfig/talentum/escenic/plugins/authenticator/AuthenticatorManager
 * .properties
 * 
 * @author stefan.norman
 * 
 */
public class RegistrationAction extends Action {

	private static Log log = LogFactory.getLog(RegistrationAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		RegistrationForm registrationForm = (RegistrationForm) form;

		String publicationName = (String) request
				.getAttribute("com.escenic.publication.name");


		try {
			// perform the actual registration
			AuthenticatorManager.getInstance().register(publicationName,
					registrationForm.getUsername(),
					registrationForm.getPassword(),
					registrationForm.getPostalcode(),
					registrationForm.getCustomernumber());
		} catch (DuplicateUserNameException dune) {
			return mapping.findForward("duplicateusername");
		} catch (RegistrationException re) {
			return mapping.findForward("failure");
		}

		// perform the authentication to automatically login
		AuthenticatedUser user = AuthenticatorManager.getInstance()
				.authenticate(publicationName,
						registrationForm.getUsername(),
						registrationForm.getPassword(),
						AuthenticatorManager.getRemoteAddress(request));

		if (user != null) {

			Cookie sessionCookie = AuthenticatorManager.getInstance()
					.getSessionCookie(publicationName, user.getToken());
			response.addCookie(sessionCookie);

			// // user checked autologin
			// if (registationForm.isAutologin()) {
			// Cookie autoCookie = AuthenticatorManager.getInstance()
			// .getAutologinCookie(publicationName,
			// registationForm.getUsername(),
			// registationForm.getPassword());
			// response.addCookie(autoCookie);
			// }

			if (log.isInfoEnabled()) {
				log.info("User with token " + user.getToken()
						+ " and email " + user.getEmail() + " logged in");
			}

			// redirect to page found in form
			if (registrationForm.getRedirectToURL() != null
					&& registrationForm.getRedirectToURL().trim().length() > 0) {
				if (log.isDebugEnabled()) {
					log.debug("Redirecting user " + user.getName() + " to "
							+ registrationForm.getRedirectToURL());
				}
				return new ActionForward(
						registrationForm.getRedirectToURL(), true);
			}

			return mapping.findForward("authenticated");
		}
		return mapping.findForward("not-authenticated");
	}
}
