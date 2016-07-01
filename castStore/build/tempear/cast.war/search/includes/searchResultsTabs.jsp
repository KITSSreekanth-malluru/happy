<dsp:page>
<dsp:getvalueof var="activeTab" param="activeTab"/>

<div class="titleSubText"></div>
  <div class="tabsArea searchResultWr">
    <ul class="blueTabs">
     <dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
        <dsp:param name="value" param="searchResponse.results"/>
        <dsp:oparam name="false">
          <c:choose>
            <c:when test="${not empty activeTab && activeTab == 'produitsTab'}">
              <li class="active" id="produitsTabHeader">
            </c:when>
            <c:otherwise>
              <li class="" id="produitsTabHeader">
            </c:otherwise>
          </c:choose>
          
            <fmt:message var="produitsTab" key="search_searchResultsContainer.produitsTab"/>
            <a title="${produitsTab }" href="#" onclick="switchSearchTab('produitsTab');">
             ${produitsTab}
             <span>
              (<dsp:valueof param="searchResponse.groupCount"/>)
             </span>
            </a>
          </li>
        </dsp:oparam>
      </dsp:droplet>
      <dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
        <dsp:param name="value" param="docSearchResponse.results"/>
        <dsp:oparam name="false">
          <c:choose>
            <c:when test="${not empty activeTab && activeTab == 'ideasTab'}">
              <li class="active" id="ideasTabHeader">
            </c:when>
            <c:otherwise>
              <li class="" id="ideasTabHeader">
            </c:otherwise>
          </c:choose>
            <fmt:message var="ideasTab" key="search_searchResultsContainer.ideasTab"/>
              <a title="${ideasTab }" href="#" onclick="switchSearchTab('ideasTab');"> 
                ${ideasTab} 
                <span>
                  (<dsp:valueof param="docSearchResponse.groupCount"/>)
                </span>
              </a>
            </li>
        </dsp:oparam>
      </dsp:droplet>
      <dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
        <dsp:param name="value" param="magasinSearchResponse.results"/>
        <dsp:oparam name="false"> 
          <c:choose>
            <c:when test="${not empty activeTab && activeTab == 'magasinTab'}">
              <li class="active" id="magasinTabHeader">
            </c:when>
            <c:otherwise>
              <li class="" id="magasinTabHeader">
            </c:otherwise>
          </c:choose>
            <fmt:message var="magasinTab" key="search_searchResultsContainer.magasinTab"/>
              <a title="${magasinTab }" href="#" onclick="switchSearchTab('magasinTab');"> 
                ${magasinTab }
                <span>
                  (<dsp:valueof param="magasinSearchResponse.groupCount"/>)
                </span> 
              </a>
            </li>
        </dsp:oparam>
      </dsp:droplet>
      
    </ul>
    <div class="blueTabsBorder"><!-- --></div>
  </div>
  <div class="clear"></div>
  <c:if test="${empty param.trail }">
  <dsp:droplet name="/com/castorama/droplet/SearchMessageDroplet">
 		<dsp:param name="query" param="questionVar" /> 		
 	</dsp:droplet>
 	</c:if>
</dsp:page>