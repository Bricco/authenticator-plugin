package talentum.escenic.plugins.authenticator;

import java.util.Collection;

import talentum.escenic.plugins.authenticator.authenticators.AuthenticatedUser;

/**
 * A list holding all logged in users.
 * 
 * @author stefan.norman
 *
 */
public interface UserCache {

	/**
	 * Add a user to the list.
	 * @param user AuthenticatedUser to add.
	 */
	public void addUser(AuthenticatedUser user);
	
	/**
	 * Get a specified user.
	 * @param token the token to search for.
	 * @return a user.
	 */
	public AuthenticatedUser getUser(String token);
	
	/**
	 * Get all logged in users.
	 * @return a collection of AuthenticatedUser objects.
	 */
	public Collection getAllUsers();
	
	/**
	 * Remove a user form the list.
	 * @param token the token to remove.
	 */
	public void removeUser(String token);
	
	/**
	 * Check if the user has been previously removed.
	 * @param token the token to check.
	 * @return true if the user has bee previously removed.
	 */
	public boolean userHasBeenRemoved(String token);
	
}
