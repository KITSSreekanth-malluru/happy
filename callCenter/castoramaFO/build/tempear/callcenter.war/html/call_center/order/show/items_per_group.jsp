<dsp:page xml="true">


<dsp:droplet name="/atg/dynamo/droplet/ForEach">
  <dsp:param name="array" param="group.commerceItemRelationships"/>
  <dsp:param name="elementName" value="cItemRel"/>
  <dsp:oparam name="output">
     <dsp:setvalue param="cItem" paramvalue="cItemRel.commerceItem"/>
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
			<img src="<dsp:valueof param="urlPicto"/>">
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
<%--			<dsp:param name="prix" param="cItem.priceInfo.amount"/>--%>
			<dsp:param name="prix" value="${priceInfo.amount}"/>
			<dsp:param name="formatEntreePrix" value="E"/>
			<dsp:param name="formatSortiePrix" value="E"/>
		</dsp:include></td>
      </tr>
	  <tr><td height="2" colspan=5>&nbsp;</td></tr>
      <br>
  </dsp:oparam>
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
  <dsp:oparam name="empty"></dsp:oparam>              
</dsp:droplet>

</dsp:page>