<dsp:page>
    <dsp:getvalueof var="noResultsMessage" bean="/com/castorama/seo/SEOConfiguration.noResultsMessage"/>
    <dsp:getvalueof var="continueSearchMessage" bean="/com/castorama/seo/SEOConfiguration.continueSearchMessage"/>
    <dsp:importbean bean="/atg/commerce/catalog/ProductCatalog" />
    <dsp:droplet name="/atg/dynamo/droplet/RQLQueryRange" >
        <dsp:param name="itemDescriptor" value="fastLabConfigs" />
        <dsp:param name="repository" bean="ProductCatalog" />
        <dsp:param name="queryRQL" value="ALL" />
        <dsp:param name="howMany" value="1" />
        <dsp:param name="sortProperties" value="" />
        <dsp:oparam name="output">
          <dsp:getvalueof var="fastLabConfig" param="element"/>
        </dsp:oparam>
    </dsp:droplet>
    <dsp:getvalueof var="questionParam" param="question"/>
    <dsp:setvalue param="fastLabConfig" value="${fastLabConfig}" />

    <div class="errorMessageWrapper" >
        <div class="errorMessage">
            <dsp:droplet name="/atg/dynamo/droplet/Format">
                <dsp:param name="format" value="${noResultsMessage}"/>
                <dsp:param param="question" name="0"/>
                <dsp:oparam name="output">
                    <dsp:getvalueof param="message" var="message" />
                    ${message}
                </dsp:oparam>
            </dsp:droplet>
        </div>
    </div>
    <dsp:include page="/castCatalog/includes/top3categories.jsp">
        <dsp:param name="fastLabConfig" param="fastLabConfig"/>
    </dsp:include>
</dsp:page>