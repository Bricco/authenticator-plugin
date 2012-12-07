package talentum.escenic.plugins.authenticator.authenticators;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.ListUtils;

import net.kundservice.www.WS.Authorization.UserStruct;
import net.kundservice.www.prenstatusws.loginservice.ProductDto;
import net.kundservice.www.prenstatusws.loginservice.UserStatusDto;


public class PressDataUser implements AuthenticatedUser {

	private int userId;
	private String token;
	private String userName;
	private String name;
	private String companyName;
	private String email;
	private String productId;
	private Date loggedInTime = new Date();

	private ArrayList products = new ArrayList();

	/**
	 * Constructs a user from the LoginService web service
	 * @param userSDto
	 */
	public PressDataUser(UserStatusDto userSDto) {
		this.userId = userSDto.getUserId();
		this.userName = userSDto.getUsername();
		this.name = userSDto.getFirstname() + " " + userSDto.getLastname();
		this.companyName = userSDto.getCompanyName();
		this.email = userSDto.getEmail();
		
		ProductDto[] prDto = userSDto.getProducts();
		for (int i = 0; i < prDto.length; i++) {
			addProduct(prDto[i].getProductId(), prDto[i].getStatus(), prDto[i].getRoles());
			// token of last product used as token of user
			this.token = prDto[i].getToken();
			// productId of last product used as productId of user
			this.productId = prDto[i].getProductId();
		}
	}
	
	/**
	 * Constructs a user from the Authorization web service
	 * @param userStruct
	 * @param product
	 */
	public PressDataUser(UserStruct userStruct, String product) {
		this.userId = Integer.parseInt(userStruct.getUserId());
		this.userName = userStruct.getUserName();
		this.name = userStruct.getFirstName() + " " + userStruct.getLastName();
		this.companyName = userStruct.getCompanyName();
		this.email = userStruct.getEMail();
		this.token = userStruct.getToken();
		this.productId = product;
		// the basic ws has only one product
		addProduct(product, userStruct.getStatus(), userStruct.getRoles());
	}
	
	public int getUserId() {
		return userId;
	}

	public String getToken() {
		return token;
	}

	public String getUserName() {
		return userName;
	}

	public String getName() {
		return name;
	}

	public String getCompanyName() {
		return companyName;
	}

	public String getEmail() {
		return email;
	}

	public String getProductId() {
		return productId;
	}

	public Date getLoggedInTime() {
		return loggedInTime;
	}

	public void addProduct(String productId, String status, String[] roles) {
		products.add(new Product(productId, status, roles));
	}

	public String[] getRoles() {
		ArrayList roles = new ArrayList();
		for (Iterator iter = products.iterator(); iter.hasNext();) {
			Product product = (Product) iter.next();
			roles.addAll(Arrays.asList(product.getRoles()));
		}
		return (String[]) roles.toArray(new String[roles.size()]);
	}

	public boolean hasRole(String[] roles) {
		// intersecion() returns a new list containing all elements that are contained in both given lists
		List diff = ListUtils.intersection(Arrays.asList(getRoles()), Arrays.asList(roles));
		return !diff.isEmpty();
	}

	/**
	 * Check if user has passive role(s)
	 * 
	 * @return true If user has no active roles return and at least one matching passive role
	 */
	public boolean hasPassiveStatusForRole(String[] roles) {
		int passiveRoleMatches = 0;
		int activeRoleMatches = 0;
		for (Iterator iter = products.iterator(); iter.hasNext();) {
			Product product = (Product) iter.next();
			// intersecion() returns a new list containing all elements that are contained in both given lists
			List diff = ListUtils.intersection(Arrays.asList(product.getRoles()), Arrays.asList(roles));
			if(!diff.isEmpty()) {
				if(product.isPassive()) {
					passiveRoleMatches++;
				} else {
					activeRoleMatches++;
				}
			}
		}
		return (activeRoleMatches==0 && passiveRoleMatches > 0);
	}

	public String getAdminPage() {
		return null;
	}

	private class Product implements Serializable {
		
		private static final String STATUS_NORMAL = "NORMAL";
		private static final String STATUS_PASSIVE = "PASSIVE";
		private static final String STATUS_PENDING = "PENDING";

		String productId;
		String status;
		String[] roles;
		
		public Product(String productId, String status, String[] roles) {
			this.productId = productId;
			this.status = status;
			this.roles = new String[roles.length];
			for (int i = 0; i < roles.length; i++) {
				this.roles[i] = roles[i].trim();
			}
		}

		public String[] getRoles() {
			return roles;
		}

		public boolean isPassive() {
			return status.equals(STATUS_PASSIVE);
		}
		
	}

	public String[] getProductIds() {
		return null;
	}

	public List getProducts() {
		return products;
	}

	
}
