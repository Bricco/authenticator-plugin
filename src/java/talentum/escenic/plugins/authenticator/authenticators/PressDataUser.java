package talentum.escenic.plugins.authenticator.authenticators;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;

import net.kundservice.www.WS.Authorization.UserStruct;
import net.kundservice.www.prenstatusws.login.ProductDto;
import net.kundservice.www.prenstatusws.login.UserStatusDto;

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

	public boolean hasRole(String role) {
		String roles[] = getRoles();
		for (int i = 0; i < roles.length; i++) {
			if(roles[i].equals(role)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return true if one product has passive status and one of its roles matches
	 */
	public boolean hasPassiveStatusForRole(String role) {
		for (Iterator iter = products.iterator(); iter.hasNext();) {
			Product product = (Product) iter.next();
			if(product.isPassive()) {
				String[] roles = product.getRoles();
				for (int i = 0; i < roles.length; i++) {
					if(roles[i].equals(role)) {
						return true;
					}
				}
			}
		}
		return false;
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

}
