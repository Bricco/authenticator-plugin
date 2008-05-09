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
        if(!SockIOPool.getInstance().isInitialized())
            SockIOPool.getInstance().initialize();
	}
		
	public void addUser(AuthenticatedUser user) {
		
		ensureValidUsers();
		ensureEvictedUsers();
		
		super.addUser(user);

		memCachedClient.replace(VALID_USERS, validUsers);
		memCachedClient.replace(EVICTED_USERS, evictedUsers);
	
	}
	
	public AuthenticatedUser getUser(String token) {
		ensureValidUsers();
		return super.getUser(token);
	}
	
	public Collection getAllUsers() {
		ensureValidUsers();
		return super.getAllUsers();
	}

	public void removeUser(String token) {
		ensureValidUsers();
		super.removeUser(token);
		memCachedClient.replace(VALID_USERS, validUsers);
	}
	
	public boolean userAsBeenRemoved(String token) {
		ensureEvictedUsers();
		return super.userAsBeenRemoved(token);
	}
	
	private void ensureValidUsers() {
        if(memCachedClient.get(VALID_USERS) == null) {
	        // add empty map if it doesn't exist
	        memCachedClient.add(VALID_USERS, new HashMap());
		}
        validUsers = (HashMap) memCachedClient.get(VALID_USERS);
	}

	private void ensureEvictedUsers() {
        if(memCachedClient.get(EVICTED_USERS) == null) {
	        // add empty map if it doesn't exist
	        memCachedClient.add(EVICTED_USERS, new HashMap());
		}
        evictedUsers = (HashMap) memCachedClient.get(EVICTED_USERS);
	}

}