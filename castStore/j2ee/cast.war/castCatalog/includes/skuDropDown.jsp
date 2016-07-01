<dsp:page>

  <dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  <dsp:importbean bean="/com/castorama/droplet/CastSortSkuDropDownDroplet"/>
  <dsp:importbean bean="/com/castorama/droplet/CastCategoryLinkDroplet"/>
  <dsp:importbean bean="/com/castorama/droplet/CastProductLinkDroplet"/>

  <dsp:getvalueof var="productId" param="productId"/>
  <dsp:getvalueof var="categoryId" param="categoryId"/>
  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
  <dsp:getvalueof var="requestURI" bean="/OriginatingRequest.requestURI"/>
  <dsp:getvalueof var="featuredProduct" param="featuredProduct"/>
  <dsp:getvalueof var="productListingView" param="productListingView"/>

  <div class="sorter productTaille">
    <div class="ddWrapper">
      <dsp:droplet name="ProductLookup">
        <dsp:param name="id" value="${productId}"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="choisePhrase" param="element.libelleListe"/>
          <c:if test="${empty choisePhrase || choisePhrase == ''}">
            <fmt:message var="choisePhrase" key="castCatalog_includes_skuDropDown.choose"/>
          </c:if>
          
          <c:choose>
            <c:when test="${featuredProduct}">
              <dsp:getvalueof var="paramSkuId" param="featuredSkuId"/>
              <dsp:droplet name="CastCategoryLinkDroplet">
                <dsp:param name="categoryId" value="${categoryId}"/>
                <dsp:param name="navCount" bean="/Constants.null"/>
                <dsp:param name="navAction" value="jump"/>
                <dsp:param name="productListingView" value="${productListingView}"/>
                <dsp:oparam name="output">
                  <dsp:getvalueof var="template" param="url"/>
                </dsp:oparam>
              </dsp:droplet>
              
              <script>
                function featuredProd(selectedValue) {
                  if (selectedValue != '${choisePhrase}') {
                      var url='${template}';
                      var separator='?';
                      var index = url.indexOf('?');
                      if(index != -1) {
                        separator = '&';
                      }
                      location.href='${contextPath}${template}'+ separator + 'featuredSkuId='+ selectedValue;
                  }
                }
              </script>
              <select class="styled"  name="searchSorterSelect" id="featuredProd" rel="featuredProd" onChange="location.href='${contextPath}${template}&featuredSkuId='+ this.options[this.selectedIndex].value">
            </c:when>
            <c:otherwise>
              <dsp:getvalueof var="paramSkuId" param="skuId"/>
              <dsp:droplet name="CastProductLinkDroplet">
                <dsp:param name="productId" value="${productId}"/>
                <dsp:param name="navAction" param="navAction"/>
                <dsp:param name="navCount" param="navCount"/>
                <dsp:param name="ba" value="${baFakeContext}"/>
                <dsp:param name="hideBreadcrumbs" value="${bonnesAffaires}"/>
                <dsp:oparam name="output">
                  <dsp:getvalueof var="templateUrl" param="url"/>
                </dsp:oparam>
              </dsp:droplet>
              
              <script>
                function changeMultiskuProduct(selectedValue) {
                    if (selectedValue != '${choisePhrase}') {
                      var url='${templateUrl}';
                      var separator='?';
                      var index = url.indexOf('?');
                      if(index != -1) {
                        separator = '&';
                      }
                      location.href='${contextPath}${templateUrl}'+ separator + 'skuId='+ selectedValue;
                    }
                }
              </script>
              
              <select class="styled" id="multiskuPage" rel="mutisku" name="searchSorterSelect">
            </c:otherwise>
          </c:choose>
          
          <c:if test="${empty paramSkuId}">
            <dsp:getvalueof var="dropDownValue" param="element.libelleListe"/>
            <c:choose>
              <c:when test="${empty dropDownValue}">
                <option><fmt:message key="castCatalog_includes_skuDropDown.choose"/></option>
              </c:when>
              <c:otherwise>
                <option><dsp:valueof value="${dropDownValue}"/></option>
              </c:otherwise>
            </c:choose>
          </c:if>
          <dsp:droplet name="CastSortSkuDropDownDroplet">
            <dsp:param name="childSKUs" param="element.childSKUs"/>
            <dsp:param name="isMultiSku" param="isMultiSku" />
            <dsp:param name="store" bean="/atg/userprofiling/Profile.currentLocalStore" />
            <dsp:param name="svAvailableMap" value="${svAvailableMap}" />
            <dsp:oparam name="output">
              <dsp:getvalueof var="skuList" param="skuList"/>
            </dsp:oparam>
          </dsp:droplet>
          
          <%-- Following line of code is added to support the Omniture functionality. --%>
          <c:set var="childSkusCodesList" value=""  scope="request"/>
          <dsp:droplet name="ForEach">
            <dsp:param name="array" value="${skuList}"/>
            <dsp:oparam name="output">
              <dsp:getvalueof var="skuId" param="element.repositoryId"/>
              <dsp:getvalueof var="thisCodeArticle" param="element.CodeArticle"/>
              <%-- Following line of code is added to support the Omniture functionality. --%>
              <c:choose>
                <c:when test="${not empty childSkusCodesList}">
                  <c:set var="childSkusCodesList" value="${childSkusCodesList},${thisCodeArticle}"  scope="request"/>
                </c:when>
                <c:otherwise>
                  <c:set var="childSkusCodesList" value="${thisCodeArticle}"  scope="request"/>
                </c:otherwise>
              </c:choose>
              <c:choose>
                <c:when test="${skuId==paramSkuId}">
                  <dsp:getvalueof var="choosenSkuId" value="${thisCodeArticle}" scope="request"/>
                  <option value="${skuId}" selected><dsp:valueof param="element.displayName"/></option>
                </c:when>
                <c:otherwise>
                  <option value="${skuId}"><dsp:valueof param="element.displayName"/></option>
                </c:otherwise>
              </c:choose>
            </dsp:oparam>
          </dsp:droplet>
          </select>
        </dsp:oparam>
      </dsp:droplet>
    </div>
  </div>
</dsp:page>