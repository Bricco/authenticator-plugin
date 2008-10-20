package talentum.escenic.plugins.authenticator.authenticators;


/**
 * Abstract WebService authenticator.
 * 
 * @author stefan.norman
 */
public abstract class WSAuthenticator extends Authenticator {

	private int timeout = 15000;

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	
	
}
