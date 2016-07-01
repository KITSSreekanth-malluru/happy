<dsp:page>

<%-- 
------------------------------------------------------------------------------------------ 
Sécurisation HTTPS------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>
<dsp:importbean bean="/atg/userprofiling/Profile" />

<dsp:include page="./common/protocolChange.jsp"><dsp:param name="protocol" value="secure"/></dsp:include>


<HTML>
<TITLE>Castorama : Call center</TITLE>
<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<LINK rel="stylesheet" href="../css/hp.css">
<BODY bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">

<dsp:droplet name="/castorama/profile/CastoGetPrivileges">
	<dsp:param name="profile" bean="Profile"/>	
	<dsp:param name="requis" value="bla modifier"/>	
	<dsp:oparam name="output">
		<dsp:valueof param="privileges"/>
		<br/>
		<dsp:valueof param="access"/>
	</dsp:oparam>
</dsp:droplet>	
	
	
</BODY>
</HTML>

</dsp:page>