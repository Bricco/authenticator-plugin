package talentum.escenic.plugins.authenticator.struts;

import org.apache.struts.action.ActionForm;

/**
 * Struts action form used for logging in.
 * 
 * @author stefan.norman
 *
 */
public class ForgotPasswordForm extends ActionForm {

	String email;
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
}
