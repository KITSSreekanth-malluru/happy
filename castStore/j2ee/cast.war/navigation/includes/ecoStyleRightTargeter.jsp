<dsp:page>
	
	<dsp:importbean bean="/atg/targeting/TargetingRandom"/>
	<dsp:importbean bean="/atg/registry/Slots/EcoStyleRightSlot"/>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
	<dsp:getvalueof var="ecoStyleRightSlot" param="ecoStyleRightSlot"/>
	<div class="rightCol">

		<dsp:droplet name="TargetingRandom">
			<dsp:param name="targeter" value="${ecoStyleRightSlot}"/>
			<dsp:param name="fireViewItemEvent" value="false"/>
			<dsp:param name="howMany" value="2"/>
			<dsp:oparam name="output">
				<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
					<dsp:param name="value" param="element.document"/>
					<dsp:oparam name="false">
						<!-- add correct URL for document -->
						<dsp:getvalueof var="url" param="#"/>
						<dsp:getvalueof var="image" param="element.document.image.url"/>
						<dsp:getvalueof var="title" param="element.document.title" />
						<dsp:getvalueof var="description" param="element.document.description" />
					</dsp:oparam>
					<dsp:oparam name="true">
						<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
							<dsp:param name="value" param="element.product"/>
							<dsp:oparam name="false">
								<dsp:droplet name="/com/castorama/droplet/CastProductLinkDroplet">
									<dsp:param name="productId" param="element.product.repositoryId"/>
									<dsp:param name="navAction" value="jump"/>
									<dsp:oparam name="output">
										<dsp:getvalueof var="template" param="url"/>
									</dsp:oparam>
								</dsp:droplet>
								<dsp:getvalueof var="url" value="${contextPath}${template }"/>
								<dsp:getvalueof var="image" param="element.product.thumbnailImage.url"/>
								<dsp:getvalueof var="title" param="element.product.displayName" />
								<dsp:getvalueof var="description" param="element.product.description" />
							</dsp:oparam>
							<dsp:oparam name="true">
								<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
									<dsp:param name="value" param="element.category"/>
									<dsp:oparam name="false">
										<dsp:droplet name="/com/castorama/droplet/CastCategoryLinkDroplet">
											<dsp:param name="categoryId" param="element.category.repositoryId"/>
											<dsp:param name="navCount" bean="/atg/commerce/catalog/CatalogNavHistory.navCount"/>
											<dsp:param name="navAction" value="jump"/>
											<dsp:oparam name="output">
												<dsp:getvalueof var="template" param="url"/>
											</dsp:oparam>
										</dsp:droplet>
										<dsp:getvalueof var="url" value="${contextPath}${template }"/>
										<dsp:getvalueof var="image" param="element.category.thumbnailImage.url"/>
										<dsp:getvalueof var="title" param="element.category.displayName" />
										<dsp:getvalueof var="description" param="element.category.description" />
									</dsp:oparam>
									<dsp:oparam name="true">
										<dsp:getvalueof var="url" param="element.url"/>
										<dsp:getvalueof var="image" param="element.image.url"/>	
										<dsp:getvalueof var="title" param="element.title" />
										<dsp:getvalueof var="description" param="element.description" />
									</dsp:oparam>
								</dsp:droplet>
							</dsp:oparam>
						</dsp:droplet>
					</dsp:oparam>
				</dsp:droplet>
				<div>	
					<a href="${url }"><img src="${image }" width="80" height="80" alt=""/></a>
					<div class="subCol">
						<h2><a href="${url }"><dsp:valueof value="${title }" valueishtml="true"/></a></h2>
						<dsp:valueof value="${description }" valueishtml="true"/>
					</div>
				</div>
			</dsp:oparam>
		</dsp:droplet>

	</div>
</dsp:page>


