<dsp:page>
  <dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
  <dsp:droplet name="IsEmpty">
    <dsp:param name="value" param="promoInformation"/>
    <dsp:oparam name="false">
      <dsp:include page="../layoutTypeInformation.jsp">
        <dsp:param name="promoInformation" param="promoInformation"/>
      </dsp:include>
    </dsp:oparam>
  </dsp:droplet>
</dsp:page>
