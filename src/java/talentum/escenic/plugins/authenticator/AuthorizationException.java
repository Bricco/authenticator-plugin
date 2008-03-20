package talentum.escenic.plugins.authenticator;

/**
 * Indicates that a user has insufficient permissions.
 * 
 * @author stefan.norman
 */
public class AuthorizationException extends Exception {

	public AuthorizationException(String message) {
		super(message);
	}

}
