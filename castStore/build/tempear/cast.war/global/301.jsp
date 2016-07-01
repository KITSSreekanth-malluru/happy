<%@ page contentType="text/html; charset=UTF-8"%>
<dsp:page>
  <cast:pageContainer>
  
    <jsp:attribute name="bodyContent">
      <div class="content pageError">
        <div>
          <div id="ct_deprecated">
            <h1><fmt:message key="global.301.title"/></h1>
            <h2>
              <fmt:message key="global.301.redirect">
                <fmt:param>
                  <a href="/store">cliquez ici</a>
                </fmt:param>
              </fmt:message>
            </h2>
          </div>
        </div>
      </div>
    </jsp:attribute>
  </cast:pageContainer>
  
  <script type="text/javascript" language="javascript">
   <!--
    setTimeout(function() {window.location.href = "/store";}, 5000);
   //-->
  </script>
  <!-- Fin content -->
</dsp:page>