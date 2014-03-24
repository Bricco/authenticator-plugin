package talentum.escenic.plugins.authenticator.authenticators;

import java.io.FileReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import talentum.escenic.plugins.authenticator.AuthenticationException;
import talentum.escenic.plugins.authenticator.ChangePasswordException;
import talentum.escenic.plugins.authenticator.RegistrationException;
import talentum.escenic.plugins.authenticator.ReminderException;
import au.com.bytecode.opencsv.CSVReader;

/**
 * CSV File authenticator. (not in use, keeping code for reference)
 * 
 * @author stefan.norman
 */
public class CSVFileAuthenticator extends Authenticator {

	private static Log log = LogFactory.getLog(CSVFileAuthenticator.class);

	String filePath;

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public AuthenticatedUser authenticate(String username, String password,
			String ipaddress) throws AuthenticationException {
		AuthenticatedUser user = null;
		if (username == null || username.trim().length() == 0
				|| password == null || password.trim().length() == 0) {
			throw new AuthenticationException(
					"Authentication failed: Invalid arguments");
		}
		try {

			String customerNo = new String(username);
			String postalCode = new String(password);

			CSVReader reader = new CSVReader(new FileReader(getFilePath()), ';');
			List entries = reader.readAll();

			// sort entries of list based on second column (customer number)
			Collections.sort(entries, getComparator());

			int index = Collections.binarySearch(entries, new String[] {"","",customerNo, ""},
					getComparator());

			if (index > 0) {
				String[] line = (String[]) entries.get(index);
				if (line[3].equals(postalCode)) {
					// we have a match, create user object
//					user = new KayakUser(line);
				}
			}

		} catch (Exception e) {
			log.error("Authentication failed: Finding user failed");
			if (log.isDebugEnabled()) {
				log.debug(e.getMessage(), e);
			}
		}
		if (user == null) {
			throw new AuthenticationException(
					"Authentication failed: User not found");
		}
		return user;
	}

	public void logout(String token) {
		// do nothing
	}

	public void passwordReminder(String emailAddress, String publication)
			throws ReminderException {
		// do nothing
	}

	public void register(String username, String password, String postalCode,
			String customerNumber)
			throws RegistrationException {
		// do nothing
	}

	private Comparator getComparator() {
		return new Comparator() {
			public int compare(Object o1, Object o2) {
				String[] arr1 = (String[]) o1;
				String[] arr2 = (String[]) o2;
				Integer i1 = new Integer(arr1[2]);
				Integer i2 = new Integer(arr2[2]);
				return i1.compareTo(i2);
			}
		};
	}

	public void changePassword(String username, String oldPassword,
			String newPassword) throws ChangePasswordException {
		// do nothing
	}
}
