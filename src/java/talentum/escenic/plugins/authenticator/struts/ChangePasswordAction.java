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
 * Struts action that performs a changing of password.
 * 
 * @author stefan.norman
 * 
 */
public class ChangePasswordAction extends Action {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		ChangePasswordForm pwdForm = (ChangePasswordForm) form;

		String publicationName = (String) request
				.getAttribute("com.escenic.publication.name");

		AuthenticatedUser user = AuthenticatorManager.getInstance()
				.getUserFromCookie(publicationName, request.getCookies());

		if (user == null) {
			return mapping.findForward("failure");
		}

		boolean changeOK = AuthenticatorManager.getInstance().changePassword(
				publicationName, user, pwdForm.getOldPassword(),
				pwdForm.getNewPassword());

		if (!changeOK) {
			return mapping.findForward("failure");
		}
		return mapping.findForward("success");
	}
}
