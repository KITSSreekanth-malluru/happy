<%@ taglib prefix="c" uri="/core"%>
<%@ taglib prefix="fmt" uri="/fmt"%>
<%@ taglib prefix="dsp" uri="/dspTaglib"%>
<%@ taglib prefix="cordsp" uri="/coreDSP"%>

<dsp:page xml="true">



<dsp:droplet name="/atg/dynamo/droplet/ForEach">
  <dsp:param name="array" param="group.shippingGroupRelationships"/>
  <dsp:param name="elementName" value="cItemRel"/>
  <dsp:oparam name="output">
      <dsp:droplet bean="/atg/dynamo/droplet/Switch">
        <dsp:param name="value" param="cItemRel.relationshipTypeAsString"/>
          <dsp:oparam name="SHIPPINGAMOUNT">
             <dsp:droplet name="/atg/dynamo/droplet/CurrencyFormatter">
                <dsp:param name="currency" param="cItemRel.amount"/>
                <dsp:oparam name="output">
                     <dsp:valueof param="formattedCurrency">0</dsp:valueof>
                </dsp:oparam>
             </dsp:droplet> of Shipping Charges
          </dsp:oparam>
          <dsp:oparam name="SHIPPINGAMOUNTREMAINING">
              Remaining Amount of Shipping Charges
          </dsp:oparam>
      </dsp:droplet>                            
      <br>
  </dsp:oparam>
  <dsp:oparam name="empty"></dsp:oparam>              
</dsp:droplet>


</dsp:page>

