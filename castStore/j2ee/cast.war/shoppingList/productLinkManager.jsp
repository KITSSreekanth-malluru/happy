<dsp:page>
	<dsp:getvalueof var="productId" param="productId"/>
	<dsp:getvalueof var="skuId" param="skuId"/>
	<dsp:droplet name="/com/castorama/droplet/CastProductLinkDroplet">
		<dsp:param name="productId" value="${productId}"/>
		<dsp:param name="navAction" value="jump"/>
		<dsp:oparam name="output">
			<dsp:getvalueof var="templateUrl" param="url"/>
			<dsp:valueof value="${templateUrl}&skuId=${skuId}"/>
		</dsp:oparam>
	</dsp:droplet>
</dsp:page>