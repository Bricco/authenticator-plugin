package talentum.escenic.plugins.authenticator.authenticators;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;

import talentum.escenic.plugins.authenticator.AuthenticationException;
import talentum.escenic.plugins.authenticator.RegistrationException;
import talentum.escenic.plugins.authenticator.ReminderException;

public class TitelDataAuthenticator extends Authenticator {

	private static Log log = LogFactory.getLog(TitelDataAuthenticator.class);

	private URL RESTUrl;
	private String APIKey;
	private int titelNr;
	private HttpClient httpClient;

	public TitelDataAuthenticator() {
		httpClient = new HttpClient();
	}

	public String getRESTUrl() {
		return RESTUrl.toString();
	}

	public void setRESTUrl(String rESTUrl) {
		try {
			RESTUrl = new URL(rESTUrl);
		} catch (MalformedURLException e) {
			log.error("malformed url", e);
		}
	}

	public String getAPIKey() {
		return APIKey;
	}

	public void setAPIKey(String aPIKey) {
		APIKey = aPIKey;
	}

	public int getTitelNr() {
		return titelNr;
	}

	public void setTitelNr(int titelNr) {
		this.titelNr = titelNr;
	}

	public AuthenticatedUser authenticate(String username, String password,
			String ipaddress) throws AuthenticationException {

		AuthenticatedUser user = null;

		// set username and password credentials
		httpClient.getState().setCredentials(
				new AuthScope(RESTUrl.getHost(), 443),
				new UsernamePasswordCredentials(username, password));

		// REST URL to check if a user has an active subscription to the publication
		String activeURI = RESTUrl.getProtocol() + "://" + RESTUrl.getHost()
				+ "/Abonnemang/AktivtAbonnemang/" + titelNr + "?key=" + APIKey;
		// REST URL to get user info
		String infoURI = RESTUrl.getProtocol() + "://" + RESTUrl.getHost()
				+ "/Abonnemang/Oversikt?key=" + APIKey;

		if (log.isDebugEnabled()) {
			log.debug("REST uri: " + activeURI);
		}

		GetMethod method = new GetMethod(activeURI);

		try {
			// call the activation URL to see if user is active.
			int statusCode = httpClient.executeMethod(method);
			if (statusCode != HttpStatus.SC_OK) {
				throw new AuthenticationException("Wrong status from REST: "
						+ method.getStatusLine());
			} else {
				String result = method.getResponseBodyAsString();
				try {
					Document document = DocumentHelper.parseText(result);
					Node node = document.selectSingleNode("/boolean");
					if (node != null && node.getStringValue().equals("true")) {
						// the user has active subscription
						// try to get his info to populate user object
						method = new GetMethod(infoURI);
						statusCode = httpClient.executeMethod(method);
						if (statusCode != HttpStatus.SC_OK) {
							throw new AuthenticationException("Wrong status from REST: "
									+ method.getStatusLine());
						} else {
							result = method.getResponseBodyAsString();
							try {
								document = DocumentHelper.parseText(result);
								String customerNo = document.selectSingleNode("/AbonnemangöversiktContainer/Abonnemangöversikt/Mottagare/Kundnummer").getStringValue();
								String firstName = document.selectSingleNode("/AbonnemangöversiktContainer/Abonnemangöversikt/Mottagare/Förnamn").getStringValue();
								String lastName = document.selectSingleNode("/AbonnemangöversiktContainer/Abonnemangöversikt/Mottagare/Efternamn").getStringValue();
								user = new TitelDataUser(customerNo, username, firstName, lastName);
							} catch (DocumentException e) {
								throw new AuthenticationException("info result parsing failed",
										e);
							}
						}						
					}
				} catch (DocumentException e) {
					throw new AuthenticationException("active result parsing failed",
							e);
				}

			}

		} catch (HttpException e) {
			if (log.isDebugEnabled()) {
				log.debug(e.getMessage(), e);
			}
			throw new AuthenticationException("http call failed", e);
		} catch (IOException e) {
			if (log.isDebugEnabled()) {
				log.debug(e.getMessage(), e);
			}
			throw new AuthenticationException("http call failed (i/o)", e);
		}

		return user;
	}

	public void logout(String token) {
		// not implemented
	}

	public void passwordReminder(String emailAddress, String publication)
			throws ReminderException {
		// not implemented
	}

	public void register(String username, String password)
			throws RegistrationException {
		// not implemented
	}

}
