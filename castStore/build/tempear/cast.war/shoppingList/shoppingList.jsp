  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/atg/userprofiling/SessionBean"/>
  
  <dsp:getvalueof var="requestUri" bean="/OriginatingRequest.requestURI"/>
  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
  <input type="hidden" id="contextPath" value="${contextPath}"/>
  
  <dsp:importbean bean="/com/castorama/droplet/CanonicalLinkDroplet"/>
  <dsp:droplet name="CanonicalLinkDroplet">
    <dsp:param name="type" param="currentPage"/>
    <dsp:oparam name="output">
      <dsp:getvalueof var="canonicalUrl" param="url"/>
    </dsp:oparam>
  </dsp:droplet>
  
  <c:choose>
    <c:when test="${not empty canonicalUrl and canonicalUrl != 'null'}">
      <dsp:setvalue bean="SessionBean.values.lastVisitedPage" value="${canonicalUrl}"/>
    </c:when>
    <c:when test="${not empty requestUri}">
      <dsp:setvalue bean="SessionBean.values.lastVisitedPage" value="${requestUri}"/>
    </c:when>
    <c:otherwise>
      <dsp:setvalue bean="SessionBean.values.lastVisitedPage" value="${contextPath}"/>
    </c:otherwise>
  </c:choose>
  
  <dsp:getvalueof var="isNotLogin" bean="Profile.transient"/>
  
  <table cellpadding="0" cellspacing="0" width="100%" >
  <c:choose>
    <c:when test="${isNotLogin}">
      <tr>
        <td style="height:65px; text-align:center; padding: 0px 40px; color:#7b7b7b; font-size:12px;font-weight:bold">
          <fmt:message key="shoppinglist.notlogin"/>
        </td>
      </tr>
      <tr>
        <td style="height:34px; text-align:center; padding: 0px 80px 6px;">
          <c:choose>
            <c:when test="${not empty canonicalUrl and canonicalUrl != 'null'}">
              <dsp:setvalue bean="SessionBean.values.loginSuccessURL" value="${canonicalUrl}?fromMaListe=true"/>
            </c:when>
            <c:when test="${not empty requestUri}">
              <dsp:setvalue bean="SessionBean.values.loginSuccessURL" value="${requestUri}?fromMaListe=true"/>
            </c:when>
            <c:otherwise>
              <dsp:setvalue bean="SessionBean.values.loginSuccessURL" value="${contextPath}?fromMaListe=true"/>
            </c:otherwise>
          </c:choose>
              <img class="clickable" src="/images/ma_liste_login.png" onclick="location.href='${contextPath}/user/login.jsp'"/>
        </td>
      </tr>
    </c:when>
    <c:otherwise>
    
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
      <!-- shopping list items -->
      <!-- forEach over shopping list -->
      <dsp:droplet name="/com/castorama/droplet/CastShoppingListDroplet">
        <dsp:param name="store" bean="Profile.currentLocalStore" />
        <dsp:param name="svAvailableMap" value="${svAvailableMap}" />
        <dsp:oparam name="output">
          <dsp:getvalueof var="amount" param="amount"/>
          <input type="hidden" id="amount" value="${amount}"/>
          <tr class="sliderHeader">
            <td style="width:150px;">
              <!-- number of items in shopping list -->
              <span id="producsAmountInShoppingList">
                ${amount}</span>                
                <c:choose>
                  <c:when test="${amount == 0 || amount > 1}">
                    <span id="productsText"><fmt:message key="shoppinglist.articles"/></span>
                  </c:when>
                  <c:otherwise>
                    <span id="productsText"><fmt:message key="shoppinglist.article"/></span>
                  </c:otherwise>
                </c:choose>
            </td>
            <td id="maListButtonCell">
              <!-- link to shoppingListContainer.jsp -->
                    <div id="actMaListeBtn">           
                        <img class="clickable" src="/images/voir_ma_liste_button.gif" width="120" height="24"
                            onclick="location.href='${contextPath}/shoppingList/shoppingListContainer.jsp'"/>
                    </div>                  

                    <div id="inactMaListeBtn">                   
                        <img class="clickable" src="/images/voir_ma_liste_button_inact.gif" width="120" height="24"/>                              
                    </div>
            </td>
          </tr>
          <tr>
            <td colspan="2">
              <div id="slContainer">
              <table cellpadding="0" cellspacing="0" id="sliderList">
                <dsp:droplet name="ForEach">
                  <dsp:param name="array" param="productsList"/>
                  <dsp:param name="elementName" value="sku"/>
                  <dsp:oparam name="output">
                          <dsp:getvalueof var="skuId" param="sku.id"/>
                          <dsp:getvalueof var="set" param="sku.parentProducts"/>
                        <dsp:param name="parentProductsList" value="${castCollection:list(set)}"/>
                        <dsp:param name="product" param="parentProductsList[0]"/>
                  
                        <tr class="sliderItem">
                          <dsp:getvalueof var="imageUrl" param="sku.thumbnailImage.url"/>
                          <dsp:getvalueof var="description" param="sku.LibelleDescriptifArticle"/>
                          <dsp:getvalueof var="productId" param="product.id"/>                          
                          <dsp:droplet name="/com/castorama/droplet/CastProductLinkDroplet">
                            <dsp:param name="productId" value="${productId}"/>
                            <dsp:param name="navAction" value="jump"/>
                            <dsp:oparam name="output">
                              <dsp:getvalueof var="templateUrl" param="url"/>
                            </dsp:oparam>
                            <dsp:getvalueof var="templateUrl" value="${templateUrl}&amp;skuId=${skuId}"/>
                           </dsp:droplet>
                           <td class="slPicCell">
                             <input type="hidden" id="productUrl" value="${contextPath}${templateUrl}"/>
                             <div class="slPictureBlock">
                               <c:choose>
                                  <c:when test="${not empty imageUrl}">
                                    <img class="clickable" src="${imageUrl}" width="44px" height="44px"/>
                                  </c:when>
                                  <c:otherwise>
                                    <img class="clickable" src="/default_images/d_no_img.jpg" width="44px" height="44px"/>
                                  </c:otherwise>
                               </c:choose>
                             </div>
                           </td>
                           <td class="slDescrCell">
                             <span class="clickable">${description}</span>
                           </td>
                        </tr>
                        <script type="text/javascript">
                            $("#slidePanel").ready(function(){                                      
                                shoppingListSkus[shoppingListSkus.length] = '${skuId}';
                            });
                        </script>
                  </dsp:oparam>
                </dsp:droplet>
              </table>
              </div>
           </td>
        </tr>          
        </dsp:oparam>
      </dsp:droplet>
      <!-- place pictures is dropped to -->
      <tr>
        <td colspan="2" id="dropPlace">
            <c:if test="${amount == 0}">
                  <img id="emptyImage" src="/images/empty_shoplist.png" width="261" height="67"/>
            </c:if>
            <img src="/images/drop_place.png" width="265" height="80"/>
        </td>
      </tr>
    </c:otherwise>
  </c:choose>
  </table>
  
  <script type="text/javascript">
    $container = $("#slContainer");
    var windowHeight = $(window).height();
    var containerHeight = $container.height();    
    
    if (windowHeight < containerHeight + 350) {
        $container.css({"overflow": "auto"});
        $container.height(windowHeight - 350);
    }
    
    $(document).ready(function(){
        $amount = $("#amount")[0];
        if ($amount !== undefined) {
            var amount = parseInt($amount.value,10);
            if (amount == 0) {
                $("#actMaListeBtn").hide();
            } else {
                $("#inactMaListeBtn").hide();
            }
        }
    });
  </script>
