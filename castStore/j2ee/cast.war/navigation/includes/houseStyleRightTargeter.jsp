<dsp:page>

	<dsp:importbean bean="/atg/targeting/TargetingRandom"/>
	<dsp:importbean bean="/atg/registry/Slots/HouseStyleRightSlot"/>
	
	<dsp:getvalueof var="houseStyleRightSlot" param="houseStyleRightSlot"/>
	<div class="rightCol">
	<dsp:droplet name="TargetingRandom">
		<dsp:param name="targeter" value="${houseStyleRightSlot}"/>
		<dsp:param name="fireViewItemEvent" value="false"/>
		<dsp:param name="howMany" value="3"/>
		<dsp:oparam name="output">	
			<dsp:getvalueof var="url" param="element.url"/>
			<dsp:getvalueof var="image" param="element.image.url"/>				
			<div>	
				<a href="${url }"><img src="${image }" width="80" height="80" alt=""/></a>
				<div class="subCol">	
					<h2><a href="${url }"><dsp:valueof param="element.title" valueishtml="true"/></a></h2>
					<dsp:valueof param="element.description" valueishtml="true"/>												
				</div>	
			</div>
		</dsp:oparam>
	</dsp:droplet>
	</div>	
</dsp:page>			