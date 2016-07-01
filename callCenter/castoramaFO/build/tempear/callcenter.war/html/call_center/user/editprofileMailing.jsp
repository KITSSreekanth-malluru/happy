<dsp:page>

<%-- 
------------------------------------------------------------------------------------------ 
IMPORTS-----------------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>


<dsp:importbean bean="/atg/userprofiling/ProfileAdminFormHandler"/>
<dsp:importbean bean="/castorama/CastoProfileAdminFormHandler"/>
<dsp:importbean bean="/castorama/CastAdminFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/castorama/profile/CastoGetPrivileges"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>


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
					<SPAN class="texterou">Cet e-mail correspond un souscription existant.</span>
				</dsp:oparam>
				<dsp:oparam name="2">
					<SPAN class="texterou">Souscription qui n'auront pas trouv&eacute;</span>		
				</dsp:oparam>
				<dsp:oparam name="3">
					<SPAN class="texterou">Votre e-mail n'est pas correctement renseign&eacute;.</span>		
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
			        <SPAN class=marques>&nbsp;Editer le profile de l'internaute : </SPAN>
			        <SPAN class=prix><dsp:valueof param="profile.login"/></SPAN>
	           		<P>
				</TD>
			</TR>
			<TR>
				<TD align=center>
				<P>
	           		<SPAN class=texteg><B>Bloc Mailing :</B></SPAN>
	           	<P>
				</TD>
			</TR>
	   		<TR>
	    		<TD width="760" align="center">
