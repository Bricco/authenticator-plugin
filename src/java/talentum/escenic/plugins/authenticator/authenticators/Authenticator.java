package talentum.escenic.plugins.authenticator.authenticators;

import talentum.escenic.plugins.authenticator.AuthenticationException;
import talentum.escenic.plugins.authenticator.ReminderException;

/**
 * Implements authentication functionality.
 * 
 * @author stefan.norman
 */
public abstract class Authenticator {

	private String publicationName;
	private String cookieName;
	private String cookieDomain = "";
	private String autoLoginCookieName;

	public String getPublicationName() {
		return publicationName;
	}

	public void setPublicationName(String publicationName) {
		this.publicationName = publicationName;
	}

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
	 * @param ipaddress String the ip address
	 * @return a valid user
	 * @throws AuthenticationException if authentication fails
	 */
	public abstract AuthenticatedUser authenticate(String username, String password, String ipaddress) throws AuthenticationException;

	/**
	 * Performs authentication using token.
	 * 
	 * @param token String the token
	 * @return a valid user
	 * @throws AuthenticationException if authentication fails
	 */
	public AuthenticatedUser authenticateUsingToken(String token)
		throws AuthenticationException {
		return null;
	}

	/**
	 * Performs logout.
	 * 
	 * @param token String the token
	 */
	public abstract void logout(String token);

	/**
	 * Performs forgot password action.
	 * 
	 * @param token String the token
	 */
	public abstract void passwordReminder(String emailAddress, String publication) throws ReminderException;

}
