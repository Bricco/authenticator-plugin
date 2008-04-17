package talentum.escenic.plugins.authenticator.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import talentum.escenic.plugins.authenticator.AuthenticatorManager;
import talentum.escenic.plugins.authenticator.authenticators.AuthenticatedUser;

/**
 * This action si called from SIX to verify the status of a user.
 * 
 * @author stefan.norman
 *
 */
public class UserStatusAction  extends Action {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		String token = request.getParameter("token");
		AuthenticatedUser user = AuthenticatorManager.getInstance().getUser(token);
		
		if(user == null) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return null;
		} else {
			request.setAttribute("authenticatedUser", user);
		}

		return mapping.findForward("status");
	}
}
