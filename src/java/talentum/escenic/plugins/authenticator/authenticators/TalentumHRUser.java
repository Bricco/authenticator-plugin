package talentum.escenic.plugins.authenticator.authenticators;

import java.util.Date;

public class TalentumHRUser implements AuthenticatedUser {

	private String artefact;
	private String name;
	private String userName;
	private String myPage;
	private Date loggedInTime = new Date();

	/**
	 * Constructs a user from the LoginService web service
	 * @param userSDto
	 */
	public TalentumHRUser(String artefact, String name, String userName, String myPage) {
		this.artefact = artefact;
		this.name = name;
		this.userName = userName;
		this.myPage = myPage;
	}
	
	public int getUserId() {
		return -1;
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

	public String getCompanyName() {
		return null;
	}

	public String getEmail() {
		return null;
	}

	public String getProductId() {
		return null;
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

	public String getMyPage() {
		return myPage + "?artefact=" + getToken();
	}


}
