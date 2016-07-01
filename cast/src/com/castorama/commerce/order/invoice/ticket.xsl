<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns:lxslt="http://xml.apache.org/xslt"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                xmlns:xlink="http://www.w3.org/1999/xlink">

  <xsl:template name="parse-ticket">
    <fo:block >
      <fo:external-graphic src="{$url}/images/pdf/casto-logo.gif"/>
    </fo:block>
    <fo:block/>
    <fo:table xsl:use-attribute-sets="table-tight">
      <fo:table-column column-width="proportional-column-width(100%)"/>
      <fo:table-body>
        <fo:table-row>
          <fo:table-cell text-align="center">
            <xsl:if test="count(store) > 0">
              <fo:block xsl:use-attribute-sets="store-blue-normal-8">CASTORAMA&#160;<xsl:value-of select="store/@name"/></fo:block>
              <fo:block xsl:use-attribute-sets="store-blue-normal-8"><xsl:value-of select="store/address/street/line1/text()"/></fo:block>
              <fo:block xsl:use-attribute-sets="store-blue-normal-8"><xsl:value-of select="store/address/street/line2/text()"/></fo:block>
              <fo:block xsl:use-attribute-sets="store-blue-normal-8"><xsl:value-of select="store/address/postal-code/text()"/>&#160;<xsl:value-of select="store/address/city/text()"/></fo:block>
              <xsl:if test="normalize-space(store/address/tel/text()) != ''">
                <fo:block xsl:use-attribute-sets="store-blue-normal-8">TEL&#160;:&#160;<xsl:value-of select="store/address/tel/text()"/></fo:block>
              </xsl:if>
              <xsl:if test="normalize-space(store/address/fax/text()) != ''">
                <fo:block xsl:use-attribute-sets="store-blue-normal-8">FAX&#160;:&#160;<xsl:value-of select="store/address/fax/text()"/></fo:block>
              </xsl:if>
            </xsl:if>
            <xsl:if test="count(store) = 0">
              <fo:block padding-top="60px"/>
            </xsl:if>
            <xsl:if test="normalize-space(store/address/street/line1/text()) = ''">
              <fo:block padding-top="8px"/>
            </xsl:if>
            <xsl:if test="normalize-space(store/address/street/line2/text()) = ''">
              <fo:block padding-top="8px"/>
            </xsl:if>
            <xsl:if test="normalize-space(store/address/postal-code/text()) = ''">
              <fo:block padding-top="8px"/>
            </xsl:if>
            <xsl:if test="normalize-space(store/address/tel/text()) = ''">
              <fo:block padding-top="8px"/>
            </xsl:if>
            <xsl:if test="normalize-space(store/address/fax/text()) = ''">
              <fo:block padding-top="8px"/>
            </xsl:if>
            
            <fo:block text-align="left" padding-top="10px" margin-left="15px" margin-right="20px">
              <fo:block xsl:use-attribute-sets="information-text-black-bold-9">Ce document ne donne pas droit à</fo:block>
              <fo:block xsl:use-attribute-sets="information-text-black-bold-9">un remboursement de produit, ni</fo:block>
              <fo:block xsl:use-attribute-sets="information-text-black-bold-9">échange de produit, mais peut servir</fo:block>
              <fo:block xsl:use-attribute-sets="information-text-black-bold-9">de justificatif dans le cadre d’une</fo:block>
              <fo:block xsl:use-attribute-sets="information-text-black-bold-9">garantie.</fo:block>
            </fo:block>
            <fo:block padding-top="20px"/>
            <fo:block xsl:use-attribute-sets="user-line-blue-normal-7">Porteur&#160;:&#160;<xsl:value-of select="user/@sofincoName1"/>&#160;<xsl:value-of select="user/@sofincoName2"/></fo:block>
          </fo:table-cell>
        </fo:table-row>
      </fo:table-body>
    </fo:table>
    
    <fo:block padding-top="15px"/>
    
    <fo:table xsl:use-attribute-sets="table-tight">
      <fo:table-column column-width="proportional-column-width(15%)"/>
      <fo:table-column column-width="proportional-column-width(50%)"/>
      <fo:table-column column-width="proportional-column-width(14%)"/>
      <fo:table-column column-width="proportional-column-width(21%)"/>

      <fo:table-body>
        <fo:table-row >
          <fo:table-cell text-align="left" padding-left="15px">
            <fo:block xsl:use-attribute-sets="blue-normal-7">QTE&#160;</fo:block>
          </fo:table-cell>
          <fo:table-cell text-align="left">
              <fo:block  xsl:use-attribute-sets="blue-normal-7">&#160;DESCRIPTION</fo:block>
          </fo:table-cell>
          <fo:table-cell text-align="left">
              <fo:block xsl:use-attribute-sets="blue-normal-7">PRIX</fo:block>
          </fo:table-cell>
          <fo:table-cell text-align="left">
              <fo:block xsl:use-attribute-sets="blue-normal-7">MONTANT</fo:block>
          </fo:table-cell>
        </fo:table-row>

        <xsl:for-each select="items/item">
          <xsl:sort select="@lineNumber" data-type="number"/>
          <xsl:call-template name="ticketLine"/>
        </xsl:for-each>
        
        <!-- Removed: See Mantis 0001968: [Purchase History] Delete line 'SOUS-TOTAL' on ticket  
        <fo:table-row height="15px">
          <fo:table-cell/>
          <fo:table-cell display-align="after" text-align="left">
              <fo:block xsl:use-attribute-sets="blue-normal-7">SOUS-TOTAL</fo:block>
          </fo:table-cell>
          <fo:table-cell display-align="after" text-align="left">
              <fo:block font-size="12pt" font-weight="900" xsl:use-attribute-sets="blue-normal-7"><xsl:value-of select="@sousTotal"/></fo:block>
          </fo:table-cell>
          <fo:table-cell/>
        </fo:table-row>
        -->
        
        <fo:table-row height="15px">
          <fo:table-cell/>
          <fo:table-cell display-align="after" text-align="left">
              <fo:block xsl:use-attribute-sets="blue-normal-7">TOTAL</fo:block>
          </fo:table-cell>
          <fo:table-cell display-align="after" text-align="left">
              <fo:block font-size="12pt" font-weight="900" xsl:use-attribute-sets="blue-normal-7"><xsl:value-of select="@totalPriceTTC"/></fo:block>
          </fo:table-cell>
          <fo:table-cell/>
        </fo:table-row>

        <fo:table-row height="5px">
          <fo:table-cell><fo:block font-size="8pt">&#160;</fo:block></fo:table-cell>
        </fo:table-row>
        <!--  See 0001969: [Purchase History] Add 'Remise perso' on ticket line with DISCOUNT_AMOUNT 
        <xsl:for-each select="discounts/discount">
          <xsl:sort select="@discountLabel" data-type="text"/>
          <xsl:call-template name="ticketDiscount"/>
        </xsl:for-each>
        -->
        <xsl:for-each select="payments/method">
          <xsl:sort select="@paymentLabel" data-type="text"/>
          <xsl:call-template name="paymentMethod"/>
        </xsl:for-each>

        <fo:table-row height="5px">
          <fo:table-cell><fo:block font-size="8pt">&#160;</fo:block></fo:table-cell>
        </fo:table-row>

        <xsl:for-each select="rendu">
          <xsl:call-template name="rendu"/>
        </xsl:for-each>

      </fo:table-body>
    </fo:table>

    <fo:block padding-top="20px"/>

    <fo:table xsl:use-attribute-sets="table-tight">
      <fo:table-column column-width="proportional-column-width(40%)"/>
      <fo:table-column column-width="proportional-column-width(25%)"/>
      <fo:table-column column-width="proportional-column-width(35%)"/>
      <fo:table-body>
        <fo:table-row>
          <fo:table-cell text-align="left" padding-left="15px">
            <fo:block xsl:use-attribute-sets="blue-normal-7">DETAIL TVA</fo:block>
          </fo:table-cell>
           <fo:table-cell/>
           <fo:table-cell/>
        </fo:table-row>
        <xsl:for-each select="tvas/tva">
          <xsl:call-template name="tvaLine"/>
        </xsl:for-each>
      </fo:table-body>
    </fo:table>

    <fo:block padding-top="15px" padding-bottom="25px" margin-left="15px" text-align="left" xsl:use-attribute-sets="blue-normal-7">
      NOMBRE D'ARTICLES &#160;: &#160;&#160;<xsl:value-of select="@totalQuantity"/>
    </fo:block>

    <fo:table xsl:use-attribute-sets="table-tight">
      <fo:table-column column-width="proportional-column-width(27%)"/>
      <fo:table-column column-width="proportional-column-width(22%)"/>
      <fo:table-column column-width="proportional-column-width(15%)"/>
      <fo:table-column column-width="proportional-column-width(5%)"/>
      <fo:table-column column-width="proportional-column-width(7%)"/>
      <fo:table-column column-width="proportional-column-width(23%)"/>
      <fo:table-body>
        <fo:table-row>
          <fo:table-cell text-align="left" padding-left="15px">
            <fo:block xsl:use-attribute-sets="blue-normal-7"><xsl:value-of select="@date"/></fo:block>
          </fo:table-cell>
          <fo:table-cell text-align="center">
            <fo:block xsl:use-attribute-sets="blue-normal-7"><xsl:value-of select="@time"/></fo:block>
          </fo:table-cell>
          <fo:table-cell text-align="center">
            <fo:block xsl:use-attribute-sets="blue-normal-7"><xsl:value-of select="@storeId"/></fo:block>
          </fo:table-cell>
          <fo:table-cell padding-right="3px" text-align="right">
            <fo:block xsl:use-attribute-sets="blue-normal-7"><xsl:value-of select="@tillId"/></fo:block>
          </fo:table-cell>
          <fo:table-cell padding-right="3px" text-align="right">
            <fo:block  xsl:use-attribute-sets="blue-normal-7"><xsl:value-of select="@hostessId"/></fo:block>
          </fo:table-cell>
          <fo:table-cell text-align="left">
            <fo:block xsl:use-attribute-sets="blue-normal-7"><xsl:value-of select="@ticketId"/></fo:block>
          </fo:table-cell>
        </fo:table-row>
      </fo:table-body>
    </fo:table>
    
    <fo:block padding-top="15px"/>
  </xsl:template>
  
  <!--  
      Ticket Line Template
  -->
  
  <xsl:template name="ticketLine">
  
    <fo:table-row>
      <fo:table-cell text-align="right">
        <fo:block padding-top="2px" xsl:use-attribute-sets="blue-normal-7"><xsl:value-of select="./@productQuantity"/>&#160;</fo:block>
      </fo:table-cell>
      <fo:table-cell hyphenate="true" text-align="left">
        <fo:block language="fr" hyphenate="false" padding-top="2px" xsl:use-attribute-sets="blue-normal-7"><xsl:value-of select="./@productLabel"/></fo:block>
      </fo:table-cell>
      
      <!-- 0001975: [Purchase History] Don't display word 'Ref' if product id isn't existing in ticket line  -->
      <xsl:if test="normalize-space(./@productRef) = ''">
        <fo:table-cell text-align="left">
            <fo:block padding-top="2px" xsl:use-attribute-sets="blue-normal-7">&#160;<xsl:value-of select="./@unitPriceTTC"/></fo:block>
        </fo:table-cell>
        <fo:table-cell text-align="left">
          <fo:block  padding-top="2px" xsl:use-attribute-sets="blue-normal-7">&#160;&#160;<xsl:value-of select="./@amount"/></fo:block>
        </fo:table-cell>
      </xsl:if>
    </fo:table-row>

    <xsl:if test="normalize-space(./@productRef) != ''">
      <fo:table-row >
        <fo:table-cell/>
        <fo:table-cell text-align="left">
          <fo:block padding-top="2px" xsl:use-attribute-sets="blue-normal-7">
              <fo:inline font-style="italic">réf &#160;</fo:inline><xsl:value-of select="./@productRef"/>
          </fo:block>
        </fo:table-cell>
        <fo:table-cell text-align="left">
            <fo:block padding-top="2px" xsl:use-attribute-sets="blue-normal-7">&#160;<xsl:value-of select="./@unitPriceTTC"/></fo:block>
        </fo:table-cell>
        <fo:table-cell text-align="left">
          <fo:block  padding-top="2px" xsl:use-attribute-sets="blue-normal-7">&#160;&#160;<xsl:value-of select="./@amount"/></fo:block>
        </fo:table-cell>
      </fo:table-row>
    </xsl:if>


    <xsl:if test="count(./discount) > 0">
      <fo:table-row>
        <fo:table-cell/>
        <fo:table-cell text-align="left">
           <fo:block padding-top="2px" xsl:use-attribute-sets="blue-normal-7">REMISE PERSO</fo:block>
        </fo:table-cell>
        <fo:table-cell text-align="left">
           <fo:block padding-top="2px" xsl:use-attribute-sets="blue-normal-7">&#160;<xsl:value-of select="./discount/@discountAmount"/></fo:block>
        </fo:table-cell>
        <fo:table-cell/>
      </fo:table-row>
   
    </xsl:if>
   
    <fo:table-row height="5px">
      <fo:table-cell><fo:block font-size="8pt">&#160;</fo:block></fo:table-cell>
    </fo:table-row>

  </xsl:template>
  
  <!--  
      TVA Template
  -->
  
  <xsl:template name="tvaLine">
  
    <fo:table-row>
      <fo:table-cell text-align="left" padding-left="17px">
        <fo:block padding-top="2px" xsl:use-attribute-sets="blue-normal-7">TVA&#160;&#160;<xsl:value-of select="@tvaRate"/>%</fo:block>
      </fo:table-cell>
      <fo:table-cell text-align="left">
        <fo:block padding-top="2px" xsl:use-attribute-sets="blue-normal-7"><xsl:value-of select="@diff"/></fo:block>
      </fo:table-cell>
      <fo:table-cell text-align="left">
        <fo:block padding-top="2px" xsl:use-attribute-sets="blue-normal-7"><xsl:value-of select="@totalHT"/>&#160;HT</fo:block>
      </fo:table-cell>
    </fo:table-row>
  
  </xsl:template>

  <!--  
      Ticket Discounts
  -->

  <xsl:template name="ticketDiscount">
  
    <fo:table-row height="10px">
      <fo:table-cell/>
      <fo:table-cell display-align="after" text-align="left">
          <fo:block xsl:use-attribute-sets="blue-normal-7"><xsl:value-of select="@discountLabel"/></fo:block>
      </fo:table-cell>
      <fo:table-cell/>
      <fo:table-cell display-align="after" text-align="left">
        <fo:block xsl:use-attribute-sets="blue-normal-7"><xsl:value-of select="@discountAmount"/></fo:block>
      </fo:table-cell>
    </fo:table-row>
  
  </xsl:template>

  <!--  
      Payment Method
  -->

  <xsl:template name="paymentMethod">

    <fo:table-row height="10px">
      <fo:table-cell/>
      <fo:table-cell display-align="after" text-align="left">
        <fo:block xsl:use-attribute-sets="blue-normal-7"><xsl:value-of select="@paymentLabel"/></fo:block>
      </fo:table-cell>
      <fo:table-cell/>
      <fo:table-cell display-align="after" text-align="left">
        <fo:block xsl:use-attribute-sets="blue-normal-7"><xsl:value-of select="@paymentAmmount"/></fo:block>
      </fo:table-cell>
    </fo:table-row>
  
  </xsl:template>

  <!--  
      RENDU line
      0001970: [Purchase History] Add line RENDU on ticket 
  -->

  <xsl:template name="rendu">

    <fo:table-row height="10px">
      <fo:table-cell/>
      <fo:table-cell display-align="after" text-align="left">
        <fo:block xsl:use-attribute-sets="blue-normal-7">RENDU</fo:block>
      </fo:table-cell>
      <fo:table-cell/>
      <fo:table-cell display-align="after" text-align="left">
        <fo:block xsl:use-attribute-sets="blue-normal-7"><xsl:value-of select="@value"/></fo:block>
      </fo:table-cell>
    </fo:table-row>
  
  </xsl:template>
  
</xsl:stylesheet>
