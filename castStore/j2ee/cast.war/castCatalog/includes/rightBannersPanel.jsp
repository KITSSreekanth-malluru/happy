<dsp:page>

  <dsp:importbean bean="/com/castorama/droplet/GUIMenuLookup"/>

  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
  <dsp:getvalueof var="disableCastoche" param="disablePC" />
  <dsp:getvalueof var="prodDisFbLinkTitle" param="fastLabConfigs.fbLinkTitle" />
  <dsp:getvalueof var="fbLinkValue" param="fastLabConfigs.fbLinkValue" />
  <dsp:getvalueof var="prodDisFdLinkTitle" param="fastLabConfigs.fdLinkTitle" />
  <dsp:getvalueof var="fdLinkValue" param="fastLabConfigs.fdLinkValue" />
  <dsp:getvalueof var="prodDisEgLinkTitle" param="fastLabConfigs.egLinkTitle" />
  <dsp:getvalueof var="egLinkValue" param="fastLabConfigs.egLinkValue" />

  <dsp:getvalueof var="requestURI" bean="/OriginatingRequest.requestURI"/>
  <c:choose>
    <c:when test="${fn:contains(requestURI, 'roductDetails.jsp') }">
      <dsp:getvalueof var="omnitureLocation" value="FicheProduit"/>
    </c:when>
    <c:when test="${fn:contains(requestURI, 'basicPivotCategoryTemplate.jsp') }">
      <dsp:getvalueof var="omnitureLocation" value="PageListe"/>
    </c:when>
    <c:when test="${fn:contains(requestURI, 'basicCategoryTemplate.jsp') }">
      <dsp:getvalueof var="omnitureLocation" value="PageCategorie"/>
    </c:when>
    <c:when test="${fn:contains(requestURI, 'searchResults.jsp') }">
      <dsp:getvalueof var="omnitureLocation" value="MoteurRecherche"/>
    </c:when>
    <c:otherwise>
      <dsp:getvalueof var="omnitureLocation" value="Others"/>
    </c:otherwise>
  </c:choose>

  <div class="groupMenuPointV2 forumsV2">
    <div class="groupMenuPointHeaderV2">
      <div class="nameMenuPointHeaderV2"></div>
    </div>
    <div class="menuPointV2 noBg">
      <img src="/images/temp/4.png"></img>
      <h2><fmt:message key="fl.partagezEchangez"/></h2>
      <span><fmt:message key="fl.desConseils"/></span>
      <a class="linkWithArrow" href="${fbLinkValue}" onClick="s.tl(this,'e','ForumBrico_${omnitureLocation}');" target="_blank">${prodDisFbLinkTitle}</a>
      <a class="linkWithArrow" href="${fdLinkValue}" onClick="s.tl(this,'e','ForumDeco_${omnitureLocation}');" target="_blank">${prodDisFdLinkTitle}</a>
    </div>
    <div class="menuPointV2">
      <img src="/images/temp/5.jpg"></img>
      <h2><fmt:message key="fl.entradeGraduite"/></h2>
      <span><fmt:message key="fl.partagezDes"/></span>
      <a class="linkWithArrow" href="${egLinkValue}" onClick="s.tl(this,'e','TrocHeures_${omnitureLocation}');" target="_blank">${prodDisEgLinkTitle}</a>
    </div>
  </div>
</dsp:page>