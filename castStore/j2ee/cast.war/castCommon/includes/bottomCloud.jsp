 <dsp:page>
  <dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>
  <dsp:importbean bean="/atg/commerce/catalog/CategoryLookup"/>

  <dsp:getvalueof var="cloudTypePar" param="cloudType"/>
  <c:if test="${empty cloudTypePar}">
    <c:set var="cloudTypePar" value="category"/>
  </c:if>

  <c:choose>
    <c:when test="${cloudTypePar == 'page'}">
      <dsp:getvalueof var="requestURI" bean="/OriginatingRequest.requestURI" />
      <c:set var="a1" value="${fn:split(requestURI,'/')}"/>
      <c:if test="${fn:length(a1) > 0}">
        <c:set var="referenceId" value="${a1[fn:length(a1)-1] }"/>
        <c:if test="${!fn:endsWith(referenceId, '.jsp')}">
          <c:set var="referenceId" value="home.jsp"/>
        </c:if>
        <c:if test="${fn:endsWith(referenceId, 'azIndex.jsp')}">
          <c:set var="referenceId" value="home.jsp"/>
        </c:if>
      </c:if>
      <c:if test="${empty referenceId}">
        <c:set var="referenceId" value="home.jsp"/>
      </c:if>
    </c:when>
    <c:when test="${cloudTypePar == 'advice'}">
      <dsp:droplet name="/com/castorama/droplet/CloudTermsDroplet">
        <dsp:param name="documentId" param="documentId"/>
        <dsp:oparam name="output">  
          <dsp:getvalueof var="referenceId" param="referenceId"/>
        </dsp:oparam>
      </dsp:droplet>
    </c:when>
    <c:when test="${cloudTypePar == 'static'}">
    </c:when>
    <c:otherwise> 
      <dsp:getvalueof var="referenceId" param="categoryId"/>
      <c:if test="${empty referenceId}">
        <dsp:droplet name="/com/castorama/droplet/CloudTermsDroplet">
          <dsp:param name="productId" param="productId"/>
          <dsp:oparam name="output">  
            <dsp:getvalueof var="referenceId" param="referenceId"/>
          </dsp:oparam>
        </dsp:droplet>
      </c:if>
      <c:if test="${empty referenceId}">
        <c:set var="referenceId" value=""/>
      </c:if>

      <dsp:droplet name="CategoryLookup">
        <dsp:param name="id" value="referenceId"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="color3" param="element.fixedStyle.color"/>
        </dsp:oparam>
      </dsp:droplet>
    </c:otherwise> 
  </c:choose>

  <dsp:droplet name="CategoryLookup">
    <dsp:param name="id" param="categoryId"/>
    <dsp:oparam name="output">
      <dsp:getvalueof var="cloudColor" param="element.style.cloudStyle"/>			
    </dsp:oparam>
  </dsp:droplet>

  <dsp:setvalue param="pReferenceId" value="${referenceId}"/>

  <c:if test="${not empty referenceId}">
    <dsp:droplet name="/atg/dynamo/droplet/RQLQueryRange">
      <dsp:param name="queryRQL" value="cloud.referenceId EQUALS :pReferenceId ORDER BY sortOrder"/>
      <dsp:param name="repository" value="/atg/registry/Repository/CloudRepository"/>
      <dsp:param name="itemDescriptor" value="cloud_term"/>
      <dsp:param name="howMany" value="50"/>

      <dsp:oparam name="outputStart">
        <div class="cloud ${cloudColor}">
          <div class="cloudContent">
            <h3>Top recherche</h3>
            <div class="linksRandom">
      </dsp:oparam>
      <dsp:oparam name="outputEnd">
              </div>
            </div>
          </div>
      </dsp:oparam>
      <dsp:oparam name="output">
        <dsp:getvalueof id="termUrl" param="element.url" idtype="java.lang.String"/>
        <dsp:getvalueof id="typeCat" param="element.type" idtype="java.lang.Integer"/>
        <dsp:getvalueof var="styleVal" value=""/>
        <c:choose> 
          <c:when test="${typeCat == 1}" > 
            <dsp:getvalueof var="className" value="level1"/>
          </c:when> 
          <c:when test="${typeCat == 2}" > 
            <dsp:getvalueof var="className" value="level2"/>
          </c:when> 
          <c:when test="${typeCat == 3}" > 
            <dsp:getvalueof var="className" value="level3"/>
          </c:when> 
          <c:when test="${typeCat == 4}" > 
            <dsp:getvalueof var="className" value="level4"/>
          </c:when> 
          <c:otherwise> 
            <dsp:getvalueof var="className" value=""/>
          </c:otherwise> 
        </c:choose>  
        
        <c:if test="${fn:contains(termUrl, '/rechercher') }">
	        <c:choose>
	          <c:when test="${not empty termUrl && (fn:endsWith(termUrl, '.jsp') || fn:endsWith(termUrl, '.html') || fn:endsWith(termUrl, '.htm')) }">
	            <dsp:getvalueof var="termUrl" value="${termUrl}?osearchmode=tagcloud"/>
	          </c:when>
	          <c:otherwise>
	            <c:if test="${not empty termUrl}">
	              <dsp:getvalueof var="termUrl" value="${termUrl}&osearchmode=tagcloud"/>
	            </c:if>
	          </c:otherwise>
	        </c:choose>
        </c:if>

        <span class="${className}">
          <dsp:a href="${termUrl}"><dsp:valueof param="element.term"/></dsp:a>
        </span>
      </dsp:oparam>

      <dsp:oparam name="empty">
      </dsp:oparam>
    </dsp:droplet>
  </c:if>

</dsp:page>
