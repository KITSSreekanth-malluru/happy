<dsp:page>
<%-- 
------------------------------------------------------------------------------------------ 
imports-----------------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>
<dsp:importbean bean="/castorama/profile/CastoGetPrivileges"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>


<%-- 
------------------------------------------------------------------------------------------ 
Sécurisation HTTPS------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>
<dsp:include page="../common/protocolChange.jsp"><dsp:param name="protocol" value="secure"/></dsp:include>


<TABLE>
	<TR>
		<TD class="marques">
			COMMANDES :
		</TD>
	</TR>
	<TR>
		<TD class="texteg">
			<dsp:droplet name="CastoGetPrivileges">
			    <dsp:param name="profile" bean="Profile"/>
			   	<dsp:param name="requis" value="commerce-csr-orders-privilege,informatique-privilege"/>	
			   		   		
		   		<dsp:oparam name="accesAutorise">
          		  <IMG src="${pageContext.request.contextPath}/html/img/flecheb_retrait.gif" border=0>&nbsp;<A class=moncasto href="${pageContext.request.contextPath}/html/call_center/order/search/find_orders_page.jsp">Rechercher une commande</A> 
		          <dsp:droplet name="/atg/dynamo/droplet/IsNull">
		           <dsp:param name="value" param="user"/>
		           <dsp:oparam name="false"><BR>
		             <IMG src="${pageContext.request.contextPath}/html/img/flecheb_retrait.gif" border=0>&nbsp;
		             <A class=moncasto href="${pageContext.request.contextPath}/html/call_center/order/search/orders_by_user.jsp?login=<dsp:valueof param="user"/>">              
		               Rechercher les commandes de <dsp:valueof param="user"><dsp:valueof param="id"></dsp:valueof></dsp:valueof>
		             </A></NOBR>
		           </dsp:oparam>
		           <dsp:oparam name="true"></dsp:oparam>
		          </dsp:droplet>
       		    </dsp:oparam>
       			<dsp:oparam name="accesRefuse"></dsp:oparam>
			</dsp:droplet>
			
			<%-- 
            		STABILISATION : historique modif commande : seuelement pour le groupe admin_habilitation_utilisateur
            --%>
            <br/>
            <dsp:droplet name="CastoGetPrivileges">
		    	<dsp:param name="profile" bean="Profile"/>
		   		<dsp:param name="requis" value="user_manager_privilege"/>	
		   		   		
	   			<dsp:oparam name="accesAutorise">
         		 	 <IMG src="${pageContext.request.contextPath}/html/img/flecheb_retrait.gif" border=0>&nbsp;<A class=moncasto href="${pageContext.request.contextPath}/html/call_center/order/show/historique-commande.jsp">Historique des actions commande</A> 
      			</dsp:oparam>
      			<dsp:oparam name="accesRefuse"></dsp:oparam>
			</dsp:droplet>
			
			<%-- 
            		0001294: Nouveau rapport dans call center 
            		Logica
            		Semaine 43
            --%>
            <br/>
			<dsp:droplet name="CastoGetPrivileges">
		    	<dsp:param name="profile" bean="Profile"/>
		   		<dsp:param name="requis" value="commerce-csr-orders-privilege,informatique-privilege, user_manager_privilege"/>	
	   			<dsp:oparam name="accesAutorise">
         		 	 <IMG src="${pageContext.request.contextPath}/html/img/flecheb_retrait.gif" border=0>&nbsp;<A class=moncasto href="${pageContext.request.contextPath}/html/call_center/order/show/suivi-commande.jsp">Suivi des commandes</A> 
      			</dsp:oparam>
      			<dsp:oparam name="accesRefuse"></dsp:oparam>
			</dsp:droplet>
			<%-- 
            		0001294: Nouveau rapport dans call center 
            		Logica
            		Semaine 43
            --%>
            			
		</TD>
	</TR>
</TABLE>

</dsp:page>