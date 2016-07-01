<dsp:page>

<%-- 
------------------------------------------------------------------------------------------ 
IMPORTS-----------------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>

<dsp:importbean bean="/atg/userprofiling/ProfileAdminFormHandler"/>
<dsp:importbean bean="/castorama/CastoProfileAdminFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>
<dsp:importbean bean="/castorama/profile/CastoGetPrivileges"/>

<%-- 
------------------------------------------------------------------------------------------ 
Sécurisation HTTPS------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>
<dsp:include page="../common/protocolChange.jsp"><dsp:param name="protocol" value="secure"/></dsp:include>


<HTML>
<TITLE>Castorama : Call center - Edit Login</TITLE>
<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<LINK rel="stylesheet" href="../../css/hp.css">



<BODY bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">



			<dsp:droplet name="CastoGetPrivileges">
			    <dsp:param name="profile" bean="Profile"/>
			   	<dsp:param name="requis" value="commerce-csr-profiles-privilege"/>	
			   		   		
		   		<dsp:oparam name="accesAutorise">



	<dsp:droplet name="/atg/targeting/RepositoryLookup">
		<dsp:param name="repository" bean="/atg/userprofiling/ProfileAdapterRepository" />
		<dsp:param name="itemDescriptor" value="user" />
		<dsp:param name="id" param="id" />		
		<dsp:param name="elementName" value="profile"/>							
		<dsp:oparam name="output">
     			
     		<dsp:tomap var="profile" param="profile"/>     		
     		
     		<dsp:include page="../common/header.jsp">
       			<dsp:param name="id" param="profile.repositoryId"/>
       			<dsp:param name="user" param="profile.login"/>
     		</dsp:include>

<center>	
	<dsp:droplet name="IsNull">
		<dsp:param name="value" param="erreur"/>
		<dsp:oparam name="false">
			<dsp:droplet name="Switch">
				<dsp:param name="value" param="erreur"/>
				<dsp:oparam name="1">
					<SPAN class="texterou">Veuillez renseigner le login.</span>
				</dsp:oparam>
				<dsp:oparam name="2">
					<SPAN class="texterou">Veuillez renseigner le mot de passe.</span>		
				</dsp:oparam>
				<dsp:oparam name="3">
					<SPAN class="texterou">Veuillez s&eacute;lectionner la question de rappel.</span>		
				</dsp:oparam>
				<dsp:oparam name="4">
					<SPAN class="texterou">Veuillez renseigner la r&eacute;ponse.</span>	
				</dsp:oparam>
				<dsp:oparam name="5">
					<SPAN class="texterou">Le login demand&eacute; existe.</span>
				</dsp:oparam>
				<dsp:oparam name="6">
					<SPAN class="texterou">Une erreur impr&eacute;vue est survenue.</span>
				</dsp:oparam>				
			</dsp:droplet>
		</dsp:oparam>
	</dsp:droplet>
