<%@ taglib prefix="dsp"
	uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"%>
<dsp:page>
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/com/castorama/profile/CastProfileFormHandler"/>
<dsp:importbean bean="/com/castorama/commerce/clientspace/CastNewsletterFormHandler" />
<dsp:importbean bean="/atg/userprofiling/SessionBean"/>

<dsp:getvalueof var="magasinId" param="magasinId"/>

<dsp:getvalueof var="magasinsUrl" bean="/com/castorama/CastConfiguration.magasinsUrl" />
<c:if test="${empty magasinsUrl || magasinsUrl == ''}">
  <dsp:getvalueof var="magasinsUrl" value="${pageContext.request.contextPath}/magasin/magasin-fiche.jsp?parent=clientspace" />
</c:if>

<c:if test="${empty magasinId }">
  <dsp:getvalueof var="magasinId" value="magasinId"/>
</c:if>
<dsp:droplet name="/atg/dynamo/droplet/Compare">
 <dsp:param bean="/atg/userprofiling/Profile.securityStatus" name="obj1"/>
 <dsp:param bean="/atg/userprofiling/PropertyManager.securityStatusCookie" name="obj2"/>
 
  <dsp:oparam name="lessthan">
    <%-- if user logged out or auto-logged in --%>
      <dsp:a href="${pageContext.request.contextPath}/user/login.jsp" id="${magasinId }">
        <dsp:param name="loginSuccessURL" value="../magasin/magasin-fiche.jsp?parent=clientspace&magasinId=" />
      <fmt:message key="header.my.magasin" /></dsp:a>
  </dsp:oparam> <%-- end of lessthan --%>
  
  <dsp:oparam name="default">
  <%-- if user logged in or auto-logged in --%>
	  <dsp:setvalue bean="CastNewsletterFormHandler.repositoryId" beanvalue="Profile.login"/>
	  <dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
	    <dsp:param name="value" value="Profile.wrappedCurrentLocalStore"/>
	    <dsp:oparam name="false">
	      <%-- if  logged in or auto-logged in user has pref store --%>
	      <dsp:getvalueof var="prefStore" bean="Profile.wrappedCurrentLocalStore.nom"/>
	      <dsp:getvalueof var="prefStoreId" bean="Profile.wrappedCurrentLocalStore.repositoryId"/>
	      <c:choose>
	        <c:when test="${fn:length(prefStore)!=0}">
				<c:choose>
                 <c:when test="${prefStoreId!=999}">
                   <dsp:droplet name="/com/castorama/droplet/CastLookupDroplet">
					  <dsp:param name="id" value="${prefStoreId}"/>
					  <dsp:param name="elementName" value="repItem"/>
					  <dsp:param name="itemDescriptor" value="magasin" />
					  <dsp:param name="repository" bean="/atg/registry/Repository/MagasinGSARepository" />
					  <dsp:oparam name="output">
					  	<dsp:getvalueof var="storeUrl" param="repItem.storeUrl" />
				        <c:choose>
				      	  <c:when test="${not empty storeUrl && storeUrl != ''}">
				      	    <a href="${storeUrl}" target="_blank" id="${magasinId }">
                               <fmt:message key="header.my.magasin" /> 
                               :&nbsp;
                           		<dsp:valueof bean="Profile.wrappedCurrentLocalStore.nom"/>
                           	</a>
				      	  </c:when>
				      	  <c:otherwise>
                           <a href="${pageContext.request.contextPath}/magasin/magasin-fiche.jsp?magasinId=${prefStoreId}&parent=clientspace" id="${magasinId }">
                               <fmt:message key="header.my.magasin" /> 
                               :&nbsp;
                           <dsp:valueof bean="Profile.wrappedCurrentLocalStore.nom"/>
                           </a>
				      	  </c:otherwise>
				      	</c:choose>
					  </dsp:oparam>
					</dsp:droplet>
                 </c:when>
                 <c:otherwise>
                           <a href="${pageContext.request.contextPath}" id="${magasinId }">
                               <fmt:message key="header.my.magasin" /> 
                           </a>
                    </c:otherwise>  
                </c:choose>
	        </c:when>
	        <c:otherwise>
	          <a href="${magasinsUrl}" id="${magasinId }">
	          <fmt:message key="header.my.magasin" /></a>
	        </c:otherwise>  
	      </c:choose>
	    </dsp:oparam>
	    <dsp:oparam name="true">
	     <%-- if  logged in or auto-logged in user hasn't pref store --%>
	      <a href="${magasinsUrl}" id="${magasinId }">
	      <fmt:message key="header.my.magasin" /></a>
	    </dsp:oparam>
	  </dsp:droplet>  
  </dsp:oparam> <%-- end of default --%>
</dsp:droplet><%-- end of Compare --%>
</dsp:page>

