package talentum.escenic.plugins.authenticator;

/**
 * Indicates that a user is not found.
 * 
 * @author stefan.norman
 */
public class UserNotFoundException extends Exception {

	public UserNotFoundException(String message) {
		super(message);
	}

}
