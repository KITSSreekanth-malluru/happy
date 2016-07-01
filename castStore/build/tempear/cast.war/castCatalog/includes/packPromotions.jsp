<dsp:page>
	<dsp:importbean bean="/com/castorama/droplet/CastGetApplicablePromotions"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
	
	<dsp:droplet name="CastGetApplicablePromotions">
	  <dsp:param name="item" param="sku"/>
	  <dsp:oparam name="output">
	      <dsp:droplet name="ForEach">
	        <dsp:param name="array" param="promotions"/>
	        <dsp:param name="elementName" value="promotion"/>
	        <dsp:oparam name="output">
	          <dsp:getvalueof var="promoName" param="promotion.displayName"/>
	          <dsp:getvalueof var="description" param="promotion.description"/>
	          <c:if test="${not empty description}">
	            <dsp:droplet name="ForEach">
	              <dsp:param name="array" param="promotion.media"/>
	              <dsp:param name="elementName" value="media"/>
	              <dsp:oparam name="output">
	                <dsp:getvalueof var="mediaUrl" param="media.url"/>
	                <dsp:getvalueof var="emptyMedia" value="false"/>
	              </dsp:oparam>
	              <dsp:oparam name="empty">
	                <dsp:getvalueof var="emptyMedia" value="true"/>
	              </dsp:oparam>
	            </dsp:droplet>
	            
	            <tr>
					<td colspan="5" class="offers">
						<div class="grayCorner grayCornerGray preMessageLayer">
			       		  <div class="grayBlockBackground"><!--~--></div>
			       		  <div class="cornerBorder cornerTopLeft"><!--~--></div>
                  		  <div class="cornerBorder cornerTopRight"><!--~--></div>
                  		  <div class="cornerBorder cornerBottomLeft"><!--~--></div>
                   		  <div class="cornerBorder cornerBottomRight"><!--~--></div>
			 			  <div class="preMessage">
							<c:choose>
						        <c:when test="${fn:startsWith(promoName, 'VenteFlash')==true}">
						        	<img src="${contextPath}/images/icoTimer.gif" class="icoChopCart" />
						        </c:when>
						        <c:when test="${emptyMedia}">
							      <img src="${contextPath}/images/icoPresent.gif" class="icoChopCart" />
							    </c:when>
							    <c:otherwise>
							      <img src="${mediaUrl}" class="icoChopCart" />
							    </c:otherwise>
						    </c:choose>
			                <table cellpadding="0" cellspacing="0" class="emilateValignCenter">
			                   <tr>
			                     <td>
			                       <strong><dsp:valueof value="${description}" valueishtml="true"/></strong><br />
			                     </td>
			                   </tr>
			                </table>
			             </div>
			           </div>
			         </td>
			     </tr>
	          </c:if>  
	        </dsp:oparam>
	      </dsp:droplet>
	    </div>  
	  </dsp:oparam>
	</dsp:droplet>
</dsp:page>
