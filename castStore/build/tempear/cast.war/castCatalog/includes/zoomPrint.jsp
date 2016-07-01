<dsp:page>
  <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
  <html xmlns="http://www.w3.org/1999/xhtml">
    <head>
      <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
      <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/styles/main.css"/>
    </head>
    <body class="print productInfo">
      <dsp:importbean bean="/atg/commerce/catalog/SKULookup"/>
      <dsp:importbean bean="/atg/dynamo/droplet/Range"/>
      
      <dsp:getvalueof var="borderColor" param="borderColor"/>
      <dsp:getvalueof var="imageId" param="imageId"/>
      
      <fmt:message key="castCatalog_label.close" var="fermer"/>
      <fmt:message key="castCatalog_label.print" var="imprimer"/>
      <div class="productImageColumn">
        <div class="whitePopupContent popupFormContainer">
          <div class="whitePopupHeader">
            <dsp:a href="javascript:void(0)" onclick="window.close();" iclass="closeBut" title="${fermer}"><span><!--~--></span>${fermer}</dsp:a>
            <dsp:a href="javascript:void(0)" onclick="window.print();" iclass="imprimer" title="${imprimer}"><span><!--~--></span>${imprimer}</dsp:a>
          </div>
          <div class="clear"><!--~--></div>
          <div class="popupContentContainer">
            <div class="productLrgImg">
              <c:choose>
                <c:when test="${not empty imageId}">
                  <dsp:img id="popupImgLrgActive" src="${imageId}" iclass="${borderColor}"/>
                </c:when>
                <c:otherwise>
                  <dsp:img id="popupImgLrgActive" src="/default_images/h_no_img.jpg" iclass="${borderColor}"/>
                </c:otherwise>
              </c:choose>
            </div>
          </div>
        </div>
      </div>  
    </body>
  </html>
</dsp:page>