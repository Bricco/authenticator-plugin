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
import org.apache.commons.httpclient.methods.PostMethod;
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

		// REST URL to check if a user has an active subscription to the
		// publication
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
							throw new AuthenticationException(
									"Wrong status from REST: "
											+ method.getStatusLine());
						} else {
							result = method.getResponseBodyAsString();
							try {
								document = DocumentHelper.parseText(result);
								String customerNo = document
										.selectSingleNode(
												"/AbonnemangöversiktContainer/Abonnemangöversikt/Mottagare/Kundnummer")
										.getStringValue();
								String firstName = document
										.selectSingleNode(
												"/AbonnemangöversiktContainer/Abonnemangöversikt/Mottagare/Förnamn")
										.getStringValue();
								String lastName = document
										.selectSingleNode(
												"/AbonnemangöversiktContainer/Abonnemangöversikt/Mottagare/Efternamn")
										.getStringValue();
								user = new TitelDataUser(customerNo, username,
										firstName, lastName);
							} catch (DocumentException e) {
								throw new AuthenticationException(
										"info result parsing failed", e);
							}
						}
					}
				} catch (DocumentException e) {
					throw new AuthenticationException(
							"active result parsing failed", e);
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

	/**
	 * Call
	 * https://webapi2.prenservicetest.se/Help/Api/GET-Konto-PasswordReminder-id
	 */
	public void passwordReminder(String emailAddress, String publication)
			throws ReminderException {

		// REST URL to password reminder
		String remindURI = RESTUrl.getProtocol() + "://" + RESTUrl.getHost()
				+ "/Konto/PasswordReminder/" + emailAddress + "?key=" + APIKey;
		if (log.isDebugEnabled()) {
			log.debug("REST uri: " + remindURI);
		}

		GetMethod method = new GetMethod(remindURI);

		try {
			// call the activation URL to see if user is active.
			int statusCode = httpClient.executeMethod(method);
			if (statusCode != HttpStatus.SC_OK) {
				throw new ReminderException("Wrong status from REST: "
						+ method.getStatusLine());
			} else {
				String result = method.getResponseBodyAsString();
				try {
					Document document = DocumentHelper.parseText(result);
					Node node = document.selectSingleNode("/boolean");
					if (node == null || node.getStringValue().equals("false")) {
						// the reminder failed, throw exception
						throw new ReminderException("reminder failed");
					}
				} catch (DocumentException e) {
					throw new ReminderException(
							"reminder result parsing failed", e);
				}

			}

		} catch (HttpException e) {
			if (log.isDebugEnabled()) {
				log.debug(e.getMessage(), e);
			}
			throw new ReminderException("http call failed", e);
		} catch (IOException e) {
			if (log.isDebugEnabled()) {
				log.debug(e.getMessage(), e);
			}
			throw new ReminderException("http call failed (i/o)", e);
		}
	}

	public void register(String username, String password, String postalCode,
			String customerNumber) throws RegistrationException {

		// REST URL to registration
		String registerURI = RESTUrl.getProtocol() + "://" + RESTUrl.getHost()
				+ "Konto/Register?key=" + APIKey;
		if (log.isDebugEnabled()) {
			log.debug("REST uri: " + registerURI);
		}

		PostMethod method = new PostMethod(registerURI);
		String body = "<NyttKonto>" + "<Kundnummer>" + customerNumber
				+ "</Kundnummer>" + "<Postnummer>" + postalCode
				+ "</Postnummer>" + "<Username>" + username + "</Username>"
				+ "<Password>" + password + "</Password>" + "<Epostadress>"
				+ username + "</Epostadress>" + "</NyttKonto>";
		method.setRequestBody(body);
		try {
			// call the activation URL to see if user is active.
			int statusCode = httpClient.executeMethod(method);
			if (statusCode != HttpStatus.SC_OK) {
				throw new RegistrationException("Wrong status from REST: "
						+ method.getStatusLine());
			} else {
				String result = method.getResponseBodyAsString();
				try {
					Document document = DocumentHelper.parseText(result);
					Node node = document.selectSingleNode("/boolean");
					if (node == null || node.getStringValue().equals("false")) {
						// the reminder failed, throw exception
						throw new RegistrationException("registration failed");
					}
				} catch (DocumentException e) {
					throw new RegistrationException(
							"registration result parsing failed", e);
				}

			}

		} catch (HttpException e) {
			if (log.isDebugEnabled()) {
				log.debug(e.getMessage(), e);
			}
			throw new RegistrationException("http call failed", e);
		} catch (IOException e) {
			if (log.isDebugEnabled()) {
				log.debug(e.getMessage(), e);
			}
			throw new RegistrationException("http call failed (i/o)", e);
		}

		
//		<NyttKonto>
//		  <Kundnummer>212044770</Kundnummer>
//		  <Postnummer>42738</Postnummer>
//		  <Username>stefan@bricco.se</Username>
//		  <Password>norman</Password>
//		  <Epostadress>stefan@bricco.se</Epostadress>
//		</NyttKonto>
		
		// TODO make call
	}

}
