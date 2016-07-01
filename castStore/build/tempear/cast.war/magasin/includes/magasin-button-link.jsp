<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/com/castorama/profile/CastProfileFormHandler"/>
<dsp:importbean bean="/com/castorama/commerce/clientspace/CastNewsletterFormHandler" />
<dsp:importbean bean="/atg/userprofiling/SessionBean"/>

<dsp:getvalueof var="magasinsUrl" bean="/com/castorama/CastConfiguration.magasinsUrl" />
<c:if test="${empty magasinsUrl || magasinsUrl == ''}">
  <dsp:getvalueof var="magasinsUrl" value="${pageContext.request.contextPath}/magasin/magasin-carte-france.jsp" />
</c:if>
<dsp:droplet name="/atg/dynamo/droplet/Switch">
  <dsp:param bean="Profile.transient" name="value"/>
  <dsp:oparam name="false">
    <dsp:setvalue bean="CastNewsletterFormHandler.repositoryId" beanvalue="Profile.login"/>
    <dsp:droplet name="/atg/dynamo/droplet/IsEmpty">
      <dsp:param name="value" value="CastNewsletterFormHandler.value.id_magasin_ref"/>
      <dsp:oparam name="false">
        <dsp:getvalueof var="prefStore" bean="CastNewsletterFormHandler.value.id_magasin_ref.nom"/>
        <dsp:getvalueof var="prefStoreId" bean="CastNewsletterFormHandler.value.id_magasin_ref.repositoryId"/>
        <c:choose>
          <c:when test="${fn:length(prefStore)!=0}">
            <dsp:droplet name="/com/castorama/droplet/CastLookupDroplet">
                <dsp:param name="id" value="${prefStoreId}"/>
              <dsp:param name="elementName" value="repItem"/>
              <dsp:param name="itemDescriptor" value="magasin" />
              <dsp:param name="repository" bean="/atg/registry/Repository/MagasinGSARepository" />
              <dsp:oparam name="output">
                <dsp:getvalueof var="storeUrl" param="repItem.storeUrl" />
                  <c:choose>
                    <c:when test="${not empty storeUrl && storeUrl != ''}">
                      <dsp:a href="${storeUrl}" target="_blank" iclass="buttonTrouver" />
                    </c:when>
                    <c:otherwise>
                      <dsp:a href="${pageContext.request.contextPath}/magasin/magasin-fiche.jsp?magasinId=${prefStoreId}&parent=clientspace" iclass="buttonTrouver" />
                    </c:otherwise>
                  </c:choose>
              </dsp:oparam>
            </dsp:droplet>
          </c:when>
          <c:otherwise>
            <dsp:a href="${magasinsUrl}" iclass="buttonTrouver" />
          </c:otherwise>
        </c:choose>
      </dsp:oparam>
      <dsp:oparam name="true">
        <dsp:a href="${magasinsUrl}" iclass="buttonTrouver" />
      </dsp:oparam>
    </dsp:droplet>
  </dsp:oparam>
  <dsp:oparam name="true">
    <dsp:a href="${magasinsUrl}" iclass="buttonTrouver" />
  </dsp:oparam>
</dsp:droplet>