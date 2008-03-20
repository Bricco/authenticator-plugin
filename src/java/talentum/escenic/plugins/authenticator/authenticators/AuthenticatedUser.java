package talentum.escenic.plugins.authenticator.authenticators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * Internal representation of a valid user
 * 
 * @author stefan.norman
 */
public class AuthenticatedUser {

	private int userId;

	private String userName;

	private String email;

	private String name;

	private String status;

	private boolean autologin;

	private String companyName;
	
	private String token;
	
	private Date lastChecked;
	
	private ArrayList roles = new ArrayList();

	public boolean isAutologin() {
		return autologin;
	}

	public void setAutologin(boolean autologin) {
		this.autologin = autologin;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Date getLastChecked() {
		return lastChecked;
	}

	public void setLastChecked(Date lastChecked) {
		this.lastChecked = lastChecked;
	}

	public String[] getRoles() {
		return (String[]) roles.toArray(new String[roles.size()]);
	}

	public void addRole(String role) {
		if(!roles.contains(role))
			roles.add(role);
	}
	
	public boolean hasRole(String role) {
		return Arrays.binarySearch(getRoles(), role) > 0;
	}

	public String toString() {
		return getUserId() + " " + getUserName();
	}

}
