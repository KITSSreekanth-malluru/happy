<%@ page language="java" contentType="text/html; charset=UTF-8" %>

<%@page import="com.castorama.webservices.clients.ClientUpdateUser" %> 
<%@page import="java.util.HashMap" %> 
<%@page import="java.util.Iterator" %> 

<html>
<head>
<title>Test du webservice updateUser</title>
<style>
.request,.response { background-color: #ccc; border: 1px solid black; padding: 5px; font-family: Courier }
.valeur { font-weight: bold; color: black }
.balise { color: blue }
</style>
</head>
<body>

<h1>Test du webservice updateUser</h1>

<%!
String ajoutChamps(String nomAffiche, String nomParametre, String type, String current) {
	String ret = "<tr><td><label for=\"" + nomParametre + "\">" + nomAffiche + "</label></td><td><input type=\"" + type + "\" name=\"" + nomParametre + "\" value=\"";
	if (current != null)
		ret += current;
	ret += "\" /></td></tr>";
	return ret;
}

String ajoutChampsCheckbox(String nomAffiche, String nomParametre, String value, String current) {
	String ret = "<tr><td><label for=\"" + nomParametre + "\">" + nomAffiche + "</label></td><td><input type=\"checkbox\" name=\"" + nomParametre + "\" value=\"" + value + "\"";
	if (current != null)
		ret += "checked=\"checked\"";
	ret += "/></td></tr>";
	return ret;
}


String ajoutChampsSelect(String nomAffiche, String nomParametre, HashMap choix, String current) {
	String ret = "<tr><td><label for=\"" + nomParametre + "\">" + nomAffiche + "</label></td><td><select name=\"" + nomParametre + "\"/>";
	ret += "<option value=\"\">&nbsp;</option>";
	for (Iterator it = choix.keySet().iterator(); it.hasNext(); )
	{
		String choi = (String) it.next();
		String valeur = (String) choix.get(choi);
		ret += "<option value=\"" + valeur + "\"" + (valeur.equals(current) ? "selected=\"selected\"" : "") + ">" + choi + "</option>";
	}
	ret += "</select></td></tr>";
	return ret;
}

String ajoutChampsXML(String nomChamps, String valeurChamps)
{
	//if (valeurChamps != null && !"".equals(valeurChamps))
		return "<" + nomChamps + ">" + replaceSpecials(valeurChamps) + "</" + nomChamps + ">";
	//return "";
}

%>

<%
HashMap civilites = new HashMap();
civilites.put("Monsieur", "1");
civilites.put("Madame", "2");
civilites.put("Mlle", "3");

HashMap typeAccess = new HashMap();
typeAccess.put("Web", "web");
typeAccess.put("Magasin", "magasin");

request.setCharacterEncoding("UTF-8");
%>

<form action="test.jsp" method="post">
	<table>
	<%=ajoutChamps("Num&eacute;ro de client", "id", "text", request.getParameter("id"))%>		
	<%=ajoutChampsSelect("Type d'acc&egrave;s", "typeAcces", typeAccess, request.getParameter("typeAcces"))%>	
	<tr><td colspan="2"><hr /></td></tr>
	<%--ajoutChampsSelect("civilite", civilites, request.getParameter("civilite"))--%>
	<%=ajoutChamps("Civilit&eacute; (1=M, 2=Mme, 3=Mlle)", "civilite", "text", request.getParameter("civilite"))%>
	<%=ajoutChamps("Nom", "nom", "text", request.getParameter("nom"))%>	
	<%=ajoutChamps("Pr&eacute;nom", "prenom", "text", request.getParameter("prenom"))%>
	<%=ajoutChamps("Code postal", "codePostal", "text", request.getParameter("codePostal"))%>
	<%=ajoutChamps("Mot de passe", "password", "password", request.getParameter("password"))%>
	<tr><td colspan="2"><hr /></td></tr>	
	<%=ajoutChampsCheckbox("Inscription newsletter", "inscriptionNewsletter", "true", request.getParameter("inscriptionNewsletter"))%>	
	<%=ajoutChampsCheckbox("Inscription newsletter partenaires", "inscriptionNewsletterPartenaires", "true", request.getParameter("inscriptionNewsletterPartenaires"))%>  
	<%=ajoutChampsCheckbox("J’accepte d’être recontacté par un vendeur Castorama sur mon projet 3D", "acceptRecontact", "true", request.getParameter("acceptRecontact"))%>  
	<tr><td>(1 - Test Page; 2 - Kitchen Planner)</td><td>  
	<%=ajoutChamps("E-mail", "email", "text", request.getParameter("email"))%>	
	<tr><td colspan="2"><hr /></td></tr>
	<%=ajoutChamps("Adresse", "adresse", "text", request.getParameter("adresse"))%>
	<%=ajoutChamps("Compl&eacute;ment", "complement", "text", request.getParameter("complement"))%>
	<%=ajoutChamps("Pays", "pays", "text", request.getParameter("pays"))%>
	<tr><td colspan="2"><hr /></td></tr>	
	<%=ajoutChamps("Ville", "ville", "text", request.getParameter("ville"))%>	
	<%=ajoutChamps("Soci&eacute;t&eacute;", "societe", "text", request.getParameter("societe"))%>
	<%=ajoutChamps("Magasin de r&eacute;f&eacute;rence (ID)", "magasin_reference", "text", request.getParameter("magasin_reference"))%>
	<%=ajoutChamps("Date de naissance (JJ/MM/AAAA)", "dateNaissance", "text", request.getParameter("dateNaissance"))%>		
	
	</table>

	<input type="submit" value="Mise à jour de l'utilisateur" />
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
		
if (request.getParameter("id") != null || request.getParameter("email") != null || request.getParameter("password") != null || request.getParameter("civilite") != null ||
		request.getParameter("nom") != null || request.getParameter("prenom") != null || request.getParameter("societe") != null ||
		request.getParameter("codePostal") != null || request.getParameter("adresse") != null || request.getParameter("complement") != null ||
		request.getParameter("ville") != null || request.getParameter("pays") != null || request.getParameter("dateNaissance") != null ||
		request.getParameter("inscriptionNewsletter") != null || request.getParameter("inscriptionNewsletterPartenaires") != null || 
		request.getParameter("acceptRecontact") != null || request.getParameter("typeAcces") != null) {	
	
	String id = request.getParameter("id");
	String email = request.getParameter("email");
	String password = request.getParameter("password");
	String civilite = request.getParameter("civilite");
	String nom = request.getParameter("nom");
	String prenom = request.getParameter("prenom");
	String societe = request.getParameter("societe");
	String codePostal = request.getParameter("codePostal");
	String adresse = request.getParameter("adresse");
	String complement = request.getParameter("complement");
	String pays = request.getParameter("pays");
	String dateNaissance = request.getParameter("dateNaissance");
	String inscriptionNewsletter = request.getParameter("inscriptionNewsletter");
	if (inscriptionNewsletter == null)
		inscriptionNewsletter = "false";
	String inscriptionNewsletterPartenaires = request.getParameter("inscriptionNewsletterPartenaires");
	if (inscriptionNewsletterPartenaires == null)
		inscriptionNewsletterPartenaires = "false";
	String acceptRecontact = request.getParameter("acceptRecontact");
	if (acceptRecontact == null || acceptRecontact.equals("false"))
		acceptRecontact = "0";
	else
		acceptRecontact = "1";
	String typeAcces = request.getParameter("typeAcces");

	String ville = request.getParameter("ville");
	String magasinReference = request.getParameter("magasin_reference");	
	
	String req = "<updateUserRequest>";
	req += ajoutChampsXML("typeAcces", typeAcces);
	req += "<updatedUser>";
	req += ajoutChampsXML("id", id);	
	req += ajoutChampsXML("email", email);
	req += ajoutChampsXML("password", password);
	req += ajoutChampsXML("civilite", civilite);
	req += ajoutChampsXML("nom", nom);
	req += ajoutChampsXML("prenom", prenom);
	req += ajoutChampsXML("societe", societe);
	req += ajoutChampsXML("codePostal", codePostal);
	req += ajoutChampsXML("adresse", adresse);
	req += ajoutChampsXML("complement", complement);
	req += ajoutChampsXML("ville", ville);
	req += ajoutChampsXML("pays", pays);
	req += ajoutChampsXML("dateNaissance", dateNaissance);
	req += ajoutChampsXML("magasinReference", magasinReference);		
	req += ajoutChampsXML("inscriptionNewsletter", inscriptionNewsletter);
	req += ajoutChampsXML("inscriptionNewsletterPartenaires", inscriptionNewsletterPartenaires);
	req += ajoutChampsXML("acceptRecontact", acceptRecontact);
	req += "</updatedUser>";
		
	req += "</updateUserRequest>";
%>

<hr />

<div class="request">	
	<% out.println(replaceChevrons(req)); %>
</div>

<hr />

<% String ret = ClientUpdateUser.updateUser(req, request);	%>

<div class="response">
	<% out.println(replaceChevrons(ret)); %>
</div>

<% } %>

</body>
</html>
