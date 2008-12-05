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

/**
 * Struts action that performs a logout of the current user.
 * 
 * @author stefan.norman
 * 
 */
public class ForgotPasswordAction extends Action {
	private static Log log = LogFactory.getLog(LoginAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		ForgotPasswordForm pwdForm = (ForgotPasswordForm) form;

		String publicationName = (String) request
				.getAttribute("com.escenic.publication.name");

		boolean reminderOK = AuthenticatorManager.getInstance()
				.passwordReminder(publicationName, pwdForm.getEmail());

		if(!reminderOK) {
			return mapping.findForward("failure");
		}
		return mapping.findForward("success");
	}
}
