package talentum.escenic.plugins.authenticator.authenticators;

import java.util.Date;
import java.util.List;

public class TitelDataUser implements AuthenticatedUser {

	private String customerNo;
	private String username;
	private String firstName;
	private String lastName;

	private Date timestamp = new Date();

	public TitelDataUser(String customerNo, String username, String firstName, String lastName) {
		this.customerNo = customerNo;
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
	}
	
	public int getUserId() {
		return Integer.parseInt(customerNo);
	}

	public int getSourceUserId() {
		return 0;
	}

	public String getToken() {
		return getUserId() + String.valueOf(timestamp.getTime());
	}

	public String getUserName() {
		return username;
	}

	public String getName() {
		return firstName + " " + lastName;
	}

	public String getCompanyName() {
		return null;
	}

	public String[] getRoles() {
		return new String[] {"T"};
	}

	public String getEmail() {
		return null;
	}

	public String getProductId() {
		return null;
	}

	public int getCustomerNumber() {
		return Integer.parseInt(customerNo);
	}

	public Date getExpirationDate() {
		return null;
	}

	public Date getLoggedInTime() {
		return timestamp;
	}

	public String[] matchingRoles(String[] roles) {
		return null;
	}

	public boolean hasPassiveStatusForRole(String[] roles) {
		return false;
	}

	public String getAdminPage() {
		return null;
	}

	public List getProducts() {
		return null;
	}

	public String[] getProductIds() {
		return null;
	}

}
