<%--
 PAYBOX ( REFUSE CALLBACK )
 --%>
<dsp:page>
    <dsp:droplet name="/castorama/droplet/PayboxReplyCallCenterDroplet">
        <dsp:param name="orderId" bean="/castorama/CastoOrderEditor.order.id"/>
        <dsp:param name="callback" value="refuse"/>
        <dsp:oparam name="output">
        </dsp:oparam>
    </dsp:droplet>
</dsp:page>