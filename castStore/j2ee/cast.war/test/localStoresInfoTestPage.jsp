<dsp:page>

    <dsp:importbean bean="/com/castorama/test/pages/LocalStoresInfoFormHandler"/>
    <dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>

    <dsp:form name="localStoresInfoTestForm"
              action="${pageContext.request.contextPath}/test/localStoresInfoTestPage.jsp"
              method="post">
        <table border="0">
            <tr>
                <td><fmt:message key="test.localStoresInfo.input.cast.storeId"/></td>
                <td><dsp:input type="text" bean="LocalStoresInfoFormHandler.storeId"/></td>
            </tr>
            <tr>
                <td><fmt:message key="test.localStoresInfo.input.codeArticle"/></td>
                <td><dsp:input type="text" bean="LocalStoresInfoFormHandler.codeArticle"/></td>
            </tr>
        </table>
        <dsp:input type="submit" bean="LocalStoresInfoFormHandler.submit" value="Submit"/>
    </dsp:form>

    <dsp:droplet name="IsEmpty">
        <dsp:param name="value" param="storeInfo"/>
        <dsp:oparam name="false">
            <dsp:getvalueof var="displayDiscountBeginDate" param="storeInfo.displayDiscountBeginDate"
                            vartype="java.util.Date"/>
            <dsp:getvalueof var="displayDiscountEndDate" param="storeInfo.displayDiscountEndDate"
                            vartype="java.util.Date"/>
            <table border="1">
                <tr>
                    <td><fmt:message key="test.localStoresInfo.output.atg.storeId"/></td>
                    <td><dsp:valueof param="storeInfo.storeId"/></td>
                </tr>
                <tr>
                    <td><fmt:message key="test.localStoresInfo.output.cast.storeId"/></td>
                    <td><dsp:valueof param="storeInfo.castoramaStoreId"/></td>
                </tr>
                <tr>
                    <td><fmt:message key="test.localStoresInfo.output.skuId"/></td>
                    <td><dsp:valueof param="storeInfo.skuId"/></td>
                </tr>
                <tr>
                    <td><fmt:message key="test.localStoresInfo.output.codeArticle"/></td>
                    <td><dsp:valueof param="storeInfo.codeArticle"/></td>
                </tr>
                <tr>
                    <td><fmt:message key="test.localStoresInfo.output.localPrice"/></td>
                    <td><dsp:valueof param="storeInfo.localPrice"/></td>
                </tr>
                <tr>
                    <td><fmt:message key="test.localStoresInfo.output.MD3E"/></td>
                    <td><dsp:valueof param="storeInfo.md3e"/></td>
                </tr>
                <tr>
                    <td><fmt:message key="test.localStoresInfo.output.MREP"/></td>
                    <td><dsp:valueof param="storeInfo.mrep"/></td>
                </tr>
                <tr>
                    <td><fmt:message key="test.localStoresInfo.output.promo.flag"/></td>
                    <td><dsp:valueof param="storeInfo.fPromo"/></td>
                </tr>
                <tr>
                    <td><fmt:message key="test.localStoresInfo.output.promo.code"/></td>
                    <td><dsp:valueof param="storeInfo.codePromo"/></td>
                </tr>
                <tr>
                    <td><fmt:message key="test.localStoresInfo.output.cc.available"/></td>
                    <td><dsp:valueof param="storeInfo.clickCollectFlag"/></td>
                </tr>
                <tr>
                    <td><fmt:message key="test.localStoresInfo.output.display.discount"/></td>
                    <td><dsp:valueof param="storeInfo.displayDiscount"/></td>
                </tr>
                <tr>
                    <td><fmt:message key="test.localStoresInfo.output.discount.begin.date"/></td>
                    <td><fmt:formatDate value="${displayDiscountBeginDate}" pattern="dd/MM/yyyy HH:mm"/></td>
                </tr>
                <tr>
                    <td><fmt:message key="test.localStoresInfo.output.discount.end.date"/></td>
                    <td><fmt:formatDate value="${displayDiscountEndDate}" pattern="dd/MM/yyyy HH:mm"/></td>
                </tr>
            </table>
        </dsp:oparam>
        <dsp:oparam name="true">
            <p><fmt:message key="test.localStoresInfo.output.no.info"/></p>
        </dsp:oparam>
    </dsp:droplet>

</dsp:page>