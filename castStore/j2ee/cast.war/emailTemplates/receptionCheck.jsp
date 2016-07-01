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
      
        <table width="580" cellspacing="0" cellpadding="0" align="center">
            <tr>
                <td>
                  <br />
                  <font size="2" face="Arial" color="#000000">
                    <font size="3" face="Arial" color="#09438b"><b>
                      <fmt:message key="email.receptionCheck.title">
                        <fmt:param>${orderId}</fmt:param>
                      </fmt:message>
                    </b></font>
                    <br/>
                    <p><br />
                      <fmt:message key="email.common.client.code">
                        <fmt:param>${userId}</fmt:param>
                      </fmt:message>
                    </p>
                    
                    <%@ include file="/emailTemplates/includes/commandeHeader.jspf" %>
                    
                    <p><fmt:message key="email.receptionCheck.text.1"/></p>
                    
                    <p><fmt:message key="email.receptionCheck.text.2"/></p>
                    
                    <p><strong>
                      <fmt:message key="email.confirmation.common.order.status">
                        <fmt:param>
                          <a href="${httpLink }/user/clientSpaceHome.jsp"><font face="Arial" color="#09438b">
                            <fmt:message key="email.link.clientspace"/>
                          </font></a>
                        </fmt:param>
                      </fmt:message>
                    </strong></p>
                    
                    <p><fmt:message key="email.confirmation.common.notification"/></p>
                    
                    <p><fmt:message key="email.confirmation.common.thanks"/></p>
                      
                    <p>
                      <fmt:message key="email.signature.soon.on">
                        <fmt:param>
                          <a href="${httpLink }"><font face="Arial" color="#09438b">www.castorama.fr</font></a>
                        </fmt:param>
                      </fmt:message>
                    </p>
                    <p><fmt:message key="email.signature.cordially"/></p>
                    <p>
                      <fmt:message key="email.signature.customer.service">
                        <fmt:param><br/><a href="${httpLink}">www.castorama.fr</a></fmt:param>
                      </fmt:message>
                    </p>
                    
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
