package talentum.escenic.plugins.authenticator;

/**
 * Indicates that something went wrong when trying to change password.
 * 
 * @author stefan.norman
 */
public class ChangePasswordException extends Exception {

	public ChangePasswordException(String message) {
		super(message);
	}
	public ChangePasswordException(String message, Exception e) {
		super(message, e);
	}

}
