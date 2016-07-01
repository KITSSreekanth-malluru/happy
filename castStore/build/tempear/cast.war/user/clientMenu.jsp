<dsp:page>
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/com/castorama/profile/CastProfileFormHandler"/>
<dsp:importbean bean="/com/castorama/profile/CastLoginFormHandler"/>
<dsp:importbean bean="/com/castorama/commerce/clientspace/CastNewsletterFormHandler" />
<dsp:importbean bean="/atg/userprofiling/SessionBean"/>
<dsp:importbean var="CastConfiguration" bean="/com/castorama/CastConfiguration" />
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />

<dsp:getvalueof var="currPage" param="currPage" />

<script type="text/javascript">
$(document).ready(function(){ 
  if ("${currPage}" != "") {
    $("#${currPage}").attr("class", "currentUserPage");
  }
});
</script>

<div class="leftColumn">
	<dsp:droplet name="/atg/dynamo/droplet/Switch">
	    <dsp:param bean="Profile.transient" name="value"/>
	    <dsp:oparam name="false">
		<div class="bienvenueBlock">
			<div class="bienvenueHead">
				<h2><fmt:message key="client.menu.welcome" /></h2>
				<span class="userName">
				
				<%@include file="../castCommon/includes/displayUserName.jspf" %>
         
				</span><br />
				<span>
				<dsp:valueof bean="Profile.billingAddress.address1"/>&nbsp;<dsp:valueof bean="Profile.billingAddress.address2"/><br />

				<dsp:getvalueof var="address3" bean="Profile.billingAddress.address3"/>
				<dsp:getvalueof var="locality" bean="Profile.billingAddress.locality"/>
				</span>
				<c:if test="${not empty address3 || not empty  locality}">
					<dsp:valueof bean="Profile.billingAddress.address3"/>&nbsp;<dsp:valueof bean="Profile.billingAddress.locality"/>
				<br />
				</c:if>
				<dsp:valueof bean="Profile.billingAddress.postalCode"/>&nbsp; <dsp:valueof bean="Profile.billingAddress.city"/><br />				 
				<br /><dsp:valueof bean="Profile.billingAddress.phoneNumber"/><br />
				
				<dsp:getvalueof var="phoneNumber2" bean="Profile.billingAddress.phoneNumber2"/>
				<c:if test="${not empty phoneNumber2}">
					<dsp:valueof bean="Profile.billingAddress.phoneNumber2"/>
					<br />				
				</c:if>
				<br />
				<dsp:valueof bean="Profile.login"/>
			</div>
			<div class="usbBottom2">
			  <a href="${pageContext.request.contextPath}/user/myProfile.jsp" class="icoEdit"><fmt:message key="client.menu.modify" /></a>

        <dsp:droplet name="/atg/dynamo/droplet/Compare">
          <dsp:param bean="/atg/userprofiling/Profile.securityStatus" name="obj1"/>
          <dsp:param bean="/atg/userprofiling/PropertyManager.securityStatusCookie" name="obj2"/>
          <dsp:oparam name="lessthan">
            <a href="${pageContext.request.contextPath}/user/login.jsp" class="icoDeconnecter">
                <fmt:message key="client.menu.log.out" /></a>
         </dsp:oparam>
         <dsp:oparam name="default">
             <dsp:form method="post" iclass="formless">              
              <a href="javascript:document.getElementById('logoutMenu').click()" class="icoDeconnecter">
                <fmt:message key="client.menu.log.out" /></a>       
              <dsp:input bean="CastLoginFormHandler.logout" type="submit" value="Valider" id="logoutMenu" style="display:none"/>
              <dsp:input bean="CastLoginFormHandler.logoutSuccessURL" type="hidden"
                            value="${pageContext.request.contextPath}/user/login.jsp"/>
             </dsp:form>
         </dsp:oparam>
         </dsp:droplet>
			</div>
		</div>
		</dsp:oparam>
	</dsp:droplet>
	
	<div class="accueilBlock">
		<h2><fmt:message key="client.menu.client.space.home" /></h2>
		<dsp:droplet name="/atg/dynamo/droplet/Switch">
	    	<dsp:param bean="Profile.transient" name="value"/>
			<dsp:oparam name="true">
				<div class="">
				<a href="${pageContext.request.contextPath}/user/login.jsp" class="loginLink"><fmt:message key="header.connect" /></a>
                </div>
			</dsp:oparam>
		</dsp:droplet>
		<h3><fmt:message key="header.my.account" /></h3>
		<ul>
			<li><a href="${pageContext.request.contextPath}/user/myProfile.jsp" id="profile">						
				<fmt:message key="header.my.profil" /></a>
			</li>
			<li><a href="${pageContext.request.contextPath}/user/myNewsletters1.jsp" id="newsletters">
				<fmt:message key="header.my.newsletters" /></a></li>
			<li><a href="${pageContext.request.contextPath}/user/myAddressBook.jsp" id="addressBook">
			<fmt:message key="header.my.address" /></a></li>
			<li>
			<dsp:include page="/magasin/includes/magasin-link.jsp">
			 <dsp:param name="magasinId" value="magasin"/>
			</dsp:include>
			
			</li>
            <dsp:droplet name="/com/castorama/invite/ReferrerProgramAvailabilityDroplet">
              <dsp:oparam name="true">
			   <li><a href="${pageContext.request.contextPath}/user/parrainerUnAmi.jsp" id="friend"><fmt:message key="header.sponsor" /></a></li>
		      </dsp:oparam>
            </dsp:droplet>
            <dsp:droplet name="Switch">
                <dsp:param name="value" bean="Profile.transient"/>
                <dsp:oparam name="false">
                    <dsp:getvalueof var="profileId" bean="Profile.repositoryId" />
                    <dsp:getvalueof var="userProjectsLink" bean="CastConfiguration.urlUserProjects"/>
                    <dsp:droplet name="/atg/dynamo/droplet/Format">
                        <dsp:param name="format" value="${userProjectsLink}"/>
                        <dsp:param name="profileId" value="${profileId}"/>
                        <dsp:oparam name="output">
                            <dsp:getvalueof param="message" var="message" />
                            <li><a href="${message}"><dsp:valueof bean="CastConfiguration.userProjectsLinkName"/> </a></li>
                        </dsp:oparam>
                    </dsp:droplet>
                </dsp:oparam>
                <dsp:oparam name="true">
                    <li><a href="${pageContext.request.contextPath}/user/login.jsp"><dsp:valueof bean="CastConfiguration.userProjectsLinkName"/> </a></li>
                </dsp:oparam>
            </dsp:droplet>
        </ul>
		<h3><fmt:message key="header.cart" /></h3>
		<ul>
			<li><a href="${pageContext.request.contextPath}/user/ordersHistory.jsp" id="order"><fmt:message key="header.my.web" /></a></li>
		</ul>
		<h3><fmt:message key="header.my.card" /></h3>
		<ul>
  		  <li><a href="${contextPath }/user/atoutAdvanteges.jsp" id="atoutAdvanteges"><fmt:message key="client.my.card.advantage" /></a></li>
  		  <li>
            <dsp:droplet name="/com/castorama/droplet/CastCarteAtout">
              <dsp:oparam name="output">      
                <dsp:getvalueof var="url" param="url"/>
               <dsp:getvalueof  var="open" bean="/com/castorama/CastConfiguration.openInformation"/>
               <dsp:getvalueof  var="URL" bean="/com/castorama/CastConfiguration.newLinkForGererMaCarte"/>
			   <dsp:getvalueof  var="URLText" bean="/com/castorama/CastConfiguration.newTextForLinkGererMaCarte"/>
               <c:choose>
				 <c:when test="${open==0}">
                <a href="#" onclick="showAtoutContPopup('${URL}')">${URLText}</a>
                </c:when>
				 <c:when test="${open==1}">
              <a href="${URL}" >${URLText}</a>
				 </c:when>
				 <c:when test="${open==2}">

                 <a href="${URL}" target="_blank">${URLText}</a>
				 </c:when>
				 <c:otherwise>
          <a href="#" onclick="showAtoutContPopup('${URL}')">${URLText}</a>	
      	    </c:otherwise>
                 </c:choose>
              </dsp:oparam>
              <dsp:oparam name="empty">
              </dsp:oparam>
            </dsp:droplet>
          </li>
          <li><a href="${contextPath }/user/storeOrders.jsp" id="storeOrders"><fmt:message key="client.my.card.memo.casto" /></a></li>
          <dsp:getvalueof var="sofincoUrl" bean="/com/castorama/CastConfiguration.sofincoUrl" />
          <c:choose>
            <c:when test="${not empty sofincoUrl && sofincoUrl != ''}">
              <li>${sofincoUrl}</li>
            </c:when>
            <c:otherwise>
          <li><a href="${contextPath }/user/orderCardAtout.jsp" id="orderCardAtout"><fmt:message key="client.my.card.ask" /></a></li>
            </c:otherwise>
          </c:choose>
           <dsp:droplet name="/com/castorama/droplet/LinkForClientEspace">
		<dsp:oparam name="output">  
			<li>
               <dsp:getvalueof var="url" param="url"/>
               <dsp:getvalueof var="urlText" param="URLTEXT"/>
                <a href="${url}">
                  ${urlText}
                </a>
              </dsp:oparam>
              <dsp:oparam name="empty">
             
              </dsp:oparam>
            </dsp:droplet>
          </li>
		     
		</ul>
		
		<h3><fmt:message key="header.my.card.gift" /></h3>
		<ul>
			<li><a href="${pageContext.request.contextPath}/user/giftCard.jsp" id="giftCard"><fmt:message key="header.my.card.discover" /></a></li>
		</ul>
		<h3><fmt:message key="header.my.assistance" /></h3>
		<ul>
			<li><a href="${pageContext.request.contextPath}/contactUs/faq.jsp?owner=faq1" id="faq1"><fmt:message key="header.contact.customer" /></a></li>
			<li><a href="${pageContext.request.contextPath}/contactUs/faq.jsp?owner=faq2" id="faq2"><fmt:message key="header.faq" /></a></li>
		</ul>
	</div>
  </div>
  <div id="atoutPopup" loaded="false">
  </div>
</dsp:page>