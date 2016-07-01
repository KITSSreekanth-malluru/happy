<%@ page contentType="text/html; charset=UTF-8"%>
<dsp:page>

  <dsp:importbean var="dynamoConfig" bean="/atg/dynamo/Configuration" />
  <dsp:importbean var="originatingRequest" bean="/OriginatingRequest" />

  <dsp:getvalueof var="serverName" vartype="java.lang.String" value="${dynamoConfig.siteHttpServerName}"/>
  <dsp:getvalueof var="serverPort" vartype="java.lang.String" value="${dynamoConfig.siteHttpServerPort}"/>
  <dsp:getvalueof var="httpServer" vartype="java.lang.String" value="http://${serverName}:${serverPort}"/>
  <dsp:getvalueof var="contextPath" vartype="java.lang.String" value="${originatingRequest.contextPath}"/>
  <dsp:getvalueof var="httpLink" vartype="java.lang.String" value="${httpServer}${contextPath}"/>
  
  <cast:emailPageContainer messageSubjectKey="email.a.friend.subject">
    <jsp:attribute name="messageBody">
        <dsp:getvalueof var="var_to" param="to"/>
        <dsp:getvalueof var="var_from" param="from"/>
        <dsp:getvalueof var="var_productId" param="productId"/>
        <table width="580" cellspacing="0" cellpadding="0" align="center">
            <tr>
              <td><br />
              <font size="2" face="Arial" color="#000000">
              <table cellpadding="0" cellspacing="0">
                <tbody>
                  <dsp:droplet name="/atg/commerce/catalog/ProductLookup">
                    <dsp:param name="id" param="productId"/>
                    <dsp:oparam name="output">
                      <dsp:getvalueof var="smallImage" param="element.smallImage.url" />
                      <tr>
                        <td>
                            <c:if test="${not empty smallImage}">
                                <dsp:img src="${httpServer}${smallImage}" width="140" height="140" alt="${name}" title="${name}" />
                            </c:if> 
                        </td>
                        <td width="20">&nbsp;</td>
                        <td valign="middle"><font size="3" face="Arial" color="#09438b"><b><dsp:valueof param="element.displayName"/> </b></font></td>
                      </tr>
                    </dsp:oparam>
                  </dsp:droplet>
                </tbody>
              </table>
              <br />
              <p>
                <fmt:message key="email.common.hello">
                  <fmt:param>${var_to}</fmt:param>
                </fmt:message>
              </p>

              <p>
                <fmt:message key="email.a.friend.offer.product">
                  <fmt:param>${var_from}</fmt:param>
                  <fmt:param>
                    <a href="${httpLink}"><font face="Arial" color="#09438b">www.castorama.fr</font></a>
                  </fmt:param>
                </fmt:message>
              </p>

              <hr style="width: 100%; height: 1px; color: #e2e2e2;">
              <p><font color="#000000" face="Arial"><dsp:valueof param="message" valueishtml="false"/></font></p>
              <hr style="width: 100%; height: 1px; color: #e2e2e2;">
              <p><br />
              <dsp:droplet name="/com/castorama/droplet/CastProductLinkDroplet">
                <dsp:param name="id" param="productId"/>
                <dsp:oparam name="output">
                  <dsp:getvalueof var="productLink" param="url"/>
                  <fmt:message key="email.a.friend.clientspace">
                    <fmt:param>
                      <a href="${httpLink}${productLink}"><font color="#09438b">
                        <fmt:message key="email.link.click.here"/>
                      </font></a></fmt:param>
                  </fmt:message>
                  </p>
    
                  <p><fmt:message key="email.a.friend.goto.product.link"/><br />
                    <a href="${httpLink}${productLink}"><font color="#09438b">${httpLink}${productLink}</font></a>
                  
                </dsp:oparam>
              </dsp:droplet>
              </p>

              <p>
                <fmt:message key="email.a.friend.sitetour">
                  <fmt:param>
                    <a href="${httpLink}"><font face="Arial" color="#09438b">www.castorama.fr</font></a>
                  </fmt:param>
                </fmt:message>
              </p>
              <p><fmt:message key="email.a.friend.merci"/></p>
              <p>
                <fmt:message key="email.a.friend.command">
                  <fmt:param><a href="${httpLink}">www.castorama.fr</a></fmt:param>
                </fmt:message>
              </p>
              </font></td>
            </tr>
            <tr>
              <td height="20"></td>
            </tr>
          </table>
    </jsp:attribute>
  </cast:emailPageContainer>

</dsp:page>
