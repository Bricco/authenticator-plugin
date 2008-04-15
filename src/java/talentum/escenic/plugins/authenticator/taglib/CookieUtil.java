package talentum.escenic.plugins.authenticator.taglib;

import javax.servlet.http.Cookie;

import talentum.escenic.plugins.authenticator.AuthenticatorManager;
import talentum.escenic.plugins.authenticator.PBEEncrypter;

public final class CookieUtil {
	
	public static Cookie getSessionCookie(String token) {
		Cookie cookie = new Cookie(AuthenticatorManager.getInstance().getCookieName(), token);
		cookie.setDomain(AuthenticatorManager.getInstance().getCookieDomain());
		cookie.setPath("/");
		return cookie;
	}

	public static Cookie getAutologinCookie(String userName, String password) {
		String cookieValue = userName + "|" + password;
		try {
			cookieValue = PBEEncrypter.encrypt(cookieValue);
		} catch (Exception e) {
			return null;
		}

		Cookie cookie = new Cookie(AuthenticatorManager.AUTOLOGIN_COOKIE, cookieValue);
		int autoLoginExpire = (60 * 60 * 24) * 100; // 100 days
		cookie.setDomain(AuthenticatorManager.getInstance().getCookieDomain());
		cookie.setPath("/");
		cookie.setMaxAge(autoLoginExpire);
		return cookie;
	}

	public static Cookie removeSessionCookie() {
		Cookie cookie = new Cookie(AuthenticatorManager.getInstance().getCookieName(), "");
		cookie.setMaxAge(0);
		cookie.setDomain(AuthenticatorManager.getInstance().getCookieDomain());
		cookie.setPath("/");
		return cookie;
	}

	public static Cookie removeAutologinCookie() {
		Cookie cookie = new Cookie(AuthenticatorManager.AUTOLOGIN_COOKIE, "");
		cookie.setMaxAge(0);
		cookie.setDomain(AuthenticatorManager.getInstance().getCookieDomain());
		cookie.setPath("/");
		return cookie;
	}
	
}
