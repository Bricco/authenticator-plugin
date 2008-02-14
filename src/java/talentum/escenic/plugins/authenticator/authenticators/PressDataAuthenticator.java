package talentum.escenic.plugins.authenticator.authenticators;

import net.kundservice.www.prenstatusws.login.LoginServiceLocator;
import net.kundservice.www.prenstatusws.login.LoginServiceSoapStub;
import net.kundservice.www.prenstatusws.login.UserStatusDto;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import talentum.escenic.plugins.authenticator.authenticators.kundservice.ws.AuthorizationLocator;
import talentum.escenic.plugins.authenticator.authenticators.kundservice.ws.AuthorizationSoapStub;
import talentum.escenic.plugins.authenticator.authenticators.kundservice.ws.UserStruct;

public class PressDataAuthenticator implements Authenticator {

	private static Log log = LogFactory.getLog(PressDataAuthenticator.class);

	public AuthenticatedUser authenticate(String username, String password) {
		AuthenticatedUser user = null;
		try {
			// call web service top authenticate
			LoginServiceSoapStub binding = (LoginServiceSoapStub) new LoginServiceLocator()
					.getLoginServiceSoap();
			// TODO read product from properties file?
			UserStatusDto status = binding.loginPublisher("AFFVAR", username, password);

			if (!status.isIsLoginOk()) {
				log.error("Authentication failed for user " + username);
			} else {
				// populate user object
				user = populateUser(struct);
			}

		} catch (Exception e) {
			log.error("authentication failed", e);
		}
		return user;
	}
	
	public AuthenticatedUser verifyUser(String token) {
		AuthenticatedUser user = null;
		try {
			// call web service top authenticate
			AuthorizationSoapStub binding = (AuthorizationSoapStub) new AuthorizationLocator()
					.getAuthorizationSoap();
			// TODO read product from properties file?
			UserStruct struct = binding.verifyUser(token, "AFR");

			if (struct.getErrorCode() > 0) {
				log.error("Verification failed for user with token " + token
						+ ". Error from web service: " + struct.getErrorCode()
						+ " " + struct.getErrorDescription());
			} else {
				// populate user object
				user = populateUser(struct);
			}

		} catch (Exception e) {
			log.error("verification failed", e);
		}
		return user;
	}

	private AuthenticatedUser populateUser(UserStruct struct) {
		AuthenticatedUser user = new AuthenticatedUser();
		user.setUserId(Integer.parseInt(struct.getUserId()));
		user.setUserName(struct.getUserName());
		user
				.setName(struct.getFirstName() + " "
						+ struct.getLastName());
		user.setAutologin(Boolean.getBoolean(struct.getAutoLogin()));
		user.setCompanyName(struct.getCompanyName());
		user.setEmail(struct.getEMail());
		user.setToken(struct.getToken());
		user.setStatus(struct.getStatus());
		user.setRoles(struct.getRoles());
		
		return user;
	}
}
