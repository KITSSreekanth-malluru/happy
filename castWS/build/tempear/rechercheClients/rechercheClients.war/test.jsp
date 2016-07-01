<%@ page language="java" contentType="text/html; charset=UTF-8" %>

<%@ page import="com.castorama.webservices.clients.ClientRechercheClients" %>
<%@ page import="com.castorama.webservices.CastoRechercheClientsManager" %>
<%@ page import="atg.nucleus.Nucleus" %>

<html>
<head>
<title>Test du webservice rechercheClients</title>
<style>
.request,.response { background-color: #ccc; border: 1px solid black; padding: 5px; font-family: Courier }
.valeur { font-weight: bold; color: black }
.balise { color: blue }
</style>
</head>
<body>

<h1>Test du webservice rechercheClients</h1>

<%!
String ajoutChamps(String nomAffiche, String nomParametre, String type, String current) {
	String ret = "<tr><td><label for=\"" + nomParametre + "\">" + nomAffiche + "</label></td><td><input type=\"" + type + "\" name=\"" + nomParametre + "\" value=\"";
	if (current != null)
		ret += current;
	ret += "\" /></td></tr>";
	return ret;
}

String replaceChevrons(String msg)
{
//	return msg.replaceAll("<([^>]*)>([^<]*)</([^>]*)>", "&lt;<span class='balise'>$1</span>&gt;<span class='valeur'>$2</span>&lt;/<span class='balise'>$3</span>&gt;<br />");
	if (msg == null)
		return null;
	return msg.replaceAll("<([^>]*)>", "&lt;<span class='balise'>$1</span>&gt;").replaceAll("&gt;&lt;", "&gt;<br />&lt;");
}

String replaceSpecials(String msg)
{
	if (msg == null)
		return null;
	return msg.replaceAll("<", "&gt;").replaceAll(">", "&lt;").replaceAll("&", "&amp;");
}
%>

<% request.setCharacterEncoding("UTF-8"); %>

<form name="form1" action="test.jsp" method="post">
	<table>
	<%=ajoutChamps("N° Client", "id", "text", request.getParameter("id"))%>	
	<%=ajoutChamps("E-mail", "email", "text", request.getParameter("email"))%>
	<tr><td colspan="2"><hr /></td></tr>	
	<%=ajoutChamps("Nom", "nom", "text", request.getParameter("nom"))%>
	<%=ajoutChamps("Pr&eacute;nom", "prenom", "text", request.getParameter("prenom"))%>
	<%=ajoutChamps("Code postal", "codePostal", "text", request.getParameter("codePostal"))%>
	<tr><td colspan="2"><hr /></td></tr>	
	<%=ajoutChamps("Page Size", "pageSize", "text", request.getParameter("pageSize"))%>
	<%=ajoutChamps("Page Number", "pageNumber", "text", request.getParameter("pageNumber"))%>
	</table>
	<input type="submit" value="Rechercher" />
</form>

<%
if (request.getParameter("id") != null) {
	String id = request.getParameter("id");
	String email = request.getParameter("email");
	String nom = request.getParameter("nom");
	String prenom = request.getParameter("prenom");
	String cp = request.getParameter("codePostal");
	String pageSize = request.getParameter("pageSize");
	String pageNumber = request.getParameter("pageNumber");
	
	String req = "<rechercheClientsRequest>";
	if (id != null && !"".equals(id))
		req += "<id>" + replaceSpecials(id) + "</id>";
	if (email != null && !"".equals(email))
		req += "<email>" + replaceSpecials(email) + "</email>";
	if (nom != null && !"".equals(nom))
		req += "<nom>" + replaceSpecials(nom) + "</nom>";
	if (prenom != null && !"".equals(prenom))
		req += "<prenom>" + replaceSpecials(prenom) + "</prenom>";
	if (cp != null && !"".equals(cp))
		req += "<codePostal>" + replaceSpecials(cp) + "</codePostal>";
	if (pageSize != null && !"".equals(pageSize))
		req += "<pageSize>" + replaceSpecials(pageSize) + "</pageSize>";
	if (pageNumber != null && !"".equals(pageNumber))
		req += "<pageNumber>" + replaceSpecials(pageNumber) + "</pageNumber>";
	req += "</rechercheClientsRequest>";

	String ret = ClientRechercheClients.rechercheClients(req, request);
%>

<script>
function formSubmit(i) {
	document.form1.pageNumber.value = i;
	document.form1.submit();
}
</script>

<%
	CastoRechercheClientsManager manager = (CastoRechercheClientsManager) Nucleus.getGlobalNucleus().resolveName("/com/castorama/webservices/CastoRechercheClientsManager");
	int totalPagesCount = manager.getTotalPagesCount();
	if (totalPagesCount > 1) {
		if (pageNumber.equals("") || pageNumber.equals("0"))
			pageNumber = "1";
		for (int i = 1; i <= totalPagesCount; i++) {
			if (String.valueOf(i).equals(pageNumber))
				out.println(i + "&nbsp;");
			else
				out.println("<a href=\"\" onclick=\"formSubmit(" + i + "); return false;\">" + i + "</a>&nbsp;");
		}
	}
%>

<br />

<hr />

<div class="request">	
	<% out.println(replaceChevrons(req)); %>
</div>

<hr />

<div class="response">
	<% out.println(replaceChevrons(ret)); %>
</div>

<% } %>

</body>
</html>
