<dsp:page>
  <dsp:getvalueof var="name" param="product.displayName" />
  <dsp:getvalueof var="comparatorImage" param="sku.comparatorImage.url" />
  <dsp:getvalueof var="templateUrl" param="templateUrl" />
  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
  <dsp:getvalueof var="skuId" param="sku.repositoryId"/>
  <dsp:getvalueof var="codeArticle" param="sku.codeArticle"/>
  <dsp:getvalueof var="productId" param="product.repositoryId"/>
  
  <dsp:getvalueof var="skuList" param="skuList}"/>
  <dsp:getvalueof var="skuCodeArticleList" param="skuCodeArticleList"/>
  <dsp:getvalueof var="imgSrcList" param="imgSrcList"/>
  <dsp:getvalueof var="namesList" param="namesList"/>
  <dsp:getvalueof var="productList" param="productList"/>
  <dsp:getvalueof var="isMultySKU" param="isMultySKU"/>
  <dsp:getvalueof var="listPrice" param="listPrice"/>
  
  <div class="elargeProduct">
    <div class="elargeProductContent">
      <div class="elargeProductImage">
        <c:choose>
		  <c:when test="${(not empty isMultySKU) and ((fn:length(isMultySKU) > 1) or (empty listPrice))}">
		    <dsp:a href="${contextPath}${templateUrl}">
               	<c:choose>
                    <c:when test="${not empty comparatorImage}">
                      <img src="${comparatorImage}" width="140" height="140" alt="${name}"  title="${name}" /> 
                    </c:when>
                    <c:otherwise>
                      <img src="/default_images/e_no_img.jpg" width="140" height="140" alt="${name}"  title="${name}" />
                    </c:otherwise>
                </c:choose>
	      	</dsp:a> 
		  </c:when>
          <c:when test="${not empty skuList}">
            <dsp:a href="${contextPath}${templateUrl}">
              <c:choose>
                <c:when test="${not empty comparatorImage}">
                  <img class="slPrdMarker" src="${comparatorImage}" id="${skuList}" srcList="${imgSrcList}" prodList="${productList}" alt="${namesList}" skuCodeArticle="${skuCodeArticleList}" productId="${product.repositoryId}" width="140" height="140" alt="${name}" title="${name}" /> 
                </c:when>
                <c:otherwise>
                  <img class="slPrdMarker" src="/default_images/e_no_img.jpg" id="${skuList}" srcList="${imgSrcList}" prodList="${productList}" alt="${namesList}" skuCodeArticle="${skuCodeArticleList}" productId="${product.repositoryId}" width="140" height="140" alt="${name}"  title="${name}" />
                </c:otherwise>
              </c:choose>
            </dsp:a>
          </c:when>
          <c:otherwise>
            <dsp:a href="${contextPath}${templateUrl}">
              <c:choose>
                <c:when test="${not empty comparatorImage}">
                  <img class="slPrdMarker" src="${comparatorImage}" id="${skuId}" skuCodeArticle="${codeArticle}" productId="${productId}" width="140" height="140" alt="${name}" title="${name}" /> 
                </c:when>
                <c:otherwise>
                  <img class="slPrdMarker" src="/default_images/e_no_img.jpg" id="${skuId}" skuCodeArticle="${codeArticle}" productId="${productId}" width="140" height="140" alt="${name}"  title="${name}" />
                </c:otherwise>
              </c:choose>
            </dsp:a>
          </c:otherwise>
        </c:choose>
      </div>
    </div>
  </div>
</dsp:page>