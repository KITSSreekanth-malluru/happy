<%@ page contentType="text/html; charset=UTF-8"%>
<dsp:page>
  <dsp:importbean var="dynamoConfig" bean="/atg/dynamo/Configuration" />
  <dsp:importbean var="originatingRequest" bean="/OriginatingRequest" />
   <dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
  
  <dsp:getvalueof var="serverName" vartype="java.lang.String" value="${dynamoConfig.siteHttpServerName}" />
  <dsp:getvalueof var="serverPort" vartype="java.lang.String" value="${dynamoConfig.siteHttpServerPort}" />
  <dsp:getvalueof var="httpServer" vartype="java.lang.String" value="http://${serverName}:${serverPort}" />
  <dsp:getvalueof var="contextPath" vartype="java.lang.String" value="${originatingRequest.contextPath}" />
  <dsp:getvalueof var="httpLink" vartype="java.lang.String" value="${httpServer}${contextPath}" />

  <cast:emailPageContainer >
  
  <jsp:attribute name="messageBody">
      
      <dsp:getvalueof var="nom" param="user.lastName"/>
      <dsp:getvalueof var="prenom" param="user.firstName"/>
      <dsp:getvalueof var="mail" param="user.login"/>    
      <dsp:getvalueof var="userId" param="user.id"/>
      <dsp:getvalueof var="civilite" param="user.civilite"/>
      <dsp:getvalueof var="orderId" param="order.id"/>
      <dsp:getvalueof var="orderMoveToPaymentDate" param="order.lastModifiedDate" vartype="java.util.Date"/> 
       
      <dsp:droplet name="/com/castorama/droplet/CastOrderDetailsDroplet">
          <dsp:param name="orderId" value="${orderId}" />
          <dsp:param name="profileId" value="${userId}" />
		   <dsp:oparam name="output">
		    <dsp:getvalueof var="deliveryType" param="details.orderItem.deliveryType" />
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
				  </dsp:oparam>
				</dsp:droplet>
			</dsp:oparam>
        </dsp:droplet>
		
        
      <table width="580" cellspacing="0" cellpadding="0" align="center">
        <tr>
          <td>
            <br />
            <font size="2" face="Arial" color="#000000">
              <font size="3" face="Arial" color="#09438b"><b>
                <fmt:message key="email.confirmation.shipment.title">
                  <fmt:param>${orderId}</fmt:param>
                </fmt:message>
              </b></font>
              <br/>
               <p>
                <fmt:message key="email.common.client.code">
                  <fmt:param>${userId}</fmt:param>
                </fmt:message>
              </p>        
              
              <p>
  <fmt:message key="email.confirmation.header.attention">
    <fmt:param>
      <strong>
        <dsp:droplet name="/atg/dynamo/droplet/Switch">
          <dsp:param name="value" param="user.civilite"/>
          <dsp:oparam name="miss"><fmt:message key="msg.address.prefix.miss"/>&nbsp;</dsp:oparam>
          <dsp:oparam name="mrs"><fmt:message key="msg.address.prefix.mrs"/>&nbsp;</dsp:oparam>
          <dsp:oparam name="mr"><fmt:message key="msg.address.prefix.mr"/>&nbsp;</dsp:oparam>
        </dsp:droplet> ${prenom}&nbsp;${nom}
      </strong>
    </fmt:param>
  </fmt:message>
</p>
              <p><fmt:message key="email.common.shipment.items"/></p>
              
              <table style="border : 1px solid #e2e2e2;">
                <tbody>
                  <tr style="border-bottom : 1px solid #e2e2e2;">
                    <td style="width:400px;">
                      <fmt:message key="email.common.shipment.items.col.designation"/>
                    </td>
                    <td style="width:50px; text-align: center">
                      <fmt:message key="email.common.shipment.items.col.ref"/>
                    </td>
                    <td style="width:50px; text-align: center">
                      <fmt:message key="email.common.shipment.items.col.quantity"/>
                    </td>
                  </tr>

                  <dsp:droplet name="/com/castorama/droplet/CastOrderDeliveryItemsDroplet">
                      <dsp:param name="delivery" param="shipmentItem"/>
                      <dsp:param name="elementName" value="shipment_item" />
                      <dsp:oparam name="output">
                          <dsp:droplet name="/com/castorama/droplet/SKUFindDroplet" >
                              <dsp:param name="code" param="shipment_item.CESCLAVE" />
                              <dsp:oparam name="output">
                                  <tr>
                                      <td>
                                          <dsp:getvalueof var="productName" param="sku.displayName" />
                                          <%@ include file="/checkout/includes/productName.jspf" %>
                                      </td>
                                      <td style="width:50px; text-align: center"><dsp:valueof param="shipment_item.CESCLAVE" /></td>
                                      <td style="width:50px; text-align: center"><dsp:valueof param="shipment_item.QTE_SORTIE_REEL" number="0" /></td>
                                  </tr>
                              </dsp:oparam>
                          </dsp:droplet>
                      </dsp:oparam>
                  </dsp:droplet>
                </tbody>
              </table>
                       
              
              

            <hr style="width:100%; height :1px; color : #e2e2e2;">
              
