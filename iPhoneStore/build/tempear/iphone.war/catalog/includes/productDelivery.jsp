<dsp:page>

  <dsp:importbean bean="/com/castorama/droplet/CastShippingDroplet"/>
  <dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/> 

  <dsp:getvalueof var="skuId" param="skuId"/>

  <dsp:droplet name="CastShippingDroplet">
    <dsp:param name="skuId" value="${skuId}"/>
    <dsp:param name="quantity" value="1"/>
      <dsp:oparam name="output">
        <dsp:droplet name="IsNull">
          <dsp:param name="value" param="expeditionPNS"/>
          <dsp:oparam name="false">
            <dsp:valueof param="expeditionPNS.deliveryTime" valueishtml="true"/>
          </dsp:oparam>
        </dsp:droplet>
      </dsp:oparam>
   </dsp:droplet>
</dsp:page>