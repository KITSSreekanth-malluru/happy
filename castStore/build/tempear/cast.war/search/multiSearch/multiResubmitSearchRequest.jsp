<dsp:page>

  
  <dsp:importbean bean="/atg/commerce/search/catalog/QueryFormHandler"/>
  <dsp:importbean bean="/com/castorama/document/CastDocQueryFormHandler"/>
  <dsp:importbean bean="/com/castorama/magasin/MagasinSearchFormHandler"/>
   
  <dsp:include page="../includes/resubmitSearchRequest.jsp" flush="true">
    <dsp:param name="searchResponse" bean="QueryFormHandler.searchResponse"/>
    <dsp:param name="isMultiSearch" value="true"/>
  </dsp:include>
  
  <dsp:include page="../includes/docResubmitSearchRequest.jsp" flush="true">
    <dsp:param name="isMultiSearch" value="true"/>
  </dsp:include>
  
  <dsp:include page="../magasins/magasinResubmitSearchRequest.jsp" flush="true">
    <dsp:param name="isMultiSearch" value="true"/>
  </dsp:include>
  
  <dsp:droplet name="/com/castorama/search/droplet/CastMultiSearchResubmitDroplet">
    <dsp:param name="queryFormHandler" bean="QueryFormHandler"/>
    <dsp:param name="docQueryFormHandler" bean="CastDocQueryFormHandler"/>
    <dsp:param name="magasinQueryFormHandler" bean="MagasinSearchFormHandler"/>
    <dsp:param name="pageNum" param="pageNum"/>
    <dsp:param name="pageNum" param="pageNum"/>
    <dsp:param name="currentTab" param="currentTab"/>
    <dsp:oparam name="output">
    </dsp:oparam>
  </dsp:droplet>

</dsp:page>