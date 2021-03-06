package talentum.escenic.plugins.authenticator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

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
	boolean singleUserAccess = true;

	private AuthenticatorManager() {
	}
	
	public static AuthenticatorManager getInstance() {
		if (manager == null)
			manager = new AuthenticatorManager();
		return manager;
	}

	public void addAuthenticator(String publicationName,
			Authenticator authenticator) {
		authenticator.setPublicationName(publicationName);
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

	public boolean isSingleUserAccess() {
		return singleUserAccess;
	}

	public void setSingleUserAccess(boolean singleUserAccess) {
		this.singleUserAccess = singleUserAccess;
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
	 * @return the user or null if not authenticated
	 */
	public AuthenticatedUser authenticate(String publicationName,
			String username, String password, String ipaddress) {

		AuthenticatedUser user = null;
		try {
			// authenticate user with the configured Authenticator
			user = getAuthenticator(publicationName).authenticate(username,
					password, ipaddress);

		} catch (AuthenticationException e) {
			log.error("Could not authenticate:" + e.getMessage());
			if (log.isDebugEnabled()) {
				log.debug(e);
			}
		}
		if(user != null && !addUserToCache(user)) {
			return null; // set user to null to keep previous error handling
		}
		return user;
	}

	/**
	 * Auhtenticates a user with a token
	 * 
	 * @param token
	 * @return the user or null if not authenticated
	 */
	public AuthenticatedUser authenticateUsingToken(String publicationName,
			String token) {

		AuthenticatedUser user = null;
		try {
			// authenticate user with the configured Authenticator
			user = getAuthenticator(publicationName).authenticateUsingToken(
					token);

		} catch (AuthenticationException e) {
			log.error("Could not authenticate:" + e.getMessage());
			if (log.isDebugEnabled()) {
				log.debug(e);
			}
		}
		if(user != null && !addUserToCache(user)) {
			return null; // set user to null to keep previous error handling
		}

		return user;
	}
	
	/**
	 * Get a user from the login cookie
	 * 
	 * @param publicationName which publication to find the cookie namn config from.
	 * @param cookies an array of Cookie from the request
	 * @return AuthenticatedUser a user if found, or else null
	 */
	public AuthenticatedUser getUserFromCookie(String publicationName, Cookie[] cookies) {
		// loop cookies to find the right one
		Cookie userDataCookie = null;
		for (int i = 0; cookies != null && i < cookies.length; i++) {
			if (cookies[i].getName().equals(getCookieName(publicationName))) {
				userDataCookie = cookies[i];
			}
		}
		AuthenticatedUser user = null;
		if (userDataCookie != null) {
			user = getUser(userDataCookie.getValue());
		}
		return user;
	}
	
	/**
	 * Stores the user to the cache
	 * 
	 * @param user the user to store 
	 * @return succesful storage
	 */
	private boolean addUserToCache(AuthenticatedUser user) {
		try {
			userCache.addUser(user, singleUserAccess);
		} catch (Exception e) {
			log.error("User was authenticated, but storing the user in the cache failed. Memcached needs flush_all? Exception: "
					+ e.getMessage());
			if (log.isDebugEnabled()) {
				log.debug(e.getMessage(), e);
			}
			return false;
		}
		return true;
	}

	public boolean passwordReminder(String publicationName, String emailAddress) {

		try {
			getAuthenticator(publicationName).passwordReminder(emailAddress,
					publicationName);
			return true;

		} catch (ReminderException e) {
			log.error("Could not send reminder: " + e.getMessage());
			if (log.isDebugEnabled()) {
				log.debug(e);
			}
		}

		return false;
	}
	
	public void register(String publicationName, String username,
			String password, String postalCode, String customerNumber) throws RegistrationException {
		getAuthenticator(publicationName).register(username, password,
				postalCode, customerNumber);

	}

	public AuthenticatedUser authenticateAuto(String authenticatorName,
			String encryptedUserInfo, String ipadress) {
		String userInfo;
		try {
			userInfo = PBEEncrypter.decrypt(encryptedUserInfo);
		} catch (Exception e) {
			log.error("Could not decrypt autologin cookie: " + e.getMessage());
			if (log.isDebugEnabled()) {
				log.debug(e);
			}
			return null;
		}

		if (userInfo.indexOf('|') < 0)
			return null;
		StringTokenizer tokenizer = new StringTokenizer(userInfo, "|");
		String username = tokenizer.nextToken();
		String password = tokenizer.nextToken();

		return authenticate(authenticatorName, username, password, ipadress);
	}

	public Collection getLoggedInUsers() {
		return userCache.getAllUsers();
	}

	public void evictUser(String token) {
		userCache.removeUser(token);
	}

	public void evictUser(String publicationName, String token) {
		getAuthenticator(publicationName).logout(token);
		evictUser(token);
	}

	/**
	 * Retrieves a user
	 * 
	 * @param token
	 *            String the token to look for
	 * @return a valid user
	 */
	public AuthenticatedUser getUser(String token) {
		if (token == null || token == "") {
			return null;
		}
		return userCache.getUser(token);
	}

	/**
	 * Check whether a user has been evicted, due to another user using the
	 * account.
	 * 
	 * @param token
	 *            String the token to look for
	 * @return true if the user with specified token has been evicted
	 */
	public boolean userHasBeenEvicted(String token) {
		// if singleUserAccess flag is set to false (true is default)
		// return false indicating that no eviction has taken place
		if(!singleUserAccess) {
			return false;
		}
		return userCache.userHasBeenRemoved(token);
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

	public Cookie getAutologinCookie(String publicationName, String userName,
			String password) {
		String cookieValue = userName + "|" + password;
		try {
			cookieValue = PBEEncrypter.encrypt(cookieValue);
		} catch (Exception e) {
			return null;
		}

		Cookie cookie = new Cookie(getAutoLoginCookieName(publicationName),
				cookieValue);
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

	/**
	 * Get the ip adress of the request. If Varnish is in front use HTTP headers
	 * to figure out ip.
	 * 
	 * @param request
	 *            the servlet request
	 * @return the ip adress
	 */
	public static String getRemoteAddress(HttpServletRequest request) {
		//
		String callerIP = request.getRemoteAddr();
		if (request.getHeader("x-varnish") != null) {
			callerIP = request.getHeader("x-forwarded-for");
			if (callerIP.indexOf(',') > 0) {
				callerIP = callerIP.split(",")[0];
			}
		}
		return callerIP;
	}

	/**
	 * Change the password of a user.
	 * 
	 * @param publicationName the publication to locate the Authenticator to use
	 * @param user the user whose password should change
	 * @param oldPassword the old password
	 * @param newPassword the new password
	 * @return true if password change was successful
	 */
	public boolean changePassword(String publicationName,
			AuthenticatedUser user, String oldPassword, String newPassword) {
		try {
			getAuthenticator(publicationName).changePassword(
					user.getUserName(), oldPassword, newPassword);
		} catch (ChangePasswordException e) {
			log.error("Could not change password", e);
			return false;
		}
		return true;
	}
}
