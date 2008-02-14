package talentum.escenic.plugins.authenticator.authenticators;

public interface Authenticator {

	public AuthenticatedUser authenticate(String username, String password);
	
	public AuthenticatedUser verifyUser(String token);
	
}
