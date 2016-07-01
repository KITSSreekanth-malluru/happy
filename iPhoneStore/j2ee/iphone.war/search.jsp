<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>

<%@ taglib prefix="dsp" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>

<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json" %>

<%@ taglib prefix="m" tagdir="/WEB-INF/tags/mobile" %>
<%@ taglib prefix="cast" tagdir="/WEB-INF/tags/cast" %>
<%@ taglib prefix="castCollection" uri="http://castFunction"%>
<%@page contentType="application/json"%>
<dsp:page>
  <dsp:importbean bean="/com/castorama/mobile/search/MobileQueryFormHandler" />
  <dsp:importbean bean="/atg/search/repository/FacetSearchTools" />
  <dsp:importbean var="dynamoConfig" bean="/atg/dynamo/Configuration" />
  <dsp:getvalueof var="serverName" vartype="java.lang.String" value="${dynamoConfig.siteHttpServerName}" />
  <dsp:getvalueof var="serverPort" vartype="java.lang.String" value="${dynamoConfig.siteHttpServerPort}" />
  <dsp:getvalueof var="httpServer" vartype="java.lang.String" value="http://${serverName}:${serverPort}" />
  <dsp:getvalueof var="contextPath" vartype="java.lang.String" value="${originatingRequest.contextPath}" />
  <dsp:getvalueof var="httpLink" vartype="java.lang.String" value="${httpServer}${contextPath}" />
  <dsp:importbean bean="/atg/commerce/search/refinement/RefinementValueDroplet" />
  <dsp:getvalueof var="searchKeyword" param="searchKeyword" />
  <dsp:getvalueof var="n" param="n" />
  <dsp:getvalueof var="pageNum" param="pageNum" />
  <dsp:getvalueof var="appliedFacets" param="appliedFacets" />
  <dsp:getvalueof var="lang" param="lang" />
  <c:set var="searchKeyword" value="${fn:trim(searchKeyword)}" />
  <c:set var="pageSize" value="9" />
  
    <c:if test="${!empty searchKeyword}">
      <dsp:setvalue bean="MobileQueryFormHandler.searchRequest.question" value="${searchKeyword}" />
    </c:if>
    <dsp:droplet name="/atg/targeting/TargetingFirst">
      <dsp:param name="targeter" bean="/atg/registry/RepositoryTargeters/ProductCatalog/RootCategoryTargeter" />
      <dsp:param name="howMany" value="1" />
      <dsp:param name="elementName" value="rootCategory" />
      <dsp:oparam name="output">
        <dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
          <dsp:param name="value" param="rootCategory" />
          <dsp:oparam name="false">
            <dsp:getvalueof var="rootCategoryId" param="rootCategory.repositoryId" />
          </dsp:oparam>
        </dsp:droplet>
      </dsp:oparam>
    </dsp:droplet>
    <c:if test="${not empty rootCategoryId}">
      <dsp:setvalue bean="FacetSearchTools.categoryId" value="${rootCategoryId}" />
    </c:if>
    <c:if test="${not empty n}">
      <dsp:setvalue bean="MobileQueryFormHandler.searchRequest.pageSize" value="${n}" />
    </c:if>
    <c:choose>
      <c:when test="${not empty appliedFacets}">
        <dsp:setvalue bean="FacetSearchTools.facetTrail" value="${appliedFacets}" />
        <dsp:setvalue bean="MobileQueryFormHandler.searchRequest.facetSearchRequest" value="true" />
        <dsp:setvalue bean="MobileQueryFormHandler.searchRequest.multiSearchSession" value="true" />
      </c:when>
      <c:otherwise>
        <dsp:setvalue bean="FacetSearchTools.facetTrail" value="SRCH:${fn:trim(searchKeyword)}" />
      </c:otherwise>
    </c:choose>
    <dsp:setvalue bean="MobileQueryFormHandler.searchRequest.sorting" value="property" />
    <dsp:setvalue bean="MobileQueryFormHandler.searchRequest.saveRequest" value="true" />
    <dsp:setvalue bean="MobileQueryFormHandler.searchRequest.refineConfig" value="GLOBAL" />
    <dsp:setvalue bean="MobileQueryFormHandler.searchRequest.pageNum" value="${pageNum}" />
    <%-- add one to the pageNumber being sent since it's zero based, then remove it after it's used --%>
    <c:set var="pageNum" value="${pageNum + 1}" />
    <c:if test="${pageNum > 1}">
      <%-- convert the int to a string because that's what goToPage expects --%>
      <c:set var="pageNumberString">${pageNum}</c:set>
        <dsp:setvalue bean="MobileQueryFormHandler.goToPage" value="${pageNumberString}" />
      </c:if>
    <c:set var="pageNum" value="${pageNum - 1}" />
    <%--make sure we prohibit searchandizing redirects--%>
    <dsp:setvalue bean="MobileQueryFormHandler.redirectEnabled" value="false" />
    <c:catch var="ex">
    <dsp:setvalue bean="MobileQueryFormHandler.search" value="submit" />
    </c:catch>
    <dsp:getvalueof var="errorCode" value="0"/>
    <fmt:setLocale value="${lang}"/>
    <fmt:setBundle basename="com.castorama.resources.CastMobileWebAppResources" var="bund"/>
    <c:if test="${not empty ex }">
      <%-- start catch exception of search engine --%>
      <dsp:getvalueof var="errorCode" value="1"/>
      <c:if test="${empty lang}">
        <dsp:getvalueof var="lang" value="fr" />
      </c:if>
      <fmt:message var="errorMessage" key="search.searchUnavailable" bundle="${bund}"/>
      <%-- end of search engine exception --%>
    </c:if>
    <dsp:getvalueof bean="MobileQueryFormHandler.searchResponse" var="queryResponse"/>
    
    
    <m:jsonObject>
    <c:if test="${not empty searchKeyword || not empty appliedFacets}">
      <json:array name="products">
        <c:forEach var="searchResult" items="${queryResponse.results}" varStatus="searchResultStatus">
          <dsp:getvalueof var="count" value="${searchResultStatus.count}" />
          <dsp:include page="/search/product.jsp">
            <dsp:param name="repositoryId" value="${searchResult.document.properties.$repositoryId}" />
          </dsp:include>
        </c:forEach>
      </json:array>
    </c:if>
    <%-- render available facets --%>
    <json:property name="totalize"><dsp:valueof bean="FacetSearchTools.facetTrail"/></json:property>
    <c:if test="${not empty searchKeyword || not empty appliedFacets}">
    <json:array name="facets">
      <dsp:droplet name="/atg/dynamo/droplet/ForEach">
        <dsp:param name="array" bean="FacetSearchTools.facets"/>
        <dsp:param name="elementName" value="facetHolder"/>
        <dsp:oparam name="output">
        <dsp:getvalueof var="facetName" param="facetHolder.facet.label" />
        <c:if test="${facetName == 'facet.label.Category'}">
          <dsp:getvalueof var="facetProperty" param="facetHolder.facet.refinementElement.property" />
          <dsp:getvalueof var="facetId" param="facetHolder.facet.id" />
          <c:if test="${facetProperty != 'ancestorCategories.displayName' || !(empty searchKeyword && empty appliedFacets)}">
            <%--${facetProperty != 'onSale'}" -> ${facetProperty != 'rating'}--%>
            <c:if test="${facetProperty != 'rating'}">
              <m:jsonObject>
                <json:property name="facetName">
                  <fmt:message key="${facetName}" />
                </json:property>
                <json:property name="facetId" value="${facetId}" />
                <json:array name="facetValues">
                  <dsp:droplet name="/com/castorama/search/droplet/CastFacetValuesDroplet">
                    <dsp:param name="facetValues" param="facetHolder.facetValueNodes"/>
                    <dsp:oparam name="oparam">
                      <dsp:getvalueof var="facetValuesResultListVar" param="facetValuesResultList"/>
                      <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                        <dsp:param name="array" param="facetValuesResultList"/>
                        <dsp:param name="elementName" value="facetValue"/>
                        <dsp:oparam name="output">
                        <dsp:getvalueof var="facetValue" param="facetValue" />
                        <dsp:getvalueof var="valueNodeCount" param="facetValue.facetValue.matchingDocsCount" />
                        <m:jsonObject>
                          <dsp:droplet name="RefinementValueDroplet">
                            <dsp:param name="refinementId" value="${facetValue.facetValue.facet.refinementElement.id}" />
                            <dsp:param name="refinementValue" value="${facetValue.facetValue.value}" />
                            <dsp:oparam name="output">
                              <dsp:getvalueof var="displayValue" param="displayValue" />
                              <json:property name="facetValueName" value="${displayValue}" />
                            </dsp:oparam>
                          </dsp:droplet>
                          <json:property name="facetValueId" value="${facetValue.facetValue.value}" />
                        </m:jsonObject>
                      
                      </dsp:oparam>
                      </dsp:droplet>
                    </dsp:oparam>
                  </dsp:droplet>
                </json:array>
              </m:jsonObject>
            </c:if>
          </c:if>
        </c:if>
      </dsp:oparam>
      <dsp:oparam name="empty">
        <dsp:getvalueof var="errorCode" value="1"/>
        <fmt:message var="errorMessage" key="search.results.isEmpty" bundle="${bund}"/>
      </dsp:oparam>
      </dsp:droplet>
    </json:array>
    <dsp:getvalueof var="totalResults" bean="MobileQueryFormHandler.searchResponse.groupCount" />
    <json:property name="total" value="${totalResults}" />
    <json:property name="errorCode" value="${errorCode}" />
    <json:property name="errorMessage" value="${errorMessage}" />
    </c:if>
  </m:jsonObject>
</dsp:page>