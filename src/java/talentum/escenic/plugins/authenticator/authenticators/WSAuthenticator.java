package talentum.escenic.plugins.authenticator.authenticators;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import talentum.escenic.plugins.authenticator.AuthenticationException;
import talentum.escenic.plugins.authenticator.ReminderException;

/**
 * Abstract WebService authenticator.
 * 
 * @author stefan.norman
 */
public abstract class WSAuthenticator extends Authenticator {

	private static Log log = LogFactory.getLog(WSAuthenticator.class);

	private int timeout = 15000;

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public AuthenticatedUser authenticate(String username, String password,
			String ipaddress) throws AuthenticationException {
		AuthenticatedUser user = null;
		if (username == null || username.trim().length() == 0
				|| password == null || password.trim().length() == 0) {
			throw new AuthenticationException(
					"Authentication failed: Invalid arguments");
		}
		try {

			user = performLogin(username, password, ipaddress);

		} catch (ServiceException e) {
			log.error("Authentication failed: Web Service not found");
			if(log.isDebugEnabled()) {
				log.debug(e);
			}
		} catch (RemoteException e) {
			log.error("Authentication failed: Web Service not available");
			if(log.isDebugEnabled()) {
				log.debug(e);
			}
		}
		if (user == null) {
			throw new AuthenticationException(
					"Authentication failed: User not found");
		}
		return user;
	}

	public void logout(String token) {

		if (token == null || token.trim().length() == 0) {
			return;
		}
		try {
			performLogout(token);

		} catch (ServiceException e) {
			log.error("Logout failed: Web Service not found: " + e.getMessage());
			if(log.isDebugEnabled()) {
				log.debug(e);
			}
		} catch (RemoteException e) {
			log.error("Logout failed: Web Service not available:" + e.getMessage());
			if(log.isDebugEnabled()) {
				log.debug(e);
			}
		}
	}

	public void passwordReminder(String emailAddress, String publication) throws ReminderException {

		if (emailAddress == null || emailAddress.trim().length() == 0) {
			return;
		}
		boolean reminderOK = false;
		try {
			reminderOK = performPasswordReminder(emailAddress, publication);

		} catch (ServiceException e) {
			log.error("Logout failed: Web Service not found", e);
		} catch (RemoteException e) {
			log.error("Logout failed: Web Service not available", e);
		}
		if (!reminderOK) {
			throw new ReminderException("Password reminder failed");
		}
	}

	protected abstract AuthenticatedUser performLogin(String username,
			String password, String ipaddress) throws ServiceException,
			RemoteException;

	protected void performLogout(String token) throws ServiceException,
			RemoteException {
		// do nothing
	}

	protected boolean performPasswordReminder(String emailAddress, String publication)
			throws ServiceException, RemoteException {
		return true;
	}

}
