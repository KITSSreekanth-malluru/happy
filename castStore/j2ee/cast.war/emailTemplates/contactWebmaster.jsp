<%@ page contentType="text/html; charset=iso-8859-1"%>
<dsp:page>
  <dsp:getvalueof var="civilite" param="civilite"/>
  <dsp:getvalueof var="nom" param="nom"/>
  <dsp:getvalueof var="prenom" param="prenom"/>
    <cast:emailPageContainer displayHeader="false" displayFooter="false">
        <jsp:attribute name="messageBody">
            <br><br>
            <div align="left">
                <dsp:droplet name="/atg/dynamo/droplet/Switch">
                  <dsp:param name="value" value="${civilite }"/>
                  <dsp:oparam name="miss"><fmt:message key="msg.address.prefix.miss" />&nbsp;</dsp:oparam>
                  <dsp:oparam name="mrs"><fmt:message key="msg.address.prefix.mrs" />&nbsp;</dsp:oparam>
                  <dsp:oparam name="mr"><fmt:message key="msg.address.prefix.mr" />&nbsp;</dsp:oparam>
                </dsp:droplet> ${prenom}&nbsp;${nom}<br>
                <fmt:message key="contact.product.email">
                  <fmt:param><dsp:valueof param="email" /></fmt:param>
                </fmt:message>
                <br>
                <fmt:message key="contact.product.address">
                  <fmt:param>
                    <dsp:valueof param="address1" />&nbsp;<dsp:valueof param="address2" />&nbsp;<dsp:valueof param="address3" />&nbsp;<dsp:valueof param="locality" /><br>
                    <dsp:valueof param="postalCode" />&nbsp;<dsp:valueof param="city" />
                  </fmt:param>
                </fmt:message>
                <br>
                <br>

                <fmt:message key="contact.product.phone">
                  <fmt:param><dsp:valueof param="telephone" /></fmt:param>
                </fmt:message>
                <br>
                <fmt:message key="contact.webmaster.remarque">
                  <fmt:param><dsp:valueof param="message" /></fmt:param>
                </fmt:message>
                <br>
                <fmt:message key="contact.webmaster.operating.system">
                  <fmt:param><dsp:valueof param="operatingSystem" /></fmt:param>
                </fmt:message>
                <br>
                <fmt:message key="contact.webmaster.browser">
                  <fmt:param><dsp:valueof param="browser" /></fmt:param>
                </fmt:message>
                <br>
                <fmt:message key="contact.webmaster.connection">
                  <fmt:param><dsp:valueof param="connectionType" /></fmt:param>
                </fmt:message>
                <br>
            </div>
            <br><br>
        </jsp:attribute>
    </cast:emailPageContainer>

</dsp:page>


