<%@ page language="java" contentType="text/html; charset=UTF-8" %>

<%@ taglib prefix="c"	uri="/core" %>
<%@ taglib prefix="fmt"	uri="/fmt"  %>
<%@ taglib prefix="dsp"	uri="/dspTaglib" %>

<dsp:page xml="true">

	<%-- Import des beans --%>
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/castorama/config/CastoConfiguration" />
	
	<%-- On fixe le bundle pour les messages --%>
	<fmt:setBundle basename="com.castorama.identification.TemplateMailInscription" />
	
	<%-- On rend l'objet Profile accessible a la JSTL --%>		
	<dsp:tomap var="currentProfile" bean="Profile"/>
	
	<%-- On rend la configuration accessible à la JSTL --%>
	<dsp:tomap var="config" bean="CastoConfiguration" />
	
	<%-- On fixe le sujet du message --%>
	<dsp:setvalue param="messageSubject" value="Castorama" />
	
	<%--
	 * ============================================================================================
	 *
	 * Debut de la page
	 *
	 * ============================================================================================
	--%>

	<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
		"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
	<html lang="fra" xmlns="http://www.w3.org/1999/xhtml" xml:lang="fra">
		<head>
			<title><fmt:message key="inscription.titre" /></title>
		</head>
		<body link="#000000">
			<table width="600" cellspacing="0" cellpadding="0">
				<tr>
					<td><img src="<dsp:valueof bean='CastoConfiguration.urlSite' /><dsp:valueof bean='CastoConfiguration.urlBaseImg' />/img-haut-newsletter.gif" alt="" width="600" height="63" border="0"/></td>
				</tr>
				<tr>
					<td>
						<table width="520" cellspacing="0" cellpadding="0" align="center">
							<tr>
								<td><br/><font size="2" face="Arial" color="#000000">
									<font size="5" face="Arial" color="#7ecdd0"><fmt:message key="inscription.entete" /></font><br/><br/>
									<dsp:droplet name="/atg/dynamo/droplet/Switch">
										<dsp:param name="value" value="${currentProfile.civilite}"/>
										<dsp:oparam name="Monsieur">
											<c:set var="civilite" value="Monsieur"/>
											<c:set var="informe" value="informé"/>
										</dsp:oparam>
										<dsp:oparam name="Madame">
											<c:set var="civilite" value="Madame"/>
											<c:set var="informe" value="informée"/>
										</dsp:oparam>
										<dsp:oparam name="Mlle">
											<c:set var="civilite" value="Mademoiselle"/>
											<c:set var="informe" value="informée"/>
										</dsp:oparam>
									</dsp:droplet>
									<fmt:message key="inscription.texte">
										<fmt:param value="${civilite}" />	
										<fmt:param value="${currentProfile.firstName}" />	
										<fmt:param value="${currentProfile.lastName}" />	
										<fmt:param value="${currentProfile.login}" />
										<fmt:param value="${currentProfile.password}" />
										<fmt:param value="${config.urlSite}" />
										<fmt:param value="${informe}" />
									</fmt:message>
									</font>
								</td>
							</tr>
							<tr>
								<td height="50"></td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td><img src="<dsp:valueof bean='CastoConfiguration.urlSite' /><dsp:valueof bean='CastoConfiguration.urlBaseImg' />/img-bas-newsletter.gif" alt="" width="600" height="30" border="0"/></td>
				</tr>
				<tr>
					<td align="center" height="30"><a href="http://www.castorama.fr"><font face="Arial" size="2" color="#09438b">www.castorama.fr</font></a></td>
				</tr>
			</table>
		</body>
	</html>

</dsp:page>