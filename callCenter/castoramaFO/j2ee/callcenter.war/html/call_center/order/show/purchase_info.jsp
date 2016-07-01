
<dsp:page xml="true">


<table border=0 width=600 align=center>
<tr><td width=600 align=center>
 
<dsp:droplet name="/atg/dynamo/droplet/IsNull">
	<dsp:param name="value" param="order"/>
    <dsp:oparam name="true"><span class=prix>Aucun ordre d&eacute;fini !</span></dsp:oparam>
    <dsp:oparam name="false">
    	<dsp:droplet name="/atg/dynamo/droplet/ForEach">
        	<dsp:param name="array" param="order.relationships"/>
            <dsp:param name="elementName" value="rShip"/>
              
            <dsp:oparam name="outputStart">
							
				<table align=center border=0 width=600 cellspacing =0 cellpadding=0>
					<tr bgcolor="#FFDF63">
						<td width=1><img src="/img/1pixel.gif" width="1" height="1"></td>
					    <td width=598><img src="/img/1pixel.gif" width="1" height="1"></td>
						<td width=1><img src="/img/1pixel.gif" width="1" height="1"></td>
					</tr>
					<tr>
						<td width=1 bgcolor="#FFDF63"><img src="/img/1pixel.gif" width="1" height="1"></td>
					    <td align=center width=598>
					    <table align=center border=0 width=598 cellspacing =0 cellpadding=0>
					     <tr  align="center">
					       <td class="moncasto" align=left width=120>&nbsp;Arborescence</td>
					       
					       <%-- Projet Castorama - La Défense : ajout du service associe au produit --%>
					       <td class="moncasto" align=center width=120>&nbsp;Service</td>
					       <%-- Fin Projet Castorama - La Défense --%>
					       
					       <td class="moncasto" align=center width=120>Code article</td>
					       <td class="moncasto" align=center width=60>Quantit&eacute;</td>
					       <td class="moncasto" align=center width=118>Identifiant</td>
						   <td class="moncasto" align=center width=60>Poids</td>
					       <td class="moncasto" align=center width=60>Prix</td>
					     </tr>
						</table>
						</td>
						<td width=1 bgcolor="#FFDF63"><img src="/img/1pixel.gif" width="1" height="1"></td>
					</tr>
					<tr bgcolor="#FFDF63">
						<td width=1><img src="/img/1pixel.gif" width="1" height="1"></td>
					    <td width=598><img src="/img/1pixel.gif" width="1" height="1"></td>
						<td width=1><img src="/img/1pixel.gif" width="1" height="1"></td>
					</tr>
				</table>

				<table align=center border=0 width=600>
			</dsp:oparam>

            <dsp:oparam name="output">
              
            	<dsp:droplet name="/atg/dynamo/droplet/Switch">
              		<dsp:param name="value" param="size"/>
              		<dsp:oparam name="0">
		       			<span class="prix">Cette commande ne contient pas d'article !</span>
              		</dsp:oparam>
					<dsp:oparam name="default">           
		            	<dsp:droplet name="/atg/dynamo/droplet/Switch">
		              		<dsp:param name="value" param="rShip.relationshipType"/>
		              		<dsp:oparam name="100">
								<dsp:setvalue param="cItem" paramvalue="rShip.commerceItem"/>	
							   	<dsp:setvalue param="sGroup" paramvalue="rShip.shippingGroup"/>		
		
					
					           	<tr>
									<td class="moncasto" align=center width=120>
										<dsp:droplet name="/castorama/droplet/Arborescence">
											<dsp:param name="productId" param="cItem.auxiliaryData.productRef.repositoryId"/>
											<dsp:param name="elementName" value="viewArbo"/>
											<dsp:oparam name="output">
												<dsp:valueof param="viewArbo"/>
											</dsp:oparam>
										</dsp:droplet>
									</td>
									
									<%-- Projet Castorama - La Défense *** Logica : ajout du service associe au produit --%>
									<td class="moncasto" align="center" width="120">
										<dsp:valueof param="sGroup.shippingMethod" />
									</td>
									<%-- Fin Projet Castorama - La Défense --%>
									
									<dsp:droplet name="/atg/targeting/RepositoryLookup">
										<dsp:param name="repository" bean="/atg/commerce/order/OrderRepository"/>
										<dsp:param name="elementName" value="article"/>
										<dsp:param name="itemDescriptor" value="commerceItem"/>
										<dsp:param name="id" param="cItem.id"/>
										<dsp:oparam name="output">
											<td class="moncasto" align="center" width=120><dsp:valueof param="article.codeArticle"/></td>
											<td class="moncasto" align="center" width=60><dsp:valueof param="cItem.quantity"/></td>
											<td class="moncasto" align="center" width=118>
												<dsp:droplet name="/atg/targeting/RepositoryLookup">
													<dsp:param name="repository" bean="/atg/commerce/catalog/ProductCatalog"/>
													<dsp:param name="elementName" value="produit"/>
													<dsp:param name="itemDescriptor" value="sku"/>
													<dsp:param name="id" param="cItem.catalogRefId"/>
													<dsp:oparam name="output">
														<dsp:valueof param="produit.displayName"/>
													</dsp:oparam>
							
												</dsp:droplet>					
											</td>
											<td class="moncasto" align="center" width=60>
											<dsp:droplet name="/castorama/droplet/DropletPfe">
												<dsp:param name="poids" param="article.poids"/>
												<dsp:param name="quantite" param="cItem.quantity"/>
												<dsp:oparam name="output">
													<dsp:valueof param="poidsEnKg"/> Kg 
													<dsp:droplet name="/atg/dynamo/droplet/IsNull">
														<dsp:param name="value" param="urlPicto"/>
														<dsp:oparam name="false">
															<img src="<dsp:valueof param="urlPicto"/>"/>
														</dsp:oparam>
													</dsp:droplet>
												</dsp:oparam>
											</dsp:droplet>
											</td>
										</dsp:oparam>
									</dsp:droplet><%-- fin de la droplet RepositoryLookup --%>
		
		
									<dsp:tomap var="priceInfo" param="cItem.priceInfo"/>
									<dsp:tomap var="cItem" param="cItem"/>			
		
									<td class="moncasto" align="center" width=60>
										<dsp:include page="../../common/frag_prix.jsp">
											<dsp:param name="prix" value="${priceInfo.amount}"/>
											<dsp:param name="formatEntreePrix" value="E"/>
											<dsp:param name="formatSortiePrix" value="E"/>
										</dsp:include>
									</td>
		        				</tr>
								<tr><td height="2" colspan=5>&nbsp;</td></tr>
				        	</dsp:oparam>
		
						</dsp:droplet><%-- fin de la droplet Switch --%>
					</dsp:oparam>
				</dsp:droplet><%-- fin de la droplet Switch --%>		
	            			
	        </dsp:oparam>
	          
			<dsp:oparam name="outputEnd">
				</table>
			</dsp:oparam>
		       					
		</dsp:droplet><%-- fin de la droplet ForEach --%>	
	            	
	</dsp:oparam>
