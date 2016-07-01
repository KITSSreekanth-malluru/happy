<dsp:page>
  <dsp:importbean bean="/com/castorama/stockvisualization/MagasinSearchFormHandler" />
  <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
  <dsp:getvalueof var="fromSVContext" param="fromSVContext"/>

  <div class="noteArea"></div>

  <p><fmt:message key="stockVisualization.nearMyHome" /></p>
  <div class="ddWrapper">
    <label><fmt:message key="stockVisualization.typeInCityOrPostalCode" /></label>

          <script>
          <!--
              $(document).ready(function(){  
                  $("#example").autocomplete("${pageContext.request.contextPath}/castCommon/includes/CityOrPostalAutocomplete.jsp", {
                      minChars: 2,
                      formatResult: formatResult,
                      formatItem: formatItem
                      });
                  
                  function formatResult(row) {
                          return row = row[0].replace("&nbsp;", " ").replace("&amp;","&").replace("&#39;", "'");
                  }
                  
                  function formatItem(row) {
                      return row = row[0].replace("&nbsp;", " ");
                  }
              });
            function formEnterPress(e) {
        	  if (e.keyCode==13) {
            	$("#magasinSearchSubmit").click();
        	    return false;
        	  }
        	  return true;
        	}
          //-->
          </script>
    
        <dsp:form formid="magasinSearch" action="" method="post" >
          <dsp:input id="example" iclass="i-selectW220" type="text" bean="MagasinSearchFormHandler.searchQuestion"
			onkeypress="return formEnterPress(event);" /> 
          <dsp:input id="magasinSearchSubmit" type="submit" iclass="buttonOK" bean="MagasinSearchFormHandler.searchMagasin" />

          <c:if test="${fromSVContext}">
            <dsp:input type="hidden" bean="MagasinSearchFormHandler.addFromSVContextParam" value="true" />
          </c:if>
           
          <dsp:input type="hidden" bean="MagasinSearchFormHandler.handleSearchSuccessURL"
                  value="${pageContext.request.contextPath}/magasin/magasin-fiche.jsp" /> 
          <dsp:input type="hidden" bean="MagasinSearchFormHandler.handleSearchErrorURL" 
                  value="${pageContext.request.contextPath}/magasin/magasin-carte-france.jsp${not empty pageContext.request.queryString? \"?\" : \"\"}${not empty pageContext.request.queryString? pageContext.request.queryString : \"\"}" /> 
    
        </dsp:form>
    
      <dsp:droplet name="Switch">
        <dsp:param bean="MagasinSearchFormHandler.formError" name="value"/>
        <dsp:oparam name="true">
          <div class="error">
            <fmt:message key="stockVisualization.magasinNotFountErrMsg" />
          </div>
        </dsp:oparam>
      </dsp:droplet>

  </div>

</dsp:page>