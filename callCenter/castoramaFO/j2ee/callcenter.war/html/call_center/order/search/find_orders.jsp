<dsp:page xml="true">


<%-- 
------------------------------------------------------------------------------------------ 
IMPORTS-----------------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>
<dsp:importbean bean="/atg/commerce/order/FindOrdersFormHandler"/>
<dsp:importbean bean="/castorama/RechercheCommandeFormHandler"/>
<dsp:importbean bean="/castorama/OrderFinderByState"/>
<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
<dsp:importbean bean="/castorama/RechercheCommandeParDateFormHandler"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/castorama/profile/CastoGetPrivileges"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>


	<dsp:droplet name="CastoGetPrivileges">
			    <dsp:param name="profile" bean="Profile"/>
			   	<dsp:param name="requis" value="commerce-csr-orders-privilege,informatique-privilege"/>				   		   		
		   		<dsp:oparam name="accesAutorise">



<TABLE align=center width=600 border=0 cellpadding=0 cellspacing=0>
<TR>
	<TD align=center>
		<SPAN class="prix">Rechercher une commande par :</SPAN>
	</TD>
</TR>
<TR>
 	<TD align=center>
		&nbsp;
	</TD>
</TR>
<TR>
 	<TD align=center>
<dsp:form method="POST" action="./find_order_result.jsp">
<dsp:input type="hidden" bean="FindOrdersFormHandler.generalSuccessURL" value="./find_order_result.jsp"/>
  <TABLE width="600" border="0" cellspacing="0" cellpadding="0" align="center">
    <TR bgcolor="#FFDE63"> 
      <TD width="1"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="10"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="189"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="289"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
	  <TD width="100"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="10"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="1"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
    </TR>
    <TR> 
      <TD width="1" bgcolor="#FFDE63"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="10" valign="MIDDLE" class="texte" align="CENTER"></TD>
      <TD width="189" valign="MIDDLE" class="texte" align="center" height="30">Num&eacute;ro commande 
        :&nbsp; 
      </TD>
	  <TD width="289" valign="MIDDLE" class="texte" align="center" height="30">
	  <dsp:input type="text" size="25" maxlength="150" iclass="moncasto" bean="FindOrdersFormHandler.orderId" value=""/>
	  </TD>
	  <TD width="100" valign="MIDDLE" class="texte" align="center" height="30">
	  	 <TABLE width="100" border="0" cellspacing="0" cellpadding="0">
        	<TR align="center">
        	<TD>
			  	<dsp:input type="hidden" bean="FindOrdersFormHandler.generalFailureURL" value="${pageContext.request.requestURI}"/>
				<dsp:input type="hidden" bean="FindOrdersFormHandler.searchByOrderId" value=" Find this Order "/>
				<dsp:input type="image" src="../../../img/valider.gif" border="0" bean="FindOrdersFormHandler.searchByOrderId" value=" Chercher "/>
			</TD>
        	</TR>
  		</TABLE>
	  </TD>
      <TD width="10" valign="MIDDLE" class="texte" align="CENTER">&nbsp;</TD>
      <TD width="1" align="CENTER" bgcolor="#FFDE63"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
    </TR>
    <TR bgcolor="#FFDE63"> 
      <TD width="1"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="10"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
	  <TD width="189"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="289"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="100"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="10"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="1"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
    </TR>
  </TABLE>  
  <BR>
</dsp:form>
<dsp:form method="POST" action="./find_orders_results.jsp">
<dsp:input type="hidden" bean="FindOrdersFormHandler.generalSuccessURL" value="./find_orders_results.jsp"/>
  <TABLE width="600" border="0" cellspacing="0" cellpadding="0" align="center">
    <TR bgcolor="#FFDE63"> 
      <TD width="1"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="10"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="189"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="289"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="100"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="10"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="1"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
    </TR>
    <TR> 
      <TD width="1" bgcolor="#FFDE63"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="10" valign="MIDDLE" class="texte" align="CENTER"></TD>
      <TD width="189" valign="MIDDLE" class="texte" align="center" height="30">Email Login <BR>(v&eacute;rifier majuscule/minuscule) 
        :&nbsp; 
      </TD>
	  <TD width="289" valign="MIDDLE" class="texte" align="center" height="30">
	  <dsp:input type="text" size="25" maxlength="150" iclass="moncasto" bean="FindOrdersFormHandler.login" value=""/>
	  </TD>
	  <TD width="100" valign="MIDDLE" class="texte" align="center" height="30">
	  	<TABLE width="100" border="0" cellspacing="0" cellpadding="0">
        <TR align="center">
          <TD>
          <dsp:input type="hidden" bean="FindOrdersFormHandler.generalFailureURL" value="${pageContext.request.requestURI}"/>
		  <dsp:input type="hidden" bean="FindOrdersFormHandler.searchByProfileId" value=" Find this User's Orders "/>
		  <dsp:input type="image" src="../../../img/valider.gif" border="0" bean="FindOrdersFormHandler.searchByProfileId" value=" Chercher la commande de ... "/>
		 </TD>
        </TR>
  </TABLE>
      </TD>
      <TD width="10" valign="MIDDLE" class="texte" align="CENTER">&nbsp;</TD>
      <TD width="1" align="CENTER" bgcolor="#FFDE63"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
    </TR>
    <TR bgcolor="#FFDE63"> 
      <TD width="1"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="10"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="189"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="289"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="100"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="10"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="1"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
    </TR>
  </TABLE>  
  <BR>
