<%@ page contentType="text/html; charset=UTF-8"%>
<dsp:page>

  <dsp:importbean var="dynamoConfig" bean="/atg/dynamo/Configuration"/>
  <dsp:importbean var="originatingRequest" bean="/OriginatingRequest"/>

  <dsp:getvalueof var="serverName" vartype="java.lang.String" value="${dynamoConfig.siteHttpServerName}"/>
  <dsp:getvalueof var="serverPort" vartype="java.lang.String" value="${dynamoConfig.siteHttpServerPort}"/>
  <dsp:getvalueof var="httpServer" vartype="java.lang.String" value="http://${serverName}:${serverPort}"/>
  <dsp:getvalueof var="contextPath" vartype="java.lang.String" value="${originatingRequest.contextPath}"/>
  <dsp:getvalueof var="httpLink" vartype="java.lang.String" value="${httpServer}${contextPath}"/>
  
  <dsp:getvalueof var="civilite" param="civilite"/>
  <dsp:getvalueof var="nom" param="nom"/>
  <dsp:getvalueof var="prenom" param="prenom"/>
  <dsp:getvalueof var="email" param="email"/>
  <dsp:getvalueof var="adresse" param="adresse"/>
  <dsp:getvalueof var="codePostal" param="codePostal"/>
  <dsp:getvalueof var="ville" param="ville"/>
  <dsp:getvalueof var="telephone" param="telephone"/>
  <dsp:getvalueof var="motif" param="motif"/> 
  <dsp:getvalueof var="message" param="message"/>
  
  <cast:emailPageContainer headerAlign="left" displayHeader="true" displayFooter="true">
    <jsp:attribute name="messageBody">
            <font size="2" face="Arial" color="#000000">
                <font size="3" face="Arial" color="#09438b"><b><fmt:message key="email.sendOrder.receiving.request.for.advice"/></b></font>
                <br />
                <p><br /><fmt:message key="email.sendOrder.request.for.advice.created"/></p>
                <table align="left">
                    <tr>
                      <td><fmt:message key="email.sendOrder.motif"/></td>
                      <td>${motif}</td>
                    </tr>
                    <tr>
                      <td><fmt:message key="email.sendOrder.civilite"/></td>
                      <td>
                        <dsp:droplet name="/atg/dynamo/droplet/Switch">
                          <dsp:param name="value" value="${civilite }"/>
                          <dsp:oparam name="miss">
                            <fmt:message key="msg.address.prefix.full.miss" />
                          </dsp:oparam>
                          <dsp:oparam name="mrs">
                            <fmt:message key="msg.address.prefix.full.mrs" />
                          </dsp:oparam>
                          <dsp:oparam name="mr">
                            <fmt:message key="msg.address.prefix.full.mr" />
                          </dsp:oparam>
                        </dsp:droplet>
                      </td>
                    </tr>
                    <tr>
                      <td><fmt:message key="email.sendOrder.nom"/></td>
                      <td>${nom}</td>
                    </tr>
                    <tr>
                      <td><fmt:message key="email.sendOrder.prenom"/></td>
                      <td>${prenom}</td>
                    </tr>
                    <tr>
                      <td><fmt:message key="email.sendOrder.email"/></td>
                      <td>${email}</td>
                    </tr>
                    <tr>
                      <td><fmt:message key="email.sendOrder.address"/></td>
                      <td>${adresse}</td>
                    </tr>
                    <tr>
                      <td><fmt:message key="email.sendOrder.city"/></td>
                      <td>${ville}</td>
                    </tr>
                    <tr>
                      <td><fmt:message key="email.sendOrder.zip"/></td>
                      <td>${codePostal}</td>
                    </tr>
                    <tr>
                      <td><fmt:message key="email.sendOrder.phone"/></td>
                      <td>${telephone}</td>
                    </tr>
                    <c:if test="${not empty productId }">
                    <tr>
                      <td><fmt:message key="email.sendOrder.ref"/></td>
                      <td>${productId}</td>
                    </tr>
                    </c:if>
                    <c:if test="${not empty store }">
                      <tr>
                        <td><fmt:message key="email.sendOrder.store"/></td>
                        <td>${store}</td>
                      </tr>
                    </c:if>
                    <tr>
                      <td><fmt:message key="email.sendOrder.question"/></td>
                      <td>${message}</td>
                    </tr>
                </table>
            </font> 
    </jsp:attribute>
  </cast:emailPageContainer>
  
</dsp:page>