</dsp:droplet><%-- fin de la droplet IsNull --%>      				

           
           
           
           
             <%--<dsp:param name="array" param="order.commerceItems"/>
             <dsp:param name="elementName" value="cItem"/>
			<dsp:oparam name="outputStart">
				<table align=center border=0 width=600 cellspacing =0 cellpadding=0>
				     <tr bgcolor="#FFDF63">
					 <td width=1><img src="/img/1pixel.gif" width="1" height="1"></td>
				       <td width=598><img src="/img/1pixel.gif" width="1" height="1"></td>
					   <td width=1><img src="/img/1pixel.gif" width="1" height="1"></td>
					   </tr>
					   <tr>
					   <td width=1 bgcolor="#FFDF63"><img src="/img/1pixel.gif" width="1" height="1"></td>
				       <td align=center width=598>
				    <table align=center border=0 width=598 cellspacing =0 cellpadding=0>
				     <tr  align="center">
				       <td class="moncasto" align=left width=240>&nbsp;Arborescence</td>
				       <td class="moncasto" align=center width=120>Code article</td>
				       <td class="moncasto" align=center width=60>Quantit&eacute;</td>
				       <td class="moncasto" align=center width=118>Identifiant</td>
					   <td class="moncasto" align=center width=60>Poids</td>
				       <td class="moncasto" align=center width=60>Prix</td>
				    </tr>
					</table>
					</td>
					<td width=1 bgcolor="#FFDF63"><img src="/img/1pixel.gif" width="1" height="1"></td>
					   </tr>
					   <tr bgcolor="#FFDF63">
					   <td width=1><img src="/img/1pixel.gif" width="1" height="1"></td>
				       <td width=598><img src="/img/1pixel.gif" width="1" height="1"></td>
					   <td width=1><img src="/img/1pixel.gif" width="1" height="1"></td>
					   </tr>
				  </table>
			
			<table align=center border=0 width=600>
			</dsp:oparam>
			
			<dsp:oparam name="outputEnd">
				
				</table>
			</dsp:oparam>

             <dsp:oparam name="output">
             <tr>
			 	<td class="moncasto" align=center width=240>
					<dsp:droplet name="/castorama/droplet/Arborescence">
						<dsp:param name="productId" param="cItem.auxiliaryData.productRef.repositoryId"/>
						<dsp:param name="elementName" value="viewArbo"/>
						<dsp:oparam name="output">
							<dsp:valueof param="viewArbo"/>
						</dsp:oparam>
					</dsp:droplet>
				</td>
				
