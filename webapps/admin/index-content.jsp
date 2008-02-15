<%@ page
	import="talentum.escenic.plugins.authenticator.AuthenticatorManager"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>

<h1>Authenticator</h1>

<%
AuthenticatorManager manager = AuthenticatorManager.getInstance();

// if user clicked "EVICT"
if(request.getParameter("evictUser") != null) {
	manager.evictUser(request.getParameter("evictUser"));
}

request.setAttribute("authenticatorManager", manager);
%>

<logic:present name="authenticatorManager">
	<table cellspacing="1" cellpadding="1" border="1">
		<tr>
			<th align="left" colspan="7">Logged in users</th>
		</tr>
		<logic:iterate id="user" name="authenticatorManager" property="loggedInUsers">
			<tr>
				<td><bean:write name="user" property="userId" /></td>
				<td><bean:write name="user" property="userName" /></td>
				<td><bean:write name="user" property="name" /></td>
				<td><bean:write name="user" property="companyName" /></td>
				<td><a href="mailto:<bean:write name="user" property="email" />"><bean:write name="user" property="email" /></a></td>
				<td><bean:write name="user" property="lastChecked" format="yyyy-MM-dd hh:mm" /></td>
				<td><a href="index.jsp?evictUser=<bean:write name="user" property="token" />">EVICT</a></td>
			</tr>
		</logic:iterate>
	</table>
</logic:present>
<logic:notPresent name="authenticatorManager">
	<p>Could not find authenticatorManager. Authenticator plugin is not properly installed.</p>
</logic:notPresent>