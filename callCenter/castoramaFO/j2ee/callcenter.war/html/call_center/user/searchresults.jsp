<dsp:page>

<dsp:importbean bean="/castorama/RechercheUserFormHandler"/>


<%-- 
------------------------------------------------------------------------------------------ 
Sécurisation HTTPS------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>
<dsp:include page="../common/protocolChange.jsp"><dsp:param name="protocol" value="secure"/></dsp:include>

<HTML>
<HEAD>
<TITLE>Castorama : Call center - Rechercher un contact</TITLE>
<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<LINK rel="stylesheet" href="../../css/hp.css">
</HEAD>


<BODY bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<dsp:include page="../common/header.jsp"/>

<TABLE align=center width=600 border=0 cellpadding=0 cellspacing=0>
<TR><TD><SPAN class=prix align=center>Resultat de la recherche :</SPAN></TD></TR>
<TR><TD><SPAN align=right><IMG src="../../img/flecheb_retrait.gif" border=0>&nbsp;<A class=moncasto href="javascript:history.back();">retour</A></SPAN></TD></TR>
<TR><TD>&nbsp;</TD></TR>
</TABLE>
<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">  
  <dsp:oparam name="false">
   <dsp:droplet name="/atg/dynamo/droplet/TableRange">
   <dsp:param name="numColumns" value="1"/>
    <dsp:param name="array" bean="RechercheUserFormHandler.resultatRecherche"/>
	<dsp:param name="numColumns" value="1"/>
	<dsp:param name="howMany" value="25"/>
    <dsp:oparam name="outputStart">
	<TABLE cellspacing="0" cellpadding="0" align=center bordercolor="#CC0033" border=1 width=400>
        <TR  align="center" >
           <TD valign=top><SPAN class="prix" align=center>&nbsp;Cliquer sur le nom, login ou compte internaute pour le voir ou le modifier.</SPAN></TD>
		</TR>
	</TABLE>
	<BR>
	<TABLE align=center border=0 width=600 cellspacing =0 cellpadding=0>
     <TR bgcolor="#FFDF63">
	 <TD width=1><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
       <TD width=598><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
	   <TD width=1><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
	   </TR>
	   <TR>
	   <TD width=1 bgcolor="#FFDF63"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
       <TD align=center width=598>
    <TABLE align=center border=0 width=598 cellspacing =0 cellpadding=0>
     <TR  align="center">
       <TD width=18>&nbsp;</TD>
       <TD class="texte" align=left width=120>Pr&eacute;nom - Nom</TD>
       <TD class="texte" align=center width=120>Login</TD>
       <TD class="texte	" align=center width=120>Compte internaute</TD>
       <TD class="texte" align=center width=100>Code postal</TD>
       <TD class="texte" align=center width=120>Ville</TD>
    </TR>
	</TABLE>
	</TD>
	<TD width=1 bgcolor="#FFDF63"><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
	   </TR>
	   <TR bgcolor="#FFDF63">
	   <TD width=1><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
       <TD width=598><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
	   <TD width=1><IMG src="../../img/1pixel.gif" width="1" height="1"></TD>
	   </TR>
	   </TABLE>
	<TABLE align=center border=0 width=600>
	</dsp:oparam>
    <dsp:oparam name="outputEnd">
      </TABLE>
		<TABLE width="600" border="0" cellspacing="0" cellpadding="0" align="center">
		<TR>
		<TD align=center colspan=8>
			<dsp:droplet name="/atg/dynamo/droplet/Switch">
				<dsp:param name="value" param="hasPrev"/>
				<dsp:oparam name="true">
					<A class=moncasto href="bean:/OriginatingRequest.pathInfo">
						<dsp:param name="start" param="prevStart"/>
						Pr&eacute;c&eacute;dent&nbsp;<dsp:valueof param="prevHowMany"/>
					</A>&nbsp;&nbsp;&nbsp;&nbsp;
				</dsp:oparam>
			</dsp:droplet>
			<dsp:droplet name="/atg/dynamo/droplet/Switch">
				<dsp:param name="value" param="hasNext"/>
				<dsp:oparam name="true">
					<A class=moncasto href="bean:/OriginatingRequest.pathInfo">
						<dsp:param name="start" param="nextStart"/>
						Suivant&nbsp;<dsp:valueof param="nextHowMany"/>
					</A>
				</dsp:oparam>
			</dsp:droplet>
		</TD>
		</TR>
		</TABLE>

    </dsp:oparam>
    <dsp:oparam name="output">
	<TR>
		<TD class="moncasto" width=18><dsp:a iclass="moncasto" href="./editprofilePage.jsp"><dsp:param name="id" param="element.repositoryId"/><dsp:valueof param="count"></dsp:valueof>.</dsp:a></TD>
		<TD width=120><dsp:a iclass="moncasto" href="./editprofilePage.jsp"><dsp:param name="id" param="element.repositoryId"/>
		<dsp:valueof param="element.firstName">___</dsp:valueof> <dsp:valueof param="element.lastName">___</dsp:valueof></dsp:a></TD>
		<TD class="moncasto" align=center width=120>
		<dsp:a iclass="moncasto" href="./editprofilePage.jsp"><dsp:param name="id" param="element.repositoryId"/>
		<dsp:valueof param="element.login">&nbsp;</dsp:valueof></dsp:a></TD>
		<TD class="moncasto" align=center width=120>
		<dsp:a iclass="moncasto" href="./editprofilePage.jsp"><dsp:param name="id" param="element.repositoryId"/>
		<dsp:valueof param="element.repositoryId"/></dsp:a></TD>
		<TD class="moncasto" align=center width=100><dsp:valueof param="element.billingAddress.postalCode"/></TD>
		<TD class="moncasto" align=center width=120><dsp:valueof param="element.billingAddress.city"/></TD>
	</TR>		
	</dsp:oparam>
    <dsp:oparam name="empty">
      <SPAN class=prix>Il n'y a aucun r&eacute;sultat!</SPAN>
    </dsp:oparam>
  </dsp:droplet>
  </dsp:oparam>
  <dsp:oparam name="true">
	<TABLE align=center width=600 border=0 cellpadding=0 cellspacing=0>
		<TR><TD>
  	<SPAN class=prix>Il n'y a aucun r&eacute;sultat !</SPAN>
		</TD></TR>
		</TABLE>
  </dsp:oparam>
  <dsp:param name="value" value="bean:RechercheUserFormHandler.resultatRecherche"/>
</dsp:droplet>
<dsp:include page="../common/menuBas.jsp"/>

</BODY> </HTML>


</dsp:page>

