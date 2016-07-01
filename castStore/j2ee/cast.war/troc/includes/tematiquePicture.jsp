<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
<dsp:getvalueof param="thematique" var="thematique"/>

<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
  <dsp:param name="value" param="thematique"/>
  <dsp:oparam name="false">
    <dsp:getvalueof param="thematique.title" var="thematiqueTitle"/>
    <dsp:getvalueof param="thematique.smallImage.url" var="thematiqueSmallImage"/>
    <dsp:getvalueof param="thematique.bgColor" var="thematiqueBgColor"/>
    <dsp:getvalueof param="thematique.fontColor" var="thematiqueFontColor"/>
    <dsp:getvalueof var="topicId" param="thematique.repositoryId"/>
    <dsp:droplet name="/com/castorama/droplet/CastTopicLinkDroplet">
      <dsp:param name="topicId" value="${topicId}"/>
      <dsp:oparam name="output">
        <dsp:getvalueof var="url" param="url"/>
        <a href="${contextPath}${url}">
      </dsp:oparam>
    </dsp:droplet>
    <span class="themesPicture">
      <span class="themesPictureImage">
        <img src="${thematiqueSmallImage}"/>
      </span>
      <c:if test="${empty thematiqueFontColor || thematiqueFontColor == ''}">
        <dsp:getvalueof value="white" var="thematiqueFontColor"/>
      </c:if>      
      <span class="themesPictureTitle" style="background-color: ${thematiqueBgColor}; color: ${thematiqueFontColor};">
        <span>${thematiqueTitle}</span>
      </span>
      <span class="themesPictureVideoLabel"><fmt:message key="troc.imageMenu.video" /></span>
    </span>
    </a>
  </dsp:oparam>
</dsp:droplet>
