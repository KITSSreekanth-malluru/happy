<dsp:page>
    <dsp:importbean bean="/atg/userprofiling/Profile"/>
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
    <dsp:importbean bean="/atg/commerce/catalog/CategoryLookup"/>
    <dsp:importbean bean="/com/castorama/droplet/CastCategoryLinkDroplet"/>
    <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
    <dsp:getvalueof var="array" bean="Profile.catalog.topNavigationCategories"/>
    
    
    <dsp:getvalueof var="firstCategoryURL" param="fastLabConfig.firstCategoryImage.url"/>
    <dsp:getvalueof var="secondCategoryURL" param="fastLabConfig.secondCategoryImage.url"/>
    <dsp:getvalueof var="thirdCategoryURL" param="fastLabConfig.thirdCategoryImage.url"/>
    <dsp:getvalueof var="firstCategoryLink" param="fastLabConfig.firstCategoryLink"/>
    <dsp:getvalueof var="secondCategoryLink" param="fastLabConfig.secondCategoryLink"/>
    <dsp:getvalueof var="thirdCategoryLink" param="fastLabConfig.thirdCategoryLink"/>
    <c:set var="listImages" value="${castCollection:column()}"/>
    <c:set var="listUrls" value="${castCollection:column()}"/>
    ${castCollection:add(listImages, firstCategoryURL)}
    ${castCollection:add(listImages, secondCategoryURL)}
    ${castCollection:add(listImages, thirdCategoryURL)}
    ${castCollection:add(listUrls, firstCategoryLink)}
    ${castCollection:add(listUrls, secondCategoryLink)}
    ${castCollection:add(listUrls, thirdCategoryLink)}
    
    <dsp:droplet name="/atg/dynamo/droplet/Cache">
        <dsp:param name="key" value="error_404_top3_categories" />
        <dsp:param name="cacheCheckSeconds" bean="/com/castorama/CastConfiguration.cacheCheckSecondsLong"/>
        <dsp:oparam name="output">
        <dsp:droplet name="ForEach">
        <dsp:param name="array" value="${array}"/>
            <dsp:oparam name="outputStart">
                <div class="top3Header">
            </dsp:oparam>
            <dsp:oparam name="outputEnd">
                </div>
            </dsp:oparam>
            <dsp:oparam name="output">
            <dsp:getvalueof var="index" param="index"/>
            <c:if test="${index < 2}">
                <div class="categoryColName paddingRight">
                    <dsp:valueof param="element.displayName"/>
                </div>
            </c:if>
            <c:if test="${index eq 2}">
                <div class="categoryColName">
                    <dsp:valueof param="element.displayName"/>
                </div>
            </c:if>
        </dsp:oparam>
    </dsp:droplet>
    
    <dsp:droplet name="ForEach">
    <dsp:param name="array" value="${array}"/>
        <dsp:oparam name="outputStart">
            <table class="top3content">
		</dsp:oparam>
		<dsp:oparam name="outputEnd">
            </table>
            <script>
            $(function(){
                $('.top3content td img').click(function(){
                    var link = $(this).siblings('input[type=hidden]').val();
                    if(link) {
                        location.href=link;
                    }
                });
            })
            </script>
		</dsp:oparam>
        <dsp:oparam name="output">
            <dsp:param name="id" param="element.repositoryId"/>
            <dsp:getvalueof var="index" param="index"/>
            <dsp:getvalueof var="count" param="size"/>
            <dsp:getvalueof var="displayName" param="element.displayName"/>
            <c:if test="${index < 3}">
            <c:set var="catClassName" value="categoryColumn borderRight paddingLeft"/>
            <c:if test="${index eq 0}">
                <c:set var="catClassName" value="categoryColumn borderRight"/>
            </c:if>
            <c:if test="${index eq 2}">
                <c:set var="catClassName" value="leftCategoryColumn paddingLeft"/>
            </c:if>
                <td valign="top" class="${catClassName}">
                <input type="hidden" value="${listUrls[index]}">
                <img src="${listImages[index]}" style="max-width:240px; cursor: pointer;"/>
                <dsp:droplet name="CategoryLookup">
                    <dsp:param name="id" param="id"/>
                    <dsp:oparam name="output">
                        <dsp:getvalueof var="childCategories" param="element.childCategories" />
                        <dsp:droplet name="ForEach">
								<dsp:param name="array" value="${childCategories}"/>
								<dsp:oparam name="outputStart">
                                    <ul>
								</dsp:oparam>
								<dsp:oparam name="outputEnd">
                                    </ul>
								</dsp:oparam>
								<dsp:oparam name="output">
                                    <dsp:droplet name="CastCategoryLinkDroplet">
										<dsp:param name="categoryId" param="element.repositoryId"/>	
										<dsp:param name="navCount" bean="/Constants.null"/>
										<dsp:param name="navAction" value="jump"/>
										<dsp:oparam name="output">
											<dsp:getvalueof var="url" param="url"/>
											<li>
												<dsp:a href="${contextPath}${url}&wrap=true">
													<dsp:valueof param="element.displayName"/>																				
												</dsp:a>
											</li>
										</dsp:oparam>
									</dsp:droplet>
                                </dsp:oparam>
                        </dsp:droplet>
                    </dsp:oparam>
                </dsp:droplet>
                </td>
            </c:if>
        </dsp:oparam>           
    </dsp:droplet>
    </dsp:oparam>
    </dsp:droplet>
</dsp:page>