<%--
 Expected params:
 item - 
 itemName - 
 categoryId - 
 navAction - navigation action
 navCount - navigation count
 className - style class name
 --%>
<dsp:page>

  <dsp:importbean bean="/atg/commerce/catalog/CatalogNavHistoryCollector" />
  <dsp:importbean bean="/atg/commerce/catalog/CategoryLookup" />
  <dsp:importbean bean="/atg/commerce/catalog/ProductLookup" />
  <dsp:importbean bean="/com/castorama/droplet/ThematiqueLookup"/>
  <dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
  <dsp:importbean bean="/com/castorama/droplet/CastLookupDroplet" />
  <dsp:importbean bean="/atg/commerce/catalog/ProductCatalog" />

  <dsp:droplet name="IsEmpty">
    <dsp:param name="value" param="item" />
    <dsp:oparam name="true">
      <dsp:droplet name="IsEmpty">
        <dsp:param name="value" param="documentId" />
        <dsp:oparam name="false">
          <dsp:droplet name="CastLookupDroplet">
            <dsp:param name="id" param="documentId" />
            <dsp:param name="elementName" value="document" />
            <dsp:param name="itemDescriptor" value="castoramaDocument" />
            <dsp:param name="repository" bean="ProductCatalog" />
            <dsp:oparam name="output">
              <dsp:droplet name="CatalogNavHistoryCollector">
                <dsp:param name="item" param="document" />
                <dsp:param name="navAction" param="navAction" />
                <dsp:param name="navCount" param="navCount" />
                <dsp:param name="disableGJ" value="true" />
              </dsp:droplet>
            </dsp:oparam>
          </dsp:droplet>
        </dsp:oparam>
        <dsp:oparam name="true">
          <dsp:droplet name="IsEmpty">
            <dsp:param name="value" param="productId" />
            <dsp:oparam name="false">
              <dsp:droplet name="ProductLookup">
                <dsp:param name="id" param="productId" />
                <dsp:oparam name="output">
                  <dsp:droplet name="CatalogNavHistoryCollector">
                    <dsp:param name="item" param="element" />
                    <dsp:param name="navAction" param="navAction" />
                    <dsp:param name="navCount" param="navCount" />
                  </dsp:droplet>
                </dsp:oparam>
              </dsp:droplet>
            </dsp:oparam>
            <dsp:oparam name="true">
              <dsp:droplet name="IsEmpty">
                <dsp:param name="value" param="categoryId" />
                <dsp:oparam name="false">
                  <dsp:droplet name="CategoryLookup">
                    <dsp:param name="id" param="categoryId" />
                    <dsp:oparam name="output">
                      <dsp:droplet name="CatalogNavHistoryCollector">
                        <dsp:param name="item" param="element" />
                        <dsp:param name="navAction" param="navAction" />
                        <dsp:param name="navCount" param="navCount" />
                      </dsp:droplet>
                    </dsp:oparam>
                  </dsp:droplet>
                </dsp:oparam>
                <dsp:oparam name="true">
                  <dsp:droplet name="IsEmpty">
                    <dsp:param name="value" param="topicId" />
                    <dsp:oparam name="false">
                      <dsp:droplet name="ThematiqueLookup">
                        <dsp:param name="thematiqueId" param="topicId" />
                        <dsp:oparam name="output">
                          <dsp:droplet name="CatalogNavHistoryCollector">
                            <dsp:param name="item" param="thematique" />
                            <dsp:param name="itemName" param="thematique.title" />
                            <dsp:param name="navAction" value="pop" />
                            <dsp:param name="navCount" param="navCount" />
                          </dsp:droplet>
                        </dsp:oparam>
                      </dsp:droplet>
                    </dsp:oparam>
                  </dsp:droplet>
                </dsp:oparam>
              </dsp:droplet>
           </dsp:oparam>
        </dsp:droplet>
      </dsp:oparam>
     </dsp:droplet>
    </dsp:oparam>
    <dsp:oparam name="false">
      <dsp:droplet name="CatalogNavHistoryCollector">
        <dsp:param name="itemName" param="itemName" />
        <dsp:param name="item" param="item" />
        <dsp:param name="navAction" param="navAction" />
        <dsp:param name="navCount" param="navCount" />
      </dsp:droplet>
    </dsp:oparam>
  </dsp:droplet>
</dsp:page>