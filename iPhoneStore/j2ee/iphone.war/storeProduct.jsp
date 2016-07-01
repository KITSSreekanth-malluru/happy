<%@page contentType="application/json"%>
<dsp:page>
  <dsp:getvalueof var="codeArticle" vartype="java.lang.String" param="codeArticle" />
  <dsp:getvalueof var="productId" vartype="java.lang.String" param="productId" />
  <dsp:getvalueof var="barcode" vartype="java.lang.String" param="barcode" />
  <cast:pageContainer>
    <jsp:attribute name="bodyContent">
      <c:choose>
        <c:when test="${not empty barcode || not empty codeArticle}">
          <dsp:include page="/catalog/productByCodeArticle.jsp" flush="true">
            <dsp:param name="barcode" param="barcode"/>
            <dsp:param name="codeArticle" param="codeArticle"/>
          </dsp:include>
        </c:when>
        <c:when test="${not empty productId}">
          <m:jsonObject>
            <json:property name="result" value="${0}"/>
            <json:property name="errorMessage" value=""/>
            <json:array name="products">
              <dsp:include page="/catalog/product.jsp" flush="true">
                <dsp:param name="repositoryId" param="productId"/>
                <dsp:param name="selectedSkuId" param="selectedSkuId"/>
              </dsp:include>
            </json:array>
          </m:jsonObject>
        </c:when>
      </c:choose>
    </jsp:attribute>
  </cast:pageContainer>
</dsp:page>