<dsp:droplet name="/atg/targeting/RepositoryLookup">
<dsp:param name="repository" bean="/atg/commerce/order/OrderRepository"/>
<dsp:param name="elementName" value="article"/>
<dsp:param name="itemDescriptor" value="commerceItem"/>
<dsp:param name="id" param="cItem.id"/>
<dsp:oparam name="output">
				<td class="moncasto" align="center" width=120><dsp:valueof param="article.codeArticle"/></td>
				<td class="moncasto" align="center" width=60><dsp:valueof param="cItem.quantity"/></td>
				<td class="moncasto" align="center" width=118>
					<dsp:droplet name="/atg/targeting/RepositoryLookup">
						<dsp:param name="repository" bean="/atg/commerce/catalog/ProductCatalog"/>
						<dsp:param name="elementName" value="produit"/>
						<dsp:param name="itemDescriptor" value="sku"/>
						<dsp:param name="id" param="cItem.catalogRefId"/>
						<dsp:oparam name="output">
							<dsp:valueof param="produit.displayName"/>
						</dsp:oparam>

					</dsp:droplet>					
				</td>
				<td class="moncasto" align="center" width=60>
		<dsp:droplet name="/castorama/droplet/DropletPfe">
		<dsp:param name="poids" param="article.poids"/>
		<dsp:param name="quantite" param="cItem.quantity"/>
		<dsp:oparam name="output">
			<dsp:valueof param="poidsEnKg"/> Kg 
			<dsp:droplet name="/atg/dynamo/droplet/IsNull">
			<dsp:param name="value" param="urlPicto"/>
			<dsp:oparam name="false">
				<img src="<dsp:valueof param="urlPicto"/>"/>
			</dsp:oparam>
			</dsp:droplet>
		</dsp:oparam>
		</dsp:droplet>
				</td>
</dsp:oparam>
</dsp:droplet>


				<dsp:tomap var="priceInfo" param="cItem.priceInfo"/>
				<dsp:tomap var="cItem" param="cItem"/>				

				<td class="moncasto" align="center" width=60>
					<dsp:include page="../../common/frag_prix.jsp">
						<dsp:param name="prix" value="${priceInfo.amount}"/>
						<dsp:param name="formatEntreePrix" value="E"/>
						<dsp:param name="formatSortiePrix" value="E"/>
					</dsp:include></td>
              </tr>
			  <tr><td height="2" colspan=5>&nbsp;</td></tr>
             </dsp:oparam>
             <dsp:oparam name="empty"><span class="prix">Cette commande ne contient pas d'article !</span></dsp:oparam>--%>

</td></tr></table>


</dsp:page>