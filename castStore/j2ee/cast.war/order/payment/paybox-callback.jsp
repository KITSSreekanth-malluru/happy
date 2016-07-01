<%--
 PAYBOX ( OK CALLBACK )
 --%>
<dsp:page>

    <dsp:importbean bean="/com/castorama/payment/PayboxParametersConfiguration"/>

    <dsp:droplet name="/atg/dynamo/droplet/Switch">
        <dsp:param name="value" bean="PayboxParametersConfiguration.accessibleForPaybox"/>
        <dsp:oparam name="false">
            <%@ include file="/order/payment/paybox-payment-verification.jspf" %>
        </dsp:oparam>
    </dsp:droplet>
    <dsp:droplet name="/com/castorama/droplet/PayboxReplyCommerceDroplet">
        <dsp:param name="shoppingCart" bean="/atg/commerce/ShoppingCart"/>
        <dsp:param name="callback" value="callback"/>
    </dsp:droplet>
</dsp:page>