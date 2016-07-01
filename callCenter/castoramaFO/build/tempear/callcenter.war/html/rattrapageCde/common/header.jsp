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

<dsp:form action="header.jsp" method="post">
<TABLE width="600" cellspacing=0 cellpadding=0 border=0 align="center">
   <TR valign=top>
      
		<%-- GY : stabilisation --%>
		<dsp:setvalue bean="Profile.actionUtilisateur" value="${pageContext.request.requestURI}?${pageContext.request.queryString}" />

      <TD>&nbsp;&nbsp;</TD>
      <TD align="center" class="moncasto"> Vous &ecirc;tes connect&eacute; : <dsp:valueof bean="Profile.login"/>
		<dsp:input type="hidden" bean="CastoCallCenterProfileFormHandler.logoutSuccessURL" value="/adminFO/html/rattrapageCde/login.jsp" />      	
		</FONT><BR><BR>
          <A href="<%=request.getContextPath()%>/html/call_center/login_success.jsp"><IMG src="<%=request.getContextPath()%>/html/img/menu_gene.gif" border=0></A>
          &nbsp;<dsp:input type="image" src="/adminFO/html/img/deconnexion.gif" bean="CastoCallCenterProfileFormHandler.logout" value="Se déconnecter" /> 
          
          <BR>
      </TD>

   </TR>
   <TR><TD colspan=2>&nbsp;</TD></TR>
   <TR><TD colspan=2><dsp:include page="./menu.jsp"/></TD></TR>
</TABLE>
</dsp:form>
</dsp:page>