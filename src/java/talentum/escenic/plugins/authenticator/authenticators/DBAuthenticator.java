package talentum.escenic.plugins.authenticator.authenticators;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import neo.dbaccess.Transaction;
import neo.dbaccess.TransactionOperation;
import neo.xredsys.content.ContentManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import talentum.escenic.plugins.authenticator.AuthenticationException;
import talentum.escenic.plugins.authenticator.ChangePasswordException;
import talentum.escenic.plugins.authenticator.RegistrationException;
import talentum.escenic.plugins.authenticator.ReminderException;

/**
 * Database authenticator.
 * 
 * @author stefan.norman
 */
public class DBAuthenticator extends Authenticator {

	private static Log log = LogFactory.getLog(DBAuthenticator.class);

	private String table;
	private String userClass;
	private String reference;
	private HashMap columns = new HashMap();
	private final String ENCRYPT_SALT = "salt_and_pepper";

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getUserClass() {
		return userClass;
	}

	public void setUserClass(String userClass) {
		this.userClass = userClass;
	}
	
	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public void addColumn(String column,
			String columnName) {
		columns.put(column, columnName);
	}

	public AuthenticatedUser authenticate(String username, String password,
			String ipaddress) throws AuthenticationException {
		AuthenticatedUser user = null;
		if (username == null || username.trim().length() == 0
				|| password == null || password.trim().length() == 0) {
			throw new AuthenticationException(
					"Authentication failed: Invalid arguments");
		}
		try {


			ContentManager contentManager = ContentManager.getContentManager();
			List result = new ArrayList();
			// build the SQL
			// it's possible to configure a "altusername",
			// i e email address, that can be used in conjunction with "username"
			String sql = "SELECT * FROM " + table + " WHERE " +
					(
							columns.get("altusername") != null ?
									"(" + columns.get("username") + "= ?  OR " + columns.get("altusername") + "= ? )" :
									columns.get("username") + "= ? "
					)
					+ " AND "
					+ columns.get("password") + "= ENCRYPT(?, '" + ENCRYPT_SALT + "') AND "					
					+ columns.get("reference") + "=?";
			if(log.isDebugEnabled()) {
				log.debug(sql);				
			}
			String[] queryParams = new String[] { username, password, reference};
			if(columns.get("altusername") != null) {
				queryParams = new String[] { username, username, password, reference};
			}
			if(log.isDebugEnabled()) {
				log.debug("Query parameters: " + Arrays.toString(queryParams));				
			}
			contentManager.doQuery(new Query(sql, queryParams, result));
			
			if(log.isDebugEnabled()) {
				log.debug("found " + result.size() + " records");
			}
			if(result.size() > 0) {
				// get the first found row and create user object
				Map row = (Map) result.get(0);

				// intantiate the user class an add the map
				Class clazz = Class.forName(userClass);
				if(log.isDebugEnabled()) {
					log.debug("creating user class " + clazz.getName());
				}
				DBUser dbUser = (DBUser)clazz.newInstance();
				dbUser.init(row);
				user = dbUser;
			}

		} catch (Exception e) {
			log.error("Authentication failed: Finding user failed");
			if (log.isDebugEnabled()) {
				log.debug(e.getMessage(), e);
			}
		}
		if (user == null) {
			throw new AuthenticationException(
					"Authentication failed: User not found");
		}
		return user;
	}

	public void logout(String token) {
		// do nothing
	}

	public void passwordReminder(String emailAddress, String publication)
			throws ReminderException {
		// do nothing
	}

	public void register(String username, String password, String postalCode,
			String customerNumber)
			throws RegistrationException {
		// do nothing
	}

	private static class Query implements TransactionOperation {
		private String query;
		private String[] args;
		private List list;

		public Query(String query, String[] args, List list) {
			this.query = query;
			this.args = args;
			this.list = list;
		}

		public void execute(Transaction t) throws SQLException {
			PreparedStatement prepStmt = t.getConnection().prepareStatement(query);
			for (int i = 0; i < args.length; i++) {
				prepStmt.setString(i+1, args[i]);
			}
			try {
				ResultSet rs = prepStmt.executeQuery();
				ResultSetMetaData metaData = rs.getMetaData();
				while (rs.next()) {
					Map map = new HashMap();
					for (int i = 0; i < metaData.getColumnCount(); i++) {
						map.put(metaData.getColumnLabel(i + 1), rs.getString(i + 1));
					}
					list.add(map);
				}
	        } finally {
	        	prepStmt.close();
	        }
		}
	}

	public void changePassword(String username, String oldPassword,
			String newPassword) throws ChangePasswordException {
		// do nothing
	}
}