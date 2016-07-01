<dsp:page xml="true">


<%-- 
------------------------------------------------------------------------------------------ 
IMPORTS-----------------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>
<dsp:importbean bean="/atg/dynamo/droplet/TableForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/commerce/order/FindOrdersFormHandler"/>
<dsp:importbean bean="/atg/dynamo/droplet/URLArgument"/>
<TABLE align=center width=600 border=0 cellpadding=0 cellspacing=0>
	<TR>
		<TD><SPAN align=right><IMG src="${pageContext.request.contextPath}/html/img/flecheb_retrait.gif" border=0>&nbsp;<A class=moncasto href="javascript:history.back();">retour</A></SPAN></TD>
	</TR>
	</TABLE>
<TABLE cellpadding="0"  cellspacing="0" border=1 bordercolor="#CC0033" width=300 align=center>
	<TR>
		<TD align=center>
			<SPAN class=prix>Cliquer sur le num&eacute;ro de la commande.</SPAN>
		</TD>
	</TR>
</TABLE><BR>


<%-- Fiche Mantis 1256 : Liste les commandes triees par ordre de submitted date decroissante - 18/09/2008 --%>
<dsp:droplet name="/castorama/order/CastoListeCommandesParDate">
    <dsp:param name="listeOrderIds" bean="/atg/commerce/order/FindOrdersFormHandler.orderIds" />
    <dsp:oparam name="output">
    	<dsp:droplet name="/atg/dynamo/droplet/ForEach">
    		<dsp:param name="array" param="lesCommandesTriees" />
    		<dsp:param name="elementName" value="commande"/>
    		<dsp:oparam name="outputStart">
		    	<TABLE align="center" border="0" width="600" cellspacing ="0" cellpadding="0">
					<TR bgcolor="#FFDF63">
					 <TD width=1><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
				       <TD width=598><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
					   <TD width=1><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
					   </TR>
					   <TR>
					   <TD width=1 bgcolor="#FFDF63"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
				       <TD align=center width=598>
				    <TABLE align=center border=0 width=598 cellspacing =0 cellpadding=0>
				     <TR  align="center">
				       <TD width=18>&nbsp;</TD>
				       <TD class="texte" align=center width=80>Commande</TD>
				       <TD class="texte" align=center width=160>Internaute</TD>
				       <TD class="texte" align=center width=80>Date</TD>
				       <TD class="texte" align=center width=60>Total TTC</TD>
				       <TD class="texte" align=center width=200>Etat</TD>
				    </TR>
					</TABLE>
					</TD>
					<TD width=1 bgcolor="#FFDF63"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
					</TR>
					   <TR bgcolor="#FFDF63">
					   <TD width=1><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
				       <TD width=598><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
					   <TD width=1><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
					</TR>
				</TABLE>
				<TABLE align=center border=0 width=600>
		    </dsp:oparam><%-- Fin ForEach - outputStart --%>
 			<dsp:oparam name="outputEnd">
				</TABLE>
				<TABLE width="600" border="0" cellspacing="0" cellpadding="0" align="center">
					<TR>
						<TD align=center colspan=8>
							<dsp:droplet name="/atg/dynamo/droplet/Switch">
								<dsp:param name="value" param="hasPrev"/>
								<dsp:oparam name="true">
									<A class="moncasto" href="./find_orders_results.jsp">
										<dsp:param name="start" param="prevStart"/>
										Pr&eacute;c&eacute;dent&nbsp;<dsp:valueof param="prevHowMany"/>
									</A>&nbsp;&nbsp;&nbsp;&nbsp;
								</dsp:oparam>
							</dsp:droplet>
							<dsp:droplet name="/atg/dynamo/droplet/Switch">
								<dsp:param name="value" param="hasNext"/>
								<dsp:oparam name="true">
									<A class="moncasto" href="./find_orders_results.jsp">
										<dsp:param name="start" param="nextStart"/>
										Suivant&nbsp;<dsp:valueof param="nextHowMany"/>
									</A>
								</dsp:oparam>
							</dsp:droplet>
						</TD>
					</TR>
				</TABLE>	
		 	</dsp:oparam><%-- Fin ForEach - outputEnd --%>
	 		<dsp:oparam name="outputRowStart">
		    	<TR>
			</dsp:oparam>
			<dsp:oparam name="outputRowEnd">
			    </TR>
			</dsp:oparam>
   			<dsp:oparam name="output">
    			<%--<dsp:valueof param="commande.id" /> : <dsp:valueof param="commande.submittedDate" /> <br/>--%>
    			<TR valign=top>
        			<TD align=right class="texteb" width=18><dsp:valueof param="count"/></SPAN></TD>
	            	<TD align=center width=80>
        <dsp:getvalueof var="orderId" param="commande.id"/>
  <c:set var="numOrder" value="${fn:substring(orderId, 1, -1)}"/>
  <c:choose>
    <c:when test="${numOrder < 400000000}">
		               	<A class="moncasto" href="${pageContext.request.contextPath}/html/call_center/order/show/order_migr.jsp?id=<dsp:valueof param="commande.id"/>">
	                   		<dsp:valueof param="commande.id"/>
	                    </A>
    </c:when>
    <c:otherwise> 
		               	<A class="moncasto" href="${pageContext.request.contextPath}/html/call_center/order/show/order.jsp?id=<dsp:valueof param="commande.id"/>">
	                   		<dsp:valueof param="commande.id"/>
	                    </A>
     </c:otherwise> 
  </c:choose>
                
	                </TD>
			    	<TD align=center width=160>
						<dsp:droplet name="/atg/targeting/RepositoryLookup">
							<dsp:param name="repository" bean="/atg/userprofiling/ProfileAdapterRepository" />
							<dsp:param name="itemDescriptor" value="user" />
							<dsp:param name="id" param="commande.profileId"/>
							<dsp:param name="elementName" value="profile"/>							
							<dsp:oparam name="output">  		                 
					            <dsp:droplet name="/atg/dynamo/droplet/IsNull">
						            <dsp:param name="value" param="profile"/>
						            <dsp:oparam name="true">
							            <I>Pas de compte internaute <U><dsp:valueof param="profile.repositoryId">null</dsp:valueof></U></I>
							        </dsp:oparam>
							        <dsp:oparam name="false">
										<dsp:droplet name="/atg/dynamo/droplet/IsNull">
											<dsp:param name="value" param="profile.login"/>
											<dsp:oparam name="true">Internaute non enregistr&eacute; !</dsp:oparam>
											<dsp:oparam name="false">
											    <A class="moncasto" href="${pageContext.request.contextPath}/html/call_center/user/editprofilePage.jsp?id=<dsp:valueof param="profile.repositoryId"/>">
											        <dsp:valueof param="profile.firstName"><I>___</I></dsp:valueof>
											           <dsp:valueof param="profile.lastName"><I>---</I></dsp:valueof>
											           (<dsp:valueof param="profile.login"></dsp:valueof>)
											       </A>
											</dsp:oparam>
										</dsp:droplet>
							        </dsp:oparam>
							    </dsp:droplet>
						    </dsp:oparam>
					    </dsp:droplet>
					</TD>
					<TD class="moncasto" align="center" width="80">
					    <dsp:tomap var="beanOrder" param="commande"/>
                        <c:choose> 
                          <c:when test="${!empty beanOrder.submittedDate}" > 
    				        <fmt:formatDate  value="${beanOrder.submittedDate}" pattern="dd/M/yyyy HH:mm:ss"></fmt:formatDate>
                          </c:when> 
                          <c:otherwise>
                          <I>non d&eacute;finie</I> 
                          </c:otherwise> 
                        </c:choose>  
				   </TD>
				   <TD class="moncasto" align="right" width="60">
				   		<%-- <dsp:getvalueof var="subTotal" param="commande.priceInfo.rawSubtotal" scope="request"/>
				   		<dsp:getvalueof var="montantPFT" param="commande.processingFees" scope="request"/>
				   		<dsp:getvalueof var="montantPFL" param="commande.shippingFees" scope="request"/>
				   		<c:set var="total" value="${subTotal + montantPFT + montantPFL}"/> --%>
                        <dsp:getvalueof var="montantTotal" param="commande.montantTotalCommandeTTC" scope="request"/>
				   		<dsp:include page="../../common/frag_prix.jsp">
				        	<dsp:param name="prix" value="${montantTotal}" />
				            <dsp:param name="formatEntreePrix" value="E"/>
				            <dsp:param name="formatSortiePrix" value="E"/>
				        </dsp:include>              
					</TD>
					<TD class="moncasto" align="center" width="200">
						<dsp:include page="../../common/fragmentEtat.jsp">
							<dsp:param name="state" param="commande.BOState"/>
						</dsp:include>
					</TD>
				</TR>
    	    </dsp:oparam>
		</dsp:droplet>
    </dsp:oparam>
