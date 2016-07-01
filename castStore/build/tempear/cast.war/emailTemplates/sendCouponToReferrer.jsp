<%@ page contentType="text/html; charset=UTF-8"%>
<dsp:page>
  <dsp:importbean var="dynamoConfig" bean="/atg/dynamo/Configuration" />
  <dsp:importbean var="originatingRequest" bean="/OriginatingRequest" />
  <dsp:importbean bean="/atg/commerce/catalog/ProductLookup" />
  <dsp:importbean bean="/com/castorama/droplet/CastProductLinkDroplet" />

  <dsp:getvalueof var="serverName" vartype="java.lang.String" value="${dynamoConfig.siteHttpServerName}" />
  <dsp:getvalueof var="serverPort" vartype="java.lang.String" value="${dynamoConfig.siteHttpServerPort}" />
  <dsp:getvalueof var="httpServer" vartype="java.lang.String" value="http://${serverName}:${serverPort}" />
  <dsp:getvalueof var="contextPath" vartype="java.lang.String" value="${originatingRequest.contextPath}" />
  <dsp:getvalueof var="httpLink" vartype="java.lang.String" value="${httpServer}${contextPath}" />

  <cast:emailPageContainer>
  
  
  
  <jsp:attribute name="messageBody">
    <dsp:getvalueof var="referrer" param="referrer" />
    <dsp:getvalueof var="referee" param="referee" /> 
    <dsp:getvalueof var="coupon" param="coupon" />
    <dsp:getvalueof var="promoAmount" param="promoAmount" />
    <dsp:getvalueof var="orderLimit" param="orderLimit" />
    
    <table width="580" cellspacing="0" cellpadding="0">
      <tr>
        <td align="left">
          <img src="${httpServer}/images/email/castorama-email-image-top.gif" alt="" border="0" /> 
          <font size="2" face="Arial" color="#323232">
            <br />
            <p>
              <br />
              <fmt:message key="email.common.hello">
                <fmt:param>${referrer}</fmt:param>
              </fmt:message>
            </p>
          </font>
          <p>
            <font color="#323232" size="2" face="Arial">
              <fmt:message key="sendReferrer.line1">
                <fmt:param>${referee}</fmt:param>
                <fmt:param>
                  <a href="${httpLink}"><font color="#09438b">castorama.fr</font></a>
                </fmt:param>
              </fmt:message>
            </font>
          </p>  
          <font size="2" face="Arial" color="#323232">
            <p><fmt:message key="sendReferrer.line2"/></p>
            <p><img src="${httpServer}/images/email/castorama-email-parrain-recompense.gif" alt="" border="0" /></p>
            <p>
              <fmt:message key="sendReferrer.line3">
                <fmt:param>
                  <span style="font-weight: bold; color : #FF0000">${coupon}</span>
                </fmt:param>
              </fmt:message>
            </p>
            <p>
              <fmt:message key="sendReferrer.line4">
                <fmt:param>
                  <a href="${httpLink}"><font color="#09438b">castorama.fr</font></a>
                </fmt:param>
              </fmt:message>
            </p>
            <p align="right"><fmt:message key="sendReferee.line6" /></p>
            <p align="center">
              <a href="${httpLink}" title=""><img src="${httpServer}/images/email/castorama-email-image-bottom.gif" alt="" border="0" />
              </a>
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
    <tr>
      <td align="justify">
        <table width="580" cellspacing="0" cellpadding="0" align="center">
          <tbody>
            <tr>
              <td align="justify">
                <font face="arial" color="828e9f" size="1">
                  <fmt:message key="email.footerMsg" /> 
                </font>
              </td>
            </tr>
          </tbody>
        </table>
      </td>
    </tr>
  </jsp:attribute>
  </cast:emailPageContainer>
</dsp:page>
