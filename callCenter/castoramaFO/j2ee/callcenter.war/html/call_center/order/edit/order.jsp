
<dsp:page xml="true">


	<%-- 
------------------------------------------------------------------------------------------ 
IMPORTS-----------------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>

<dsp:importbean bean="/castorama/BeanSessionOrder"/>
<dsp:importbean bean="/castorama/CastoOrderEditor"/>
<dsp:importbean bean="/atg/commerce/order/FindOrdersFormHandler"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/castorama/BeanSetDateOrder"/>
<dsp:importbean bean="/castorama/profile/CastoGetPrivileges"/>
<dsp:importbean bean="/castorama/droplet/commande/CastoIsCommandeMagasin"/>




	<%-- 
------------------------------------------------------------------------------------------ 
SÃ©curisation HTTPS------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>
	<dsp:include page="../../common/protocolChange.jsp"> <dsp:param name="protocol" value="secure" /> </dsp:include>



<html>
<head>
<title>Castorama : Call center - Gestion Commande</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../../css/hp.css">

</head>
<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<div class="gray_overlay"></div>
	<div id="whiteContainer">
	<div class="m10">

<script language="JavaScript">
var taux = 6.55957 ;
function updateEuros(){
	francs = window.document.formCheque.francs ;
	euros = window.document.formCheque.euros ;
	replace(francs);
	euros.value=francs.value/taux;
	format(euros,2);
	replace(euros);
}

function updateFrancs(){
	francs = window.document.formCheque.francs ;
	euros = window.document.formCheque.euros ;
	replace(euros);
	francs.value=euros.value*taux;
	format(francs,2);
	replace(francs);
}

function format(decimal,nbdec){
	nbdec=nbdec+1;
	var i=0;
	
	while (i<eval(decimal.value.length))
	{
	if (decimal.value.charAt(i)=="," || decimal.value.charAt(i)==".")
	{
		valtp = decimal.value.substring(i+1,i+nbdec);
		if (decimal.value.charAt(i+nbdec)>=5)
		{
			valtp = eval(valtp) +1;
			if (valtp<10)
			{
				valtp="0"+valtp;
			}
		}
		decimal.value=decimal.value.substring(0,i)+","+ valtp;
			if (decimal.value.substring(0,i)!="")
			{
				if (valtp==100)
				{
					decimal.value=eval(decimal.value.substring(0,i)) +1 +",00";
				}
			}
			else
			{
				if (valtp==100)
				{
					decimal.value="1,00";
				}
				else
				{
					decimal.value="0,"+ valtp;
				}
			}
	}
	i++;
	}
}

function replace(decimal){
	var i=0;
	while (i<eval(decimal.value.length))
	{
	if (decimal.value.charAt(i)==",")
	{
		decimal.value=decimal.value.substring(0,i)+"."+decimal.value.substring(i+1,decimal.value.length);
	}
	i++;
	}
}

</script>

