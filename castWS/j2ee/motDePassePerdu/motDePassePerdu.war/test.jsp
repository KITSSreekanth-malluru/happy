<%@ page language="java" contentType="text/html; charset=UTF-8" %>

<%@page import="com.castorama.webservices.clients.ClientMotDePassePerdu" %> 

<html>
<head>
<title>Test du webservice motDePassePerdu</title>
<style>
.request,.response { background-color: #ccc; border: 1px solid black; padding: 5px; font-family: Courier }
.valeur { font-weight: bold; color: black }
.balise { color: blue }
</style>
</head>
<body>

<h1>Test du webservice motDePassePerdu</h1>

<% request.setCharacterEncoding("UTF-8"); %>

<form action="test.jsp" method="get">
	<label for="email">Email : </label><input type="text" name="email" value="<% if (request.getParameter("email") != null) out.println(request.getParameter("email")); %>" />
	<input type="submit" value="Demander son mot de passe" />
</form>

<br />

<%!
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

<%

if (request.getParameter("email") != null) {
	String email = request.getParameter("email");
	String req = "<motDePassePerduRequest><email>" + replaceSpecials(email) + "</email></motDePassePerduRequest>";
%>

<hr />

<div class="request">	
	<% out.println(replaceChevrons(req)); %>
</div>

<hr />

<% String ret = ClientMotDePassePerdu.motDePassePerdu(req, request);	%>

<div class="response">
	<% out.println(replaceChevrons(ret)); %>
</div>

<% } %>

</body>
</html>
