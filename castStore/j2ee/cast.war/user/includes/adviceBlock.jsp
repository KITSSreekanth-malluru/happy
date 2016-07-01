<dsp:page>
  <dsp:importbean var="dynamoConfig" bean="/atg/dynamo/Configuration" />
  <dsp:importbean bean="/com/castorama/droplet/CastCheckOrdersCountDroplet"/>

  <dsp:getvalueof var="serverName" vartype="java.lang.String" value="${dynamoConfig.siteHttpServerName}" />
  <dsp:getvalueof var="serverPort" vartype="java.lang.String" value="${dynamoConfig.siteHttpServerPort}" />

  <dsp:getvalueof var="hideAdviceBannner" param="hideAdviceBannner"/>
  <dsp:getvalueof var="hideFAQBannner" param="hideFAQBannner"/>
  <dsp:getvalueof var="hideAdviceLink" param="hideAdviceLink"/>
  <c:if test="${'true' != hideAdviceBannner}">
    <div class="banner_block_1">
      <dsp:droplet name="CastCheckOrdersCountDroplet">
        <dsp:param name="userId" bean="/atg/userprofiling/Profile.repositoryId" />
        <dsp:oparam name="empty">
         <img src="/images/banners/contact_no_orders.png" alt="" title="" />
        </dsp:oparam>
        <dsp:oparam name="output">
         <img src="/images/banners/contact_1_banner.png" alt="" title="" />
        </dsp:oparam>
      </dsp:droplet>
    
       <c:if test="${'true' != hideAdviceLink}">
       <a href="${pageContext.request.contextPath}/contactUs/faq.jsp" class="moveLink">En savoir plus</a>
       </c:if>
    </div>
  </c:if>
  <c:if test="${'true' != hideFAQBannner}">
    <div class="questions_block">
      <div class="content">
        <h2><a href="${pageContext.request.contextPath}/contactUs/faq.jsp"><fmt:message key="faq.banner.header1" /><br /> 
      <fmt:message key="faq.banner.header2" /></a></h2>
      <ul>
        <li><fmt:message key="faq.banner.quest1" /></li>
        <li><fmt:message key="faq.banner.quest2" /></li>
        <li><fmt:message key="faq.banner.quest3" /></li>
        <li><fmt:message key="faq.banner.quest4" /></li>
        <li><fmt:message key="faq.banner.quest5" /></li>
      </ul>
      </div>
      <a href="${pageContext.request.contextPath}/contactUs/faq.jsp" class="moveLink"><fmt:message key="header.faq" /></a>          
    </div>
  </c:if>
</dsp:page>
