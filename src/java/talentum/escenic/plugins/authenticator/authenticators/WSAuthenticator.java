package talentum.escenic.plugins.authenticator.authenticators;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import talentum.escenic.plugins.authenticator.AuthenticationException;

/**
 * Abstract WebService authenticator.
 * 
 * @author stefan.norman
 */
public abstract class WSAuthenticator extends Authenticator {

	private static Log log = LogFactory
		.getLog(WSAuthenticator.class);
	
	private int timeout = 15000;

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public AuthenticatedUser authenticate(String username, String password)
			throws AuthenticationException {
		AuthenticatedUser user = null;
		if (username == null || username.trim().length() == 0
				|| password == null || password.trim().length() == 0) {
			throw new AuthenticationException(
					"Authentication failed: Invalid arguments");
		}
		try {
			
			user = performLogin(username, password);

		} catch (ServiceException e) {
			log.error("Authentication failed: Web Service not found", e);
		} catch (RemoteException e) {
			log.error("Authentication failed: Web Service not available", e);
		}
		if (user == null) {
			throw new AuthenticationException(
					"Authentication failed: User not found");
		}
		return user;
	}

	protected abstract AuthenticatedUser performLogin(String username, String password)
			throws ServiceException, RemoteException;
	

}
