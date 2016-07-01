<dsp:page>


    <dsp:importbean var="originatingRequest" bean="/OriginatingRequest"/>
    <dsp:importbean bean="/castorama/payment/PayboxParametersConfiguration"/>

    <dsp:getvalueof var="PBX_TOTAL" param="PBX_TOTAL"/>
    <dsp:getvalueof var="PBX_CMD" param="PBX_CMD"/>
    <dsp:getvalueof var="PBX_TYPECARTE" param="PBX_TYPECARTE"/>
    <dsp:getvalueof var="PBX_PORTEUR" param="PBX_PORTEUR"/>
    <dsp:getvalueof var="PBX_TYPEPAIEMENT" param="PBX_TYPEPAIEMENT"/>
    <dsp:getvalueof var="host" bean="/com/castorama/util/ServerSetting.secureHost"/>
    <c:set var="shost" value="${host}${originatingRequest.contextPath}"/>
    <dsp:getvalueof var="sid" value="${castCollection:encode(pageContext.session.id)}"/>
    <dsp:getvalueof var="PBX_SITE" bean="PayboxParametersConfiguration.pbx_site"/>
    <dsp:getvalueof var="PBX_IDENTIFIANT" bean="PayboxParametersConfiguration.pbx_identifiant"/>

    <dsp:droplet name="/com/castorama/droplet/CastLookupDroplet">
        <dsp:param name="elementName" value="order"/>
        <dsp:param name="repository" bean="/atg/commerce/order/OrderRepository"/>
        <dsp:param name="itemDescriptor" value="order"/>
        <dsp:param name="id" value="${PBX_CMD}"/>
        <dsp:oparam name="output">
            <dsp:getvalueof var="deliveryType" param="order.deliveryType"/>
            <dsp:getvalueof var="magasinId" param="order.magasinId"/>
        </dsp:oparam>
    </dsp:droplet>

    <c:choose>
        <c:when test="${deliveryType == 'clickAndCollect'}">
            <dsp:droplet name="/com/castorama/droplet/CastLookupDroplet">
                <dsp:param name="elementName" value="orderStore"/>
                <dsp:param name="repository" bean="/atg/registry/Repository/MagasinGSARepository"/>
                <dsp:param name="itemDescriptor" value="magasin"/>
                <dsp:param name="id" value="${magasinId}"/>
                <dsp:oparam name="output">
                    <dsp:getvalueof var="pbxSite" param="orderStore.pbxSite"/>
                    <dsp:getvalueof var="pbxRang" param="orderStore.pbxRang"/>
                    <dsp:getvalueof var="pbxIdentifiant" param="orderStore.pbxIdentifiant"/>
                    <dsp:getvalueof var="pbxSecretKey" param="orderStore.pbxSecretKey"/>
                    <dsp:getvalueof var="pbxSiteSofinco" param="orderStore.pbxSiteSofinco"/>
                    <dsp:getvalueof var="pbxRangSofinco" param="orderStore.pbxRangSofinco"/>
                    <dsp:getvalueof var="pbxIdentifiantSofinco" param="orderStore.pbxIdentifiantSofinco"/>
                    <dsp:getvalueof var="pbxSecretKeySofinco" param="orderStore.pbxSecretKeySofinco"/>
                </dsp:oparam>
            </dsp:droplet>
            <c:choose>
                <c:when test="${'SOFINCO' == PBX_TYPECARTE}">
                    <c:if test="${not empty pbxSiteSofinco && pbxSiteSofinco != ''}">
                        <c:set var="PBX_SITE" value="${pbxSiteSofinco}"/>
                    </c:if>
                    <c:if test="${not empty pbxRangSofinco && pbxRangSofinco != ''}">
                        <c:set var="PBX_RANG" value="${pbxRangSofinco}"/>
                    </c:if>
                    <c:if test="${not empty pbxIdentifiantSofinco && pbxIdentifiantSofinco != ''}">
                        <c:set var="PBX_IDENTIFIANT" value="${pbxIdentifiantSofinco}"/>
                    </c:if>
                    <c:if test="${not empty pbxSecretKeySofinco && pbxSecretKeySofinco != ''}">
                        <c:set var="PBX_SECRET_KEY" value="${pbxSecretKeySofinco}"/>
                    </c:if>
                </c:when>
                <c:otherwise>
                    <c:set var="PBX_SITE" value="${pbxSite}"/>
                    <c:set var="PBX_IDENTIFIANT" value="${pbxIdentifiant}"/>
                    <c:set var="PBX_RANG" value="${pbxRang}"/>
                    <c:set var="PBX_SECRET_KEY" value="${pbxSecretKey}"/>
                </c:otherwise>
            </c:choose>
        </c:when>
        <c:otherwise>
            <c:choose>
                <c:when test="${'SOFINCO' == PBX_TYPECARTE}">
                    <dsp:getvalueof var="PBX_RANG" bean="PaymentConfiguration.sofincoRang"/>
                    <dsp:getvalueof var="PBX_SECRET_KEY" bean="PaymentConfiguration.sofincoSecretKey"/>
                </c:when>
                <c:otherwise>
                    <dsp:getvalueof var="PBX_RANG" bean="PayboxParametersConfiguration.pbx_rang"/>
                    <dsp:getvalueof var="PBX_SECRET_KEY" bean="PayboxParametersConfiguration.pbx_secret_key"/>
                </c:otherwise>
            </c:choose>
        </c:otherwise>
    </c:choose>

    <dsp:getvalueof var="PBX_ANNULE" bean="PayboxParametersConfiguration.pbx_annule"/>
    <dsp:getvalueof var="PBX_EFFECTUE" bean="PayboxParametersConfiguration.pbx_effectue"/>
    <dsp:getvalueof var="PBX_REFUSE" bean="PayboxParametersConfiguration.pbx_refuse"/>
    <dsp:getvalueof var="PBX_RETOUR" bean="PayboxParametersConfiguration.pbx_retour"/>
    <dsp:getvalueof var="PBX_REPONDRE_A" bean="PayboxParametersConfiguration.pbx_repondre_a"/>
    <dsp:getvalueof var="PBX_DEVICE" bean="PayboxParametersConfiguration.pbx_devise"/>
    <dsp:getvalueof var="PBX_NBCARTESKDO" bean="PayboxParametersConfiguration.pbx_nbcarteskdo"/>
    <dsp:getvalueof var="PBX_LANGUE" bean="PayboxParametersConfiguration.pbx_langue"/>

    <c:if test="${not empty PBX_SECRET_KEY}">
        <dsp:droplet name="/com/castorama/droplet/GenerateHmacDroplet">
            <dsp:param name="PBX_SECRET_KEY" value="${PBX_SECRET_KEY}"/>
            <dsp:param name="PBX_SITE" value="${PBX_SITE}"/>
            <dsp:param name="PBX_RANG" value="${PBX_RANG}"/>
            <dsp:param name="PBX_TOTAL" value="${PBX_TOTAL}"/>
            <dsp:param name="PBX_DEVICE" value="${PBX_DEVICE}"/>
            <dsp:param name="PBX_CMD" value="${PBX_CMD}"/>
            <dsp:param name="PBX_PORTEUR" value="${PBX_PORTEUR}"/>
            <dsp:param name="PBX_ANNULE" value="${PBX_ANNULE}"/>
            <dsp:param name="PBX_EFFECTUE" value="${PBX_EFFECTUE}"/>
            <dsp:param name="PBX_REFUSE" value="${PBX_REFUSE}"/>
            <dsp:param name="PBX_RETOUR" value="${PBX_RETOUR}"/>
            <dsp:param name="PBX_REPONDRE_A" value="${PBX_REPONDRE_A}"/>
            <dsp:param name="PBX_TYPEPAIMENT" value="${PBX_TYPEPAIEMENT}"/>
            <dsp:param name="PBX_TYPECARTE" value="${PBX_TYPECARTE}"/>
            <dsp:param name="PBX_LANGUE" value="${PBX_LANGUE}"/>
            <dsp:param name="PBX_IDENTIFIANT" value="${PBX_IDENTIFIANT}"/>
            <dsp:param name="PBX_NBCARTESKDO" value="${PBX_NBCARTESKDO}"/>
            <dsp:param name="PBX_CODEFAMILLE" value="${PBX_CODEFAMILLE}"/>
            <dsp:oparam name="output">
                <dsp:getvalueof var="PBX_HMAC" param="PBX_HMAC"/>
                <dsp:getvalueof var="PBX_TIME" param="PBX_TIME"/>
                <dsp:getvalueof var="PBX_HASH" param="PBX_HASH"/>
            </dsp:oparam>
        </dsp:droplet>
    </c:if>

    <dsp:getvalueof var="mode" bean="PayboxParametersConfiguration.testMode"/>
    <c:choose>
        <c:when test="${ mode != 'true' }">
            <dsp:getvalueof var="action" bean="PayboxParametersConfiguration.pbx_paybox"/>
        </c:when>
        <c:otherwise>
            <dsp:getvalueof var="action" bean="PayboxParametersConfiguration.pbx_paybox_test"/>
        </c:otherwise>
    </c:choose>

    <dsp:droplet name="/com/castorama/droplet/PayboxHandledDroplet">
        <dsp:param name="orderId" value="${PBX_CMD}"/>
        <dsp:param name="orderTotal" value="${PBX_TOTAL}"/>
        <dsp:param name="userId" bean="/atg/userprofiling/Profile.repositoryId"/>
        <dsp:param name="source" value="CALL-CENTER"/>
        <dsp:oparam name="output">
            <BODY onload="document.PAYBOX.submit();">
            <form name="PAYBOX" id="PAYBOX" action="${action} " method="post">
                <input type="hidden" name="PBX_SITE" id="PBX_SITE" value="${PBX_SITE}"/>
                <input type="hidden" name="PBX_RANG" id="PBX_RANG" value="${PBX_RANG}"/>
                <input type="hidden" name="PBX_TOTAL" id="PBX_TOTAL" value="${PBX_TOTAL}"/>
                <input type="hidden" name="PBX_DEVISE" id="PBX_DEVICE" value="${PBX_DEVICE}"/>
                <input type="hidden" name="PBX_CMD" id="PBX_CMD" value="${PBX_CMD}"/>
                <input type="hidden" name="PBX_PORTEUR" id="PBX_PORTEUR" value="${PBX_PORTEUR}"/>
                <input type="hidden" name="PBX_ANNULE" id="PBX_ANNULE" value="${PBX_ANNULE}"/>
                <input type="hidden" name="PBX_EFFECTUE" id="PBX_EFFECTUE" value="${PBX_EFFECTUE}"/>
                <input type="hidden" name="PBX_REFUSE" id="PBX_REFUSE" value="${PBX_REFUSE}"/>
                <input type="hidden" name="PBX_RETOUR" id="PBX_RETOUR" value="${PBX_RETOUR}"/>
                <input type="hidden" name="PBX_REPONDRE_A" id="PBX_REPONDRE_A" value="${PBX_REPONDRE_A}"/>
                <input type="hidden" name="PBX_TYPEPAIEMENT" id="PBX_TYPEPAIEMENT" value="${PBX_TYPEPAIEMENT}"/>
                <input type="hidden" name="PBX_TYPECARTE" id="PBX_TYPECARTE" value="${PBX_TYPECARTE}"/>
                <input type="hidden" name="PBX_LANGUE" id="PBX_LANGUE" value="${PBX_LANGUE}"/>
                <input type="hidden" name="PBX_IDENTIFIANT" id="PBX_IDENTIFIANT" value="${PBX_IDENTIFIANT}"/>
                <c:if test="${not empty PBX_NBCARTESKDO}">
                    <input type="hidden" name="PBX_NBCARTESKDO" id="PBX_NBCARTESKDO"
                           value="${PBX_NBCARTESKDO}"/>
                </c:if>
                <c:if test="${'SOFINCO' == PBX_TYPECARTE}">
                    <dsp:getvalueof var="PBX_CODEFAMILLE" param="PBX_CODEFAMILLE"/>
                    <input type="hidden" name="PBX_CODEFAMILLE" id="PBX_CODEFAMILLE"
                           value="${PBX_CODEFAMILLE}"/>
                </c:if>
                <input type="hidden" name="PBX_HASH" id="PBX_HASH" value="${PBX_HASH}"/>
                <input type="hidden" name="PBX_TIME" id="PBX_TIME" value="${PBX_TIME}"/>
                <input type="hidden" name="PBX_HMAC" id="PBX_HMAC" value="${PBX_HMAC}"/>
            </form>
            </BODY>
        </dsp:oparam>
        <dsp:oparam name="empty">
            <dsp:droplet name="/atg/dynamo/droplet/Redirect">
                <dsp:param name="url" value="${shost}/html/call_center/order/edit/order.jsp"/>
            </dsp:droplet>
        </dsp:oparam>
    </dsp:droplet>


</dsp:page>