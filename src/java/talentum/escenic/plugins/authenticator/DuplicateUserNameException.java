package talentum.escenic.plugins.authenticator;

public class DuplicateUserNameException extends RegistrationException {

	public DuplicateUserNameException(String message) {
		super(message);
	}

	public DuplicateUserNameException(String message, Exception e) {
		super(message, e);
	}

}
