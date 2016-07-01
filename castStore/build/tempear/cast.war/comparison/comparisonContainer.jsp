<dsp:page>
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/atg/commerce/pricing/priceLists/PriceDroplet"/>
  <dsp:importbean bean="/com/castorama/CastShoppingCartFormHandler"/>
  <dsp:importbean bean="/com/castorama/droplet/CastComparisonDroplet"/>
  
  <dsp:importbean bean="/atg/userprofiling/SessionBean"/>
  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
  <dsp:getvalueof var="requestURI" bean="/OriginatingRequest.requestURI" />
  <dsp:getvalueof var="stockVisualizationDisabled" 
              bean="/com/castorama/stockvisualization/StockVisualizationManager.stockVisualizationDisabled" />

  <!-- script>
      $(document).ready(function(){
          $(".greyLink").hide();
      });
  </script-->

  <div class="whitePopupContent">
    <div class="whitePopupHeader">
      <h1><fmt:message key="castCatalog_comparator.title"/></h1>
      <fmt:message key="castCatalog_label.close" var="fermer"/>
      <fmt:message key="castCatalog_label.print" var="imprimer"/>
      <a href="javascript:void(0)" onclick="hidePopup(this)" class="closeBut" title="${fermer}"><span><!--~--></span>${fermer}</a>
      <dsp:a href="${pageContext.request.contextPath}/comparison/printComparisonContainer.jsp" target="_blank" iclass="imprimer" title="${imprimer}"><span><!--~--></span>${imprimer}</dsp:a>
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
              <c:forEach var="result" items="${compareProducts}">
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
                      <input type="hidden" id="name_<dsp:valueof param="sku.id"/>" value="<dsp:valueof param="sku.displayName"/>"/>
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
                            <dsp:param name="showedInComparator" value="true"/>
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
                        <td><dsp:valueof param="sku.${propertyName.key}"/></td>
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
                      <dsp:getvalueof var="skuId" param="sku.id"/>
                      <dsp:getvalueof var="productId" param="key.repositoryId"/>
                      <dsp:getvalueof var="imageSrc" param="sku.thumbnailImage.url"/>
                      <dsp:getvalueof var="isNotLogin" bean="Profile.transient"/>
                      <td>
                          <dsp:getvalueof var="templateUrl" param="key.template.url" />
                          <dsp:a href="${contextPath}${templateUrl}">
                            <fmt:message key="castCatalog_label.view.product"/>
                            <dsp:param name="productId" param="key.repositoryId" />
                            <dsp:param name="skuId" value="${skuId}"/>
                            <dsp:param name="navAction" value="jump"/>
                          </dsp:a>
                          <br/>
                          <c:choose>
                            <%-- check if sku is a pack --%>
                            <c:when test="${empty packs}">
                              <c:choose>
                                <c:when test="${isNotLogin}">
                                  <dsp:a href="${contextPath}/user/login.jsp">
                                    <fmt:message key="shoppinglist.addtolist"/>
                                    <dsp:param name="shoppingListSkuId" value="${skuId}"/>
                                    <dsp:param name="shoppingListAction" value="add"/>
                                    <dsp:param name="fromComp" value="true"/>
                                  </dsp:a>
                                </c:when>
                                <c:otherwise>
                                  <a href="#" onclick="addImageToShopListFromComp('${skuId}', '${productId}', '${imageSrc}' );">
                                    <fmt:message key="shoppinglist.addtolist"/>
                                  </a>
                                </c:otherwise>
                              </c:choose>
                            </c:when>
                            <c:otherwise>
                              <br/>
                            </c:otherwise>
                          </c:choose>
                      </td>
                    </dsp:oparam>
                  </dsp:droplet>
                </c:if>
              </c:forEach>
            </tr>

            <tr class="buttonsRow addToCartComparisonContainer">
              <th></th>
              <c:forEach var="result" items="${compareProducts}" varStatus='status'>
                <c:if test="${not empty result.value}">
                  <dsp:droplet name="ForEach">
                    <dsp:param name="array" value="${result.value}" />
                    <dsp:param name="elementName" value="sku"/>
                    <dsp:oparam name="output">
                      <dsp:getvalueof var="skuId" param="sku.id"/>
                      <dsp:getvalueof var="productId" param="key.repositoryId"/>
                      <dsp:getvalueof var="imageSrc" param="sku.thumbnailImage.url"/>
                      <dsp:getvalueof var="isNotLogin" bean="Profile.transient"/>
                      <td>
                        <%@ include file="/castCatalog/includes/addToCartComparisonContainer.jspf" %>
                        <%@ include file="/comparison/includes/comparisonAvailability.jspf" %>
                      </td>
                    </dsp:oparam>
                  </dsp:droplet>
                </c:if>
              </c:forEach>
            </tr>

            <c:if test="${!stockVisualizationDisabled}">
              <tr class="buttonsRow">
                <th></th>
                <c:forEach var="result" items="${compareProducts}" varStatus='status'>
                  <c:if test="${not empty result.value}">
                    <dsp:droplet name="ForEach">
                      <dsp:param name="array" value="${result.value}" />
                      <dsp:param name="elementName" value="sku"/>
                      <dsp:oparam name="output">
                        <td>
                          <%@ include file="/castCatalog/includes/stockLevelIndicatorComparisonContainer.jspf" %>
                        </td>
                      </dsp:oparam>
                    </dsp:droplet>
                  </c:if>
                </c:forEach>
              </tr>
            </c:if>
          </dsp:oparam>
        </dsp:droplet>
      </table>
    </div>
  </div>

</dsp:page>