<%@ page contentType="text/html; charset=UTF-8"%>
<dsp:page>

    <dsp:importbean var="dynamoConfig" bean="/atg/dynamo/Configuration"/>
    <dsp:importbean var="originatingRequest" bean="/OriginatingRequest"/>

    <dsp:getvalueof var="serverName" vartype="java.lang.String" value="${dynamoConfig.siteHttpServerName}"/>
    <dsp:getvalueof var="serverPort" vartype="java.lang.String" value="${dynamoConfig.siteHttpServerPort}"/>
    <dsp:getvalueof var="httpServer" vartype="java.lang.String" value="http://${serverName}:${serverPort}"/>
    <dsp:getvalueof var="contextPath" vartype="java.lang.String" value="${originatingRequest.contextPath}"/>
    <dsp:getvalueof var="httpLink" vartype="java.lang.String" value="${httpServer}${contextPath}"/>

    <cast:emailPageContainer messageSubjectKey="createAccount.mail.subject">

    <jsp:attribute name="messageBody">
        <dsp:getvalueof var="prefix" param="civilite"/>
        <dsp:getvalueof var="var_lastName" param="lastName"/>
        <dsp:getvalueof var="var_firstName" param="firstName"/>
        <dsp:getvalueof var="var_email" param="email"/>
        <dsp:getvalueof var="var_password" param="newpassword"/>
        
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
                        <b><fmt:message key="createAccount.mail.title"/></b>
                        <br />

                    </font>
                    <p><font color="#000000" size="2" face="Arial"><fmt:message key="createAccount.mail.bonjour"/>&nbsp;</font><font size="2" face="Arial"><fmt:message key="${var_civilite}" />&nbsp;${var_firstName}&nbsp;${var_lastName}</font><font color="#000000" size="2" face="Arial">,</font></p>

                    <font color="#000000" size="2" face="Arial"><p><fmt:message key="createAccount.mail.string1"/></p>

                        <p><br />
                            <fmt:message key="createAccount.mail.clientspace">
                                <fmt:param>
                                    <a href="${httpLink}/user/clientSpaceHome.jsp"><font color="#09438b">
                                        <fmt:message key="createAccount.mail.clientspace.link"/>
                                    </font></a>
                                </fmt:param>
                            </fmt:message>
                        </p>
                        <p>
                            <fmt:message key="createAccount.mail.clientspace.ability.1">
                                <fmt:param>
                                    <a href="${httpLink}"><font color="#09438b">www.castorama.fr</font></a>
                                </fmt:param>
                            </fmt:message>
                        </p>

                        <p><fmt:message key="createAccount.mail.clientspace.ability.2"/></p>

                        <p><fmt:message key="createAccount.mail.clientspace.ability.3"/></p>

                        <p>
                            <fmt:message key="createAccount.mail.register.newsletter">
                                <fmt:param>
                                    <a target="_blank" href="mailto:castorama@edt02.net"><font color="#09438b">castorama@edt02.net</font></a>
                                </fmt:param>
                            </fmt:message>
                        </p>

                        <p><fmt:message key="createAccount.mail.thanks"/></p>

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
                                <fmt:param><br/>www.castorama.fr</fmt:param>
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

    </cast:emailPageContainer>

</dsp:page>