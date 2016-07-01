<dsp:page xml="true">

	<%-- 
------------------------------------------------------------------------------------------ 
IMPORTS-----------------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>
<dsp:importbean bean="/castorama/BeanOrderTransaction"/>
<dsp:importbean bean="/castorama/SessionBeanOrder"/>


	<%-- 
------------------------------------------------------------------------------------------ 
Sécurisation HTTPS------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>
	<dsp:include page="../../common/protocolChange.jsp"> <dsp:param name="protocol" value="secure" /> </dsp:include>

<HTML> <HEAD>
<LINK REL="stylesheet" HREF="../../../css/hp.css">
<TITLE></TITLE>
</HEAD>

<BODY BGCOLOR="#FFFFFF">

<dsp:include page="../../common/header.jsp"/>
<center><font face="Tahoma, Arial, Helvetica, sans-serif" size="3" color="#003399">La transaction n'a pas &eacute;t&eacute; effectu&eacute;e.<br></font></center>
</BODY> </HTML>

</dsp:page>