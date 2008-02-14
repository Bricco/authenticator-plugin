<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>

<html:form action="/login">
	Username<br />
	<html:text property="username" />
	<br />
	PassWord<br />
	<html:password property="password" />
	<br />
	<html:submit>Log in</html:submit>
</html:form>


<html:link action="/logout">Log out</html:link>
