package talentum.escenic.plugins.authenticator.struts;

import org.apache.struts.action.ActionForm;

/**
 * Struts action form used for registration.
 * 
 * @author stefan.norman
 *
 */
public class RegistrationForm extends ActionForm {

	String username;
	String password;
	String postalcode;
	String customernumber;
	String redirectToURL;
	
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
	public String getPostalcode() {
		return postalcode;
	}
	public void setPostalcode(String postalcode) {
		this.postalcode = postalcode;
	}
	public String getCustomernumber() {
		return customernumber;
	}
	public void setCustomernumber(String customernumber) {
		this.customernumber = customernumber;
	}
	public String getRedirectToURL() {
		return redirectToURL;
	}
	public void setRedirectToURL(String redirectToURL) {
		this.redirectToURL = redirectToURL;
	}
	
}
