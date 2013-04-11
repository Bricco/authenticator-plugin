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
	
	public int getSourceUserId();
	
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
	 * Customer number from backend subscription service
	 * @return th customer number
	 */
	public int getCustomerNumber();
	
	/**
	 * @return the date when the user expires
	 */
	public Date getExpirationDate();
	
	/**
	 * @return the date when the user was checked last time
	 */
	public Date getLoggedInTime();
	
	/**
	 * Roles of the user that matches the requested roles.
	 * 
	 * @param roles the requested roles
	 * @return an array of roles that matches the user's roles
	 */
	public String[] matchingRoles(String[] roles);
	
	/**
	 * The user has passive status for any of the requested roles.
	 * 
	 * @param roles the requested roles
	 * @return true if user has passive status
	 */
	public boolean hasPassiveStatusForRole(String[] roles);
	
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
