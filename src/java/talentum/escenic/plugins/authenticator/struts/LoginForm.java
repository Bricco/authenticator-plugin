package talentum.escenic.plugins.authenticator.struts;

import org.apache.struts.action.ActionForm;

/**
 * Struts action form used for logging in.
 * 
 * @author stefan.norman
 *
 */
public class LoginForm extends ActionForm {

	String username;
	String password;
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
}
