
<dsp:page>
  <dsp:getvalueof var="base_price" param="basePrice" />
  <dsp:getvalueof var="adj_price" param="adjPrice" />
  
  <c:choose>
    <c:when test="${null eq adj_price or adj_price eq base_price}" >
      <dsp:valueof converter="euro" locale="fr_FR" param="basePrice" />
    </c:when>
    <c:otherwise>
      <dsp:valueof converter="euro" locale="fr_FR" param="adjPrice" /> <br />
      <s><dsp:valueof converter="euro" locale="fr_FR" param="basePrice" /></s>
    </c:otherwise>
  </c:choose>

</dsp:page>