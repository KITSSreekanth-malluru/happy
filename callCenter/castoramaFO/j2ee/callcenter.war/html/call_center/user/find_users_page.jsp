<dsp:page>


<%-- 
------------------------------------------------------------------------------------------ 
Import------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
<dsp:importbean bean="/castorama/RechercheUserFormHandler"/>
<dsp:importbean bean="/castorama/profile/CastoGetPrivileges"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>


<%-- 
------------------------------------------------------------------------------------------ 
Entete------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>
<HTML>
<HEAD>
<TITLE>Castorama : Call center - Rechercher un internaute</TITLE>
<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<LINK rel="stylesheet" href="../../css/hp.css">
</HEAD>


<BODY bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<dsp:include page="../common/header.jsp"/>
<P>

<dsp:droplet name="CastoGetPrivileges">
 <dsp:param name="profile" bean="Profile"/>
 <dsp:param name="requis" value="commerce-csr-profiles-privilege"/>
 <dsp:oparam name="accesAutorise">
   <dsp:include page="./find_users_simple.jsp"/>
 </dsp:oparam>
 <dsp:oparam name="accesRefuse"></dsp:oparam>
</dsp:droplet>
</BODY>
</HTML>

</dsp:page>