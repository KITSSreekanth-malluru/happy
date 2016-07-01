<dsp:page>
	<dsp:getvalueof var="activeCardCastoramaVar" bean="/com/castorama/CastConfiguration.activeCardCastorama" />
	
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	
	<dsp:getvalueof var="numberOfHundredsForFirstOrderVar" bean="CastShoppingCartFormHandler.numberOfHundreds" />
	<dsp:getvalueof var="savedAmountForFirstOrderVar" bean="CastShoppingCartFormHandler.savedAmount" />
	<dsp:setvalue param="numberOfHundredsForFirstOrder" value="${numberOfHundredsForFirstOrderVar}" />
	<dsp:setvalue param="savedAmountForFirstOrder" value="${savedAmountForFirstOrderVar}" />
	
	<c:choose>
    	<c:when test="${savedAmountForFirstOrderVar >= 120}">
    		<dsp:getvalueof var="ruleForFirstOrder" value="msg.cart.latout.cart.rule.2"/>
    	</c:when>
    	<c:otherwise><dsp:getvalueof var="ruleForFirstOrder" value="msg.cart.latout.cart.rule.1"/></c:otherwise>
    </c:choose>
  
    <c:if test="${storeIsCC}">    
    	<dsp:getvalueof var="numberOfHundredsForSecondOrderVar" bean="CastShoppingCartFormHandler.localNumberOfHundreds" />
    	<dsp:getvalueof var="savedAmountForSecondOrderVar" bean="CastShoppingCartFormHandler.localSavedAmount" />
    	<dsp:setvalue param="numberOfHundredsForSecondOrder" value="${numberOfHundredsForSecondOrderVar}" />
    	<dsp:setvalue param="savedAmountForSecondOrder" value="${savedAmountForSecondOrderVar}" />
    	
    	<c:choose>
    		<c:when test="${savedAmountForSecondOrderVar >= 120}">
    			<dsp:getvalueof var="ruleForSecondOrder" value="msg.cart.latout.cart.rule.2"/>
    		</c:when>
    		<c:otherwise><dsp:getvalueof var="ruleForSecondOrder" value="msg.cart.latout.cart.rule.1"/></c:otherwise>
    	</c:choose>
    </c:if>
    
    <tbody class="boxCartTableFooter">
    
    	<tr class="bgWhite">
    		<td class="produits">
    			<c:if test="${mode != 'confirmation'}">
    				<%@ include file="claim_coupon.jsp" %>
    			</c:if>
    		</td>
    		
    		<c:choose>
    			<c:when test="${activeCardCastoramaVar && (savedAmountForFirstOrderVar > 0) && !storeIsCC}" >
    				<td colspan="2">
    					<c:if test="${mode != 'confirmation'}">
    						<div class="castCartNew">
    							<p class="textAligne"><strong><fmt:message key="${ruleForFirstOrder}">
    							<fmt:param><dsp:valueof param="numberOfHundredsForFirstOrder" /></fmt:param>
    							<fmt:param><dsp:valueof param="savedAmountForFirstOrder" /></fmt:param>
    							</fmt:message></strong>
    							<br/><span id="atoutAdvanteges"><dsp:a href="${contextPath}/user/atoutAdvanteges.jsp" target="_blank"><fmt:message key="msg.cart.atout.advantages"/></dsp:a></span></p>
    						</div>
    					</c:if>
    				</td>
    			</c:when>
    			<c:when test="${activeCardCastoramaVar && storeIsCC && ((savedAmountForFirstOrderVar > 0) || (savedAmountForSecondOrderVar > 0))}" >
    				<c:choose>
    					<c:when test="${savedAmountForFirstOrderVar == 0}" >
    						<c:if test="${mode != 'confirmation'}">  						
    							<td class="ccTitle ${leftClass}" />
    							<td class="ccTitle ${rightClass}">
    								<div class="castCartNew">
    									<p class="textAligne"><strong><fmt:message key="${ruleForSecondOrder}">
    									<fmt:param><dsp:valueof param="numberOfHundredsForSecondOrder" /></fmt:param>
    									<fmt:param><dsp:valueof param="savedAmountForSecondOrder" /></fmt:param>
    									</fmt:message></strong>
    									<span id="atoutAdvanteges"><dsp:a href="${contextPath}/user/atoutAdvanteges.jsp" target="_blank"><fmt:message key="msg.cart.atout.advantages"/></dsp:a></span></p>   							
    								</div>
    							</td>
    						</c:if>
    					</c:when>
    					<c:when test="${savedAmountForSecondOrderVar == 0}" >
    						<c:if test="${mode != 'confirmation'}">
    							<td class="ccTitle ${leftClass}">
    								<div class="castCartNew">
    									<p class="textAligne"><strong><fmt:message key="${ruleForFirstOrder}">
    									<fmt:param><dsp:valueof param="numberOfHundredsForFirstOrder" /></fmt:param>
    									<fmt:param><dsp:valueof param="savedAmountForFirstOrder" /></fmt:param>
    									</fmt:message></strong>
    									<span id="atoutAdvanteges"><dsp:a href="${contextPath}/user/atoutAdvanteges.jsp" target="_blank"><fmt:message key="msg.cart.atout.advantages"/></dsp:a></span></p>   							
    								</div>
    							</td>
    							<td class="ccTitle ${rightClass}" />
    						</c:if>
    					</c:when>
    					<c:otherwise>
    						<c:if test="${mode != 'confirmation'}">
    							<td class="ccTitle ${leftClass}">
    								<div class="castCartNew">
    									<p class="textAligne"><strong><fmt:message key="${ruleForFirstOrder}">
    									<fmt:param><dsp:valueof param="numberOfHundredsForFirstOrder" /></fmt:param>
    									<fmt:param><dsp:valueof param="savedAmountForFirstOrder" /></fmt:param>
    									</fmt:message></strong>
    									<span id="atoutAdvanteges"><dsp:a href="${contextPath}/user/atoutAdvanteges.jsp" target="_blank"><fmt:message key="msg.cart.atout.advantages"/></dsp:a></span></p>   							
    								</div>
    							</td>
    							<td class="ccTitle ${rightClass}">
    								<div class="castCartNew">
    									<p class="textAligne"><strong><fmt:message key="${ruleForSecondOrder}">
    									<fmt:param><dsp:valueof param="numberOfHundredsForSecondOrder" /></fmt:param>
    									<fmt:param><dsp:valueof param="savedAmountForSecondOrder" /></fmt:param>
    									</fmt:message></strong>
    									<span id="atoutAdvanteges"><dsp:a href="${contextPath}/user/atoutAdvanteges.jsp" target="_blank"><fmt:message key="msg.cart.atout.advantages"/></dsp:a></span></p>   							
    								</div>
    							</td>
    						</c:if>
    					</c:otherwise>
    				</c:choose>    	
    			</c:when>
    			<c:otherwise>
    				<td colspan="2" class="cardCastoramaHTMLBlock">
    					<c:if test="${mode != 'confirmation'}">
    						<dsp:droplet name="/atg/dynamo/droplet/RQLQueryRange" >
    							<dsp:param name="itemDescriptor" value="fastLabConfigs" />
    							<dsp:param name="repository" bean="/atg/commerce/catalog/ProductCatalog" />
    							<dsp:param name="queryRQL" value="ALL" />
    							<dsp:param name="howMany" value="1" />
    							<dsp:param name="sortProperties" value="" />
    							<dsp:oparam name="output">
    								<dsp:getvalueof var="fastLabConfigs" param="element" />
    							</dsp:oparam>
    						</dsp:droplet>
    						<dsp:setvalue param="fastLabConfigs" value="${fastLabConfigs}" />
    						<dsp:getvalueof var="staticContentPath" bean="/com/castorama/CastConfiguration.staticContentPath" />
    						<dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
    							<dsp:param name="value" param="fastLabConfigs.castoramaCard" />
    							<dsp:oparam name="false">
    								<dsp:getvalueof var="castoramaCardHtml" param="fastLabConfigs.castoramaCard.htmlUrl" />
    								<c:if test="${not empty castoramaCardHtml}">
    									<c:import charEncoding="utf-8" url="${staticContentPath}${castoramaCardHtml}"/>
    								</c:if>
    							</dsp:oparam>
    						</dsp:droplet>
    					</c:if>
    				</td>
    			</c:otherwise>
			</c:choose>
		</tr>
	</tbody>
</dsp:page>
