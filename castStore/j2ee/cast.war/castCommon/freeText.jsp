<dsp:page>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean var="originatingRequest" bean="/OriginatingRequest"/>

  <c:if test="${originatingRequest.servletPath == '/home.jsp'}">
  <div id="freeText">
    <dsp:getvalueof var="set" bean="Profile.catalog.rootCategories"/>
    <dsp:param name="list" value="${castCollection:list(set)}"/>
    <dsp:valueof param="list[0].freeText" valueishtml="true"/>
  </div>
  </c:if>
</dsp:page>
