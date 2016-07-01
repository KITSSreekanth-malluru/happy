<dsp:page>
  <dsp:getvalueof var="containerVar" param="container"/>
   
  <dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
    <dsp:param name="value" param="suggestion"/>
    <dsp:oparam name="false">
    
    <dsp:getvalueof var="suggestionVar" param="suggestion.value"/>

     <c:if test="${suggestionVar != '_unk_' && suggestionVar != '_nulx_' && suggestionVar != '_null_'}">
      <div class="titleSubText">
        <fmt:message key="search_searchSuggestionTerm.leftPart"/>&nbsp;<fmt:message key="common.doubleQuote"/>
          <dsp:a href="${containerVar }">
            ${suggestionVar}
            <dsp:param name="question" value="${suggestionVar}"/>
            <dsp:param name="searchType" param="searchType"/>
            <dsp:param name="osearchmode" value="tagcloud"/>
          </dsp:a>
        <fmt:message key="common.doubleQuote"/>
        <fmt:message key="common.dot"/>
      </div>
      </c:if>
    </dsp:oparam>
  </dsp:droplet>
</dsp:page>