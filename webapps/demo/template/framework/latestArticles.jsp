<%@ taglib uri="/WEB-INF/escenic-article.tld" prefix="article"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>

<article:list all="true" id="latest" max="5" sectionId="${section.id}" includeSubSections='true' includeArticleTypes="news"/>
<table width="100%">
  <tr><th align="left">Latest articles</th></tr>
  <logic:iterate id="tmp" name="latest" type="neo.xredsys.presentation.PresentationArticle">
    <tr>
      <td>
        <article:use article="${tmp}" >
            <article:link>
              <article:field field="title">
                <article:field field="name">
                  (no title)
                </article:field>
              </article:field>  
            </article:link>
            <article:homeSection id="sec" />
            <logic:equal name="sec" property="agreementRequired" value="true">
            	<html:img page="/template/img/lock_icon.jpg" />
            </logic:equal>
            <br><font size="-2">(<article:lastChanged />)</font>
        </article:use>
      </td>
    </tr>
  </logic:iterate> 
</table>