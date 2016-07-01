<%@ page contentType="text/html; charset=UTF-8"%>
<dsp:page>

  <dsp:importbean var="dynamoConfig" bean="/atg/dynamo/Configuration"/>
  <dsp:importbean var="originatingRequest" bean="/OriginatingRequest"/>

  <dsp:getvalueof var="serverName" vartype="java.lang.String" value="${dynamoConfig.siteHttpServerName}"/>
  <dsp:getvalueof var="serverPort" vartype="java.lang.String" value="${dynamoConfig.siteHttpServerPort}"/>
  <dsp:getvalueof var="httpServer" vartype="java.lang.String" value="http://${serverName}:${serverPort}"/>
  <dsp:getvalueof var="contextPath" vartype="java.lang.String" value="${originatingRequest.contextPath}"/>
  <dsp:getvalueof var="httpLink" vartype="java.lang.String" value="${httpServer}${contextPath}"/>
  
  <cast:emailPageContainer messageSubjectKey="forgot.password.mail.subject">

    <jsp:attribute name="messageBody">
      <dsp:getvalueof var="prefix" param="civilite"/>
      <dsp:getvalueof var="var_lastName" param="lastName"/>
      <dsp:getvalueof var="var_email" param="email"/>
      <dsp:getvalueof var="var_link" param="link"/>

      <c:choose>
        <c:when test="${'miss' == prefix}">
          <dsp:getvalueof var="var_civilite" value="msg.address.prefix.miss"/>
        </c:when>
        <c:when test="${'mrs' == prefix}">
          <dsp:getvalueof var="var_civilite" value="msg.address.prefix.mrs"/>
        </c:when>
        <c:when test="${'mr' == prefix}">
         <dsp:getvalueof var="var_civilite" value="msg.address.prefix.mr"/>
        </c:when>
      </c:choose>

      <table width="580" cellspacing="0" cellpadding="0" align="center">
        <tr>
          <td>
            <br />
            <font size="2" face="Arial" color="#000000">
              <b><fmt:message key="forgot.password.mail.title" /></b>
              <br />
            </font>

            <p><font color="#000000" size="2" face="Arial"><fmt:message key="forgot.password.mail.bonjour" />&nbsp;</font><font size="2" face="Arial"><fmt:message key="${var_civilite}" />&nbsp;${var_lastName}</font><font color="#000000" size="2" face="Arial">,</font></p>

            <p><font color="#000000" size="2" face="Arial"><fmt:message key="forgot.password.mail.string1" /></font></p>

            <font size="2" face="Arial" color="#000000">
              <p><fmt:message key="forgot.password.mail.password" />&nbsp;: <a href="${httpLink}/user/checkLink.jsp?key=${var_link}">https://castorama.fr/store/user/checkLink.jsp?key=${var_link}</a></p>
              <hr style="width:100%; height :1px; color : #e2e2e2;">

              <p><br/><fmt:message key="forgot.password.mail.footer1" />
              <fmt:message key="forgot.password.mail.footer2" /></p>
                
              <p><fmt:message key="forgot.password.mail.site" />&nbsp;<a href="${httpLink}"><font color="#09438b">www.castorama.fr</font></a> !</p>
              <p><fmt:message key="forgot.password.mail.merci" />,</p>
              <p><fmt:message key="forgot.password.mail.command" />&nbsp;www.castorama.fr</p>
            </font>
          </td>
        </tr>
        <tr>
          <td height="20"></td>
        </tr>
      </table>

    </jsp:attribute>

  </cast:emailPageContainer>

</dsp:page>
