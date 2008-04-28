package talentum.escenic.plugins.authenticator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import talentum.escenic.plugins.authenticator.authenticators.AuthenticatedUser;

/**
 * Holds an in memory list of users in a HashMap.
 * This list will NOT survive a server restart.
 * 
 * @author stefan.norman
 *
 */
public class HashMapUserCache implements UserCache {

	private HashMap validUsers;
	private HashMap evictedUsers;
	
	public HashMapUserCache() {
		validUsers = new HashMap();
		evictedUsers = new HashMap();
	}
	
	public void addUser(AuthenticatedUser user) {
		for (Iterator iter = validUsers.keySet().iterator(); iter.hasNext();) {
			String token = (String) iter.next();
			if(((AuthenticatedUser)validUsers.get(token)).getUserId()==user.getUserId()) {
				evictedUsers.put(token, validUsers.remove(token));
				break;
			}
			
		}
		
		validUsers.put(user.getToken(), user);
	}

	public Collection getAllUsers() {
		return validUsers.values();
	}

	public AuthenticatedUser getUser(String token) {
		return (AuthenticatedUser) validUsers.get(token);
	}

	public void removeUser(String token) {
		validUsers.remove(token);
	}

	public boolean userAsBeenRemoved(String token) {
		return evictedUsers.containsKey(token);
	}

}
