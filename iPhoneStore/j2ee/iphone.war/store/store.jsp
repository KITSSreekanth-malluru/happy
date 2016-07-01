<dsp:page>
  <dsp:importbean var="dynamoConfig" bean="/atg/dynamo/Configuration" />
  <dsp:getvalueof var="serverName" vartype="java.lang.String" value="${dynamoConfig.siteHttpServerName}" />
  <dsp:getvalueof var="serverPort" vartype="java.lang.String" value="${dynamoConfig.siteHttpServerPort}" />
  <dsp:getvalueof var="httpServer" vartype="java.lang.String" value="http://${serverName}:${serverPort}" />
  <dsp:getvalueof var="contextPath" vartype="java.lang.String" value="${originatingRequest.contextPath}" />
  <dsp:getvalueof var="httpLink" vartype="java.lang.String" value="${httpServer}${contextPath}" />
  <m:jsonObject name="store">
    <dsp:droplet name="/com/castorama/droplet/MagasinLookupDroplet">
      <dsp:param name="id" param="storeId" />
      <dsp:param name="elementName" value="magasin" />
      <dsp:oparam name="output">
        <json:property name="storeId">
          <dsp:valueof param="storeId"/>
        </json:property>
        <json:property name="stockStoreId">
          <dsp:valueof param="magasin.storeId"/>
        </json:property>
        
        <json:property name="name">
          <dsp:valueof param="magasin.nom"/>
        </json:property>
        <json:object name="address">       
          <json:property name="department">
            <dsp:valueof param="magasin.entite.adresse.departement.nom"/>
          </json:property>
          <json:property name="tel">
            <dsp:valueof param="magasin.entite.adresse.tel" converter="phoneNumber"/>
          </json:property> 
          <json:property name="rue">
            <dsp:valueof param="magasin.entite.adresse.rue" />
          </json:property>
          <json:property name="cp">
            <dsp:valueof param="magasin.entite.adresse.cp" />
          </json:property>
          <json:property name="ville">
            <dsp:valueof param="magasin.entite.adresse.ville" />
          </json:property>
        </json:object>
        <json:property name="horaires">
          <dsp:valueof param="magasin.horaires" />
        </json:property>
        <%-- %>json:array name="ouvexcep">
          <dsp:droplet name="/atg/dynamo/droplet/ForEach">
            <dsp:param name="array" param="magasin.listactus"/>
            <dsp:param name="elementName" value="actu"/>            
            <dsp:oparam name="output">
              <json:property>
                <dsp:valueof param="actu.contenu"/>
              </json:property>
            </dsp:oparam>
          </dsp:droplet>          
        </json:array--%>
        <json:array name="services">
          <dsp:droplet name="/atg/dynamo/droplet/ForEach">
            <dsp:param name="array" param="magasin.listservices"/>
            <dsp:param name="elementName" value="magasinService"/>
            <dsp:oparam name="output">
              <json:property>
                <dsp:valueof param="magasinService.nom"/>
              </json:property>
            </dsp:oparam>
          </dsp:droplet>
        </json:array>
        <dsp:droplet name="/com/castorama/mobile/droplet/CalculateDistanceToStore">
          <dsp:param name="longitude" param="longitude" />
          <dsp:param name="latitude" param="latitude" />
          <dsp:param name="storeId" param="storeId" />
          <dsp:oparam name="output">
            <dsp:getvalueof var="distance" param="distance"/>
          </dsp:oparam>
          <dsp:oparam name="empty">
          </dsp:oparam>
        </dsp:droplet>
        
        <json:property name="distance">
          ${distance}
        </json:property>
        <json:property name="latitude">
          <dsp:valueof param="magasin.latitude"/>
        </json:property>
        <json:property name="longitude">
          <dsp:valueof param="magasin.longitude"/>
        </json:property>
        <json:property name="productLocalization">
          <dsp:getvalueof var="localization" param="magasin.productLocalization"/>
          <dsp:valueof param="magasin.productLocalization"/>
        </json:property>
	
    	<dsp:getvalueof var="img" param="magasin.url_photo" />
    	<c:if test="${not empty img && (!fn:startsWith(img,'http') && !fn:startsWith(img,'https'))}">
          <c:set var="img" value="${httpLink}${img}"/>
        </c:if>
        <json:property name="img" value="${img}"/>

        <json:property name="errorCode" value="${0}"/>
      </dsp:oparam>
      <dsp:oparam name="empty">
          <json:property name="errorMessage">
            <fmt:message key="er_408"/>
          </json:property>          
          <json:property name="errorCode" value="${1}"/>
      </dsp:oparam>
    </dsp:droplet>
    
  </m:jsonObject>
</dsp:page>