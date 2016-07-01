<dsp:page>
  <dsp:importbean bean="/atg/dynamo/droplet/Redirect" />
  <dsp:importbean bean="/com/castorama/mobile/droplet/VideoURLConverter" />
  <dsp:getvalueof var="app" param="app"/>
  <c:choose>
  <c:when test="${empty app}">
    <dsp:droplet name="VideoURLConverter">
      <dsp:param name="shortURL" param="shortURL"/>
      <dsp:oparam name="output">
        <dsp:droplet name="Redirect">
         <dsp:param name="url" param="videoItem.longURL" />
       </dsp:droplet>
      </dsp:oparam>
    </dsp:droplet>   
     
  </c:when>
  <c:otherwise>
    <json:object>
      <dsp:droplet name="VideoURLConverter">
        <dsp:param name="shortURL" param="shortURL"/>
        <dsp:oparam name="output">
          <json:property name="longURL">
            <dsp:valueof param="videoItem.longURL"/>
          </json:property>         
          <json:property name="errorCode" value="${0}"/>
        </dsp:oparam>
        <dsp:oparam name="error">
          <json:property name="errorMessage">
            <fmt:message key="er_501"/>
          </json:property>         
          <json:property name="errorCode" value="${1}"/>
        </dsp:oparam>
        <dsp:oparam name="empty">
          <json:property name="errorMessage">
            <fmt:message key="er_501"/>
          </json:property>         
          <json:property name="errorCode" value="${1}"/>
        </dsp:oparam>
      </dsp:droplet>
    </json:object>
  </c:otherwise>
  </c:choose>
</dsp:page>