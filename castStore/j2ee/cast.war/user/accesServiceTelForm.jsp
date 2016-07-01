<dsp:page>
  <dsp:importbean bean="/com/castorama/sponkey/TelFormServiceClient"/>
  
  <dsp:getvalueof param="Tel" var="phoneFix"/>
  <dsp:getvalueof param="session_telform" var="userSession"/>

  <dsp:droplet name="TelFormServiceClient">
    <dsp:param name="phoneFix" value="${phoneFix}"/>
    <dsp:param name="userSession" value="${userSession}"/>
    <dsp:oparam name="output">
      <dsp:valueof param="accauntInformaton" valueishtml="true"/>
    </dsp:oparam>
  </dsp:droplet>
</dsp:page>