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

public class KayakUser extends DBUser {

	private static Log log = LogFactory
			.getLog(KayakUser.class);

	private Date timestamp = new Date();
	
	public int getUserId() {
		return Integer.parseInt((String)map.get("id"));
	}

	public int getSourceUserId() {
		return getUserId();
	}

	public String getToken() {
		return getUserId() + String.valueOf(timestamp.getTime());
	}

	public String getUserName() {
		return (String)map.get("id");
	}

	public String getName() {
		return (String)map.get("name");
	}

	public String getCompanyName() {
		return null;
	}

	public String[] getRoles() {
		return new String[] {"T"};
	}

	public String getEmail() {
		return (String)map.get("email");
	}

	public String getProductId() {
		return null;
	}

	public int getCustomerNumber() {
		return Integer.parseInt((String)map.get("id"));
	}

	public Date getExpirationDate() {
		return null;
	}

	public Date getLoggedInTime() {
		return timestamp;
	}

	public String[] matchingRoles(String[] roles) {
		// intersecion() returns a new list containing all elements that are contained in both given lists
		List diff = ListUtils.intersection(Arrays.asList(getRoles()), Arrays.asList(roles));
		return (String[]) diff.toArray(new String[diff.size()]	);
	}

	public boolean hasPassiveStatusForRole(String[] roles) {
		
		String status = (String)map.get("status");
		log.debug("status:" + status);

		// check status "dygn"
		if(status.equals("dygn")) {
			
			Date activationDate = null;
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
			String activattionDateStr = (String)map.get("activationdate");
			try {
				activationDate = format.parse(activattionDateStr);
				log.debug("activationDate:" + activationDate);
			} catch (ParseException e) {
				log.error("Could not parse expiration date " + activattionDateStr
						+ "of customer " + getCustomerNumber(), e);
			}
			
			if(activationDate == null) {
				log.error("User "
						+ getCustomerNumber()
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

	/**
	 * Return passive status in not the nicest fashion. Used in "my page".
	 */
	public boolean isPassive() {
		return hasPassiveStatusForRole(null);
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
