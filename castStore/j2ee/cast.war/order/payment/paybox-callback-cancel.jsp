<%--
 PAYBOX ( CANCEL CALLBACK )
 --%>
<dsp:page>
    <dsp:droplet name="/com/castorama/droplet/PayboxReplyCommerceDroplet">
        <dsp:param name="shoppingCart" bean="/atg/commerce/ShoppingCart"/>
        <dsp:param name="callback" value="cancel"/>
        <dsp:oparam name="output">
        </dsp:oparam>
    </dsp:droplet>
</dsp:page>