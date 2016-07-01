<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns:lxslt="http://xml.apache.org/xslt"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                xmlns:xlink="http://www.w3.org/1999/xlink">


  <xsl:param name="url"/>

  <xsl:variable name="basePath" select="$url"/>

  <xsl:variable name="productsWithDiscounts" select="count(/ticket/items/item[count(./discount) > 0])"/>
  <xsl:variable name="productsWith2LinesInLabel" select="count(/ticket/items/item[@productLabelLines = 2])"/>
  <xsl:variable name="productsWith3LinesInLabel" select="count(/ticket/items/item[@productLabelLines = 3])"/>
  <xsl:variable name="reqularProducts" select="count(/ticket/items/item) - $productsWith2LinesInLabel - $productsWith3LinesInLabel"/>
  <xsl:variable name="discountsAndPayments" select="count(/ticket/discounts/discount) + count(/ticket/payments/method)"/>
  <xsl:variable name="tvas" select="count(/ticket/tvas/tva)"/>
  
  <!-- Fix for Mantis 0001924 'Display long ticket on one page'.
       This part is very tricky - we need to estimate the size of the ticket data to display.
       EstimatedSize variable holds the sum of length of all areas(logo, store info, ticket lines, blank space, etc).  -->
  <xsl:variable name="estimatedSize" select="40 + 176 + 9 + $reqularProducts * 28 + $productsWith2LinesInLabel * 36 + $productsWith3LinesInLabel * 42 +
  $productsWithDiscounts * 10 - 8 + 22 + 5 + $discountsAndPayments * 10 + 15 + 25 + $tvas * 10 + 50 + 20"/>
  
  
  <xsl:template match="ticket">
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">

    <fo:layout-master-set>

      <xsl:choose>
        <xsl:when test="$estimatedSize &lt; 770">
          <fo:simple-page-master master-name="repeat_common"
                                 page-width="8.5in"
                                 page-height="11in"
                                 margin-right="2.915in"
                                 margin-left="2.915in"
                                 margin-top="0.1in"
                                 margin-bottom="0.1in" >
            <fo:region-body/>
          </fo:simple-page-master>
        </xsl:when>
        <xsl:otherwise>
          <fo:simple-page-master master-name="repeat_common"
                                 page-height="11in" 
                                 page-width="8.5in"
                                 margin-top="0.1in" 
                                 margin-bottom="0.1in" 
                                 margin-left="0.75in" 
                                 margin-right="0.75in">

            <fo:region-body column-count="2" column-gap="1.66in"/>
          </fo:simple-page-master>
        </xsl:otherwise>
      </xsl:choose>

    </fo:layout-master-set>
  
    <fo:page-sequence master-reference="repeat_common">
      <fo:flow flow-name="xsl-region-body">
        <fo:block border-style="solid" border-width="1px" background-image="{$url}/images/pdf/ticket-background.gif" background-repeat="repeat-y">
          <xsl:call-template name="parse-ticket"/>
        </fo:block>
      </fo:flow>
    </fo:page-sequence>
    
    </fo:root>
  </xsl:template>

  <xsl:include href="ticket.xsl"/>
  <xsl:include href="ticket-formats.xsl"/>
</xsl:stylesheet>

