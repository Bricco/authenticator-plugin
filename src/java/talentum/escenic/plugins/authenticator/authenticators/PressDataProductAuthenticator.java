package talentum.escenic.plugins.authenticator.authenticators;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import net.kundservice.www.WS.Authorization.AuthorizationLocator;
import net.kundservice.www.WS.Authorization.AuthorizationSoapStub;
import net.kundservice.www.WS.Authorization.UserStruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implements authentication through Pressdata Authorization web service.
 * 
 * @author stefan.norman
 */
public class PressDataProductAuthenticator extends WSAuthenticator {

	private static Log log = LogFactory
			.getLog(PressDataProductAuthenticator.class);

	private String product;
	

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public AuthenticatedUser performLogin(String username, String password, String ipaddress)
			throws ServiceException, RemoteException {
		
		AuthenticatedUser user = null;

		// call web service to authenticate
		AuthorizationSoapStub binding = (AuthorizationSoapStub) new AuthorizationLocator()
				.getAuthorizationSoap();
		
		binding.setTimeout(getTimeout());

		UserStruct userStruct = binding.login(username, password, product);

		if (userStruct.getErrorCode() != 0) {
			log.error("Authentication failed for user " + username
					+ ". Error from web service (error code "
					+ userStruct.getErrorCode() + "): "
					+ userStruct.getErrorDescription());
		} else {
			// populate user object
			user = new PressDataUser(userStruct, product);
		}

		return user;
	}

}
