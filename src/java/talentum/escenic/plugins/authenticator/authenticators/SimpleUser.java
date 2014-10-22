package talentum.escenic.plugins.authenticator.authenticators;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.ListUtils;


public class SimpleUser implements AuthenticatedUser {

	private String userName;
	private String productId;
	private int customerNumber = 0;
	private Date expirationDate;
	private Date loggedInTime = new Date();


	/**
	 * Constructs a user
	 * @param userName the user name
	 */
	public SimpleUser(String userName) {
		this.userName = userName;
	}

	
	public int getUserId() {
		return -1;
	}

	public int getSourceUserId() {
		return -1;
	}

	public String getToken() {
		return getUserName() + String.valueOf(loggedInTime.getTime());
	}

	public String getUserName() {
		return userName;
	}

	public String getName() {
		return userName;
	}

	public String getCompanyName() {
		return null;
	}

	public String getEmail() {
		return null;
	}

	public String getProductId() {
		return null;
	}

	public int getCustomerNumber() {
		return -1;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public Date getLoggedInTime() {
		return loggedInTime;
	}

	public String[] getRoles() {
		return new String[0];
	}

	public String[] matchingRoles(String[] roles) {
		return new String[0];
	}

	public boolean hasPassiveStatusForRole(String[] roles) {
		return true;
	}

	public String getAdminPage() {
		return null;
	}

	public String[] getProductIds() {
		return new String[0];
	}

	public List getProducts() {
		return null;
	}

	
}