</center>
<br/>


	<TABLE width=760 border=0 cellpadding=0 cellspacing=0 align="center">
			<TR>
				<TD align=center>
				 <P>
			        <SPAN class=marques>&nbsp;Editer le profile de l'internaute : </SPAN><SPAN class=prix><dsp:valueof param="profile.login"/></SPAN>
	           		<P>
				</TD>
			</TR>
			<TR>
				<TD align=center>
				<P>
	           		<SPAN class=texteg><B>Bloc Login :</B></SPAN>
	           	<P>
				</TD>
			</TR>
	   		<TR>
	    		<TD width="760" align="center">
				<%
				String l_strCommentaire = "Le bloc login de l'internaute "+request.getParameter("profile.id")+ " a ete modifie";
			%>
     
     <dsp:form action="${pageContext.request.requestURI}" method="POST" name="formAdmin">
	 <dsp:input type="hidden" bean="CastoProfileAdminFormHandler.type" value="Contact CallCenter"/>
		<dsp:input type="hidden" bean="CastoProfileAdminFormHandler.action" value="Modification"/>
		<dsp:input type="hidden" bean="CastoProfileAdminFormHandler.commentaire" value="l_strCommentaire"/>
		<dsp:input type="hidden" bean="CastoProfileAdminFormHandler.ip" beanvalue="/atg/dynamo/servlet/pipeline/BrowserAttributes.remoteAddr"/>
		
		<%--<DROPLET name="/atg/dynamo/security/CurrentUser">
  	      	  <OPARAM name="output">
			  <INPUT type="hidden" bean="CastoProfileAdminFormHandler.nomLogin" value="param:username">
		  </OPARAM>
        	  	  <OPARAM name="empty"></OPARAM>
        	  	  <OPARAM name="error">error</OPARAM>
		</DROPLET>--%>
                
              <dsp:droplet name="/atg/dynamo/droplet/URLArgument">
                 <dsp:param name="url" value="editprofileLogin.jsp"/>
                 <dsp:param name="argName" value="id"/>
                 <dsp:param name="argValue" param="profile.repositoryId"/>
                 <dsp:oparam name="output">
                   <dsp:input type="HIDDEN" bean="CastoProfileAdminFormHandler.updateErrorURL" value="./editprofileLogin.jsp?id=${param['id']}"/>
                 </dsp:oparam>
              </dsp:droplet>
              
              <dsp:input type="HIDDEN" bean="CastoProfileAdminFormHandler.deleteSuccessURL" value="user_deleted.jsp"/>
              
              <dsp:droplet name="/atg/dynamo/droplet/URLArgument">
                 <dsp:param name="url" value="editprofilePage.jsp"/>
                 <dsp:param name="argName" value="id"/>
                 <dsp:param name="argValue" param="profile.repositoryId"/>
                 <dsp:oparam name="output">
                   <dsp:input type="HIDDEN" bean="CastoProfileAdminFormHandler.updateSuccessURL" value="./editprofilePage.jsp?id=${param['id']}"/>
                 </dsp:oparam>
              </dsp:droplet>
            
            <dsp:input type="hidden" bean="CastoProfileAdminFormHandler.repositoryId" paramvalue="profile.repositoryId"/>
   <!--- In Case of an Error ---->
		<dsp:droplet name="Switch">
		<dsp:param name="value" bean="CastoProfileAdminFormHandler.formError"/>
		<dsp:oparam name="true">
			<TABLE width=600 border=0 cellpadding=0 cellspacing=0 align=center>
		   	<TR>
		   		<TD width=600 align="center"><dsp:include page="../common/erreur.jsp"/></TD>
		   	</TR>
		   	</TABLE>
		</dsp:oparam>
		</dsp:droplet>
	      	<BR>
			<!-- debut -->
			<TABLE width="760" border="0" cellspacing="0" cellpadding="0" align="center">
    <TR bgcolor="#FFDE63"> 
      <TD width="1"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
      <TD width="10"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
      <TD width="389"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
      <TD width="289"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
      <TD width="10"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
      <TD width="1"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
    </TR>
    <TR> 
      <TD width="1" bgcolor="#FFDE63"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
      <TD width="10" valign="MIDDLE" class="texte" align="CENTER"></TD>
      <TD width="389" valign="MIDDLE" class="texte" align="center" height="30">Login e-mail*
        :&nbsp; 
        
        
        <dsp:input type="hidden" size="30" maxlength="150" iclass="moncasto" bean="CastoProfileAdminFormHandler.value.login" paramvalue="profile.login" name="loginDeBase"/>
      <dsp:input type="text" size="30" maxlength="150" iclass="moncasto" bean="CastoProfileAdminFormHandler.value.login" paramvalue="profile.login" name="formLogin" disabled="true"/>
      
      </TD>
      <TD width="289" valign="MIDDLE" class="texte" align="left">
      	<dsp:input type="submit" bean="CastoProfileAdminFormHandler.sendMail" value="envoi du mot de passe"/>
      
      </TD>
      <TD width="10" valign="MIDDLE" class="texte" align="CENTER">&nbsp;</TD>
      <TD width="1" align="CENTER" bgcolor="#FFDE63"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
    </TR>
    <TR bgcolor="#FFDE63"> 
      <TD width="1"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
      <TD width="10"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
      <TD width="389"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
      <TD width="289"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
      <TD width="10"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
      <TD width="1"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
    </TR>
  </TABLE>  
  <BR>
  </TD>
<TD width="153" valign="top"></TD>
  </TR>
</TABLE>
</dsp:form>
   		</TD>
   	</TR>
	</TABLE>

</dsp:oparam>
</dsp:droplet>

</dsp:oparam>
<dsp:oparam name="accesRefuse"></dsp:oparam>
</dsp:droplet>

<dsp:include page="../common/menuBas.jsp"/>
</BODY>
</HTML>


</dsp:page>       
