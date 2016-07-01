<%@ taglib prefix="dsp" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"%>
<dsp:page>
  <dsp:importbean bean="/OriginatingRequest" var="originatingRequest" />
  <dsp:importbean bean="/atg/dynamo/droplet/Switch" />
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
  <dsp:importbean bean="/atg/dynamo/droplet/Range" />
  <dsp:importbean bean="/atg/dynamo/droplet/Compare" />
  <dsp:importbean bean="/atg/dynamo/droplet/IsNull" />
  <dsp:importbean bean="/atg/commerce/order/OrderLookup" />
  <dsp:importbean bean="/com/castorama/droplet/MessageStateDroplet" />
  <dsp:importbean bean="/atg/userprofiling/Profile" />
  <dsp:importbean bean="/com/castorama/droplet/SKUFindDroplet" />
  <dsp:importbean bean="/com/castorama/droplet/WebOrderSummaryDroplet" />
  <dsp:importbean bean="/atg/commerce/inventory/InventoryLookup" />
  <dsp:importbean bean="/com/castorama/droplet/ProfileInfoDroplet" />
  <dsp:importbean bean="/com/castorama/commerce/order/CastWebOrdersFormHandler" />
  <dsp:importbean bean="/com/castorama/droplet/MessageDetailsDroplet" />
  
  <dsp:importbean var="dynamoConfig" bean="/atg/dynamo/Configuration" />
  <dsp:getvalueof var="serverName" vartype="java.lang.String" value="${dynamoConfig.siteHttpServerName}" />
  <dsp:getvalueof var="serverPort" vartype="java.lang.String" value="${dynamoConfig.siteHttpServerPort}" />
  <dsp:getvalueof var="httpServer" vartype="java.lang.String" value="http://${serverName}:${serverPort}" />
  <dsp:getvalueof var="staticContentPath" bean="/com/castorama/CastConfiguration.staticContentPath" />
  <c:set var="mode" value="confirmation"/>

  <dsp:getvalueof var="orderId" param="orderId"/>
  <dsp:droplet name="/atg/dynamo/droplet/Compare">
    <dsp:param bean="/atg/userprofiling/Profile.securityStatus" name="obj1"/>
    <dsp:param bean="/atg/userprofiling/PropertyManager.securityStatusLogin" name="obj2"/>
      <dsp:oparam name="lessthan">
        <!-- send the user to the login form -->
        <dsp:setvalue bean="/atg/userprofiling/SessionBean.values.loginSuccessURL" value="${pageContext.request.contextPath}/user/order.jsp?orderId=${orderId}" />
         <dsp:droplet name="/atg/dynamo/droplet/Redirect">
           <dsp:param name="url" value="login.jsp" />
         </dsp:droplet>
      </dsp:oparam>
  </dsp:droplet>

  <cast:pageContainer>
    <jsp:attribute name="bodyContent">

      <dsp:getvalueof var="orderView" param="orderView" />
      <c:choose>
        <c:when test="${'new' == orderView}" >
          <script type="text/javascript">
            $(window).load( function(){ showPopup('newMessage'); });
          </script>
        </c:when>
        <c:when test="${'view' == orderView}" >
          <script type="text/javascript">
            $(window).load( function(){ showPopup('readMessage'); });
          </script>
        </c:when>
        <c:when test="${'replay' == orderView}" >
          <script type="text/javascript">
            $(window).load( function(){ showPopup('replayMessage'); });
          </script>
        </c:when>
        <c:when test="${'sent' == orderView}" >
          <script type="text/javascript">
            $(window).load( function(){ showPopup('sentMessage'); });
          </script>
        </c:when>
        <c:otherwise>
        </c:otherwise>
      </c:choose>

      <dsp:getvalueof var="pageType" value="order" scope="request"/>


      <dsp:getvalueof var="brElement" value="user_orderCardAtout.breadCrumb" scope="request"/>
      <dsp:include page="includes/breadcrumbsClientHeader.jsp">
        <dsp:param name="pageType" value="${pageType}"/>
        <dsp:param name="orderId" param="orderId"/>
      </dsp:include>

      <c:set var="footerBreadcrumb" value="client" scope="request"/>

      <dsp:include page="clientMenu.jsp">
        <dsp:param name="currPage" value="order"/>
      </dsp:include>

      <div class="formBlock orderA noTopMargin">
        <dsp:droplet name="/com/castorama/droplet/CastOrderDetailsDroplet">
          <dsp:param name="orderId" param="orderId" />
          <dsp:param name="profileId" bean="/atg/userprofiling/Profile.repositoryId" />
          <dsp:oparam name="output">
            <dsp:getvalueof var="deliveryType" param="details.orderItem.deliveryType" />
            <div class="orderTitleH">
              <h2>
                <dsp:getvalueof var="result_submittedDate" vartype="java.util.Date" param="details.orderItem.submittedDate" />
                <dsp:getvalueof var="result_validatedDate" vartype="java.util.Date" param="details.orderBOItem.DVALIDATION" />
                <c:choose>
                  <c:when test="${null == result_validatedDate}">
                    <c:set var="submittedDate">
                      <fmt:formatDate value="${result_submittedDate}" pattern="dd/MM/yyyy" />
                    </c:set>
                    <div class="orderHistoryMsg">
                      <fmt:message key="client.order.submitted" >
                        <fmt:param value="${orderId}" />
                        <fmt:param value="${submittedDate}" />
                      </fmt:message>
                    </div>
                  </c:when>
                  <c:otherwise>
                    <c:set var="submittedDate">
                      <fmt:formatDate value="${result_submittedDate}" pattern="dd/MM/yyyy" />
                    </c:set>
                    <c:set var="validatedDate">
                      <fmt:formatDate value="${result_validatedDate}" pattern="dd/MM/yyyy" />
                    </c:set>
                    <div class="orderHistoryMsg">
                      <fmt:message key="client.order.validated" >
                        <fmt:param value="${orderId}" />
                        <fmt:param value="${submittedDate}" />
                        <fmt:param value="${validatedDate}" />
                      </fmt:message>
                    </div>
                  </c:otherwise>
                </c:choose>
              </h2>
              <c:if test="${deliveryType != 'clickAndCollect'}">
                <div id="orderSummuryView">
                  <a href="javascript:void(0);" onclick="javascript:document.getElementById('orderSummury').style.display='';document.getElementById('orderSummuryView').style.display='none';">
                    <fmt:message key="client.orders.view.details" />
                  </a>
                </div>
              </c:if>
            </div>

            <dsp:setvalue param="order" paramvalue="details.orderItem" />
            <%@include file="orderAddresses.jspf" %>

            <div id="orderSummury" style='<c:if test="${deliveryType != 'clickAndCollect'}">display:none;</c:if>'>
              
              <c:if test="${deliveryType != 'clickAndCollect'}">
                <div class="hideOrderDetails">
                  <a class="flLLink" href="javascript:void(0);" onclick="javascript:document.getElementById('orderSummury').style.display='none';document.getElementById('orderSummuryView').style.display='';">
                    <fmt:message key="client.orders.hide.details" />
                  </a>
                </div>
              </c:if>
              
            <dsp:droplet name="/atg/commerce/order/OrderLookup">
                <dsp:param bean="/atg/userprofiling/Profile.repositoryId" name="userId"/>
                <dsp:param param="details.orderItem.id" name="orderId"/>
                <dsp:oparam name="output">
                    <dsp:getvalueof var="orderObj" param="result" />
                </dsp:oparam>
            </dsp:droplet>
            
            <dsp:setvalue param="orderObj" value="${orderObj}" />
               
            <div class="boxOrderTop">
                <table class="boxCartGiftWr" cellspacing="0" cellpadding="0">
                    <tr><td colspan="2" class="boxCartGiftLeft">
                        <dsp:include page="orderPromotionTopBlock.jsp">
                            <dsp:param name="orderObj" value="${orderObj}"/>
                        </dsp:include>
                    </td></tr>
                </table>
            </div>
              
              <div id="boxCartMiddle">
                <dsp:droplet name="WebOrderSummaryDroplet">
                  <dsp:param name="order" param="details.orderItem" />
                  <dsp:oparam name="output">
                    <dsp:getvalueof var="currencyCode" param="currencyCode"/>
                    <dsp:getvalueof var="rawSubTotal" param="rawSubTotal"/>
                    <dsp:getvalueof var="totalDiscount" param="totalDiscount"/>
                    <dsp:getvalueof var="handlingPrice" param="handlingPrice"/>
                    <dsp:getvalueof var="shippingPrice" param="shippingPrice"/>
                    <dsp:getvalueof var="shippingDiscount" param="shippingDiscount"/>
                    <dsp:getvalueof var="hasProductsWithDiscount" param="hasProductsWithDiscount"/>
                    <dsp:getvalueof var="total" param="total"/>
  
                    <table id="productTable" class="boxCartTable singleBasket orderHistoryBoxCartTable">
                      <thead>
                        <tr>
                          <th class="produits"><fmt:message key="msg.cart.products"/></th>
                          <th class="ccTitle ${leftClass}">
                              <table class="boxCartTitle orderHistoryBoxCartTitle">
                                <tr>
                                  <c:if test="${orderDeliveryType == 'deliveryToHome'}">
                                    <td><fmt:message key="msg.cart.delivery"/></td>
                                  </c:if>
                                  <td><fmt:message key="msg.cart.quantity"/></td>
                                  <td><fmt:message key="msg.cart.unit-price-unitaire"/></td>
                                  <td class="sous-total"><fmt:message key="msg.cart.sous-total"/></td>
                                </tr>
                              </table>
                          </th>
                        </tr>
                      </thead>
                      <tbody>
  
                        <dsp:droplet name="ForEach">
                            <dsp:param name="array" param="details.orderItem.commerceItems" />
                            <dsp:param name="elementName" value="commerceItem" />
                            <dsp:oparam name="output">                          
                                <dsp:getvalueof var="commerceItemId" param="commerceItem.catalogRefId" />
                           
                                <dsp:droplet name="ForEach">
                                    <dsp:param name="array" param="orderObj.commerceItems" />
                                    <dsp:param name="elementName" value="commerceItemObj" />
                                    <dsp:oparam name="output">                       
                                    <dsp:getvalueof var="commerceItemObjId" param="commerceItemObj.catalogRefId" />
                                        <c:if test="${commerceItemObjId == commerceItemId}">
                                            <dsp:getvalueof var="commerceItemObject" param="commerceItemObj" />
                                        </c:if>
                                    </dsp:oparam>
                                </dsp:droplet>                                 
                                <tr>
                                    <dsp:include page="orderItem.jsp">
                                        <dsp:param name="commerceItem" param="commerceItem"/>
                                        <dsp:param name="commerceItemObject" value="${commerceItemObject}"/>
                                        <dsp:param name="hasProductsWithDiscount" value="${hasProductsWithDiscount}"/>
                                        <dsp:param name="currencyCode" value="${currencyCode}"/>
                                        <dsp:param name="orderDeliveryType" param="order.deliveryType"/>
                                    </dsp:include>
                                </tr>
                          </dsp:oparam>
                        </dsp:droplet>
                      </tbody>
                    </table>
  
                    <div class="boxCartMiddle orderHistoryBoxCartMiddle">
                      <c:set var="mode" value="confirmation"/>
                      <table class="boxCartTable singleBasket">
                        <tbody class="boxCartTableSummary">
                          <tr>
                            <dsp:getvalueof var="order" param="order"/>
                            <%@ include file="/checkout/includes/prepareSummary.jspf" %>
                            
                            <dsp:include page="/checkout/includes/cart_total_td.jsp">
                              <dsp:param name="labelsList" value="${labelsList}"/>
                            </dsp:include>
                            
                            <td class="productItem_left ccEnableBasket noBorderRight">
                              <div class="boxCartInnerWr">
                                <dsp:getvalueof var="pricesList" value="${d2hPricesList}"/>
                                <%@ include file="/checkout/includes/cartTotalTable.jspf" %>
                              </div>
                            </td>
                          <tr/>
                        </tbody>
                      </table>
                    </div>
                    
                    <div class="orderHistoryInfo">
                    
                      <div class="summaryInfo">
                        <div>
                          <fmt:message key="msg.cart.weight">
                            <fmt:param>
                              <dsp:valueof param="deliveryWeight" />
                            </fmt:param>
                          </fmt:message>
                        </div>
                      </div>
                      <c:if test="${deliveryType == 'clickAndCollect'}">
                        <dsp:droplet name="/com/castorama/droplet/CastLookupDroplet">
                          <dsp:param name="elementName" value="orderStore" />
                          <dsp:param name="repository" bean="/atg/registry/Repository/MagasinGSARepository" />
                          <dsp:param name="itemDescriptor" value="magasin" />
                          <dsp:param name="id" param="order.magasinId" />
                          <dsp:oparam name="output">
                            <dsp:getvalueof var="storeName" param="orderStore.nom"/>
                            <dsp:getvalueof var="phoneNumber" param="orderStore.ccPhone"/>
                            <dsp:getvalueof var="storeUrl" param="orderStore.storeUrl"/>
                          </dsp:oparam>
                        </dsp:droplet>
                        <div class="ccInfo">
                          <p><fmt:message key="msg.cart.cc.store"/></p>
                          <div class="store">
                            <h3>${storeName}</h3>
                            <a href="${storeUrl}" target="_blank"><fmt:message key="stockVisualization.hoursAndLocations" /></a>
                          </div>
                        </div>
                        <div class="ccStorePhone">
                          <c:if test="${not empty phoneNumber}">
                            <p><span><fmt:message key="msg.cart.cc.phone"/></span>
							<span>${phoneNumber}</span></p>
                          </c:if>
                        </div>
                      </c:if>
                    
                    </div>
  
                  </dsp:oparam>
                </dsp:droplet>
              </div>
            </div>

            <div class="clear"></div>

            <c:if test="${deliveryType != 'clickAndCollect'}">
              <dsp:droplet name="ForEach">
                <dsp:param name="array" param="details.deliveries" />
                <dsp:oparam name="outputStart">
                  <dsp:getvalueof var="deliveries_size" param="size" />
                  <c:if test="${1 < deliveries_size}">
                    <h2 class="ordSubTitle">
                      <fmt:message key="client.order.delivery.splitted1" />
                      &nbsp;${deliveries_size}&nbsp;
                      <fmt:message key="client.order.delivery.splitted2" />
                    </h2>
                  </c:if>
                </dsp:oparam>
                <dsp:oparam name="output">
                  <dsp:getvalueof var="deliveries_count" param="count" />
                  <dsp:getvalueof var="deliveries_id" param="element.repositoryId" />
                  <dsp:getvalueof var="delivery_state" param="element.CETAT_OL_C651" />
                  <dsp:getvalueof var="shippment_type" param="element.CEXPEDITION_C657" />
                  <dsp:getvalueof var="shippment_carrier" param="element.TRANSPORTEUR" />
		          <dsp:getvalueof var="url_transporteur" param="element.URL_TRANSPORTEUR" />
		          <dsp:getvalueof var="tracking_number" param="element.NCOLIS_TRANSPORTEUR" />
				  
				  
  
                  <div class="borderBlock2 orderBTbl">
                    <h1>
                      <c:choose>
                        <c:when test="${1 < deliveries_size}">
                          <fmt:message key="client.order.deliveries.number" >
                            <fmt:param value="${deliveries_count}" />
                            <fmt:param value="${deliveries_id}" />
                          </fmt:message>
                        </c:when>
                        <c:otherwise>
                          <fmt:message key="client.order.delivery.number" >
                            <fmt:param value="${deliveries_id}" />
                          </fmt:message>
                        </c:otherwise>
                      </c:choose>
                    </h1>
                    <div class="padded">
                      <table class="orderBTblIns" cellspacing="0" cellpadding="0">
                        <colgroup>
                          <col width="110px"/>
                          <col width="110px"/>
                          <col width="100px"/>
                          <col width="116px"/>
                          <col width="202px"/>
        				  <col width="130px"/>
                        </colgroup>
                        <tbody>
                          <tr>
                            <td class="firstCell" style="padding-right: 0px;">
                              <strong>
                                <fmt:message key="client.order.delivery.state" />
                              </strong>
                            </td>
                            <td style="padding-left: 0px; padding-right: 0px;">
                              <%@include file="shippmentState.jspf" %>
                              <div class="orderNumQuestion">
                                <a href="javascript:showPopup('shipmentStatesHelp');">
                                  <img src="../images/icoQuestionOrange.png" alt="" title=""/>
                                </a>
                              </div>
                            </td>
                            <td style="padding-left: 0px;padding-right: 0px;">
                                    <dsp:getvalueof var="expeditionDate" param="element.DATE_EXPEDITION_REELLE" />
                                    <c:if test="${(5 == delivery_state || 9 == delivery_state) && null != expeditionDate}">
                                        le <dsp:valueof param="element.DATE_EXPEDITION_REELLE" date="dd/MM/yyyy" />
                                    </c:if>
                            </td>
			   
                            <dsp:droplet name="/com/castorama/droplet/CastDisplaySupplierInformation">
                            <dsp:param name="shippment_carrier" value="${shippment_carrier}"/>
                           <dsp:param name="tracking_number" value="${tracking_number}"/>
                            <dsp:param name="url_transporteur" value="${url_transporteur}"/>
                               <dsp:oparam name="output">  
                                   <dsp:getvalueof var="condition" param="condition"/>
                               </dsp:oparam>
                            </dsp:droplet>
                             <td style="padding-left: 0px;padding-right: 0px;">
                              <c:if test="${not empty shippment_carrier}">
                                par ${shippment_carrier}
                              </c:if>
                            </td>
							
                            <td class="suivi" style="padding-left: 10px;">
                              <c:if test="${5 == delivery_state || 9 == delivery_state}">
                                <c:choose>
                                  <c:when test="${1 == shippment_type}" >
                                     <c:if test="${1 == condition}">
                                       <c:if test="${not empty tracking_number}">
                                        <fmt:message key="client.orders.view.suivi" />
                                         ${tracking_number}<br/>
                                           <c:if test="${not empty url_transporteur}">
                                             <a class="seeDetails" href="${url_transporteur}${tracking_number}" target="_blank" rel="nofollow" >
                                               <fmt:message key="client.orders.view.details" />
                                             </a>
                                          </c:if>
                                        </c:if>
                                    </c:if>
                                    <c:if test="${2 == condition}">
                                     <fmt:message key="client.orders.view.suivi" />
                                      ${tracking_number}<br/>
                                      <a class="seeDetails" href="${url_transporteur}" target="_blank" rel="nofollow" >
                                        <fmt:message key="client.orders.view.details" />
                                      </a>
                                    </c:if>
                                     <c:if test="${3 == condition}">
                                       <fmt:message key="client.orders.view.suivi" />
                                        ${tracking_number}<br/>
                                       <fmt:message key="client.order.delivery.later" />
                                     </c:if>
                                     <c:if test="${4 == condition}">
                                     <fmt:message key="client.order.delivery.later" />
                                    </c:if>
                                  </c:when>
                                  <c:when test="${2 == shippment_type || 3 == shippment_type }" >
                                    <c:if test="${1 == condition}">
                                      <c:if test="${not empty tracking_number}">
                                         <fmt:message key="client.orders.view.suivi" />
                                            ${tracking_number}<br/>
                                           <c:if test="${not empty url_transporteur}">
                                              <a class="seeDetails" href="${url_transporteur}" target="_blank" rel="nofollow" >
                                                <fmt:message key="client.orders.view.details" />
                                             </a>
                                          </c:if>
                                        </c:if>
                                    </c:if>
                                     <c:if test="${2 == condition }">
                                     <fmt:message key="client.orders.view.suivi" />
                                      ${tracking_number}<br/>
                                      <a class="seeDetails" href="${url_transporteur}" target="_blank" rel="nofollow" >
                                           <fmt:message key="client.orders.view.details" />
                                       </a>
                                    </c:if>
                                     <c:if test="${3 == condition}">
                                       <fmt:message key="client.orders.view.suivi" />
                                        ${tracking_number}<br/>
                                       <fmt:message key="client.order.delivery.later" />
                                     </c:if>
                                     <c:if test="${4 == condition}">
                                     <fmt:message key="client.order.delivery.later" />
                                    </c:if>
                                    <c:if test ="${5 == condition}">
                                    <fmt:message key="client.orders.view.suivi" />
                                        ${tracking_number}<br/>
                                        <a class="seeDetails" href="${url_transporteur}" target="_blank" rel="nofollow" >
                                           <fmt:message key="client.orders.view.details" />
                                       </a>
                                    </c:if>
                                    <c:if test ="${6 == condition}">
                                     <fmt:message key="client.orders.view.suivi" />
                                        ${tracking_number}<br/>
                                      <fmt:message key="client.order.delivery.later" />
                                    </c:if>
                                  </c:when>
                                  <c:otherwise>
                                    <fmt:message key="client.order.delivery.later" />
                                  </c:otherwise>
                                </c:choose>
                              </c:if>
                            </td>
                            <td style="padding-right: 0px; padding-left: 10px;">
                             
                        <div class="orderBTblRtxtL" style="float: left">
                          <strong>
                            <fmt:message key="client.order.invoice" />
                          </strong>
                        </div>
                        <div class="orderBTblRtxtR"  style="float: left; padding-left: 20px;">
                          <c:choose>
                            <c:when test="${5 == delivery_state || 9 == delivery_state}">
                              <a href="${pageContext.request.contextPath}/generatePdf?delivery_id=${deliveries_id}" target="_blank">
                                <fmt:message key="client.order.invoice.view" />
                              </a>
                            </c:when>
                            <c:otherwise>
                              <fmt:message key="client.order.invoice.unavilable" />
                            </c:otherwise>
                          </c:choose>
                        </div>
                      
                            </td>
                          </tr>
                        </tbody>
                      </table>
                      <c:if test="${8 != delivery_state}" >
                        <dsp:droplet name="/com/castorama/droplet/CastOrderDeliveryItemsDroplet">
                          <dsp:param name="delivery" param="element" />
                          <dsp:param name="elementName" value="shipment_item" />
                          <dsp:param name="indexName" value="shipment_index" />
                          <dsp:oparam name="outputStart">
                            <table class="productsTable darkTable orderTable" cellspacing="0" cellpadding="0">
                              <tbody>
                                <tr>
                                  <th class="alignLeft"><fmt:message key="client.order.item.product" /></th>
                                  <th><fmt:message key="client.order.item.quantity" /></th>
                                  <th><fmt:message key="client.order.item.reserved" /></th>
                                  <th><fmt:message key="client.order.item.shipped" /></th>
                                </tr>
                          </dsp:oparam>
                          <dsp:oparam name="outputEnd">
                              </tbody>
                            </table>
							<script>
								$(function(){
									var images = $('.productItemImage');
									$.each(images, function(index, image){
										var height = $(image).parent().height();
										var paddingTop = Math.round(($(image).parent().height() - $(image).height())/2);
										$(image).css('padding-top', paddingTop);
									})
								})
							</script>
                          </dsp:oparam>
                          <dsp:oparam name="output">
                            <%@include file="itemInShipment.jspf" %>
                          </dsp:oparam>
                        </dsp:droplet>
                      </c:if>
                    </div>
                  </div>
                </dsp:oparam>
              </dsp:droplet>
            </c:if>

            <c:if test="${deliveryType != 'clickAndCollect'}">
              <dsp:droplet name="ForEach">
                <dsp:param name="array" param="details.returns" />
                <dsp:oparam name="outputStart">
                  <h2 class="ordSubTitle"><fmt:message key="client.order.returns" /></h2>
                  <div class="orderTblHolder">
                    <table class="productsTable darkTable orderTable" cellspacing="0" cellpadding="0" >
                      <tbody>
                        <tr valign=top>
                          <th class="alignLeft tmpArticleColumn"><fmt:message key="client.order.returns.article" /></th>
                          <th><fmt:message key="client.order.returns.code" /></th>
                          <th><fmt:message key="client.order.returns.qte" /></th>
                          <th><fmt:message key="client.order.returns.date" /></th>
                          <th><fmt:message key="client.order.returns.state" /></th>
                          <th><fmt:message key="client.order.returns.motif" /></th>
                        </tr>
                </dsp:oparam>
                <dsp:oparam name="outputEnd">
                      </tbody>
                    </table>
                  </div>
                </dsp:oparam>
                <dsp:oparam name="output">
                  <dsp:droplet name="ForEach">
                    <dsp:param name="array" param="element.LIGNES" />
                    <dsp:param name="elementName" value="line" />
                    <dsp:param name="indexName" value="line_index" />
                    <dsp:oparam name="output">
                      <%@include file="returnInDetails.jspf" %>
                    </dsp:oparam>
                  </dsp:droplet>
                </dsp:oparam>
              </dsp:droplet>
            </c:if>

            <c:if test="${deliveryType != 'clickAndCollect'}">
              <dsp:droplet name="ForEach">
                <dsp:param name="array" param="details.refunds" />
                <dsp:oparam name="outputStart">
                  <h2 class="ordSubTitle"><fmt:message key="client.order.refunds" /></h2>
                  <div class="orderTblHolder">
                    <table class="productsTable darkTable orderTable" cellspacing="0" cellpadding="0">
                      <tbody>
                        <tr>
                          <th class="alignLeft tmpRefundsColumn"><fmt:message key="client.order.refunds.motif" /></th>
                          <th><fmt:message key="client.order.refunds.date" /></th>
                          <th class="alignLeft"><fmt:message key="client.order.refunds.method" /></th>
                          <th class="alignLeft"><fmt:message key="client.order.refunds.amount" /></th>
                        </tr>
                </dsp:oparam>
                <dsp:oparam name="outputEnd">
                      </tbody>
                    </table>
                  </div>
                </dsp:oparam>
                <dsp:oparam name="output">
                  <%@include file="refundInDetails.jspf" %>
                </dsp:oparam>
              </dsp:droplet>
            </c:if>

            <c:if test="${deliveryType != 'clickAndCollect'}">
              <dsp:getvalueof var="orderMessages" param="details.messages" />
              <c:if test="${orderMessages != null && fn:length(orderMessages) > 0 }">
                <dsp:form method="post" name="messageForm" id="messageForm">
                  <dsp:input type="hidden" bean="CastWebOrdersFormHandler.messageId" value="" />
                  <dsp:input type="hidden" bean="CastWebOrdersFormHandler.successURL" value=""  priority="1" />
                  <dsp:input type="hidden" bean="CastWebOrdersFormHandler.openMessage" value=""  />
                  <h2 class="ordSubTitle" id="orderMessages" ><fmt:message key="client.order.messages" /></h2>
                  <div class="orderTblHolder">
                    <table class="productsTable darkTable orderTable" cellspacing="0" cellpadding="0" >
                      <tbody>
                        <tr>
                          <th class="alignLeft"><fmt:message key="client.order.messages.status" /></th>
                          <th><fmt:message key="client.order.messages.date" /></th>
                          <th class="alignLeft"><fmt:message key="client.order.messages.subject" /></th>
                          <th class="alignLeft"><fmt:message key="client.order.messages.sender" /></th>
                        </tr>
                        <dsp:droplet name="ForEach">
                          <dsp:param name="array" param="details.messages" />
                          <dsp:oparam name="output">
                            <%@include file="messageInDetails.jspf" %>
                          </dsp:oparam>
                        </dsp:droplet>
                      </tbody>
                    </table>
                  </div>
                </dsp:form>
              </c:if>
            </c:if>

            <%@include file="shipmentStatesHelp.jspf" %>

            <%@include file="readMessage.jspf" %>
            <%@include file="replayMessage.jspf" %>
            <%@include file="newMessage.jspf" %>
            <%@include file="sentMessage.jspf" %>

          </dsp:oparam>
        </dsp:droplet>

        <dsp:include page="includes/adviceBlock.jsp" >
          <dsp:param name="hideAdviceBannner" value="${deliveryType == 'clickAndCollect'}"/>
          <dsp:param name="hideFAQBannner" value="${deliveryType == 'clickAndCollect'}"/>
        </dsp:include>

      </div>
    </jsp:attribute>
  </cast:pageContainer>
  <dsp:getvalueof var="popupName" param="popupName"/>
  <c:if test="${not empty popupName && fn:length(popupName)>0}">
    <script type="text/javascript">
        showPopup('newMessage');
    </script>
  </c:if>

  <div class="whitePopupContainer" id="faq" style="position: absolute;">
  <div class="whitePopupContent">
    <div class="whitePopupHeader">
      <h1><fmt:message key="header.faq"/></h1>
      <fmt:message key="castCatalog_label.close" var="fermer"/>
      <a href="javascript:void(0);" onclick="hideQuestionPopup(this)" class="closeBut" title="${fermer}"><span><!--~--></span>${fermer}</a></div>

      <div class="clear"><!--~--></div>
      <div class="popupContentContainer">
        <div class="popupForm">
          <div class="formMainBlock questionContent">
            <c:import charEncoding="utf-8" url="${staticContentPath}/static-pages/faq-popup-content.html"/>
          </div>
        <div class="clear"><!--~--></div>
      </div>
    </div>
  </div>
  </div>
  <dsp:getvalueof var="popup" param="popup"/> 
  <c:if test="${not empty popup && fn:length(popup)>0}">
    <script type="text/javascript">    
      showPopup('${popup}');
    </script>
  </c:if>
</dsp:page>