package talentum.escenic.plugins.authenticator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.servlet.http.Cookie;

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

	private static AuthenticatorManager manager;

	private HashMap authenticators = new HashMap();

	UserCache userCache;

	private AuthenticatorManager() {
	}
	
	public static AuthenticatorManager getInstance() {
		if (manager == null)
			manager = new AuthenticatorManager();
		return manager;
	}

	public void addAuthenticator(String publicationName, Authenticator authenticator) {
		authenticators.put(publicationName, authenticator);
	}
	
	private Authenticator getAuthenticator(String publicationName) {
		return (Authenticator) authenticators.get(publicationName);
	}

	public UserCache getUserCache() {
		return userCache;
	}

	public void setUserCache(UserCache userCache) {
		this.userCache = userCache;
	}

	public String[] getCookieNames() {
		ArrayList al = new ArrayList();
		for (Iterator iter = authenticators.values().iterator(); iter.hasNext();) {
			Authenticator authenticator = (Authenticator) iter.next();
			al.add(authenticator.getCookieName());
		}
		return (String[]) al.toArray(new String[al.size()]);
	}

	/**
	 * Auhtenticates a user with specified credentials
	 * 
	 * @param username
	 * @param password
	 * @return the user token or null if not authenticated
	 */
	public AuthenticatedUser authenticate(String publicationName, String username, String password) {
		
		AuthenticatedUser user = null;
		try {
			// authenticate user with the configured Authenticator
			user = getAuthenticator(publicationName).authenticate(username,
					password);
			// if user was found add him to cache
			userCache.addUser(user);

		} catch (AuthenticationException e) {
			log.error("Could not authenticate", e);
		}

		return user;
	}

	public AuthenticatedUser authenticateAuto(String authenticatorName, String encryptedUserInfo) {
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

		return authenticate(authenticatorName, username, password);
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
	
	public String getCookieName(String publicationName) {
		return getAuthenticator(publicationName).getCookieName();
	}
	
	public String getAutoLoginCookieName(String publicationName) {
		return getAuthenticator(publicationName).getAutoLoginCookieName();
	}
	
	public Cookie getSessionCookie(String publicationName, String token) {
		Cookie cookie = new Cookie(getCookieName(publicationName), token);
		cookie.setDomain(getAuthenticator(publicationName).getCookieDomain());
		cookie.setPath("/");
		return cookie;
	}

	public Cookie getAutologinCookie(String publicationName, String userName, String password) {
		String cookieValue = userName + "|" + password;
		try {
			cookieValue = PBEEncrypter.encrypt(cookieValue);
		} catch (Exception e) {
			return null;
		}

		Cookie cookie = new Cookie(getAutoLoginCookieName(publicationName), cookieValue);
		int autoLoginExpire = (60 * 60 * 24) * 100; // 100 days
		cookie.setDomain(getAuthenticator(publicationName).getCookieDomain());
		cookie.setPath("/");
		cookie.setMaxAge(autoLoginExpire);
		return cookie;
	}

	public Cookie removeSessionCookie(String publicationName) {
		Cookie cookie = new Cookie(getCookieName(publicationName), "");
		cookie.setMaxAge(0);
		cookie.setDomain(getAuthenticator(publicationName).getCookieDomain());
		cookie.setPath("/");
		return cookie;
	}

	public Cookie removeAutologinCookie(String publicationName) {
		Cookie cookie = new Cookie(getAutoLoginCookieName(publicationName), "");
		cookie.setMaxAge(0);
		cookie.setDomain(getAuthenticator(publicationName).getCookieDomain());
		cookie.setPath("/");
		return cookie;
	}
}
