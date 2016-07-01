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
            <font size="3" face="Arial" color="#09438b">
              <b>
                <fmt:message key="email.receptionNoCheck.text.1">
                  <fmt:param>${orderId}</fmt:param>
                </fmt:message>
              </b>
            </font>
            <br />
            <p>
              <fmt:message key="email.common.client.code">
                <fmt:param>${userId}</fmt:param>
              </fmt:message>
            </p>
            
            <%@ include file="/emailTemplates/includes/commandeHeader.jspf" %>
            
            <p>
              <fmt:message key="email.receptionNoCheck.text.3">
                <fmt:param><dsp:valueof param="submittedDate" date="dd MMMM yyyy" locale="fr_FR"/></fmt:param>
              </fmt:message>
            </p>
            
            <p><fmt:message key="email.receptionNoCheck.text.4"/></p>
            
            <p>
              <fmt:message key="email.receptionNoCheck.text.5">
                <fmt:param>
                  ${orderId}
                </fmt:param>
                <fmt:param>
                  <dsp:valueof param="totalPrice" number="#.00"/>
                </fmt:param>
              </fmt:message>
            </p>
            
            <p><fmt:message key="email.receptionNoCheck.text.6"/></p>
            <p><fmt:message key="email.receptionNoCheck.customer.address"/></p>
            
            <p><fmt:message key="email.receptionNoCheck.text.7"><fmt:param>${orderId}</fmt:param></fmt:message></p>
            
            <p><fmt:message key="email.receptionNoCheck.upon.payment"/></p>
            <ul>
              <li><fmt:message key="email.receptionNoCheck.upon.payment.1"/></li>
              <li>
                <fmt:message key="email.receptionNoCheck.upon.payment.2">
                  <fmt:param>
                    <a href="${httpLink }/user/clientSpaceHome.jsp"><font face="Arial" color="#09438b"><fmt:message key="email.link.clientspace"/></font></a>
                  </fmt:param>
                </fmt:message>
              </li>
            </ul>
            
            <p><fmt:message key="email.receptionNoCheck.text.8"/></p>
              
            <p><fmt:message key="email.receptionNoCheck.good.to.know"/></p>
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
