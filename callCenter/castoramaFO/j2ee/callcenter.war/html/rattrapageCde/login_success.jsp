<dsp:importbean bean="/atg/userprofiling/Profile" />
<dsp:importbean bean="/castorama/login/CastoCallCenterProfileFormHandler" />

<dsp:page>

<%-- 
------------------------------------------------------------------------------------------ 
SÃ©curisation HTTPS------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>
<dsp:include page="./common/protocolChange.jsp"><dsp:param name="protocol" value="secure"/></dsp:include>
<HTML>
<TITLE>Castorama : Outil de rattrapage des commandes</TITLE>
<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<LINK rel="stylesheet" href="../css/hp.css">
<dsp:form action="./common/header.jsp" method="post">
<TABLE width="600" cellspacing=0 cellpadding=0 border=0 align="center">
   <TR valign=top>
		<dsp:setvalue bean="Profile.actionUtilisateur" value="${pageContext.request.requestURI}?${pageContext.request.queryString}" />
      <TD>&nbsp;&nbsp;</TD>
      <TD align="center" class="moncasto"> Vous &ecirc;tes connect&eacute; : <dsp:valueof bean="Profile.login"/>
		<dsp:input type="hidden" bean="CastoCallCenterProfileFormHandler.logoutSuccessURL" value="/adminFO/html/rattrapageCde/login.jsp" />      	
		</FONT><BR><BR>
          <A href="<%=request.getContextPath()%>/html/rattrapageCde/login_success.jsp"><IMG src="<%=request.getContextPath()%>/html/img/menu_gene.gif" border=0></A>
          &nbsp;<dsp:input type="image" src="/adminFO/html/img/deconnexion.gif" bean="CastoCallCenterProfileFormHandler.logout" value="Se déconnecter" /> 
          
          <BR>
      </TD>

   </TR>
</TABLE>
</dsp:form>


<BODY bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
	<dsp:include page="./includes/inc-menu-choix-dates.jsp"/>
</BODY>
</HTML>

</dsp:page>