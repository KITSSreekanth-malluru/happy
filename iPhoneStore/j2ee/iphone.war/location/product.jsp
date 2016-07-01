<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml"%>
<%@ taglib prefix="dsp" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json"%>
<%@ taglib prefix="m" tagdir="/WEB-INF/tags/mobile"%>
<%@ taglib prefix="cast" tagdir="/WEB-INF/tags/cast"%>
<%@ taglib prefix="castCollection" uri="http://castFunction"%>
<%@page contentType="application/json"%>
<dsp:page>
  <m:jsonObject>
    <dsp:getvalueof var="stockStoreId" param="stockStoreId" />
    <dsp:getvalueof var="codeArticle" param="codeArticle" />
    <dsp:getvalueof var="lang" param="language" />
    <c:if test="${empty lang}">
      <dsp:getvalueof var="lang" value="fr" />
    </c:if>
    <dsp:getvalueof var="errorCode" value="0" />
    <fmt:setLocale value="${lang}"/>
    <fmt:setBundle basename="com.castorama.resources.CastMobileWebAppResources" var="bund" />
    <json:array name="locations">
      <c:catch var="ex">
        <dsp:droplet name="/com/castorama/mobile/droplet/EmplacementLookupDroplet">
          <dsp:param name="stockStoreId" param="stockStoreId" />
          <dsp:param name="productId" param="codeArticle" />
          <dsp:oparam name="output">
            <dsp:droplet name="/atg/dynamo/droplet/ForEach">
              <dsp:param name="array" param="emplacements" />
              <dsp:oparam name="output">
                <m:jsonObject>
                  <json:property name="level">
                    <dsp:getvalueof var="levelCode" param="element.localisation.levelCode" />
                    <fmt:message key="location.level.${levelCode}" bundle="${bund}" />
                  </json:property>
                  <json:property name="path">
                    <dsp:getvalueof var="pathNumber" param="element.localisation.pathNumber" />
                    <fmt:message key="location.path" bundle="${bund}" />${pathNumber}
                  </json:property>
                  <json:property name="side">
                    <dsp:getvalueof var="sideCode" param="element.localisation.sideCode" />
                    <fmt:message key="location.side.${sideCode}" bundle="${bund}" />
                  </json:property>
                  <json:property name="localization">
                    <dsp:getvalueof var="localizationCode" param="element.localisation.localizationCode" />
                    <fmt:message key="location.localization.${localizationCode}" bundle="${bund}" />
                  </json:property>
                </m:jsonObject>
              </dsp:oparam>
            </dsp:droplet>
          </dsp:oparam>
          <dsp:oparam name="empty">
            <dsp:getvalueof var="errorCode" value="1" />
            <fmt:message var="errorMessage" key="location.search.isEmpty" bundle="${bund}" />
          </dsp:oparam>
        </dsp:droplet>
      </c:catch>
    </json:array>
    <c:if test="${not empty ex }">
      <%-- start catch exception of search engine --%>
      <dsp:getvalueof var="errorCode" value="1" />
      <fmt:message var="errorMessage" key="location.search.error" bundle="${bund}" />
      <%-- end of search engine exception --%>
    </c:if>
    <json:property name="errorCode" value="${errorCode}" />
    <json:property name="errorMessage" value="${errorMessage}" />
  </m:jsonObject>
</dsp:page>
