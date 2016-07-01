<dsp:page>
    <form name="localStoresManagerTestForm"
          action="${pageContext.request.contextPath}/test/localStoresManagerTestPage.jsp"
          method="get">
        <table border="0">
            <tr>
                <td><fmt:message key="test.localStoresInfo.input.cast.storeId"/></td>
                <td><input type="text" name="storeId"></td>
                <td><fmt:message key="test.localStoresInfo.input.info.storeId"/></td>
            </tr>
            <tr>
                <td><fmt:message key="test.localStoresInfo.output.skuId"/>:</td>
                <td><input type="text" name="skuId"></td>
                <td><fmt:message key="test.localStoresInfo.input.info.skuId"/></td>
            </tr>
            <tr>
                <td><fmt:message key="test.localStoresManager.output.packId"/>:</td>
                <td><input type="text" name="packId"></td>
                <td><fmt:message key="test.localStoresInfo.input.info.packId"/></td>
            </tr>
        </table>
        <input type="submit" value="Submit"/>
    <form>

    <dsp:droplet name="/com/castorama/test/pages/LocalStoresManagerTestDroplet">
        <dsp:param name="storeId" value="${param.storeId}"/>
        <dsp:param name="skuId" value="${param.skuId}"/>
        <dsp:param name="packId" value="${param.packId}"/>
        <dsp:oparam name="output">
            <table border=0>
                <c:if test="${not empty param.skuId}">
                    <tr><td><fmt:message key="test.localStoresManager.output.result.sku"/> ${param.skuId}:</td></tr>
                    <tr><td><dsp:valueof param="resultForSku"/></td></tr>
                </c:if>
                <dsp:getvalueof var="result" param="resultForSkuWithBundle"/>
                <c:if test="${not empty result}">
                    <tr><td><fmt:message key="test.localStoresManager.output.result.sku.bundles"/> ${param.skuId}:</td></tr>
                    <tr><td><dsp:valueof param="resultForSkuWithBundle"/></td></tr>
                </c:if>
                <c:if test="${not empty param.packId}">
                    <tr><td><fmt:message key="test.localStoresManager.output.result.pack"/> ${param.packId}:</td></tr>
                    <tr><td><dsp:valueof param="resultForPack"/></td></tr>
                </c:if>
            </table>
        </dsp:oparam>
        <dsp:oparam name="error">
            <br><dsp:valueof param="errorMessage"/>
        </dsp:oparam>
    </dsp:droplet>
</dsp:page>