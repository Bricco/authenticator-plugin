package talentum.escenic.plugins.authenticator;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
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
		// authenticate user with the configures Authenticator
		AuthenticatedUser user = getAuthenticator().authenticate(username, password);
		
		// if user was found we set login time and add the him to the map
		if(user != null) {
			user.setLastChecked(new Date());
			userMap.put(user.getToken(), user);
			
			cookie = new Cookie(getCookieName(), user.getToken());
			cookie.setDomain(getCookieDomain());
			cookie.setPath("/");
		}
		
		return cookie;
	}
	
	public Cookie authenticateAuto(String encryptedUserInfo) {
		// TODO unscramble user info
		String userInfo = encryptedUserInfo;
		
		StringTokenizer tokenizer = new StringTokenizer(userInfo, "|");
		String username = tokenizer.nextToken();
		String password = tokenizer.nextToken();
		
		return authenticate(username, password);
	}

		
	public Collection getLoggedInUsers() {
		return userMap.values();
	}
	
	public Cookie evictUser(String token) {
		userMap.remove(token);
		Cookie cookie = new Cookie(AuthenticatorManager.getInstance().getCookieName(), "");
		cookie.setMaxAge(0);
		cookie.setDomain(getCookieDomain());
		cookie.setPath("/");
		return cookie;
	}
	
	public Cookie getAutologinCookie(String userName, String password) {
		String cookieValue = userName + "|" + password;
		// TODO scramble cookie value
		
		Cookie cookie = new Cookie(AUTOLOGIN_COOKIE, cookieValue);
		int autoLoginExpire = (60 * 60 * 24) * 100; // 100 days
		cookie.setDomain(getCookieDomain());
		cookie.setPath("/");
		cookie.setMaxAge(autoLoginExpire);
		return cookie;
	}
	
	public AuthenticatedUser getVerifiedUser(String token) {
		if(token == null) {
			return null;
		}
		return (AuthenticatedUser) userMap.get(token);
	}
}
