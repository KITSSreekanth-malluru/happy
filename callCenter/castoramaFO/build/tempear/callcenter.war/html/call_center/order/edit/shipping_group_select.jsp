<%@ taglib prefix="c" uri="/core"%>
<%@ taglib prefix="fmt" uri="/fmt"%>
<%@ taglib prefix="dsp" uri="/dspTaglib"%>
<%@ taglib prefix="cordsp" uri="/coreDSP"%>

<dsp:page xml="true">

<%-- 
------------------------------------------------------------------------------------------ 
IMPORTS-----------------------------------------------------------------------------------
------------------------------------------------------------------------------------------ 
--%>

<dsp:droplet name="/atg/dynamo/droplet/ForEach">
   <dsp:param name="array" param="iterate_over"/>
   <dsp:param name="elementName" value="sGroup"/>
   <dsp:oparam name="empty">No Groups In Order</dsp:oparam>
   <dsp:oparam name="output">
        <dsp:droplet name="/atg/dynamo/droplet/Switch">
           <dsp:param name="value" param="sGroup.id"/>
           <dsp:oparam param="selected.id">
               <dsp:option paramvalue="sGroup.id" selected="true"><dsp:valueof param="sGroup.id"/>: [<dsp:valueof param="sGroup.shippingMethod"/>] <dsp:valueof param="sGroup.shippingAddress.firstName"/> <dsp:valueof param="sGroup.shippingAddress.lastName"/></dsp:valueof>
           </dsp:oparam>
           <dsp:oparam name="default">
             <dsp:droplet name="/castorama/order/CastoShippingGroupIsModifiable">
               <dsp:param name="id" param="sGroup.id"/>
               <dsp:oparam name="true">
                 <dsp:option paramvalue="sGroup.id"><dsp:valueof param="sGroup.id"/>: [<dsp:valueof param="sGroup.shippingMethod"/>] <dsp:valueof param="sGroup.shippingAddress.firstName"/> <valueof param="sGroup.shippingAddress.lastName"/> 
               </dsp:oparam>
             </dsp:droplet>
           </dsp:oparam>
        </dsp:droplet>  <!-- /Switch -->           
    </dsp:oparam>
</dsp:droplet> <!-- /ForEach -->


</dsp:page>