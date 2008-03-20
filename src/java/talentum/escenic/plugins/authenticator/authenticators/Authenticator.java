package talentum.escenic.plugins.authenticator.authenticators;

import talentum.escenic.plugins.authenticator.AuthenticationException;

/**
 * Implements authentication functionality.
 * 
 * @author stefan.norman
 */
public interface Authenticator {

	/**
	 * Performs authentication.
	 * 
	 * @param username String the username
	 * @param password String the password
	 * @return a valid user
	 * @throws AuthenticationException if authentication fails
	 */
	public AuthenticatedUser authenticate(String username, String password) throws AuthenticationException;
	
}
