package talentum.escenic.plugins.authenticator;

/**
 * Indicates that a user could not be authenticated.
 * 
 * @author stefan.norman
 */
public class AuthenticationException extends Exception {

	public AuthenticationException(String message) {
		super(message);
	}

}