</dsp:form>
<dsp:form method="POST" action="./find_orders_page.jsp">
<dsp:input type="hidden" bean="RechercheCommandeFormHandler.submitSuccessURL" value="./find_orders_by_profileId.jsp"/>
<dsp:input type="hidden" bean="RechercheCommandeFormHandler.submitErrorURL" value="${pageContext.request.requestURI}"/>
<dsp:input type="hidden" bean="RechercheCommandeFormHandler.profileId" beanvalue="RechercheCommandeFormHandler.profileId"/>
  <TABLE width="600" border="0" cellspacing="0" cellpadding="0" align="center">
    <TR bgcolor="#FFDE63"> 
      <TD width="1"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="10"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="189"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="289"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="100"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="10"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="1"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
    </TR>
    <TR> 
      <TD width="1" bgcolor="#FFDE63"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="10" valign="MIDDLE" class="texte" align="CENTER"></TD>
      <TD width="189" valign="MIDDLE" class="texte" align="center" height="30">Num&eacute;ro Client
        :&nbsp; 
      </TD>
	  <TD width="289" valign="MIDDLE" class="texte" align="center" height="30">
	  <dsp:input type="text" size="25" maxlength="150" iclass="moncasto" bean="RechercheCommandeFormHandler.profileId" name="profileClient"/>
	  </TD>
	  <TD width="100" valign="MIDDLE" class="texte" align="center" height="30">
	  		<TABLE width="100" border="0" cellspacing="0" cellpadding="0">
        	<TR align="center">
        	<TD>
        	  <dsp:input type="image" src="../../../img/valider.gif" border="0"  bean="RechercheCommandeFormHandler.submit"/>
			  </TD>
      		</TR>
		  </TABLE> 
      </TD>
      <TD width="10" valign="MIDDLE" class="texte" align="CENTER">&nbsp;</TD>
      <TD width="1" align="CENTER" bgcolor="#FFDE63"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
    </TR>
	
    <TR bgcolor="#FFDE63"> 
      <TD width="1"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="10"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="189"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="289"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="100"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="10"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="1"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
    </TR>
  </TABLE>
  <BR>
