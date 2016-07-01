<%@page import="atg.servlet.DynamoHttpServletRequest"%>
<dsp:page xml="true">


	<%-- 
------------------------------------------------------------------------------------------ 
IMPORTS-----------------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>

<%-- GY : stabilisation : journalisation--%>
<dsp:importbean bean="/atg/userprofiling/Profile"/>



<dsp:importbean bean="/castorama/CastoOrderEditor"/>


<!--- Preset some params based on what kind of a group we are --->
	<%-- 
------------------------------------------------------------------------------------------ 
Sécurisation HTTPS------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>
	<dsp:include page="../../common/protocolChange.jsp"> <dsp:param name="protocol" value="secure" /> </dsp:include>



<dsp:droplet name="/atg/dynamo/droplet/TableForEach">
  <dsp:param name="array" param="group.commerceItemRelationships"/>
  <dsp:param name="numColumns" value="1"/>
  <dsp:param name="elementName" value="cItemRel"/>
   <dsp:oparam name="outputStart">
   		<table align=center border=0 width=700 cellspacing =0 cellpadding=0>
		     <tr bgcolor="#FFDF63">
			 <td width=1><img src="/img/1pixel.gif" width="1" height="1"></td>
		       <td width=648><img src="/img/1pixel.gif" width="1" height="1"></td>
			   <td width=1><img src="/img/1pixel.gif" width="1" height="1"></td>
			   </tr>
			   <tr>
			   <td width=1 bgcolor="#FFDF63"><img src="/img/1pixel.gif" width="1" height="1"></td>
		       <td align=center width=648>
		    <table align=center border=0 width=648 cellspacing =0 cellpadding=0>
		     <tr  align="center">
			 	<td class=moncasto align="center" width=100>
		            <dsp:droplet name="/atg/dynamo/droplet/Switch">
		               <dsp:param name="value" param="group_type"/>
		               <dsp:oparam name="shipping">
		                  Quantit&eacute; d&eacute;sir&eacute;e
		               </dsp:oparam>
		               <dsp:oparam name="default"></dsp:oparam>
		            </dsp:droplet>
         		</td>
				<td class="moncasto" align=center width=50>&nbsp;Quantit&eacute; actuelle</td>
				<td class="moncasto" align=center width=180>&nbsp;Arborescence</td>
		       <td class="moncasto" align=center width=60>Code article</td>
		       <td class="moncasto" align=center width=158>Identifiant</td>
		       <td class="moncasto" align=center width=150>&nbsp;</td>
		    </tr>
			</table>
			</td>
			<td width=1 bgcolor="#FFDF63"><img src="/img/1pixel.gif" width="1" height="1"></td>
			   </tr>
			   <tr bgcolor="#FFDF63">
			   <td width=1><img src="/img/1pixel.gif" width="1" height="1"></td>
		       <td width=648><img src="/img/1pixel.gif" width="1" height="1"></td>
			   <td width=1><img src="/img/1pixel.gif" width="1" height="1"></td>
			   </tr>
		</table>
  	 	<table align=center border=0 width=700>
       
   </dsp:oparam>
   <dsp:oparam name="outputEnd">
       </table>
   </dsp:oparam>
   <dsp:oparam name="output">
   	<%
		//com.castorama.CastoOrderModifierFormHandler l_strOrderEditor = (com.castorama.CastoOrderModifierFormHandler)((DynamoHttpServletRequest)request).resolveName("/castorama/CastoOrderEditor");
		//String l_strOrderId = l_strOrderEditor.getOrder().getId();
		//String l_strCommentaire = "La commande "+l_strOrderId+ " a ete modifiee";
	%>
    <dsp:form action="./order_unavailable.jsp" method="POST" name="form_supp">
       <dsp:setvalue param="cItem" paramvalue="cItemRel.commerceItem"/>
	   <dsp:input type="hidden" bean="CastoOrderEditor.relationshipId" paramvalue="cItemRel.id"/>
       <dsp:input type="hidden" bean="CastoOrderEditor.commerceItemId" paramvalue="cItem.id"/>
       <dsp:input type="hidden" bean="CastoOrderEditor.shippingGroupId" paramvalue="group.id"/>
	   <dsp:input type="hidden" bean="CastoOrderEditor.productId" paramvalue="cItem.auxiliaryData.productRef.repositoryId"/>
	   <dsp:input type="hidden" bean="CastoOrderEditor.catalogRefId" paramvalue="cItem.catalogRefId"/>
	   <dsp:input type="hidden" bean="CastoOrderEditor.totalQuantity" paramvalue="cItemRel.quantity"/>
	   <dsp:input type="hidden" bean="CastoOrderEditor.type" value="Contact CallCenter"/>
	   <dsp:input type="hidden" bean="CastoOrderEditor.action" value="Modification"/>
	   <dsp:input type="hidden" bean="CastoOrderEditor.commentaire" value="La commande ${CastoOrderEditor.order.id} a ete modifiee"/>
	   <dsp:input type="hidden" bean="CastoOrderEditor.ip" beanvalue="/atg/dynamo/servlet/pipeline/BrowserAttributes.remoteAddr"/>
	   <dsp:input type="hidden" bean="CastoOrderEditor.saveOrderSuccessURL" value="./order.jsp"/>
	   <dsp:input type="hidden" bean="CastoOrderEditor.saveOrderFailureURL" value="order_unavailable.jsp"/>
		<%--<dsp:droplet name="/atg/dynamo/security/CurrentUser">
            <dsp:oparam name="output">
				  <input type="hidden" bean="CastoOrderEditor.nomLogin" param="username">
			</dsp:oparam>
            <dsp:oparam name="empty"></dsp:oparam>
            <dsp:oparam name="error">error</dsp:oparam>
		</dsp:droplet>--%>
				
      <input type="hidden" name="ORDER_MODIFIED" value="true">
	          <tr>
              <!-- quantite -->
              <td align="center" width="100">
                  <dsp:droplet name="/atg/dynamo/droplet/Switch">
                     <dsp:param name="value" param="cItemRel.relationshipTypeAsString"/>
                          <!--- Quantite --->
                          <dsp:oparam name="SHIPPINGQUANTITY">
                             <dsp:input size="3" bean="CastoOrderEditor.wantedQuantity" name="champsQuantite" paramvalue="cItemRel.quantity" iclass="moncasto"/>
                          </dsp:oparam>
                          <dsp:oparam name="unset">
                                  <i>Non d&eacute;finie</i>
                          </dsp:oparam>
                  </dsp:droplet>
              </td>
			  <!-- quantite actuelle-->
              <td align="center" width="50">
                  <dsp:droplet name="/atg/dynamo/droplet/Switch">
                     <dsp:param name="value" param="cItemRel.relationshipTypeAsString"/>
                          <!--- Quantite --->
                          <dsp:oparam name="SHIPPINGQUANTITY">
                             <span class="moncasto"><dsp:valueof param="cItemRel.quantity"/></span>
                          </dsp:oparam>
                          <dsp:oparam name="unset">
                                  <i>Non d&eacute;finie</i>
                          </dsp:oparam>
                  </dsp:droplet>
              </td>
              <!-- Product -->
              <td align="center" class=moncasto width="180">
                    <dsp:valueof param="cItem.auxiliaryData.productRef.displayName"><dsp:valueof param="cItem.auxiliaryData.productId"><i>undef</i></dsp:valueof></dsp:valueof>                                                 
              </td>
              <!-- Code article -->
              <td align="center" class="moncasto" width="60">
