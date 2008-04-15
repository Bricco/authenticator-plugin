package talentum.escenic.plugins.authenticator;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

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

	public static String AUTOLOGIN_COOKIE = "afv_autologin";

	private static AuthenticatorManager manager;

	private Authenticator authenticator;

	private String cookieName;

	private String cookieDomain = "";

	private HashMap validUsers = new HashMap();

	private HashMap evictedUsers = new HashMap();

	private AuthenticatorManager() {
	}

	public static AuthenticatorManager getInstance() {
		if (manager == null)
			manager = new AuthenticatorManager();
		return manager;
	}

	public Authenticator getAuthenticator() {
		return authenticator;
	}

	public void setAuthenticator(Authenticator authenticator) {
		this.authenticator = authenticator;
	}

	public String getCookieDomain() {
		return cookieDomain;
	}

	public void setCookieDomain(String cookieDomain) {
		this.cookieDomain = cookieDomain;
	}

	public String getCookieName() {
		return cookieName;
	}

	public void setCookieName(String cookieName) {
		this.cookieName = cookieName;
	}

	/**
	 * Auhtenticates a user with specified credentials
	 * 
	 * @param username
	 * @param password
	 * @return the user token or null if not authenticated
	 */
	public AuthenticatedUser authenticate(String username, String password) {
		
		AuthenticatedUser user = null;
		try {
			// authenticate user with the configures Authenticator
			user = getAuthenticator().authenticate(username,
					password);
	
			// if user is already logged in move him to the evicted list
			for (Iterator iter = validUsers.keySet().iterator(); iter.hasNext();) {
				String token = (String) iter.next();
				if(((AuthenticatedUser)validUsers.get(token)).getUserId()==user.getUserId()) {
					evictedUsers.put(token, validUsers.remove(token));
					break;
				}
				
			}
			
			// if user was found we set login time and add him to the map
			user.setLastChecked(new Date());
			validUsers.put(user.getToken(), user);


		} catch (AuthenticationException e) {
			log.error("Could not authenticate", e);
		}

		return user;
	}

	public AuthenticatedUser authenticateAuto(String encryptedUserInfo) {
		String userInfo;
		try {
			userInfo = PBEEncrypter.decrypt(encryptedUserInfo);
		} catch (Exception e) {
			log.error("Could not decrypt autologin cookie.", e);
			return null;
		}

		if (userInfo.indexOf('|') < 0)
			return null;
		StringTokenizer tokenizer = new StringTokenizer(userInfo, "|");
		String username = tokenizer.nextToken();
		String password = tokenizer.nextToken();

		return authenticate(username, password);
	}

	public Collection getLoggedInUsers() {
		return validUsers.values();
	}

	public void evictUser(String token) {
		validUsers.remove(token);
	}

	/**
	 * Retrieves a user
	 * @param token String the token to look for
	 * @return a valid user
	 */
	public AuthenticatedUser getUser(String token) {
		if (token == null) {
			return null;
		}
		return (AuthenticatedUser) validUsers.get(token);
	}

	/**
	 * Check whether a user has been evicted, due to another user using the account.
	 * 
	 * @param token String the token to look for
	 * @return true if the user with specified token has been evicted
	 */
	public boolean userHasBeenEvicted(String token)  {
		return evictedUsers.containsKey(token);
	}
}
