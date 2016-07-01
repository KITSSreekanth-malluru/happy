<dsp:page xml="true">


	<%-- 
------------------------------------------------------------------------------------------ 
IMPORTS-----------------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>
	<dsp:importbean bean="/atg/commerce/order/FindOrdersFormHandler" />
	<dsp:importbean bean="/castorama/CastoOrderEditor" />
	<dsp:importbean bean="/atg/dynamo/droplet/Switch" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/castorama/droplet/Arborescence" />
	<dsp:importbean bean="/castorama/OrderFormHandler" />
	<dsp:importbean bean="/castorama/profile/CastoGetPrivileges"/>
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:importbean bean="/castorama/droplet/commande/CastoIsCommandeMagasin" />

	<%-- 
------------------------------------------------------------------------------------------ 
SÃ©curisation HTTPS------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>
	<dsp:include page="../../common/protocolChange.jsp"> <dsp:param name="protocol" value="secure" /> </dsp:include>


	<html>
	<head>
	<title>Castorama : Call center - Editer une commande</title>
	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	<link rel="stylesheet" href="../../../css/hp.css">
	</head>

	<body bgcolor="#FFFFFF" leftmargin="0" topmargin="0" marginwidth="0"
		marginheight="0">

<dsp:droplet name="CastoGetPrivileges">
	<dsp:param name="profile" bean="Profile"/>
   	<dsp:param name="requis" value="commerce-csr-orders-privilege,informatique-privilege"/>	
			   		   		
	<dsp:oparam name="accesAutorise">

	<dsp:droplet name="FindOrdersFormHandler">
		<dsp:param name="orderId" param="id" />
		<dsp:param name="searchType" value="byOrderId" />
		<dsp:oparam name="error">
			<span class=prix>Commande <dsp:valueof param="id">null</dsp:valueof>
			non trouv&eacute;e</span>
		</dsp:oparam>
		
		<dsp:oparam name="output">

			<%-- mapping JSTL de la commande trouvÃ©e --%>
			<dsp:tomap var="laCommande" bean="FindOrdersFormHandler.orderPourAffichage" />

			<dsp:droplet name="/atg/dynamo/droplet/IsNull">
				<dsp:param name="value"
					bean="FindOrdersFormHandler.orderPourAffichage" />
				<dsp:oparam name="true">
					<span class=prix>Commande non trouv&eacute;e !</span>
				</dsp:oparam>

				<dsp:oparam name="false">
					<dsp:setvalue param="order" beanvalue="FindOrdersFormHandler.order" />
					
					<dsp:droplet name="/atg/targeting/RepositoryLookup">
						<dsp:param name="repository" bean="/atg/userprofiling/ProfileAdapterRepository" />
						<dsp:param name="itemDescriptor" value="user" />
						<dsp:param name="id" param="order.profileId" />
						<dsp:param name="elementName" value="profile" />
						<dsp:oparam name="output">
							<dsp:droplet name="/atg/dynamo/droplet/IsNull">
								<dsp:param name="value" param="profile" />
								<dsp:oparam name="true">
									<dsp:include page="../../common/header.jsp" />
								</dsp:oparam>
								<dsp:oparam name="false">
									<dsp:include page="../../common/header.jsp">
										<dsp:param name="id" param="order.profileId" />
										<dsp:param name="user" param="profile.login" />
									</dsp:include>
								</dsp:oparam>
							</dsp:droplet>
						</dsp:oparam>
					</dsp:droplet>
					<table align="center" border="0" width="600" cellspacing="0"
						cellpadding="0">
						<tr>
							<td align="center" width="600"><span class=marques>Informations
							sur la commande :</span>
							<p>
							</td>
						</tr>
					</table>

					<table align="center" width="600" border="0" cellpadding="0"
						cellspacing="0">
						<tr>
							<td><span align=right><img
								src="${pageContext.request.contextPath}/html/img/flecheb_retrait.gif"
								border="0">&nbsp;<a class=moncasto
								href="javascript:history.back();">retour</a></span></td>
						</tr>
					</table>

					<table align="center" border="0" width="300" cellspacing="0"
						cellpadding="0">
						<tr bgcolor="#FFDF63">
							<td width=1><img
								src="${pageContext.request.contextPath}/html/img/1pixel.gif"
								width="1" height="1"></td>
							<td width=290><img
								src="${pageContext.request.contextPath}/html/img/1pixel.gif"
								width="1" height="1"></td>
							<td width=8><img
								src="${pageContext.request.contextPath}/html/img/1pixel.gif"
								width="1" height="1"></td>
							<td width=1><img
								src="${pageContext.request.contextPath}/html/img/1pixel.gif"
								width="1" height="1"></td>
						</tr>
						<tr>
							<td width=1 bgcolor="#FFDF63"><img
								src="${pageContext.request.contextPath}/html/img/1pixel.gif"
								width="1" height="1"></td>
							<td width=8><img
								src="${pageContext.request.contextPath}/html/img/1pixel.gif"
								width="1" height="1"></td>
							<td align=center width=290>
							<table align=left border=0 width=290 cellspacing=1 cellpadding=1>
							
							
							
								<%-- Projet Castorama - La Défense --%>
								<%-- dans le cas d'une commande magasin afficher le nom du magasin --%>
								<dsp:droplet name="atg/targeting/RepositoryLookup">
									<dsp:param name="repository" bean="/atg/commerce/order/OrderRepository" />
									<dsp:param name="id" param="id" />
									<dsp:param name="itemDescriptor" value="order" />
									<dsp:param name="elementName" value="orderItem" />
									<dsp:oparam name="output">	
										<dsp:droplet name="/atg/dynamo/droplet/IsNull">
											<dsp:param name="value" param="orderItem.origineMagasin" />
											<dsp:oparam name="false">
												<dsp:droplet name="/atg/dynamo/droplet/Switch">
													<dsp:param name="value" param="orderItem.origineMagasin.id" />
													<dsp:oparam name="999">
														<%-- CastoDirect --%>
													</dsp:oparam>
													<dsp:oparam name="">
														<%-- le magasin origine est vide --%>
													</dsp:oparam>
													<dsp:oparam name="default">
														<%-- Retrait en magasin --%>
														<tr align="center">
														<td class="texte" align=left width=290><b>Magasin : 
															<dsp:valueof param="orderItem.origineMagasin.Nom"/>
														</b>
														</td>
														</tr>
													</dsp:oparam>
												</dsp:droplet>
											</dsp:oparam>	
										</dsp:droplet>	
									</dsp:oparam>
								</dsp:droplet>		
								<%-- Fin *** Projet Castorama - La Défense --%>							
							
							
							
								<tr align="center">
									<td class="texte" align=left width=290><b>Commande num&eacute;ro <dsp:valueof
										param="order.id">
										<i>none</i>
									</dsp:valueof></b></td>
								</tr>
								<tr align="center">
									<td class="texte" align="left" width="290" height="10">&nbsp;</td>
								</tr>
								<tr align="center">
									<td class="texte" align=left width=290><b>Etat : </b>
									<dsp:include page="../../common/fragmentEtat.jsp">
										<dsp:param name="state" param="order.repositoryItem.BOState" />
										<dsp:param name="stateDetail" param="order.repositoryItem.BOStateDetail" />
									</dsp:include>
									</td>
								</tr>
								<tr align="center">
									<td class="texte" align=LEFT width=290><b>M&eacute;thode
									de paiement : </b> <dsp:droplet name="Switch">
										<dsp:param name="value"
											param="order.repositoryItem.paymentgroups[0].paymentMethod" />
										<dsp:oparam name="creditCard">
											Carte bancaire (<dsp:include page="./fragTypeCarte.jsp" />)
										</dsp:oparam>
										<dsp:oparam name="Atout">Carte l'Atout</dsp:oparam>
										<dsp:oparam name="Cheque">Ch&egrave;que</dsp:oparam>
										<dsp:oparam name="Call-Center">Call-Center (<dsp:include page="./fragTypeCarte.jsp" />)</dsp:oparam>
										<dsp:oparam name="default">
											<dsp:valueof param="order.repositoryItem.paymentgroups[0].paymentMethod">
												<i>none</i>
											</dsp:valueof>
										</dsp:oparam>
									</dsp:droplet></td>
								</tr>
								<tr align="center">
									<td class="texte" align=left width=290><b>Cr&eacute;&eacute;e
									le : </b><dsp:valueof param="order.creationDate"
										date="dd/M/yyyy HH:mm:ss">
										<i>non renseign&eacute;e</i>
									</dsp:valueof></td>
								</tr>
								<tr align="center">
									<td class="texte" align=left width=290><b>Soumise le :
									</b><dsp:valueof param="order.submittedDate"
										date="dd/M/yyyy HH:mm:ss">
										<i>non renseign&eacute;e</i>
									</dsp:valueof></td>
								</tr>
								<tr align="center">
									<td class="texte" align=left width=290><b>Modifi&eacute;e
									le : </b><dsp:valueof param="order.lastModifiedDate"
										date="dd/M/yyyy HH:mm:ss">
										<i>non renseign&eacute;e</i>
									</dsp:valueof></td>
								</tr>
								
								<dsp:droplet name="/atg/targeting/RepositoryLookup">
									<dsp:param name="repository"
										bean="/atg/commerce/order/OrderRepository" />
									<dsp:param name="id" param="id" />
									<dsp:param name="itemDescriptor" value="order" />
									<dsp:param name="elementName" value="orderItem" />
									<dsp:oparam name="output">
									
										<dsp:droplet name="/atg/dynamo/droplet/IsNull">
											<dsp:param name="value"
												param="orderItem.commentaireValidation" />
											<dsp:oparam name="false">
												<tr align="center">
													<td class="texte" align=left width=290><b>Commentaire
													de validation : </b><br>
													<dsp:valueof param="orderItem.commentaireValidation">
														<i>non renseign&eacute;</i>
													</dsp:valueof></td>
												</tr>
											</dsp:oparam>
										</dsp:droplet>
									</dsp:oparam>
								</dsp:droplet>		
							</table>
							</td>
							<td width=1 bgcolor="#FFDF63"><img
								src="${pageContext.request.contextPath}/html/img/1pixel.gif"
								width="1" height="1">
							</td>
						</tr>
						<tr bgcolor="#FFDF63">
							<td width=1><img src="${pageContext.request.contextPath}/html/img/1pixel.gif" width="1" height="1"></td>
							<td width=290><img src="${pageContext.request.contextPath}/html/img/1pixel.gif"
								width="1" height="1"></td>
							<td width=8><img src="${pageContext.request.contextPath}/html/img/1pixel.gif"
								width="1" height="1"></td>
							<td width=1><img src="${pageContext.request.contextPath}/html/img/1pixel.gif"
								width="1" height="1"></td>
						</tr>
					</table>
					
					
					<br>
					
			<dsp:droplet name="/atg/targeting/RepositoryLookup">
				<dsp:param name="repository" bean="/atg/commerce/order/OrderRepository" />
				<dsp:param name="id" param="id" />
				<dsp:param name="itemDescriptor" value="order" />
				<dsp:param name="elementName" value="orderItem" />
				<dsp:oparam name="output">

