package talentum.escenic.plugins.authenticator;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.servlet.http.Cookie;

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
	public Cookie authenticate(String username, String password) {

		Cookie cookie = null;
		try {
			// authenticate user with the configures Authenticator
			AuthenticatedUser user = getAuthenticator().authenticate(username,
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

			cookie = new Cookie(getCookieName(), user.getToken());
			cookie.setDomain(getCookieDomain());
			cookie.setPath("/");

		} catch (AuthenticationException e) {
			log.error("Could not authenticate", e);
		}

		return cookie;
	}

	public Cookie authenticateAuto(String encryptedUserInfo) {
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

	public Cookie evictUser(String token) {
		validUsers.remove(token);
		Cookie cookie = new Cookie(AuthenticatorManager.getInstance()
				.getCookieName(), "");
		cookie.setMaxAge(0);
		cookie.setDomain(getCookieDomain());
		cookie.setPath("/");
		return cookie;
	}

	public Cookie getAutologinCookie(String userName, String password) {
		String cookieValue = userName + "|" + password;
		try {
			cookieValue = PBEEncrypter.encrypt(cookieValue);
		} catch (Exception e) {
			log.error("Could not encrypt autologin cookie.", e);
			return null;
		}

		Cookie cookie = new Cookie(AUTOLOGIN_COOKIE, cookieValue);
		int autoLoginExpire = (60 * 60 * 24) * 100; // 100 days
		cookie.setDomain(getCookieDomain());
		cookie.setPath("/");
		cookie.setMaxAge(autoLoginExpire);
		return cookie;
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
	 * Retrieves a verified user.
	 * 
	 * @param token String the token to look for
	 * @param role String the role the user must be in
	 * @return a valid user
	 * @throws UserNotFoundException if the user is not found
	 * @throws AuthorizationException if the user is not in specified role
	 */
	public AuthenticatedUser getVerifiedUser(String token, String role)
			throws UserNotFoundException, AuthorizationException, OtherUserLoggedInException {
		
		AuthenticatedUser user = getUser(token);
		if (user == null) {
			if(evictedUsers.containsKey(token)) {
				throw new OtherUserLoggedInException("user with token " + token
						+ " was rejected, another user was logged in");
				
			} else {
				throw new UserNotFoundException("user with token " + token
						+ " not found");
			}
		} else if (!user.hasRole(role)) {
			throw new AuthorizationException("user with token " + token
					+ " is not in role " + role);
		}

		return user;
	}
}
