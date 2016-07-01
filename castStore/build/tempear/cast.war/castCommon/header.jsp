<dsp:page>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/com/castorama/profile/CastProfileFormHandler"/>
  <dsp:importbean bean="/com/castorama/commerce/clientspace/CastNewsletterFormHandler" />
  <dsp:importbean bean="/atg/userprofiling/SessionBean"/>
  <dsp:importbean var="dynamoConfig" bean="/atg/dynamo/Configuration" />
  <dsp:importbean var="CastConfiguration" bean="/com/castorama/CastConfiguration" />
  <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
  <dsp:getvalueof var="aServerName" vartype="java.lang.String" value="${dynamoConfig.siteHttpServerName}" />
  <dsp:getvalueof var="aServerPort" vartype="java.lang.String" value=":${dynamoConfig.siteHttpServerPort}" />
  <c:if test='${aServerPort == ":80"}'>
    <dsp:getvalueof var="aServerPort" value="" />
  </c:if>

  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
  
  <div id="monEspaceClient">
    <div class="userShadow">
      <div class="m18">    
        <div class="gfLabel"><b><fmt:message key="header.my.account" /></b></div>    
         <div class="grayFrame">    
          <div class="container">    
            <ul>    
              <li><a href="${pageContext.request.contextPath}/user/myProfile.jsp"/><fmt:message key="header.my.profil" /></a></li>
              <li><a href="${pageContext.request.contextPath}/user/myNewsletters1.jsp"><fmt:message key="header.my.newsletters" /></a></li>
              <li><a href="${pageContext.request.contextPath}/user/myAddressBook.jsp"><fmt:message key="header.my.address" /></a></li>
              <li><dsp:include page="/magasin/includes/magasin-link.jsp"/></li>
              <dsp:droplet name="/com/castorama/invite/ReferrerProgramAvailabilityDroplet">
                <dsp:oparam name="true">
                   <li><a href="${pageContext.request.contextPath}/user/parrainerUnAmi.jsp"><fmt:message key="header.sponsor" /></a></li> 
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
        <div class="gfLabel"><b><fmt:message key="header.cart" /></b></div>
        <div class="grayFrame">    
          <div class="container">    
            <ul>
              <li><a href="${pageContext.request.contextPath}/user/ordersHistory.jsp"><fmt:message key="header.my.web" /></a></li>    
            </ul>
          </div>
        </div>
        <!--div class="gfLabel"><b>Mes Projets</b></div>
          <div class="grayFrame">
            <div class="clear">&nbsp;</div>
            <div class="container">
            <ul>
              <li><a href="#">Ma nouvelle salle de bain</a></li>
              <li><a href="#">Chambres des enfants</a></li>
              <li><a href="#">Mon nouveau dressing</a></li>
            </ul>
          </div>
        </div-->
        
        <div class="gfLabel"><b><fmt:message key="header.my.card" /></b></div>
          <div class="grayFrame">
            <div class="container">
              <ul>
                <li><a href="${contextPath }/user/atoutAdvanteges.jsp"><fmt:message key="client.my.card.advantage" /></a></li>
            
              <dsp:getvalueof  var="open" bean="/com/castorama/CastConfiguration.openInformation"/>
              <dsp:getvalueof  var="URL" bean="/com/castorama/CastConfiguration.newLinkForGererMaCarte"/>
         <dsp:getvalueof  var="URLText" bean="/com/castorama/CastConfiguration.newTextForLinkGererMaCarte"/>
               <c:choose>
                 <c:when test="${open==0}">
                <li><a href="#" onclick="showAtoutContPopup('${URL}')">${URLText}</a></li>
                </c:when>
                 <c:when test="${open==1}">
                 <li><a href="${URL}" >${URLText}</a></li>
                 </c:when>
                 <c:when test="${open==2}">
                 <li><a href="${URL}" target="_blank">${URLText}</a></li>
                 </c:when>
                 <c:otherwise>
          <li><a href="#" onclick="showAtoutContPopup('${URL}')">${URLText}</a> </li>    
              </c:otherwise>
                 </c:choose>
                <li><a href="${contextPath }/user/storeOrders.jsp"><fmt:message key="client.my.card.memo.casto" /></a></li>
                <dsp:getvalueof var="sofincoUrl" bean="/com/castorama/CastConfiguration.sofincoUrl" />
                <c:choose>
                  <c:when test="${not empty sofincoUrl && sofincoUrl != ''}">
                    <li>${sofincoUrl}</li>
                  </c:when>
                  <c:otherwise>
                <li><a href="${contextPath }/user/orderCardAtout.jsp"><fmt:message key="client.my.card.ask" /></a></li>    
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
          <div class="gfLabel"><b><fmt:message key="header.my.card.gift" /></b></div>
          <div class="grayFrame">
            <div class="container">
              <ul>
                <li><a href="${pageContext.request.contextPath}/user/giftCard.jsp"><fmt:message key="header.my.card.discover" /></a></li>
              </ul>    
              <div class="gfImgLabel_CC"></div>
            </div>    
          </div>    
          <div class="gfLabel"><b><fmt:message key="header.my.assistance" /></b></div>
          <div class="grayFrame">
            <div class="container">
            <ul>
              <li><a href="${pageContext.request.contextPath}/contactUs/faq.jsp?owner=faq1"><fmt:message key="header.contact.customer" /></a></li>    
              <li><a href="${pageContext.request.contextPath}/contactUs/faq.jsp?owner=faq2"><fmt:message key="header.faq" /></a></li>    
            </ul>
            
            <dsp:getvalueof var="showServiceClient2012Flap" bean="/com/castorama/CastConfiguration.showServiceClient2012Flap" />
            <c:if test="${showServiceClient2012Flap}">
              <div class="gfImgLabel_CS"></div>
            </c:if>
            
          </div>    
        </div>    
      </div>    
    </div>    
  </div>    
  <dsp:importbean bean="/atg/commerce/catalog/ProductCatalog" /> 
  <dsp:droplet name="/atg/dynamo/droplet/RQLQueryRange" >
    <dsp:param name="itemDescriptor" value="fastLabConfigs" />
    <dsp:param name="repository" bean="ProductCatalog" />
    <dsp:param name="queryRQL" value="ALL" />
    <dsp:param name="howMany" value="1" />
    <dsp:param name="sortProperties" value="" />
    <dsp:oparam name="output">
      <dsp:getvalueof var="fastLabConfig" param="element"/>
          </dsp:oparam>
  </dsp:droplet>
  <dsp:setvalue param="fastLabConfig" value="${fastLabConfig}" />
  <dsp:getvalueof var="yourStoreLabel" param="fastLabConfig.yourStoreLabel"/>
  <c:if test="${fn:length(yourStoreLabel) gt 30}">
    <dsp:getvalueof var="yourStoreLabel" value="${fn:substring(yourStoreLabel, 0, 30)}"/>
  </c:if>
  <dsp:getvalueof var="ccStoreMessageLong" param="fastLabConfig.ccStoreMessageLong"/>
  <c:if test="${fn:length(ccStoreMessageLong) gt 35}">
    <dsp:getvalueof var="ccStoreMessageLong" value="${fn:substring(ccStoreMessageLong, 0, 35)}"/>
  </c:if>
  <dsp:getvalueof var="leadformanceLinkValue" param="fastLabConfig.leadformanceLinkValue"/>
  <dsp:getvalueof var="bestOffers" param="fastLabConfig.bestOffers"/>
  <dsp:getvalueof var="changeStoreLabel" param="fastLabConfig.changeStoreLabel"/>
  <dsp:getvalueof var="ccStoreMessageShort" param="fastLabConfig.ccStoreMessageShort"/>
  <dsp:getvalueof var="chooseOtherStoreLabel" param="fastLabConfig.chooseOtherStoreLabel"/>
  <dsp:getvalueof var="chooseStoreLabel" param="fastLabConfig.chooseStoreLabel"/>
  <dsp:getvalueof var="chooseStoreDescr" param="fastLabConfig.chooseStoreDescr"/>
  <dsp:getvalueof var="searchStoreLabel" param="fastLabConfig.searchStoreLabel"/>
  <dsp:getvalueof var="chooseThisStoreLabel" param="fastLabConfig.chooseThisStoreLabel"/>
  <dsp:getvalueof var="searchInputTitle" param="fastLabConfig.searchInputTitle"/>
  <dsp:getvalueof var="linkToCatalogPage" param="fastLabConfig.linkToCatalogPage"/>
  <dsp:getvalueof var="closeButtonEnabled" param="fastLabConfig.closeButtonEnabled" />
  <dsp:getvalueof var="magasinsUrl" bean="/com/castorama/CastConfiguration.magasinsUrl" />
  <dsp:getvalueof var="magasinsUrl" value="${(not empty magasinsUrl && magasinsUrl != '')?magasinsUrl:#}"/>  
  <div id="votreMagasin">
	<div class="userShadow">
		<div class="m18">
		<dsp:getvalueof var="storeNom" bean="Profile.wrappedCurrentLocalStore.nom" scope="request"/>
        <dsp:getvalueof var="storeIsLocal" bean="Profile.currentLocalStore.localPrix" scope="request"/>
        <dsp:getvalueof var="storeURL" bean="Profile.wrappedCurrentLocalStore.storeUrl" scope="request"/>
		<dsp:getvalueof var="storeId" bean="Profile.wrappedCurrentLocalStore.storeId" scope="request"/>
        <dsp:getvalueof var="wrappedStoreIsCC" bean="Profile.wrappedCurrentLocalStore.retraitMagasin" scope="request"/>
        <dsp:getvalueof var="wrappedStoreId" bean="Profile.wrappedCurrentLocalStore.id" scope="request"/>
		<div class="gfLabel"><b>${yourStoreLabel}
            <c:choose>
                <c:when test="${storeURL ne null}">
                    <a href="${storeURL}" style="background: none; margin: 0px; padding: 0;" target="_blank">
                        ${storeNom}
                    </a>
				<dsp:getvalueof var="storeURL_blank" value="_blank"/>
                </c:when>
                <c:otherwise>
				<dsp:getvalueof var="storeURL" value="#"/>
                    ${storeNom}
                </c:otherwise>
            </c:choose></b>
            
		</div>    
         <div class="grayFrame">    
          <div class="container">
			<c:if test='${wrappedStoreIsCC}'>
              <dsp:getvalueof var="delayTime" bean="Profile.wrappedCurrentLocalStore.ccDelayPeriod" scope="request"/>
              <dsp:getvalueof var="delayUnit" bean="Profile.wrappedCurrentLocalStore.ccDelayPeriodUnit"/>
              <fmt:message key="header.mon.magasin.delayunit.hour" var="hour"/>
              <fmt:message key="header.mon.magasin.delayunit.day" var="day"/>
              <fmt:message key="header.mon.magasin.delayunit.days" var="days"/>
              <dsp:getvalueof var="displayDelayUnit" value="${(delayUnit == 0)?hour:(delayTime == 1?day:days)}" scope="request"/>
              <p class="shopNavLinkCC">${ccStoreMessageLong}${delayTime}${displayDelayUnit}</p>
            </c:if>	  
            <ul>    
              <li><a href="${storeURL}" target="${storeURL_blank}">${leadformanceLinkValue}</a></li>
              <li><a href="${linkToCatalogPage}">${bestOffers}</a></li>
			  <img src="../images/delimiter.png" id="logo" alt="Castorama.fr" class="delimiter" />
              <li><a href="${magasinsUrl}">${changeStoreLabel}</a></li>
            </ul>
          </div>
        </div>
	</div>	
  </div>
  </div>
  <div id="userLocatedPopup" aria-haspopup="true">
	  <div class="userShadow">
			<div class="m18">
			
			<div class="gfLabel"><b>
                <c:choose>
                <c:when test="${wrappedStoreId != '999'}">                
                    ${yourStoreLabel}
                    <c:choose>
                        <c:when test="${storeURL ne null}">
                            <a href="${storeURL}" style="background: none; margin: 0px; padding: 0;" target="_blank">
                                ${storeNom}
                            </a>
                        </c:when>
                        <c:otherwise>
                            ${storeNom}
                        </c:otherwise>
                    </c:choose>
                </c:when>
                <c:otherwise>
                    <fmt:message key="header.mon.magasin" />
                </c:otherwise>
                </c:choose>
                </b>
				<c:if test='${wrappedStoreIsCC}'>
				  <span class="shopNavLinkCC">${ccStoreMessageLong}${delayTime}${displayDelayUnit}</span>
				</c:if>
				
			</div>    
			 <div class="grayFrame">    
			  <div class="container">
              <c:choose>
                  <c:when test="${wrappedStoreId != '999'}">
                    <ul>    
                      <li><a href="${storeURL}" target="${storeURL_blank}">${leadformanceLinkValue}</a></li>
                      <li><a href="${linkToCatalogPage}">${bestOffers}</a></li>
                    </ul>
                  </c:when>
                  <c:otherwise>
                      <div class="managableLabel"><span>${chooseStoreDescr}</span></div>
                  </c:otherwise>
              </c:choose>
			  </div>
			</div>
			<div class="delimiter"></div>
			<div class="grayFrame">
				<div class="mapContainer">
					<div class="userLocatedMapWr">
						<div id="userLocatedMap">
						</div>
					</div>
					<div class="shopListContainer">
						<div class="shopListLabel"><b>${searchStoreLabel}</b></div>
						<div class="chooseShopInput">
							<form onsubmit="return geo.searchStore('#userLocatedPopup');">
                                <input type="hidden">
                                <input class="search-input" type="text" maxlength="40"/>
                                <input type="submit" class="goBtn" value="">
                            </form>
                            <div class="searchErrorMessage">
                                <fmt:message key="search_emptySearchResult.emptySearch.city" var="emptySearchCity"/>
                                <fmt:message key="search_emptySearchResult.emptySearch.pCode" var="emptySearchPCode"/>
                                <p class="emptySearchMessage"></p>
                            </div>
						</div>
						<div class="shopListWr">
						<script>
                            var searchForCityError = "${emptySearchCity}";
                            var searchForPCodeError = '${emptySearchPCode}';
							var chooseStoreMessage = '${chooseThisStoreLabel}';
                            var leadformanceLinkText = '${leadformanceLinkValue}';
						</script>
						<div class="customScrollBox" tabindex="0" style="overflow: hidden; ">

                            </div>
						</div>
						<div class="shopNavLinkContainer">
							<img src="/store/images/isCCmiddle.png">
							<p>=&nbsp;${ccStoreMessageShort}</p>
						</div>
					</div>
				</div>
			</div>
		</div>	
	  </div>
  </div>
  <div id="userLocatedPopupNoMap" aria-haspopup="true">
	  <div class="userShadow">
			<div class="m18">
			<div class="gfLabel"><b>
            <c:choose>
                <c:when test="${wrappedStoreId != '999'}">                
                    ${yourStoreLabel}
                    <c:choose>
                        <c:when test="${storeURL ne null}">
                            <a href="${storeURL}" style="background: none; margin: 0px; padding: 0;" target="_blank">
                                ${storeNom}
                            </a>
                        </c:when>
                        <c:otherwise>
                            ${storeNom}
                        </c:otherwise>
                    </c:choose>
                </c:when>
                <c:otherwise>
                    <fmt:message key="header.mon.magasin" />
                </c:otherwise>
            </c:choose></b>
				<c:if test='${wrappedStoreIsCC}'>
				  <span class="shopNavLinkCC">${ccStoreMessageLong}${delayTime}${displayDelayUnit}</span>
				</c:if>
				
			</div>    
			 <div class="grayFrame">    
			  <div class="container">
              <c:choose>
                  <c:when test="${wrappedStoreId != '999'}">
                    <ul>    
                      <li><a href="${storeURL}" target="${storeURL_blank}">${leadformanceLinkValue}</a></li>
                      <li><a href="${linkToCatalogPage}">${bestOffers}</a></li>
                    </ul>
                  </c:when>
                  <c:otherwise>
                      <div class="managableLabel"><span>${chooseStoreDescr}</span></div>
                  </c:otherwise>
              </c:choose>
			  </div>
			</div>
			<div class="delimiter"></div>
			<div class="grayFrame">
				<div class="mapContainer">
					<div class="shopListContainer">
						<div class="shopListLabel"><b>${searchStoreLabel}</b></div>
						<div class="chooseShopInput">
                            <form onsubmit="return geo.searchStore('#userLocatedPopupNoMap');">
                                <input type="hidden">
                                <input class="search-input" type="text"  maxlength="40"/>
                                <input type="submit" value="" class="goBtn">
                            </form>
                            <div class="searchErrorMessage">
                                <p class="emptySearchMessage"></p>
                            </div>
						</div>
						<div class="shopListWr">
							<script>
								var chooseStoreMessage = '${chooseThisStoreLabel}';
								var leadformanceLink = '${leadformanceLinkValue}';
                                var closeButtonEnabled = '${closeButtonEnabled}' == 'true';
							</script>
							<div class="customScrollBox" tabindex="0" style="overflow: hidden; ">

                            </div>
						</div>
						<div class="shopNavLinkContainer">
							<img src="/store/images/isCCmiddle.png">
							<p>=&nbsp;${ccStoreMessageShort}</p>
						</div>
					</div>
				</div>
			</div>
		</div>	
	  </div>
  </div>
  <div id="userNotLocatedPopup">
    <c:if test="${closeButtonEnabled}">
    <div class="closePopup" style="position: relative; height: 30px; background-color: #f1f1f1">
        <a href="javascript:void(0);">
            <span><fmt:message key="castCatalog_label.close.upper"/></span>
        </a>
    </div>
    </c:if>
	<div class="leftCol">
		<div class="logo">
			<img src="../images/logo.png"/>		
		</div>
		<div class="infoContainer">
			<div class="header">
				<div class="chooseMagLabel">
					<span>${chooseStoreLabel}</span>
				</div>
				<div class="managableLabel"><span>${chooseStoreDescr}</span></div>
			</div>
			<div class="mapWrapper">
				<div id="userNotLocatedMap">
				</div>
			</div>
			<div class="blockDelimiter"></div>
			<div class="shopListContainer">
				<div class="chooseShopInput">
					<form onsubmit="return geo.searchStore('#userNotLocatedPopup');">
                        <input type="hidden">
                        <input class="search-input" type="text" customph="${searchInputTitle}" maxlength="40"/>
                        <input type="submit" value="" class="goBtn">
                    </form>
                    <div class="searchErrorMessage">
                        <p class="emptySearchMessage"></p>
                    </div>
				</div>
				<div class="scrollBoxWr">
                    <div class="customScrollBox" tabindex="0" style="overflow: hidden; ">
                    </div>
                </div>
                <div class="shopNavLinkContainer">
					<img src="/store/images/isCCmiddle.png">
					<p>=&nbsp;${ccStoreMessageShort}</p>
				</div>
			</div>
		</div>
	</div>
	<div class="rightCol">
	</div>
  </div>
  <div id="userNotLocatedNoMap">
    <c:if test="${closeButtonEnabled}">
    <div class="closePopup" style="position: relative; height: 30px; background-color: #f1f1f1">
        <a href="javascript:void(0);">
            <span><fmt:message key="castCatalog_label.close.upper"/></span>
        </a>
    </div>
    </c:if>
	<div class="leftCol">
		<div class="logo">
			<img src="../images/logo.png"/>		
		</div>
		<div class="infoContainer">
			<div class="header">
				<div class="chooseMagLabel">
					<span>${chooseStoreLabel}</span>
				</div>
				<div class="managableLabel"><span>${chooseStoreDescr}</span></div>
			</div>
			<div class="shopListContainer">
				<div class="chooseShopInput">
					<form onsubmit="return geo.searchStore('#userNotLocatedNoMap');">
                        <input type="hidden">
                        <input class="search-input" type="text" customph="${searchInputTitle}" maxlength="40"/>
                        <input type="submit" value="" class="goBtn">
                    </form>
                    <div class="searchErrorMessage">
                        <p class="emptySearchMessage"></p>
                    </div>
				</div>
                <div class="scrollBoxWr">
                    <div class="customScrollBox" tabindex="0" style="overflow: hidden; ">
                    </div>
                </div>
                <div class="shopNavLinkContainer">
					<img src="/store/images/isCCmiddle.png">
					<p>=&nbsp;${ccStoreMessageShort}</p>
				</div>
			</div>
		</div>
	</div>
	<div class="rightCol">
    </div>
  </div>
  <div id="header">
    <div class="leftCol">
      <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
      <a href="${contextPath }"><img src="${pageContext.request.contextPath}/images/logo.png" id="logo" alt="Castorama.fr" class="topLogo" /><br></a>
      <dsp:getvalueof var="currentLocalStore" bean="Profile.currentLocalStore"/>
      <dsp:getvalueof var="currentLocalStoreId" bean="Profile.currentLocalStore.id" scope="request"/>
      <dsp:getvalueof var="wrappedCurrentLocalStore" bean="Profile.wrappedCurrentLocalStore"/>
      <dsp:getvalueof var="magasinsUrl" bean="/com/castorama/CastConfiguration.magasinsUrl" />
      <dsp:getvalueof var="emptyMagasinsUrl" value="${pageContext.request.contextPath}/magasin/magasin-carte-france.jsp"/>
      <dsp:getvalueof var="chooseStoreUrl" value="${(not empty magasinsUrl && magasinsUrl != '')?magasinsUrl:emptyMagasinsUrl}"/>
        <c:choose>
            <c:when test="${wrappedStoreId != '999'}">
                <div class="shopNavWr" id="shopContextInfo" aria-haspopup="true">
            </c:when>
            <c:otherwise>
                <div class="shopNavWr blueVotreMagasin" id="shopContextInfo" aria-haspopup="true">
            </c:otherwise>
        </c:choose>
            <dsp:getvalueof var="storeNom" bean="Profile.wrappedCurrentLocalStore.nom" scope="request"/>
            <dsp:getvalueof var="storeIsCC" bean="Profile.currentLocalStore.retraitMagasin" scope="request"/>
            <dsp:getvalueof var="storeIsLocal" bean="Profile.currentLocalStore.localPrix" scope="request"/>
            <dsp:getvalueof var="storeURL" bean="Profile.wrappedCurrentLocalStore.storeUrl" scope="request"/>
            <dsp:getvalueof var="wrappedStoreIsLocal" bean="Profile.wrappedCurrentLocalStore.localPrix" scope="request"/>
            <dsp:getvalueof var="wrappedStoreIsCC" bean="Profile.wrappedCurrentLocalStore.retraitMagasin" scope="request"/>
            <span class="shopVotreMagasin" id="changeStore"><fmt:message key="header.mon.magasin" /></span>
            <p class="monMagasin">
                <c:if test='${(wrappedStoreId != "999")}'>
                    <c:choose>
                        <c:when test='${storeURL ne null}'>
                            <a href="${storeURL}" target="_blank">
                            ${storeNom}
                            </a>
                        </c:when>
                        <c:otherwise>
                            <a href="#" target="">
                                ${storeNom}
                            </a>
                        </c:otherwise>
                    </c:choose>                    
                </c:if>
            </p>
            <c:if test='${wrappedStoreIsCC}'>
              <dsp:getvalueof var="delayTime" bean="Profile.wrappedCurrentLocalStore.ccDelayPeriod" scope="request"/>
              <dsp:getvalueof var="delayUnit" bean="Profile.wrappedCurrentLocalStore.ccDelayPeriodUnit"/>
              <fmt:message key="header.mon.magasin.delayunit.hour" var="hour"/>
              <fmt:message key="header.mon.magasin.delayunit.day" var="day"/>
              <fmt:message key="header.mon.magasin.delayunit.days" var="days"/>
              <dsp:getvalueof var="displayDelayUnit" value="${(delayUnit == 0)?hour:(delayTime == 1?day:days)}" scope="request"/>
              <p class="shopNavLinkCC">${ccStoreMessageLong}${delayTime}${displayDelayUnit}</p>
            </c:if>
            <c:if test='${(wrappedStoreId == "999")}'>
                <div class="chooseMagasinHeader">
                    <div class="link">
                    <a href="#">CHOISIR UN MAGASIN</a>
                    </div>
                </div>
            </c:if>
          </div>
      <div id="additionalLineLeftCol" aria-haspopup="true"></div>
    </div>
      <div class="rightCol">
        <div class="userSpaceBlock" id="mecLabel" aria-haspopup="true">
          <div class="usbTop">
            <dsp:a href="${pageContext.request.contextPath}/user/clientSpaceHome.jsp">
              <big><fmt:message key="header.my.client" /></big>
              <dsp:droplet name="/atg/dynamo/droplet/Switch">
                <dsp:param bean="Profile.transient" name="value"/>
                <dsp:oparam name="false">
                  <span>
                    <%@include file="../castCommon/includes/displayUserName.jspf" %>
                  </span>
                </dsp:oparam>    
                <%--<dsp:oparam name="true">
                  <big><fmt:message key="header.my.client" /></big>
                </dsp:oparam>--%>
              </dsp:droplet>
            </dsp:a>
          </div>
          <div class="usbBottom">
              <dsp:include page="includes/logout-link.jsp">
                <dsp:param name="closeImage" value="${pageContext.request.contextPath}/images/icoBlueArrow.png"/>
              </dsp:include>
          </div>
		<div id="additionalLineMec"></div>
        </div>
        <div class="userSpaceBlock" id="basketLabel">
          <div class="usbTop">
          
            <dsp:getvalueof var="aLinkPrefix" value="http://${aServerName}${aServerPort}${contextPath}"/>
            <dsp:a page="${aLinkPrefix}/checkout/cart.jsp">
              <img src="${pageContext.request.contextPath}/images/icoShoppingCart.png" width="16" height="16" alt="Mon panier"/>
              <big><fmt:message key="header.my.cart" /></big>
              <dsp:property bean="/com/castorama/CastShoppingCartFormHandler.continueURL" value="${requestURIwithQueryString}"/>
            </dsp:a>
            <dsp:setvalue beanvalue="/atg/commerce/ShoppingCart.totalCommerceItemCount" param="NumItems"/>
            <c:choose>
              <c:when test="${NumItems == 0}">
                <span class="articles"><dsp:a page="${aLinkPrefix}/checkout/cart.jsp">0 <fmt:message key="header.article" />
                  <dsp:property bean="/com/castorama/CastShoppingCartFormHandler.continueURL" value="${requestURIwithQueryString}"/>
                </dsp:a></span>
              </c:when>
              <c:otherwise>
                <span class="articles"><dsp:a page="${aLinkPrefix}/checkout/cart.jsp"><dsp:valueof param="NumItems"/>&nbsp;<fmt:message key="header.article" />
                  <dsp:property bean="/com/castorama/CastShoppingCartFormHandler.continueURL" value="${requestURIwithQueryString}"/>
                </dsp:a></span>
              </c:otherwise>
            </c:choose>
          </div>
          <div class="usbBottom">
              <div class="seeMyBasket">
            <dsp:a page="${aLinkPrefix}/checkout/cart.jsp">
              <img src="${pageContext.request.contextPath}/images/icoBlueArrow.png" alt='<fmt:message key="header.see.my.basket" />'/>
              <fmt:message key="header.see.my.basket" />
              <dsp:property bean="/com/castorama/CastShoppingCartFormHandler.continueURL" value="${requestURIwithQueryString}"/>
            </dsp:a>
                  </div>
          </div>
        </div>
      </div>
    </div>
    <div id="atoutPopup" loaded="false">
    </div>
</dsp:page>
