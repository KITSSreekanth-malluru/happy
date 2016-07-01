<dsp:page>     
  <dsp:importbean bean="/atg/userprofiling/Profile" />
  <dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach" />
  <dsp:importbean bean="/com/castorama/commerce/clientspace/CastNewsletterFormHandler" />
  <dsp:importbean bean="/atg/dynamo/droplet/Switch" />
  <dsp:importbean bean="/com/castorama/profile/CastProfileFormHandler" />
  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />

  <dsp:droplet name="Switch">
    <dsp:param bean="CastNewsletterFormHandler.formError" name="value" />
    <dsp:oparam name="true">
          <dsp:droplet name="ProfileErrorMessageForEach">
            <dsp:param bean="CastNewsletterFormHandler.formExceptions" name="exceptions" />
            <dsp:oparam name="output">
              <LI><dsp:valueof param="message" />
            </dsp:oparam>
          </dsp:droplet>
    </dsp:oparam>
  </dsp:droplet>
</dsp:page>