<dsp:tomap var="repositoryItem" bean="CastoOrderEditor.order.repositoryItem"/>	

		<dsp:droplet name="/atg/dynamo/droplet/IsNull">
			<dsp:param name="value" bean="CastoOrderEditor.order"/>
			<dsp:oparam name="true"><span class="prix">La commande n'est pas charg&eacute;e !</span></dsp:oparam>
			<dsp:oparam name="false">
				<dsp:setvalue param="order" beanvalue="CastoOrderEditor.order"/>			
				<dsp:droplet name="/atg/targeting/RepositoryLookup">
					<dsp:param name="repository" bean="/atg/userprofiling/ProfileAdapterRepository" />
					<dsp:param name="itemDescriptor" value="user" />
					<dsp:param name="id" param="order.profileId" />
					<dsp:param name="elementName" value="profile" />
					<dsp:oparam name="output">
					<dsp:droplet name="/atg/dynamo/droplet/IsNull">
							<dsp:param name="value" value="item"/>
							<dsp:oparam name="true">
								<dsp:include page="../../common/header.jsp"/>
							</dsp:oparam>
							<dsp:oparam name="false">
								<dsp:include page="../../common/header.jsp">
									<dsp:param name="id" param="order.profileId"/>
									<dsp:param name="user" param="profile.login"/>
								</dsp:include>
      				</dsp:oparam>
  					</dsp:droplet> <!--- /Switch --->
					</dsp:oparam>
				</dsp:droplet> 
         	 <!---- START ---->
				<table width=600 align=CENTER border=0 cellpadding=0 cellspacing=0>
					<tr><td width=600 align=CENTER><span class=TEXTE align=CENTER>
					
					
					<dsp:include page="./order_validation_errors.jsp"/></span>
					
					
					</td></tr>
				</table>
				<table align=CENTER width=600 border=0 cellpadding=0 cellspacing=0>
				<tr><td><span align=RIGHT><img src="<%=request.getContextPath()%>/html/img/flecheb_retrait.gif" border=0>&nbsp;<a class=MONCASTO href="javascript:history.back();">retour</a></span></td></tr>
				</table>
				<table align=CENTER border=0 width=600 cellspacing =0 cellpadding=0>
			     <tr>
				 <td align=CENTER width=600><span class=MARQUES>Gestion de la commande :</span>
			     <p></td>
				 </tr>
				</table>
	<table align=CENTER border=0 width=300 cellspacing =0 cellpadding=0>
	<tr bgcolor="#FFDF63">
		<td width=1><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
		<td width=290><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
		<td width=8><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
		<td width=1><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
	</tr>
	<tr>
		<td width=1 bgcolor="#FFDF63"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
		<td width=8><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
		<td align=CENTER width=290>
			<table align=LEFT border=0 width=290 cellspacing =1 cellpadding=1>
			<tr  align="center">
				<td class="texte" align=LEFT width=290>Commande num&eacute;ro <b><dsp:valueof param="order.id"><i>none</i></dsp:valueof></b></td>
			</tr>
			</table>
		</td>
		<td width=1 bgcolor="#FFDF63"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
	</tr>
	<tr bgcolor="#FFDF63">
		<td width=1><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
		<td width=290><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
		<td width=8><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
		<td width=1><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
	</tr>
	</table><br>
	<dsp:include page="frag_displayAdresses.jsp"/><br>
	<table align=CENTER border=0 width=300 cellspacing =0 cellpadding=0>
	<tr bgcolor="#FFDF63">
		<td width=1><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
		<td width=290><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
		<td width=8><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
		<td width=1><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
	</tr>
	<tr>
		<td width=1 bgcolor="#FFDF63"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
		<td width=8><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
		<td align=CENTER width=290>
			<table align=LEFT border=0 width=290 cellspacing =1 cellpadding=1>
			<tr align="center"> 
				<td class="texte" align=LEFT width=290><b>Etat : </b>
			<dsp:setvalue param="etat" paramvalue="order.repositoryItem.BOState"/>
			<dsp:include page="../../common/fragmentEtat.jsp">
				<dsp:param name="state" param="etat"/>
				<dsp:param name="stateDetail" param="order.repositoryItem.BOStateDetail"/>
			</dsp:include>
				</td>
			</tr>
			<tr align="center"> 
				<td class="texte" align=LEFT width=290><b>M&eacute;thode de paiement : </b>
				<dsp:droplet name="Switch">
					<dsp:param name="value" param="order.paymentGroups[0].paymentMethod"/>
					<dsp:oparam name="creditCard">
						Carte bancaire (<dsp:include page="../show/fragTypeCarte.jsp"/>)
					</dsp:oparam>
					<dsp:oparam name="Atout">Carte l'Atout</dsp:oparam>
					<dsp:oparam name="Cheque">Ch&egrave;que</dsp:oparam>
					<dsp:oparam name="Virement">Virement</dsp:oparam>
					<dsp:oparam name="Cadeau">Carte Cadeau</dsp:oparam>
					<dsp:oparam name="Call-Center">Call-Center (<dsp:include page="../show/fragTypeCarte.jsp"/>)</dsp:oparam>
					<dsp:oparam name="default"><dsp:valueof param="order.paymentGroups[0].paymentMethod"><i>none</i></dsp:valueof></dsp:oparam>
				</dsp:droplet>
				</td>
			</tr>
			<tr align="center">
				<td class="texte" align=LEFT width=290><b>Cre&eacute; le : </b><dsp:valueof param="order.creationDate" date="dd/M/yyyy HH:mm:ss"><i>none</i></dsp:valueof></td>
			</tr>
			<tr align="center">
				<td class="texte" align=LEFT width=290><b>Soumise le : </b><dsp:valueof param="order.submittedDate" date="dd/M/yyyy HH:mm:ss"><i>none</i></dsp:valueof></td>
			</tr>
			<tr align="center">
				<td class="texte" align=LEFT width=290><b>Modifi&eacute;e le : </b><dsp:valueof param="order.lastModifiedDate" date="dd/M/yyyy HH:mm:ss"><i>none</i></dsp:valueof></td>
			</tr>
			</table>
		</td>
		<td width=1 bgcolor="#FFDF63"><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
	</tr>
	<tr bgcolor="#FFDF63">
		<td width=1><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
		<td width=290><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
		<td width=8><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
		<td width=1><img src="<%=request.getContextPath()%>/html/img/1pixel.gif" width="1" height="1"></td>
	</tr>
	</table>
	<br>
         		<!---- RENDER THE ITEM & SHIPPING INFORMATION ----->
				<table width=600 align=CENTER border=0 cellpadding=0 cellspacing=0>
					<tr>
					<td width=600 align=CENTER>
					<dsp:droplet name="ForEach">
						<dsp:param name="array" param="order.shippingGroups"/>
						<dsp:param name="elementName" value="sGroup"/>
						<dsp:oparam name="outputStart">
							<table cellspacing=0 cellpadding=0 border=0 align=CENTER>
						</dsp:oparam>
						<dsp:oparam name="outputEnd"> </table> </dsp:oparam>
						<dsp:oparam name="output">
							<tr valign=TOP>
								<td width=600 align=CENTER>
								<dsp:include page="../show/items_per_group.jsp">
									<dsp:param name="group" param="sGroup"/>
									<dsp:param name="empty_msg" value="None"/>
								</dsp:include>
								</td>
							</tr>
							<tr><td align=RIGHT>
					<!-- reseignement -->
	<table width="600" border="0" cellspacing="1" cellpadding="0" align=CENTER>
              <tr valign="MIDDLE" align="RIGHT"> 
                <td width="300" class="moncasto">&nbsp;</td>
                <td width="150" align="right" class="texteb" bgcolor="#E7E6E4">Sous-total TTC :</td>
                <td width="100" class="prixgris2">
                	<dsp:include page="../../common/frag_prix.jsp">
                		<dsp:param name="prix" param="order.priceInfo.rawSubTotal"/>
                		<%--<dsp:param name="prix" param="order.priceInfo.amount"/>--%>
                		<dsp:param name="formatEntreePrix" value="E"/>
                		<dsp:param name="formatSortiePrix" value="E"/>
                	</dsp:include></td>
              </tr>
            </table>
            
            <dsp:droplet name="/atg/targeting/RepositoryLookup">
				<dsp:param name="repository" bean="/atg/commerce/order/OrderRepository" />
				<dsp:param name="id" param="order.id" />
				<dsp:param name="itemDescriptor" value="order" />
				<dsp:param name="elementName" value="orderItem" />
				<dsp:oparam name="output">
				
				
					<%-- Projet Castorama - La Défense *** logica --%>
					<dsp:droplet name="CastoIsCommandeMagasin">
						<dsp:param name="order" param="order"/>
						<dsp:oparam name="false">
                <dsp:getvalueof var="totalDiscount" param="orderItem.totalDiscount"/>
  <c:if test="${not empty totalDiscount && totalDiscount != 0}">
            <table width="600" border="0" cellspacing="1" cellpadding="0">
              <tr valign="MIDDLE" align="RIGHT">
                <td width="300" class="moncasto">&nbsp;</td>
                <td width="150" class="texteb" bgcolor="#E7E6E4" align="right">Vous avez &eacute;conomis&eacute; :</td>
                <td width="100" class="prixgris2">
                  <dsp:include page="../../common/frag_prix.jsp">
                                        <dsp:param name="prix" param="orderItem.totalDiscount"/>
                    <dsp:param name="formatEntreePrix" value="E" />
                    <dsp:param name="formatSortiePrix" value="E" />
                  </dsp:include><br>
                </td>
              </tr>
            </table>
  </c:if>
							<table width="600" border="0" cellspacing="1" cellpadding="0">
				              <tr valign="MIDDLE" align="RIGHT"> 
				                <td width="300" class="moncasto">&nbsp;</td>
				                <td width="150" class="texteb" bgcolor="#E7E6E4" align="right">Frais de traitement TTC : </td>
				                <td width="100" class="prixgris2">
				          			<dsp:include page="../../common/frag_prix.jsp">
                    <dsp:param name="prix" param="orderItem.processingFees"/>
					                    <%-- AP<dsp:param name="prix" param="orderItem.montantPFT"/> --%>
					                    <dsp:param name="formatEntreePrix" value="E"/>
					                    <dsp:param name="formatSortiePrix" value="E"/>
				                  </dsp:include><br>
				                </td>
				              </tr>
				            </table> 
				        </dsp:oparam>
				    </dsp:droplet>        
		            
		            <dsp:droplet name="CastoIsCommandeMagasin">
		            	<dsp:param name="order" param="order"/>
		            	<dsp:oparam name="false">
				            <table width="600" border="0" cellspacing="1" cellpadding="0">
				              <tr valign="MIDDLE" align="RIGHT"> 
				                <td width="300" class="moncasto">&nbsp;</td>
				                <td width="150" class="texteb" bgcolor="#E7E6E4" align="right">Frais de livraison TTC : </td>
				                <td width="100" class="prixgris2">
				          				<dsp:include page="../../common/frag_prix.jsp">
				          					<%--<dsp:param name="prix" param="ShippingGroup.priceInfo.amount"/>--%>
				          					<%--<dsp:param name="prix" param="ShippingGroup.priceInfo.rawShipping"/>--%>
				          					<dsp:param name="prix" param="orderItem.shippingFees"/>
				          					<dsp:param name="formatEntreePrix" value="E"/>
				          					<dsp:param name="formatSortiePrix" value="E"/>
				          				</dsp:include>
				          				<%--</dsp:oparam>
				          				</dsp:droplet>--%><br>
				                </td>
				              </tr>
				            </table>
                <dsp:getvalueof var="shippingDiscount" param="orderItem.shippingDiscount"/>
  <c:if test="${not empty shippingDiscount && shippingDiscount != 0}">
            <table width="600" border="0" cellspacing="1" cellpadding="0">
              <tr valign="MIDDLE" align="RIGHT">
                <td width="300" class="moncasto">&nbsp;</td>
                <td width="150" class="texteb" bgcolor="#E7E6E4" align="right">Remise livraison :</td>
                <td width="100" class="prixgris2">
                  <dsp:include page="../../common/frag_prix.jsp">
                                        <dsp:param name="prix" param="orderItem.shippingDiscount"/>
                    <dsp:param name="formatEntreePrix" value="E" />
                    <dsp:param name="formatSortiePrix" value="E" />
                  </dsp:include><br>
                </td>
              </tr>
            </table>
  </c:if>
				        </dsp:oparam>
				        <dsp:oparam name="true">
				        	<dsp:droplet name="/atg/dynamo/droplet/ForEach">
								<dsp:param name="array" param="order.ShippingGroups" />
								<dsp:param name="elementName" value="ShippingGroup2" />
								<dsp:param name="indexName" value="shippingGroupIndex2" />
								<dsp:oparam name="output">
									<dsp:droplet name="Switch">
										<dsp:param name="value" param="ShippingGroup2.shippingMethod" />
										<dsp:oparam name="LAD">	
											<table width="600" border="0" cellspacing="1" cellpadding="0">
												<tr valign="MIDDLE" align="RIGHT">
													<td width="300" class="moncasto">&nbsp;</td>
													<td width="150" class="texteb" bgcolor="#E7E6E4" align="right">Frais
													de livraison TTC :</td>
													<td width="100" class="prixgris2">
														<dsp:include page="../../common/frag_prix.jsp">
															<%--<dsp:param name="prix" param="orderItem.montantPFL"/>--%>
															<%--<dsp:param name="prix" param="ShippingGroup2.priceInfo.rawShipping"/>--%>
															<dsp:param name="prix" param="ShippingGroup2.priceInfo.amount" />
															<dsp:param name="formatEntreePrix" value="E" />
															<dsp:param name="formatSortiePrix" value="E" />
														</dsp:include>
													<br>
													</td>
												</tr>
											</table>	
										</dsp:oparam>
										<dsp:oparam name="MAD">
											<table width="600" border="0" cellspacing="1" cellpadding="0">
												<tr valign="MIDDLE" align="RIGHT">
													<td width="300" class="moncasto">&nbsp;</td>
													<td width="150" class="texteb" bgcolor="#E7E6E4" align="right">Frais
													de mise &agrave; dispo TTC :</td>
													<td width="100" class="prixgris2">
														<dsp:include page="../../common/frag_prix.jsp">
															<%--<dsp:param name="prix" param="orderItem.montantPFL"/>--%>
															<%--<dsp:param name="prix" param="ShippingGroup2.priceInfo.rawShipping"/>--%>
															<dsp:param name="prix" param="ShippingGroup2.priceInfo.amount" />
															<dsp:param name="formatEntreePrix" value="E" />
															<dsp:param name="formatSortiePrix" value="E" />
														</dsp:include>
													<br>
													</td>
												</tr>
											</table>															
										</dsp:oparam>	
									</dsp:droplet>
								</dsp:oparam>	
							</dsp:droplet>	
							
							
				        </dsp:oparam>
				    </dsp:droplet>        
					<%-- Fin Projet Castorama - La Défense *** logica --%>
				
				</dsp:oparam>
			</dsp:droplet>
            
            
		<dsp:droplet name="/castorama/droplet/CalculPromo">
		<dsp:param name="order" bean="/castorama/CastoShoppingCartModifier.order"/>
		<dsp:param name="elementName" value="remise"/>
		<dsp:oparam name="OUTPUT">
		    <table width="600" border="0" cellspacing="1" cellpadding="0">
	              <tr valign="MIDDLE" align="RIGHT"> 
	                <td width="300" class="moncasto">&nbsp;</td>
	                <td width="150" class="texteb" bgcolor="#E7E6E4" align="right">Remise exceptionnelle : </td>
	                <td width="100" class="prix">
	                	<dsp:include page="../../common/frag_prix.jsp">
	                		<dsp:param name="prix" param="remise"/>
	                		<dsp:param name="formatEntreePrix" value="E"/>
	                		<dsp:param name="formatSortiePrix" value="E"/>
	                	</dsp:include>
						</td>
						</tr>
	            </table>				
		</dsp:oparam>
		</dsp:droplet>
				            
	    <table width="600" border="0" cellspacing="1" cellpadding="0">
              <tr valign="MIDDLE" align="RIGHT"> 
                <td width="300" class="moncasto">&nbsp;</td>
                <td width="150" class="texteb" bgcolor="#E7E6E4" align="right">Total commande TTC : </td>
                <td width="100" class="prix">
                	<dsp:include page="../../common/frag_prix.jsp">
                		<dsp:param name="prix" param="order.priceInfo.total"/>
                		<dsp:param name="formatEntreePrix" value="E"/>
                		<dsp:param name="formatSortiePrix" value="E"/>
                	</dsp:include>
					</td>
					</tr>
            </table>
	<!-- fin -->
			</td></tr>
			</table>
			
			<br>

		
			
