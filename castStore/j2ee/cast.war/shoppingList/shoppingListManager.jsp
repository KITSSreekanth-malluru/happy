<dsp:page>

  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
  <dsp:getvalueof var="fromComparisonPage" param="fromComp"/>
  
  <dsp:droplet name="/com/castorama/droplet/CastShoppingListDroplet">
    <dsp:param name="action" param="action"/>
    <dsp:param name="skuIds" param="skuIds"/>
    <dsp:oparam name="error">
    	<dsp:getvalueof var="notProcessed" param="notProcessed"/>
    	${notProcessed}
    </dsp:oparam>
  </dsp:droplet>
  <c:if test="${not empty fromComparisonPage}">
  	<c:redirect url="/shoppingList/shoppingListContainer.jsp"/>
  </c:if>
</dsp:page>