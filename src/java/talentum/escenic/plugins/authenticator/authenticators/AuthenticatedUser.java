package talentum.escenic.plugins.authenticator.authenticators;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Internal representation of a valid user
 * 
 * @author stefan.norman
 */
public interface AuthenticatedUser extends Serializable {

	public int getUserId();
	
	public String getToken();

	/**
	 * @return the username
	 */
	public String getUserName();
	
	/**
	 * @return the name
	 */
	public String getName();
	
	/**
	 * @return the company name
	 */
	public String getCompanyName();
	
	/**
	 * @return the roles of the user
	 */
	public String[] getRoles();
	
	/**
	 * @return the email
	 */
	public String getEmail();
	
	/**
	 * Sent to Pressdata to see My Page
	 * @return the product id
	 */
	public String getProductId();
	
	/**
	 * @return the date when the user was checked last time
	 */
	public Date getLoggedInTime();
	
	/**
	 * The user has the requested role.
	 * @param role the requested role
	 * @return true if user has role
	 */
	public boolean hasRole(String role);
	
	/**
	 * The user has passive status for the requested role.
	 * @param role the requested role
	 * @return true if user has passive status
	 */
	public boolean hasPassiveStatusForRole(String role);
	
	/**
	 * Get a URL configured in the Authenticator. It will be used to redirect to, after logging in.
	 * 
	 * @return A URL String
	 */
	public String getAdminPage();
	
	/**
	 * Products of a user. From Tiger.
	 * 
	 * @return a List of Products
	 */
	public List getProducts();
	
	/**
	 * Product ids of a user. From Tiger.
	 * 
	 * @return a String[] of product ids
	 */
	public String[] getProductIds();

}
