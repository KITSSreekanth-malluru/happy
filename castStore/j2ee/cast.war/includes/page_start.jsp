<%@ page contentType="text/html; charset=UTF-8"%>
<dsp:page>
    <dsp:importbean var="originatingRequest" bean="/OriginatingRequest"/>
    <dsp:importbean bean="/atg/userprofiling/Profile"/>
    <dsp:importbean bean="/com/castorama/bazaarvoice/utils/BVConfiguration"/>
    <dsp:importbean var="dynamoConfig" bean="/atg/dynamo/Configuration" />
    <dsp:importbean var="castConfig" bean="/com/castorama/CastConfiguration" />
    <dsp:importbean var="contextTools" bean="/com/castorama/util/ContextTools" />
    <dsp:importbean bean="/com/castorama/ClearBasketFormHandler" />
    <dsp:getvalueof var="serverName" vartype="java.lang.String" value="${dynamoConfig.siteHttpServerName}" />
    <dsp:getvalueof var="serverPort" vartype="java.lang.String" value=":${dynamoConfig.siteHttpServerPort}" />
    <c:if test='${serverPort == ":80"}'>
        <dsp:getvalueof var="serverPort" value="" />
    </c:if>
    <dsp:getvalueof var="httpServer" vartype="java.lang.String" value="${pageContext.request.scheme}://${serverName}${serverPort}" />
    <dsp:getvalueof var="documentDomain" bean="BVConfiguration.documentDomain"/>
    <dsp:getvalueof var="bvRoot" bean="BVConfiguration.bvRoot"/>
    <dsp:getvalueof var="bvDisplayCode" bean="BVConfiguration.bvDisplayCode"/>
    <dsp:getvalueof var="requestURIwithQueryString" bean="/OriginatingRequest.requestURIwithQueryString" scope="request"/>
    <dsp:getvalueof var="forwardRequestURI" value="${requestScope['javax.servlet.forward.request_uri']}" />
    <dsp:getvalueof var="forwardQueryString" value="${requestScope['javax.servlet.forward.query_string']}" />
    <c:if test="${not empty forwardQueryString}">
      <dsp:getvalueof var="forwardQueryString" value="?${forwardQueryString}" />
    </c:if>
    <c:if test="${not empty forwardRequestURI}">
      <dsp:getvalueof var="requestURIwithQueryString" value="${forwardRequestURI}${forwardQueryString}" scope="request"/>
    </c:if>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" class="castorama">
