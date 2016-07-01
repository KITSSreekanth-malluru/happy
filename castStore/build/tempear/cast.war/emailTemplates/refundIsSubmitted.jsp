<%@ page contentType="text/html; charset=UTF-8"%>
<dsp:page>
  <dsp:importbean var="dynamoConfig" bean="/atg/dynamo/Configuration" />
  <dsp:importbean var="originatingRequest" bean="/OriginatingRequest" />

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
      
      <dsp:getvalueof var="refundAmount" param="refundAmount"/>
      <dsp:getvalueof var="refundDate" param="refundDate"/>  
      <dsp:getvalueof var="refundMode" param="refundMode" />

       <table width="580" cellspacing="0" cellpadding="0" align="center">
            <tr>
                <td>
                  <br />
                  <font face="Arial" color="#000000" size="2">
                    <font size="3" face="Arial" color="#09438b"><b>
                      <fmt:message key="email.refundIsSubmitted.title">
                        <fmt:param>${orderId}</fmt:param>
                      </fmt:message>
                    </b></font>
                    <br />
                    <p>
                      <fmt:message key="email.common.client.code">
                        <fmt:param>${userId}</fmt:param>
                      </fmt:message>
                    
                    <%@ include file="/emailTemplates/includes/commandeHeader.jspf" %>
                    
                    <p>
                      <fmt:message key="email.refundIsSubmitted.text">
                        <fmt:param>${refundAmount}</fmt:param>
                        <fmt:param>
                          <c:if test="${not empty refundDate}"> le <fmt:formatDate value="${refundDate}" pattern="dd/MM/yyyy" /></c:if>
                        </fmt:param>
                        <fmt:param>${orderId}</fmt:param>
                      </fmt:message>
                    </p>
                    
                    <c:choose>
                        <c:when test="${3 == refundMode}">
                            <p>
                              <fmt:message key="email.refundIsSubmitted.refund.by">
                                <fmt:param><fmt:message key="email.refundIsSubmitted.refund.type.check"/></fmt:param>
                              </fmt:message>
                            </p>
                        </c:when>
                        <c:when test="${1 == refundMode}">
                            <p>
                              <fmt:message key="email.refundIsSubmitted.refund.by">
                                <fmt:param><fmt:message key="email.refundIsSubmitted.refund.type.cb"/></fmt:param>
                              </fmt:message>
                            </p>
                            <p><fmt:message key="email.refundIsSubmitted.cb.attention"/></p>
                        </c:when>
                        <c:when test="${2 == refundMode}">
                            <p>
                              <fmt:message key="email.refundIsSubmitted.refund.by">
                                <fmt:param><fmt:message key="email.refundIsSubmitted.refund.type.cast.card"/></fmt:param>
                              </fmt:message>
                            </p>
                        </c:when>
                    </c:choose>
                    
                    <p>
                      <fmt:message key="email.refundIsSubmitted.thanks.trust">
                        <fmt:param>
                          <a href="${httpLink}"><font face="Arial" color="#09438b">www.castorama.fr</font></a>
                        </fmt:param>
                      </fmt:message>
                    </p>
                    
                    <p><fmt:message key="email.signature.cordially"/></p>
                    <p>
                      <fmt:message key="email.signature.customer.service">
                        <fmt:param><br/><a href="${httpLink}">www.castorama.fr</a></fmt:param>
                      </fmt:message>
                    </p>
                    
                    <%@ include file="/emailTemplates/includes/confirmarionCommandeFAQSection.jspf" %>  
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
