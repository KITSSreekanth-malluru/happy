<dsp:page>
  <fmt:message key="forgot.password.mail.email" var="emailLabel" />
  <div id="subscription">
    <div class="right floatLeft">
      <img src="${pageContext.request.contextPath}/images/icoNewsletter.png" alt="Newsletter" /> <small><fmt:message key="msg.newsletter.subscription" /></small>
    </div>
    <form id="newsletterForm" action="${pageContext.request.contextPath}/user/myNewsletters1.jsp" method="get" onsubmit="subscribe()">
      <div class="newsletterFormDiv">
        <input id="newsletterMail" value="${emailLabel}" name="footerEmail" maxlength="50" />
        <input class="buttonMailOK" type="submit">
      </div>
    </form>
  </div>
</dsp:page>
<script>
  function subscribe() {
    var footerEmailValue = document.getElementById("newsletterMail").value;

    if ("${emailLabel}" == footerEmailValue) {
      document.getElementById("newsletterMail").value = "";
    }
  }
</script>
