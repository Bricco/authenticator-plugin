package talentum.escenic.plugins.authenticator.authenticators;

import talentum.escenic.plugins.authenticator.RegistrationException;

public class UserRejectedException extends RegistrationException {

	public UserRejectedException(String message) {
		super(message);
	}

	public UserRejectedException(String message, Exception e) {
		super(message, e);
	}

}
