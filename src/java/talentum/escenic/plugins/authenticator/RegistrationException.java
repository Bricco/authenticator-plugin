package talentum.escenic.plugins.authenticator;

/**
 * Indicates that something went wrong when trying to register a user.
 * 
 * @author stefan.norman
 */
public class RegistrationException extends Exception {

	public RegistrationException(String message) {
		super(message);
	}

}
