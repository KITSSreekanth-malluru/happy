<%@ page contentType="text/html; charset=UTF-8"%>
<dsp:page>
  <dsp:importbean bean="/com/castorama/droplet/CastPriceItem"/>
  <dsp:importbean bean="/com/castorama/droplet/CastPriceDroplet"/>
  <dsp:importbean var="dynamoConfig" bean="/atg/dynamo/Configuration" />
  <dsp:importbean var="originatingRequest" bean="/OriginatingRequest" />
  
  <dsp:getvalueof var="serverName" vartype="java.lang.String" value="${dynamoConfig.siteHttpServerName}" />
  <dsp:getvalueof var="serverPort" vartype="java.lang.String" value="${dynamoConfig.siteHttpServerPort}" />
  <dsp:getvalueof var="httpServer" vartype="java.lang.String" value="http://${serverName}:${serverPort}" />
  <dsp:getvalueof var="contextPath" vartype="java.lang.String" value="${originatingRequest.contextPath}" />
  <dsp:getvalueof var="httpLink" vartype="java.lang.String" value="${httpServer}${contextPath}" />

  <cast:emailPageContainer displayHeader="false" displayFooter="false"> 
    <jsp:attribute name="messageBody">
      <dsp:getvalueof var="nom" param="user.lastName"/>
      <dsp:getvalueof var="prenom" param="user.firstName"/>
      <dsp:getvalueof var="mail" param="user.login"/>    
      <dsp:getvalueof var="userId" param="user.id"/>
      <dsp:getvalueof var="civilite" param="user.civilite"/>
      <dsp:getvalueof var="transId" param="transId"/>   

      <dsp:getvalueof var="orderId" param="order.id"/>
      <dsp:getvalueof var="orderMoveToPaymentDate" param="order.lastModifiedDate" vartype="java.util.Date"/>

      <dsp:droplet name="/com/castorama/droplet/CastLookupDroplet">
        <dsp:param name="elementName" value="orderStore" />
        <dsp:param name="repository" bean="/atg/registry/Repository/MagasinGSARepository" />
        <dsp:param name="itemDescriptor" value="magasin" />
        <dsp:param name="id" param="order.magasinId" />
        <dsp:oparam name="output">
          <dsp:getvalueof var="storeName" param="orderStore.nom"/>
          <dsp:getvalueof var="storePhoneNumber" param="orderStore.ccPhone"/>
          <dsp:getvalueof var="storeUrl" param="orderStore.storeUrl"/>
          <dsp:getvalueof var="delayTime" param="orderStore.ccDelayPeriod"/>
          <dsp:getvalueof var="delayUnit" param="orderStore.ccDelayPeriodUnit"/>
          <fmt:message key="header.mon.magasin.delayunit.hour" var="hour"/>
          <fmt:message key="header.mon.magasin.delayunit.day" var="day"/>
          <fmt:message key="header.mon.magasin.delayunit.days" var="days"/>
          <dsp:getvalueof var="displayDelayUnit" value="${(delayUnit == 0)?hour:(delayTime == 1?day:days)}"/>
        </dsp:oparam>
      </dsp:droplet>

      <table cellspacing="0" cellpadding="0" bgcolor="#ffffff" align="center" style="width:600px;">
        <tbody>
          <tr>
            <td width="10">
              <font size="1">&nbsp;</font>
            </td>
          </tr>
          <tr>
            <td width="10">
              &nbsp;
            </td>
            <td width="580">
              <table cellspacing="0" cellpadding="0" bgcolor="#ffffff">
                <tbody>
                  <tr>
                    <td>
                      <img alt="Castorama.fr" src="${httpServer}/images/email/castorama-email-cc-header.gif">
                    </td>
                  </tr>
                    <tr>
                      <td align="left">
                        <br>
                        <h1 style="font-family: arial; color: #4B4B4D; font-size: 20px; font-weight: normal">
                          <fmt:message key="email.confirmation.cc.title"><fmt:param>${orderId}</fmt:param></fmt:message></h1>
                        <br>
                        
                        <p><font size="2" face="Arial"> 
                          <p>
                            <fmt:message key="email.confirmation.cc.hello"/>&nbsp;
                             <dsp:droplet name="/atg/dynamo/droplet/Switch">
                              <dsp:param name="value" param="user.civilite"/>
                              <dsp:oparam name="miss"><fmt:message key="msg.address.prefix.miss" />&nbsp;</dsp:oparam>
                              <dsp:oparam name="mrs"><fmt:message key="msg.address.prefix.mrs" />&nbsp;</dsp:oparam>
                              <dsp:oparam name="mr"><fmt:message key="msg.address.prefix.mr" />&nbsp;</dsp:oparam>
                            </dsp:droplet>
                            ${nom},
                          </p>
                        </font></p>
                        
                        <p><font size="2" face="Arial">
                          <fmt:message key="email.confirmation.cc.content.1">
                            <fmt:param>
                              <b><fmt:formatDate value="${orderMoveToPaymentDate }" pattern="dd/MM/yyyy à HH'h'mm" /></b>
                            </fmt:param>
                          </fmt:message>
                        </font></p>
                        
                        <p><font size="2" face="Arial"><b>
                          <fmt:message key="email.confirmation.cc.content.2">
                            <fmt:param>
                              ${delayTime}${displayDelayUnit}
                            </fmt:param>
                            <fmt:param>
                              <a title="Horaires et coordonnées" href="${storeUrl}"><font face="Arial" color="#09438b">${storeName}</font></a>
                            </fmt:param>
                          </fmt:message>
                        </b></font></p>
                        
                        <c:if test="${not empty storePhoneNumber}">
                          <p><font size="2" face="Arial">
                            <fmt:message key="email.confirmation.cc.content.3">
                              <fmt:param>
                                ${storePhoneNumber}
                              </fmt:param>
                            </fmt:message>
                          </font></p>
                        </c:if>
                        
                        <p><font size="2" face="Arial">
                          <fmt:message key="email.signature.soon.on">
                            <fmt:param>
                              <a title="Castorama.fr" href="${httpLink}"><font face="Arial" color="#09438b">www.castorama.fr</font></a>
                            </fmt:param>
                          </fmt:message>
                        </font></p>
                        
                        <p><font size="2" face="Arial"><p>
                          <fmt:message key="email.signature.customer.service">
                            <fmt:param>
                              <br/><a href="${httpLink}">www.castorama.fr</a>
                            </fmt:param>
                          </fmt:message>
                        </p></font></p>
            
                        <h1 style="font-family: arial; color: #4B4B4D; font-size: 20px; font-weight: normal">
                          <fmt:message key="email.confirmation.cc.reminder"><fmt:param>${orderId}</fmt:param></fmt:message>
                        </h1>
                        <table width="100%" cellspacing="0" cellpadding="0" border="0">
                          <tbody>
                            <tr>
                              <td valign="top" width="280">
                                <table width="100%" cellspacing="0" cellpadding="10" border="0" style="border: 1px solid #c7c7c7">
                                  <tbody>
                                    <tr>
                                      <td>
                                        <dsp:getvalueof var="prefix" param="order.paymentGroups[0].billingAddress.prefix" />
                                        <dsp:getvalueof var="firstName" param="order.paymentGroups[0].billingAddress.firstName" />
                                        <dsp:getvalueof var="lastName" param="order.paymentGroups[0].billingAddress.lastName" />
                                        <dsp:getvalueof var="address1" param="order.paymentGroups[0].billingAddress.address1" />
                                        <dsp:getvalueof var="address2" param="order.paymentGroups[0].billingAddress.address2" />
                                        <dsp:getvalueof var="address3" param="order.paymentGroups[0].billingAddress.address3" />
                                        <dsp:getvalueof var="postalCode" param="order.paymentGroups[0].billingAddress.postalCode" />
                                        <dsp:getvalueof var="city" param="order.paymentGroups[0].billingAddress.city" />
                                        <dsp:getvalueof var="phoneNumber" param="order.paymentGroups[0].billingAddress.phoneNumber" />
                                        <dsp:getvalueof var="locality" param="order.paymentGroups[0].billingAddress.locality" />
                                        <dsp:getvalueof var="phoneNumber2" param="order.paymentGroups[0].billingAddress.phoneNumber2" />
                
                                        <h2 style="margin: 0"><font size="4" face="Arial" color="#003f8c"><b><fmt:message key="msg.delivery.primary" /></b></font></h2>
                
                                        <p style="margin-bottom: 0">
                                          <font size="2" face="Arial" color="#4b4c4c">
                                            <b>
                                              <c:choose>
                                                <c:when test="${'miss' == prefix}">
                                                  <fmt:message key="msg.address.prefix.miss" />&nbsp;
                                                </c:when>
                                                <c:when test="${'mrs' == prefix}">
                                                  <fmt:message key="msg.address.prefix.mrs" />&nbsp;
                                                </c:when>
                                                <c:when test="${'mr' == prefix}">
                                                  <fmt:message key="msg.address.prefix.mr" />&nbsp;
                                                </c:when>
                                              </c:choose>${firstName}&nbsp;${lastName}<br />
                                              ${address1}<br />
                                              <c:if test="${not empty address2}">${address2}<br /></c:if>
                                              <c:if test="${not empty address3}">${address3}<br /></c:if>
                                              <c:if test="${not empty locality}">${locality}<br /></c:if>
                                              ${postalCode}&nbsp;${city}<br />
                                              <fmt:message key="msg.address.phone"/> : ${phoneNumber}
                                            </b>
                                          </font>
                                        </p>
                                      </td>
                                    </tr>
                                  </tbody>
                                </table>
                              </td>
                              <td width="10">&nbsp;</td>
                              <td valign="top" width="280">
                                <table width="100%" cellspacing="0" cellpadding="10" border="0" style="border: 1px solid #c7c7c7">
                                  <tbody>
                                    <tr>
                                      <td>
                                        <h2 style="margin: 0"><font size="4" face="Arial" color="#003f8c"><b><fmt:message key="confirmation.order.payment"/></b></font></h2>
                                        
                                        <dsp:getvalueof var="paymentMethod" param="order.paymentGroups[0].paymentMethod" />
                                        <dsp:getvalueof var="orderStateBO" param="order.BOState" />
                                        <dsp:getvalueof var="orderTotal" param="order.priceInfo.rawSubTotal" />
                                        <dsp:getvalueof var="orderTotalCommande" param="order.priceInfo.total" />
                                        
                                        <p style="margin-bottom: 0">
                                          <font size="2" face="Arial" color="#4b4c4c">
                                            <b>
                                              <dsp:droplet name="/com/castorama/droplet/OrderPaymentDroplet" >
                                                <dsp:param name="paymentMethod" value="${paymentMethod}"/>
                                                <dsp:param name="orderId" value="${orderId}" />
                                                <dsp:oparam name="output">
                                                  <dsp:getvalueof var="paymentAmount" param="paymentAmount" />
                                                  <dsp:valueof param="paymentType" /> : <span class="blue">
                                                  <c:choose>
                                                    <c:when test="${null == paymentAmount}" >
                                                      <c:choose>
                                                        <c:when test="${'VALIDE' == orderStateBO}">
                                                          <font color="#003f8c"><dsp:valueof value="${orderTotal}" converter="euro" locale="fr_FR"/></font>
                                                        </c:when>
                                                        <c:otherwise>
                                                          <dsp:valueof param="paymentDescription" />
                                                        </c:otherwise>
                                                      </c:choose>
                                                    </c:when>
                                                    <c:otherwise>
                                                      <font color="#003f8c"><dsp:valueof param="paymentAmount" converter="euro" locale="fr_FR"/></font>
                                                    </c:otherwise>
                                                   </c:choose>
                                                   </span><br/>
                                                </dsp:oparam>
                                              </dsp:droplet>
                                              <br>
                                              <fmt:message key="confirmation.order.payment.total"/> :<font color="#003f8c">&nbsp;<dsp:valueof value="${orderTotalCommande}" converter="euro" locale="fr_FR"/></font>
                                            </b>
                                          </font>
                                        </p>
                                      </td>
                                    </tr>
                                  </tbody>
                                </table>
                              </td>
                            </tr>
                          </tbody>
                        </table>
                        
                        <br>
                        
                        <table width="100%" cellspacing="0" cellpadding="10" border="0">
                          <tbody>
                            <tr>
                              <th bgcolor="#c7c7c7" align="left">
                                <font size="1" face="Arial" color="#ffffff">
                                  <fmt:message key="email.confirmation.cc.summary.product"/>
                                </font>
                              </th>
                              <th bgcolor="#c7c7c7">
                                <font size="1" face="Arial" color="#ffffff">
                                  <fmt:message key="email.confirmation.cc.summary.desc"/>
                                </font>
                              </th>
                              <th bgcolor="#c7c7c7">
                                <font size="1" face="Arial" color="#ffffff">
                                  <fmt:message key="email.confirmation.cc.summary.quantity"/>
                                </font>
                              </th>
                              <th bgcolor="#c7c7c7" align="right" width="70">
                                <font size="1" face="Arial" color="#ffffff">
                                  <fmt:message key="email.confirmation.cc.summary.price"/>
                                </font>
                              </th>
                            </tr>
                            <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                              <dsp:param name="array" param="order.commerceItems"/>
                              <dsp:param name="elementName" value="ci"/>
                              <dsp:oparam name="output">
                                <tr background="${httpServer}/images/email/castorama-email-cc-bgTable.gif" style="border-bottom: 1px solid #c7c7c7">
                                  <dsp:droplet name="/com/castorama/droplet/CastLookupDroplet">
                                    <dsp:param name="id" param="ci.catalogRefId"/>
                                    <dsp:param name="elementName" value="sku"/>
                                    <dsp:param name="itemDescriptor" value="casto_sku"/>
                                    <dsp:param name="repository" bean="/atg/commerce/catalog/ProductCatalog"/>
                                    <dsp:oparam name="output">
                                      <td>
                                        <font size="1" face="Arial"><i><dsp:valueof param="sku.codeArticle"/></i></font>
                                      </td>
                                      <td>
                                        <font size="2" face="Arial"><b>
                                          <dsp:getvalueof var="productName" param="sku.displayName"/>
                                          <%@ include file="includes/productName.jspf" %>
                                        </b></font>
                                      </td>
                                     </dsp:oparam>
                                  </dsp:droplet> 
                                  
                                  <td align="center">
                                    <font size="2" face="Arial"><dsp:valueof param="ci.quantity"/></font>
                                  </td>
                                  
                                  <td align="right">
                                    <font size="2" face="Arial" color="#8c1730"><b>
                                      <dsp:getvalueof var="itemAmount"   param="ci.priceInfo.amount"/>
                                      <dsp:getvalueof var="itemQuantity" param="ci.quantity"/>
                                      <dsp:getvalueof var="itemPrice"    value="${itemAmount/itemQuantity}"/>
                                      <dsp:valueof value="${itemPrice}" converter="euro" locale="fr_FR"/> 
                                    </b></font>
                                  </td>
                                </tr>
                              </dsp:oparam>
                            </dsp:droplet>
                            <tr background="${httpServer}/images/email/castorama-email-cc-bgTable.gif" style="border-bottom: 1px solid #c7c7c7">
                              <td colspan="3"><font size="2" face="Arial"><b><fmt:message key="email.confirmation.cc.summary.total"/></b></font></td>
                              <td align="right">
                                <font size="2" face="Arial" color="#8c1730"><b>
                                  <dsp:valueof value="${orderTotal}" converter="euro" locale="fr_FR"/>
                                </b></font>
                              </td>
                            </tr>
                          </tbody>
                        </table>
                        <p align="justify">
                          <font size="2" face="Arial">
                            <fmt:message key="email.confirmation.cc.info.general.conditions">
                              <fmt:param>
                                <a href="${httpLink}/contactUs/legal-notice.jsp">
                                  <font face="Arial" color="#09438b"><fmt:message key="email.link.click.here.hyphen"/></font>
                                </a>
                              </fmt:param>
                            </fmt:message>
                          </font>
                        </p>
						<p align="justify" >
							<font size="2" face="Arial"><fmt:message key="email.confirmation.cc.lh"/></font>
						</p>
						<br/>
                      </td>
                    </tr>
                    <dsp:getvalueof var="staticContentPath" bean="/com/castorama/CastConfiguration.staticContentPath" />
                    <c:import url="${staticContentPath}/html/cc_email_confirmation.html" charEncoding="iso-8859-1"/>
                  </tbody>
                </table>
              </td>
              <td width="10">
                &nbsp;
              </td>
            </tr>
            <tr>
              <td width="10">
                <font size="1">&nbsp;</font>
              </td>
            </tr>
          </tbody>
        </table>
      </jsp:attribute>
    </cast:emailPageContainer>
  </dsp:page>
