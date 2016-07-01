<dsp:page>


<%--
-------------------------------------------------------------------------------------------- 
Imports-------------------------------------------------------------------------------------
--------------------------------------------------------------------------------------------
--%>
	
<dsp:importbean bean="/atg/commerce/csr/CallCenterAdminLoginFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/SimpleProfileFindForm"/>
<dsp:importbean bean="/atg/userprofiling/ProfileAdminFormHandler"/>
<dsp:importbean bean="/castorama/CastoProfileAdminFormHandler"/>
<dsp:importbean bean="/atg/commerce/order/FindOrdersFormHandler"/>
<dsp:importbean bean="/castorama/RechercheUserFormHandler"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
<dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach"/>





<!-- ************************ erreur sur le login ************************ -->
<dsp:droplet name="ErrorMessageForEach">
 <dsp:param name="exceptions" bean="CallCenterAdminLoginFormHandler.formExceptions"/>
<dsp:oparam name="outputStart">
	<TABLE border=0 cellpadding="10"><TR><TD align="center">
		<SPAN class="texterou">Il y a un problème avec l'identification <BR></SPAN></TD></TR>
			<TR><TD align="left">
	</dsp:oparam>
<dsp:oparam name="outputEnd">
	</TD></TR></TABLE>
</dsp:oparam>
 <dsp:oparam name="output">
	<dsp:droplet name="Switch">
 	<dsp:param name="value" param="message"/>
	<dsp:oparam name="Missing login">
	<dsp:setvalue param="l_strLogin" beanvalue="CallCenterAdminLoginFormHandler.username"/>
	<dsp:setvalue param="l_strPassword" beanvalue="CallCenterAdminLoginFormHandler.password"/>
	<%
		String l_strCommentaire = "Erreur de connexion. Le champ login est incorrect. Login = '"+request.getParameter("l_strLogin")+"' et Password = '"+request.getParameter("l_strPassword")+"'";
	%>
		<dsp:droplet name="/castorama/droplet/DropletCreateLogAdmin">
			<dsp:param name="elementName" value="CreateLogAdmin"/>
			<dsp:param name="type" value="Contact CallCenter"/>
			<dsp:param name="action" value="Autres"/>
			<dsp:param name="commentaire" value="l_strCommentaire"/>
			<dsp:param name="adrIp" bean="/atg/dynamo/servlet/pipeline/BrowserAttributes.remoteAddr"/>
			<dsp:param name="nomLogin" value=""/>
			<dsp:oparam name="output">
			</dsp:oparam>
		</dsp:droplet>
		<SPAN class="texterou"><IMG src="../img/flecheb_retrait.gif" border=0>&nbsp;Le champ "login" est incorrect <BR></SPAN>
	</dsp:oparam>
	<dsp:oparam name="Bad password">
	<dsp:setvalue param="l_strLogin" beanvalue="CallCenterAdminLoginFormHandler.username"/>
	<dsp:setvalue param="l_strPassword" beanvalue="CallCenterAdminLoginFormHandler.password"/>
	<%
		String l_strCommentaire = "Erreur de connexion. Le champ mot de passe est incorrect. Login = '"+request.getParameter("l_strLogin")+"' et Password = '"+request.getParameter("l_strPassword")+"'";
	%>
		<dsp:droplet name="/castorama/droplet/DropletCreateLogAdmin">
			<dsp:param name="elementName" value="CreateLogAdmin"/>
			<dsp:param name="type" value="Contact CallCenter"/>
			<dsp:param name="action" value="Autres"/>
			<dsp:param name="commentaire" value="l_strCommentaire"/>
			<dsp:param name="adrIp" bean="/atg/dynamo/servlet/pipeline/BrowserAttributes.remoteAddr"/>
			<dsp:param name="nomLogin" value=""/>
			<dsp:oparam name="output">
			</dsp:oparam>
		</dsp:droplet>
		<SPAN class="texterou"><IMG src="/img/flecheb_retrait.gif" border=0>&nbsp;Le champ "mot de passe" est incorrect <BR></SPAN>
	</dsp:oparam>
	</dsp:droplet>
  </dsp:oparam>
</dsp:droplet>

