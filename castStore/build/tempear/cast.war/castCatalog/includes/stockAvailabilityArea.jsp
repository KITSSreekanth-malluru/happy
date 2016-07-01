<dsp:page>
  <a name="stockArea"></a>

  <dsp:getvalueof var="skuId" param="skuId"/>
  <dsp:getvalueof var="singleSku" param="singleSku"/>
  <dsp:getvalueof var="stockVisualizationDisabled" 
            bean="/com/castorama/stockvisualization/StockVisualizationManager.stockVisualizationDisabled" />

  <dsp:getvalueof var="showStockVisArea" value="false"/>

  <dsp:importbean bean="/atg/userprofiling/Profile" />

  <c:choose>
    <c:when test="${not empty baFakeContext && baFakeContext}">
      <dsp:getvalueof var="localStore" bean="Profile.wrappedCurrentLocalStore"/>
      <dsp:getvalueof var="localStoreId" bean="Profile.wrappedCurrentLocalStore.id" />
    </c:when>
    <c:otherwise>
      <dsp:getvalueof var="localStore" bean="Profile.currentLocalStore"/>
      <dsp:getvalueof var="localStoreId" bean="Profile.currentLocalStore.id" />
    </c:otherwise>
  </c:choose>


  <dsp:droplet name="/com/castorama/droplet/StockAvailabilityDroplet">
    <dsp:param name="skuId" value="${skuId}"/>
    <dsp:param name="store" value="${localStore}" />
    <dsp:param name="svAvailableMap" value="${svAvailableMap}" />
    <dsp:oparam name="immidiateWithdrawal">
    </dsp:oparam>
    <dsp:oparam name="remainingStock">
      <dsp:getvalueof var="showStockVisArea" value="true"/>
    </dsp:oparam>
    <dsp:oparam name="soldOnlyInStore">
      <dsp:getvalueof var="showStockVisArea" value="true"/>
    </dsp:oparam>
    <dsp:oparam name="deliveryTime">
      <dsp:getvalueof var="showStockVisArea" value="true"/>
    </dsp:oparam>
    <dsp:oparam name="ccRemainingStock">
      <dsp:getvalueof var="showStockVisArea" value="true"/>
    </dsp:oparam>
    <dsp:oparam name="ccSoldOnlyInStore">
      <dsp:getvalueof var="showStockVisArea" value="true"/>
    </dsp:oparam>
    <dsp:oparam name="ccDeliveryTime">
      <dsp:getvalueof var="showStockVisArea" value="true"/>
    </dsp:oparam>
  </dsp:droplet>
  
  <dsp:droplet name="/atg/commerce/pricing/priceLists/PriceDroplet">
    <dsp:param name="sku" param="sku"/> 
    <dsp:oparam name="output">
      <dsp:getvalueof var="listPrice" param="price.listPrice"/> 
    </dsp:oparam> 
  </dsp:droplet>

  <c:if test="${stockVisualizationDisabled || (not empty singleSku && !singleSku) || empty listPrice}">
    <dsp:getvalueof var="showStockVisArea" value="false"/>
  </c:if>  
  
  <c:if test="${showStockVisArea}">
    <script type="text/javascript">
      var sendOmnitureIfPageLoad = true;
    </script>
    
    <div class="stockAvailabilityTab tabContent redContent prodDescMarker stockVisV2" tabindex="100">
      <div class="rd_visuStockNavigationBoxHead"><!-- --></div>

      <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
      <dsp:getvalueof var="productId" param="productId" />
    

      <c:if test="${(not empty localStore) and (localStoreId != '999')}">
        <dsp:importbean bean="/com/castorama/stockvisualization/StockModelHolder" />
        <dsp:getvalueof var="stockModels" bean="StockModelHolder.stockModels" />
        
        <c:if test="${not empty stockModels}">
          <div class="refreshableTab">
            <div class="noteArea" ></div>
            <table class="availabilityTable" cellspacing="0" cellpadding="0">
              <tbody>
              
                <dsp:getvalueof var="currStockStatus" bean="StockModelHolder.stockModels[0].status" />
                <dsp:getvalueof var="currMagasinName" bean="StockModelHolder.stockModels[0].magasin.nom" />
                <dsp:getvalueof var="currMagasinId" bean="StockModelHolder.stockModels[0].magasin.id" />
                <dsp:getvalueof var="currMagasinStoreId" bean="StockModelHolder.stockModels[0].magasin.storeId" />
                <dsp:getvalueof var="currMagasinRegionId" bean="StockModelHolder.stockModels[0].magasin.entite.adresse.departement.region.nom" />
                <dsp:getvalueof var="currMagasinDepartamentId" bean="StockModelHolder.stockModels[0].magasin.entite.adresse.departement.nom" />
                <dsp:getvalueof var="stockVisuStatuses" value="${stockVisuStatuses},${currStockStatus}--${currMagasinStoreId}"/>
                <tr  style="position:relative;">
              
                  <td class="firstColumn">
                   <div class="monMagasinV2"><fmt:message key="stockVisualization.mon.magasin"/></div>
                   <div class="nameMagasV2">
                    <fmt:message key="stockVisualization.castorama">
                      <fmt:param value="${currMagasinName}" />
                    </fmt:message>
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

                </tr>
                <tr class="linksInsideTR">
                    <td  colspan="2">

                    <input type="hidden" id="chosenFavStoreAsyncHolder" name="chosenMagasinId" value="${localStoreId}" />

                    <dsp:droplet name="/com/castorama/droplet/CastLookupDroplet">
                      <dsp:param name="id" value="${currMagasinId}"/>
                      <dsp:param name="elementName" value="repItem"/>
                      <dsp:param name="itemDescriptor" value="magasin" />
                      <dsp:param name="repository" bean="/atg/registry/Repository/MagasinGSARepository" />
                      <dsp:oparam name="output">
                        <dsp:getvalueof var="storeUrl" param="repItem.storeUrl" />
                          <c:choose>
                            <c:when test="${not empty storeUrl && storeUrl != ''}">
                              <a href="${storeUrl}" target="_blank" class="svRefreshableTabLinks">
                                    <strong><fmt:message key="stockVisualization.hoursAndLocations" /></strong>
                                </a>
                            </c:when>
                            <c:otherwise>
                            <a href="${contextPath}/magasin/magasin-fiche.jsp?magasinId=${currMagasinId}&regionId=${currMagasinRegionId}&departamentId=${currMagasinDepartamentId}"
                                 class="svRefreshableTabLinks">
                              <strong><fmt:message key="stockVisualization.hoursAndLocations" /></strong>
                            </a>
                            </c:otherwise>
                          </c:choose>
                      </dsp:oparam>
                    </dsp:droplet>

                    </td>

                </tr>

              </tbody>
            </table>
          </div>
        </c:if>
      </c:if>

      <form>
        <div class="postalCodePanel">
          <p class="headerSVV2">
            <strong><fmt:message key="stockVisualization.nearMyHouse" /></strong>
          </p>
          <script type="text/javascript">
          <!--
            $(document).ready(function(){
                $("#postalCodeSV").autocomplete("${pageContext.request.contextPath}/castCommon/includes/CityOrPostalAutocomplete.jsp", {
                    minChars: 2,
                    formatResult: formatResult,
                    formatItem: formatItem
                    });
                function formatResult(row) {
                        return row = row[0].replace("&nbsp;", " ").replace("&amp;","&").replace("&#39;", "'");
                }

                function formatItem(row) {
                    return row = row[0].replace("&nbsp;", " ");
                }
                 var searchTypeValue=1;


            });
          //-->
          </script>

          <div class="ddWrapper">
            <input type="text" id="postalCodeSV"  value="" onkeypress="return pcEnterPress(event, '${productId}', '${skuId}', '${localStoreId}', '${contextPath}');" />
            <input type="button" class="buttonOK" onclick="setPostalCode('${productId}', '${skuId}', '${localStoreId}', '${contextPath}');" />
            <p id="setPostalCodeErrorSV">
              <strong><fmt:message key="stockVisualization.postalCodeErrorMessage" /></strong>
            </p>
          </div>
          <p class="headerSVV2Details"><fmt:message key="stockVisualization.noticeMessage" /></p>
          <div class="clear"></div>
        </div>

      </form>

    </div>

    <div class="whitePopupContainer stockVisPopup" id="stockVisPopup">
      <div class="whitePopupContent">
        <fmt:message key="castCatalog_label.close" var="fermer"/>
        <a title="${fermer}" class="closeBut" onclick="hidePopup(this)" href="javascript:void(0)">${fermer}</a>
        <div class="title">
          <h2><fmt:message key="stockVisualization.availabilityInCastoramaStore" /></h2>
          <p><fmt:message key="stockVisualization.noticeMessage" /></p>
        </div>

        <dsp:include page="/svTest/svHiddenFormInputs.jsp" />
        <input type="hidden" id="topSVButtonRefreshableContentLink" />

        <div class="changeMagasinPanel">
          <form>
            <p class="headerSVV2">Changer de magasin:</p>
            <script type="text/javascript">
            <!--
              $(document).ready(function(){  
                  $("#changeMagasinSV").autocomplete("${pageContext.request.contextPath}/castCommon/includes/CityOrPostalAutocomplete.jsp", {
                      minChars: 2,
                      formatResult: formatResult,
                      formatItem: formatItem
                      });
                  function formatResult(row) {
                          return row = row[0].replace("&nbsp;", " ").replace("&amp;","&").replace("&#39;", "'");
                  }
                  function formatItem(row) {
                      return row = row[0].replace("&nbsp;", " ");
                  }
                   var searchTypeValue=1;
              });
            //-->
            </script>

            <div class="ddWrapper">
              <input type="text" id="changeMagasinSV"  value="" onkeypress="javascript:document.getElementById('postalCodeSV').value=document.getElementById('changeMagasinSV').value;return pcEnterPress(event, '${productId}', '${skuId}', '${localStoreId}', '${contextPath}');" />
              <input type="button" class="buttonOK" onclick="javascript:document.getElementById('postalCodeSV').value=document.getElementById('changeMagasinSV').value;setPostalCode('${productId}', '${skuId}', '${localStoreId}', '${contextPath}');" />
            </div>
          </form>
        </div>

        <div id="stockVisualizationRefreshableContent" class="">
        </div>

        <div class="rememberMyMagasinPanel">
          <div class="rectangle" >
            <input type="button" class="i" />
            <p><fmt:message key="stockVisualization.toSeeAvailabilityOfProducts" /></p>

            <dsp:droplet name="/com/castorama/droplet/CastProductLinkDroplet">
              <dsp:param name="productId" value="${prodId}"/>
               <dsp:param name="navAction" value="jump"/>
               <dsp:param name="skuId" param="skuId"/>
               <dsp:param name="fromSVContext" value="true" />
               <dsp:oparam name="output">
                 <dsp:getvalueof var="rememberMyStoreURL" param="url"/>
               </dsp:oparam>
            </dsp:droplet>
            <dsp:getvalueof var="rememberMyStoreURL" value="${pageContext.request.contextPath}${rememberMyStoreURL}#stockArea"/>

            <dsp:importbean bean="/atg/userprofiling/PropertyManager" />
            <dsp:importbean bean="/atg/userprofiling/SessionBean"/>

            <dsp:droplet name="/atg/dynamo/droplet/Compare">
              <dsp:param bean="Profile.securityStatus" name="obj1"/>
              <dsp:param bean="PropertyManager.securityStatusCookie" name="obj2"/>
              <dsp:oparam name="greaterthan">
                <dsp:setvalue value="${rememberMyStoreURL}" bean="SessionBean.values.loginSuccessURL"/>
                <div class="svRrememberMyMagasinDiv">
                  <a class="svRrememberMyMagasinLink" href="${pageContext.request.contextPath}/user/myProfile.jsp?fromSVContext=true&prodId=${productId}&skuId=${skuId}">
                    <fmt:message key="stockVisualization.rememberMyMagasin" />
                  </a>
                </div>

              </dsp:oparam>
              <dsp:oparam name="default">

                <c:url var="loginURL" value="/user/login.jsp">
                  <c:param name="selectFavoriteStoreSuccessURL" value="${rememberMyStoreURL}" />
                </c:url>

                <div class="svRrememberMyMagasinDiv">
                  <a class="svRrememberMyMagasinLink" href="${loginURL}" >
                    <fmt:message key="stockVisualization.rememberMyMagasin" />
                  </a>
                </div>

              </dsp:oparam>
            </dsp:droplet>
          </div>
        </div>

        <span class="ccBlueButton"><button type="button" id="stockVizCloseButton" class="ccBlueButton" onclick="javascript:hidePopup(this)">CONTINUER MES ACHATS</button></span>

      </div>
    </div>

  </c:if>

</dsp:page>
