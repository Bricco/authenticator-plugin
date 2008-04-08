package talentum.escenic.plugins.authenticator.taglib;

import javax.servlet.http.Cookie;

import talentum.escenic.plugins.authenticator.AuthenticatorManager;
import talentum.escenic.plugins.authenticator.PBEEncrypter;

public final class CookieUtil {
	
	public static Cookie getRemovalCookie(String cookieName, String cookieDomain) {
		Cookie cookie = new Cookie(cookieName, "");
		cookie.setMaxAge(0);
		cookie.setDomain(cookieDomain);
		cookie.setPath("/");
		return cookie;
	}

	public static Cookie getAutologinCookie(String userName, String password, String cookieDomain) {
		String cookieValue = userName + "|" + password;
		try {
			cookieValue = PBEEncrypter.encrypt(cookieValue);
		} catch (Exception e) {
			return null;
		}

		Cookie cookie = new Cookie(AuthenticatorManager.AUTOLOGIN_COOKIE, cookieValue);
		int autoLoginExpire = (60 * 60 * 24) * 100; // 100 days
		cookie.setDomain(cookieDomain);
		cookie.setPath("/");
		cookie.setMaxAge(autoLoginExpire);
		return cookie;
	}
}
