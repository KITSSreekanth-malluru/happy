<dsp:page xml="true">


	<%-- 
------------------------------------------------------------------------------------------ 
IMPORTS-----------------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>

<dsp:importbean bean="/castorama/CastoOrderEditor"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>


	<%-- 
------------------------------------------------------------------------------------------ 
Sécurisation HTTPS------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>
	<dsp:include page="../../common/protocolChange.jsp"> <dsp:param name="protocol" value="secure" /> </dsp:include>



<HTML>
<BODY>


<dsp:droplet bame="/atg/dynamo/droplet/IsNull">
   <dsp:param name="value" bean="CastoOrderEditor.order"/>
   <dsp:oparam name="true"><span class=prix>La commande n'a pas &eracute;t&eracute; charg&eracute;e</span>
   </dsp:oparam>
   <dsp:oparam name="false">
      
      <dsp:droplet name="/atg/targeting/RepositoryLookup">
		<dsp:param name="repository" bean="/atg/userprofiling/ProfileAdapterRepository" />
		<dsp:param name="itemDescriptor" value="user" />
		<dsp:param name="id" param="order.profileId" />
		<dsp:param name="elementName" value="profile" />
		<dsp:oparam name="output">
           
           <dsp:droplet name="Switch">
              <dsp:param name="value" param="profile"/>
              <dsp:oparam name="unset">
                    <dsp:include page="../../common/header.jsp"/>
              </dsp:oparam>
              <dsp:oparam name="default">                 
                  <dsp:include page="../../common/header.jsp">
                      <dsp:param name="id" bean="CastoOrderEditor.order.profileId"/>
                      <dsp:param name="user" param="profile.login"/>
                  </dsp:include>
              </dsp:oparam>
          </dsp:droplet> <!--- /Switch --->
        </dsp:oparam>
     </dsp:droplet> <!--- /ProfileRepositoryItemServlet --->
     <p>
     <!--- START --->
    
  <table border=0 cellpadding=0 cellspacing=0 align="center">
   <tr>
  	<td align=center>
      <span class=marques>Ajouter un article à la commande</span>
     <dsp:form action="./order.jsp" method="POST">
     
     <table cellspacing=4 cellpadding=0 border=0>
       <tr>
         <td align="center" class="moncasto">Quantit&eacute;</td>
         <td align="center" class="moncasto"><dsp:setvalue bean="CastoOrderEditor.quantity" value="0"/> 
         <dsp:input type="text" bean="CastoOrderEditor.quantity" size="2"/></td>
       </tr>

       <tr>
         <td align="center" class="moncasto">Product ID</td>
         <td align="center" class="moncasto">
         <dsp:setvalue bean="CastoOrderEditor.productId" value=""/> 
         <dsp:input type="text" bean="CastoOrderEditor.productId" value=""/></td>
       </tr>

       <tr>
         <td align="center" class="moncasto">SKU ID</td>
         <td align="center" class="moncasto">
         <dsp:setvalue bean="CastoOrderEditor.catalogRefId" value=""/> 
         <dsp:input type="text" bean="CastoOrderEditor.catalogRefId" value=""/></td>
       </tr>

       <tr>
         <td align="center" class="moncasto">Adresse</td>
         <td align="center" class="moncasto">
            <dsp:select bean="CastoOrderEditor.shippingGroupId">
               <dsp:include page="./shipping_group_select.jsp">
               	<dsp:param name="iterate_over" bean="CastoOrderEditor.order.shippingGroups"/>
               </dsp:include>
            </dsp:select>
         </td>
       </tr>

       </table>
       <p>
       	   <dsp:input type="hidden" bean="CastoOrderEditor.addItemSuccessURL" value="./order.jsp"/>
	   		<dsp:input type="hidden" bean="CastoOrderEditor.addItemFailureURL" value="${pageContext.request.requestURI}?type=giftCertificate"/>
           <dsp:input type="submit" bean="CastoOrderEditor.addItemToOrder" value="Ajouter"/>
   </dsp:form>
  </td></tr></table>
  </dsp:oparam>
</dsp:droplet>   <!--- /Switch --->

</BODY>
</HTML>
</dsp:page>