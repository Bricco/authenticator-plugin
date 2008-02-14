<%@ taglib uri="/WEB-INF/escenic-section.tld" prefix="section"%>
<%@ taglib uri="/WEB-INF/escenic-view.tld" prefix="view"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>

<table cellpadding="6" width="100%">
  <tr>
    <td valign="top">
      <section:recursiveView id="secMain" section="${publication.rootSection}" />
      <ul>
      <view:iterate view="${secMain}" id="item" type="neo.xredsys.api.Section">
        <section:use section="${item}">
            <view:forEachLevel startAt="0"><ul></view:forEachLevel>
              <li>
              	<section:link><section:name /></section:link>
	            <logic:equal name="item" property="agreementRequired" value="true">
	            	<html:img page="/template/img/lock_icon.jpg" />
	            </logic:equal>
              </li>
              
            <view:forEachLevel startAt="0"></ul></view:forEachLevel>
        </section:use>
      </view:iterate>
      </ul>
    </td>
  </tr>
</table>