<dsp:droplet name="CastoGetPrivileges">
    <dsp:param name="profile" bean="Profile"/>
   	<dsp:param name="requis" value="commerce-csr-orders-privilege"/>	
	<dsp:oparam name="accesAutorise">
		<table width="600" border="0" cellspacing="1" cellpadding="0" align=CENTER>
			<tr>			
				<dsp:droplet name="/castorama/inventory/CheckOrderAvecReservationStock">
					<dsp:param name="order" param="order"/>
					<dsp:oparam name="false">
						<%--<td align="CENTER" valign="top" width="300">

							<dsp:droplet name="castorama/order/CastoShippingGroupIsModifiable">
								<dsp:param name="id" bean="CastoOrderEditor.order.id"/>
								<dsp:oparam name="output">
									<a class="moncasto" href="./edit_commerce_items.jsp?id=<dsp:valueof param="sGroup.id"/>&group_type=shipping">
										<img src="<%=request.getContextPath()%>/html/img/modifier_commande.gif" border="0">
									</a>
								</dsp:oparam>								
							</dsp:droplet>
								
						</td>--%>
					<!---- DONE WITH SHIPPING INFORMATION ---->
						<td align=CENTER valign="top" width="600">
							<dsp:droplet name="castorama/order/CastoOrderIsCancelable">
									<dsp:param name="id" bean="CastoOrderEditor.order.id"/>
									<dsp:oparam name="output">
										<dsp:form action="./order.jsp" method="post">
											<dsp:input type="hidden" bean="CastoOrderEditor.forwardSuccessURL" value="./confirm_to_cancel.jsp"/>
											<dsp:input type="image" src="../../../img/supprimer_commande.gif" border="0" bean="CastoOrderEditor.forward" value="Supprimer la commande"/>
										</dsp:form>	
									</dsp:oparam>
								</dsp:droplet>
							</td>
				</tr>
					</dsp:oparam>
					<dsp:oparam name="true">
			<!---- DONE WITH SHIPPING INFORMATION ---->
					<td align="CENTER" valign="top" width="600">				
						<dsp:droplet name="castorama/order/CastoOrderIsCancelable">
									<dsp:param name="id" bean="CastoOrderEditor.order.id"/>
									<dsp:oparam name="output">
										<dsp:form action="./order.jsp" method="post">
											<dsp:input type="hidden" bean="CastoOrderEditor.forwardSuccessURL" value="./confirm_to_cancel.jsp"/>
											<dsp:input type="image" src="../../../img/supprimer_commande.gif" border="0" bean="CastoOrderEditor.forward" value="Supprimer la commande"/>
										</dsp:form>	
									</dsp:oparam>
								</dsp:droplet>
					</td>
				</tr>
					</dsp:oparam>
					</dsp:droplet> 
					</dsp:oparam>
				</dsp:droplet>
				</td>
				</tr>
			</table>
		</dsp:oparam>
		</dsp:droplet>

