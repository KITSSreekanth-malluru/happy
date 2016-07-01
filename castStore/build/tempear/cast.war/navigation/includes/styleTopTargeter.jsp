<dsp:page>
	
	<dsp:importbean bean="/atg/targeting/TargetingRandom"/>
	<dsp:importbean bean="/atg/registry/Slots/EcoStyleTopSlot"/>
	
	<dsp:getvalueof var="styleTopSlot" param="styleTopSlot"/>

	<dsp:droplet name="TargetingRandom">
		<dsp:param name="targeter" value="${styleTopSlot}"/>
		<dsp:param name="fireViewItemEvent" value="false"/>
		<dsp:oparam name="output">
			<h1><dsp:valueof param="element.title" valueishtml="true"/></h1>
			<dsp:valueof param="element.description" valueishtml="true"/>
		</dsp:oparam>
	</dsp:droplet>		
</dsp:page>