<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<HTML>
<HEAD>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8">
<TITLE>CREALFI</TITLE>
<LINK HREF="https://financement.transcred.com/Partenaires/CREALFI/epaiement/css/style.css" TYPE="text/css" REL="stylesheet">
<SCRIPT LANGUAGE="javascript" type="text/javascript">
<!--
    function updateLibelle(obj) {
        document.f.lb.value = obj.title;
    }
//-->
</SCRIPT>
</HEAD>
<BODY>
  <div id="container">
    <dsp:getvalueof var="host" bean="/com/castorama/util/ServerSetting.secureHost" />
    <form name="f" action="${host}${pageContext.request.contextPath}/html/commande/paiement/retour-sofinco.jsp" method="POST">
      <input type="hidden" name="rc" value="${param.rc}"> <input type="hidden" name="mc" value="${param.mc}"> <input type="hidden" name="lb" value="">
      <div id="top"></div>
      <div class="divTitre">
        <span class="labelTitre"><fmt:message key="sofinco.choose.payment" /></span>
      </div>

      <c:forEach begin="1" end="6" varStatus="status">
        <dsp:getvalueof var="index" value="${status.index}"/>
        <div class="divChoix">
          <div class="divOption">
            <fmt:message key="sofinco.payment.${index}.title" var="paymentTitle" />
            <input type="radio" name="op" value="10" title="${paymentTitle}" onClick="updateLibelle(this);"><span class="labelOption">${paymentTitle}</span>
          </div>
          <div class="divTxtCommercial">
            <span class="labelTxtCommercial"><fmt:message key="sofinco.payment.${index}.commerceText"/></span>
          </div>
          <div class="divExemple">
            <span class="labelExemple"><fmt:message key="sofinco.payment.${index}.example"/></span>
          </div>
        </div>
      </c:forEach>

      <div class="divBouton">
        <fmt:message key="sofinco.button.validate" var="validateButtonLabel" />
        <input type="submit" value="${validateButtonLabel}">
      </div>

      <div class="labelMention"><fmt:message key="sofinco.mention"/></div>
    </form>
  </div>
</BODY>
</HTML>
