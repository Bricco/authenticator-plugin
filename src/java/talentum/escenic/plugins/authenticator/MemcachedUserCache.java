package talentum.escenic.plugins.authenticator;

import java.util.Collection;
import java.util.HashMap;

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
public class MemcachedUserCache extends HashMapUserCache {
	
	private static final String VALID_USERS = "validUsers";
	private static final String EVICTED_USERS = "evictedUsers";

	MemCachedClient memCachedClient;
	
	public MemcachedUserCache() {
		memCachedClient = new MemCachedClient();
		SockIOPool pool = SockIOPool.getInstance();
        if(!pool.isInitialized()) {
        	pool.initialize();
        }
	}
		
	public void addUser(AuthenticatedUser user, boolean singleUserAccess) {
		
		if(singleUserAccess) {
			// remove any cheater first
			String cheaterToken = findCheater(user);
			if(cheaterToken != null) {
				memCachedClient.delete(cheaterToken);
			}
		}
		// add user to memcached
		memCachedClient.add(user.getToken(), user);
		
		ensureValidUsers();
		ensureEvictedUsers();
		
		// add user to HashMap
		super.addUser(user, singleUserAccess);

		// replace HashMaps in memcached
		memCachedClient.replace(VALID_USERS, validUsers);
		memCachedClient.replace(EVICTED_USERS, evictedUsers);
	
	}
	
	/**
	 * Get user directly from memcached. If not found try in the HashMap.
	 */
	public AuthenticatedUser getUser(String token) {
		AuthenticatedUser user = (AuthenticatedUser) memCachedClient.get(token);
		if(user == null) {
			user = super.getUser(token);
		}
		return user;
	}
	
	public Collection getAllUsers() {
		ensureValidUsers();
		return super.getAllUsers();
	}

	public void removeUser(String token) {
		memCachedClient.delete(token);
		ensureValidUsers();
		super.removeUser(token);
		memCachedClient.replace(VALID_USERS, validUsers);
	}
	
	public boolean userHasBeenRemoved(String token) {
		ensureEvictedUsers();
		return super.userHasBeenRemoved(token);
	}
	
	private void ensureValidUsers() {
        validUsers = (HashMap) memCachedClient.get(VALID_USERS);
        if(validUsers == null) {
	        // add empty map if it doesn't exist
        	validUsers = new HashMap();
	        memCachedClient.add(VALID_USERS, validUsers);
		}
	}

	private void ensureEvictedUsers() {
		evictedUsers = (HashMap) memCachedClient.get(EVICTED_USERS);
        if(evictedUsers == null) {
	        // add empty map if it doesn't exist
        	evictedUsers = new HashMap();
	        memCachedClient.add(EVICTED_USERS, evictedUsers);
		}
	}

}