<dsp:page>
  <div class="productListe">
    <div class="rowSplitter"></div>
    <table class="productsTable grayTable idees" cellspacing="0" cellpadding="0">
      <colgroup>
        <col width="96"/>
        <col/>
      </colgroup>
      <tbody>
        <dsp:droplet name="/atg/dynamo/droplet/ForEach">
          <dsp:param name="array" param="results" />
          <dsp:oparam name="output">
            <dsp:getvalueof var="count" param="count" />
            <c:choose>
              <c:when test="${count mod 2 == 0}">
                <tr class="odd">
              </c:when>
              <c:otherwise>
                <tr>
              </c:otherwise>
            </c:choose>
            
            <dsp:include page="listDocumentAndMagasinTemplate.jsp">
              <dsp:param name="repItemId" param="element.document.properties.$repositoryId" />
               <dsp:param name="itemDescriptor" param="itemDescriptor"/>
               <dsp:param name="repository" param="repository"/>
               <dsp:param name="isSearchResult" param="isSearchResult"/>
            </dsp:include>
            </tr>
          </dsp:oparam>
        </dsp:droplet>
      </tbody>
    </table>
  </div>
</dsp:page>