</dsp:form> 
<dsp:form method="POST" action="./find_orders_page.jsp">
<dsp:input type="hidden" bean="RechercheCommandeParDateFormHandler.submitSuccessURL" value="./find_orders_by_Date.jsp"/>
<dsp:input type="hidden"  bean="RechercheCommandeParDateFormHandler.dateDeb" beanvalue="RechercheCommandeParDateFormHandler.dateDeb"/>
<dsp:input type="hidden" bean="RechercheCommandeParDateFormHandler.submitErrorURL" value="${pageContext.request.requestURI}"/>
  <TABLE width="600" border="0" cellspacing="0" cellpadding="0" align="center">
    <TR bgcolor="#FFDE63">
      <TD width="1"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="10"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="189"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="289"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="100"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="10"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="1"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
    </TR>
    <TR>
      <TD width="1" bgcolor="#FFDE63"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="10" valign="MIDDLE" class="texte" align="CENTER"></TD>
      <TD width="189" valign="MIDDLE" class="texte" align="center" height="30">Date (jj/mm/aaaa) &agrave; partir de
        :&nbsp; 
      </TD>
	  <TD width="289" valign="MIDDLE" class="texte" align="center" height="30">
	  <dsp:input type="text" size="25" maxlength="150" name="dateDebutRecherche" iclass="moncasto" bean="RechercheCommandeParDateFormHandler.dateDeb" value=""/>
	  </TD>
	  <TD width="100" valign="MIDDLE" class="texte" align="center" height="30">
	  		<TABLE width="100" border="0" cellspacing="0" cellpadding="0">
        	<TR align="center">
        	<TD>
        	  &nbsp;
			  </TD>
      		</TR>
		  </TABLE>
      </TD>
      <TD width="10" valign="MIDDLE" class="texte" align="CENTER">&nbsp;</TD>
      <TD width="1" align="CENTER" bgcolor="#FFDE63"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
    </TR>
	<TR>
      <TD width="1" bgcolor="#FFDE63"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="10" valign="MIDDLE" class="texte" align="CENTER"></TD>
      <TD width="189" valign="MIDDLE" class="texte" align="center" height="30">jusque ...
        :&nbsp; 
      </TD>
	  <TD width="289" valign="MIDDLE" class="texte" align="center" height="30">
	  <dsp:input type="text" size="25" maxlength="150" name="dateFinRecherche" iclass="moncasto" bean="RechercheCommandeParDateFormHandler.dateFin" value=""/>
	  </TD>
	  <TD width="100" valign="MIDDLE" class="texte" align="center" height="30">
	  		<TABLE width="100" border="0" cellspacing="0" cellpadding="0">
        	<TR align="center">
        	<TD>
        	  <dsp:input type="image" src="../../../img/valider.gif" border="0"  bean="RechercheCommandeParDateFormHandler.submit3"/>
			</TD>
      		</TR>
		  </TABLE>
      </TD>
      <TD width="10" valign="MIDDLE" class="texte" align="CENTER">&nbsp;</TD>
      <TD width="1" align="CENTER" bgcolor="#FFDE63"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
    </TR>
    <TR bgcolor="#FFDE63">
      <TD width="1"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="10"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="189"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="289"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="100"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="10"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="1"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
    </TR>
  </TABLE>
  <BR>
</dsp:form>
<dsp:form method="POST" action="./find_orders.jsp">
<dsp:input type="hidden" bean="OrderFinderByState.submitSuccessURL" value="./resultsByState.jsp"/>
<TABLE width="600" border="0" cellspacing="0" cellpadding="0" align="center">
<TR bgcolor="#FFDE63"> 
	<TD width="1"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
	<TD width="10"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
	<TD width="189"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
	<TD width="289"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
	<TD width="100"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
	<TD width="10"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
	<TD width="1"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
</TR>
<TR> 
	<TD width="1" bgcolor="#FFDE63"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
	<TD width="10" valign="MIDDLE" class="texte" align="CENTER"></TD>
	<TD width="189" valign="MIDDLE" class="texte" align="center" height="30">Etat de la commande :&nbsp; </TD>
	<TD width="289" valign="MIDDLE" class="texte" align="center" height="30">
<dsp:select  bean="OrderFinderByState.state">
	<dsp:option value="A_CONTROLER">A_CONTROLER</dsp:option>
	<dsp:option value="EN_ANOMALIE_FACTURE">EN_ANOMALIE_FACTURE</dsp:option>
	<dsp:option value="EN_ANOMALIE_STOCK">EN_ANOMALIE_STOCK</dsp:option>
	<dsp:option value="EN_PREPARATION">EN_PREPARATION</dsp:option>
	<dsp:option value="EN_SUSPENS">EN_SUSPENS</dsp:option>
	<dsp:option value="ERROR_COMMIT_SIPS">ERROR_COMMIT_SIPS</dsp:option>
	<dsp:option value="EXPEDIEE">EXPEDIEE</dsp:option>
	<dsp:option value="PENDING_CALL_CENTER">PENDING_CALL_CENTER</dsp:option>
	<dsp:option value="PENDING_CHEQUE">PENDING_CHEQUE</dsp:option>
	<dsp:option value="PENDING_REMOVE">PENDING_REMOVE</dsp:option>
	<dsp:option value="PENDING_VIREMENT">PENDING_VIREMENT</dsp:option>
    <dsp:option value="PENDING_PAYBOX">PENDING_PAYBOX</dsp:option>
	<dsp:option value="VALIDE">VALIDE</dsp:option>
	<dsp:option value="TERMINEE">TERMINEE</dsp:option>
    <dsp:option value="FAILED">FAILED</dsp:option>
