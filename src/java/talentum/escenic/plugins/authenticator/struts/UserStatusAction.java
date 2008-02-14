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

public class UserStatusAction  extends Action {

	private static Log log = LogFactory.getLog(UserStatusAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		String token = request.getParameter("token");
		AuthenticatedUser user = AuthenticatorManager.getInstance().getVerifiedUser(token);
		
		if(user == null) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return null;
		} else {
			request.setAttribute("authenticatedUser", user);
		}
		return mapping.findForward("status");
	}
}
