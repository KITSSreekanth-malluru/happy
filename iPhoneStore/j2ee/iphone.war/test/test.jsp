<dsp:page>
  <cast:pageContainer>
    <jsp:attribute name="bodyContent">
      <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
      <table>
        <thead>
          <tr>
            <td>Gateway name</td>
            <td>Test URL</td>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>Search Gateway
            </td>
            <td>
              <a href="${contextPath}/test/includes/searchTest.jsp">
                Search Gateway
              </a>
            </td>
          </tr>
          <tr>
            <td>
            </td>
            <td>
            </td>
          </tr>
        </tbody>
      </table>
    </jsp:attribute>
  </cast:pageContainer>
</dsp:page>