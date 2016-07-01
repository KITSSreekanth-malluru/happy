<dsp:page>

	<dsp:importbean bean="/atg/dynamo/droplet/Switch" />
	<dsp:importbean bean="/com/castorama/droplet/RecommendedProductDroplet"/>
	<dsp:importbean bean="/atg/registry/RepositoryTargeters/ProductCatalog/CastRecomendedProducts" />

	<dsp:droplet name="RecommendedProductDroplet">
		<dsp:param name="targeter" bean="CastRecomendedProducts"/>
		<dsp:param name="howMany" value="4"/>
		<dsp:oparam name="empty" >
      <dsp:getvalueof var="isEmpty" value="true"/>
    </dsp:oparam>
		<dsp:oparam name="outputStart"> 
			<div class="productsRow hasHighlight">
				<h2>
					<dsp:getvalueof var="title" param="titlekey">
						<fmt:message key="${title}" />
					</dsp:getvalueof>
				</h2>
		</dsp:oparam>
		<dsp:oparam name="outputEnd"> 
			</div>
			<div class="clear"><!--~--></div>		
		</dsp:oparam>
		<dsp:oparam name="output">
			<dsp:droplet name="Switch">
				<dsp:param name="value" param="index"/>
				<dsp:oparam name="3">
					<div class="productItem piLast">
				</dsp:oparam>
				<dsp:oparam name="default">
					<div class="productItem">
				</dsp:oparam>
			</dsp:droplet>
			<dsp:include page="shopping_item.jsp">
				<dsp:param name="product" param="element" />
				<dsp:param name="url" value="preshopping.jsp" />
			</dsp:include>
			</div>	
		</dsp:oparam>
	</dsp:droplet>
  
  <c:if test="${!isEmpty}">
	 <div class="rowSplitter"><!--~--></div>
  </c:if>
   
</dsp:page>