<!-- ************************ erreur sur la recherche des users ************************ -->
<dsp:droplet name="ErrorMessageForEach">
<dsp:param name="exceptions" bean="SimpleProfileFindForm.formExceptions"/>
<dsp:oparam name="outputStart">
	<TABLE border=0 cellpadding="10">
		<TR><TD align="center">
</dsp:oparam>
<dsp:oparam name="outputEnd">
	</TD></TR></TABLE>
</dsp:oparam>
<dsp:oparam name="output">

<dsp:droplet name="Switch">
	<dsp:param name="value" param="message"/>
	<dsp:oparam name="Please fill out at least one of the provided fields">
		<SPAN class="texterou"><IMG src="../../img/flecheb_retrait.gif" border=0>&nbsp;Veuillez saisir au moins 1 critï¿½re de recherche <BR></SPAN>
	</dsp:oparam>
</dsp:droplet>
</dsp:oparam>
</dsp:droplet>
<!-- ************************ erreur sur l'edition des users ************************ -->
<dsp:droplet name="ErrorMessageForEach">
<dsp:param name="exceptions" bean="CastoProfileAdminFormHandler.formExceptions"/>
<dsp:oparam name="outputStart">
		<TABLE border=0 cellpadding="10"><TR><TD align="left">
			<SPAN class="texterou">D&eacute;sol&eacute;, des anomalies sont survenues.<BR>Merci de v&eacute;rifier si les champs ci-dessous sont correctement renseign&eacute;s.</SPAN><BR></TD></TR>
			<TR><TD align="left">
	</dsp:oparam>
	<dsp:oparam name="outputEnd">
		</TD></TR></TABLE>
	</dsp:oparam>
<dsp:oparam name="output">
<dsp:droplet name="Switch">
	<dsp:param name="value" param="message"/>
	<dsp:oparam name="Cannot convert value for reponseReminder">
		<SPAN class="texterou"><IMG src="../../img/puce.gif" border=0>&nbsp;Le champs "R&eacute;ponse"<BR></SPAN><BR>
	</dsp:oparam>
	<dsp:oparam name="Your password field is empty">
		<SPAN class="texterou"><IMG src="../../img/puce.gif" border=0>&nbsp;Le champs "Mot de passe"</SPAN><BR>
	</dsp:oparam>
	<dsp:oparam name="Cannot convert value for password">
		<SPAN class="texterou"><IMG src="../../img/puce.gif" border=0>&nbsp;Le champs "Mot de passe"<BR></SPAN><BR>
	</dsp:oparam>
	<dsp:oparam name="Cannot convert value for login">
		<SPAN class="texterou"><IMG src="../../img/puce.gif" border=0>&nbsp;Le champs "Login e-mail"<BR></SPAN><BR>
	</dsp:oparam>
</dsp:droplet>

</dsp:oparam>
</dsp:droplet>
<!-- ************************ erreur sur l'edition des contacts ************************ -->

<!-- ************************ erreur sur la creation des contacts ************************ -->
<!-- ************************ erreur sur la creation des contacts en fonction d'un user ************************ -->

<!-- ************************ erreur sur la recherche des internautes ************************ -->  
   
	<dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
	<dsp:param name="exceptions" bean="/castorama/RechercheUserFormHandler.formExceptions"/>
	<dsp:oparam name="output">
		
		<dsp:droplet name="Switch">
		<dsp:param name="value" param="message"/>
		<dsp:oparam name="LoginMissing">
			<SPAN class="prix"><IMG src="../../img/flecheb_retrait.gif" border=0>&nbsp;<U>Le champs "Email Login" est incorrect !!</U></SPAN>
		</dsp:oparam>
		<dsp:oparam name="ProfileIdMissing"> 
			<SPAN class="prix"><IMG src="../../img/flecheb_retrait.gif" border=0>&nbsp;<U>Le champs "Num&eacute;ro Client " est incorrect !!</U></SPAN>
		</dsp:oparam>
		<dsp:oparam name="ErreurForm"> 
			<SPAN class="prix"><IMG src="../../img/flecheb_retrait.gif" border=0>&nbsp;<U>Les champs "Nom, Pr&eacute;nom, Code Postal et Ville" sont incorrects !!</U></SPAN>
		</dsp:oparam>
		
		</dsp:droplet>
	</dsp:oparam>
	</dsp:droplet>
  
<!-- ************************ fin erreur sur la recherche des commmandes ************************ -->



</dsp:page>