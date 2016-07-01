<dsp:page>
  <dsp:getvalueof var="httplinkVar" param="httplink"/>
  <dsp:getvalueof var="httpserverVar" param="httpserver"/>
 
		<br>
		<img id="logo" class="topLogo" alt="Castorama.fr" src="${httplinkVar}/images/email_bottom_banner.png"/>
		<br>
    <p style="font-size: 7.5pt;color:rgb(130,140,152);text-align: left;margin: 5px;">
    <fmt:message key="email.footerMsg"/>
    
    </p>
	<a href="${httplinkVar}">www.castorama.fr</a>
</dsp:page>
