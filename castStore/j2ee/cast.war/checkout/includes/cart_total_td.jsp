<dsp:getvalueof var="labelsList" param="labelsList"/>

<td class="boxCartTableSummaryTitle">
  <div class="boxCartInnerWr">
    <h4>${labelsList[0]}</h4>
    
    <c:if test="${showVousAvezEconomise}">
      <p>${labelsList[1]}</p></c:if>
    <p>${labelsList[2]}</p>
    <p>${labelsList[3]}</p>
    <c:if test="${showRemiseLivraison}">
      <p>${labelsList[4]}</p></c:if>
    
    <h5>
      ${labelsList[5]}
      <span>${labelsList[6]}</span>
    </h5>
  </div>
</td>