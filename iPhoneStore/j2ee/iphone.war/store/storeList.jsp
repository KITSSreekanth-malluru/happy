<dsp:page>
  <json:array name="stores">
    <dsp:droplet name="/atg/dynamo/droplet/ForEach">
      <dsp:param name="array" param="storeList"/>
      <dsp:oparam name="output">
        <dsp:getvalueof var="storeIdVar" param="element.storeId"/>
          <m:jsonObject>
            <dsp:droplet name="/com/castorama/droplet/MagasinLookupDroplet">
              <dsp:param name="id" param="element.storeId" />
              <dsp:param name="elementName" value="magasin" />
              <dsp:oparam name="output">
                <json:property name="storeId">
                  <dsp:valueof param="magasin.repositoryId"/>
                </json:property>
  		        <json:property name="stockStoreId">
                  <dsp:valueof param="magasin.storeId"/>
                </json:property>
                
                <json:property name="name">
                  <dsp:valueof param="magasin.nom"/>
                </json:property>
                <dsp:getvalueof var="distance" param="element.distance"/>
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
              </dsp:oparam>
            </dsp:droplet>
          </m:jsonObject>
      </dsp:oparam>
    </dsp:droplet>
  </json:array>
</dsp:page>