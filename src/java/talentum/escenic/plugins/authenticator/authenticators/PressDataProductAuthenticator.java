package talentum.escenic.plugins.authenticator.authenticators;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import net.kundservice.www.WS.Authorization.AuthorizationLocator;
import net.kundservice.www.WS.Authorization.AuthorizationSoapStub;
import net.kundservice.www.WS.Authorization.UserStruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import talentum.escenic.plugins.authenticator.AuthenticationException;

/**
 * Implements authentication through Pressdata Authorization web service.
 * 
 * @author stefan.norman
 */
public class PressDataProductAuthenticator extends Authenticator {

	private static Log log = LogFactory
			.getLog(PressDataProductAuthenticator.class);

	private String product;

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public AuthenticatedUser authenticate(String username, String password)
			throws AuthenticationException {
		AuthenticatedUser user = null;
		try {
			// call web service top authenticate
			AuthorizationSoapStub binding = (AuthorizationSoapStub) new AuthorizationLocator()
					.getAuthorizationSoap();

			UserStruct userStruct = binding.login(username, password, product);

			// populate user object
			user = new PressDataUser(userStruct, product);

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

}
