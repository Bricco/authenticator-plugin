package talentum.escenic.plugins.authenticator.authenticators;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TitelDataUser implements AuthenticatedUser {

	private String customerNo;
	private String username;
	private String name;
	private List roles = new ArrayList();

	private Date timestamp = new Date();

	public TitelDataUser(String customerNo, String username, String name) {
		this.customerNo = customerNo;
		this.username = username;
		this.name = name;
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
		return name;
	}

	public String getCompanyName() {
		return null;
	}

	public String[] getRoles() {
		return (String[]) roles.toArray(new String[roles.size()]);
	}
	
	public void addRole(String role) {
		roles.add(role);
	}

	public String getEmail() {
		return username;
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
