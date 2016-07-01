<dsp:page>
  <dsp:importbean bean="/com/castorama/stockvisualization/StockVisualizationDroplet" />
  <dsp:importbean bean="/atg/commerce/catalog/SKULookup"/>

  <dsp:getvalueof var="prodId" param="prodId" />
  <dsp:getvalueof var="magasinId" param="magasinId" />
  <dsp:getvalueof var="queryCondition" param="queryCondition" />
  <dsp:getvalueof var="postalCode" param="postalCode" />
  <dsp:getvalueof var="skuId" param="skuId" />

  <dsp:droplet name="SKULookup">
	<dsp:param name="id" value="${skuId}"/>
	<dsp:oparam name="output">
		<dsp:getvalueof var="codeArticle" param="element.codeArticle"/>
	</dsp:oparam>
  </dsp:droplet>

  <dsp:droplet name="StockVisualizationDroplet">
    <dsp:param name="queryCondition" value="${queryCondition}" />
    <dsp:param name="prodId" value="${codeArticle}" />
    <dsp:param name="magasinId" value="${magasinId}" />
    <dsp:param name="postalCode" value="${postalCode}" />
  </dsp:droplet>

  <dsp:include page="/svTest/stockVisualizationRefreshableContent.jsp">
    <dsp:param name="codeArticle" value="${codeArticle}" />
    <dsp:param name="prodId" param="prodId" />
    <dsp:param name="skuId" param="skuId" />
  </dsp:include>

</dsp:page>