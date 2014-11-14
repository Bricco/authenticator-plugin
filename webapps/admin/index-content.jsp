<%@ page
	import="talentum.escenic.plugins.authenticator.AuthenticatorManager"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>

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
			<th align="left" colspan="9">Logged in users</th>
		</tr>
        <tr>
          <th>ID</th>
          <th>USERNAME</th>
          <th>NAME</th>
          <th>COMPANY</th>
          <th>ROLES</th>
          <th>EMAIL</th>
          <th>LOGGED IN</th>
          <th>TOKEN</th>
          <th></th>
        </tr>
		<logic:iterate id="user" name="authenticatorManager" property="loggedInUsers">
			<tr>
				<td><bean:write name="user" property="userId" /></td>
				<td><bean:write name="user" property="userName" /></td>
				<td><bean:write name="user" property="name" /></td>
				<td><bean:write name="user" property="companyName" /></td>
                <td>
                  <logic:iterate id="role" name="user" property="roles">
                    <bean:write name="role" />
                  </logic:iterate>
                </td>
				<td><a href="mailto:<bean:write name="user" property="email" />"><bean:write name="user" property="email" /></a></td>
				<td><bean:write name="user" property="loggedInTime" format="yyyy-MM-dd hh:mm" /></td>
                <td><bean:write name="user" property="token" /></td>
                <td><a href="index.jsp?evictUser=<bean:write name="user" property="token" />">EVICT</a></td>
			</tr>
		</logic:iterate>
	</table>
</logic:present>
<logic:notPresent name="authenticatorManager">
	<p>Could not find authenticatorManager. Authenticator plugin is not properly installed.</p>
</logic:notPresent>
