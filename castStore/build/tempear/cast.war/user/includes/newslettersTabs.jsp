<dsp:page>
  <dsp:getvalueof var="activeNewsletterTab" param="activeNewsletterTab" />

  <div class="titleSubText"></div>
  <div class="tabsArea tabsAreaNewsletter">
    <ul class="blueTabs">
      <c:choose>
        <c:when
          test="${not empty activeNewsletterTab && activeNewsletterTab == 'subscribeTab'}">
          <li class="active" id="subscribeTabHeader">
        </c:when>
        <c:otherwise>
          <li class="" id="subscribeTabHeader">
        </c:otherwise>
      </c:choose>
      <fmt:message var="subscribeTab"
        key="msg.newsletter.subscribeTab" />
      <a title="${subscribeTab }" href="myNewsletters1.jsp">
        ${subscribeTab} </a>
      </li>

      <c:choose>
        <c:when
          test="${not empty activeNewsletterTab && activeNewsletterTab == 'unsubscribeTab'}">
          <li class="active" id="unsubscribeTabHeader">
        </c:when>
        <c:otherwise>
          <li class="" id="unsubscribeTabHeader">
        </c:otherwise>
      </c:choose>
      <fmt:message var="unsubscribeTab"
        key="msg.newsletter.unsubscribeTab" />
      <a title="${unsubscribeTab }" href="newsletterUnsubscribe.jsp">
        ${unsubscribeTab} </a>
      </li>


      <c:choose>
        <c:when
          test="${not empty activeNewsletterTab && activeNewsletterTab == 'editSubscriptionTab'}">
          <li class="active" id="editSubscriptionTabHeader">
        </c:when>
        <c:otherwise>
          <li class="" id="editSubscriptionTabHeader">
        </c:otherwise>
      </c:choose>
      <fmt:message var="editSubscriptionTab"
        key="msg.newsletter.editSubscriptionTab" />
      <a title="${editSubscription }" href="myNewsletters2.jsp">
        ${editSubscriptionTab } </a>
      </li>
    </ul>
    <div class="blueTabsBorder"><!-- --></div>
  </div>
  <div class="clear"></div>
</dsp:page>