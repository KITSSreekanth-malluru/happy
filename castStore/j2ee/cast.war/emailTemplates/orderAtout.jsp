<%@ page contentType="text/html; charset=iso-8859-1"%>
<dsp:page>

  
  <cast:emailPageContainer displayHeader="false" displayFooter="false">
    <jsp:attribute name="messageBody">
      <dsp:getvalueof var="lundi" param="lundi"/>
      <dsp:getvalueof var="mardi" param="mardi"/>
      <dsp:getvalueof var="mercredi" param="mercredi"/>
      <dsp:getvalueof var="jeudi" param="jeudi"/>
      <dsp:getvalueof var="vendredi" param="vendredi"/>
      
      <dsp:getvalueof var="matin" param="matin"/>
      <dsp:getvalueof var="apresMidi" param="apresMidi"/>
      
      <b>
	      <dsp:droplet name="/atg/dynamo/droplet/Switch">
		    <dsp:param name="value" param="user.civilite"/>
		    <dsp:oparam name="miss"><fmt:message key="msg.address.prefix.miss" />&nbsp;</dsp:oparam>
		    <dsp:oparam name="mrs"><fmt:message key="msg.address.prefix.mrs" />&nbsp;</dsp:oparam>
		    <dsp:oparam name="mr"><fmt:message key="msg.address.prefix.mr" />&nbsp;</dsp:oparam>
		  </dsp:droplet><dsp:valueof param="nom"/>&nbsp;<dsp:valueof param="prenom"/><br/><br/>
	      <dsp:valueof param="email"/><br/><br/>
	      <dsp:valueof param="address1" />&nbsp;<dsp:valueof param="address2" />&nbsp;<dsp:valueof param="address3" />&nbsp;<dsp:valueof param="locality" /><br>
		  <dsp:valueof param="postalCode" /><br>
		  <dsp:valueof param="city" /><br><br/><br/><br/>
      </b>
      
      <fmt:message  key="user_orderCardAtout.telephon"/> <b><dsp:valueof param="telephone"/></b><br/><br/>
      <fmt:message  key="user_orderCardAtout.days"/>
      <c:set var="isOtherDayExist" value="false"/>
      <b>
	      <%-- This line must be so long in order to escape extra spaces before "," character! --%>
	      <c:if test="${not empty lundi && lundi}"><fmt:message key="user_orderCardAtout.lundi"/><c:set var="isOtherDayExist" value="true"/></c:if><c:if test="${not empty mardi && mardi}"><c:if test="${not empty isOtherDayExist && isOtherDayExist }">, </c:if><fmt:message key="user_orderCardAtout.mardi"/></c:if><c:if test="${not empty mercredi && mercredi}"><c:if test="${not empty isOtherDayExist && isOtherDayExist }">, </c:if><fmt:message key="user_orderCardAtout.mercredi"/></c:if><c:if test="${not empty jeudi && jeudi}"><c:if test="${not empty isOtherDayExist && isOtherDayExist }">, </c:if><fmt:message key="user_orderCardAtout.jeudi"/></c:if><c:if test="${not empty vendredi && vendredi}"><c:if test="${not empty isOtherDayExist && isOtherDayExist }">, </c:if><fmt:message key="user_orderCardAtout.vendredi"/></c:if>
      </b>
      <br/><br/>
      
      <c:set var="isMomentOfTheDay" value="false"/>
      <fmt:message  key="user_orderCardAtout.momentOfTheDay"/>
      <b>
      <%-- This line must be so long in order to escape extra spaces before "," character! --%>
      <c:if test="${not empty matin && matin}"><fmt:message key="user_orderCardAtout.matin"/><c:set var="isMomentOfTheDay" value="true"/></c:if><c:if test="${not empty apresMidi && apresMidi}"><c:if test="${not empty isMomentOfTheDay && isMomentOfTheDay }">, </c:if><fmt:message key="user_orderCardAtout.apresMidi"/></c:if>
      <br/><br/>
      </b>
      
      <fmt:message  key="user_orderCardAtout.favoriteStore"/> <b><dsp:valueof param="magasin"/></b>
       
    </jsp:attribute>
  </cast:emailPageContainer>
  
</dsp:page>


