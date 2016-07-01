<dsp:page>

	<dsp:importbean bean="/atg/dynamo/droplet/Redirect" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/com/castorama/commerce/clientspace/CastNewsletterFormHandler" />
	
	<dsp:setvalue bean="CastNewsletterFormHandler.repositoryId" beanvalue="Profile.login"/>
	
	<dsp:getvalueof var="magasinId" param="magasinId"/>
	<dsp:getvalueof var="parent" param="parent"/>
      <dsp:getvalueof var="fromSVContext" param="fromSVContext" />
  
	<c:choose>
  <c:when test="${parent == 'clientspace'}">
  
     <dsp:droplet name="/atg/dynamo/droplet/Compare">
      <dsp:param bean="/atg/userprofiling/Profile.securityStatus" name="obj1"/>
      <dsp:param bean="/atg/userprofiling/PropertyManager.securityStatusLogin" name="obj2"/>
       <dsp:oparam name="lessthan">
         <%-- if user ananymous or auto-logged in - > redirect on login and than (if user doesn't have pref store on Find Store page) --%>
         <dsp:setvalue bean="/atg/userprofiling/SessionBean.values.loginSuccessURL" value="${pageContext.request.contextPath}/magasin/magasin-fiche.jsp?parent=clientspace" />
          <dsp:droplet name="/atg/dynamo/droplet/Redirect">
            <dsp:param name="url" value="/user/login.jsp" />
          </dsp:droplet>
       </dsp:oparam>
       <dsp:oparam name="default">
       <%-- if user logged in and magasin id is empty- > redirect on Find Store page) --%>
          <c:if test="${empty magasinId }">
	          <dsp:getvalueof var="magasinId" bean="CastNewsletterFormHandler.value.id_magasin_ref.repositoryId"/>         
	          <c:if test="${empty magasinId || fn:length(magasinId)==0}">         
	             <dsp:droplet name="Redirect">
	               <dsp:param name="url" value="/magasin/magasin-carte-france.jsp" />
	             </dsp:droplet>
	          </c:if>
          </c:if>
       </dsp:oparam>
      </dsp:droplet>
  </c:when>
	
	 <c:when test="${empty magasinId && parent != 'clientspace'}">
      <%-- params are empty -> on Find Store --%>
      <dsp:droplet name="Redirect">
        <dsp:param name="url" value="/magasin/magasin-carte-france.jsp" />
      </dsp:droplet>  
  </c:when>
  
	</c:choose>
<cast:pageContainer>
    <jsp:attribute name="bodyContent">
			<dsp:include page="includes/magasin-fiche-content.jsp" flush="true">
			 <dsp:param name="magasinId" value="${magasinId}"/>
             <dsp:param name="fromSVContext" value="${fromSVContext}" />
			</dsp:include>
			
			<%-- Omniture params Section begins--%>
			<dsp:getvalueof var="departamentId" param="departamentId"/>
			<c:if test="${not empty departamentId}">
				<dsp:droplet name="/com/castorama/droplet/DepartamentLookupDroplet">
					<dsp:param name="id" param="departamentId" />
					<dsp:param name="elementName" value="departament" />
					<dsp:oparam name="output">		
						<dsp:getvalueof var="departamentName" param="departament.nom" />					
					</dsp:oparam>
				</dsp:droplet>
			</c:if>
			<c:if test="${not empty magasinId}">
				<dsp:droplet name="/com/castorama/droplet/MagasinLookupDroplet">
					<dsp:param name="id" param="magasinId" />
					<dsp:param name="elementName" value="magasin" />
					<dsp:oparam name="output">
						<dsp:getvalueof var="magasinName" param="magasin.nom" />
						
						   <%-- we need departament name for omniture --%>
				      <c:if test="${empty departamentId}">
				       <dsp:getvalueof var="departamentName" param="magasin.entite.adresse.departement.nom" />
				      </c:if>
					</dsp:oparam>
				</dsp:droplet>			
			</c:if>
		   	<fmt:message var="omniturePageName" key="omniture.pageName.shops.shop"/>
			<fmt:message var="omnitureChannel" key="omniture.channel.shops"/>
			 

      
			<c:set var="omniturePageName" value="${omniturePageName}${departamentName}:${magasinName}" scope="request"/>
			<c:set var="omnitureChannel" value="${omnitureChannel}" scope="request"/>
			<%-- Omniture params Section ends--%>
			
     </jsp:attribute>
	</cast:pageContainer>
	<dsp:getvalueof var="street0" param="street0" />
	<dsp:getvalueof var="city0" param="city0" />
	<dsp:getvalueof var="magasinId" param="magasinId" />
	<c:import context="${pageContext.request.contextPath}" url="/magasin/includes/magasin-direction.jsp" charEncoding="ISO-8859-1">
		<c:param name="street0" value="${street0}" />
		<c:param name="city0" value="${city0}" />
		<c:param name="magasinId" value="${magasinId}" />
	</c:import>

	<dsp:getvalueof var="error" param="error" />

	<c:if test="${not empty error && fn:length(error)>0}">
		<script type="text/javascript">
	// $(window).load(function () {
		showPopup('${error}');
	//	});		
	</script>
	</c:if>
	
</dsp:page>