</dsp:select>
	</TD>
	<TD width="100" valign="MIDDLE" class="texte" align="center" height="30">
		<TABLE width="100" border="0" cellspacing="0" cellpadding="0">
		<TR align="center">
			<TD>
<dsp:input type="image" src="../../../img/valider.gif" border="0"  bean="OrderFinderByState.find"/>
			</TD>
		</TR>
		</TABLE> 
	</TD>
	<TD width="10" valign="MIDDLE" class="texte" align="CENTER">&nbsp;</TD>
	<TD width="1" align="CENTER" bgcolor="#FFDE63"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
</TR>
<%--AP TR> 
	<TD width="1" bgcolor="#FFDE63"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
	<TD width="10" valign="MIDDLE" class="texte" align="CENTER"></TD>
	<TD width="189" valign="MIDDLE" class="texte" align="center" height="30">Avec r&eacute;servation ?&nbsp; </TD>
	<TD width="289" valign="MIDDLE" class="texte" align="center" height="30">
	&nbsp;Oui&nbsp;
	
	<dsp:input type="radio" name="reservable" value="true" bean="OrderFinderByState.reservable"/>&nbsp;Non&nbsp;
	<dsp:input type="radio" name="reservable" value="false" bean="OrderFinderByState.reservable"/>
	</TD>
	<TD width="100" valign="MIDDLE" class="texte" align="center" height="30">
	</TD>
	<TD width="10" valign="MIDDLE" class="texte" align="CENTER">&nbsp;</TD>
	<TD width="1" align="CENTER" bgcolor="#FFDE63"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
</TR AP--%>
<TR bgcolor="#FFDE63">
	<TD width="1"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
	<TD width="10"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
	<TD width="189"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
	<TD width="289"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
	<TD width="100"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
	<TD width="10"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
	<TD width="1"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
</TR>
</TABLE><BR>
</dsp:form> 


<%--
--------------------------- Projet Castorama - La D�fense -----------------------------------
------------ ajout de la possibilite de rechercher les commande par magasin ------------------
--%>
<%--AP- dsp:form method="POST" action="./find_orders_page.jsp">
<dsp:input type="hidden" bean="RechercheCommandeFormHandler.submitSuccessURL" value="./find_orders_by_magasin.jsp"/>
<dsp:input type="hidden" bean="RechercheCommandeFormHandler.submitErrorURL" value="${pageContext.request.requestURI}"/>
  <TABLE width="600" border="0" cellspacing="0" cellpadding="0" align="center">
    <TR bgcolor="#FFDE63"> 
      <TD width="1"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="10"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="189"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="289"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="100"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="10"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="1"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
    </TR>
    <TR> 
      <TD width="1" bgcolor="#FFDE63"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="10" valign="MIDDLE" class="texte" align="CENTER"></TD>
      <TD width="189" valign="MIDDLE" class="texte" align="center" height="30">Magasin
        :&nbsp; 
      </TD>
	  <TD width="289" valign="MIDDLE" class="texte" align="center" height="30">
	  
	  	<dsp:select bean="RechercheCommandeFormHandler.magasinId" id="LesMagasins" >
		<dsp:droplet name="/atg/dynamo/droplet/RQLQueryForEach">
			<dsp:param name="queryRQL" value="ALL"/>
			<dsp:param name="repository" value="/atg/registry/Repository/magasinGSARepository"/>
			<dsp:param name="itemDescriptor" value="magasin"/>
			<dsp:param name="sortProperties" value="+Nom"/>
			<dsp:param name="elementName" value="magasinItem"/>
			<dsp:oparam name="output"> 
				<option value="<dsp:valueof param='magasinItem.id'/>">
					<dsp:valueof param="magasinItem.Nom" />
				</option>
			</dsp:oparam>	
		</dsp:droplet>
		</dsp:select>
		
	  </TD>
	  <TD width="100" valign="MIDDLE" class="texte" align="center" height="30">
	  		<TABLE width="100" border="0" cellspacing="0" cellpadding="0">
        	<TR align="center">
        	<TD>
        	  <dsp:input type="image" src="../../img/valider.gif" border="0"  bean="RechercheCommandeFormHandler.rechercheParMagasin"/>
			  </TD>
      		</TR>
		  </TABLE> 
      </TD>
      <TD width="10" valign="MIDDLE" class="texte" align="CENTER">&nbsp;</TD>
      <TD width="1" align="CENTER" bgcolor="#FFDE63"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
    </TR>
	
    <TR bgcolor="#FFDE63"> 
      <TD width="1"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="10"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="189"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="289"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="100"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="10"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
      <TD width="1"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
    </TR>
  </TABLE>
  <BR>
