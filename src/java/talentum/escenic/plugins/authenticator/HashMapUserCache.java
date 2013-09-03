package talentum.escenic.plugins.authenticator;

import java.util.Calendar;
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

	protected HashMap validUsers;
	protected HashMap evictedUsers;
	
	public HashMapUserCache() {
		validUsers = new HashMap();
		evictedUsers = new HashMap();
	}
	
	public void addUser(AuthenticatedUser user, boolean singleUserAccess) {
		
		// remove old users
		validUsers = removeOldUsers(validUsers);
		evictedUsers = removeOldUsers(evictedUsers);

		if(singleUserAccess) {
			String cheaterToken = findCheater(user);
			if(cheaterToken != null) {
				evictedUsers.put(cheaterToken, validUsers.remove(cheaterToken));
			}		
		}
		
		validUsers.put(user.getToken(), user);
	}

	/**
	 * Loop existing users to see if the new user is already logged in
	 * if so evict the the existing user
	 * 
	 * @return the token of the evicted user or null if no user was evicted
	 */
	protected String findCheater(AuthenticatedUser user) {
		String token;
		for (Iterator iter = validUsers.keySet().iterator(); iter.hasNext();) {
			token = (String) iter.next();
			if(((AuthenticatedUser)validUsers.get(token)).getUserId()==user.getUserId()) {
				return token;
			}
		}
		return null;
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

	public boolean userHasBeenRemoved(String token) {
		return evictedUsers.containsKey(token);
	}

	private HashMap removeOldUsers(HashMap userMap) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -3);
		HashMap checkedUsers = new HashMap();
		for (Iterator iter = userMap.values().iterator(); iter.hasNext();) {
			AuthenticatedUser u = (AuthenticatedUser) iter.next();
			if(u.getLoggedInTime().after(calendar.getTime())) {
				checkedUsers.put(u.getToken(), u);
			}
		}
		return checkedUsers;
	}

}