<dsp:droplet name="CastoGetPrivileges">
			    <dsp:param name="profile" bean="Profile"/>
			   	<dsp:param name="requis" value="commerce-csr-orders-privilege"/>	
			   		   		
		   		<dsp:oparam name="accesAutorise">	

									<dsp:droplet name="/atg/dynamo/droplet/ForEach">
										<dsp:param name="array" param="order.PaymentGroups"/>
										<dsp:param name="elementName" value="paymentGroup"/>
										<dsp:param name="indexName" value="paymentGroupIndex"/>
										<dsp:oparam name="output">
									
										<dsp:droplet name="/atg/dynamo/droplet/Switch">
											<dsp:param name="value" param="paymentGroup.paymentMethod"/>
											<dsp:oparam name="creditCard">

											</dsp:oparam>
									
											<dsp:oparam name="Call-Center">
												<dsp:droplet name="/atg/dynamo/droplet/Switch">
													<dsp:param name="value" param="order.repositoryItem.BOState"/>
													<dsp:oparam name="VALIDE"></dsp:oparam>
													<dsp:oparam name="EXPEDIEE"></dsp:oparam>
													<dsp:oparam name="TERMINEE"></dsp:oparam>
													<dsp:oparam name="default">
													<dsp:include page="./payment.jsp">
														<dsp:param name="amount" param="order.priceInfo.total"/>
														<dsp:param name="orderId" param="order.id"/>
														<dsp:param name="profileId" param="order.profileId"/>		
													</dsp:include>
													<%--	<dsp:include page="./fragSIPS.jsp">
															<dsp:param name="amount" param="order.priceInfo.total"/>
															<dsp:param name="orderId" param="order.id"/>
															<dsp:param name="profileId" param="order.profileId"/>		
															<dsp:param name="_requestid" param="_requestid"/>				
														</dsp:include>--%>
														<dsp:include page="./fragUpdateCheque.jsp"/>
													</dsp:oparam>
												</dsp:droplet>
											</dsp:oparam>
									
											<dsp:oparam name="Cheque">
												<dsp:droplet name="/atg/dynamo/droplet/Switch">
													<dsp:param name="value" param="order.repositoryItem.BOState"/>
													<dsp:oparam name="EXPEDIEE"></dsp:oparam>
													<dsp:oparam name="TERMINEE"></dsp:oparam>
													<dsp:oparam name="default">
													<dsp:include page="./payment.jsp">
														<dsp:param name="amount" param="order.priceInfo.total"/>
														<dsp:param name="orderId" param="order.id"/>
														<dsp:param name="profileId" param="order.profileId"/>	
													</dsp:include>
													<%-- 	<dsp:include page="./fragSIPS.jsp">
															<dsp:param name="amount" param="order.priceInfo.total"/>
															<dsp:param name="orderId" param="order.id"/>
															<dsp:param name="profileId" param="order.profileId"/>		
															<dsp:param name="_requestid" param="_requestid"/>				
														</dsp:include>--%>
														<dsp:include page="./fragUpdateCheque.jsp"/>
													</dsp:oparam>
												</dsp:droplet>
											</dsp:oparam>
									
											<dsp:oparam name="Atout">
												<dsp:droplet name="/atg/dynamo/droplet/Switch">
													<dsp:param name="value" param="order.repositoryItem.BOState"/>
													<dsp:oparam name="EXPEDIEE"></dsp:oparam>
													<dsp:oparam name="TERMINEE"></dsp:oparam>
													<dsp:oparam name="default">
													<dsp:include page="./payment.jsp">
														<dsp:param name="amount" param="order.priceInfo.total"/>
														<dsp:param name="orderId" param="order.id"/>
														<dsp:param name="profileId" param="order.profileId"/>		
													</dsp:include>
														<dsp:include page="./fragUpdateCheque.jsp"/>
													</dsp:oparam>
												</dsp:droplet>
											</dsp:oparam>
											<dsp:oparam name="Fax">
												<dsp:droplet name="/atg/dynamo/droplet/Switch">
													<dsp:param name="value" param="order.repositoryItem.BOState"/>
													<dsp:oparam name="VALIDE"></dsp:oparam>
													<dsp:oparam name="EXPEDIEE"></dsp:oparam>
													<dsp:oparam name="TERMINEE"></dsp:oparam>
													<dsp:oparam name="default">
													<dsp:include page="./payment.jsp">
														<dsp:param name="amount" param="order.priceInfo.total"/>
														<dsp:param name="orderId" param="order.id"/>
														<dsp:param name="profileId" param="order.profileId"/>		
													</dsp:include>
														<dsp:include page="./fragUpdateCheque.jsp"/>
													</dsp:oparam>
												</dsp:droplet>
											</dsp:oparam>
											<dsp:oparam name="Virement">
												<dsp:droplet name="/atg/dynamo/droplet/Switch">
													<dsp:param name="value" param="order.repositoryItem.BOState"/>
													<dsp:oparam name="VALIDE"></dsp:oparam>
													<dsp:oparam name="EXPEDIEE"></dsp:oparam>
													<dsp:oparam name="TERMINEE"></dsp:oparam>
													<dsp:oparam name="default">
													<dsp:include page="./payment.jsp">
														<dsp:param name="amount" param="order.priceInfo.total"/>
														<dsp:param name="orderId" param="order.id"/>
														<dsp:param name="profileId" param="order.profileId"/>		
													</dsp:include>
														<dsp:include page="./fragUpdateCheque.jsp"/>
													</dsp:oparam>
												</dsp:droplet>
											</dsp:oparam>
											<dsp:oparam name="default">
											</dsp:oparam>
									</dsp:droplet>

