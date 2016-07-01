<dsp:page>

    <dsp:importbean bean="/atg/targeting/TargetingRandom"/>
    
    <dsp:getvalueof var="magasinPagePromo" param="magasinPagePromoBean"/>
    
        <dsp:droplet name="TargetingRandom">
            <dsp:param value="${magasinPagePromo}" name="targeter"/>
            <dsp:param name="fireViewItemEvent" value="false"/>
            <dsp:param name="elementName" value="magasin"/>
            <dsp:oparam name="output">
                <dsp:getvalueof var="imageName" param='magasin.image.name'/>
                <dsp:getvalueof var="url" param='magasin.url'/>
                <div class="magasins_banner">
                    <a href="${url }" title="${imageName }">
                        <img src="<dsp:valueof param='magasin.image.url'/>" alt="${imageName}" title="${imageName}" />
                    </a>
                    <div>
                        <strong><dsp:valueof param="magasin.title" valueishtml="true"/></strong><br/>
                        <dsp:valueof param="magasin.description" valueishtml="true"/>
                    </div>
                    <a href="${url }" class="bcLink"><fmt:message key="magasin.see.details"/></a>
                </div>
            </dsp:oparam>
        </dsp:droplet>
</dsp:page>