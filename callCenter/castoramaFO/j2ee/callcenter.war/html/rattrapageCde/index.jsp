<%@page import="atg.servlet.DynamoHttpServletRequest"%>
<dsp:page>

<%-- 
------------------------------------------------------------------------------------------ 
Sécurisation HTTPS------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>
<dsp:include page="./common/protocolChange.jsp"><dsp:param name="protocol" value="secure"/></dsp:include>


<HTML>
<HEAD>
<TITLE>Castorama : Outil de rattrapage des commandes</TITLE>
<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<LINK rel="stylesheet" href="/css/hp.css">
</HEAD>
<BODY bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<dsp:droplet name="/atg/dynamo/droplet/Redirect">
	<dsp:param name="url" value="login.jsp"/>
</dsp:droplet>
</BODY>
</HTML>
</dsp:page>