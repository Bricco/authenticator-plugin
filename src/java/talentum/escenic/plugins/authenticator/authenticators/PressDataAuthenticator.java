package talentum.escenic.plugins.authenticator.authenticators;

import net.kundservice.www.prenstatusws.login.LoginServiceLocator;
import net.kundservice.www.prenstatusws.login.LoginServiceSoapStub;
import net.kundservice.www.prenstatusws.login.ProductDto;
import net.kundservice.www.prenstatusws.login.UserStatusDto;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PressDataAuthenticator implements Authenticator {

	private static Log log = LogFactory.getLog(PressDataAuthenticator.class);

	public AuthenticatedUser authenticate(String username, String password) {
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

		} catch (Exception e) {
			log.error("authentication failed", e);
		}
		return user;
	}
	
	public AuthenticatedUser verifyUser(String token) {
		AuthenticatedUser user = null;
		try {
			// call web service top authenticate
//			AuthorizationSoapStub binding = (AuthorizationSoapStub) new AuthorizationLocator()
//					.getAuthorizationSoap();
			// TODO read product from properties file?
//			UserStruct struct = binding.verifyUser(token, "AFR");
//
//			if (struct.getErrorCode() > 0) {
//				log.error("Verification failed for user with token " + token
//						+ ". Error from web service: " + struct.getErrorCode()
//						+ " " + struct.getErrorDescription());
//			} else {
//				// populate user object
//				user = populateUser(struct);
//			}

		} catch (Exception e) {
			log.error("verification failed", e);
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
