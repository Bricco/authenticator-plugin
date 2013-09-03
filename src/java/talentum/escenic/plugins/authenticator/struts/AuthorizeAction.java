package talentum.escenic.plugins.authenticator.struts;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import talentum.escenic.plugins.authenticator.AuthenticatorManager;
import talentum.escenic.plugins.authenticator.agreements.DefaultAgreement;
import talentum.escenic.plugins.authenticator.authenticators.AuthenticatedUser;

/**
 * This action is called authorize a user.
 * 
 * @author stefan.norman
 *
 */
public class AuthorizeAction  extends Action {
	
	private static Log log = LogFactory.getLog(AuthorizeAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		String token = request.getParameter("token");
		if(token == null) {
			// missing parameter, return 400
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "parameter token must be provided");
			return null;
		}
		String roles = request.getParameter("roles");
		if(roles == null) {
			// missing parameter, return 400
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "parameter roles must be provided");
			return null;
		}

		if(log.isDebugEnabled()) {
			log.debug("token=" + token);
			log.debug("roles=" + roles);
		}
		
		AuthenticatedUser user = AuthenticatorManager.getInstance().getUser(token);
		if(log.isDebugEnabled()) {
			log.debug("user=" + user);
		}
		if(user == null) {	
			
			if (AuthenticatorManager.getInstance().userHasBeenEvicted(token)) {
				if (log.isDebugEnabled()) {
					log.debug("User " + user.getUserId()
							+ " was rejected, another user was logged in");
				}
				// user was rejected, return 409
				response.sendError(HttpServletResponse.SC_CONFLICT, "user was rejected");
				return null;
			}

			// user not found, return 403
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "user not found");
			return null;
		}
		
		if(log.isDebugEnabled()) {
			log.debug("user.roles=" + Arrays.toString(user.getRoles()));
		}
		if(Collections.disjoint(Arrays.asList(user.getRoles()), Arrays.asList(DefaultAgreement.splitCommaSeparatedString(roles)))) {
			// roles don't match, return 406
			response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, "roles did not match");
			return null;
		} else if (user.hasPassiveStatusForRole(DefaultAgreement.splitCommaSeparatedString(roles))) {

			if (log.isDebugEnabled()) {
				log.debug("User " + user + " has passive status for role "
						+ roles);
			}
			// if user is logged in but has status passive, return 406
			response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, "user has passive status for role");
			return null;
		}
		if(user.getExpirationDate() != null) {
			Date now = new Date();
			if(user.getExpirationDate().before(now)) {
				// user has expired, return 401
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "user is too old");
				return null;
			}
		}
		
		// all is well, return 200
		response.sendError(HttpServletResponse.SC_OK, "roles did match");
		return null;
	}
}
