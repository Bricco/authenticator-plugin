package talentum.escenic.plugins.authenticator.authenticators;

import java.rmi.RemoteException;

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
				user = populateUser(userSDto);
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
	
	private AuthenticatedUser populateUser(UserStatusDto userSDto) {
		AuthenticatedUser user = new AuthenticatedUser();
		user.setUserId(userSDto.getUserId());
		user.setUserName(userSDto.getUsername());
		user
				.setName(userSDto.getFirstname() + " "
						+ userSDto.getLastname());
		user.setAutologin(userSDto.isAutologin());
		user.setCompanyName(userSDto.getCompanyName());
		user.setEmail(userSDto.getEmail());
		// TODO set Token when Pressdata fix ws
		//user.setToken(userSDto.getToken());
		//user.setStatus(userSDto.getStatus());
		ProductDto[] prDto = userSDto.getProducts();
		for (int i = 0; i < prDto.length; i++) {
			String[] roles = prDto[i].getRoles();
			for (int j = 0; j < roles.length; j++) {
				user.addRole(roles[j].trim());
			}
			user.setToken(prDto[i].getToken());
		}
		
		return user;
	}
}
