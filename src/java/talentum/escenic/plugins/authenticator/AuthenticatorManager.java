package talentum.escenic.plugins.authenticator;

import java.util.Collection;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import talentum.escenic.plugins.authenticator.authenticators.AuthenticatedUser;
import talentum.escenic.plugins.authenticator.authenticators.Authenticator;

/**
 * Manager class for Authenticator plugin.
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

	UserCache userCache;

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

	public UserCache getUserCache() {
		return userCache;
	}

	public void setUserCache(UserCache userCache) {
		this.userCache = userCache;
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
			// authenticate user with the configured Authenticator
			user = getAuthenticator().authenticate(username,
					password);
			// if user was found add him to cache
			userCache.addUser(user);

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
		return userCache.getAllUsers();
	}

	public void evictUser(String token) {
		userCache.removeUser(token);
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
		return userCache.getUser(token);
	}

	/**
	 * Check whether a user has been evicted, due to another user using the account.
	 * 
	 * @param token String the token to look for
	 * @return true if the user with specified token has been evicted
	 */
	public boolean userHasBeenEvicted(String token)  {
		return userCache.userAsBeenRemoved(token);
	}
	

}
