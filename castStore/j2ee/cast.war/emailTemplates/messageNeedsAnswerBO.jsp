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
    
    <dsp:getvalueof var="orderId" param="orderId"/>
    <dsp:getvalueof var="orderDate" param="orderDate" vartype="java.util.Date"/>  
    <dsp:getvalueof var="message" param="message"/>
    
    <table width="580" cellspacing="0" cellpadding="0" align="center">
      <tr>
        <td>
          <br />
          <font size="2" face="Arial" color="#000000">
          <font size="3" face="Arial" color="#09438b"><b>
            <fmt:message key="email.message.needs.answer.title">
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
            <fmt:message key="email.message.common.order.info">
              <fmt:param>${orderId}</fmt:param>
            </fmt:message>
          </p>
          
          <hr style="width:100%; height :1px; color : #e2e2e2;">
          <p><b>
            <fmt:message key="email.message.common.service.message">
              <fmt:param><fmt:formatDate value="${orderDate }" type="date" dateStyle="long"/></fmt:param>
            </fmt:message>
          </b></p>
          
          <p><font color="#000000" face="Arial">${message}</font></p>
          <hr style="width:100%; height :1px; color : #e2e2e2;">
                               
          <p><br /><b><fmt:message key="email.message.needs.answer.please.reply"/></b><br />
            <fmt:message key="email.message.needs.answer.access.message">
              <fmt:param>
                <a href="${httpLink }/user/myProfile.jsp"><font face="Arial" color="#09438b">
                  <fmt:message key="email.link.click.here"/>
                </font></a>
              </fmt:param>
            </fmt:message>
          
          <p><fmt:message key="email.message.common.remaining.disposal"/></p>
            
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