<%-- CR: Modification du modèle de données --%>
					<dsp:droplet name="/atg/dynamo/droplet/IsNull">
						<dsp:param name="value" param="orderItem.numcheque_avtCodeReview" />
						<dsp:oparam name="false">
							<dsp:include page="inc-order_cheque.jsp">
								<dsp:param name="libelleBanque" param="orderItem.libelleBanque_avtCodeReview" />
								<dsp:param name="numcheque" param="orderItem.numcheque_avtCodeReview" />
								<dsp:param name="montantChequeEuros" param="orderItem.montantChequeEuros_avtCodeReview" />								
							</dsp:include>
						</dsp:oparam>
					</dsp:droplet>
					
					<dsp:droplet name="/atg/dynamo/droplet/IsNull">
						<dsp:param name="value" param="orderItem.paymentGroups[0].numcheque" />
						<dsp:oparam name="false">
							<dsp:include page="inc-order_cheque.jsp">
								<dsp:param name="libelleBanque" param="orderItem.paymentGroups[0].libelleBanque" />
								<dsp:param name="numcheque" param="orderItem.paymentGroups[0].numcheque" />
								<dsp:param name="montantChequeEuros" param="orderItem.paymentGroups[0].montantChequeEuros" />								
							</dsp:include>
						</dsp:oparam>
					</dsp:droplet>
					
					<dsp:droplet name="/atg/dynamo/droplet/IsNull">
						<dsp:param name="value" param="orderItem.numCarteAtout_avtCodeReview" />
						<dsp:oparam name="false">
							<dsp:include page="inc-order_atout.jsp">
								<dsp:param name="numeroCarteAtout" bean="OrderFormHandler.numeroCarteAtout" />
								<dsp:param name="dateValidAtout" param="orderItem.dateValidAtout_avtCodeReview" />
								<dsp:param name="optionPaiementAtout" param="orderItem.optionPaiementAtout_avtCodeReview" />
							</dsp:include>
						</dsp:oparam>
					</dsp:droplet>
					
					<dsp:droplet name="/atg/dynamo/droplet/IsNull">
						<dsp:param name="value" param="orderItem.paymentGroups[0].dateValidAtout" />
						<dsp:oparam name="false">
							<dsp:include page="inc-order_atout.jsp">
								<dsp:param name="numeroCarteAtout" value="" />
								<dsp:param name="dateValidAtout" param="orderItem.paymentGroups[0].dateValidAtout" />
								<dsp:param name="optionPaiementAtout" param="orderItem.paymentGroups[0].optionPaiementAtout" />							
							</dsp:include>
						</dsp:oparam>
					</dsp:droplet>			
					
					
				</dsp:oparam>
			</dsp:droplet>
					<%-- !CR: Modification du modèle de données --%>
			<table width="600" align="center" border="0" cellpadding="0"
				cellspacing="0">
				<tr>
					<td width=600 align=center>
					<dsp:include page="./purchase_info_migr.jsp"></dsp:include></td>
				</tr>
				<dsp:droplet name="/atg/dynamo/droplet/IsNull">
					<dsp:param name="value" param="order.submittedDate" />
					<dsp:oparam name="false">
				<tr>
					<td align=right><!-- reseignement -->
						<table width="600" border="0" cellspacing="1" cellpadding="0">
							<tr valign="MIDDLE" align="RIGHT">
								<td width="300" class="moncasto">&nbsp;</td>
								<td width="150" align="right" class="texteb" bgcolor="#E7E6E4">Sous-total TTC :</td>
								<td width="100" class="prixgris2">
									<dsp:tomap var="ssTotal" param="order.repositoryItem.priceInfo"/>
									
									<dsp:include page="../../common/frag_prix.jsp">
										<dsp:param name="prix" param="order.repositoryItem.priceInfo.rawSubTotal" />
										<%--<dsp:param name="prix" param="order.priceInfo.amount" />--%>
										<dsp:param name="formatEntreePrix" value="E" />
										<dsp:param name="formatSortiePrix" value="E" />
									</dsp:include>
								</td>
							</tr>
						</table>
							
					<dsp:droplet name="/atg/targeting/RepositoryLookup">
						<dsp:param name="repository" bean="/atg/commerce/order/OrderRepository" />
						<dsp:param name="id" param="id" />
						<dsp:param name="itemDescriptor" value="order" />
						<dsp:param name="elementName" value="orderItem" />
						<dsp:oparam name="output">
								
									<%--
									---- Projet Castorama - La Défense *** logica 
									---- On n'affiche les FT que pour une commande CD
									--%>
							<dsp:droplet name="CastoIsCommandeMagasin">
								<dsp:param name="order" param="order" />
								<dsp:oparam name="false">	
						<table width="600" border="0" cellspacing="1" cellpadding="0">
							<tr valign="MIDDLE" align="RIGHT">
								<td width="300" class="moncasto">&nbsp;</td>
								<td width="150" class="texteb" bgcolor="#E7E6E4" align="right">Frais de traitement TTC :</td>
								<td width="100" class="prixgris2">
										<!-- AP dsp:tomap var="traitement" param="order.preparationPriceInfo"/-->
									<dsp:include page="../../common/frag_prix.jsp">
										<dsp:param name="prix" param="orderItem.processingFees"/>
										<%--<dsp:param name="prix" param="order.preparationPriceInfo.amount" />--%>
										<dsp:param name="formatEntreePrix" value="E" />
										<dsp:param name="formatSortiePrix" value="E" />
									</dsp:include><br>
								</td>
							</tr>
						</table>
								</dsp:oparam>	
							</dsp:droplet>
									<%-- Fin Projet Castorama - La Défense --%>
		
							<dsp:droplet name="CastoIsCommandeMagasin">
								<dsp:param name="order" param="order" />
								<dsp:oparam name="false">
						<table width="600" border="0" cellspacing="1" cellpadding="0">
							<tr valign="MIDDLE" align="RIGHT">
								<td width="300" class="moncasto">&nbsp;</td>
								<td width="150" class="texteb" bgcolor="#E7E6E4" align="right">Frais de livraison TTC :</td>
								<td width="100" class="prixgris2">
									<dsp:include page="../../common/frag_prix.jsp">
										<dsp:param name="prix" param="orderItem.shippingFees"/>
										<dsp:param name="formatEntreePrix" value="E" />
										<dsp:param name="formatSortiePrix" value="E" />
									</dsp:include>
									<br>
								</td>
							</tr>
						</table>	
								</dsp:oparam>
								<dsp:oparam name="true">
									<dsp:droplet name="/atg/dynamo/droplet/ForEach">
												<dsp:param name="array" param="order.repositoryItem.shippinggroups" />
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
												
							</dsp:oparam>
						</dsp:droplet>
							
							<%--
							---------------------------------------------------
							-----------------ICI PROMOTION--------------------
							---------------------------------------------------
							--%>
             
						<dsp:droplet name="/castorama/droplet/CalculPromo">								
							<dsp:param name="order" bean="FindOrdersFormHandler.order" />
							<dsp:param name="elementName" value="remise" />
							<dsp:oparam name="output">								
																		
								
							<table width="600" border="0" cellspacing="1" cellpadding="0">
								<tr valign="MIDDLE" align="RIGHT">
									<td width="300" class="moncasto">&nbsp;</td>
									<td width="150" class="texteb" bgcolor="#E7E6E4"
												align="right">Remise exceptionnelle :</td>
									<td width="100" class="prix">
											<dsp:include page="../../common/frag_prix.jsp">
												<dsp:param name="prix" param="remise" />
												<dsp:param name="formatEntreePrix" value="E" />
												<dsp:param name="formatSortiePrix" value="E" />
											</dsp:include></td>
							</table>
							</dsp:oparam>
						</dsp:droplet>
							
  						<%--
							---------------------------------------------------
							-----------------ICI FIN PROMOTION--------------------
							---------------------------------------------------
							--%>
							
							
							
							

							<table width="600" border="0" cellspacing="1" cellpadding="0">
								<tr valign="MIDDLE" align="RIGHT">
									<td width="300" class="moncasto">&nbsp;</td>
									<td width="150" class="texteb" bgcolor="#E7E6E4" align="right">Total
									commande TTC :</td>
									<td width="100" class="prix">
                  <%--<dsp:getvalueof var="amount" param="order.repositoryItem.priceInfo.amount" />
                  <dsp:getvalueof var="shipping" param="order.repositoryItem.priceInfo.shipping" />
                  <dsp:getvalueof var="tax" param="order.repositoryItem.priceInfo.tax" />--%>
									<dsp:include page="../../common/frag_prix.jsp">
										<%--<dsp:param name="prix" value="${final}" />--%>
										<dsp:param name="prix" param="order.repositoryItem.montantTotalCommandeTTC"/>
                    <%--<dsp:param name="prix" value="${amount+shipping+tax}"/>--%>
										<dsp:param name="formatEntreePrix" value="E" />
										<dsp:param name="formatSortiePrix" value="E" />
									</dsp:include></td>
								</tr>
							</table>
							</td>
						</tr>
					</dsp:oparam>
				</dsp:droplet>

				</table>
			
			<p>&nbsp;<br>
      
	<%--AP		<table width=600 align=center border=0 cellpadding=0 cellspacing=0>
				<tr>
					<td width=600 align=center><dsp:form formid="editOrder"
						action="../edit/order.jsp" method="post">
						<dsp:input type="hidden" bean="CastoOrderEditor.orderId"
							paramvalue="order.id" />
						<dsp:input type="hidden"
							bean="CastoOrderEditor.loadOrderFailureURL"
							value="../edit/order_unavailable.jsp" />
						<dsp:input type="hidden"
							bean="CastoOrderEditor.loadOrderSuccessURL"
							value="../edit/order.jsp" />
						<dsp:input type="hidden" bean="CastoOrderEditor.orderLockedURL"
							value="../edit/order_unavailable.jsp" />
							
						<dsp:droplet name="/atg/targeting/RepositoryLookup">
							<dsp:param name="repository" bean="/atg/commerce/order/OrderRepository" />
							<dsp:param name="id" param="id" />
							<dsp:param name="itemDescriptor" value="order" />
							<dsp:param name="elementName" value="orderItem" />
							<dsp:oparam name="output">
								<dsp:droplet name="/atg/dynamo/droplet/IsNull">
									<dsp:param name="value" param="orderItem.origineMagasin"/>
									<dsp:oparam name="true">
										<dsp:input type="image" src="../../../img/gestion_commande.gif"
											border="0" bean="CastoOrderEditor.loadOrder"
											value=" Modifier la commande " />
									</dsp:oparam>
									<dsp:oparam name="false">		
										<dsp:droplet name="/atg/dynamo/droplet/Switch">	
											<dsp:param name="value" param="orderItem.origineMagasin.id" />
											<dsp:oparam name="999">
												<dsp:input type="image" src="../../../img/gestion_commande.gif"
													border="0" bean="CastoOrderEditor.loadOrder"
													value=" Modifier la commande " />
											</dsp:oparam>	
										</dsp:droplet>	
									</dsp:oparam>
								</dsp:droplet>	
							</dsp:oparam>
						</dsp:droplet> 
							
						
					</dsp:form></td>
				</tr>
			</table>--%>

				</dsp:oparam> <%-- FIN DE FALSE POUR COMMANDE PAS NULLE ????? --%> 
			</dsp:droplet> 
			
		</dsp:oparam>
	</dsp:droplet>
	</dsp:oparam>
</dsp:droplet>
	<!---- /FindOrdersFormHandler ---->

	<dsp:include page="../../common/menuBas.jsp" />
	</body>
	</html>

</dsp:page>
