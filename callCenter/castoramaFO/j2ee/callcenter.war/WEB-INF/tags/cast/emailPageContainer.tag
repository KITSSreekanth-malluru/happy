<%--
  Tag that acts as a container for top-level pages for all email templates, including all relevant
  header, footer elements wherever required.
  The body of this tag should include any required gadgets.

  If any of the divId, titleKey or textKey attributes are set, then the
  emailPageIntro gadget will be included.  If none of these attributes are
  specified, then the pageIntro gadget will not be included.

  This tag accepts the following attributes
  divId (optional) - id for the containing div. Will be passed to the pageIntro
                     gadget.
  titleKey (optional)- resource bundle key for the title. Will be passed to the
                       pageIntro gadget.
  textKey (optional) - resource bundle key for the intro text. Will be passed
                       to the pageIntro gadget.
  titleString(optional) - Title String  that will be passed to the pageIntro gadget
  textString(optional) - Intro text string that will be passed to the pageIntro gadget

  displayHeader (optional) - Specify whether to render/not-render the common email header 
                       in the email content (Boolean, default-true)
  displayFooter (optional) - Specify whether to render/not-render the common email footer 
                       in the email content (Boolean, default-true)

  messageSubjectKey (optional) - Resource Key for the string to be used as Subject of the email
  messageSubjectString (optional) - String to be used as Subject of the email
  messageFromAddressString (optional) - Email address to be set as Sender's address for the email

  The tag accepts the following fragments
  subNavigation - define a fragment that will be contain sub navigation gadgets.
                  If required, use as
                    <jsp:attribute name="subNavigation">
                      ....
                    </jsp:attribute>
--%>

<%@ include file="/includes/taglibs.jspf" %>

<%@ tag language="java" %>

<%@ attribute name="divId" %>
<%@ attribute name="titleKey" %>
<%@ attribute name="textKey" %>
<%@ attribute name="titleString" %>
<%@ attribute name="textString" %>
<%@ attribute name="displayHeader" type="java.lang.Boolean" %>
<%@ attribute name="displayFooter" type="java.lang.Boolean" %>
<%@ attribute name="messageSubjectKey" %>
<%@ attribute name="messageSubjectString" %>
<%@ attribute name="messageFromAddressString" %>
<%@ attribute name="messageBody" fragment="true" %>

<dsp:page>

  <dsp:importbean var="dynamoConfig" bean="/atg/dynamo/Configuration"/>
  <dsp:importbean var="originatingRequest" bean="/OriginatingRequest"/>

  <dsp:getvalueof var="serverName" vartype="java.lang.String" value="${dynamoConfig.siteHttpServerName}"/>
  <dsp:getvalueof var="serverPort" vartype="java.lang.String" value="${dynamoConfig.siteHttpServerPort}"/>
  <dsp:getvalueof var="httpServer" vartype="java.lang.String" value="http://${serverName}:${serverPort}"/>
  <dsp:getvalueof var="contextPath" vartype="java.lang.String" value="${originatingRequest.contextPath}"/>
  <dsp:getvalueof var="httpLink" vartype="java.lang.String" value="${httpServer}${contextPath}"/>

  <%-- Show the header if requested. --%>
  <c:if test="${empty displayHeader || displayHeader}">
    <dsp:include page="/emailTemplates/includes/header.jsp">
      <dsp:param name="httpserver" value="${httpServer}"/>
      <dsp:param name="httplink" value="${httpLink}"/>
    </dsp:include>
  </c:if>


  <%-- Set TemplateInfo parameters if required --%>
  <cast:messageWithDefault key="${messageSubjectKey}" string="${messageSubjectString}"/>
  <c:if test="${!empty messageText}">
    <dsp:setvalue param="messageSubject" value="${messageText}"/>
  </c:if>

  <c:if test="${!empty messageFromAddressString}">
    <dsp:setvalue param="messageFrom" beanvalue="${messageFromAddressString}"/>
  </c:if>

  <jsp:invoke fragment="messageBody"/>

  <%-- Show the footer if requested. --%>
  <c:if test="${empty displayFooter || displayFooter}">
    <dsp:include page="/emailTemplates/includes/footer.jsp">
      <dsp:param name="httpserver" value="${httpServer}"/>
      <dsp:param name="httplink" value="${httpLink}"/>
    </dsp:include>
  </c:if>

</dsp:page>