package talentum.escenic.plugins.authenticator.authenticators;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;
import javax.xml.rpc.holders.BooleanHolder;
import javax.xml.rpc.holders.StringHolder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import se.talentumhr.webservices.LoginAccountServiceLocator;
import se.talentumhr.webservices.LoginAccountServiceSoapStub;

/**
 * Implements authentication through Talentum HR LoginAccount web service.
 * 
 * @author stefan.norman
 */
public class TalentumHRLoginAccountAuthenticator extends WSAuthenticator {

	private static Log log = LogFactory
			.getLog(TalentumHRLoginAccountAuthenticator.class);

	private String endPoint;

	private String adminPageURL;

	public String getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(String endPoint) {
		this.endPoint = endPoint;
	}

	public String getAdminPageURL() {
		return adminPageURL;
	}

	public void setAdminPageURL(String adminPageURL) {
		this.adminPageURL = adminPageURL;
	}

	public AuthenticatedUser performLogin(String username, String password,
			String ipaddress) throws ServiceException, RemoteException {

		TalentumHRUser user = null;

		StringHolder artefact = new StringHolder();
		StringHolder name = new StringHolder();

		// call web service to authenticate
		// send publicationName to differentiate on branch
		int responseCode = getLoginBinding().login(username, password,
				ipaddress, getPublicationName(), artefact, name);

		if (responseCode < 0) {
			log.error("Authentication failed for user " + username
					+ ". Error from web service (error code " + responseCode
					+ ")");
		} else {

			// populate user object
			user = new TalentumHRUser(artefact.value, name.value, username,
					getAdminPageURL());

			if (log.isDebugEnabled()) {
				log.debug("User " + user.getName()
						+ " logged in and got artefact " + user.getToken());
			}

			// get further info for my page

			StringHolder customerName = new StringHolder();
			StringHolder companyName = new StringHolder();
			StringHolder customerNo = new StringHolder();
			BooleanHolder isLinkUser = new BooleanHolder();

			responseCode = getLoginBinding().getUserInformation(
					artefact.value, customerName, companyName, customerNo,
					isLinkUser);
			if (responseCode < 0) {
				log.error("Getting user info for user " + username
						+ ". Error from web service (error code "
						+ responseCode + ")");
			} else {
				user.setUserId(Integer.parseInt(customerNo.value));
				user.setCompanyName(companyName.value);
				user.setLinkUser(isLinkUser.value);
			}

			StringHolder productLinkList = new StringHolder();

			responseCode = getLoginBinding().getProductLinkList(
					artefact.value, productLinkList);
			if (responseCode < 0) {
				log.error("Getting product links for user " + username
						+ ". Error from web service (error code "
						+ responseCode + ")");
			} else {
				// parse productLinkList.
				// format: [product name]@[link to product]|[product name]@[link to product]
				String[] products = productLinkList.value.split("\\|");
				for (int i = 0; i < products.length; i++) {
					String[] values = products[i].split("@");
					user.addProduct(values[0], values[1]);
				}
			}

			StringHolder filterList = new StringHolder();

			responseCode = getLoginBinding().getProductIdFilter(
					artefact.value, filterList);
			if (responseCode < 0) {
				log.error("Getting product filters for user " + username
						+ ". Error from web service (error code "
						+ responseCode + ")");
			} else {
				// parse productLinkList.
				// format: [product id],[product id],[product id]
				String[] productIDs = filterList.value.split(",");
				user.setProductIds(productIDs);
			}

		}

		return user;
	}

	protected void performLogout(String artefact) throws ServiceException,
			RemoteException {

		// call web service to log out
		boolean logoutOK = getLoginBinding().logout(artefact);

		if (!logoutOK) {
			log.error("Logout failed for artefact " + artefact);
		}

	}

	protected boolean performPasswordReminder(String emailAddress)
			throws ServiceException, RemoteException {
		// call web service to log out
		// if method returns 0 everything is ok
		return getLoginBinding().forgotPassword(emailAddress) == 0;

	}

	private LoginAccountServiceSoapStub getLoginBinding()
			throws ServiceException {

		LoginAccountServiceLocator locator = new LoginAccountServiceLocator();

		// set endpoint from configuration
		locator.setEndpointAddress("LoginAccountServiceSoap", getEndPoint());

		LoginAccountServiceSoapStub binding = (LoginAccountServiceSoapStub) locator
				.getLoginAccountServiceSoap();

		binding.setTimeout(getTimeout());

		return binding;
	}

}