</dsp:oparam>
</dsp:droplet>

<dsp:droplet name="/atg/dynamo/droplet/Switch">
<dsp:param name="value" param="order.repositoryItem.BOState"/>
<dsp:oparam name="unset">
	<%--<dsp:include page="./frag_commitIncompleteOrders.jsp"/>--%>
</dsp:oparam>
<dsp:oparam name="default">
	<dsp:include page="./frag_validationCommande.jsp"/>
</dsp:oparam>
</dsp:droplet>
		</dsp:oparam>
		</dsp:droplet>  <!---- /Switch ---->


</dsp:oparam>
</dsp:droplet>


<dsp:droplet name="CastoGetPrivileges">
    <dsp:param name="profile" bean="Profile"/>
   	<dsp:param name="requis" value="informatique-privilege"/>	
	<dsp:oparam name="accesAutorise">

		<dsp:droplet name="/atg/dynamo/droplet/Switch">
		<dsp:param name="value" value="${repositoryItem.BOState}"/>
		<dsp:oparam name="unset">
			<%--<dsp:include page="./frag_commitIncompleteOrders.jsp"/>--%>
		</dsp:oparam>
		<dsp:oparam name="default">
			<dsp:include page="./frag_validationCommande.jsp"/>
		</dsp:oparam>
		</dsp:droplet>	
	</dsp:oparam>
</dsp:droplet>






<dsp:include page="../../common/menuBas.jsp"/>
</div>
</div>
</body>
</html>


</dsp:page>