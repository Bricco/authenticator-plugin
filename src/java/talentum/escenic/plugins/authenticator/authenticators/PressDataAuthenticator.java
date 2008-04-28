package talentum.escenic.plugins.authenticator.authenticators;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;

import javax.xml.rpc.ServiceException;

import net.kundservice.www.prenstatusws.login.LoginServiceLocator;
import net.kundservice.www.prenstatusws.login.LoginServiceSoapStub;
import net.kundservice.www.prenstatusws.login.ProductDto;
import net.kundservice.www.prenstatusws.login.UserStatusDto;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import talentum.escenic.plugins.authenticator.AuthenticationException;

/**
 * Implements authentication through Pressdata web service.
 * 
 * @author stefan.norman
 */
public class PressDataAuthenticator implements Authenticator {

	private static Log log = LogFactory.getLog(PressDataAuthenticator.class);

	public AuthenticatedUser authenticate(String username, String password)
			throws AuthenticationException {
		AuthenticatedUser user = null;
		try {
			// call web service top authenticate
			LoginServiceSoapStub binding = (LoginServiceSoapStub) new LoginServiceLocator()
					.getLoginServiceSoap();
			// TODO read product from properties file?
			UserStatusDto userSDto = binding.loginPublisher("AFFVAR", username, password);

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
