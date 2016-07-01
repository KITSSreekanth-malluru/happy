<dsp:page>
  <dsp:droplet name="/com/castorama/mobile/droplet/BarcodeToCodeArticleConverter">
    <dsp:param name="barcode" param="barcode"/>
    <dsp:param name="codeArticle" param="codeArticle"/>
    <dsp:oparam name="output">
      <c:choose>
        <c:when test="${(not empty errorCode && errorCode == 1) || empty codeArticle}">
          <c:choose>
            <c:when test="${not empty param.codeArticle && not empty codeArticle}">
              <m:jsonObject>
                <json:property name="result" value="${1}"/>
                <json:property name="errorMessage">
                  <fmt:message key="er_105"/>
                </json:property>
                <json:array name="products">
                </json:array>
              </m:jsonObject>
            </c:when>
            <c:when test="${not empty param.barcode || empty codeArticle}">
              <m:jsonObject>
                <json:property name="result" value="${1}"/>
                <json:property name="errorMessage">
                  <fmt:message key="er_103"/>
                </json:property>
                <json:array name="products">
                </json:array>
              </m:jsonObject>
            </c:when>
          </c:choose>
         
        </c:when>
        <c:otherwise>
          <dsp:include page="/catalog/productByCodeArticle.jsp" flush="true">
            <dsp:param name="latitude" param="latitude"/>
            <dsp:param name="longitude" param="longitude"/>
            <dsp:param name="storeId" param="storeId"/>
            <dsp:param name="codeArticle" value="${codeArticle}"/>
          </dsp:include>
        </c:otherwise>
      </c:choose>
    </dsp:oparam>
  </dsp:droplet>
</dsp:page>
