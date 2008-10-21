package talentum.escenic.plugins.authenticator.authenticators;

import talentum.escenic.plugins.authenticator.AuthenticationException;

/**
 * Implements authentication functionality.
 * 
 * @author stefan.norman
 */
public abstract class Authenticator {

	private String cookieName;
	private String cookieDomain = "";
	private String autoLoginCookieName;

	public String getCookieDomain() {
		return cookieDomain;
	}

	public void setCookieDomain(String cookieDomain) {
		this.cookieDomain = cookieDomain;
	}

	public String getCookieName() {
		return cookieName;
	}

	public void setCookieName(String cookieName) {
		this.cookieName = cookieName;
	}
	
	public String getAutoLoginCookieName() {
		return autoLoginCookieName;
	}

	public void setAutoLoginCookieName(String cookieName) {
		this.autoLoginCookieName = cookieName;
	}
	
	/**
	 * Performs authentication.
	 * 
	 * @param username String the username
	 * @param password String the password
	 * @return a valid user
	 * @throws AuthenticationException if authentication fails
	 */
	public abstract AuthenticatedUser authenticate(String username, String password) throws AuthenticationException;

}
