<dsp:page>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/castorama/profile/CastoGetPrivileges"/>
<dsp:importbean bean="/atg/userprofiling/ProfileAdminFormHandler"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>



<%-- 
------------------------------------------------------------------------------------------ 
Sécurisation HTTPS------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>
<dsp:include page="../common/protocolChange.jsp"><dsp:param name="protocol" value="secure"/></dsp:include>

<HTML>
<head>
<TITLE>Castorama : Call center Edit Compte</TITLE>
<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=iso-8859-1">
<LINK REL="stylesheet" HREF="../../css/hp.css">
</head>



<%--
	=================================================================================
		GY : STABILISATION
	=================================================================================
 --%>
<dsp:droplet name="IsNull">
	<dsp:param name="value" param="success"/>
	<dsp:oparam name="false">
		<BODY bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="javascript:alert('Le mail a &eacute;t&eacute; envoy&eacute;');">
	</dsp:oparam>
	<dsp:oparam name="default">
		<dsp:droplet name="IsNull">
			<dsp:param name="value" param="error"/>
			<dsp:oparam name="false">
				<BODY bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onload="javascript:alert('Le mail n'a pas &eacute;t&eacute; envoy&eacute;. Une erreur est survenue');">
			</dsp:oparam>
			<dsp:oparam name="true">
				<BODY bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
			</dsp:oparam>
		</dsp:droplet>
	</dsp:oparam>
</dsp:droplet>


			<dsp:droplet name="CastoGetPrivileges">
			    <dsp:param name="profile" bean="Profile"/>
			   	<dsp:param name="requis" value="commerce-csr-profiles-privilege"/>	
			   		   		
		   		<dsp:oparam name="accesAutorise">


	<dsp:droplet name="/atg/targeting/RepositoryLookup">
		<dsp:param name="repository" bean="/atg/userprofiling/ProfileAdapterRepository" />
		<dsp:param name="itemDescriptor" value="user" />
		<dsp:param name="id" param="id" />		
		<dsp:param name="elementName" value="client"/>							
		<dsp:oparam name="output">
	
     		<dsp:include page="../common/header.jsp">
       			<dsp:param name="id" param="client.repositoryId"/>
       			<dsp:param name="user" param="client.login"/>
     		</dsp:include>
	
			<table width="600" border=0 cellpadding=0 cellspacing=0 align="center">
		 	<tr>
			 	<td align="center">
				        <p>		         
							<span class=marques>&nbsp;Editer le profile de l'internaute : </span><span class="prix"><dsp:valueof param="client.login"/></span>						           		
		           		<p>
				</td>
		   	</tr>
		   	<tr>
		   		<td align="center">
		   			<table>
		   				<tr>
		   					<td>
		   						<img src="../../img/flecheb_retrait.gif" border=0>&nbsp;<dsp:a iclass="moncasto" href="editprofileLogin.jsp">
		   						<dsp:param name="id" param="client.repositoryId"/>Editer le bloc LOGIN</dsp:a>
		   					</td>
		   				</tr>
						<tr>
		   					<td>
		   						<img src="../../img/flecheb_retrait.gif" border=0>&nbsp;<dsp:a iclass="moncasto" href="editprofileMailing.jsp">
		   						<dsp:param name="id" param="client.repositoryId"/>Editer le bloc MAILING</dsp:a>
		   					</td>
		   				</tr>
		   			</table>
		   		</td>
		   	</tr>
			</table>
	</dsp:oparam>
	</dsp:droplet>
</dsp:oparam>
<dsp:oparam name="accesRefuse"></dsp:oparam>
</dsp:droplet>
<dsp:include page="../common/menuBas.jsp"/>
</body>
</html>          

</dsp:page>
