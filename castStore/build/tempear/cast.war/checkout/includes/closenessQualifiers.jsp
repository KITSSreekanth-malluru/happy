<dsp:page>
  <dsp:importbean bean="/com/castorama/droplet/CastClosenessQualifierDroplet" />
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
  <dsp:droplet name="CastClosenessQualifierDroplet">
    <dsp:param name="commerceItem" param="commerceItem" />
    <dsp:param name="elementName" value="closenessQualifiers" />
    <dsp:oparam name="output">
      <dsp:droplet name="ForEach">
        <dsp:param name="array" param="closenessQualifiers" />
        <dsp:param name="elementName" value="qualifier" />
        <dsp:oparam name="output">
          <tr>
            <td colspan="7" class="offers">
            <div class="grayCorner grayCornerWhite preMessageLayer">
            <div class="grayBlockBackground"><!--~--></div>
            <div class="cornerBorderGrayBg cornerTopLeft"><!--~--></div>
            <div class="cornerBorderGrayBg cornerTopRight"><!--~--></div>
            <div class="cornerBorderGrayBg cornerBottomLeft"><!--~--></div>
            <div class="cornerBorderGrayBg cornerBottomRight"><!--~--></div>
            <div class="icoPresent preMessage">
            <table cellpadding="0" cellspacing="0" class="emilateValignCenter">
              <tr>
                <td>
                  <dsp:valueof param="qualifier.upsellMedia.data" valueishtml="true" />
                </td>
              </tr>
            </table>
            </div>
            </div>
            </td>
          </tr>
        </dsp:oparam>
      </dsp:droplet>
    </dsp:oparam>
  </dsp:droplet>
</dsp:page>