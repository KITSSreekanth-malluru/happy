
<dsp:page xml="true">


<%-- 
------------------------------------------------------------------------------------------ 
IMPORTS-----------------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>
<dsp:importbean bean="/castorama/RechercheCommandeFormHandler"/>


<%-- 
------------------------------------------------------------------------------------------ 
Sécurisation HTTPS------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>
<dsp:include page="../../common/protocolChange.jsp"><dsp:param name="protocol" value="secure"/></dsp:include>


<HTML>
<HEAD>
<TITLE>Castorama : Call center - Rechercher un contact</TITLE>
<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<LINK rel="stylesheet" href="../../../css/hp.css">
</HEAD>

<BODY bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
    
    <dsp:droplet name="/atg/targeting/RepositoryLookup">
		<dsp:param name="repository" bean="/atg/userprofiling/ProfileAdapterRepository" />
		<dsp:param name="itemDescriptor" value="user" />
		<dsp:param name="id" param="profileId" />		
		<dsp:param name="elementName" value="profile"/>							
		<dsp:oparam name="output">
	 		<dsp:droplet name="/atg/dynamo/droplet/IsNull">
	          <dsp:param name="value" param="profile"/>
	          <dsp:oparam name="true">
	          	 <dsp:include page="../../common/header.jsp"/>
	          </dsp:oparam> 
	          <dsp:oparam name="false">	           	 
				 <dsp:include page="../../common/header.jsp">
	             	<dsp:param name="id" param="profileId"/>
	             	<dsp:param name="user" param="profile.login"/>
	            </dsp:include>
	          </dsp:oparam> 
	      	</dsp:droplet>
     	</dsp:oparam>
	</dsp:droplet>

<TABLE align=center width=600 border=0 cellpadding=0 cellspacing=0>
<TR><TD><SPAN class=prix align=center>Resultat de la recherche :</SPAN></TD></TR>
<TR><TD><SPAN align=right><IMG src="${pageContext.request.contextPath}/html/img/flecheb_retrait.gif" border=0>&nbsp;<A class=moncasto href="javascript:history.back();">retour</A></SPAN></TD></TR>
</TABLE>
<BR>
<TABLE cellpadding="0"  cellspacing="0" border=1 bordercolor="#CC0033" width=450 align=center>
	<TR>
		<TD align=center>
			<SPAN class=prix>Cliquer sur le num&eacute;ro de la commande pour la voir ou la modifier.</SPAN>
		</TD>
	</TR>
</TABLE><BR>

<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">  
  <dsp:param name="value" bean="RechercheCommandeFormHandler.resultatRecherche"/>	
  <dsp:oparam name="false">
   		<dsp:droplet name="/atg/dynamo/droplet/TableRange">
    		<dsp:param name="array" bean="RechercheCommandeFormHandler.resultatRecherche"/>
   			<dsp:param name="numColumns" value="1"/>
   			<dsp:param name="howMany" value="25"/>
    		<dsp:oparam name="outputStart">
    			<TABLE width=600 align="center" cellspacing=0 cellpadding=3 border=0>
       				<TR align="center" valign="middle" bgcolor="#FFDF63">
				       	<TD align="center" width=100 align="center" class=moncasto>Commande</TD>
				        <TD align="center" width=100 class=moncasto align=center>Internaute</TD>
				        <TD align="center" width=150 class=moncasto align=center>Date</TD>
				       	<TD align="center" width=250 class=moncasto align=center>Etat</TD>
       				</TR>       
  			</dsp:oparam>
  			<dsp:oparam name="outputEnd">
   				</TABLE>
   		
				<TABLE width="600" border="0" cellspacing="0" cellpadding="0" align="center">
					<TR>
						<TD align=center colspan=8>
							<dsp:droplet name="/atg/dynamo/droplet/Switch">
								<dsp:param name="value" param="hasPrev"/>
								<dsp:oparam name="true">
									<A class="moncasto" href="<dsp:valueof bean="/OriginatingRequest.pathInfo"/>?start=<dsp:valueof param="prevStart"/>">
										Pr&eacute;c&eacute;dent&nbsp;<dsp:valueof param="prevHowMany"/>
									</A>&nbsp;&nbsp;&nbsp;&nbsp;
								</dsp:oparam>
							</dsp:droplet>
							<dsp:droplet name="/atg/dynamo/droplet/Switch">
								<dsp:param name="value" param="hasNext"/>
								<dsp:oparam name="true">
									<A class="moncasto" href="<dsp:valueof bean="/OriginatingRequest.pathInfo"/>?start=<dsp:valueof param="nextStart"/>">
										Suivant&nbsp;<dsp:valueof param="nextHowMany"/>
									</A>
								</dsp:oparam>
							</dsp:droplet>
						</TD>
					</TR>
				</TABLE>
  			</dsp:oparam>
		    <dsp:oparam name="outputRowStart">
			    <TR>
		    </dsp:oparam>
  			<dsp:oparam name="outputRowEnd">
   				 </TR>
  			</dsp:oparam>
    		<dsp:oparam name="output">
				<dsp:droplet name="/atg/dynamo/droplet/IsNull">
			        <dsp:param name="value" param="element.submittedDate"/>
					<dsp:oparam name="false">
						<TR>
							<TD align="center" class="moncasto">
        <dsp:getvalueof var="orderId" param="element.repositoryId"/>
  <c:set var="numOrder" value="${fn:substring(orderId, 1, -1)}"/>
  <c:choose>
    <c:when test="${numOrder < 400000000}">
			<A href="../show/order_migr.jsp?id=<dsp:valueof param="element.repositoryId"/>"><dsp:valueof param="element.repositoryId"/></A>
    </c:when>
    <c:otherwise> 
			<A href="../show/order.jsp?id=<dsp:valueof param="element.repositoryId"/>"><dsp:valueof param="element.repositoryId"/></A>
     </c:otherwise> 
  </c:choose>
							</TD>
					        <TD align="center" class="moncasto"><dsp:valueof param="element.profileId"/></TD>
							<TD align="center" class="moncasto"><dsp:valueof param="element.submittedDate" date="dd/M/yyyy HH:mm:ss"><I>non renseign&eacute;e</I></dsp:valueof></TD>
							<TD align="center" class="moncasto">
							<dsp:setvalue param="etat" paramvalue="element.BOState"/>
							<dsp:include page="../../common/fragmentEtat.jsp">
								<dsp:param name="state" param="etat"/>
							</dsp:include>
							</TD>
						</TR>
					</dsp:oparam>
	  			</dsp:droplet>		
			</dsp:oparam>
    		<dsp:oparam name="empty">
      			<SPAN class=prix>Il n'y a aucun r&eacute;sultat!</SPAN>
    		</dsp:oparam>
  		</dsp:droplet>
  </dsp:oparam>
  <dsp:oparam name="true">
	<TABLE align="center" width="600" border="0" cellpadding="0" cellspacing="0">
		<TR><TD>
  			<SPAN class=prix>Il n'y a aucun r&eacute;sultat !</SPAN>
		</TD></TR>
	</TABLE>
  </dsp:oparam>
  <%--<dsp:param name="value" bean="RechercheCommandeFormHandler.resultatRecherche"/>--%>
</dsp:droplet>

<dsp:include page="../../common/menuBas.jsp"/>

</BODY> </HTML>

</dsp:page>



