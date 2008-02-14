<%@ taglib uri="/WEB-INF/escenic-article.tld" prefix="article" %>
<%@ taglib uri="/WEB-INF/escenic-relation.tld" prefix="relation" %>

<table cellspacing="5">
  <tr>
    <td>
      <span class="title">
        <article:field field="TITLE">
          <article:field field="NAME"/>
        </article:field>
      </span>
      <div>
        <article:hasRelation type="image">
          <relation:images id="image" field="" max="1" version="b,c">
            <table align=<%=image.getAlignment()%>" border="0">
              <tr>
                <td>
                  <img src="<%=image.getUrl()%>"/>
                </td>
              </tr>
              <tr>
                <td>
                  <span class="caption">
                    <%=image.getCaption()%>
                  </span>
                </td>
              </tr>
            </table>
          </relation:images>
        </article:hasRelation>
        <article:field field="frontpageleadtext" />
      </div>
      <article:link>
        <article:field field="linktext">
          Read more
        </article:field>
      </article:link>
    </td>
  </tr>
</table>
