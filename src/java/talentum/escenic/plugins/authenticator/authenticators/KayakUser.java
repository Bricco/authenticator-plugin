package talentum.escenic.plugins.authenticator.authenticators;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class KayakUser implements AuthenticatedUser {

	private String token;
	private String customerNo;
	private String name;
	private String postalCode;
	private String status;
	private Date loggedInTime = new Date();
	private Date activationDate = null;
	
	private static Log log = LogFactory
			.getLog(KayakUser.class);

	/**
	 * 
	 * @param line an array of field on this format 
	 * AN;P MOF;72108563;67150;aktiv;20121214;20130613;ANNLISA;CARLSSON;CARLSSON ANNLISA;Ja;20120601 21:05:21
	 */
	public KayakUser(String[] line) {
		this.token = line[2] + String.valueOf(System.currentTimeMillis());
		this.customerNo = line[2];
		this.postalCode = line[3];
		this.status = line[4];
		this.name = line[7] + " " + line[8];
		if(line.length >= 11) {
			log.debug("activationDateString:" + line[11]);
			DateFormat format = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
			try {
				activationDate = format.parse(line[11]);
				log.debug("activationDate:" + activationDate);
			} catch (ParseException e) {
				log.error("Could not parse expiration date " + line[11]
						+ "of customer " + customerNo, e);
			}
		}
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
		
		log.debug("status:" + status);

		// check status "dygn"
		if(status.equals("dygn")) {
			if(activationDate == null) {
				log.error("User "
						+ this.customerNo
						+ " has status 'dygn' but no activation date set. That's weird so we return passive.");
				return true;
			} else {
				// check activation date
				boolean isActive = true;
				if(System.currentTimeMillis() < activationDate.getTime()) {
					// activation date hasn't been reached yet
					isActive = false;
					log.debug("activation date hasn't been reached yet");
				}  
				if(System.currentTimeMillis() > (24 * 60 * 60 * 1000 + activationDate.getTime())) {
					// current time has passed actviation date + 24hrs
					isActive = false;
					log.debug("current time has passed actviation date + 24hrs");
				}
				// if not active return has passive
				return !isActive;
			}
		} else {
			// all other statuses that don't have "aktiv" are considered passive.
			return !status.equals("aktiv");
		}
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
