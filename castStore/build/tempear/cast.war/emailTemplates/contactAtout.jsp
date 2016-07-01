<%@ page contentType="text/html; charset=iso-8859-1"%>
<dsp:page>

  <dsp:getvalueof var="civilite" param="civilite"/>
  <dsp:getvalueof var="nom" param="nom"/>
  <dsp:getvalueof var="prenom" param="prenom"/>
  <dsp:getvalueof var="email" param="email"/>
  <dsp:getvalueof var="contractNumber" param="contractNumber"/>
  <dsp:getvalueof var="motif" param="motif"/> 
  <dsp:getvalueof var="motifTitle" param="motifTitle"/>
  <dsp:getvalueof var="message" param="message"/>
  
  <cast:emailPageContainer headerAlign="left" displayHeader="true" displayFooter="true">
    <jsp:attribute name="messageBody">
      <font size="3" face="Arial" color="#09438b">
        <b><fmt:message key="user_cardAtout.title"/><br/>
          <span style="background-color : #FFFF00; font-weight: bold; color : #FF0000">${motifTitle}</span>
        </b>
      </font>
      <hr style="width: 100%; height: 1px; color: rgb(226, 226, 226);"/>               
      
      <table>
        <tr>
          <td align="right" width="300"><fmt:message key="user_cardAtout.civilite"/></td>
      	  <td width="300">${civilite }</td>
        </tr>
        <tr>
          <td align="right"><fmt:message key="user_cardAtout.nom"/></td>
          <td> ${nom }</td>
        </tr>
        <tr>
          <td align="right"><fmt:message key="user_cardAtout.prenom"/></td>
          <td>${prenom }</td>
        </tr>
        <tr>
          <td align="right"><fmt:message key="user_cardAtout.email"/></td>
          <td>${email }</td>
        </tr>
        <tr>
          <td align="right"><fmt:message key="user_cardAtout.address"/></td>
          <td><dsp:valueof param="address1" />&nbsp;<dsp:valueof param="address2" />&nbsp;<dsp:valueof param="address3" />&nbsp;<dsp:valueof param="locality" />
          </td>
        </tr>
        <tr>
          <td align="right"><fmt:message key="user_cardAtout.zip"/></td>
          <td><dsp:valueof param="postalCode" /></td>
        </tr>
        <tr>
          <td align="right"><fmt:message key="user_cardAtout.city"/></td>
          <td><dsp:valueof param="city" /></td>
        </tr>
        <tr>
          <td align="right"><fmt:message key="user_cardAtout.contractNumber"/></td>
          <td>${contractNumber }</td>
        </tr>
        <tr>
          <td align="right"><fmt:message key="user_cardAtout.phone"/></td>
          <td><dsp:valueof param="telephone" /></td>
        </tr>
         <tr>
          <td align="right"><fmt:message key="user_cardAtout.motif"/></td>
          <td>${motifTitle}</td>
         </tr>
         <tr>
          <td align="right"><fmt:message key="user_cardAtout.demande"/></td>
          <td>${message }</td>
        </tr>
        </table>
        <hr style="width: 100%; height: 1px; color: rgb(226, 226, 226);"/>
    </jsp:attribute>
  </cast:emailPageContainer>
</dsp:page>