package talentum.escenic.plugins.authenticator.authenticators;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.ListUtils;

public class KayakUser implements AuthenticatedUser {

	private String token;
	private String customerNo;
	private String name;
	private String postalCode;
	private String status;
	private Date loggedInTime = new Date();

	/**
	 * 
	 * @param line an array of field on this format 
	 * AN;P MOF;72108563;67150;aktiv;20121214;20130613;ANNLISA;CARLSSON;CARLSSON ANNLISA;Ja
	 */
	public KayakUser(String[] line) {
		this.token = line[2] + String.valueOf(System.currentTimeMillis());
		this.customerNo = line[2];
		this.postalCode = line[3];
		this.status = line[4];
		this.name = line[7] + " " + line[8];
	}

	public int getUserId() {
		return Integer.parseInt(customerNo);
	}

	public int getSourceUserId() {
		return Integer.parseInt(customerNo);
	}

	public String getToken() {
		return token;
	}

	public String getUserName() {
		return customerNo;
	}

	public String getName() {
		return name;
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
		return loggedInTime;
	}

	public String[] matchingRoles(String[] roles) {
		// intersecion() returns a new list containing all elements that are contained in both given lists
		List diff = ListUtils.intersection(Arrays.asList(getRoles()), Arrays.asList(roles));
		return (String[]) diff.toArray(new String[diff.size()]	);
	}

	public boolean hasPassiveStatusForRole(String[] roles) {
		// all statuses that don't have "aktiv" are considered passive.
		return !status.equals("aktiv");
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
