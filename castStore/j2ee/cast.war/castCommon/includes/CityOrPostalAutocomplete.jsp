<dsp:page>
  <dsp:importbean bean="/com/castorama/droplet/CastCityOrPostalCodeAjaxSuggest" />
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

  <dsp:droplet name="CastCityOrPostalCodeAjaxSuggest" >
    <dsp:oparam name="output">
      <dsp:droplet name="ForEach">
        <dsp:param name="array" param="hints"/>
        <dsp:oparam name="output">
          <dsp:valueof param="element" />&nbsp;
        </dsp:oparam>
      </dsp:droplet>
    </dsp:oparam>
  </dsp:droplet>
</dsp:page>
