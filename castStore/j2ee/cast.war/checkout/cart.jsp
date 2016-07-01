<dsp:page>

  <cast:pageContainer>
    <jsp:attribute name="bodyContent">
      <dsp:include page="cartContent.jsp">
        <dsp:param name="mode" value="cart"/>
        <dsp:param name="invalidOrder" param="invalidOrder"/>
      </dsp:include>
    </jsp:attribute>
  </cast:pageContainer>
  <script type="text/javascript">
    initFields();
  </script>
</dsp:page>
