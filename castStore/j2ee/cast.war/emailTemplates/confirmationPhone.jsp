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

      <table width="580" cellspacing="0" cellpadding="0">
        <tr>
          <td align="left">
            <%@ include file="/emailTemplates/includes/confirmationCommandeHeader.jspf" %>
            <p><strong><fmt:message key="email.confirmation.phone.validate.order.attention"/></strong></p>
            <p><fmt:message key="email.confirmation.phone.work.time"/></p>
              
            <p><fmt:message key="email.confirmation.common.thanks"/></p>
            
            <p>
              <fmt:message key="email.signature.soon.on">
                <fmt:param><a href="${httpLink}"><font face="Arial" color="#09438b">www.castorama.fr</font></a></fmt:param>
              </fmt:message>
            </p> 
            
            <p>
              <fmt:message key="email.signature.customer.service">
                <fmt:param><br/><a href="${httpLink}">www.castorama.fr</a></fmt:param>
              </fmt:message>
            </p>
            
            <hr style="width:100%; height :1px; color : #e2e2e2;">
            <p><strong><fmt:message key="email.confirmation.common.summary.title"/></strong></p>
            <p>
              <fmt:message key="email.confirmation.common.summary.order.num">
                <fmt:param>${orderId}</fmt:param>
              </fmt:message>
              <br />
              <fmt:message key="email.confirmation.common.summary.payment.mode">
                <fmt:param><fmt:message key="email.confirmation.common.summary.payment.mode.phone"/></fmt:param>
              </fmt:message>
            </p>
            
            <%@ include file="/emailTemplates/includes/confirmarionShoppingCartInfo.jspf" %>
            
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
