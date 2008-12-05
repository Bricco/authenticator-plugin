package talentum.escenic.plugins.authenticator;

/**
 * Indicates that something went wrong when trying to send password reminder.
 * 
 * @author stefan.norman
 */
public class ReminderException extends Exception {

	public ReminderException(String message) {
		super(message);
	}

}
