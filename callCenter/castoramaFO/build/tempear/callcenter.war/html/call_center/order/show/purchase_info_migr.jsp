
<dsp:page xml="true">


<table border=0 width=600 align=center>
<tr><td width=600 align=center>
 
<dsp:droplet name="/atg/dynamo/droplet/IsNull">
	<dsp:param name="value" param="order"/>
    <dsp:oparam name="true"><span class=prix>Aucun ordre d&eacute;fini !</span></dsp:oparam>
    <dsp:oparam name="false">
    	<dsp:droplet name="/atg/dynamo/droplet/ForEach">
        	<dsp:param name="array" param="order.repositoryItem.commerceitems"/>
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
					       <td class="moncasto" align=left width=120>&nbsp;Arborescence</td>
					       
					       <%-- Projet Castorama - La Défense : ajout du service associe au produit --%>
					       <td class="moncasto" align=center width=120>&nbsp;Service</td>
					       <%-- Fin Projet Castorama - La Défense --%>
					       
					       <td class="moncasto" align=center width=120>Code article</td>
					       <td class="moncasto" align=center width=60>Quantit&eacute;</td>
					       <td class="moncasto" align=center width=178>Identifiant</td>
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
					   	<dsp:setvalue param="sGroup" paramvalue="order.repositoryItem.shippinggroups[0]"/>		
					
			           	<tr>
							<td class="moncasto" align=center width=120>
								<%-- <dsp:droplet name="/castorama/droplet/Arborescence">
									<dsp:param name="productId" param="cItem.auxiliaryData.productRef.repositoryId"/>
									<dsp:param name="elementName" value="viewArbo"/>
									<dsp:oparam name="output">
											<dsp:valueof param="viewArbo"/>
									</dsp:oparam>
								</dsp:droplet> --%>
							</td>
									
							<%-- Projet Castorama - La Défense *** Logica : ajout du service associe au produit --%>
							<td class="moncasto" align="center" width="120">
									<dsp:valueof param="sGroup.shippingMethod" />
							</td>
							<%-- Fin Projet Castorama - La Défense --%>
                <td class="moncasto" align="center" width=120><dsp:valueof param="cItem.codeArticle"/></td>
                <td class="moncasto" align="center" width=60><dsp:valueof param="cItem.quantity"/></td>
                <td class="moncasto" align="center" width=178><dsp:valueof param="cItem.displayName"/></td>
							<dsp:tomap var="priceInfo" param="cItem.priceInfo"/>
							<dsp:tomap var="cItem" param="cItem"/>			
		
							<td class="moncasto" align="center" width=60>
								<dsp:include page="../../common/frag_prix.jsp">
									<dsp:param name="prix" value="${priceInfo.listPrice*cItem.quantity}"/>
									<dsp:param name="formatEntreePrix" value="E"/>
									<dsp:param name="formatSortiePrix" value="E"/>
								</dsp:include>
							</td>
        				</tr>
						<tr><td height="2" colspan=5>&nbsp;</td></tr>
					</dsp:oparam>
				</dsp:droplet><%-- fin de la droplet Switch --%>		
	            			
	        </dsp:oparam>

			<dsp:oparam name="outputEnd">
				</table>
			</dsp:oparam>
		       					
		</dsp:droplet><%-- fin de la droplet ForEach --%>	
	            	
	</dsp:oparam>
</dsp:droplet><%-- fin de la droplet IsNull --%>      				

</td></tr></table>


</dsp:page>