<head>
  <meta http-equiv="X-UA-Compatible" content="IE=8"/>
    <dsp:getvalueof var="bvInclude" param="bvInclude" />
    <c:if test='${bvInclude == "view"}'>
        <script type="text/javascript" language="javascript">
        <!--
        document.domain = "${documentDomain}";
        function ratingsDisplayed(totalReviewsCount, avgRating, ratingsOnlyReviewCount, recommendPercentage, productID)
        {
            if (totalReviewsCount == 0) {
                var bvRevCntr = document.getElementById("BVReviewsContainer");
                    var bvSVPLink = document.getElementById("BVSVPLinkContainer");
       
                    if (bvRevCntr) { bvRevCntr.style.display = "none"; }
                    if (bvSVPLink) { bvSVPLink.style.display = "none"; }
            } else {
                showReviewTab();
            }
        }
        //-->
        </script>
    </c:if>    

    <c:if test='${bvInclude == "submit"}'>
        <script type="text/javascript" language="javascript">
            <!--
            document.domain = "${documentDomain}";
            //-->
        </script>
        <meta name="ROBOTS" content="NOINDEX, NOFOLLOW" />
        <script type="text/javascript" language="javascript">
            <!--
            function pageChanged(pageName, pageStatus) {
                // TODO: place web analytics tagging code here to track submission process
            }
            //-->
        </script>
    </c:if>    

        <dsp:getvalueof var="metaInfoInclude" param="metaInfoInclude" />
        <c:choose>
                <c:when test="${metaInfoInclude != null}">
                        <dsp:include page="${metaInfoInclude}" />
                </c:when>
                <c:otherwise>
                        <dsp:getvalueof var="metaKeyword" param="metaKeyword" />
                        <dsp:getvalueof var="metaDescription" param="metaDescription" />
                        <dsp:getvalueof var="title" param="title" />
                        <c:if test="${title == null}">
                                <dsp:getvalueof var="title" value="Castorama.fr" />
                        </c:if>
                        <c:if test="${metaKeyword == null}">
                                <dsp:getvalueof var="metaKeyword" value="" />
                        </c:if>
                        <c:if test="${metaDescription == null}">
                                <dsp:getvalueof var="metaDescription" value="" />
                        </c:if>
                        <title>${title}</title>
                        <meta name="description" content="${metaDescription}" />
                        <meta name="keywords" content="${metaKeyword}" />
                </c:otherwise>
        </c:choose>

    <dsp:getvalueof var="canonicalUrl" param="canonicalUrl" />

        <meta name="google-site-verification" content="OGDv3v0xaSb7L8p1A-Nd7nKlZvV1BKkbIEUO76O124U" />
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
        <meta name="p:domain_verify" content="48479c239a853c5ab1fb16059dc4492d"/> 

        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/main.css" />
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/cc_main.css" />

    <c:if test='${bvInclude == "view"}'>
        <link rel="stylesheet" type="text/css" href="${bvRoot}/static/${bvDisplayCode}/bazaarvoice.css" />
    </c:if>
    <c:if test='${bvInclude == "submit"}'>
        <link rel="stylesheet" type="text/css" href="${bvRoot}/static/${bvDisplayCode}/bazaarvoice.css" />
        <link rel="stylesheet" type="text/css" href="${bvRoot}/static/${bvDisplayCode}/bazaarvoiceUI.css" />
    </c:if>
    <!--[if lte IE 6]><link type="text/css" href="${pageContext.request.contextPath}/styles/ie6.css" rel="stylesheet" media="screen" /><![endif]-->
    <!--[if lte IE 6]><script type="text/javascript" src="${pageContext.request.contextPath}/js/iepngfix_tilebg.js"></script><![endif]-->
    <!--[if lte IE 8]>
      <style>
        .boxCartPromotion+.noCurrentBox {background-color:#F0F0F0; opacity: 0.7;}
      </style>
    <![endif]-->
    <!--[if IE]>
      <style>
        .boxCartTableFooter .castCartNew .customCheckbox {margin-top:25px; padding-left:5px;}
        .orderHistoryBoxCartTitle .sous-total {padding-right:15px;}
        .innerSous-total .newprice {margin-right:11px;}
        .rightPanelContent.cart_info .boxCartTable .boxCartTableSummaryTitle + td h5 {margin-top:4px; padding-bottom:20px;}
      </style>
    <![endif]-->
    <!--[if IE 9]>
      <style>
        .rightPanelContent.cart_info .boxCartTable .boxCartTableSummaryTitle + td p {padding-bottom:7px;}
      </style>
    <![endif]-->

        <script type="text/javascript">var contextPath = "${pageContext.request.contextPath}";</script> 
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
        <c:if test='${castConfig.googleMapEnabled}'>
            <!--<script type="text/javascript" src="http://www.google.com/jsapi"></script>-->
            <script type="text/javascript" src="${pageContext.request.contextPath}/js/markerclusterer.js"></script>
        </c:if>
		<!--<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.easing.js"></script>-->
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.mousewheel.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.mCustomScrollbar.js"></script>
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/main.js?v=${castConfig.currentDate}"></script>
        <!-- Maxymiser script start -->
        <script type="text/javascript" src="//service.maxymiser.net/cdn/fr.castorama/js/mmapi.js"></script>
        <!-- Maxymiser script end -->
        <script>
            var display = {
                banner : Boolean(<dsp:valueof bean="Profile.catalog.displayBanner"/>),
                stores : Boolean(<dsp:valueof bean="Profile.catalog.displayStores"/>),
                eraseBasketPopup : Boolean(<dsp:valueof param="contextState.displayEraseBasketPopup"/>),
                bonnesAffairesPopup : Boolean(<dsp:valueof param="contextState.displayBonnesAffairesPopup"/>)
            };
            var COOKIE_NAME = '${contextTools.cookieName}';
            var COOKIE_AGE = Number(<dsp:valueof bean="Profile.catalog.cookieAge"/>);
            var COOKIE_PATH = '${contextTools.cookiePath}';
            var WEB_CONTEXT_STORE_ID = Number(${contextTools.webContextStoreId});
            var LEADFORMANCE_STORE_URL_PARAM = "magasinId";
            var OMNITURE_SYNCH_DELAY = "${castConfig.omnitureSynchDelay}";
            var TMP_COOKIE_NAME = 'dpe';
            var DISPLAY_GEO_POPUP = 'dgp';
            var DISPLAY_DURATION = '${castConfig.popupDisplayingDurationSeconds}';
            var MAP_ENABLED = '${castConfig.googleMapEnabled}' == 'true';
            var GOOGLE_API_ENABLED = '${castConfig.googleApiEnabled}' == 'true';
                
        </script>
    <c:if test="${castConfig.googleMapEnabled || castConfig.googleApiEnabled}">
        <script type="text/javascript" src="${castConfig.googleGeolocationAPIUrl}${castConfig.googleGeolocationAPIKey}"></script>
    </c:if>
    <c:if test='${bvInclude == "view"}'>
        <script type="text/javascript" src="${bvRoot}/static/${bvDisplayCode}/bazaarvoice.js"></script>
    </c:if>
    <c:if test='${bvInclude == "submit"}'>
        <script type="text/javascript" src="${bvRoot}/static/${bvDisplayCode}/bazaarvoiceUI.js"></script>
    </c:if>
    
    <c:if test='${canonicalUrl != null && not empty canonicalUrl}'>
        <link rel="canonical" href="${httpServer}${canonicalUrl}" />
    </c:if>
    
    </head>
        <dsp:getvalueof var="bodystyle" value="" />
        <c:catch var="missingCatalogPropertyException">
          <dsp:getvalueof var="bodystyle" bean="Profile.catalog.bodystyle"/>
        </c:catch>
        <c:if test="${missingCatalogPropertyException != null}">
             <dsp:getvalueof var="profile_id" bean="Profile.repositoryId"/>
             <dsp:droplet name="/com/castorama/droplet/LoggingDroplet">
            <dsp:param name="message" value="Can't find property named: catalog in class: atg.userprofiling.Profile(id : ${profile_id})"/>
            <dsp:param name="logLevel" value="warn"/>
          </dsp:droplet>
        </c:if>
        
    <body style="${bodystyle}">
        <div class="whitePopupContainer" id="geoPopup">
            <div class="whitePopupContent popupFormContainer geoPopUpWr">
                <div class="whitePopupHeader">
                    <fmt:message key="castCatalog_label.close" var="fermer"/>
                    <a href="javascript:void(0)" onclick="hidePopup(this)" class="closeBut" title="${fermer}">
                        <span> <!--~--> </span>${fermer}</a>
                </div>
                <div class="clear">
                    <!--~-->
                </div>
                <div class="popupContentContainer">
                    <div id="mainPartContainer"></div>
                    <div id="allStoresContainer">
                        <h4><fmt:message key="cc.popup.geo.title"/></h4>
                        <div class="chooseShopSelectorWr">
                            <dsp:getvalueof var="query" value="entite.adresse.departement.numero != 999" />
                            <dsp:form name="geoForm">
                                <dsp:select bean="" name="magasin" iclass="styled" id="geo">
                                    <dsp:option value=""><fmt:message key="msg.newsletter.choose.store" /></dsp:option>
                                    <dsp:droplet name="/atg/dynamo/droplet/RQLQueryForEach">
                                        <dsp:param name="queryRQL" value="${query}" />
                                        <dsp:param name="repository" bean="/atg/registry/Repository/MagasinGSARepository" />
                                        <dsp:param name="itemDescriptor" value="magasin" />
                                        <dsp:param name="elementName" value="magasinRQL" />
                                        <dsp:param name="sortProperties" value="+entite.adresse.cp,+nom" />
                                        <dsp:oparam name="output">
                                            <dsp:getvalueof var="iscc" param="magasinRQL.retraitMagasin"/>
                                            <dsp:getvalueof var="castStoreId" param="magasinRQL.storeId"/>
                                            <dsp:option paramvalue="magasinRQL.id" iclass="${iscc?'iscc':''}" id="castStoreId_${castStoreId}">
                                                <dsp:valueof param="magasinRQL.entite.adresse.cp" /> - <dsp:valueof param="magasinRQL.nom" />
                                            </dsp:option>
                                        </dsp:oparam>
                                    </dsp:droplet>
                                </dsp:select>
                            </dsp:form>
                            <input value="" class="goBtn" id="confirm" />
                            <img src="/store/images/choosenShopSelectorBg.png" />
                        </div>
                        <div class="removingStore">= <fmt:message key="cc.retrait.en.magasin"/></div>
                    </div>
                </div>
            </div>
        </div>

        <div class="whitePopupContainer" id="eraseBasketPopup">
            <div class="whitePopupContent popupFormContainer">
                <div class="whitePopupHeader">
                    <!--~-->
                </div>
                <div class="clear">
                    <!--~-->
                </div>
                <div class="popupContentContainer">
                    <dsp:form method="POST" action="${requestURIwithQueryString}" name="eraseBasketForm" formid="eraseBasketForm">
                        <h2><fmt:message key="cc.popup.eraseBasket.title" /></h2>
                        <div class="text">
                            <p><fmt:message key="cc.popup.eraseBasket.text.1" /></p>
                            <p><fmt:message key="cc.popup.eraseBasket.text.2" /></p>
                        </div>
                        <div class="buttons">
                            <span class="ccBlueButton cancelButton"><button onclick="geo.forceChangeContext(<dsp:valueof param="contextState.currentContext"/>)" class="ccBlueButton"><fmt:message key="cc.popup.buttons.annuler" /></button></span>
                            <span class="ccBlueButton"><button onclick="geo.forceChangeContext(<dsp:valueof param="contextState.currentContext"/>)" class="ccBlueButton"><fmt:message key="cc.popup.buttons.savebasket" /></button></span>
                            <span class="ccBlueButton"><button onclick="geo.forceChangeContext(<dsp:valueof param="contextState.newContext"/>, <dsp:valueof param="contextState.newContextCastStoreId"/>)" class="ccBlueButton"><fmt:message key="cc.popup.buttons.erasebasket" /></button></span>
                            <dsp:input bean="ClearBasketFormHandler.successURL" type="hidden" value="${requestURIwithQueryString}" priority="1"/>
                            <dsp:input bean="ClearBasketFormHandler.useContext" type="hidden" value=""/>
                        </div>
                    </dsp:form>
                </div>
            </div>
        </div>

        <div class="whitePopupContainer" id="bonnesAffairesPopup">
            <div class="whitePopupContent popupFormContainer baUpWr">
                <div class="whitePopupHeader">
                    <!--~-->
                </div>
                <div class="clear">
                    <!--~-->
                </div>
                <div class="popupContentContainer">
                    <dsp:form method="POST" action="${requestURIwithQueryString}" name="bonnesAffairesForm" formid="bonnesAffairesForm">
                        <dsp:input bean="ClearBasketFormHandler.successURL" type="hidden" value="${requestURIwithQueryString}" priority="1"/>
                        <dsp:input bean="ClearBasketFormHandler.useContext" type="hidden" value=""/>
                    </dsp:form>
                    
                    <h2><fmt:message key="cc.popup.bonnesAffaires.title" /></h2>
                    <div class="text">
                        <p><fmt:message key="cc.popup.bonnesAffaires.text.1" /></p>
                        <p><fmt:message key="cc.popup.bonnesAffaires.text.2" /></p>
                        <p><fmt:message key="cc.popup.bonnesAffaires.text.3" /></p>
                    </div>
                    <div class="buttons">
                        <span class="ccBlueButton cancelButton"><button onclick="geo.baChangeContext()" class="ccBlueButton"><fmt:message key="cc.popup.buttons.annuler" /></button></span>
                        <span class="ccBlueButton"><button onclick="geo.baChangeContext('<dsp:valueof param="contextState.savedBAProductId"/>')" class="ccBlueButton"><fmt:message key="cc.popup.buttons.continue" /></button></span>
                    </div>
                </div>
            </div>
        </div>

        <div id="topBlueLineBg"></div>
        <div class="gray_overlay"></div>
        <div id="whiteContainer">
        <div class="m10">

     <c:if test="${not empty originatingRequest.requestURI && !fn:contains(originatingRequest.requestURI, 'login.jsp') && !fn:contains(originatingRequest.requestURI, 'createAccount.jsp')&& !fn:contains(originatingRequest.requestURI, 'delivery.jsp')&& !fn:contains(originatingRequest.requestURI, 'payment.jsp')&& !fn:contains(originatingRequest.requestURI, 'confirmation.jsp')&& !fn:contains(originatingRequest.requestURI, '404.jsp')}" >
         <c:if test="${not empty  originatingRequest.queryString}" >
               <dsp:getvalueof var="queryString" value="?${originatingRequest.queryString}" />
         </c:if>
           <dsp:setvalue bean="/atg/userprofiling/SessionBean.values.loginSuccessURL" value="${originatingRequest.requestURI}${queryString}" />
     </c:if>
     
     <script type="text/javascript">
      var sendOmnitureIfPageLoad = false;
    </script>
        
</dsp:page>
