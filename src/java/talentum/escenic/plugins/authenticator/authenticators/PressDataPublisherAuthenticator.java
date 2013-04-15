package talentum.escenic.plugins.authenticator.authenticators;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import net.kundservice.www.prenstatusws.loginservice.LoginServiceLocator;
import net.kundservice.www.prenstatusws.loginservice.LoginServiceSoapStub;
import net.kundservice.www.prenstatusws.loginservice.NTUserStatusDto;
import net.kundservice.www.prenstatusws.loginservice.UserStatusDto;
import net.kundservice.www.prenstatusws.prenservice_asmx.PrenServiceLocator;
import net.kundservice.www.prenstatusws.prenservice_asmx.PrenServiceSoapStub;
import net.kundservice.www.registersubscriptionservicews.NTOrderDto;
import net.kundservice.www.registersubscriptionservicews.RegisterSubscriptionServiceLocator;
import net.kundservice.www.registersubscriptionservicews.RegisterSubscriptionServiceSoap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import talentum.escenic.plugins.authenticator.RegistrationException;

/**
 * Implements authentication through Pressdata LoginService web service.
 * 
 * @author stefan.norman
 */
public class PressDataPublisherAuthenticator extends WSAuthenticator {

	private static Log log = LogFactory
			.getLog(PressDataPublisherAuthenticator.class);

	private String publisher;
	private String product;
	private int coupon;

	private int ntuserValidDays = 0;

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	
	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public int getCoupon() {
		return coupon;
	}

	public void setCoupon(int coupon) {
		this.coupon = coupon;
	}

	public int getNtuserValidDays() {
		return ntuserValidDays;
	}

	public void setNtuserValidDays(int ntuserValidDays) {
		this.ntuserValidDays = ntuserValidDays;
	}

	public AuthenticatedUser performLogin(String username, String password,
			String ipaddress) throws ServiceException, RemoteException {

		AuthenticatedUser user = null;

		// call web service to authenticate
		LoginServiceSoapStub binding = (LoginServiceSoapStub) new LoginServiceLocator()
				.getLoginServiceSoap();

		binding.setTimeout(getTimeout());

		UserStatusDto userSDto = binding.loginPublisher(publisher, username,
				password);

		if (userSDto.isIsLoginOk()) {
			
			// check if POEM user is a previous NTUser
			PrenServiceSoapStub bind = (PrenServiceSoapStub) new PrenServiceLocator()
					.getPrenServiceSoap();
			int ntUserId = -1;
			try {
				// Axis 1.4 has a bug that makes nil responses throw NullPointerExcepition (POC-914)
				ntUserId = bind.getNTUserIdFromPoemUser(userSDto.getUserId());
			} catch (NullPointerException e) {
				if(log.isDebugEnabled()) {
					log.debug("got nullpointer from axis for user " + userSDto.getUserId());
				}
			}

			// populate user object
			user = new PressDataUser(userSDto, ntUserId);
			
		} else {
			// fallback: check the LoginNTUser for authentication
			NTUserStatusDto ntUserSDto = binding.loginNTUser(publisher,
					username, password);
			if (ntUserSDto.isLoginOK()) {
				// populate user object
				user = new PressDataUser(ntUserSDto, getNtuserValidDays());
			} else {
				log.error("Authentication failed for user " + username);
			}
		}

		return user;
	}

	public void register(String username, String password)
			throws RegistrationException {
		try {

			RegisterSubscriptionServiceSoap binding = (RegisterSubscriptionServiceSoap) new RegisterSubscriptionServiceLocator()
					.getRegisterSubscriptionServiceSoap();
			boolean success = binding.registerNTOrder(new NTOrderDto(username,
					password, username, coupon, product));
			if(!success) {
				log.error("Registration failed. WS returned false.");
			}
		} catch (RemoteException e) {
			log.error("Registration failed", e);
			throw new RegistrationException("Registration failed");
		} catch (ServiceException e) {
			log.error("Registration failed", e);
			throw new RegistrationException("Registration failed");
		}

	}

}
