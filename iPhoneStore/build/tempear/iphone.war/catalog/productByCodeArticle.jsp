<dsp:page>
  <dsp:getvalueof var="codeArticleVar" param="codeArticle"/>
  <dsp:getvalueof var="barcodeVar" param="barcode"/>
  <m:jsonObject>
    <dsp:droplet name="/com/castorama/mobile/droplet/ProductsByCodeArticle">
      <dsp:param name="codeArticle" param="codeArticle"/>
      <dsp:param name="barcode" param="barcode"/>
      <dsp:oparam name="output">
       
        <json:property name="result" value="${0}"/>
        <json:property name="errorMessage" value=""/>
        <json:array name="products">
          
          <dsp:droplet name="/atg/dynamo/droplet/ForEach">
            <dsp:param name="array" param="productsSet"/>
            <dsp:param name="elementName" value="products"/>
            <dsp:oparam name="output"> 
              <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                <dsp:param name="array" param="products"/>
                <dsp:param name="elementName" value="product"/>
                <dsp:oparam name="output"> 
                  <dsp:include page="/catalog/product.jsp" flush="true">
                    <dsp:param name="repositoryId" param="product.id"/>
                    <dsp:param name="selectedSkuId" param="key"/>
                  </dsp:include>
                </dsp:oparam>
              </dsp:droplet>
            </dsp:oparam>
          </dsp:droplet>
        </json:array>
      </dsp:oparam>
      <dsp:oparam name="empty">
        <json:property name="result" value="${1}"/>
        <dsp:getvalueof var="isGroupedOrPackVar" param="isGroupedOrPack"/>
        <json:property name="errorMessage">
          <c:choose>
            <c:when test="${not empty isGroupedOrPackVar && isGroupedOrPackVar}">
              <fmt:message key="er_102"/>
            </c:when>
            <c:otherwise>
              <fmt:message key="er_101"/>
            </c:otherwise>
          </c:choose>
        </json:property>
        <json:array name="products">
        </json:array>
      </dsp:oparam>
      <dsp:oparam name="error">
        <json:property name="result" value="${1}"/>
        <json:property name="errorMessage">
          <c:choose>
            <c:when test="${not empty codeArticleVar}">
              <fmt:message key="er_105"/>
            </c:when>
            <c:when test="${not empty barcodeVar}">
              <fmt:message key="er_103"/>
            </c:when>
            <c:otherwise>
              <fmt:message key="er_101"/>
            </c:otherwise>
          </c:choose>
        </json:property>
        <json:array name="products">
        </json:array>
      </dsp:oparam>
      <dsp:oparam name="barcodeConverterServiceUnavailable">
        <json:property name="result" value="${1}"/>
        <json:property name="errorMessage">
          <fmt:message key="er_117"/>
        </json:property>
        <json:array name="products">
        </json:array>
      </dsp:oparam>
      <dsp:oparam name="errorEndOfLife">
        <json:property name="result" value="${1}"/>
        <json:property name="errorMessage">
          <fmt:message key="er_118"/>
        </json:property>
      </dsp:oparam>
    </dsp:droplet>
  </m:jsonObject>
</dsp:page>