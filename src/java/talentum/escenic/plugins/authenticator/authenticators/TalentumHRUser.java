package talentum.escenic.plugins.authenticator.authenticators;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TalentumHRUser implements AuthenticatedUser {

	private String artefact;
	private String name;
	private String userName;
	private String companyName;
	private int userId = -1;
	private boolean isLinkUser;
	private String adminPageURL;
	private Date loggedInTime = new Date();
	private List products = new ArrayList();
	private String[] productIds;

	/**
	 * Constructs a user from the LoginService web service
	 * @param userSDto
	 */
	public TalentumHRUser(String artefact, String name, String userName, String adminPageURL) {
		this.artefact = artefact;
		this.name = name;
		this.userName = userName;
		this.adminPageURL = adminPageURL;
	}
	
	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getToken() {
		return artefact;
	}

	public String getUserName() {
		return userName;
	}
	
	public String getName() {
		return name;
	}

	public String getEmail() {
		return null;
	}

	public String getProductId() {
		return null;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public boolean isLinkUser() {
		return isLinkUser;
	}

	public void setLinkUser(boolean isLinkUser) {
		this.isLinkUser = isLinkUser;
	}

	public Date getLoggedInTime() {
		return loggedInTime;
	}

	public String[] getRoles() {
		return new String[0];
	}

	public boolean hasRole(String role) {
		return false;
	}

	/**
	 * @return true if one product has passive status and one of its roles matches
	 */
	public boolean hasPassiveStatusForRole(String role) {
		return false;
	}

	public String getAdminPage() {
		if(isLinkUser()) {
			// deny link users access to the admin page
			return null;
		}
		return adminPageURL + "?artefact=" + getToken();
	}
	
	public String[] getProductIds() {
		return productIds;
	}

	public void setProductIds(String[] productIds) {
		this.productIds = productIds;
	}

	public void addProduct(String name, String link) {
		products.add(new Product(name, link));
	}
	
	public List getProducts() {
		return products;
	}

	public class Product implements Serializable{
		String name;
		String link;
		public Product(String name, String link) {
			super();
			this.name = name;
			this.link = link;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getLink() {
			return link;
		}
		public void setLink(String link) {
			this.link = link;
		}
		
	}

}
