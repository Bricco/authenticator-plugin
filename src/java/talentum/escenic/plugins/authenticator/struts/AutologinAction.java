package talentum.escenic.plugins.authenticator.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import talentum.escenic.plugins.authenticator.AuthenticatorManager;
import talentum.escenic.plugins.authenticator.authenticators.AuthenticatedUser;

/**
 * This action is called from an autologin action.
 * 
 * @author Samuel Eriksson
 * 
 */
public class AutologinAction extends Action {

	private static Log log = LogFactory.getLog(AutologinAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		String cookieValue = request.getParameter("cookieValue");
		if (cookieValue == null) {
			// missing parameter, return 400
			response.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"parameter cookieValue must be provided");
			return null;
		}

		if (log.isDebugEnabled()) {
			log.debug("cookieValue=" + cookieValue);
		}

		String publicationName = (String) request
				.getAttribute("com.escenic.publication.name");

		AuthenticatedUser user = AuthenticatorManager.getInstance()
				.authenticateAuto(publicationName, cookieValue,
						AuthenticatorManager.getRemoteAddress(request));
		if (user != null) {
			response.setHeader("X-Autologin-Response", user.getToken());
		} else {
			response.sendError(HttpServletResponse.SC_FORBIDDEN,
					"user not found");
			return null;
		}

		// all is well, return 200
		response.sendError(HttpServletResponse.SC_OK, "user was found");
		return null;
	}
}
