package talentum.escenic.plugins.authenticator.authenticators;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import net.kundservice.www.prenstatusws.login.LoginServiceLocator;
import net.kundservice.www.prenstatusws.login.LoginServiceSoapStub;
import net.kundservice.www.prenstatusws.login.UserStatusDto;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import talentum.escenic.plugins.authenticator.AuthenticationException;

/**
 * Implements authentication through Pressdata LoginService web service.
 * 
 * @author stefan.norman
 */
public class PressDataPublisherAuthenticator extends WSAuthenticator {

	private static Log log = LogFactory.getLog(PressDataPublisherAuthenticator.class);
	
	private String publisher;

	
	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public AuthenticatedUser authenticate(String username, String password)
			throws AuthenticationException {
		AuthenticatedUser user = null;
		try {
			// call web service top authenticate
			LoginServiceSoapStub binding = (LoginServiceSoapStub) new LoginServiceLocator()
					.getLoginServiceSoap();

			binding.setTimeout(getTimeout());

			UserStatusDto userSDto = binding.loginPublisher(publisher, username, password);

			if (!userSDto.isIsLoginOk()) {
				log.error("Authentication failed for user " + username);
			} else {
				// populate user object
				user = new PressDataUser(userSDto);
			}

		} catch (ServiceException e) {
			log.error("Authentication failed: Web Service not found", e);
		} catch (RemoteException e) {
			log.error("Authentication failed: Web Service not available", e);
		}
		if(user == null) {
			throw new AuthenticationException("Authentication failed: User not found");			
		}
		return user;
	}
	

	
}
