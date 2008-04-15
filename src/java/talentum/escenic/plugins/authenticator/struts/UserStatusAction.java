package talentum.escenic.plugins.authenticator.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class UserStatusAction  extends Action {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		if(request.getAttribute("authenticatedUser") == null) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return null;
		}
		return mapping.findForward("status");
	}
}
