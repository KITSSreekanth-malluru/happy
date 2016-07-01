<dsp:page>

  <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
  <dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach"/>
  <dsp:droplet name="/atg/dynamo/droplet/Switch">
    <dsp:param name="value" param="handler.formError"/>
    <dsp:oparam name="true">
      <dsp:droplet name="Switch">
        <dsp:param name="value" param="handler.formExceptions[0].errorCode"/>
        <dsp:oparam name="msgRemoveIllegalItems">

          <div class="grayCorner grayCornerGray preMessageLayer">
            <div class="grayBlockBackground"><!--~--></div>
            <div class="cornerBorder cornerTopLeft"><!--~--></div>
            <div class="cornerBorder cornerTopRight"><!--~--></div>
            <div class="cornerBorder cornerBottomLeft"><!--~--></div>
            <div class="cornerBorder cornerBottomRight"><!--~--></div>
            <div class="preMessage">
              <table cellpadding="0" cellspacing="0" class="emilateValignCenter">
                <tr>
                  <td class="center darkRed">
                    <fmt:message key="msg.cart.remove.items.alert"/>
                  </td>
                </tr>
              </table>
            </div>
          </div>

        </dsp:oparam>
        <dsp:oparam name="default">

          <div class="errorList">

            <ul>
              <dsp:droplet name="ProfileErrorMessageForEach">
                <dsp:param param="handler.formExceptions" name="exceptions"/>
                <dsp:oparam name="output">
                  <li>
                    <dsp:valueof param="message" valueishtml="true"/>
                  </li>
                </dsp:oparam>
              </dsp:droplet>
            </ul>

          </div>
        </dsp:oparam>
      </dsp:droplet>
    </dsp:oparam>
  </dsp:droplet>
</dsp:page>
