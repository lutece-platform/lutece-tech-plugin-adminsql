<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:useBean id="adminsql" scope="session" class="fr.paris.lutece.plugins.adminsql.web.AdminSqlJspBean" />

<%
    adminsql.init( request , adminsql.RIGHT_MANAGE_ADMIN_SQL );
    response.sendRedirect( adminsql.doModifyFieldStructure( request ) );
%>
