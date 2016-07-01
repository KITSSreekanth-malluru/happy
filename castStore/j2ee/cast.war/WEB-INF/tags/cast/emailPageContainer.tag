<%--
  displayHeader (optional) - Specify whether to render/not-render the common email header 
                       in the email content (Boolean, default-true)
  displayFooter (optional) - Specify whether to render/not-render the common email footer 
                       in the email content (Boolean, default-true)

  messageSubjectKey (optional) - Resource Key for the string to be used as Subject of the email
  messageSubjectString (optional) - String to be used as Subject of the email
  messageFromAddressString (optional) - Email address to be set as Sender's address for the email

  The tag accepts the following fragments
  messageBody - define a message content.
  disclaimerBody - define a disclaimer content.
--%>

<%@ include file="/includes/taglibs.jspf" %>
<%@ include file="/includes/context.jspf" %>

<%@ tag language="java" %>

<%@ attribute name="headerAlign"%>
<%@ attribute name="displayHeader" type="java.lang.Boolean" %>
<%@ attribute name="displayFooter" type="java.lang.Boolean" %>
<%@ attribute name="messageSubjectKey" %>
<%@ attribute name="messageSubjectString" %>
<%@ attribute name="messageFromAddressString" %>
<%@ attribute name="messageBody" fragment="true" %>
<%@ attribute name="disclaimerBody" fragment="true" %>

<dsp:page>
  <dsp:importbean var="dynamoConfig" bean="/atg/dynamo/Configuration"/>
  <dsp:importbean var="originatingRequest" bean="/OriginatingRequest"/>

  <dsp:getvalueof var="serverName" vartype="java.lang.String" value="${dynamoConfig.siteHttpServerName}"/>
  <dsp:getvalueof var="serverPort" vartype="java.lang.String" value="${dynamoConfig.siteHttpServerPort}"/>
  <dsp:getvalueof var="httpServer" vartype="java.lang.String" value="http://${serverName}:${serverPort}"/>
  <dsp:getvalueof var="contextPath" vartype="java.lang.String" value="${originatingRequest.contextPath}"/>
  <dsp:getvalueof var="httpLink" vartype="java.lang.String" value="${httpServer}${contextPath}"/>
  

  <c:if test="${!empty messageSubjectKey}">
    <fmt:message var="value" key="${messageSubjectKey}"/>
    <dsp:setvalue param="messageSubject" value="${value}"/>
  </c:if>

  <c:if test="${!empty messageSubjectString}">
    <dsp:setvalue param="messageSubject" value="${messageSubjectString}"/>
  </c:if>


  <c:if test="${!empty messageFromAddressString}">
    <dsp:setvalue param="messageFrom" beanvalue="${messageFromAddressString}"/>
  </c:if>
  <c:if test="${empty headerAlign}">
    <c:set var="headerAlign" value="center"/>
  </c:if>
  
  <body link="#000000" marginheight="0" marginwidth="0" topmargin="0" leftmargin="0">
    <table width="100%" align="center" cellpadding="0" cellspacing="0" style="border-collapse: collapse;" background="${httpLink}/images/email/castorama-email-background.gif">
      <tbody>
        <tr>
          <td align="center">
            <table width="600" cellspacing="0" cellpadding="0" bgcolor="#ffffff">
              <c:if test="${empty displayHeader || displayHeader}">
                <tr>
                  <td><img src="${httpServer}/images/email/castorama-email-header.gif" alt="" width="600" height="65" border="0" /></td>
                </tr>
              </c:if>
              <tr>
                <td align="${headerAlign }">
                  <jsp:invoke fragment="messageBody"/>
                </td>
              </tr>     
              <%-- Show the footer if requested. --%>
              <c:if test="${empty displayFooter || displayFooter}">
                <tr>
                  <td><img src="${httpServer}/images/email/castorama-email-footer.gif" alt="" width="600" height="65" border="0" /></td>
                </tr>
                <jsp:invoke fragment="disclaimerBody"/>
                <tr>
                  <td align="center" height="30"><a href="${httpLink}"><font face="Arial" size="2" color="#09438b">www.castorama.fr</font></a></td>
                </tr>     
              </c:if>
            </table>
          </td>
        </tr>
      </tbody>
    </table>
 </body>
</dsp:page>