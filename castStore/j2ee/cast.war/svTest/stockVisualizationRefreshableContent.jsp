<dsp:page>
    
  <dsp:importbean bean="/com/castorama/stockvisualization/StockModelHolder" />
  <dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
  <dsp:importbean bean="/atg/userprofiling/Profile" />
  <dsp:importbean bean="/atg/dynamo/droplet/Compare" />
  <dsp:importbean bean="/atg/userprofiling/PropertyManager" />
  <dsp:importbean bean="/com/castorama/commerce/clientspace/CastNewsletterFormHandler" />
  <dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
  <dsp:importbean bean="/atg/userprofiling/SessionBean"/>
  <dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>
  <dsp:importbean bean="/atg/commerce/catalog/SKULookup"/>

  <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
  <dsp:getvalueof var="contextReferer" bean="/OriginatingRequest.referer" />
  
  <dsp:getvalueof var="prodId" param="prodId" />
  <dsp:getvalueof var="codeArticle" param="codeArticle" />
  <dsp:getvalueof var="skuId" param="skuId" />

  <dsp:getvalueof var="queryCondition" value="byFavoriteStore" />
  <c:if test="${fn:contains(contextReferer, 'magasin-fiche.jsp')}">
    <dsp:getvalueof var="magasinId" bean="/atg/userprofiling/SessionBean.values.magasinId" />
    <dsp:getvalueof var="queryCondition" value="byDefinedStore" />
    
    <script>
       document.getElementById("queryConditionHiddenInput").value = "byDefinedStore";
       document.getElementById("magasinIdHiddenInput").value = "${magasinId}";
    </script>

  </c:if>
  <c:if test="${empty magasinId}">
    <dsp:getvalueof var="magasinId" bean="CastNewsletterFormHandler.value.id_magasin_ref.repositoryId"/>
  </c:if>
  <c:if test="${empty magasinId}">
    <dsp:getvalueof var="magasinId" bean="Profile.currentLocalStore.id"/>
    <dsp:getvalueof var="queryCondition" value="byDefinedStore" />
  </c:if>

  <dsp:getvalueof var="stockModels" bean="StockModelHolder.stockModels" />

  <dsp:droplet name="ProductLookup">
    <dsp:param name="id" value="${prodId}" />
    <dsp:oparam name="output">
      <dsp:getvalueof var="templateUrl" param="element.template.url" />
    </dsp:oparam>
  </dsp:droplet>

  <dsp:droplet name="Compare">
    <dsp:param name="obj1" bean="Profile.securityStatus" />
    <dsp:param name="obj2" bean="PropertyManager.securityStatusCookie" />
    <dsp:oparam name="lessthan">
      <dsp:getvalueof var="definedStoreStock" bean="StockModelHolder.definedStoreStock" />
      <c:choose>
        <c:when test="${not empty definedStoreStock}">
          <dsp:getvalueof var="favoriteMagasinId" bean="StockModelHolder.definedStoreStock.magasin.repositoryId" />
        </c:when>
        <c:otherwise>
          <dsp:getvalueof var="favoriteMagasinId" bean="Profile.currentLocalStore.id" />
        </c:otherwise>
      </c:choose>
    </dsp:oparam>
    <dsp:oparam name="default">

      <dsp:getvalueof var="userLoggedIn" value="true" />

      <dsp:setvalue bean="CastNewsletterFormHandler.repositoryId" beanvalue="Profile.login"/>
      <dsp:droplet name="IsEmpty">
        <dsp:param name="value" value="CastNewsletterFormHandler.prefStore"/>
        <dsp:oparam name="false">
          <dsp:getvalueof var="favoriteMagasinId" bean="CastNewsletterFormHandler.prefStore" />
        </dsp:oparam>
      </dsp:droplet>

    </dsp:oparam>
  </dsp:droplet>

  <c:if test="${not empty stockModels}">
    <div class="refreshableTab">
      <div class="noteArea" ></div>

      <div class="fQty" >
        <dsp:getvalueof var="quantity" value="${stockModels[0].quantity}"/>
        <dsp:getvalueof var="itemId" value="blabla"/>
  
        <span class="refrefashebale">Modifier la quantit&eacute;:</span>
  
        <form>
          <c:choose>
            <c:when test="${quantity == 1}">
              <a class="iconMinus disabledMinus" id="iconMinus${itemId}" href="javascript:void(0);" onclick="decreaseSVQuantity('${itemId}');" ><fmt:message key="msg.cart.quantity.less"/></a>  
            </c:when>
            <c:otherwise>
              <a class="iconMinus" id="iconMinus${itemId}" href="javascript:void(0);" onclick="decreaseSVQuantity('${itemId}');" ><fmt:message key="msg.cart.quantity.less"/></a>
            </c:otherwise>
          </c:choose>
          <input id="quantityValue${itemId}" size="4" type="text" value="${quantity}" onchange="checkSVQuantity('${itemId}');"  onkeyup="checkSVQuantity('${itemId}');" onkeypress="return quantityEnterPres(event);" maxlength="3"/>
          <a class="iconPlus" id="iconPlus${itemId}" href="javascript:void(0);" onclick="increaseSVQuantity('${itemId}');"><fmt:message key="msg.cart.quantity.more"/></a>
          <input class="recalculate" type="button" onclick="applySVQuantity('${prodId}', '${skuId}', '${contextPath}')" />
          <div class="clear"></div> 
        </form>
      </div>
  
      <div class="clear"></div>
      
        <dsp:getvalueof var="showRetraitMagasinCol" value="false" />
        <dsp:droplet name="ForEach">
          <dsp:param name="array" value="${stockModels}" />
          <dsp:param name="elementName" value="currStockModel" />
          <dsp:oparam name="output">
            <dsp:getvalueof var="currMagasinRetraitMagasin" param="currStockModel.magasin.retraitMagasin" />
            <c:if test="${not empty currMagasinRetraitMagasin and currMagasinRetraitMagasin}">
              <dsp:getvalueof var="showRetraitMagasinCol" value="true" />
            </c:if>
          </dsp:oparam>
        </dsp:droplet>

      <table class="availabilityTablePopUp" cellspacing="0" cellpadding="0">
        <dsp:getvalueof var="stockVisuStatuses" value=""/>
        <dsp:droplet name="ForEach">
          <dsp:param name="array" value="${stockModels}" />
          <dsp:param name="elementName" value="currStockModel" />
          <dsp:oparam name="outputStart">
          <thead>  
            <tr>
              <th class="firstColumn">Magasin</th>
              <th class="secondColumn">Disponibilit&eacute;</th>
              <c:if test="${showRetraitMagasinCol}"><th class="retraitEnMagasin"><fmt:message key="cc.retrait.en.magasin"/></th></c:if>
              <th><!-- --></th>
            </tr>
          </thead>
          <tbody>
  
          </dsp:oparam>
          <dsp:oparam name="output">
            <dsp:getvalueof var="currStockStatus" param="currStockModel.status" />
            <dsp:getvalueof var="currMagasinName" param="currStockModel.magasin.nom" />
            <dsp:getvalueof var="currMagasinId" param="currStockModel.magasin.id" />
            <dsp:getvalueof var="currMagasinStoreId" param="currStockModel.magasin.storeId" />
            <dsp:getvalueof var="currMagasinRetraitMagasin" param="currStockModel.magasin.retraitMagasin" />
            <dsp:getvalueof var="currMagasinRegionId" param="currStockModel.magasin.entite.adresse.departement.region.nom" />
            <dsp:getvalueof var="currMagasinDepartamentId" param="currStockModel.magasin.entite.adresse.departement.nom" />
            <dsp:getvalueof var="stockVisuStatuses" value="${stockVisuStatuses},${currStockStatus}--${currMagasinStoreId}"/>
            <tr>
              <td class="firstColumn">
               <div class="nameMagasV2">
                <h3><fmt:message key="stockVisualization.castorama"><fmt:param value="${currMagasinName}" /></fmt:message></h3>
                <dsp:droplet name="/com/castorama/droplet/CastLookupDroplet">
                  <dsp:param name="id" value="${currMagasinId}"/>
                  <dsp:param name="elementName" value="repItem"/>
                  <dsp:param name="itemDescriptor" value="magasin" />
                  <dsp:param name="repository" bean="/atg/registry/Repository/MagasinGSARepository" />
                  <dsp:oparam name="output">
                    <dsp:getvalueof var="storeUrl" param="repItem.storeUrl" />
                      <c:choose>
                        <c:when test="${not empty storeUrl && storeUrl != ''}">
                          <span><a href="${storeUrl}" target="_blank" class="svRefreshableTabLinks"><fmt:message key="stockVisualization.hoursAndLocations" /></a></span>
                        </c:when>
                        <c:otherwise>
                          <span><a href="${contextPath}/magasin/magasin-fiche.jsp?magasinId=${currMagasinId}&regionId=${currMagasinRegionId}&departamentId=${currMagasinDepartamentId}" class="svRefreshableTabLinks"><fmt:message key="stockVisualization.hoursAndLocations" /></a></span>
                        </c:otherwise>
                      </c:choose>
                  </dsp:oparam>
                </dsp:droplet>
                
               </div>
              </td>
              <td class="secondColumn">
                <ul class="fixFFV2">
                  <c:choose>
                    <c:when test="${currStockStatus == 1}">
                      <c:set var="liClass" value="available" />
                    </c:when>
                    <c:when test="${currStockStatus == 2}">
                      <c:set var="liClass" value="limitedAvailability" />
                    </c:when>
                    <c:otherwise>
                      <c:set var="liClass" value="notAvailable" />
                    </c:otherwise>
                  </c:choose>
                  <li class="${liClass}">
                    <c:if test="${liClass == 'notAvailable'}">
                      <dsp:getvalueof var="index" param="index" />
                      <input type="button" class="toolTipButton" class="i" onmouseover="document.getElementById('natooltip${index}').style.display='block';" onmouseout="document.getElementById('natooltip${index}').style.display='none';" />
                      <div id="natooltip${index}" class="toolTip"><fmt:message key="stockVisualization.notAvailableTooltip" /></div>
                    </c:if>
                  </li>
                </ul>
              </td>
              <c:if test="${showRetraitMagasinCol}">
                <td class="retraitEnMagasin">
                  <c:if test="${not empty currMagasinRetraitMagasin and currMagasinRetraitMagasin}">
                    <dsp:getvalueof var="delayTime" param="currStockModel.magasin.ccDelayPeriod"/>
                    <dsp:getvalueof var="delayUnit" param="currStockModel.magasin.ccDelayPeriodUnit"/>
                    <fmt:message key="header.mon.magasin.delayunit.hour" var="hour"/>
                    <fmt:message key="header.mon.magasin.delayunit.day" var="day"/>
                    <fmt:message key="header.mon.magasin.delayunit.days" var="days"/>
                    <dsp:getvalueof var="displayDelayUnit" value="${(delayUnit == 0)?hour:(delayTime == 1?day:days)}" />
                    <p>Retrait express sous ${delayTime}${displayDelayUnit}</p>
                  </c:if>
                </td>
            	</c:if>
              <td class="chooseDeMagazine">
                <input type="hidden" id="chosenFavStoreAsyncHolder" name="chosenMagasinId" value="${favoriteMagasinId}" />
								 
                <c:choose>
                  <c:when test="${currMagasinId == favoriteMagasinId}">
                    <%-- <fmt:message key="stockVisualization.favoriteStore">
                      <fmt:param value="${currMagasinName}" />
                    </fmt:message>
                    --%>
                    <div class="monMagasinV2"><fmt:message key="stockVisualization.favoriteStore"><fmt:param value="${currMagasinName}" /></fmt:message></div>
                    
                  </c:when>
                  <c:otherwise>
                    <c:choose>
                      <c:when test="${userLoggedIn == 'true'}">
                        <dsp:getvalueof var="queryCondition" value="byFavoriteStore" />
                      </c:when>
                      <c:otherwise>
                        <dsp:getvalueof var="queryCondition" value="byDefinedStore" />
                      </c:otherwise>
                    </c:choose>
                    <dsp:getvalueof var="escapedApostrophe" value="\\'" />
                     <dsp:getvalueof var="currMagasinName" value="${fn:replace(currMagasinName, \"'\", escapedApostrophe)}" />
                     <span><a href="javascript:geo.changeContext(${currMagasinId})" class="svRefreshableTabLinks" ><fmt:message key="stockVisualization.selectAsFavoriteStore" /></a></span>
                  </c:otherwise>
                </c:choose>
            	</td>
            </tr>
          </dsp:oparam>
          <dsp:oparam name="outputEnd">
  
          </tbody>
  
          </dsp:oparam>
        </dsp:droplet>
        <dsp:getvalueof var="stockVisuStatuses" value="${fn:substringAfter(stockVisuStatuses, ',')}"/>
        <fmt:message var="available" key="stockVisualization.available" />
        <fmt:message var="limQuantity" key="stockVisualization.limitedQuantity" />
        <fmt:message var="notAvailable" key="stockVisualization.notAvailable" />

        <dsp:getvalueof var="stockVisuStatuses" value="${castCollection:replaceAll(stockVisuStatuses, '[0-9]{2}-', notAvailable)}"/>
        <dsp:getvalueof var="stockVisuStatuses" value="${castCollection:replaceAll(stockVisuStatuses, '1-', available)}"/>
        <dsp:getvalueof var="stockVisuStatuses" value="${castCollection:replaceAll(stockVisuStatuses, '2-', limQuantity)}"/>
        <dsp:setvalue bean="SessionBean.values.stockVisuStatus" value="${stockVisuStatuses}"/>
        <dsp:setvalue bean="SessionBean.values.stockVisuProdID" value="${codeArticle}"/>
        <input type="hidden"  value="${stockVisuStatuses}" id="refreshableTabStatuses"/>
        <input type="hidden"  value="${codeArticle}" id="refreshableTabCodearticle"/>
      </table>
    </div>
  </c:if>
</dsp:page>