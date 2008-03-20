package talentum.escenic.plugins.authenticator;

/**
 * Indicates that another user is using the account.
 * 
 * @author stefan.norman
 */
public class OtherUserLoggedInException extends Exception {

	public OtherUserLoggedInException(String message) {
		super(message);
	}

}
