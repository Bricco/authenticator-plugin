package talentum.escenic.plugins.authenticator.authenticators;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import talentum.escenic.plugins.authenticator.AuthenticationException;
import talentum.escenic.plugins.authenticator.ChangePasswordException;
import talentum.escenic.plugins.authenticator.RegistrationException;
import talentum.escenic.plugins.authenticator.ReminderException;

/**
 * Simple authenticator.
 * 
 * @author stefan.norman
 */
public class SimpleAuthenticator extends Authenticator {

	private static Log log = LogFactory.getLog(SimpleAuthenticator.class);

	private String username;
	private String password;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}


	public AuthenticatedUser authenticate(String username, String password,
			String ipaddress) throws AuthenticationException {
		AuthenticatedUser user = null;
		if (username == null || username.trim().length() == 0
				|| password == null || password.trim().length() == 0) {
			throw new AuthenticationException(
					"Authentication failed: Invalid arguments");
		}

		if(username.equals(this.username) && password.equals(this.password)) {
			user = new SimpleUser(username);
		} else {
			throw new AuthenticationException(
					"Authentication failed: Invalid credentials");
		}

		return user;
	}

	public void logout(String token) {
		// do nothing
	}

	public void passwordReminder(String emailAddress, String publication)
			throws ReminderException {
		// do nothing
	}

	public void register(String username, String password, String postalCode,
			String customerNumber)
			throws RegistrationException {
		// do nothing
	}

	public void changePassword(String username, String oldPassword,
			String newPassword) throws ChangePasswordException {
		// do nothing
	}
}