<dsp:droplet name="/com/castorama/droplet/CastDisplaySupplierInformation">
                           <dsp:param name="shippment_carrier" value="${shippment_carrier}"/>
                           <dsp:param name="tracking_number" value="${tracking_number}"/>
                            <dsp:param name="url_transporteur" value="${url_transporteur}"/>
                               <dsp:oparam name="output">  
                                   <dsp:getvalueof var="condition" param="condition"/>
                               </dsp:oparam>
               </dsp:droplet>
			   <c:choose>
			        <c:when test="${1 == shippment_type}" >
						  
						     <c:if test="${1 == condition}" >
                                <fmt:message key="email.common.shipment.colissimo1" /> <br/>
                                <c:if test="${not empty url_transporteur}">
                                        - cliquez sur &laquo;<fmt:message key="client.orders.view.details" />&raquo;
                                        <br/>
                                 </c:if>
                                 <c:if test="${not empty tracking_number}">
                                        - <fmt:message key="email.common.shipment.numero"/> &nbsp; ${tracking_number} <br/>
                                 </c:if>
                                 <c:if test="${not empty url_transporteur}">
                                        - Portail de suivi : &nbsp; <a class="seeDetails" href="${url_transporteur}${tracking_number}" target="_blank" rel="nofollow" >
                                               <fmt:message key="client.orders.view.details" />
                                             </a><br/> <br/>
                                 </c:if>
                                  <fmt:message key="email.common.shipment.colissimo2" /> <br/>
								  <fmt:message key="email.common.shipment.colissimo3" /> <br/>
					</c:if>
					<c:if test ="${1 != condition }">
                                    <fmt:message key="email.common.shipment.pns.link" /><br/>
                                    <fmt:message key="email.common.shipment.pns.1" />
                                    <fmt:message key="email.common.shipment.pns.2" />
                                    <fmt:message key="email.common.shipment.pns.3" />
                                    <fmt:message key="email.common.shipment.pns.4" />
                                    <fmt:message key="email.common.shipment.pns.5" />
                               </c:if>
                               
				</c:when>
                                 <c:when test="${2 == shippment_type || 3 == shippment_type}" >
                                   <c:if test="${1 == condition}" >
                                      <fmt:message key="email.common.shipment.colissimo1" /> 
                                         <c:if test="${not empty url_transporteur}">
                                        -  &nbsp;cliquez sur &laquo;<fmt:message key="client.orders.view.details" />&raquo;
                                          <br/>
                                        </c:if>
                                        <c:if test="${not empty tracking_number}">
                                        - &nbsp;<fmt:message key="email.common.shipment.numero"/> ${tracking_number} <br/>
                                        </c:if>
                                        <c:if test="${not empty url_transporteur}">
                                        - &nbsp;Portail de suivi : <a href="${url_transporteur}">Voir le detail</a> <br/> <br/>
                                        </c:if>
                                  <fmt:message key="email.common.shipment.colissimo2" /> <br/>
								  <fmt:message key="email.common.shipment.colissimo3" /> <br/>       
                                </c:if>
                                  
                                
                                     <c:if test="${2 == condition}">
                                       <fmt:message key="email.common.shipment.ldf.2.1" />
                                       <c:if test="${not empty shippment_carrier}">
                                      - &nbsp; <fmt:message key="email.common.shipment.ldf.2.4" /> ${shippment_carrier}<br/>
                                      </c:if>
                                      <c:if test="${not empty tracking_number}">
                                      -&nbsp; <fmt:message key="email.common.shipment.numero"/> &nbsp;${tracking_number}<br/>
                                      </c:if>
                                      <c:if test="${not empty url_transporteur}">
                                      -&nbsp; Portail de suivi :<a href="${url_transporteur}">${url_transporteur}</a> <br/><br/>
                                      </c:if>
                                      <fmt:message key="email.common.shipment.ldf.2.2" />
                                    </c:if>
                                     <c:if test="${3 == condition}">
                                       <fmt:message key="email.common.shipment.ldf.2.1" />
                                       <c:if test="${not empty shippment_carrier}">
                                       -&nbsp;<fmt:message key="email.common.shipment.ldf.2.4" /> ${shippment_carrier}<br/>
                                       </c:if>
                                       <c:if test="${not empty tracking_number}">
                                       -&nbsp; <fmt:message key="email.common.shipment.numero"/> ${tracking_number}<br/><br/>
                                       </c:if>
                                      <fmt:message key="email.common.shipment.ldf.3.1" />
                                      <fmt:message key="email.common.shipment.ldf.2.2" />
                                    </c:if>
                                     <c:if test="${4 == condition || 5 == condition || 6 == condition}">
                                     <fmt:message key="email.common.shipment.ldf.4.1" /><br/>
                                     <fmt:message key="email.common.shipment.pns.link" /><br/>
                                     <fmt:message key="email.common.shipment.ldf.4.2" />
                                     <fmt:message key="email.common.shipment.ldf.4.3" />
                                     <fmt:message key="email.common.shipment.ldf.2.2" />
                                  </c:if>
                                  </c:when>
                                  
                        </c:choose>
              
              
              
             
              
              <p>
                <fmt:message key="email.signature.confirmation.shipment.soon.on">
                  <fmt:param><a href="${httpLink}"><font face="Arial" color="#09438b">www.castorama.fr</font></a></fmt:param>
                </fmt:message>
              </p>
              
              <p><fmt:message key="email.signature.cordially"/></p>
              <p>
                <fmt:message key="email.signature.customer.service">
                  <fmt:param><br/><a href="${httpLink}">www.castorama.fr</a></fmt:param>
                </fmt:message>
              </p>
              
              <hr style="width:100%; height :1px; color : #e2e2e2;">
            
              <%@ include file="/emailTemplates/includes/procedureToFollowInReceipt.jspf" %>
              
              <hr style="width:100%; height :1px; color : #e2e2e2;">
            
              <%@ include file="/emailTemplates/includes/confirmarionCommandeFAQSection.jspf" %>
               
              </font>                             
          </td>
        </tr>
        <tr>
            <td height="20"></td>
        </tr>
      </table>
  </jsp:attribute>
  
  <jsp:attribute name="disclaimerBody">
    <%@ include file="/emailTemplates/includes/salesConditionsNotice.jspf" %> 
  </jsp:attribute>
  </cast:emailPageContainer>
</dsp:page>
