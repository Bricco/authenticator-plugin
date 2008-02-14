package talentum.escenic.plugins.authenticator;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import talentum.escenic.plugins.authenticator.authenticators.AuthenticatedUser;
import talentum.escenic.plugins.authenticator.authenticators.Authenticator;

/**
 * Main class of the plugin.
 * 
 * @author stefan.norman
 *
 */
public class AuthenticatorManager {

	private static Log log = LogFactory.getLog(AuthenticatorManager.class);
	
	private static AuthenticatorManager manager;
	private Authenticator authenticator;
	private HashMap userMap = new HashMap();
	
	private AuthenticatorManager() {}
	
	public static AuthenticatorManager getInstance() {
		if(manager == null)
			manager = new AuthenticatorManager();
		return manager;
	}

	public Authenticator getAuthenticator() {
		return authenticator;
	}

	public void setAuthenticator(Authenticator authenticator) {
		this.authenticator = authenticator;
	}
	
	/**
	 * Auhtenticates a user with specified credentials
	 * 
	 * @param username
	 * @param password
	 * @return the user token or null if not authenticated
	 */
	public String authenticate(String username, String password) {
		
		String token = null;
		// authenticate user with the configures Authenticator
		AuthenticatedUser user = getAuthenticator().authenticate(username, password);
		
		// if user was found we set login time and add the him to the map
		if(user != null) {
			user.setLastChecked(new Date());
			userMap.put(user.getToken(), user);
			token = user.getToken();
		}
		
		return token;
	}
	
	public Collection getLoggedInUsers() {
		return userMap.values();
	}
	
	public void evictUser(String token) {
		userMap.remove(token);
	}
	
	public AuthenticatedUser getVerifiedUser(String token) {
		if(token == null) {
			return null;
		}
		AuthenticatedUser user = (AuthenticatedUser) userMap.get(token);
		if(user == null) {
			// user not found
			return null;
		}
		Calendar cal = Calendar.getInstance(); 
		cal.add(Calendar.MINUTE, -5);
		if(user.getLastChecked().before(cal.getTime())) {
			user = authenticator.verifyUser(token);
			if(user == null) {
				userMap.remove(token);
				return null;
			} else {
				user.setLastChecked(new Date());
				userMap.put(user.getToken(), user);
			}
		}
		return user;
	}
}
