<%@ page contentType="text/html; charset=UTF-8"%>
<dsp:page>
  <dsp:getvalueof var="civilite" param="civilite"/>
  <dsp:getvalueof var="nom" param="nom"/>
  <dsp:getvalueof var="prenom" param="prenom"/>
  <dsp:getvalueof var="email" param="email"/>
  <dsp:getvalueof var="contractNumber" param="contractNumber"/>
  <dsp:getvalueof var="motif" param="motif"/> 
  <dsp:getvalueof var="motifTitle" param="motifTitle"/>
  <dsp:getvalueof var="message" param="message"/>
  
    <cast:emailPageContainer displayHeader="true" displayFooter="true">
        <jsp:attribute name="messageBody">
        <font size="2" face="Arial" color="#000000">
        <font size="3" face="Arial" color="#09438b"><b>
          <fmt:message key="contact.iphone.title">
            <fmt:param>${motif}</fmt:param>
          </fmt:message>
        </b></font>
        <br />
        <table align="left">
          <tr>
            <td><fmt:message key="contact.iphone.motif"/></td>
            <td>${motif}</td>
          </tr>
                <tr>
                  <td><fmt:message key="contact.iphone.civilite"/></td>
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
                      <dsp:oparam name="organization">
                      <fmt:message key="msg.address.prefix.organization" />
                      </dsp:oparam>
                    </dsp:droplet>          
                  </td>
                </tr>
          <tr>
                <td><fmt:message key="contact.iphone.nom"/></td>
                  <td>${nom}</td>
                </tr>
                <tr>
                  <td><fmt:message key="contact.iphone.prenom"/></td>
                  <td>${prenom}</td>
                </tr>
                <tr>
                  <td><fmt:message key="contact.iphone.email"/></td>
                  <td>${email}</td>
                </tr>
                <tr>
                  <td><fmt:message key="contact.iphone.address"/></td>
                  <td><dsp:valueof param="address1" />&nbsp;<dsp:valueof param="address2" />&nbsp;<dsp:valueof param="address3" />&nbsp;<dsp:valueof param="locality" /></td>
                </tr>
                <tr>
                  <td><fmt:message key="contact.iphone.city"/></td>
                  <td><dsp:valueof param="city" /></td>
                </tr>
                <tr>
                  <td><fmt:message key="contact.iphone.zip"/></td>
                  <td><dsp:valueof param="postalCode" /></td>
                </tr>
                <tr>
                  <td><fmt:message key="contact.iphone.phone"/></td>
                  <td><dsp:valueof param="telephone" /></td>
                </tr>
                
                <tr>
                  <td><fmt:message key="contact.iphone.remarque"/></td>
                  <td><dsp:valueof param="message" /></td>
                </tr>
                
            </table>
           </font>
        </jsp:attribute>
    </cast:emailPageContainer>

</dsp:page>


