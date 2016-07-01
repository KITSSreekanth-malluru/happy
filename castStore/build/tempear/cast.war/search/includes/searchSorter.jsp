<dsp:page>

  <dsp:getvalueof var="searchResponse" param="searchResponse"/>
  <dsp:getvalueof var="requestURI" bean="/OriginatingRequest.requestURI"/>
  <dsp:getvalueof var="trail" param="produitsFST.facetTrail"/>

  <dsp:getvalueof var="question" param="question"/>
  <dsp:getvalueof var="sortByValue" param="sortByValue"/>
  <dsp:getvalueof var="categoryId" param="categoryId"/>
  <dsp:getvalueof var="addFacet" param="addFacet"/>
  <dsp:getvalueof var="removeFacet" param="removeFacet"/>
  <dsp:getvalueof var="productListingView" param="productListingView"/>
  <dsp:getvalueof var="pageNum" param="pageNum"/>
  <dsp:getvalueof var="searchType" param="searchType"/>
  <dsp:getvalueof var="currentTab" param="currentTab"/>
  <dsp:getvalueof var="isSearchResult" param="isSearchResult"/>
  <dsp:getvalueof var="featuredSkuId" param="featuredSkuId"/>
 
  <dsp:getvalueof var="categoryURI" bean="/OriginatingRequest.requestURI"/>
  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />

  <dsp:droplet name="/com/castorama/droplet/CastCategoryLinkDroplet">
    <dsp:param name="categoryId" param="categoryId"/>
    <dsp:param name="trail" value="${trail}"/>
    <dsp:param name="searchType" value="${searchType}"/>
    <dsp:param name="question" value="${question}"/>
    <dsp:param name="addFacet" value="${addFacet}"/>
    <dsp:param name="removeFacet" value="${removeFacet}"/>
    <dsp:param name="currentTab" value="${currentTab}"/>
    <dsp:param name="featuredSkuId" value="${featuredSkuId}"/>
    <dsp:param name="pageNum" param="pageNum"/>
    <dsp:param name="productListingView" value="${productListingView}"/>
    <dsp:param name="sortByValue" value="${sortByValue}"/>   
    <dsp:oparam name="output">
      <dsp:getvalueof var="url" param="url"/>    
      <dsp:getvalueof var="categoryURI" value="${contextPath}${url}"/>
    </dsp:oparam>
    <dsp:oparam name="error">
      <dsp:getvalueof var="url" param="url"/>    
    </dsp:oparam>
  </dsp:droplet>
  
  <dsp:getvalueof var="categoryURI" value="${url}"/>
  
  <dsp:getvalueof var="startSep" value="?"/>
    <c:if test="${fn:contains(categoryURI, '?') }">
      <dsp:getvalueof var="startSep" value="&"/>
    </c:if>
    <input id="sortSearchURL" type="hidden" value="${categoryURI}">
    
    <script>

         function sortSearchResults(selectedValue) {
            if (document.getElementById('sortSearchURL').value != null && 
             typeof(document.getElementById('sortSearchURL').value) != 'undefined' &&
             selectedValue != null &&
             typeof(selectedValue) != 'undefined'
             )
             {
               var newlocation = ""; 
               var oldlocation = document.getElementById('sortSearchURL').value;
               var mr =/sortByValue=&/; 
               var m = mr.exec(oldlocation);
               if (m != null){
                  oldlocation = oldlocation.replace(mr, '')
               }
               var mmr = /sortByValue=$/;
               var mm = mmr.exec(oldlocation);
               if (mm != null){
                   oldlocation = oldlocation.replace(mmr, '')
                }
               oldlocation = (oldlocation.replace(/\?$/g,""));
               oldlocation = (oldlocation.replace(/&$/g,""));
               if(oldlocation.match(/sortByValue=/g)) {
                   
                   newlocation = oldlocation.replace(/=${sortByValue}/,"="+selectedValue);
               } else {
                   var sep = "?";
                   if (oldlocation.match(/\?/)){
                       sep = "&";
                   }
                   newlocation = oldlocation + sep + "sortByValue=" + selectedValue;
               }
               var nr = new RegExp("^\\"+"${contextPath}", "g");
               if(!newlocation.match(nr) ) {
                   newlocation = "${contextPath}"+newlocation;
               }
               window.location=newlocation;
             } 
         
         }

    </script>
    <div class="sorter"><span><fmt:message key="search_searchSorter.sort"/></span>

    <div class="ddWrapper">
    <select class="styled"  id="searchSorterSelect" rel="redirectable"  value="${sortByValue }">
      <c:choose>
        <c:when test="${sortByValue == 'relevance'}">
          <option value="relevance" selected><fmt:message key="search_searchSorter.relevance"/></option>
        </c:when>
        <c:otherwise>
          <option value="relevance"><fmt:message key="search_searchSorter.relevance"/></option>
        </c:otherwise>
      </c:choose>
      <c:choose>
        <c:when test="${sortByValue == 'lowHighPrice'}">
          <option value="lowHighPrice" selected><fmt:message key="search_searchSorter.lowHighPrice"/></option>
        </c:when>
        <c:otherwise>
          <option value="lowHighPrice"><fmt:message key="search_searchSorter.lowHighPrice"/></option>
        </c:otherwise>
      </c:choose>
      <c:choose>
        <c:when test="${sortByValue == 'highLowPrice'}">
          <option value="highLowPrice" selected><fmt:message key="search_searchSorter.highLowPrice"/></option>
        </c:when>
        <c:otherwise>
           <option value="highLowPrice"><fmt:message key="search_searchSorter.highLowPrice"/></option>
        </c:otherwise>
      </c:choose>
      
    </select>
    </div>
    
    <dsp:getvalueof var="docCandidates" param="searchResponse.docCandidates"/>
    <c:if test="${docCandidates != null && docCandidates > 20}">
        <span><fmt:message key="search_searchPaging.articlesParPage"/></span>
        <dsp:getvalueof var="articlesParPage" param="articlesParPage"/>
        <c:choose>
            <c:when test="${empty articlesParPage || articlesParPage == ''}">
                <dsp:getvalueof var="articlesParPage" bean="/atg/userprofiling/SessionBean.values.articlesParPage"/>
                <c:if test="${empty articlesParPage || articlesParPage == ''}">
                    <dsp:getvalueof var="articlesParPage" value="20" />
                    <dsp:setvalue bean="/atg/userprofiling/SessionBean.values.articlesParPage" value="20" />
                </c:if>
            </c:when>
            <c:otherwise>
              <dsp:setvalue bean="/atg/userprofiling/SessionBean.values.articlesParPage" value="${articlesParPage}" />
            </c:otherwise>
        </c:choose>
        <script>
        function articlesParPageSelected(selectedValue) {
            function removeParameterFromUrl(url, param){
                var patternNotLastParam = new RegExp(param+"&");
                var patternLastParam = new RegExp("[&\\?]"+param);
                url = url.replace(patternNotLastParam, "");
                url = url.replace(patternLastParam, "");
                return url;
            };
            
            if (selectedValue != '${articlesParPage}') {
                var newlocation = window.location.href;
                newlocation = removeParameterFromUrl(newlocation, "pageNum=\\d*");
                newlocation = removeParameterFromUrl(newlocation, "currentTab=produitsTab");
                newlocation = removeParameterFromUrl(newlocation, "currentTab=ideasTab");
                newlocation = removeParameterFromUrl(newlocation, "currentTab=magasinTab");
                if (newlocation.indexOf("articlesParPage") != -1) {
                    newlocation = newlocation.replace(/articlesParPage=\d*/,"articlesParPage="+selectedValue);
                } else {
                    if (newlocation.match("#$")=="#") {
                        newlocation = newlocation.substr(0, newlocation.length - 1);
                    }
                    var sep = "?";
                    if (newlocation.match(/\?/)){
                        sep = "&";
                    }
                    newlocation = newlocation + sep + "articlesParPage=" + selectedValue;
                }
                window.location.href = newlocation;
            }
        }
        </script>
        <div class="ddWrapper">
        <select class="styled" id="articlesParPageSelect" rel="articlesParPageSelected"  value="${articlesParPage}">
          <c:choose>
            <c:when test="${articlesParPage == '20'}">
              <option value="20" selected>20</option>
            </c:when>
            <c:otherwise>
              <option value="20">20</option>
            </c:otherwise>
          </c:choose>
          <c:choose>
            <c:when test="${articlesParPage == '60'}">
              <option value="60" selected>60</option>
            </c:when>
            <c:otherwise>
              <option value="60">60</option>
            </c:otherwise>
          </c:choose>
          <c:choose>
            <c:when test="${articlesParPage == '100'}">
              <option value="100" selected>100</option>
            </c:when>
            <c:otherwise>
               <option value="100">100</option>
            </c:otherwise>
          </c:choose>
        </select>
        </div>
    </c:if>
    
    <div class="viewSwitch">
    
      <c:choose>
        <c:when test="${productListingView != 'gallery' or empty productListingView}">

          <a class="btnListeSwither" title="Liste"  alt="Liste" href="javascript:void(0);">
            <span class="light">
              <fmt:message key="search_searchSorter.list"/>
            </span>
          </a>
        </c:when>
        <c:otherwise>
          <c:if test="${not fn:contains(categoryURI,'productListingView=')}">
            <dsp:getvalueof var="categoryURI" value="${categoryURI}${startSep}productListingView=list}"/>
          </c:if>
          <dsp:getvalueof var="categoryURI" value="${fn:replace(categoryURI,'=gallery', '=list')}"/>
          <a class="btnListeSwither" title="Liste"  alt="Liste" href="${contextPath}${categoryURI}">
          <span>
            <fmt:message key="search_searchSorter.list"/>
          </span>
          </a>
        </c:otherwise>
      </c:choose>

      <c:choose>
        <c:when test="${productListingView == 'gallery'}">
          <a class="btnGalSwither"  title="Galerie"  alt="Galerie" href="javascript:void(0);">
            <span class="light">
              <fmt:message key="search_searchSorter.gallery"/>
            </span>
          </a>
        </c:when>
        <c:otherwise>
          <c:if test="${not fn:contains(categoryURI,'productListingView=')}">
            <dsp:getvalueof var="categoryURI" value="${categoryURI}${startSep}productListingView=gallery"/>
          </c:if>
          <dsp:getvalueof var="categoryURI" value="${fn:replace(categoryURI,'=list', '=gallery')}"/>
          <a class="btnGalSwither"   title="Galerie" alt="Galerie" href="${contextPath}${categoryURI}" >
            <span>
              <fmt:message key="search_searchSorter.gallery"/>
            </span>
          </a>
        </c:otherwise>
        </c:choose>
      </div>
    </div>
  <div class="clear"></div>
</dsp:page>