</dsp:form AP--> 
<%--
----------------- Fin de Projet Castorama - La D�fense -------------------
--%>


 		</TABLE>

<!-- ************************ erreur sur la recherche des commmandes ************************ -->  
   <dsp:droplet name="/atg/dynamo/droplet/Switch">
   <dsp:param name="value" bean="/atg/commerce/order/FindOrdersFormHandler.formError"/>
   <dsp:oparam name="true">
	<dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
	<dsp:param name="exceptions" bean="/atg/commerce/order/FindOrdersFormHandler.formExceptions"/>
	<dsp:oparam name="outputStart">
	<TABLE border=0 cellpadding="10" width="600" align="center">
	<TR><TD align="center">
	</dsp:oparam>
	<dsp:oparam name="outputEnd">
	</TD></TR></TABLE>
	</dsp:oparam>
	<dsp:oparam name="output">
		<%--<dsp:valueof param="message"/>--%>
		<dsp:droplet name="Switch">
		<dsp:param name="value" param="message"/>
		<dsp:oparam name="No order id Specified">
			<SPAN class="prix"><IMG src="${pageContext.request.contextPath}/html/img/flecheb_retrait.gif" border=0>&nbsp;<U>Le champs "N° Commande" est incorrect !!</U></SPAN>
		</dsp:oparam>
		<dsp:oparam name="Could not find the User Profile"> 
			<SPAN class="prix"><IMG src="${pageContext.request.contextPath}/html/img/flecheb_retrait.gif" border=0>&nbsp;<U>Le champs "login" est incorrect !!</U></SPAN>
		</dsp:oparam>		
		</dsp:droplet>
	</dsp:oparam>
	</dsp:droplet>
   </dsp:oparam>
</dsp:droplet>
<!-- ************************ fin erreur sur la recherche des commmandes ************************ -->
<!-- ************************ erreur sur la recherche des commmandes par profileId ************************ -->  
  <dsp:droplet name="ErrorMessageForEach">
 <dsp:param name="exceptions" bean="RechercheCommandeFormHandler.formExceptions"/>
<dsp:oparam name="outputStart">
	<TABLE border=0 cellpadding="10" width="600" align="center">
	<TR><TD align="center">
	</dsp:oparam>
<dsp:oparam name="outputEnd">
	</TD></TR></TABLE>
</dsp:oparam>
 <dsp:oparam name="output">
 	<dsp:droplet name="Switch">
 	<dsp:param name="value" param="message"/>
	<dsp:oparam name="ProfileIdMissing">
		<SPAN class="prix"><IMG src="${pageContext.request.contextPath}/html/img/flecheb_retrait.gif" border=0>&nbsp;<U>Le champ "Num&eacute;ro Client" est incorrect </U></SPAN>
	</dsp:oparam>
	</dsp:droplet>
  </dsp:oparam>
</dsp:droplet>
<!-- ************************ fin erreur sur la recherche des commmandes par profileId ************************ -->
<!-- ************************ erreur sur la recherche des commmandes par date ************************ -->  
<dsp:droplet name="ErrorMessageForEach">
 <dsp:param name="exceptions" bean="RechercheCommandeParDateFormHandler.formExceptions"/>
<dsp:oparam name="outputStart">
	<TABLE border=0 cellpadding="10" width="600" align="center">
	<TR><TD align="center">
	</dsp:oparam>
<dsp:oparam name="outputEnd">
	</TD></TR></TABLE>
</dsp:oparam>
 <dsp:oparam name="output">
 	<dsp:droplet name="Switch">
 	<dsp:param name="value" param="message"/>
	<dsp:oparam name="DateDebMissing">
		<SPAN class="prix"><IMG src="${pageContext.request.contextPath}/html/img/flecheb_retrait.gif" border=0>&nbsp;<U>Le champ "Date" est incorrect </U></SPAN>
	</dsp:oparam>
	</dsp:droplet>
  </dsp:oparam>
</dsp:droplet>
<!-- ************************ fin erreur sur la recherche des commmandes par date ************************ -->

</dsp:oparam>
<dsp:oparam name="accesRefuse"></dsp:oparam>
</dsp:droplet>

<dsp:include page="../../common/menuBas.jsp"/>

</dsp:page>