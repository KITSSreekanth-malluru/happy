<dsp:page>
    <dsp:importbean bean="/atg/userprofiling/Profile"/>
    <dsp:getvalueof var="displayBanner" bean="Profile.catalog.displayBanner"/>
    <dsp:getvalueof var="displayStores" bean="Profile.catalog.displayStores"/>

    <dsp:droplet name="/com/castorama/droplet/GeolocationDroplet">
        <dsp:oparam name="output">
            <dsp:getvalueof var="bannerId" param="info.bannerId"/>
            <dsp:getvalueof var="stores" param="info.stores"/>
            <dsp:getvalueof var="nearestCastStoreId" param="info.nearestCastStoreId"/>
            <dsp:getvalueof var="isFrance" param="isFrance"/>

            <script>
                var nearestCastStoreId = ${nearestCastStoreId};
                var isFrance = ${isFrance};
            </script>

            <c:if test="${displayBanner}">
                <div id="banner">
                    <c:if test="${not empty bannerId}">
                        <c:choose>
                            <c:when test="${bannerId == '0'}">
                                <dsp:droplet name="/com/castorama/droplet/CastLookupDroplet">
                                    <dsp:param name="id" value="masterCatalog" />
                                    <dsp:param name="itemDescriptor" value="catalog" />
                                    <dsp:param name="repository" bean="/atg/commerce/catalog/ProductCatalog" />
                                    <dsp:oparam name="output">
                                        <dsp:getvalueof var="promoInformation" param="element.ccGenericBannerPromoInfo"/>
                                    </dsp:oparam>
                                </dsp:droplet>
                            </c:when>
                            <c:otherwise>
                                <dsp:droplet name="/com/castorama/droplet/CastLookupDroplet">
                                    <dsp:param name="id" value="${bannerId}" />
                                    <dsp:param name="itemDescriptor" value="magasin" />
                                    <dsp:param name="repository" bean="/atg/registry/Repository/MagasinGSARepository" />
                                    <dsp:oparam name="output">
                                        <dsp:getvalueof var="promoInformation" param="element.ccSpecificBannerPromoInfo"/>
                                    </dsp:oparam>
                                </dsp:droplet>
                            </c:otherwise>
                        </c:choose>
                    </c:if>
                    
                    <dsp:include page="/castCatalog/includes/catalogPromoTemplates/Geolocation popup.jsp" flush="true">
                        <dsp:param name="promoInformation" value="${promoInformation}" />
                    </dsp:include>
                </div>
            </c:if>

            <c:if test="${displayStores}">
                <div class="geoShopsWr" id="stores">
                    <div class="chooseShopText">
                        <dsp:valueof bean="Profile.catalog.popupTitle" valueishtml="true" />
                    </div>
                    
                    <c:if test="${isFrance}">
                        <div class="choosenShopItemContainer" storeId="${stores[0].id}">
                            <h2 class="nearestShop"><fmt:message key="cc.popup.geo.stores.nearest" /></h2>
                            <div class="choosenShopItemWr">
                                <div class="shopItem">
                                    <h3>${stores[0].nom}</h3>
                                    <a href="${stores[0].storeUrl}" target="_blank"><fmt:message key="stockVisualization.hoursAndLocations" /></a>
                                </div>
                                <div class="chooseShopBox <c:if test="${stores[0].iscc}">withPickup</c:if>">
                                    <a href="javascript:void(0)" onclick="geo.trackableChangeContext(${stores[0].id}, ${stores[0].castStoreId})">
                                        <fmt:message key="stockVisualization.selectAsFavoriteStore" />
                                    </a>
                                </div>
                            </div>
                        </div>
    
                        <ul class="listStoresNearYou">
                            <li id="nearestStores">
                                <h4><fmt:message key="cc.popup.geo.stores.near" /></h4>
                                <div class="listOfNearStoresWr">
                                    <ul>
                                        <c:forEach items="${stores}" var="store" begin="1" end="4">
                                            <li>
                                                <div class="shopItem">
                                                    <h3>${store.nom}</h3>
                                                    <a href="${store.storeUrl}" target="_blank"><fmt:message key="stockVisualization.hoursAndLocations" /></a>
                                                </div>
                                                <div class="chooseShopBox <c:if test="${store.iscc}">withPickup</c:if>">
                                                    <a href="javascript:void(0)" onclick="geo.trackableChangeContext(${store.id}, ${store.castStoreId})">
                                                        <fmt:message key="stockVisualization.selectAsFavoriteStore" />
                                                    </a>
                                                </div>
                                            </li>
                                        </c:forEach>
                                    </ul>
                                </div>
                            </li>
                            <li id="allStores"></li>
                        </ul>
                    </c:if>
                </div>
            </c:if>
        </dsp:oparam>
    </dsp:droplet>
</dsp:page>