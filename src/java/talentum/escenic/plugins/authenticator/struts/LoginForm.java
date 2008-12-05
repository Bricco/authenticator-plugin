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
	String redirectToURL;
	boolean autologin = true;
	
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
	public String getRedirectToURL() {
		return redirectToURL;
	}
	public void setRedirectToURL(String redirectToURL) {
		this.redirectToURL = redirectToURL;
	}
	public boolean isAutologin() {
		return autologin;
	}
	public void setAutologin(boolean autologin) {
		this.autologin = autologin;
	}
	
}
