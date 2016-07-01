
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
<dsp:importbean bean="/castorama/profile/CastoGetPrivileges"/>


<%-- 
------------------------------------------------------------------------------------------ 
Sécurisation HTTPS------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>
<dsp:include page="../common/protocolChange.jsp"><dsp:param name="protocol" value="secure"/></dsp:include>



<HTML>
<TITLE>Castorama : Call center - Edit T&eacute;l&eacute;phonie</TITLE>
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
	           		<SPAN class=texteg><B>Bloc T&eacute;l&eacute;phonie :</B></SPAN>
	           	<P>
				</TD>
			</TR>
	   		<TR>
	    		<TD width="760" align="center">
				<%
				String l_strCommentaire = "Le bloc telephonie de l'internaute "+request.getParameter("profile.id")+ " a ete modifie";
				%>
	  
	  <dsp:form action="${pageContext.request.requestURI}" method="POST" name="formAdmin">
	    <dsp:input type="hidden" bean="CastoProfileAdminFormHandler.type" value="Contact CallCenter"/>
		<dsp:input type="hidden" bean="CastoProfileAdminFormHandler.action" value="Modification"/>
		<dsp:input type="hidden" bean="CastoProfileAdminFormHandler.commentaire" value="l_strCommentaire"/>
		<dsp:input type="hidden" bean="CastoProfileAdminFormHandler.ip" beanvalue="/atg/dynamo/servlet/pipeline/BrowserAttributes.remoteAddr"/>
		
		

	  
        
        
             <dsp:droplet name="/atg/dynamo/droplet/URLArgument">
                 <dsp:param name="url" value="editprofileTelephonie.jsp"/>
                 <dsp:param name="argName" value="id"/>
                 <dsp:param name="argValue" param="profile.repositoryId"/>
                 <dsp:oparam name="output">
                   <dsp:input type="HIDDEN" bean="CastoProfileAdminFormHandler.updateErrorURL" value="./editprofileTelephonie.jsp?id=${profile.repositoryId}"/>
                 </dsp:oparam>
              </dsp:droplet>

              <dsp:input type="HIDDEN" bean="CastoProfileAdminFormHandler.deleteSuccessURL" value="user_deleted.jsp"/>
              <dsp:droplet name="/atg/dynamo/droplet/URLArgument">
                 <dsp:param name="url" value="editprofilePage.jsp"/>
                 <dsp:param name="argName" value="id"/>
                 <dsp:param name="argValue" param="profile.repositoryId"/>
                 <dsp:oparam name="output">
                   <dsp:input type="hidden" bean="CastoProfileAdminFormHandler.updateSuccessURL" value="./editprofilePage.jsp?id=${profile.repositoryId}"/>
                 </dsp:oparam>
              </dsp:droplet>


            <dsp:input type="hidden" bean="CastoProfileAdminFormHandler.repositoryId" paramvalue="id"/>
			<TABLE width="760" border="0" cellspacing="0" cellpadding="0" align=center>
	    	<TR><TD>
	    		<dsp:include page="./erreurs.jsp"/>
	    	</TD></TR>
	  		</TABLE>
	      	<BR>
			<!-- debut -->
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
      <TD width="1" bgcolor="#FFDE63"  bgcolor="#ffcc00"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
      <TD width="10" valign="MIDDLE" class="texte" align="CENTER"  bgcolor="#ffcc00"></TD>
  
      <TD width="289" valign="MIDDLE" class="texte" align="center" height="30" bgcolor="#ffcc00">
      	Tel Adresse <b>Livraison</b> 1 (active) : &nbsp;
			<dsp:input iclass="moncasto" type="TEXT" size="20" maxsize="20" bean="CastoProfileAdminFormHandler.value.shippingAddress.phoneNumber" paramvalue="profile.shippingAddress.phoneNumber"/>              
      </TD>
      <TD width="289" valign="MIDDLE" class="texte" align="center" bgcolor="#ffcc00">
      	Tel Adresse <b>Facturation</b> 1 (active) : &nbsp;
			<dsp:input iclass="moncasto" type="TEXT" size="20" maxsize="20" bean="CastoProfileAdminFormHandler.value.billingAddress.phoneNumber" paramvalue="profile.billingAddress.phoneNumber"/>         
      </TD>
      <TD width="10" valign="MIDDLE" class="texte" align="CENTER"  bgcolor="#ffcc00">&nbsp;</TD>
      <TD width="1" align="CENTER" bgcolor="#FFDE63"  bgcolor="#ffcc00"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
    </TR>
	
	
	
	<%-- Trzaitement des adresses de LIVRAISON --%>
	<%
		int cptliv = 0;
		int cptAffiche = 2;
	%>
	
	<dsp:droplet name="ForEach">
		<dsp:param name="array" param="profile.secondaryAddresses"/>
		<dsp:param name="elementName" value="adresseCourante"/>
		<dsp:oparam name="output">
			<%
				if (cptliv == 0)
				{%>
					<TR> 
		      		<TD width="1" bgcolor="#FFDE63" bgcolor="#ffffcc"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
		      		<TD width="10" valign="MIDDLE" class="texte" align="CENTER" bgcolor="#ffffcc"></TD>
				<%}
			%>
							
				<TD width="289" valign="MIDDLE" class="texte" align="center" height="30" bgcolor="#ffffcc" >
	      			Tel Adresse <b>Livraison</b> <%=cptAffiche%> : &nbsp;
					<%--<dsp:input iclass="moncasto" type="TEXT" size="20" maxsize="20" bean="CastoProfileAdminFormHandler.value.secondaryAddresses[${param['index']}].phoneNumber" paramvalue="adresseCourante.phoneNumber"/>--%>              
					<input class="moncasto" type="TEXT" size="20" maxsize="20" name="telLiv<c:out value="${param['index']}"/>" value="<c:out value="${param['adresseCourante.phoneNumber'] }"/>"/>              
					<input class="moncasto" type="hidden" size="20" maxsize="20" name="telLivKey<c:out value="${param['index']}"/>" value="<c:out value="${param['key'] }"/>"/>              
					<%
						cptliv = cptliv + 1;
						cptAffiche = cptAffiche + 1;
					%>
	     		 </TD>
     		 
     		 <%
				if (cptliv == 2)
				{
					cptliv = 0;
				%>
						<TD width="10" valign="MIDDLE" class="texte" align="CENTER" bgcolor="#ffffcc" >&nbsp;</TD>
      					<TD width="1" align="CENTER" bgcolor="#FFDE63" bgcolor="#ffffcc" ><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
    				</TR>
				<%}
			%>			
		</dsp:oparam>
	</dsp:droplet>
		
		
	<%
		if (cptAffiche % 2 ==1)
		{%>
			<TD width="289" valign="MIDDLE" class="texte" align="center" height="30" bgcolor="#ffffcc">
	      			&nbsp;
	     	</TD>
	     	<TD width="10" valign="MIDDLE" class="texte" align="CENTER" bgcolor="#ffffcc" >&nbsp;</TD>
      		<TD width="1" align="CENTER" bgcolor="#FFDE63" bgcolor="#ffffcc"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
    		</TR>
		<%}
		
		cptAffiche = 2;
	%>
	
	
	
	
	<%-- Trzaitement des adresses de FACTURATION --%>
	<%
		int cptFac = 0;		
	%>
	
	<dsp:droplet name="ForEach">
		<dsp:param name="array" param="profile.secondaryBillingAddresses"/>
		<dsp:param name="elementName" value="adresseCourante"/>
		<dsp:oparam name="output">
			<%
				if (cptFac == 0)
				{%>
					<TR> 
		      		<TD width="1" bgcolor="#FFDE63"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
		      		<TD width="10" valign="MIDDLE" class="texte" align="CENTER"></TD>
				<%}
			%>
							
				<TD width="289" valign="MIDDLE" class="texte" align="center" height="30" >
	      			Tel Adresse <b>facturation</b> <%=cptAffiche%> : &nbsp;
					<%--<dsp:input iclass="moncasto" type="TEXT" size="20" maxsize="20" bean="CastoProfileAdminFormHandler.value.secondaryBillingAddresses[${param['index']}].phoneNumber" paramvalue="adresseCourante.phoneNumber"/>--%>
					<input class="moncasto" type="TEXT" size="20" maxsize="20" name="telFac<c:out value="${param['index']}"/>" value="<c:out value="${param['adresseCourante.phoneNumber'] }"/>"/>              
					<input class="moncasto" type="hidden" size="20" maxsize="20" name="telFacKey<c:out value="${param['index']}"/>" value="<c:out value="${param['key'] }"/>"/>              
					<%
						cptFac = cptFac + 1;
						cptAffiche = cptAffiche + 1;
					%>
	     		 </TD>
     		 
     		 <%
				if (cptFac == 2)
				{
					cptFac = 0;
				%>
						<TD width="10" valign="MIDDLE" class="texte" align="CENTER" >&nbsp;</TD>
      					<TD width="1" align="CENTER" bgcolor="#FFDE63" ><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
    				</TR>
				<%}
			%>			
		</dsp:oparam>
	</dsp:droplet>
		
		
	<%
		if (cptAffiche % 2 ==1)
		{%>
			<TD width="289" valign="MIDDLE" class="texte" align="center" height="30">
	      			&nbsp;
	     	</TD>
	     	<TD width="10" valign="MIDDLE" class="texte" align="CENTER" >&nbsp;</TD>
      		<TD width="1" align="CENTER" bgcolor="#FFDE63" ><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
    		</TR>
		<%}
		
			cptAffiche = 2;
	%>
	
	
	
	<%-- Trzaitement des adresses de FACTURATION / LIVRAISON --%>
	<%
		int cptFacLiv = 0;		
	%>
	
	<dsp:droplet name="ForEach">
		<dsp:param name="array" param="profile.secondaryBillingShippingAddresses"/>
		<dsp:param name="elementName" value="adresseCourante"/>
		<dsp:oparam name="output">
			<%
				if (cptFacLiv == 0)	
				{%>
					<TR> 
		      		<TD width="1" bgcolor="#FFDE63" bgcolor="#ffffcc"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
		      		<TD width="10" valign="MIDDLE" class="texte" align="CENTER" bgcolor="#ffffcc"></TD>
				<%}
			%>
							
				<TD width="289" valign="MIDDLE" class="texte" align="center" height="30" bgcolor="#ffffcc">
	      			Tel Adresse <b>Livraison / facturation</b> : &nbsp;
					<%--<dsp:input iclass="moncasto" type="TEXT" size="20" maxsize="20" bean="CastoProfileAdminFormHandler.value.secondaryBillingShippingAddresses[${param['index']}].phoneNumber" paramvalue="adresseCourante.phoneNumber"/>              --%>
					<input class="moncasto" type="TEXT" size="20" maxsize="20" name="telFacLiv<c:out value="${param['index']}"/>" value="<c:out value="${param['adresseCourante.phoneNumber'] }"/>"/>              
					<input class="moncasto" type="hidden" size="20" maxsize="20" name="telFacLivKey<c:out value="${param['index']}"/>" value="<c:out value="${param['key'] }"/>"/>              
					<%
						cptFacLiv = cptFacLiv + 1;
						cptAffiche = cptAffiche + 1;
					%>
	     		 </TD>
     		 
     		 <%
				if (cptFacLiv == 2)
				{
					cptFacLiv = 0;
				%>
						<TD width="10" valign="MIDDLE" class="texte" align="CENTER" bgcolor="#ffffcc">&nbsp;</TD>
      					<TD width="1" align="CENTER" bgcolor="#FFDE63" bgcolor="#ffffcc"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
    				</TR>
				<%}
			%>			
		</dsp:oparam>
	</dsp:droplet>
		
		
	<%
		if (cptAffiche % 2 ==1)
		{%>
			<TD width="289" valign="MIDDLE" class="texte" align="center" height="30"bgcolor="#ffffcc">
	      			&nbsp;
	     	</TD>
	     	<TD width="10" valign="MIDDLE" class="texte" align="CENTER" bgcolor="#ffffcc">&nbsp;</TD>
      		<TD width="1" align="CENTER" bgcolor="#FFDE63" bgcolor="#ffffcc"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
    		</TR>
		<%}	
	%>
	
	
	
	
	
	<tr bgcolor="#FFDE63"> 
      <td width="1"><img src="../../img/1pixel.gif" width="1" height="1"></td>
      <td width="10"><img src="../../img/1pixel.gif" width="1" height="1"></td>
      <td width="289"><img src="../../img/1pixel.gif" width="1" height="1"></td>
      <td width="289"><img src="../../img/1pixel.gif" width="1" height="1"></td>
      <td width="10"><img src="../../img/1pixel.gif" width="1" height="1"></td>
      <td width="1"><img src="../../img/1pixel.gif" width="1" height="1"></td>
    </tr>
	
  </TABLE>  
  <BR>
			<!-- debut -->
			<%--<TABLE width="600" border="0" cellspacing="0" cellpadding="0" align="center">
    <TR bgcolor="#FFDE63"> 
      <TD width="1"><IMG src="/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="10"><IMG src="/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="289"><IMG src="/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="289"><IMG src="/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="10"><IMG src="/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="1"><IMG src="/img/1pixel.gif" width="1" height="1"></TD>
    </TR>
    <TR> 
      <TD width="1" bgcolor="#FFDE63"><IMG src="/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="10" valign="MIDDLE" class="texte" align="CENTER"></TD>
      <TD width="289" valign="MIDDLE" class="texte" align="center" height="30">T&eacute;l&eacute;phone portable 
        :&nbsp; 
        <dsp:input iclass="moncasto" type="TEXT" size="20" maxsize="20" bean="CastoProfileAdminFormHandler.value.telephonePortable" paramvalue="profile.telephonePortable"/>
      </TD>
      <TD width="289" valign="MIDDLE" class="texte" align="center">T&eacute;l&eacute;phone priv&eacute; 
        :&nbsp; 
        <dsp:input iclass="moncasto" type="TEXT" size="20" maxsize="20" bean="CastoProfileAdminFormHandler.value.telephonePrive" paramvalue="profile.telephonePrive"/>
      </TD>
      <TD width="10" valign="MIDDLE" class="texte" align="CENTER">&nbsp;</TD>
      <TD width="1" align="CENTER" bgcolor="#FFDE63"><IMG src="/img/1pixel.gif" width="1" height="1"></TD>
    </TR>
	<TR> 
      <TD width="1" bgcolor="#FFDE63"><IMG src="/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="10" valign="MIDDLE" class="texte" align="CENTER"></TD>
      <TD width="289" valign="MIDDLE" class="texte" align="center" height="30">T&eacute;l&eacute;phone travail 
        :&nbsp; 
        <dsp:input iclass="moncasto" type="TEXT" size="20" maxsize="20" bean="CastoProfileAdminFormHandler.value.telephoneTravail" paramvalue="profile.telephoneTravail"/>
      </TD>
      <TD width="289" valign="MIDDLE" class="texte" align="center">&nbsp;</TD>
      <TD width="10" valign="MIDDLE" class="texte" align="CENTER">&nbsp;</TD>
      <TD width="1" align="CENTER" bgcolor="#FFDE63"><IMG src="/img/1pixel.gif" width="1" height="1"></TD>
    </TR>
    <TR bgcolor="#FFDE63"> 
      <TD width="1"><IMG src="/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="10"><IMG src="/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="289"><IMG src="/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="289"><IMG src="/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="10"><IMG src="/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="1"><IMG src="/img/1pixel.gif" width="1" height="1"></TD>
    </TR>
  </TABLE>--%>  
  <BR>
	<!-- fin -->
	 <TABLE width="760" border="0" cellspacing="0" cellpadding="0">
        <TR align="center">
          <TD valign=top><A href="javascript:history.back();"><IMG src="../../img/annuler.gif" border=0"></A></TD>
		  <TD>
          <dsp:input type="image" src="../../img/valider.gif" bean="CastoProfileAdminFormHandler.updateProfile"/>
          </TD>
        </TR>
      </TABLE>
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
