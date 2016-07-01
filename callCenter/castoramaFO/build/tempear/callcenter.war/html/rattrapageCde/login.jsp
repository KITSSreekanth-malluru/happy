<dsp:page>

	<%--
-------------------------------------------------------------------------------------------- 
Imports-------------------------------------------------------------------------------------
--------------------------------------------------------------------------------------------
--%>
	<dsp:importbean bean="/castorama/login/CastoCallCenterProfileFormHandler" />
	<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
	<dsp:importbean bean="/atg/dynamo/droplet/Switch" />

	<%-- 
------------------------------------------------------------------------------------------ 
Sécurisation HTTPS------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>
	<dsp:include page="./common/protocolChange.jsp">
		<dsp:param name="protocol" value="secure" />
	</dsp:include>


	<%-- 
------------------------------------------------------------------------------------------ 
Entête------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>
	<HTML>
	<TITLE>Castorama : Outil de rattrapage des commandes</TITLE>
	<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	<LINK rel="stylesheet" href="../css/hp.css">

	<%-- 
------------------------------------------------------------------------------------------ 
Page de login-----------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>
	<BODY bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0"
		marginheight="0">
	<CENTER><BR>
	<TABLE width="400" align="center" border="0" cellspacing="0"
		cellpadding="0">
		<TR>
			<TD width="14"><IMG src="../img/clou_bleufg.gif" width="16"
				height="18"></TD>

			<TD width="364" bgcolor="#003399" align="center" class="form">Outil de rattrapage des commandes</TD>
			<TD width="14"><IMG src="../img/clou_bleufd.gif" width="18"
				height="18"></TD>
		</TR>
	</TABLE>

	<%-- SI l'utilisateur est deja loggué, alors delogguer --%> <dsp:form
		action="/adminFO/html/rattrapageCde/login_success.jsp" method="POST">
		<dsp:input type="hidden"
			bean="CastoCallCenterProfileFormHandler.loginSuccessURL"
			value="./login_success.jsp" />
		<dsp:input type="hidden"
			bean="CastoCallCenterProfileFormHandler.loginErrorURL"
			value="./login.jsp" />



		<TABLE width="650" border="0" cellspacing="0" cellpadding="0"
			align="center">
			<TR bgcolor="#FFDE63">
				<TD width="1"><IMG src="../img/1pixel.gif" width="1" height="1"></TD>
				<TD width="10"><IMG src="../img/1pixel.gif" width="1"
					height="1"></TD>
				<TD width="328"><IMG src="../img/1pixel.gif" width="1"
					height="1"></TD>
				<TD width="300"><IMG src="../img/1pixel.gif" width="1"
					height="1"></TD>
				<TD width="10"><IMG src="../img/1pixel.gif" width="1"
					height="1"></TD>
				<TD width="1"><IMG src="../img/1pixel.gif" width="1" height="1"></TD>
			</TR>
			<TR>
				<TD width="1" bgcolor="#FFDE63"><IMG src="../img/1pixel.gif"
					width="1" height="1"></TD>
				<TD width="10" valign="MIDDLE" class="texte" align="CENTER"></TD>
				<TD width="328" valign="MIDDLE" class="texte" align="center"
					height="30">Login (v&eacute;rifier majuscule/minuscule) :
				&nbsp; <dsp:input type="text" name="login" size="20" maxsize="20"
					bean="CastoCallCenterProfileFormHandler.value.login" alt="login"
					iclass="moncasto" value="" /></TD>
				<TD width="300" valign="MIDDLE" class="texte" align="center"
					height="30">Mot de passe : &nbsp; <dsp:input type="password"
					name="password" size="35"
					bean="CastoCallCenterProfileFormHandler.value.password"
					alt="password" maxlength="35" iclass="moncasto" value="" /></TD>
				<TD width="10" valign="MIDDLE" class="texte" align="CENTER">&nbsp;</TD>
				<TD width="1" align="CENTER" bgcolor="#FFDE63"><IMG
					src="../img/1pixel.gif" width="1" height="1"></TD>
			</TR>
			<TR bgcolor="#FFDE63">
				<TD width="1"><IMG src="../img/1pixel.gif" width="1" height="1"></TD>
				<TD width="10"><IMG src="../img/1pixel.gif" width="1"
					height="1"></TD>
				<TD width="328"><IMG src="../img/1pixel.gif" width="1"
					height="1"></TD>
				<TD width="300"><IMG src="../img/1pixel.gif" width="1"
					height="1"></TD>
				<TD width="10"><IMG src="../img/1pixel.gif" width="1"
					height="1"></TD>
				<TD width="1"><IMG src="../img/1pixel.gif" width="1" height="1"></TD>
			</TR>
		</TABLE>
		<BR>
		<TABLE width="760" border="0" cellspacing="0" cellpadding="0">
			<TR align="center">
				<TD><dsp:input type="hidden" value="Log in"
					bean="CastoCallCenterProfileFormHandler.login" /> <dsp:input type="image" src="../img/login.gif" border="0" bean="CastoCallCenterProfileFormHandler.login" /></TD>
			</TR>
		</TABLE>

	</dsp:form> 
	
	

	
	<dsp:droplet name="ErrorMessageForEach">
		<dsp:param bean="CastoCallCenterProfileFormHandler.formExceptions" name="exceptions"/>
		<dsp:oparam name="output">
			<SPAN class="texterou"><img src="<%=request.getContextPath()%>/html/img/flecheb_retrait.gif" border=0><dsp:valueof param="message" /></span><br />		
		</dsp:oparam>
	</dsp:droplet>
	
	

	
	</CENTER>
	</BODY>
	</HTML>

</dsp:page>
