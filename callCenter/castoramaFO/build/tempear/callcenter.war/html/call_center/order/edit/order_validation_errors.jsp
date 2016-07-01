
<dsp:page xml="true">

        <dsp:droplet name="/atg/dynamo/droplet/Switch">
           <dsp:param name="value" bean="/castorama/CastoOrderEditor.orderValidationError"/>
           <dsp:oparam name="true">
            <span class=errorbig>La commande est incorrecte, et m&eacute;rite d'etre v&eacute;rifi&eacute;e !</span>
            <p><span class=error>
            <UL>
             <dsp:droplet name="/atg/dynamo/droplet/ForEach">
              <dsp:param name="array" bean="/castorama/CastoOrderEditor.orderValidationErrorsAsStringArray"/>
              <dsp:oparam name="output">
	        <LI> <dsp:valueof param="element">erreur</dsp:valueof>
              </dsp:oparam>
             </dsp:droplet>
            </UL></span>
           </dsp:oparam>
           <dsp:oparam name="false">Cette commande est correcte.</dsp:oparam>
        </dsp:droplet>
        <p>
        <dsp:setvalue bean="/castorama/CastoOrderEditor.resetOrderValidationErrors" value=""/>
        
</dsp:page>