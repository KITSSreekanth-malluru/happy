<dsp:page>
  <dsp:droplet name="/atg/targeting/TargetingRandom">
    <dsp:param name="targeter" bean="/atg/registry/Slots/MentionsCNILAtoutSlot"/>
    <dsp:param name="fireViewItemEvent" value="false"/>
    <dsp:oparam name="output">
      <dsp:valueof param="element.data" valueishtml="true"/>
    </dsp:oparam>
  </dsp:droplet>
</dsp:page>