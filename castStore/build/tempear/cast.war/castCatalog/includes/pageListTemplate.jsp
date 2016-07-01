<dsp:page>

  <dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  <dsp:importbean bean="/com/castorama/CastPriceRangeDroplet"/>
  <dsp:importbean bean="/atg/commerce/pricing/priceLists/PriceDroplet"/>
  <dsp:importbean bean="/com/castorama/droplet/CastGetApplicablePromotions"/>
  
  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />

  <div class="productListe">
    <table class="productsTable" cellspacing="0" cellpadding="0">
      <tbody>
        <%-- Following three lines of code are added to support the Omniture functionality.
             "productsList" variable is modified during iteration in listProductTemplate.jsp file. --%>
          <c:set var="productsList" value=""  scope="request"/>
          <dsp:getvalueof param="results" var="results"/>
          <c:set var="size" value="${fn:length(results)}"/>
          <dsp:droplet name="ForEach">
          <dsp:param name="array" param="results"/>
          <dsp:oparam name="output">
            <dsp:getvalueof var="count" param="count"/>
            <c:choose>
              <c:when test="${count mod 2 == 0}">
                <tr class="odd" id="productItemWithPrice">
              </c:when>
              <c:otherwise>
                <tr id="productItemWithPrice">
              </c:otherwise>
            </c:choose>
            <dsp:getvalueof var="productId" param="element.document.properties.$repositoryId"/>
            <dsp:include page="../../castCatalog/listProductTemplate.jsp">
              <dsp:param name="productId" value="${productId}"/>
            </dsp:include>
            </tr>
            <dsp:droplet name="ProductLookup">
              <dsp:param name="id" value="${productId}"/>
              <dsp:oparam name="output">
                <dsp:param name="product" param="element"/>
                <dsp:droplet name="CastPriceRangeDroplet">
                  <dsp:param name="productId" value="${productId}"/>
                  <dsp:oparam name="output">
                    <dsp:droplet name="CastGetApplicablePromotions">
                      <dsp:param name="item" param="sku"/>
                      <dsp:oparam name="output">
                        <dsp:droplet name="ForEach">
                          <dsp:param name="array" param="promotions"/>
                          <dsp:oparam name="output">
                            <dsp:getvalueof var="description" param="element.description"/>
                            <c:if test="${not empty description}">
                              <c:choose>
                                <c:when test="${count mod 2 == 0}">
                                  <tr class="odd">
                                    <td colspan="5" class="offers">
                                      <div class="grayCorner grayCornerGray preMessageLayer">
                                        <div class="grayBlockBackground"><!--~--></div>
                                        <div class="cornerBorderGrayBg cornerTopLeft"><!--~--></div>
                                        <div class="cornerBorderGrayBg cornerTopRight"><!--~--></div>
                                        <div class="cornerBorderGrayBg cornerBottomLeft"><!--~--></div>
                                        <div class="cornerBorderGrayBg cornerBottomRight"><!--~--></div>
                                </c:when>
                                <c:otherwise>
                                  <tr>
                                    <td colspan="5" class="offers">
                                      <div class="grayCorner grayCornerWhite preMessageLayer">
                                        <div class="grayBlockBackground"><!--~--></div>
                                                      <div class="cornerBorder cornerTopLeft"><!--~--></div>
                                                      <div class="cornerBorder cornerTopRight"><!--~--></div>
                                                      <div class="cornerBorder cornerBottomLeft"><!--~--></div>
                                                      <div class="cornerBorder cornerBottomRight"><!--~--></div> 
                                </c:otherwise>
                              </c:choose>
                              
                              <div class="preMessage">
                                <img src="${contextPath}/images/icoPresent.gif" class="icoChopCart" />
                                <table cellpadding="0" cellspacing="0" class="emilateValignCenter">
                                 <tbody>
                                  <tr>
                                    <td>
                                      <dsp:valueof value="${description}" valueishtml="true"/>
                                    </td>
                                  </tr>
                                 </tbody> 
                                </table>
                              </div>
                              </div>
                              </td>
                              </tr>
                            </c:if>  
                          </dsp:oparam>
                        </dsp:droplet> 
                      </dsp:oparam>
                    </dsp:droplet>
                  </dsp:oparam>
                </dsp:droplet>
              </dsp:oparam>
            </dsp:droplet>  
            
            <%-- Following line of code is added to support the Omniture functionality. --%>
            <c:if test="${size != count}"><c:set var="productsList" value="${productsList},"  scope="request"/></c:if>
          </dsp:oparam>
        </dsp:droplet>
      </tbody>
    </table>
  </div>
  
  
    <%-- Omniture params Section begins--%>
    <fmt:message var="omnitureChannel" key="omniture.channel.catalogue"/>
    <c:set var="omnitureChannel" value="${omnitureChannel}" scope="request"/>
    <c:if test="${size > 0}">
        <c:set var="omnitureProducts" value="${productsList}" scope="request"/>
    </c:if>
     <fmt:message var="omniturePState" key="omniture.state.product.list"/>
    <c:set var="omniturePState" value="${omniturePState}" scope="request"/>
    <%-- Omniture params Section ends--%>
  
</dsp:page>