<%@ taglib prefix="dsp" uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0"%>
<dsp:page>

    <dsp:importbean bean="/com/castorama/droplet/OrderPromotionsDroplet"/>
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
    
    <dsp:droplet name="OrderPromotionsDroplet">
        <dsp:param name="order" param="orderObj"/>
        <dsp:param name="orderLocal" value=""/>
        <dsp:param name="pageName" value="orderPage"/>
        <dsp:oparam name="output">
            
            <dsp:droplet name="ForEach">
                <dsp:param name="array" param="orderPromotions" />
                <dsp:oparam name="output">
                    <dsp:getvalueof var="promoName" param="element.displayName"/>
                    <dsp:getvalueof var="description" param="element.description"/>
                        
                    <dsp:droplet name="ForEach">
                        <dsp:param name="array" param="element.media"/>
                        <dsp:param name="elementName" value="media"/>
                        <dsp:oparam name="output">
                            <dsp:getvalueof var="mediaUrl" param="media.url"/>
                        </dsp:oparam>
                    </dsp:droplet>       
                        
                    <c:if test="${not empty description}">
                        <c:choose>
                            <c:when test="${fn:startsWith(promoName, 'VenteFlash')==true}">
                                <dsp:getvalueof var="mediaUrl" value="../images/icoTimer.gif"/>
                            </c:when>
                            <c:when test="${empty mediaUrl}">
                                <dsp:getvalueof var="mediaUrl" value="../images/icoPresent.gif"/>
                            </c:when>
                            <c:otherwise></c:otherwise>
                        </c:choose>
                        <div class="boxCartGift">
                            <p><dsp:valueof value="${description}" valueishtml="true"/></p>
                        </div>   
                    </c:if>
                    <dsp:getvalueof var="mediaUrl" value=""/>
                </dsp:oparam>
            </dsp:droplet>
            
        </dsp:oparam>
    </dsp:droplet>
    
</dsp:page>