<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/escenic-article.tld" prefix="article"%>
<%@ taglib uri="http://www.talentum.com/taglib/escenic-authenticator" prefix="auth"%>

<h1><article:field field="title" /></h1>
<i><article:field field="leadtext" /></i>
<br />

<c:choose>
	<c:when test="${auth:checkToken(cookie.pressdata_user,'T')}">
		<article:field field="body" />
	</c:when>
	<c:otherwise>
		<html:form action="/login">
		Username<br />
			<html:text property="username" />
			<br />
		PassWord<br />
			<html:password property="password" />
			<br />
			<html:submit>Log in</html:submit>
		</html:form>
	</c:otherwise>
</c:choose>
<br />
