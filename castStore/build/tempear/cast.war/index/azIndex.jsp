<dsp:page>  
  <cast:pageContainer>
    <jsp:attribute name="bottomBanners">true</jsp:attribute>
    <jsp:attribute name="cloudType">page</jsp:attribute>
    <jsp:attribute name="bodyContent">    
    <dsp:importbean bean="/atg/commerce/catalog/CatalogNavHistory" />
    <div class="content azcontent">
    
    <div class="breadcrumbs bluePage">
        <div class="homeBreadIco">
            <a href="/store/home.jsp">
              <img src="/store/images/icoHomeGray.gif" alt="" title=""/>
          </a>
      </div>
      <div class="splitter">></div>
        <div class="active"><fmt:message key="index.product"/></div>
    </div>
    
    <div class="lightBg azSwitcher">
      <a href="brandIndex.jsp" class="arrowedLink darkBlue_whiteArrow"><fmt:message key="index.brand"/></a>
    </div>
    
    <dsp:droplet name="/com/castorama/droplet/IndexedMapDroplet">
      <dsp:param name="indexedItem" value="category" />                                   
      <dsp:oparam name="output">
        <dsp:include page="includes/index.jsp">
          <dsp:param name="indexedMap" param="indexedMap"/>
          <dsp:param name="indexedItem" value="category" /> 
        </dsp:include>
      </dsp:oparam>
    </dsp:droplet>    

    </jsp:attribute>
  </cast:pageContainer>
</dsp:page>