package talentum.escenic.plugins.authenticator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;

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

    MemCachedClient memCachedClient;

	private HashMap evictedUsers = new HashMap();

	private AuthenticatorManager() {
		memCachedClient = new MemCachedClient();
        if(!SockIOPool.getInstance().isInitialized())
            SockIOPool.getInstance().initialize();
        memCachedClient.add("validUsers", new String[0]);
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
			String[] validUsers = (String[]) memCachedClient.get("validUsers");
			if(validUsers.length > 0) {
				Map userMap = memCachedClient.getMulti(validUsers);
				 
				// if user is already logged in move him to the evicted list
				for (Iterator iter = userMap.keySet().iterator(); iter.hasNext();) {
					String token = (String) iter.next();
					AuthenticatedUser tmpUser = (AuthenticatedUser)userMap.get(token);
					if(tmpUser.getUserId()==user.getUserId()) {
						evictedUsers.put(token, tmpUser);
						break;
					}
					
				}
			}
			
			// if user was found we set login time and add him to the map
			user.setLastChecked(new Date());
			memCachedClient.add(user.getToken(), user);
			List list = new ArrayList();
			list.addAll(Arrays.asList(validUsers));
			list.add(user.getToken());
			validUsers = (String[]) list.toArray(new String[list.size()]);
			memCachedClient.replace("validUsers", validUsers);

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
		Map userMap = new HashMap();
		String[] validUsers = (String[]) memCachedClient.get("validUsers");
		if(validUsers.length > 0) {
			userMap = memCachedClient.getMulti(validUsers);
		}
		return userMap.values();
	}

	public void evictUser(String token) {
		String[] validUsers = (String[]) memCachedClient.get("validUsers");
		memCachedClient.delete(token);
		List list = new ArrayList();
		list.addAll(Arrays.asList(validUsers));
		list.remove(token);
		validUsers = (String[]) list.toArray(new String[list.size()]);
		memCachedClient.replace("validUsers", validUsers);
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
		return (AuthenticatedUser) memCachedClient.get(token);
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
