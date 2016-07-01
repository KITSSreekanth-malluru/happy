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
<TITLE>Castorama : Call center - Edit contact</TITLE>
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
					<SPAN class="texterou">Veuillez renseigner l'adresse.</span>
				</dsp:oparam>
				<dsp:oparam name="2">
					<SPAN class="texterou">Veuillez renseigner le code postal.</span>		
				</dsp:oparam>
				<dsp:oparam name="3">
					<SPAN class="texterou">Veuillez renseigner la ville.</span>		
				</dsp:oparam>
				<dsp:oparam name="4">
					<SPAN class="texterou">Une erreur imprévue est survenue.</span>	
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
           		<SPAN class=texteg><B>Bloc Coordonn&eacute;es de livraison et facturation :</B></SPAN>
           	<P>
			</TD>
		</TR>
   		<TR>
	    	<TD width="760" align="center">
			<%
				String l_strCommentaire = "Le bloc Coordonnees de livraison et facturation de l'internaute "+request.getParameter("profile.id")+ " a ete modifie";
			%>
		      <dsp:form action="${pageContext.request.requestURI}" method="POST" name="formAdmin">
			  	<dsp:input type="hidden" bean="CastoProfileAdminFormHandler.type" value="Contact CallCenter"/>
				<dsp:input type="hidden" bean="CastoProfileAdminFormHandler.action" value="Modification"/>
				<dsp:input type="hidden" bean="CastoProfileAdminFormHandler.commentaire" value="l_strCommentaire"/>
				<dsp:input type="hidden" bean="CastoProfileAdminFormHandler.ip" beanvalue="/atg/dynamo/servlet/pipeline/BrowserAttributes.remoteAddr"/>
				<%--<dsp:droplet name="/atg/dynamo/security/CurrentUser">
    	      	  <dsp:oparam name="output">
					  <INPUT type="hidden" bean="CastoProfileAdminFormHandler.nomLogin" value="param:username">
				  </dsp:oparam>
          	  	  <dsp:oparam name="empty"></dsp:oparam>
          	  	  <dsp:oparam name="error">error</dsp:oparam>
				</dsp:droplet>--%>
			  			  
                <dsp:droplet name="/atg/dynamo/droplet/URLArgument">
                 <dsp:param name="url" value="editprofileContactInfo.jsp"/>
                 <dsp:param name="argName" value="id"/>
                 <dsp:param name="argValue" param="profile.repositoryId"/>
                 <dsp:oparam name="output">
                   <dsp:input type="HIDDEN" bean="CastoProfileAdminFormHandler.updateErrorURL" value="./editprofileContactInfo.jsp?id=${param['id']}"/>
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

            <dsp:input type="hidden" bean="CastoProfileAdminFormHandler.repositoryId" paramvalue="id"/>

   		<TABLE width="600" border="0" cellspacing="0" cellpadding="0" align=center>
	    	<TR><TD><dsp:include page="./erreurs.jsp"/></TD></TR>
	  	</TABLE>
		<BR>
		
		<!-- debut -->
		<TABLE width="600" border="0" cellspacing="0" cellpadding="0" align="center">
			<TR><TD align="left" class="moncasto">Adresse de Livraison 1 <b>(active)</b></TD></TR>
		</TABLE>
		<TABLE width="600" border="0" cellspacing="0" cellpadding="0" align="center" bgcolor="#fffcc">
		    <TR bgcolor="#FFDE63"> 
		      <TD width="1"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
		      <TD width="10"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
		      <TD width="289"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
		      <TD width="289"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
		      <TD width="10"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
		      <TD width="1"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
		    </TR>
		    <TR> 
		      <TD width="1" bgcolor="#FFDE63"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
		      <TD width="10" valign="MIDDLE" class="texte" align="CENTER"></TD>
		      <TD width="289" valign="MIDDLE" class="texte" align="center" height="30">Adresse 
		        :&nbsp; 
		        <dsp:textarea cols="35" iclass="moncasto" rows="4" bean="CastoProfileAdminFormHandler.value.adresseLivraison1.address1" name="adresse1Liv"><dsp:valueof param="profile.shippingAddress.address1"/></dsp:textarea>
		      </TD>
		      <TD width="289" valign="MIDDLE" class="texte" align="center">Compl&eacute;ment d'adresse (&eacute;tage,entr&eacute;e,porte ...) 
		        :&nbsp; 
		        <dsp:input name="adresse2Liv" iclass="moncasto" type="TEXT" size="30" maxsize="100" bean="CastoProfileAdminFormHandler.value.adresseLivraison1.address2" paramvalue="profile.shippingAddress.address2"/>
		      </TD>
		      <TD width="10" valign="MIDDLE" class="texte" align="CENTER">&nbsp;</TD>
		      <TD width="1" align="CENTER" bgcolor="#FFDE63"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
		    </TR>
			<TR> 
		      <TD width="1" bgcolor="#FFDE63"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
		      <TD width="10" valign="MIDDLE" class="texte" align="CENTER"></TD>
		      <TD width="289" valign="MIDDLE" class="texte" align="center" height="30">Code postal 
		        :&nbsp; 
		        <dsp:input iclass="moncasto" type="TEXT" size="7" maxsize="5" bean="CastoProfileAdminFormHandler.value.adresseLivraison1.postalCode" name="cpLiv" paramvalue="profile.shippingAddress.postalCode"/>
		      </TD>
		      <TD width="289" valign="MIDDLE" class="texte" align="center">Ville 
			  	:&nbsp;
			    <dsp:input iclass="moncasto" type="TEXT" size="30" maxsize="100" bean="CastoProfileAdminFormHandler.value.adresseLivraison1.city" name="villeLiv" paramvalue="profile.shippingAddress.city"/>
			  </TD>
		      <TD width="10" valign="MIDDLE" class="texte" align="CENTER">&nbsp;</TD>
		      <TD width="1" align="CENTER" bgcolor="#FFDE63"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
		    </TR>
		    <TR bgcolor="#FFDE63"> 
		      <TD width="1"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
		      <TD width="10"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
		      <TD width="289"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
		      <TD width="289"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
		      <TD width="10"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
		      <TD width="1"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
		    </TR>
		  </TABLE>  
  <BR>
	<!-- fin -->
	<!-- debut -->
		<TABLE width="600" border="0" cellspacing="0" cellpadding="0" align="center">
			<TR><TD align="left" class="moncasto">Adresse de Facturation 1 <b>(active)</b></TD></TR>
		</TABLE>
		<TABLE width="600" border="0" cellspacing="0" cellpadding="0" align="center" bgcolor="#fffcc">
		    <TR bgcolor="#FFDE63"> 
		      <TD width="1"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
		      <TD width="10"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
		      <TD width="289"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
		      <TD width="289"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
		      <TD width="10"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
		      <TD width="1"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
		    </TR>
		    <TR> 
		      <TD width="1" bgcolor="#FFDE63"><IMG src="../img/1pixel.gif" width="1" height="1"></TD>
		      <TD width="10" valign="MIDDLE" class="texte" align="CENTER"></TD>
		      <TD width="289" valign="MIDDLE" class="texte" align="center" height="30">Adresse 
		        :&nbsp; 
		        <dsp:textarea cols="35" iclass="moncasto" rows="4" bean="CastoProfileAdminFormHandler.value.adresseLivraison1.address1" name="adresse1Fac"><dsp:valueof param="profile.billingAddress.address1"/></dsp:textarea>
		      </TD>
		      <TD width="289" valign="MIDDLE" class="texte" align="center">Compl&eacute;ment d'adresse (&eacute;tage,entr&eacute;e,porte ...) 
		        :&nbsp; 
		        <dsp:input name="adresse2Fac" iclass="moncasto" type="TEXT" size="30" maxsize="100" bean="CastoProfileAdminFormHandler.value.adresseLivraison1.address2" paramvalue="profile.billingAddress.address2"/>
		      </TD>
		      <TD width="10" valign="MIDDLE" class="texte" align="CENTER">&nbsp;</TD>
		      <TD width="1" align="CENTER" bgcolor="#FFDE63"><IMG src="../img/1pixel.gif" width="1" height="1"></TD>
		    </TR>
			<TR> 
		      <TD width="1" bgcolor="#FFDE63"><IMG src="../img/1pixel.gif" width="1" height="1"></TD>
		      <TD width="10" valign="MIDDLE" class="texte" align="CENTER"></TD>
		      <TD width="289" valign="MIDDLE" class="texte" align="center" height="30">Code postal 
		        :&nbsp; 
		        <dsp:input iclass="moncasto" type="TEXT" size="7" maxsize="5" bean="CastoProfileAdminFormHandler.value.adresseLivraison1.postalCode" name="cpFac" paramvalue="profile.billingAddress.postalCode"/>
		      </TD>
		      <TD width="289" valign="MIDDLE" class="texte" align="center">Ville 
			  	:&nbsp;
			    <dsp:input iclass="moncasto" type="TEXT" size="30" maxsize="100" bean="CastoProfileAdminFormHandler.value.adresseLivraison1.city" name="villeFac" paramvalue="profile.billingAddress.city"/>
			  </TD>
		      <TD width="10" valign="MIDDLE" class="texte" align="CENTER">&nbsp;</TD>
		      <TD width="1" align="CENTER" bgcolor="#FFDE63"><IMG src="../img/1pixel.gif" width="1" height="1"></TD>
		    </TR>
		    <TR bgcolor="#FFDE63"> 
		      <TD width="1"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
		      <TD width="10"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
		      <TD width="289"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
		      <TD width="289"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
		      <TD width="10"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
		      <TD width="1"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
		    </TR>
		  </TABLE>  
  <BR>
	<!-- fin -->



	<%
		int compteurAdresses = 2;
	%>

	<dsp:droplet name="ForEach">
		<dsp:param name="array" param="profile.secondaryAddresses"/>
		<dsp:param name="elementName" value="adresseLiv"/>
		<dsp:oparam name="output">
			<!-- debut -->
				
			<input type="text" name="keyLiv<dsp:valueof param="index"/>" value="<dsp:valueof param="key"/>"/>
			<TABLE width="600" border="0" cellspacing="0" cellpadding="0" align="center">
				<TR><TD align="left" class="moncasto">Adresse de Livraison <%=compteurAdresses %></TD></TR>
			</TABLE>
			<TABLE width="600" border="0" cellspacing="0" cellpadding="0" align="center">
			    <TR bgcolor="#FFDE63"> 
			      <TD width="1"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
			      <TD width="10"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
			      <TD width="289"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
			      <TD width="289"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
			      <TD width="10"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
			      <TD width="1"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
			    </TR>
			    <TR> 
			      <TD width="1" bgcolor="#FFDE63"><IMG src="../img/1pixel.gif" width="1" height="1"></TD>
			      <TD width="10" valign="MIDDLE" class="texte" align="CENTER"></TD>
			      <TD width="289" valign="MIDDLE" class="texte" align="center" height="30">Adresse 
			        :&nbsp; 
			        <dsp:textarea cols="35" iclass="moncasto" rows="4" bean="CastoProfileAdminFormHandler.value.adresseLivraison1.address1" name="adresse1Liv${param['index']}"><dsp:valueof param="adresseLiv.address1"/></dsp:textarea>
			      </TD>
			      <TD width="289" valign="MIDDLE" class="texte" align="center">Compl&eacute;ment d'adresse (&eacute;tage,entr&eacute;e,porte ...) 
			        :&nbsp; 
			        <dsp:input name="adresse2Liv${param['index']}" iclass="moncasto" type="TEXT" size="30" maxsize="100" bean="CastoProfileAdminFormHandler.value.adresseLivraison1.address2" value="${param['adresseLiv.address2']}"/>
			      </TD>
			      <TD width="10" valign="MIDDLE" class="texte" align="CENTER">&nbsp;</TD>
			      <TD width="1" align="CENTER" bgcolor="#FFDE63"><IMG src="../img/1pixel.gif" width="1" height="1"></TD>
			    </TR>
				<TR> 
			      <TD width="1" bgcolor="#FFDE63"><IMG src="../img/1pixel.gif" width="1" height="1"></TD>
			      <TD width="10" valign="MIDDLE" class="texte" align="CENTER"></TD>
			      <TD width="289" valign="MIDDLE" class="texte" align="center" height="30">Code postal 
			        :&nbsp; 
			        <dsp:input iclass="moncasto" type="TEXT" size="7" maxsize="5" bean="CastoProfileAdminFormHandler.value.adresseLivraison1.postalCode" name="cpLiv${param['index']}" paramvalue="adresseLiv.postalCode"/>
			      </TD>
			      <TD width="289" valign="MIDDLE" class="texte" align="center">Ville 
				  	:&nbsp;
				    <dsp:input iclass="moncasto" type="TEXT" size="30" maxsize="100" bean="CastoProfileAdminFormHandler.value.adresseLivraison1.city" name="villeLiv${param['index']}" paramvalue="adresseLiv.city"/>
				  </TD>
			      <TD width="10" valign="MIDDLE" class="texte" align="CENTER">&nbsp;</TD>
			      <TD width="1" align="CENTER" bgcolor="#FFDE63"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
			    </TR>
			    <TR bgcolor="#FFDE63"> 
			      <TD width="1"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
			      <TD width="10"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
			      <TD width="289"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
			      <TD width="289"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
			      <TD width="10"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
			      <TD width="1"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
			    </TR>
			  </TABLE>  
 			 	<BR>
			<!-- fin -->
			<%
				compteurAdresses = compteurAdresses + 1;
			%>
		</dsp:oparam>	
	</dsp:droplet>
	
	
	
	<%
		compteurAdresses = 2;
	%>

	<dsp:droplet name="ForEach">
		<dsp:param name="array" param="profile.secondaryBillingAddresses"/>
		<dsp:param name="elementName" value="adresseFac"/>
		<dsp:oparam name="output">
			<input type="text" name="keyFac<dsp:valueof param="index"/>" value="<dsp:valueof param="key"/>"/>
			<!-- debut -->
			<TABLE width="600" border="0" cellspacing="0" cellpadding="0" align="center">
				<TR><TD align="left" class="moncasto">Adresse de Facturation <%=compteurAdresses %></TD></TR>
			</TABLE>
			<TABLE width="600" border="0" cellspacing="0" cellpadding="0" align="center" bgcolor="#fffcc">
			    <TR bgcolor="#FFDE63"> 
			      <TD width="1"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
			      <TD width="10"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
			      <TD width="289"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
			      <TD width="289"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
			      <TD width="10"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
			      <TD width="1"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
			    </TR>
			    <TR> 
			      <TD width="1" bgcolor="#FFDE63"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
			      <TD width="10" valign="MIDDLE" class="texte" align="CENTER"></TD>
			      <TD width="289" valign="MIDDLE" class="texte" align="center" height="30">Adresse 
			        :&nbsp; 
			        <dsp:textarea cols="35" iclass="moncasto" rows="4" bean="CastoProfileAdminFormHandler.value.adresseLivraison1.address1" name="adresse1Fac${param['index']}"><dsp:valueof param="adresseFac.address1"/></dsp:textarea>
			      </TD>
			      <TD width="289" valign="MIDDLE" class="texte" align="center">Compl&eacute;ment d'adresse (&eacute;tage,entr&eacute;e,porte ...) 
			        :&nbsp; 
			        <dsp:input name="adresse2Fac${param['index']}" iclass="moncasto" type="TEXT" size="30" maxsize="100" bean="CastoProfileAdminFormHandler.value.adresseLivraison1.address2" paramvalue="adresseFac.address2"/>
			      </TD>
			      <TD width="10" valign="MIDDLE" class="texte" align="CENTER">&nbsp;</TD>
			      <TD width="1" align="CENTER" bgcolor="#FFDE63"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
			    </TR>
				<TR> 
			      <TD width="1" bgcolor="#FFDE63"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
			      <TD width="10" valign="MIDDLE" class="texte" align="CENTER"></TD>
			      <TD width="289" valign="MIDDLE" class="texte" align="center" height="30">Code postal 
			        :&nbsp; 
			        <dsp:input iclass="moncasto" type="TEXT" size="7" maxsize="5" bean="CastoProfileAdminFormHandler.value.adresseLivraison1.postalCode" name="cpFac${param['index']}" paramvalue="adresseFac.postalCode"/>
			      </TD>
			      <TD width="289" valign="MIDDLE" class="texte" align="center">Ville 
				  	:&nbsp;
				    <dsp:input iclass="moncasto" type="TEXT" size="30" maxsize="100" bean="CastoProfileAdminFormHandler.value.adresseLivraison1.city" name="villeFac${param['index']}" paramvalue="adresseFac.city"/>
				  </TD>
			      <TD width="10" valign="MIDDLE" class="texte" align="CENTER">&nbsp;</TD>
			      <TD width="1" align="CENTER" bgcolor="#FFDE63"><IMG src="../img/1pixel.gif" width="1" height="1"></TD>
			    </TR>
			    <TR bgcolor="#FFDE63"> 
			      <TD width="1"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
			      <TD width="10"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
			      <TD width="289"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
			      <TD width="289"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
			      <TD width="10"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
			      <TD width="1"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
			    </TR>
			  </TABLE>  
 			 	<BR>
			<!-- fin -->
			<%
				compteurAdresses = compteurAdresses + 1;
			%>
		</dsp:oparam>	
	</dsp:droplet>
	
	
	
	<%
		compteurAdresses = 2;
	%>

	<dsp:droplet name="ForEach">
		<dsp:param name="array" param="profile.secondaryBillingShippingAddresses"/>
		<dsp:param name="elementName" value="adresseFacLiv"/>
		<dsp:oparam name="output">
			<input type="text" name="keyFacLiv<dsp:valueof param="index"/>" value="<dsp:valueof param="key"/>"/>
			<!-- debut -->
			<TABLE width="600" border="0" cellspacing="0" cellpadding="0" align="center">
				<TR><TD align="left" class="moncasto">Adresse de Livraison / Facturation</TD></TR>
			</TABLE>
			<TABLE width="600" border="0" cellspacing="0" cellpadding="0" align="center">
			    <TR bgcolor="#FFDE63"> 
			      <TD width="1"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
			      <TD width="10"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
			      <TD width="289"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
			      <TD width="289"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
			      <TD width="10"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
			      <TD width="1"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
			    </TR>
			    <TR> 
			      <TD width="1" bgcolor="#FFDE63"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
			      <TD width="10" valign="MIDDLE" class="texte" align="CENTER"></TD>
			      <TD width="289" valign="MIDDLE" class="texte" align="center" height="30">Adresse 
			        :&nbsp; 
			        <dsp:textarea cols="35" iclass="moncasto" rows="4" bean="CastoProfileAdminFormHandler.value.adresseLivraison1.address1" name="adresse1FacLiv${param['index']}"><dsp:valueof param="adresseFacLiv.address1"/></dsp:textarea>
			      </TD>
			      <TD width="289" valign="MIDDLE" class="texte" align="center">Compl&eacute;ment d'adresse (&eacute;tage,entr&eacute;e,porte ...) 
			        :&nbsp; 
			        <dsp:input name="adresse2FacLiv${param['index']}" iclass="moncasto" type="TEXT" size="30" maxsize="100" bean="CastoProfileAdminFormHandler.value.adresseLivraison1.address2" paramvalue="adresseFacLiv.address2"/>
			      </TD>
			      <TD width="10" valign="MIDDLE" class="texte" align="CENTER">&nbsp;</TD>
			      <TD width="1" align="CENTER" bgcolor="#FFDE63"><IMG src="../img/1pixel.gif" width="1" height="1"></TD>
			    </TR>
				<TR> 
			      <TD width="1" bgcolor="#FFDE63"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
			      <TD width="10" valign="MIDDLE" class="texte" align="CENTER"></TD>
			      <TD width="289" valign="MIDDLE" class="texte" align="center" height="30">Code postal 
			        :&nbsp; 
			        <dsp:input iclass="moncasto" type="TEXT" size="7" maxsize="5" bean="CastoProfileAdminFormHandler.value.adresseLivraison1.postalCode" name="cpFacLiv${param['index']}" paramvalue="adresseFacLiv.postalCode"/>
			      </TD>
			      <TD width="289" valign="MIDDLE" class="texte" align="center">Ville 
				  	:&nbsp;
				    <dsp:input iclass="moncasto" type="TEXT" size="30" maxsize="100" bean="CastoProfileAdminFormHandler.value.adresseLivraison1.city" name="villeFacLiv${param['index']}" paramvalue="adresseFacLiv.city"/>
				  </TD>
			      <TD width="10" valign="MIDDLE" class="texte" align="CENTER">&nbsp;</TD>
			      <TD width="1" align="CENTER" bgcolor="#FFDE63"><IMG src="../img/1pixel.gif" width="1" height="1"></TD>
			    </TR>
			    <TR bgcolor="#FFDE63"> 
			      <TD width="1"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
			      <TD width="10"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
			      <TD width="289"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
			      <TD width="289"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
			      <TD width="10"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
			      <TD width="1"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
			    </TR>
			  </TABLE>  
 			 	<BR>
			<!-- fin -->
			<%
				compteurAdresses = compteurAdresses + 1;
			%>
		</dsp:oparam>	
	</dsp:droplet>
	
	
	


	 <TABLE width="600" border="0" cellspacing="0" cellpadding="0">
        <TR align="center">
         <TD valign=top><A href="javascript:history.back();"><IMG src="../../img/annuler.gif" border=0"></A></TD>
		  <TD>
          <dsp:input type="image" src="../../img/valider.gif" bean="CastoProfileAdminFormHandler.updateProfileContactInfo"/>
          </TD>
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