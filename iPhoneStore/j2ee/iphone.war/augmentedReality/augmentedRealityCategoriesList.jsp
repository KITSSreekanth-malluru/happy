<%@page contentType="application/json"%>
<dsp:page>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean var="dynamoConfig" bean="/atg/dynamo/Configuration" />
  <dsp:importbean var="mobileConfig" bean="/atg/mobile/MobileConfiguration" />
  <dsp:getvalueof var="serverName" vartype="java.lang.String" value="${dynamoConfig.siteHttpServerName}" />
  <dsp:getvalueof var="serverPort" vartype="java.lang.String" value="${dynamoConfig.siteHttpServerPort}" />
  <dsp:getvalueof var="httpServer" vartype="java.lang.String" value="http://${serverName}:${serverPort}" />
  <dsp:getvalueof var="contextPath" vartype="java.lang.String" value="${originatingRequest.contextPath}" />
  <dsp:getvalueof var="httpLink" vartype="java.lang.String" value="${httpServer}${contextPath}" />
  
  <cast:pageContainer>
    <jsp:attribute name="bodyContent">
      <json:object>
        <dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
            <dsp:param name="value" bean="Profile.catalog.augmentedRealityCategory"/>
            <dsp:oparam name="true"> 
              <json:property name="result" value="1"/>
              <json:property name="errorMessage"><fmt:message key="er_117"/></json:property>
              <json:array name="categories"/>
            </dsp:oparam>
            <dsp:oparam name="false"> 
              <dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
              <dsp:param name="value" bean="Profile.catalog.augmentedRealityCategory.fixedChildCategories"/>
              <dsp:oparam name="true">
                <json:property name="result" value="1"/>
                <json:property name="errorMessage"><fmt:message key="er_117"/></json:property>
                <json:array name="categories"/>
              </dsp:oparam>
              <dsp:oparam name="false"> 
                <json:property name="result" value="0"/>
                <json:property name="errorMessage" value=""/>
                <json:array name="categories">
                    <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                      <dsp:param name="array" bean="Profile.catalog.augmentedRealityCategory.fixedChildCategories"/>
                      <dsp:param name="elementName" value="childCategory"/>
                      <dsp:oparam name="output"> 
                        <json:object>
                          <json:property name="id"><dsp:valueof param="childCategory.id"/></json:property>
                          <json:property name="title"><dsp:valueof param="childCategory.displayName"/></json:property>
                          <dsp:getvalueof var="image" param="childCategory.categoryDisplayImage.url"/>
                          <c:if test="${not empty image && (!fn:startsWith(image,'http') && !fn:startsWith(image,'https'))}">
                            <c:set var="image" value="${httpLink}${image}"/>
                          </c:if>
                          <json:property name="imageURL" value="${image}"/>
                        </json:object>
                      </dsp:oparam>
                    </dsp:droplet>
                </json:array>
              </dsp:oparam>
            </dsp:droplet>
          </dsp:oparam>
        </dsp:droplet>
      </json:object>
    </jsp:attribute>
  </cast:pageContainer>
</dsp:page>