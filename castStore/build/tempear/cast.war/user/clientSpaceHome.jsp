<dsp:page>
    <dsp:importbean bean="/atg/userprofiling/Profile"/>
    <dsp:importbean bean="/com/castorama/profile/CastProfileFormHandler"/>
    <dsp:importbean bean="/com/castorama/commerce/clientspace/CastNewsletterFormHandler" />
    <dsp:importbean bean="/atg/userprofiling/SessionBean"/>
    <dsp:importbean var="CastConfiguration" bean="/com/castorama/CastConfiguration" />
    <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
    <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
	<cast:pageContainer>
		<jsp:attribute name="bodyContent">		
			<div class="content">				
				<div class="breadcrumbs bluePage">
	            	<div class="homeBreadIco"><a href="${pageContext.request.contextPath}/home.jsp"><img src="${pageContext.request.contextPath}/images/icoHomeGray.gif" alt="" title="" /></a></div>
	                <div class="splitter">&gt;</div>
					<div class="active"><fmt:message key="client.breadcrumb.my.client" /></div>					
	            </div>
	            <c:set var="footerBreadcrumb" value="clientSpaceHome" scope="request"/>	            
	            <dsp:include page="clientMenu.jsp"/>
	            
	            <div class="myaccount">
	            
		            
		            <div class="grayFrame">
		            	<div class="gfLabel"><b><fmt:message key="header.my.account" /></b></div>
		            	<div class="container">
			            	<fmt:message key="client.change.info.txt" />
			            	<ul>
			            		<li><a href="${pageContext.request.contextPath}/user/myProfile.jsp">
			            			<fmt:message key="header.my.profil" /></a></li>
			            		<li><a href="${pageContext.request.contextPath}/user/myNewsletters1.jsp">
			            			<fmt:message key="header.my.newsletters" /></a></li>
			            		<li><a href="${pageContext.request.contextPath}/user/myAddressBook.jsp">
			            			<fmt:message key="header.my.address" /></a></li>
			            		<li>
			            		<dsp:include page="/magasin/includes/magasin-link.jsp"/>
			            		</li>
                                <dsp:droplet name="/com/castorama/invite/ReferrerProgramAvailabilityDroplet">
                                  <dsp:oparam name="true">
                                    <li><a href="parrainerUnAmi.jsp"><fmt:message key="header.sponsor" /></a></li>
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
			            </div>
		            </div>
		            
		            
		            <div class="grayFrame">
		            	<div class="gfLabel"><b><fmt:message key="header.cart" /></b></div>
		            	<div class="container">
		            		<fmt:message key="client.manage.web.store.txt" />
		            		<ul>
		            			<li><a href="${pageContext.request.contextPath}/user/ordersHistory.jsp"><fmt:message key="header.my.web" /></a></li>
		            		</ul>
		            	</div>
		            </div>
		            
		            <div class="grayFrame lastFrame">
		            	<div class="gfLabel"><b><fmt:message key="header.my.card" /></b></div>
		            	<div class="container">
			      			<p><fmt:message key="client.manage.card" /></p>
                            <fmt:message var="buttonValue" key="user_clientSpaceHome.submit" />
                            <dsp:getvalueof  var="URL" bean="/com/castorama/CastConfiguration.newLinkForGererMaCarte"/>
                            <dsp:getvalueof  var="open" bean="/com/castorama/CastConfiguration.openInformation"/>
			            	<dsp:getvalueof  var="URLText" bean="/com/castorama/CastConfiguration.newTextForLinkGererMaCarte"/>
			            	<c:choose>
			            	<c:when test="${open==0}">
			      			<input type="button" value="${buttonValue }" class="buttonAcceder" onClick="showAtoutContPopup('${URL}')"/>
			      		</c:when>
			      		<c:when test="${open==1}">
			      		<input type="button" value="${buttonValue }" class="buttonAcceder" onclick="location.href='${URL}'" />
			      	</c:when>
			      	<c:when test="${open==2}">
			      		<input type="button" value="${buttonValue }" class="buttonAcceder" onClick="window.open('${URL}','_blank')"/>
			      	</c:when>
			      	<c:otherwise>
			      	<input type="button" value="${buttonValue }" class="buttonAcceder"  onclick="showAtoutContPopup('${URL}')"/>
			      </c:otherwise>

			      		</c:choose>
			            	<ul>
			            		<li>    
			            		<li><a href="${contextPath }/user/atoutAdvanteges.jsp" id="atoutAdvanteges"><fmt:message key="client.my.card.advantage" /></a></li>
  		                        <li><a href="storeOrders.jsp"><fmt:message key="client.my.card.memo.casto" /></a></li>
                      <dsp:getvalueof var="sofincoUrl" bean="/com/castorama/CastConfiguration.sofincoUrl" />
			          <c:choose>
			            <c:when test="${not empty sofincoUrl && sofincoUrl != ''}">
			              <li>${sofincoUrl}</li>
			            </c:when>
			            <c:otherwise>
                      <li><a href="orderCardAtout.jsp"><fmt:message key="client.my.card.ask" /></a></li>
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
			            	<div class="gfImgLabel_CL"></div>
			            </div>
		            </div>
		            
		            <div class="grayFrame">
		            	<div class="gfLabel"><b><fmt:message key="header.my.card.gift" /></b></div>
		            	<div class="container">
			      			<p><fmt:message key="client.gift.txt" /></p>
			            	<ul>
			            		<li><a href="${pageContext.request.contextPath}/user/giftCard.jsp"><fmt:message key="header.my.card.discover" /></a></li>
			            	</ul>
			            	<div class="gfImgLabel_CC"></div>
			            </div>
		            </div>
		            
		            <div class="grayFrame">
		            	<div class="gfLabel"><b><fmt:message key="header.my.assistance" /></b></div>
		            	<div class="container">
			      			<p class="assistance">
			      				<span><strong><fmt:message key="client.answer.question" /></strong></span><br />
			      				<fmt:message key="client.before.ordering" /><br />
								<fmt:message key="client.after.purchase" /><br />
								<fmt:message key="client.question.site" />
			      			</p>
			            	<ul>
			            		<li><a href="${pageContext.request.contextPath}/contactUs/faq.jsp?owner=faq1"><fmt:message key="header.contact.customer" /></a></li>
			            		<li><a href="${pageContext.request.contextPath}/contactUs/faq.jsp?owner=faq2"><fmt:message key="header.faq" /></a></li>
			            	</ul>
			            </div>
		            </div>
		            
				</div>
	            
			</div>            
		</jsp:attribute>
	</cast:pageContainer>
   <div id="atoutPopup" loaded="false">
   </div>
</dsp:page>
