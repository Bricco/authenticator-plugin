package talentum.escenic.plugins.authenticator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import talentum.escenic.plugins.authenticator.authenticators.AuthenticatedUser;

import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;

/**
 * Holds a distributed list of users in memcached.
 * This list will survive a server restart since the actual list is
 * stored in memcached.
 * 
 * @author stefan.norman
 *
 */
public class MemcachedUserCache implements UserCache {
	
	private static final String VALID_USERS = "validUsers";
	private static final String EVICTED_USERS = "evictedUsers";

	MemCachedClient memCachedClient;
	
	public MemcachedUserCache() {
		memCachedClient = new MemCachedClient();
        if(!SockIOPool.getInstance().isInitialized())
            SockIOPool.getInstance().initialize();
	}
	
	private String[] getValidUsers() {
		String[] validUsers = (String[]) memCachedClient.get(VALID_USERS);
		if(validUsers == null) {
			validUsers = new String[0];
	        // add empty list if it doesn't exist
	        memCachedClient.add(VALID_USERS, validUsers);
		}
		return validUsers;
	}
	
	private String[] getEvictedUsers() {
		String[] evictedUsers = (String[]) memCachedClient.get(EVICTED_USERS);;
		if(evictedUsers == null) {
			evictedUsers = new String[0];
	        // add empty list if it doesn't exist
	        memCachedClient.add(EVICTED_USERS, evictedUsers);
		}
		return evictedUsers;
	}
	
	public void addUser(AuthenticatedUser user) {
		
		String[] validUsers = getValidUsers();
		if(validUsers.length > 0) {
			Map userMap = memCachedClient.getMulti(validUsers);
			 
			// if user is already logged in move him to the evicted list
			for (Iterator iter = userMap.keySet().iterator(); iter.hasNext();) {
				String token = (String) iter.next();
				AuthenticatedUser tmpUser = (AuthenticatedUser)userMap.get(token);
				if(tmpUser.getUserId()==user.getUserId()) {
					String[] evictedUsers = getEvictedUsers();
					evictedUsers = addStringToArray(evictedUsers, token);
					memCachedClient.replace(EVICTED_USERS, evictedUsers);
					break;
				}
				
			}
		}
		
		// if user was found add him to memcached
		memCachedClient.add(user.getToken(), user);
		validUsers = addStringToArray(validUsers, user.getToken());
		memCachedClient.replace(VALID_USERS, validUsers);
	}
	
	public AuthenticatedUser getUser(String token) {
		return (AuthenticatedUser) memCachedClient.get(token);
	}
	
	public Collection getAllUsers() {
		Map userMap = new HashMap();
		String[] validUsers = getValidUsers();
		if(validUsers.length > 0) {
			userMap = memCachedClient.getMulti(validUsers);
		}
		return userMap.values();
		
	}

	public void removeUser(String token) {
		String[] validUsers = getValidUsers();
		memCachedClient.delete(token);
		validUsers = removeStringFromArray(validUsers, token);
		memCachedClient.replace(VALID_USERS, validUsers);
	}
	
	public boolean userAsBeenRemoved(String token) {
		String[] evictedUsers = getEvictedUsers();
		for (int i = 0; i < evictedUsers.length; i++) {
			if(evictedUsers[i].equals(token)) {
				return true;
			}
		}
		return false;
	}

	private String[] addStringToArray(String[] stringArray, String stringToAdd) {
		List list = new ArrayList();
		list.addAll(Arrays.asList(stringArray));
		list.add(stringToAdd);
		return (String[]) list.toArray(new String[list.size()]);

	}

	private String[] removeStringFromArray(String[] stringArray, String stringToAdd) {
		List list = new ArrayList();
		list.addAll(Arrays.asList(stringArray));
		list.remove(stringToAdd);
		return (String[]) list.toArray(new String[list.size()]);

	}
}
