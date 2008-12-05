package talentum.escenic.plugins.authenticator.authenticators;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;
import javax.xml.rpc.holders.StringHolder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.talentumhr.webservices.LoginAccountServiceLocator;
import se.talentumhr.webservices.LoginAccountServiceSoapStub;

/**
 * Implements authentication through Talentum HR LoginAccount web service.
 * 
 * @author stefan.norman
 */
public class TalentumHRLoginAccountAuthenticator extends WSAuthenticator {

	private static Log log = LogFactory
			.getLog(TalentumHRLoginAccountAuthenticator.class);

	private String endPoint;

	private String myPageURL;

	public String getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(String endPoint) {
		this.endPoint = endPoint;
	}

	public String getMyPageURL() {
		return myPageURL;
	}

	public void setMyPageURL(String myPageURL) {
		this.myPageURL = myPageURL;
	}

	public AuthenticatedUser performLogin(String username, String password,
			String ipaddress) throws ServiceException, RemoteException {

		TalentumHRUser user = null;

		StringHolder artefact = new StringHolder();
		StringHolder name = new StringHolder();

		// call web service to authenticate
		int responseCode = getBinding().login(username, password, ipaddress,
				artefact, name);

		if (responseCode < 0) {
			log.error("Authentication failed for user " + username
					+ ". Error from web service (error code " + responseCode
					+ ")");
		} else {
			// populate user object
			user = new TalentumHRUser(artefact.value, name.value, username,
					getMyPageURL());

			if (log.isDebugEnabled()) {
				log.debug("User " + user.getName()
						+ " logged in and got artefact " + user.getToken());
			}
		}

		return user;
	}

	protected void performLogout(String artefact) throws ServiceException,
			RemoteException {

		// call web service to log out
		boolean logoutOK = getBinding().logout(artefact);

		if (!logoutOK) {
			log.error("Logout failed for artefact " + artefact);
		}

	}

	protected boolean performPasswordReminder(String emailAddress)
			throws ServiceException, RemoteException {
		// call web service to log out
		// if method returns 0 everything is ok
		return getBinding().forgotPassword(emailAddress) == 0;

	}

	private LoginAccountServiceSoapStub getBinding() throws ServiceException {

		LoginAccountServiceLocator locator = new LoginAccountServiceLocator();

		// set endpoint from configuration
		locator.setEndpointAddress("LoginAccountServiceSoap", getEndPoint());

		LoginAccountServiceSoapStub binding = (LoginAccountServiceSoapStub) locator
				.getLoginAccountServiceSoap();

		binding.setTimeout(getTimeout());

		return binding;
	}

}