</dsp:droplet><%-- Fiche Mantis 1256 : Fin du nouveau dev sur la liste les commandes triee par ordre de submitted date decroissante - 18/09/2008 --%>



<%-- Fiche Mantis 1256 : Mise en commentaire - 18/09/2008 --%>
<%--
<dsp:droplet name="/atg/dynamo/droplet/TableRange">
  	<dsp:param name="array" bean="/atg/commerce/order/FindOrdersFormHandler.orderIds"/>
   	<dsp:param name="numColumns" value="1"/>
   	<dsp:param name="howMany" value="25"/>
   	<dsp:param name="elementName" value="laCommande"/>
  	<dsp:oparam name="outputStart">
		  <TABLE align="center" border="0" width="600" cellspacing ="0" cellpadding="0">
		     <TR bgcolor="#FFDF63">
			 <TD width=1><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
		       <TD width=598><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
			   <TD width=1><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
			   </TR>
			   <TR>
			   <TD width=1 bgcolor="#FFDF63"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
		       <TD align=center width=598>
		    <TABLE align=center border=0 width=598 cellspacing =0 cellpadding=0>
		     <TR  align="center">
		       <TD width=18>&nbsp;</TD>
		       <TD class="texte" align=center width=80>Commande</TD>
		       <TD class="texte" align=center width=160>Internaute</TD>
		       <TD class="texte" align=center width=80>Date</TD>
		       <TD class="texte" align=center width=60>Total TTC</TD>
		       <TD class="texte" align=center width=200>Etat</TD>
		    </TR>
			</TABLE>
			</TD>
			<TD width=1 bgcolor="#FFDF63"><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
			   </TR>
			   <TR bgcolor="#FFDF63">
			   <TD width=1><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
		       <TD width=598><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
			   <TD width=1><IMG src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></TD>
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
					<A class="moncasto" href="./find_orders_results.jsp">
						<dsp:param name="start" param="prevStart"/>
						Pr&eacute;c&eacute;dent&nbsp;<dsp:valueof param="prevHowMany"/>
					</A>&nbsp;&nbsp;&nbsp;&nbsp;
				</dsp:oparam>
			</dsp:droplet>
			<dsp:droplet name="/atg/dynamo/droplet/Switch">
				<dsp:param name="value" param="hasNext"/>
				<dsp:oparam name="true">
					<A class="moncasto" href="./find_orders_results.jsp">
						<dsp:param name="start" param="nextStart"/>
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
	<dsp:tomap var="laCommande" bean="FindOrdersFormHandler.orderPourAffichage"/>    
	<dsp:tomap var="laCommandePriceInfo" bean="FindOrdersFormHandler.orderPourAffichage.priceInfo"/>    
    	  <dsp:droplet name="FindOrdersFormHandler">
          	<dsp:param name="orderId" param="laCommande"/>
          	<dsp:param name="searchType" value="byOrderId"/>
	    	  <dsp:oparam name="output">
	    		<TR valign=top>
                <TD align=right class="texteb" width=18><dsp:valueof param="count"/></SPAN></TD>
                	<TD align=center width=80>
	                	<A class="moncasto" href="${pageContext.request.contextPath}/html/call_center/order/show/order.jsp?id=<dsp:valueof bean="FindOrdersFormHandler.orderPourAffichage.id"/>">
	                    	<dsp:valueof bean="FindOrdersFormHandler.order.id"/>
	                    </A></TD>
		    	<TD align=center width=160>

                 <dsp:droplet name="/atg/targeting/RepositoryLookup">
					<dsp:param name="repository" bean="/atg/userprofiling/ProfileAdapterRepository" />
					<dsp:param name="itemDescriptor" value="user" />
					<dsp:param name="id" bean="FindOrdersFormHandler.order.profileId"/>
					<dsp:param name="elementName" value="profile"/>							
						<dsp:oparam name="output">  		                 
		                     <dsp:droplet name="/atg/dynamo/droplet/IsNull">
		                      <dsp:param name="value" param="profile"/>
		                       <dsp:oparam name="true">
		                         <I>Pas de compte internaute <U><dsp:valueof param="profile.repositoryId">null</dsp:valueof></U></I>
		                       </dsp:oparam>
		                       <dsp:oparam name="false">
									<dsp:droplet name="/atg/dynamo/droplet/IsNull">
									  <dsp:param name="value" param="profile.login"/>
									    <dsp:oparam name="true">Internaute non enregistr&eacute; !</dsp:oparam>
									     <dsp:oparam name="false">
						                          <A class="moncasto" href="${pageContext.request.contextPath}/html/call_center/user/editprofilePage.jsp?id=<dsp:valueof param="profile.repositoryId"/>">
						                          <dsp:valueof param="profile.firstName"><I>___</I></dsp:valueof>
						                          <dsp:valueof param="profile.lastName"><I>---</I></dsp:valueof>
						                          (<dsp:valueof param="profile.login"></dsp:valueof>)
						                         </A>
										</dsp:oparam>
							        </dsp:droplet>
		                      	</dsp:oparam>
		                	  </dsp:droplet>
	                    </dsp:oparam>
                   </dsp:droplet>
               </TD>
                   <TD class="moncasto" align="center" width="80">
                   		
                   			<dsp:tomap var="beanOrder" bean="FindOrdersFormHandler.order"/>
                   			<fmt:formatDate  value="${beanOrder.submittedDate}" pattern="dd/M/yyyy HH:mm:ss"><I>non d&eacute;finie</I></fmt:formatDate>
                   		
                   		
                   </TD>
                   <TD class="moncasto" align="right" width="60">
                   <dsp:include page="../../common/frag_prix.jsp">
                   		<dsp:param name="prix" bean="FindOrdersFormHandler.order.priceInfo.total"/>
                   		<dsp:param name="formatEntreePrix" value="E"/>
                   		<dsp:param name="formatSortiePrix" value="E"/>
                   </dsp:include>
		</TD>
		 <TD class="moncasto" align="center" width="200">
				 
		<dsp:include page="../../common/fragmentEtat.jsp"><dsp:param name="state" bean="FindOrdersFormHandler.order.repositoryItem.BOState"/></dsp:include>
		</TD>
		</TR>		     	      	   
		</dsp:oparam>
		  <dsp:oparam name="empty"><SPAN class="prix"></SPAN>Aucun r&eacute;sultat</SPAN><P></dsp:oparam>
		 </dsp:droplet>
	</dsp:oparam>
</dsp:droplet>--%>
<%-- Fin Mise en Commentaire - Fiche Mantis 1256 - 18/09/2008 --%>
<%-- fin table range --%>

</dsp:page>
