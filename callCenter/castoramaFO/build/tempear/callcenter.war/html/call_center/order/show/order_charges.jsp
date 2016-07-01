<%@ taglib prefix="c" uri="/core"%>
<%@ taglib prefix="fmt" uri="/fmt"%>
<%@ taglib prefix="dsp" uri="/dspTaglib"%>
<%@ taglib prefix="cordsp" uri="/coreDSP"%>

<dsp:page xml="true">

<dsp:droplet name="/atg/dynamo/droplet/IsNull">
   <dsp:param name="value" param="group.orderRelationship"/>
   <dsp:oparam name="true"></dsp:oparam>
   <dsp:oparam name="false">
       <dsp:droplet name="/atg/dynamo/droplet/Switch">
          <dsp:param name="value" param="group.orderRelationship.relationshipTypeAsString"/>
          <dsp:oparam name="ORDERAMOUNT">
             <dsp:droplet name="/atg/dynamo/droplet/CurrencyFormatter">
                <dsp:param name="currency" param="group.orderRelationship.amount"/>
                <dsp:oparam name="output">
                     <dsp:valueof param="formattedCurrency">0</dsp:valueof>
                </dsp:oparam>
             </dsp:droplet> of Order Charges
          </dsp:oparam>
          <dsp:oparam name="TAXAMOUNT">
             <dsp:droplet name="/atg/dynamo/droplet/CurrencyFormatter">
                <dsp:param name="currency" param="cItemRel.amount"/>
                <dsp:oparam name="output">
                     <dsp:valueof param="formattedCurrency">0</dsp:valueof>
                </dsp:oparam>
             </dsp:droplet> of Tax Charges
          </dsp:oparam>
          <dsp:oparam name="TAXAMOUNTREMAINING">
              Remaining Amount of Tax Charges
          </dsp:oparam>
          <dsp:oparam name="ORDERAMOUNTREMAINING">
              Remaining Amount of Order Charges
          </dsp:oparam>
       </dsp:droplet>                            
  </dsp:oparam>
</dsp:droplet>


</dsp:page>

