package talentum.escenic.plugins.authenticator.struts;

import org.apache.struts.action.ActionForm;

/**
 * Struts action form used for changing password.
 * 
 * @author stefan.norman
 * 
 */
public class ChangePasswordForm extends ActionForm {

	String oldPassword;
	String newPassword;

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

}
