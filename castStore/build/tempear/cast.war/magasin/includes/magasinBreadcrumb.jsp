<dsp:page>

<dsp:getvalueof var="magasinId" param="magasinId"/>
<dsp:getvalueof var="regionId" param="regionId"/>
<dsp:getvalueof var="departamentId" param="departamentId"/>



<c:if test="${not empty departamentId }">
	<dsp:droplet name="/com/castorama/droplet/DepartamentLookupDroplet">
		<dsp:param name="id" param="departamentId" />
		<dsp:param name="elementName" value="departament" />
		<dsp:oparam name="output">		
			<dsp:getvalueof var="departamentName" param="departament.nom" />					
		</dsp:oparam>
	</dsp:droplet>
</c:if>
<c:if test="${not empty regionId }">
<dsp:droplet name="/com/castorama/droplet/RegionLookupDroplet">
		<dsp:param name="id" param="regionId" />
		<dsp:param name="elementName" value="region" />
		<dsp:oparam name="output">
			<dsp:getvalueof var="regionName" param="region.nom" />
		</dsp:oparam>
	</dsp:droplet>
</c:if>
<c:if test="${not empty magasinId}">
	<dsp:droplet name="/com/castorama/droplet/MagasinLookupDroplet">
		<dsp:param name="id" param="magasinId" />
		<dsp:param name="elementName" value="magasin" />
		<dsp:oparam name="output">
			<dsp:getvalueof var="magasinName" param="magasin.nom" />
		</dsp:oparam>
	</dsp:droplet>			
</c:if>

<div class="breadcrumbs bluePage">
 	<div class="homeBreadIco">
 	  <a href="${pageContext.request.contextPath}">
 	    <img src="${pageContext.request.contextPath}/images/icoHomeGray.gif" alt="" title="" />
 	  </a>
	</div>	
  <div class="splitter">&gt;</div>
  <div><a href="${pageContext.request.contextPath}/magasin/magasin-carte-france.jsp">France</a></div>
  <div class="splitter">&gt;</div>
  <c:if test="${not empty regionId}">
    <c:choose>
      <c:when test="${not empty magasinId || not empty departamentId}">
        <div>
          <a href="${pageContext.request.contextPath}/magasin/magasin-carte-region.jsp?regionId=${regionId }">
      </c:when>
      <c:otherwise>
        <div class="active">
      </c:otherwise>
    </c:choose>
		
		<c:out value="${regionName}"/>
			 
    <c:choose>
      <c:when test="${not empty magasinId || not empty departamentId}">
	      </a></div>
	      <div class="splitter">&gt;</div>
	      </c:when>
      <c:otherwise>
        </div>
      </c:otherwise>
    </c:choose>
		
			
	  </c:if>
    <c:if test="${not empty departamentId}">
      <c:choose>
        <c:when test="${not empty magasinId }">
          <div>
          <a href="${pageContext.request.contextPath}/magasin/magasin-carte-region.jsp?departamentId=${departamentId}&regionId=${regionId }">
        </c:when>
        <c:otherwise>
          <div class="active">
        </c:otherwise>
      </c:choose>
      
      
        <c:out value="${departamentName}"/>
        
         <c:choose>
        <c:when test="${not empty magasinId }">
          </a></div>
          <div class="splitter">&gt;</div>
	        </c:when>
	      <c:otherwise>
	        </div>
	      </c:otherwise>
	    </c:choose>

    </c:if>
    <c:if test="${not empty magasinId}">
      <div class="active"><c:out value="${magasinNom}"/> </div>
    </c:if>
 </div>
 </dsp:page>
