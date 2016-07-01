<dsp:page xml="true">
    <dsp:importbean bean="/castorama/CastoOrderEditor" />
    <dsp:getvalueof var="orderId" bean="CastoOrderEditor.order.id"/>
    <dsp:droplet name="/atg/dynamo/droplet/Redirect">
        <dsp:param name="url" value="../show/order.jsp?id=${orderId}"/>
    </dsp:droplet>
</dsp:page>