<!-- start -->
	<dsp:droplet name="/atg/dynamo/droplet/RQLQueryForEach">
        <dsp:param name="queryRQL" value="profile EQUALS :profile.repositoryId ORDER BY email"/>
		<dsp:param name="repository" value="/atg/registry/Repository/NewsletterGSARepository"/>
		<dsp:param name="itemDescriptor" value="abonnementNewsletter"/>
		<dsp:param name="elementName" value="nl"/>

		<dsp:oparam name="output">
	<dsp:getvalueof var="count" param="count"/>
	<dsp:getvalueof var="profile" param="nl.profile"/>
  <dsp:form action="${pageContext.request.requestURI}" method="POST" name="form${count }" formid="form${count }">
	<dsp:input type="hidden" bean="CastAdminFormHandler.updateErrorURL" value="./editprofileMailing.jsp?id=${profile}" />
	<dsp:input type="hidden" bean="CastAdminFormHandler.updateSuccessURL" value="./editprofilePage.jsp?id=${profile}" />
    <dsp:input type="hidden" bean="CastAdminFormHandler.profileId" paramvalue="profile.repositoryId"/>
	<TABLE width="800" border="0" cellspacing="0" cellpadding="0" align="center">
    <TR bgcolor="#FFDE63"> 
      <TD width="1"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
      <TD width="10"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
      <TD width="280"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
      <TD width="188"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
      <TD width="188"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
      <TD width="120"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
      <TD width="10"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
      <TD width="1"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
    </TR>
    <TR> 
      <TD bgcolor="#FFDE63"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
      <TD valign="MIDDLE" class="texte" align="CENTER"></TD>
      <TD valign="MIDDLE" class="texte" align="center" height="30">Adresse e-mail 
        :&nbsp; 
         <dsp:input type="text" size="30" maxlength="150" iclass="moncasto" bean="CastAdminFormHandler.value.email" paramvalue="nl.email" disabled="true"/>
         <dsp:input type="hidden" size="30" maxlength="150" iclass="moncasto" bean="CastAdminFormHandler.value.email" paramvalue="nl.email"/>
      </TD>
      <TD valign="MIDDLE" class="texte" align="center">R&eacute;ception des &eacute;mails 
        :&nbsp; 
        <dsp:select iclass="moncasto" bean="CastAdminFormHandler.receiveEmail">
           <dsp:droplet name="/atg/dynamo/droplet/Switch">
             <dsp:param name="value" param="nl.receiveEmail"/>
             <dsp:oparam name="true">
				<dsp:option value="false">no</dsp:option>
               <dsp:option selected="true" value="true">yes</dsp:option>
             </dsp:oparam>
             <dsp:oparam name="false">
				<dsp:option value="true">yes</dsp:option>
               <dsp:option selected="true" value="false">no</dsp:option>
             </dsp:oparam>
             <dsp:oparam name="">
				<dsp:option selected="true" value=""></dsp:option>
				<dsp:option value="true">yes</dsp:option>
             </dsp:oparam>
           </dsp:droplet>
          </dsp:select>
	 </TD>
      <TD valign="MIDDLE" class="texte" align="center">R&eacute;ception des offers 
        :&nbsp; 
        <dsp:select iclass="moncasto" bean="CastAdminFormHandler.reseiveOffers">
           <dsp:droplet name="/atg/dynamo/droplet/Switch">
             <dsp:param name="value" param="nl.reseiveOffers"/>
             <dsp:oparam name="true">
				<dsp:option value="false">no</dsp:option>
               <dsp:option selected="true" value="true">yes</dsp:option>
             </dsp:oparam>
             <dsp:oparam name="false">
				<dsp:option value="true">yes</dsp:option>
               <dsp:option selected="true" value="false">no</dsp:option>
             </dsp:oparam>
              <dsp:oparam name="">
				<dsp:option selected="true" value=""></dsp:option>
				<dsp:option value="true">yes</dsp:option>
             </dsp:oparam>
           </dsp:droplet>
          </dsp:select>
	 </TD>
      <TD valign="MIDDLE" class="texte" align="center"> 
          <dsp:input type="image" src="../../img/valider.gif" bean="CastAdminFormHandler.updateNewsletter"/>
	 </TD>
      <TD valign="MIDDLE" class="texte" align="CENTER">&nbsp;</TD>
      <TD align="CENTER" bgcolor="#FFDE63"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
    </TR>
    <TR bgcolor="#FFDE63"> 
      <TD colspan="8"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
    </TR>
  </TABLE>  
  </dsp:form>
		</dsp:oparam>
	</dsp:droplet>

	<dsp:getvalueof var="profile" param="profile.repositoryId"/>
  <dsp:form action="${pageContext.request.requestURI}" method="POST" name="formAdd" formid="formAdd">
	<dsp:input type="hidden" bean="CastAdminFormHandler.createErrorURL" value="./editprofileMailing.jsp?id=${profile}" />
	<dsp:input type="hidden" bean="CastAdminFormHandler.createSuccessURL" value="./editprofilePage.jsp?id=${profile}" />
    <dsp:input type="hidden" bean="CastAdminFormHandler.profileId" paramvalue="profile.repositoryId"/>
	<TABLE width="800" border="0" cellspacing="0" cellpadding="0" align="center">
    <TR bgcolor="#FFDE63"> 
      <TD width="1"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
      <TD width="10"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
      <TD width="280"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
      <TD width="188"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
      <TD width="188"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
      <TD width="120"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
      <TD width="10"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
      <TD width="1"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
    </TR>
    <TR> 
      <TD bgcolor="#FFDE63"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
      <TD valign="MIDDLE" class="texte" align="CENTER"></TD>
      <TD valign="MIDDLE" class="texte" align="center" height="30">Adresse e-mail 
        :&nbsp; 
         <dsp:input type="text" size="30" maxlength="150" iclass="moncasto" bean="CastAdminFormHandler.value.email" paramvalue=""/>
      </TD>
      <TD valign="MIDDLE" class="texte" align="center">R&eacute;ception des &eacute;mails 
        :&nbsp; 
        <dsp:select iclass="moncasto" bean="CastAdminFormHandler.receiveEmail">
    		<dsp:option selected="true" value=""></dsp:option>
            <dsp:option value="true">yes</dsp:option>
          </dsp:select>
	 </TD>
      <TD valign="MIDDLE" class="texte" align="center">R&eacute;ception des offers 
        :&nbsp; 
        <dsp:select iclass="moncasto" bean="CastAdminFormHandler.reseiveOffers">
    		<dsp:option selected="true" value=""></dsp:option>
            <dsp:option value="true">yes</dsp:option>
          </dsp:select>
	 </TD>
      <TD valign="MIDDLE" class="texte" align="center"> 
          <dsp:input type="image" src="../../img/valider.gif" bean="CastAdminFormHandler.createNewsletter"/>
	 </TD>
      <TD valign="MIDDLE" class="texte" align="CENTER">&nbsp;</TD>
      <TD align="CENTER" bgcolor="#FFDE63"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
    </TR>
    <TR bgcolor="#FFDE63"> 
      <TD colspan="8"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
    </TR>
  </TABLE>  
  </dsp:form>

	<!-- fin -->
	  <TABLE width="760" border="0" cellspacing="0" cellpadding="0">
        <TR align="center">
          <TD valign=top><A href="javascript:history.back();"><IMG src="../../img/annuler.gif" border=0"></A></TD>
        </TR>
      </TABLE>
	</dsp:oparam>
	</dsp:droplet>
  </TD>
<TD width="153" valign="top"></TD>
  </TR>
</TABLE>
	
</dsp:oparam>
<dsp:oparam name="accesRefuse"></dsp:oparam>
</dsp:droplet>

<dsp:include page="../common/menuBas.jsp"/>
</BODY>
</HTML>    

   
</dsp:page>