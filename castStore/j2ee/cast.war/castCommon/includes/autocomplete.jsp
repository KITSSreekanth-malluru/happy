<dsp:page>
  <dsp:importbean bean="/com/castorama/droplet/CastAjaxSuggest" />
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

  <dsp:droplet name="CastAjaxSuggest" >
    <dsp:oparam name="output">
      <dsp:droplet name="ForEach">
        <dsp:param name="array" param="hints"/>
        <dsp:oparam name="output">
          <dsp:valueof param="element" valueishtml="true"/>&nbsp;
        </dsp:oparam>
      </dsp:droplet>
    </dsp:oparam>
  </dsp:droplet>
</dsp:page>
