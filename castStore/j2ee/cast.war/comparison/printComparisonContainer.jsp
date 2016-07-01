<%@ page contentType="text/html; charset=UTF-8"%>
<dsp:page>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
    <html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />       
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/main.css" />
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/cc_main.css" />
        <script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
    </head>
    <body class="print">
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
    <dsp:importbean bean="/atg/userprofiling/Profile"/>
    <dsp:importbean bean="/atg/commerce/pricing/priceLists/PriceDroplet"/>
    <dsp:importbean bean="/com/castorama/CastShoppingCartFormHandler"/>
    <dsp:importbean bean="/com/castorama/droplet/CastComparisonDroplet"/>
    
    <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
    <dsp:getvalueof var="requestURI" bean="/OriginatingRequest.requestURI" />
    <dsp:getvalueof var="activateOmniture" bean="/com/castorama/CastConfiguration.activateOmniture" />
    
    <fmt:message key="castCatalog_label.close" var="fermer"/>
    <fmt:message key="castCatalog_label.print" var="imprimer"/>

    <div class="whitePopupContent">
        <div class="whitePopupHeader">
            <img src="${pageContext.request.contextPath}/images/logo.png" id="logo" alt="Castorama.fr" class="topLogo" />
            <a href="javascript:void(0)" onclick="window.close();" class="closeBut" title="${fermer}"><span><!--~--></span>${fermer}</a>
            <a href="javascript:void(0)" onclick="window.print();" class="imprimer" title="${imprimer}"><span><!--~--></span>${imprimer}</a>
            <div class="clear"></div>
            <div class="addPrintInfo">
                <h1><fmt:message key="castCatalog_comparator.title"/></h1>
                <span class="printDate floatedLeft" id="printDate"></span>
                <dsp:getvalueof var="storeId" bean="Profile.currentLocalStore.id"/>
                <c:if test="${storeId != 999}">
                    <span class="printDate floatedLeft">
                        <fmt:message key="print.product.store"/>&nbsp;<dsp:valueof bean="Profile.currentLocalStore.nom"/>
                    </span>
                </c:if>
                <p><fmt:message key="castCatalog_comparator.print.attention"/></p>
            </div>
        </div>
        <div class="clear"><!--~--></div>
        <div class="popupContentContainer">
            <table class="compTable">
                <dsp:droplet name="CastComparisonDroplet">
                    <dsp:oparam name="output">
                        <dsp:getvalueof var="comparableProperties" param="comparableProperties"/>
                        <dsp:getvalueof var="propertiesNames" param="propertiesNames"/>
                        <dsp:getvalueof var="compareProducts" param="compareProducts"/>
                        
                        <tr class="productImages">
                            <th></th>
                            <c:forEach var="result" items="${compareProducts}" varStatus='status'>
                                <c:if test="${not empty result.value}">
                                    <dsp:droplet name="ForEach">
                                        <dsp:param name="array" value="${result.value}" />
                                        <dsp:param name="elementName" value="sku"/>
                                        <dsp:oparam name="output">
                                            <td>
                                                <dsp:include page="/comparison/includes/productImage.jsp">
                                                    <dsp:param name="sku" param="sku"/> 
                                                    <dsp:param name="product" param="key"/>
                                                </dsp:include>
                                            </td>
                                        </dsp:oparam>
                                    </dsp:droplet>
                                </c:if> 
                            </c:forEach>
                        </tr>
                        <tr class="compProdName">
                            <th></th>
                            <c:forEach var="result" items="${compareProducts}" varStatus='status'>
                                <c:if test="${not empty result.value}">
                                    <dsp:droplet name="ForEach">
                                        <dsp:param name="array" value="${result.value}" />
                                        <dsp:param name="elementName" value="sku"/>
                                        <dsp:oparam name="output">
                                            <td><dsp:valueof param="sku.displayName"/></td>
                                        </dsp:oparam>
                                    </dsp:droplet>
                                </c:if> 
                            </c:forEach>
                        </tr>
                        <tr class="logos">
                            <th></th>
                            <c:forEach var="result" items="${compareProducts}" varStatus='status'>
                                <c:if test="${not empty result.value}">
                                    <dsp:droplet name="ForEach">
                                        <dsp:param name="array" value="${result.value}" />
                                        <dsp:oparam name="output">
                                            <td>
                                              <dsp:include page="/castCatalog/includes/brandLink.jsp">
                                                <dsp:param name="isProductListingPage" value="true"/>
                                                <dsp:param name="product" param="key"/>
                                                <dsp:param name="className" value="greyLink"/>
                                                <dsp:param name="showImage" value="${true}"/>
                                              </dsp:include>
                                            </td>
                                        </dsp:oparam>
                                    </dsp:droplet>
                                </c:if> 
                            </c:forEach>
                        </tr>
                        <tr class="odd firstRow">
                            <th><fmt:message key="castCatalog_comparator.prix"/></th>
                            <c:forEach var="result" items="${compareProducts}" varStatus='status'>
                                <c:if test="${not empty result.value}">
                                    <dsp:droplet name="ForEach">
                                        <dsp:param name="array" value="${result.value}" />
                                        <dsp:param name="elementName" value="sku"/>
                                        <dsp:oparam name="output">
                                            <td>
                                                <div class="priceContent">
                                                    <dsp:include page="/checkout/includes/skuPrice.jsp">
                                                        <dsp:param name="thisProductId" param="key.id"/>
                                                        <dsp:param name="thisSku" param="sku"/>
                                                        <dsp:param name="hasProductsWithDiscount" value="false"/>
                                                    </dsp:include>
                                                </div>
                                            </td>
                                        </dsp:oparam>
                                    </dsp:droplet>
                                </c:if> 
                            </c:forEach>
                        </tr>
                        <tr>
                            <th><fmt:message key="castCatalog_comparator.garantie"/></th>
                            <c:forEach var="result" items="${compareProducts}" varStatus='status'>
                                <c:if test="${not empty result.value}">
                                    <dsp:droplet name="ForEach">
                                        <dsp:param name="array" value="${result.value}" />
                                        <dsp:param name="elementName" value="sku"/>
                                        <dsp:oparam name="output">
                                            <td><dsp:valueof param="sku.garantie" valueishtml="true"/></td>
                                        </dsp:oparam>
                                    </dsp:droplet>
                                </c:if> 
                            </c:forEach>
                        </tr>
                        
                        <c:forEach var="propertyName" items="${comparableProperties}" varStatus="status">
                            <c:choose>
                                <c:when test="${(status.index)%2 eq 0}">
                                    <c:set var="trclass" value=""/>
                                </c:when>
                                <c:when test="${status.last}">
                                    <c:set var="trclass" value="odd lastRow"/>
                                </c:when>
                                <c:otherwise>
                                    <c:set var="trclass" value="odd"/>
                                </c:otherwise>
                            </c:choose>
                            <tr class="<c:out value="${trclass}"/>">
                                <th>${propertyName.value}</th>
                                <c:forEach var="result" items="${compareProducts}" varStatus='status'>
                                    <c:if test="${not empty result.value}">
                                        <dsp:droplet name="ForEach">
                                            <dsp:param name="array" value="${result.value}" />
                                            <dsp:param name="elementName" value="sku"/>
                                            <dsp:oparam name="output">
                                                <td><dsp:valueof param="sku.${propertyName.key}" /></td>
                                            </dsp:oparam>
                                        </dsp:droplet>
                                    </c:if> 
                                </c:forEach>
                            </tr>
                        </c:forEach>
                        
                        <tr class="buttonsRow borderTh">
                          <th></th>
                          <c:forEach var="result" items="${compareProducts}" varStatus='status'>
                                <c:if test="${not empty result.value}">
                                    <dsp:droplet name="ForEach">
                                        <dsp:param name="array" value="${result.value}" />
                                        <dsp:param name="elementName" value="sku"/>
                                        <dsp:oparam name="output">
                                            <td><%@ include file="/comparison/includes/comparisonAvailability.jspf" %></td>
                                        </dsp:oparam>
                                    </dsp:droplet>
                                </c:if> 
                          </c:forEach>
                        </tr>
                    </dsp:oparam>
                </dsp:droplet>  
            </table>
        </div>
        
        <div class="whitePopupHeader">
            <a href="javascript:void(0)" onclick="window.print();" class="imprimer" title="${imprimer}"><span><!--~--></span>${imprimer}</a>
        </div>
        <div class="clear"></div>
    </div>
    
     <script type="text/javascript">
            var now = new Date();
            var day = now.getDate();
            var month = now.getMonth() + 1;
            month = month + "";
            if (month.length == 1) {
               month = "0" + month;
            }
            var year = now.getFullYear();
            var hours = now.getHours();
            var minutes = now.getMinutes();
            minutes = minutes + "";
            if (minutes.length == 1) {
               minutes = "0" + minutes;
            }
            var timeValue = "Date: " + day + "/" + month + "/" + year + " - " + hours + ":" + minutes; 
            document.getElementById("printDate").innerHTML = timeValue;
            
            $(document).ready(function(){
                //productItem height correction if LIVRAISON OFFERTE exists
                $("span.livreSV2_3").each(function(){
                    if ($(this).find(".fGratuite").length != 0){
                        $(this).addClass('livreWithGratuite');
                    }
                });
            });
     </script>
<c:if test="${activateOmniture}">        
    <dsp:include page="/includes/googleAnalytics.jspf"/>
</c:if>
    </body>
</html>

</dsp:page>