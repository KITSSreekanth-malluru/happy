<dsp:page>
    <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
    <dsp:importbean bean="/com/castorama/droplet/TestPricesForExportDroplet"/>

    <dsp:droplet name="TestPricesForExportDroplet">
        <dsp:param name="skuId" value="${skuId}"/>
        <dsp:param name="storeCode" value="${storeCode}"/>
        <dsp:oparam name="output">
            <dsp:droplet name="ForEach">
                <dsp:param name="array" param="listPrices"/>
                <dsp:oparam name="output">
                    List price
                    <dsp:valueof param="element"/>
                </dsp:oparam>
            </dsp:droplet>
            <dsp:droplet name="ForEach">
                <dsp:param name="array" param="salePrices"/>
                <dsp:oparam name="output">
                    Sale price
                    <dsp:valueof param="element"/>
                </dsp:oparam>
            </dsp:droplet>
        </dsp:oparam>
    </dsp:droplet>
</dsp:page>
