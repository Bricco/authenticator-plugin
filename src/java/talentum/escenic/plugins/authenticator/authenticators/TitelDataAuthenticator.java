package talentum.escenic.plugins.authenticator.authenticators;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;

import talentum.escenic.plugins.authenticator.AuthenticationException;
import talentum.escenic.plugins.authenticator.ChangePasswordException;
import talentum.escenic.plugins.authenticator.DuplicateUserNameException;
import talentum.escenic.plugins.authenticator.RegistrationException;
import talentum.escenic.plugins.authenticator.ReminderException;

public class TitelDataAuthenticator extends Authenticator {

	private static Log log = LogFactory.getLog(TitelDataAuthenticator.class);

	private URL RESTUrl;
	private String APIKey;
	private String defaultRole;
	private int[] titelNr;
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

	public String getDefaultRole() {
		return defaultRole;
	}

	public void setDefaultRole(String defaultRole) {
		this.defaultRole = defaultRole;
	}

	public int[] getTitelNr() {
		return titelNr;
	}

	public void setTitelNr(int[] titelNr) {
		this.titelNr = titelNr;
	}

	public AuthenticatedUser authenticate(String username, String password,
			String ipaddress) throws AuthenticationException {

		TitelDataUser user = null;

		// set username and password credentials
		httpClient.getState().setCredentials(
				new AuthScope(RESTUrl.getHost(), 443),
				new UsernamePasswordCredentials(username, password));

		for (int i=0; i < titelNr.length; i++) {
			
			// REST URL to check if a user has an active subscription to the
			// publication
			String activeURI = RESTUrl.getProtocol() + "://" + RESTUrl.getHost()
					+ "/Abonnemang/AktivtAbonnemang/" + titelNr[i] + "?key=" + APIKey;
			// REST URL to get user info
			String infoURI = RESTUrl.getProtocol() + "://" + RESTUrl.getHost()
					+ "/Abonnemang/Oversikt?key=" + APIKey;
			// REST URL to get user roles
			String rolesURI = RESTUrl.getProtocol() + "://" + RESTUrl.getHost()
					+ "/Abonnemang/AktivaBilagor/" + titelNr[i] + "?key=" + APIKey;

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
					if (log.isDebugEnabled()) {
						log.debug("REST response " + result);
					}
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
								if (log.isDebugEnabled()) {
									log.debug("REST response " + result);
								}
								try {
									document = DocumentHelper.parseText(result);
									String customerNo = document
											.selectSingleNode(
													"/AbonnemangöversiktContainer/Abonnemangöversikt/Mottagare/Externtkundnummer")
											.getStringValue();

									// try to get Företagsnamn and fall back on Förnamn and Efternamn
									String name = "name-not-found";
									node = document
											.selectSingleNode("/AbonnemangöversiktContainer/Abonnemangöversikt/Mottagare/Företagsnamn");
									if (node != null) {
										name = node.getStringValue();
									} else {
										Node firstNameNode = document
												.selectSingleNode("/AbonnemangöversiktContainer/Abonnemangöversikt/Mottagare/Förnamn");
										Node lastNameNode = document
												.selectSingleNode("/AbonnemangöversiktContainer/Abonnemangöversikt/Mottagare/Efternamn");
										if (firstNameNode != null
												&& lastNameNode != null) {
											name = firstNameNode.getStringValue()
													+ " "
													+ lastNameNode.getStringValue();
										}
									}
									user = new TitelDataUser(customerNo, username,
											name);
								} catch (DocumentException e) {
									throw new AuthenticationException(
											"info result parsing failed", e);
								}
								// if defaultRole is set, we use it
								// otherwise, try to get the roles
								if (defaultRole != null) {
									user.addRole(defaultRole);
								} else {
									method = new GetMethod(rolesURI);
									statusCode = httpClient.executeMethod(method);
									if (statusCode != HttpStatus.SC_OK) {
										throw new AuthenticationException(
												"Wrong status from REST: "
														+ method.getStatusLine());
									} else {
										result = method.getResponseBodyAsString();
										try {
											document = DocumentHelper
													.parseText(result);
											List nodes = document
													.selectNodes("/ArrayOfBilaga/Bilaga");

											for (Iterator iterator = nodes
													.iterator(); iterator.hasNext();) {
												node = (Node) iterator.next();
												user.addRole(node.selectSingleNode(
														"Bilagakod")
														.getStringValue());
											}
										} catch (DocumentException e) {
											throw new AuthenticationException(
													"role result parsing failed", e);
										}
									}
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

		// remove credentials
		httpClient.getState().clearCredentials();

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
				if (log.isDebugEnabled()) {
					log.debug("REST response " + result);
				}
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

		// remove credentials
		httpClient.getState().clearCredentials();

		// REST URL to registration
		String registerURI = RESTUrl.getProtocol() + "://" + RESTUrl.getHost()
				+ "/Konto/Register?key=" + APIKey;

		PostMethod method = new PostMethod(registerURI);
		String body = "<NyttKonto>" + "<Kundnummer>" + customerNumber
				+ "</Kundnummer>" + "<Postnummer>" + postalCode
				+ "</Postnummer>" + "<Username>" + username + "</Username>"
				+ "<Password>" + password + "</Password>" + "<Epostadress>"
				+ username + "</Epostadress>" + "</NyttKonto>";
		if (log.isDebugEnabled()) {
			log.debug("REST uri: " + registerURI);
			log.debug("RequestBody: " + body);
		}
		try {
			method.setRequestHeader("Content-Type", "application/xml");
			method.setRequestEntity(new StringRequestEntity(body,
					"application/xml", "utf-8"));
			// call the activation URL to see if user is active.
			int statusCode = httpClient.executeMethod(method);
			if (statusCode != HttpStatus.SC_OK) {
				throw new RegistrationException("Wrong status from REST: "
						+ method.getStatusLine());
			} else {
				String result = method.getResponseBodyAsString();
				if (log.isDebugEnabled()) {
					log.debug("REST response " + result);
				}
				try {
					Document document = DocumentHelper.parseText(result);
					Node node = document
							.selectSingleNode("/NyttKontoResult/Status");
					if (node == null) {
						throw new RegistrationException(
								"registration failed: Node is null");
					} else if (node.getStringValue()
							.equals("DuplicateUserName")) {
						throw new DuplicateUserNameException(
								"registration failed: Username already in use");
					} else if (node.getStringValue()
							.equals("UserRejected")) {
						throw new UserRejectedException(
								"registration failed: Username was rejected");
					}
				} catch (DocumentException e) {
					throw new RegistrationException(
							"registration result parsing failed", e);
				}

			}

		} catch (UnsupportedEncodingException e) {
			if (log.isDebugEnabled()) {
				log.debug(e.getMessage(), e);
			}
			throw new RegistrationException("setting request body failed", e);
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

	}

	public void changePassword(String username, String oldPassword,
			String newPassword) throws ChangePasswordException {

		// set username and password credentials
		httpClient.getState().setCredentials(
				new AuthScope(RESTUrl.getHost(), 443),
				new UsernamePasswordCredentials(username, oldPassword));

		// REST URL to change password
		String changePasswdURI = RESTUrl.getProtocol() + "://" + RESTUrl.getHost()
				+ "/Konto/ChangePassword?key=" + APIKey;

		PostMethod method = new PostMethod(changePasswdURI);
		String body = "<Credentials>" + "<Username>" + username + "</Username>"
				+ "<Password>" + newPassword + "</Password>" + "</Credentials>";
		if (log.isDebugEnabled()) {
			log.debug("REST uri: " + changePasswdURI);
			log.debug("RequestBody: " + body);
		}

		try {
			method.setRequestHeader("Content-Type", "application/xml");
			method.setRequestEntity(new StringRequestEntity(body,
					"application/xml", "utf-8"));

			// call the activation URL to see if user is active.
			int statusCode = httpClient.executeMethod(method);
			if (statusCode != HttpStatus.SC_OK) {
				throw new ChangePasswordException("Wrong status from REST: "
						+ method.getStatusLine());
			} else {

				String result = method.getResponseBodyAsString();
				if (log.isDebugEnabled()) {
					log.debug("REST response " + result);
				}
				try {
					Document document = DocumentHelper.parseText(result);
					Node node = document.selectSingleNode("/boolean");
					if (node == null || !node.getStringValue().equals("true")) {
						throw new ChangePasswordException("Wrong response from REST");
					}
				} catch (DocumentException e) {
					throw new ChangePasswordException(
							"registration result parsing failed", e);
				}
			
			}		
		} catch (UnsupportedEncodingException e) {
			if (log.isDebugEnabled()) {
				log.debug(e.getMessage(), e);
			}
			throw new ChangePasswordException("setting request body failed", e);
		} catch (HttpException e) {
			if (log.isDebugEnabled()) {
				log.debug(e.getMessage(), e);
			}
			throw new ChangePasswordException("http call failed", e);
		} catch (IOException e) {
			if (log.isDebugEnabled()) {
				log.debug(e.getMessage(), e);
			}
			throw new ChangePasswordException("http call failed (i/o)", e);
		}
	}		

}
