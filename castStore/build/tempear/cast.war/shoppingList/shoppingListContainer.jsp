<dsp:page>  
  <cast:pageContainer>
    <jsp:attribute name="bottomBanners">true</jsp:attribute>
    <jsp:attribute name="bvInclude">view</jsp:attribute>
    <jsp:attribute name="bodyContent">

      <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
      <dsp:importbean bean="/com/castorama/droplet/StyleLookupDroplet"/>
      <dsp:importbean bean="/com/castorama/droplet/IsRobotDroplet"/>
      <dsp:importbean bean="/com/castorama/droplet/CastShoppingListDroplet"/>
      <dsp:importbean bean="/atg/userprofiling/SessionBean"/>
      <dsp:importbean bean="/atg/userprofiling/Profile"/>

      <dsp:getvalueof var="requestURI" bean="/OriginatingRequest.requestURI"/>
      <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
      <dsp:getvalueof var="lastVisitedPage" bean="SessionBean.values.lastVisitedPage"/>
      <c:if test="${empty lastVisitedPage}">
        <c:set var="lastVisitedPage" value="${contexPath}"/>
      </c:if>
      <dsp:importbean bean="/atg/dynamo/service/CurrentDate"/>
      
      <div style="margin: 50px auto 50px;width:620px">
      <div class="lsShadow">
      <table cellpadding="0" cellspacing="0" class="slTable shadow">
      <thead>
	      <tr>
	        <td colspan="4">
	          <div class="slHeader"></div>
	        </td>
	      </tr>
      
	      <tr class="slHeaderBottom">
	        <td colspan="2" class="slCurrentTime">
	          <dsp:valueof bean="CurrentDate.timeAsDate" date="dd/MM/yyyy-HH:mm"/>
	        </td>
	        <td colspan="2" class="slPrintButtonCell">
	          <div class="slPrintButton clickable" onclick="window.open('${contextPath}/shoppingList/printShoppingListInfo.jsp')">&nbsp;</div>
	        </td>
	      </tr>
	  </thead>
	  <tfoot>
		  <tr class="slFooter">
	        <td colspan="4" class="slFooterCell">
	          <div class="slPrintButton clickable" onclick="window.open('${contextPath}/shoppingList/printShoppingListInfo.jsp')">&nbsp;</div>
	        </td>
	      </tr>
      </tfoot>

          <dsp:droplet name="/com/castorama/droplet/MultiStockVisAvailabilityDroplet">
		    <dsp:param name="skus" bean="Profile.shoppingList" />
		    <dsp:param name="store" bean="Profile.currentLocalStore" />
		    <dsp:oparam name="output">
		      <c:choose>
		      <c:when test="${empty svAvailableMap}">
		        <dsp:getvalueof var="svAvailableMap" param="svAvailableMap" scope="request"/>
		      </c:when>
		      <c:otherwise>
		        <dsp:getvalueof var="addToSvAvailableMap" param="svAvailableMap" />
		        ${castCollection:putAll(svAvailableMap, addToSvAvailableMap)}
		      </c:otherwise>
		      </c:choose>
		    </dsp:oparam>
		  </dsp:droplet>

        <dsp:droplet name="CastShoppingListDroplet">
          <dsp:param name="store" bean="Profile.currentLocalStore" />
          <dsp:param name="svAvailableMap" value="${svAvailableMap}" />
          <dsp:oparam name="output">
            <dsp:getvalueof var="amount" param="amount"/>
                
            <input id="producsAmountInShoppingList" type="hidden" value="${amount}"/>
            <c:if test="${amount == 0}">
              <script type="text/javascript">
                $(document).ready(function() {
                    location.href="${contextPath}${templateUrl}";
                });
              </script>
            </c:if>
                
            <dsp:droplet name="ForEach">
              <dsp:param name="array" param="productsList"/>
              <dsp:param name="elementName" value="sku"/>
              <dsp:oparam name="output">
              	<dsp:getvalueof var="index" param="index"/>
                <dsp:getvalueof var="set" param="sku.parentProducts"/>
                <dsp:getvalueof var="skuId" param="sku.id"/>
                <dsp:param name="parentProductsList" value="${castCollection:list(set)}"/>
            	<dsp:param name="product" param="parentProductsList[0]"/>
                
                <dsp:droplet name="/com/castorama/droplet/CastProductLinkDroplet">
                  <dsp:param name="productId" param="product.id"/>
                  <dsp:param name="navAction" value="jump"/>
                  <dsp:oparam name="output">
                    <dsp:getvalueof var="templateUrl" param="url"/>
                    <dsp:getvalueof var="templateUrl" value="${templateUrl}&amp;skuId=${skuId}"/>
                  </dsp:oparam>
                </dsp:droplet>
                
                    <tr class="slItem" id="row_${index}">
                      <dsp:getvalueof var="imageUrl" param="sku.thumbnailImage.url"/>
                      <dsp:getvalueof var="skuId" param="sku.id"/>
                      <dsp:getvalueof var="refNumber" param="sku.codeArticle"/>
                      <dsp:getvalueof var="productId" param="product.id"/>
                      <dsp:getvalueof var="description" param="sku.LibelleDescriptifArticle"/>
                      <td class="slPictureCell">
                         <c:choose>
                          <c:when test="${not empty imageUrl}">
                            <img src="${imageUrl}" class="slItemPicture clickable" onclick="location.href='${contextPath}${templateUrl}'"/>
                          </c:when>
                          <c:otherwise>
                            <img src="/default_images/d_no_img.jpg" class="slItemPicture clickable" onclick="location.href='${contextPath}${templateUrl}'"/>
                          </c:otherwise>
                         </c:choose>
                      </td>
                      <td class="slDescrCell">
                        <span class="slDescription" onclick="location.href='${contextPath}${templateUrl}'">${description}</span>
                        <div class="refNum"><fmt:message key="msg.cart.ref" />&nbsp; <c:out value="${refNumber}" /></div>
                      </td>
                      <td class="slTextRemove">
                          <dsp:droplet name="/com/castorama/droplet/CastProductLinkDroplet">
                            <dsp:param name="productId" value="${productId}"/>
                            <dsp:param name="navAction" value="jump"/>
                            <dsp:oparam name="output">
                              <dsp:getvalueof var="templateUrl" param="url"/>
                            </dsp:oparam>
                          </dsp:droplet>

                            <input type="hidden" id="productUrl" value="${contextPath}${templateUrl}"/>
                            <span id="del_${skuId}_${index}" class="clickable"><fmt:message key="shoppinglist.deletefromlist"/></span>
                            </td>
                            <td class="slItemRemove">
                            <div><input type="submit" class="icoTrash" id="pic_${skuId}_${index}" value="Remove"/></div>
                            <script type="text/javascript" language="javascript">
								$("#pic_${skuId}_${index}, #del_${skuId}_${index}").click(function(){
		                            $.ajax ({
		                                url: "${contextPath}" + "/shoppingList/shoppingListManager.jsp",
		                                data: "action=remove&skuIds=${skuId}&index=${index}",
		                                cache: false,
		                                async: false,
		                                beforeSend: function(xht) {
		                                    $("#pic_${skuId}_${index}").removeClass("icoTrash");
		                                    $("#pic_${skuId}_${index}").addClass("deletingInProgress");
		                                },
		                                success: function(data, textStatus, jqXHR) {
		                                	data = data.replace(/^\s*/, "").replace(/\s*$/, "");
		                                	if (data.length > 0) {
		                                		return;
		                                	}
		                                    decreaseProductsInShoppinList();
		                                    if ($("#producsAmountInShoppingList").val() == '0') {
		                                        window.location.href = "${lastVisitedPage}";
		                                        return;
		                                    }
		                                    var $tableRow = $("#row_" + "${index}");
		                                    $tableRow.remove();
		                                },
		                                complete: function(jqXHR, textStatus) {
		                                    $("#pic_${skuId}_${index}").removeClass("deletingInProgress");
		                                    $("#pic_${skuId}_${index}").addClass("icoTrash");
		                                }
		                            });
								});
                            </script>                            
                      </td>
                    </tr>
              </dsp:oparam>
            </dsp:droplet>
          </dsp:oparam>
        </dsp:droplet>
      </table>
      </div>
    </div>
   </jsp:attribute>

  </cast:pageContainer>
  
</dsp:page>
