package talentum.escenic.plugins.authenticator.taglib;

import talentum.escenic.plugins.authenticator.AuthenticatorManager;
import talentum.escenic.plugins.authenticator.authenticators.AuthenticatedUser;

public final class AuthenticatorUtil {

	public static boolean checkToken(String token, String role) {

		AuthenticatedUser user = AuthenticatorManager.getInstance().getVerifiedUser(token);
		if(user == null) {
			return false;
		}
		return user.hasRole(role);

	}
}