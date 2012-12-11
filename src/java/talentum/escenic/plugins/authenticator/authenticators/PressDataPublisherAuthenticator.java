package talentum.escenic.plugins.authenticator.authenticators;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;


import net.kundservice.www.prenstatusws.loginservice.LoginServiceLocator;
import net.kundservice.www.prenstatusws.loginservice.LoginServiceSoapStub;
import net.kundservice.www.prenstatusws.loginservice.NTUserStatusDto;
import net.kundservice.www.prenstatusws.loginservice.UserStatusDto;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implements authentication through Pressdata LoginService web service.
 * 
 * @author stefan.norman
 */
public class PressDataPublisherAuthenticator extends WSAuthenticator {

	private static Log log = LogFactory
			.getLog(PressDataPublisherAuthenticator.class);

	private String publisher;

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public AuthenticatedUser performLogin(String username, String password, String ipaddress)
			throws ServiceException, RemoteException {

		AuthenticatedUser user = null;

		// call web service top authenticate
		LoginServiceSoapStub binding = (LoginServiceSoapStub) new LoginServiceLocator()
				.getLoginServiceSoap();

		binding.setTimeout(getTimeout());

		UserStatusDto userSDto = binding.loginPublisher(publisher, username,
				password);

		if (userSDto.isIsLoginOk()) {
			// populate user object
			user = new PressDataUser(userSDto);			
		} else {
			// fallback: check the LoginNTUser for authentication
			NTUserStatusDto ntUserSDto = binding.loginNTUser(publisher, username, password);
			if (ntUserSDto.isLoginOK()) {
				// populate user object
				user = new PressDataUser(ntUserSDto);			
			} else {
				log.error("Authentication failed for user " + username);
			}
		}

		return user;
	}

}
