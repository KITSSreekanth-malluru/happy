<%--
 PAYBOX ( OK CALLBACK )
 --%>
<dsp:page>
    <dsp:importbean bean="/castorama/CastoOrderEditor"/>
    <dsp:importbean bean="/atg/userprofiling/Profile"/>
    <dsp:importbean bean="/castorama/payment/PayboxParametersConfiguration"/>

    <dsp:droplet name="/atg/dynamo/droplet/Switch">
        <dsp:param name="value" bean="PayboxParametersConfiguration.accessibleForPaybox"/>
        <dsp:oparam name="false">
            <%@ include file="/html/call_center/order/payment/paybox-payment-verification.jspf" %>
        </dsp:oparam>
    </dsp:droplet>
    <dsp:droplet name="/castorama/droplet/PayboxReplyCallCenterDroplet">
        <dsp:param name="orderId" bean="CastoOrderEditor.order.id"/>
        <dsp:param name="callback" value="callback"/>
    </dsp:droplet>

</dsp:page>