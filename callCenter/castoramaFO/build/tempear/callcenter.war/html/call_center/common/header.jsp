<dsp:page>

<%--
-------------------------------------------------------------------------------------------- 
Imports-------------------------------------------------------------------------------------
--------------------------------------------------------------------------------------------
--%>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/castorama/CastoOrderEditor"/>
<dsp:importbean bean="/atg/userprofiling/Profile" />
<dsp:importbean bean="/castorama/login/CastoCallCenterProfileFormHandler" />
<%-- 
------------------------------------------------------------------------------------------ 
SÃ©curisation HTTPS------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>
<dsp:include page="./protocolChange.jsp"><dsp:param name="protocol" value="secure"/></dsp:include>

<dsp:form formid="formHeader" action="header.jsp" method="post">
<TABLE width="600" cellspacing=0 cellpadding=0 border=0 align="center">
   <TR valign=top>
      
		<%-- GY : stabilisation --%>
		<dsp:setvalue bean="Profile.actionUtilisateur" value="${pageContext.request.requestURI}?${pageContext.request.queryString}" />

      <TD>&nbsp;&nbsp;</TD>
      <TD align="center" class="moncasto"> Vous &ecirc;tes connect&eacute; : <dsp:valueof bean="Profile.login"/>
		<dsp:input type="hidden" bean="CastoCallCenterProfileFormHandler.logoutSuccessURL" value="${pageContext.request.contextPath}/html/call_center/login.jsp" />      	
		<BR><BR>
          <A href="${pageContext.request.contextPath}/html/call_center/login_success.jsp"><IMG src="${pageContext.request.contextPath}/html/img/menu_gene.gif" border=0></A>
          &nbsp;<dsp:input type="image" src="${pageContext.request.contextPath}/html/img/deconnexion.gif" bean="CastoCallCenterProfileFormHandler.logout" value="Se déconnecter" /> 
          
          <BR>
      </TD>

   </TR>
   <TR><TD colspan=2>&nbsp;</TD></TR>
   <TR><TD colspan=2><dsp:include page="./menu.jsp"/></TD></TR>
   	
</TABLE>
</dsp:form>

 
<TABLE width="600" align="center" cellspacing=0 cellpadding=0 border=0 >
 <TR valign=top>
  <TD>&nbsp;</TD>
  <TD align="center">	
	

<dsp:droplet name="Switch">
 <dsp:param name="value" bean="CastoOrderEditor.formError"/>
 <dsp:oparam name="true">
   &nbsp;<BR>
   <SPAN class=Prix>Une erreur est survenu lors de la manipulation des commandes :</SPAN><BR>
   <SPAN class=Prix>
   <UL>
      	<dsp:droplet name="Switch">
		 	<dsp:param name="value" param="message"/>
			<dsp:oparam name="default">
				<SPAN class="texterou"><IMG src="${pageContext.request.contextPath}/html/img/flecheb_retrait.gif" border=0>&nbsp;Vous ne pouvez pas supprimer tous les articles de la commande<BR></SPAN>
				<SPAN class="texterou"><IMG src="${pageContext.request.contextPath}/html/img/flecheb_retrait.gif" border=0>&nbsp;La quantit&eacute; doit &ecirc;tre sup&eacute;rieure &agrave; 0<BR></SPAN>
			</dsp:oparam>			
		</dsp:droplet>
	</UL>
  </SPAN>
  <dsp:setvalue bean="CastoOrderEditor.resetFormErrors" value=""/>
 </dsp:oparam>
</dsp:droplet>
 </TD></TR>
 </TABLE>

</dsp:page>