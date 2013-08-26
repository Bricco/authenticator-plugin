package talentum.escenic.plugins.authenticator.authenticators;

import java.util.Map;

public abstract class DBUser implements AuthenticatedUser {

	protected Map map;

	public void init(Map map) {
		this.map = map;
	}

}
