<dsp:page>

	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
	<dsp:setvalue param="childCategory" paramvalue="childCategory"/>
	<dsp:getvalueof var="image" param="image"/>
	<dsp:getvalueof var="template" param="childCategory.template.url"/>
	
	<dsp:droplet name="/com/castorama/droplet/CastCategoryLinkDroplet">
		<dsp:param name="categoryId" param="childCategory.repositoryId"/>
		<dsp:param name="navCount" bean="/Constants.null"/>
		<dsp:param name="navAction" value="jump"/>
		<dsp:oparam name="output">
			<dsp:getvalueof var="template" param="url"/>			
		</dsp:oparam>
	</dsp:droplet>
																
	<dsp:a href="${contextPath}${template}" iclass="ecoImageLink">
		<dsp:img src="${image}" width="56" height="70" alt=""/>		
	</dsp:a>	

	<div class="bluebox">
		<h2>	
			<dsp:a href="${contextPath}${template}">	
				<dsp:valueof param="childCategory.displayName"/>
				<dsp:param name="categoryId" param="childCategory.repositoryId"/>
			</dsp:a>
		</h2>		
		<dsp:valueof param="childCategory.description"/>
	</div>		
</dsp:page>