<dsp:droplet name="/atg/targeting/RepositoryLookup">
<dsp:param name="repository" bean="/atg/commerce/order/OrderRepository"/>
<dsp:param name="elementName" value="article"/>
<dsp:param name="itemDescriptor" value="commerceItem"/>
<dsp:param name="id" param="cItem.id"/>
<dsp:oparam name="output">
	<dsp:valueof param="article.codeArticle"></dsp:valueof>                                               
</dsp:oparam>
</dsp:droplet>
              </td>
              <!-- Identifiant -->
              <td align="center" class=moncasto width="158">
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
              <td align=center class=moncasto width="150">
                  <!--&nbsp;<input type="image" src="/img/valider.gif" border=0 bean="CastoOrderEditor.setOrder" value="Supprimer">-->
				&nbsp;
				
				
			<%-- GY : stabilisation : journalisation--%>
				<input name="userId" type="hidden" value="<dsp:valueof bean="Profile.repositoryId"/>"/>
				<input name="userLogin" type="hidden" value="<dsp:valueof bean="Profile.login"/>"/>
				<input name="numeroCommande" type="hidden" value="<dsp:valueof bean="CastoOrderEditor.orderId"/>"/>
			<%-- / GY : stabilisation --%>
				
				<dsp:input type="image" src="${pageContext.request.contextPath}/html/img/valider.gif" border="0" bean="CastoOrderEditor.removeAndAddItemFromOrder" value="Supprimer"/>
             </td>
            </tr>
            <tr><td colspan=5></td></tr>
         </dsp:form>
      </dsp:oparam>
      <dsp:oparam name="empty"><i><dsp:valueof param="empty_msg"/></i></dsp:oparam>             
</dsp:droplet>
<dsp:include page="../../common/menuBas.jsp"/>


</dsp:page>
