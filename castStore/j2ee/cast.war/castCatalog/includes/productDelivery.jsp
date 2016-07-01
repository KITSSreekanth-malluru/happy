<dsp:page>

  <dsp:importbean bean="/com/castorama/droplet/CastShippingDroplet"/>
  <dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>
  
  <dsp:getvalueof var="skusIds" param="skusIds"/>
  <c:choose>
   <c:when test="${not empty skusIds && skusIds != ''}">
     <dsp:droplet name="/atg/dynamo/droplet/ForEach">
        <dsp:param name="array" value="${fn:split(skusIds, ',')}"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="skuId" param="element"/>
          <dsp:getvalueof var="skuId" value="${fn:trim(skuId)}" />
          <c:if test="${not empty skuId && skuId != ''}">
	          <dsp:droplet name="CastShippingDroplet">
			    <dsp:param name="skuId" value="${skuId}"/>
			    <dsp:param name="quantity" value="1"/>
			      <dsp:oparam name="output">
			        <dsp:droplet name="IsNull">
			          <dsp:param name="value" param="expeditionPNS"/>
			          <dsp:oparam name="false">
			            ${skuId}:<dsp:valueof param="expeditionPNS.deliveryTime" valueishtml="true"/>,
			          </dsp:oparam>
			        </dsp:droplet>
			      </dsp:oparam>
			   </dsp:droplet>
		  </c:if>
        </dsp:oparam>
     </dsp:droplet>
   </c:when>
   <c:otherwise>
     <dsp:getvalueof var="skuId" param="skuId"/>

	  <dsp:droplet name="CastShippingDroplet">
	    <dsp:param name="skuId" value="${skuId}"/>
	    <dsp:param name="quantity" param="quantity"/>
	      <dsp:oparam name="output">
	        <dsp:droplet name="IsNull">
	          <dsp:param name="value" param="expeditionPNS"/>
	          <dsp:oparam name="false">
	            <dsp:valueof param="expeditionPNS.deliveryTime" valueishtml="true"/>
	          </dsp:oparam>
	        </dsp:droplet>
	      </dsp:oparam>
	   </dsp:droplet>
   </c:otherwise>
  </c:choose>  
</dsp:page>
