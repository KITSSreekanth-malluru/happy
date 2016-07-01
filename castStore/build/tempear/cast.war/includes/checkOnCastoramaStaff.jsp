<%@ page contentType="text/html; charset=UTF-8"%>
<dsp:page>
   <dsp:droplet name="/com/castorama/droplet/CheckIfCastoramaStaff">
		<dsp:oparam name="output">  
	       <dsp:getvalueof var="xForwardedFor" param="X-Forwarded-For" scope="request"/>
		 </dsp:oparam>
       </dsp:droplet>
</dsp:page>
