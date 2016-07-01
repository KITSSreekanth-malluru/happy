<%@ page language="java" contentType="text/html; charset=UTF-8" %>

<%@page import="com.castorama.webservices.clients.ClientLoginUser" %> 

<html>
<head>
<title>Test du webservice loginUser</title>
<style>
.request,.response { background-color: #ccc; border: 1px solid black; padding: 5px; font-family: Courier }
.valeur { font-weight: bold; color: black }
.balise { color: blue }
</style>
</head>
<body>

<h1>Test du webservice loginUser</h1>

<% request.setCharacterEncoding("UTF-8"); %>

<form action="test.jsp" method="post">
	<label for="login">Login : </label><input type="text" name="login" value="<% if (request.getParameter("login") != null) out.println(request.getParameter("login")); %>" />
	<label for="password">Mot de passe : </label><input type="password" name="password" value="<% if (request.getParameter("password") != null) out.println(request.getParameter("password")); %>" />
	<input type="submit" value="S'identifier" />
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
		return "";
	return msg.replaceAll("<", "&gt;").replaceAll(">", "&lt;").replaceAll("&", "&amp;");
}
%>

<%

if (request.getParameter("login") != null && request.getParameter("password") != null) {
	String login = request.getParameter("login");
	String password = request.getParameter("password");
	String req = "<loginUserRequest><login>" + replaceSpecials(login) + "</login><password>" + replaceSpecials(password) + "</password></loginUserRequest>";
%>

<hr />

<div class="request">	
	<% out.println(replaceChevrons(req)); %>
</div>

<hr />

<% String ret = ClientLoginUser.loginUser(req, request);	%>

<div class="response">
	<% out.println(replaceChevrons(ret)); %>
</div>

<% } %>

</body>
</html>
