<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns:lxslt="http://xml.apache.org/xslt"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                xmlns:xlink="http://www.w3.org/1999/xlink">

    <xsl:template name="printrow">
        <fo:table-row  height="20pt" >
            <fo:table-cell xsl:use-attribute-sets="table-cell-content-border table-cell-border-left-bold">
                <fo:block xsl:use-attribute-sets="black-normal-10-center" text-align="left">
                    <xsl:value-of select="@code"/>
                </fo:block>
            </fo:table-cell>
            <fo:table-cell xsl:use-attribute-sets="table-cell-content-border">
                <fo:block xsl:use-attribute-sets="black-normal-10-center" text-align="left">
                    <xsl:value-of select="@name"/>
                </fo:block>
                <fo:block space-before="3mm"/>
                <fo:block xsl:use-attribute-sets="black-normal-10-center-taxd3e" text-align="left">
                    <fo:block>  </fo:block><xsl:value-of select="@TAXD3E"/><fo:block/>
                </fo:block>
                <fo:block xsl:use-attribute-sets="black-normal-10-center-taxrep" text-align="left">
                    <fo:block>     </fo:block><xsl:value-of select="@TAXREP"/>
                </fo:block>
                <fo:block space-before="3mm"/>
            </fo:table-cell>
            <fo:table-cell xsl:use-attribute-sets="table-cell-content-border">
                <fo:block xsl:use-attribute-sets="black-normal-10-center" text-align="right"><xsl:value-of select="@price"/></fo:block>
            </fo:table-cell>
            <fo:table-cell xsl:use-attribute-sets="table-cell-content-border">
                <fo:block xsl:use-attribute-sets="black-normal-10-center" text-align="right"><xsl:value-of select="@taux_tva"/></fo:block>
            </fo:table-cell>
            <fo:table-cell xsl:use-attribute-sets="table-cell-content-border">
                <fo:block xsl:use-attribute-sets="black-normal-10-center" text-align="right"><xsl:value-of select="@count"/></fo:block>
            </fo:table-cell>
            <fo:table-cell xsl:use-attribute-sets="table-cell-content-border">
                <fo:block xsl:use-attribute-sets="black-normal-10-center" text-align="right"><xsl:value-of select="@total_taux_tva"/></fo:block>
            </fo:table-cell>
            <fo:table-cell xsl:use-attribute-sets="table-cell-border-right-bold">
                <fo:block xsl:use-attribute-sets="black-normal-10-center" text-align="right"><xsl:value-of select="@total_ttc"/></fo:block>
            </fo:table-cell>
        </fo:table-row>
    </xsl:template>

    <xsl:template name="printVATRow">
        <fo:table-row height="20pt">
            <fo:table-cell xsl:use-attribute-sets="table-cell-content-border table-cell-border-left-bold">
                <fo:block padding-top="3pt"/>
                <fo:block xsl:use-attribute-sets="black-normal-10-center"/>
            </fo:table-cell>
            <fo:table-cell xsl:use-attribute-sets="table-cell-content-border">
                <fo:block padding-top="3pt"/>
                <fo:block xsl:use-attribute-sets="black-normal-10-center" text-align="left">
                    Taux TVA à
                    <xsl:value-of select="@percent"/>
                </fo:block>
            </fo:table-cell>
            <fo:table-cell xsl:use-attribute-sets="table-cell-content-border">
                <fo:block padding-top="3pt"/>
                <fo:block xsl:use-attribute-sets="black-normal-10-center"/>
            </fo:table-cell>
            <fo:table-cell xsl:use-attribute-sets="table-cell-content-border">
                <fo:block padding-top="3pt"/>
                <fo:block xsl:use-attribute-sets="black-normal-10-center"/>
            </fo:table-cell>
            <fo:table-cell xsl:use-attribute-sets="table-cell-content-border" number-columns-spanned="2">
                <fo:block padding-top="3pt"/>
                <fo:block xsl:use-attribute-sets="black-normal-10-center" text-align="right">
                    <xsl:value-of select="@total"/>
                </fo:block>
            </fo:table-cell>
            <fo:table-cell xsl:use-attribute-sets="table-cell-content-border table-cell-border-right-bold">
                <fo:block padding-top="3pt"/>
                <fo:block xsl:use-attribute-sets="black-normal-10-center"/>
            </fo:table-cell>
        </fo:table-row>
    </xsl:template>

    <xsl:template name="print_invoice">
        <fo:table xsl:use-attribute-sets="table-tight">
            <fo:table-column/>
            <fo:table-body>
                <fo:table-row>
                    <fo:table-cell padding-left="12px">
                        <fo:block padding-top="30px"/>

                        <fo:table xsl:use-attribute-sets="table-tight-border">
                            <fo:table-column column-width="proportional-column-width(50%)"/>
                            <fo:table-column column-width="proportional-column-width(50%)"/>
                            <fo:table-body>
                                <fo:table-row>
                                    <fo:table-cell text-align="left">
                                        <fo:block text-align="left" xsl:use-attribute-sets="black-courier-bold-12">Numéro de client: <xsl:value-of select="@client_id"/></fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell text-align="left">
                                        <fo:block text-align="left" xsl:use-attribute-sets="black-courier-bold-12">
                                            TEMPLEMARS, le <xsl:value-of select="@date"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </fo:table-body>
                        </fo:table>
                        <fo:block padding-top="30px"/>

                        <fo:table xsl:use-attribute-sets="table-tight-border">
                            <fo:table-column column-width="proportional-column-width(50%)"/>
                            <fo:table-column column-width="proportional-column-width(50%)"/>
                            <fo:table-body>
                                <fo:table-row>
                                    <fo:table-cell text-align="left">
                                        <fo:block text-align="left" xsl:use-attribute-sets="black-courier-bold-12">Adresse de Livraison : </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell text-align="left">
                                        <fo:block text-align="left" xsl:use-attribute-sets="black-courier-bold-12">Adresse de Facturation :</fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row>
                                    <fo:table-cell text-align="left">
                                        <fo:block padding-top="10px"/>
                                    </fo:table-cell>
                                    <fo:table-cell text-align="left">
                                        <fo:block padding-top="10px"/>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row>
                                    <fo:table-cell text-align="left">
                                        <fo:block text-align="left" xsl:use-attribute-sets="black-courier-bold-12"><xsl:value-of select="shipping_address/@name"/></fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell text-align="left">
                                        <fo:block text-align="left" xsl:use-attribute-sets="black-courier-bold-12"><xsl:value-of select="billing_address/@name"/></fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row>
                                    <fo:table-cell text-align="left">
                                        <fo:block text-align="left" xsl:use-attribute-sets="black-courier-bold-12"><xsl:value-of select="shipping_address/@street1"/> <xsl:value-of select="shipping_address/@street2"/></fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell text-align="left">
                                        <fo:block text-align="left" xsl:use-attribute-sets="black-courier-bold-12"><xsl:value-of select="billing_address/@street1"/> <xsl:value-of select="billing_address/@street2"/></fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row>
                                    <fo:table-cell text-align="left">
                                        <fo:block text-align="left" xsl:use-attribute-sets="black-courier-bold-12"><xsl:value-of select="shipping_address/@street2"/> <xsl:value-of select="shipping_address/@street2"/></fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell text-align="left">
                                        <fo:block text-align="left" xsl:use-attribute-sets="black-courier-bold-12"><xsl:value-of select="billing_address/@street2"/> <xsl:value-of select="billing_address/@street2"/></fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row>
                                    <fo:table-cell text-align="left">
                                        <fo:block text-align="left" xsl:use-attribute-sets="black-courier-bold-12"><xsl:value-of select="shipping_address/@street3"/> <xsl:value-of select="shipping_address/@street2"/></fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell text-align="left">
                                        <fo:block text-align="left" xsl:use-attribute-sets="black-courier-bold-12"><xsl:value-of select="billing_address/@street3"/> <xsl:value-of select="billing_address/@street2"/></fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row>
                                    <fo:table-cell text-align="left">
                                        <fo:block padding-top="20px"/>
                                    </fo:table-cell>
                                    <fo:table-cell text-align="left">
                                        <fo:block padding-top="20px"/>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row>
                                    <fo:table-cell text-align="left">
                                        <fo:block text-align="left" xsl:use-attribute-sets="black-courier-bold-12"><xsl:value-of select="shipping_address/@zip"/> <xsl:value-of select="shipping_address/@city"/></fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell text-align="left">
                                        <fo:block text-align="left" xsl:use-attribute-sets="black-courier-bold-12"><xsl:value-of select="billing_address/@zip"/> <xsl:value-of select="billing_address/@city"/></fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row>
                                    <fo:table-cell text-align="left">
                                        <fo:block text-align="left" xsl:use-attribute-sets="black-courier-bold-12"><xsl:value-of select="shipping_address/@country"/></fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell text-align="left">
                                        <fo:block text-align="left" xsl:use-attribute-sets="black-courier-bold-12"><xsl:value-of select="billing_address/@country"/></fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </fo:table-body>
                        </fo:table>

                        <fo:block padding-top="30px"/>
                        <fo:table xsl:use-attribute-sets="table-tight-border">
                            <fo:table-column column-width="proportional-column-width(100%)"/>
                            <fo:table-body>
                                <fo:table-row>
                                    <fo:table-cell text-align="center">
                                        <fo:block text-align="center" xsl:use-attribute-sets="black-bold-20">FACTURE N° <xsl:value-of select="@facture_id"/></fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </fo:table-body>
                        </fo:table>

                        <fo:block padding-top="20px"/>
                        <fo:table xsl:use-attribute-sets="table-tight-border">
                            <fo:table-column column-width="proportional-column-width(100%)"/>
                            <fo:table-body>
                                <fo:table-row>
                                    <fo:table-cell text-align="left">
                                        <fo:block text-align="left" xsl:use-attribute-sets="black-courier-bold-12">N° d'ordre de livraison : <xsl:value-of select="@facture"/></fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row>
                                    <fo:table-cell text-align="left">
                                        <fo:block text-align="left" xsl:use-attribute-sets="black-courier-bold-12">Référence de commande : <xsl:value-of select="@reference"/></fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </fo:table-body>
                        </fo:table>

                        <fo:block padding-top="20px"/>

                        <fo:table xsl:use-attribute-sets="table-tight-border">
                            <fo:table-column column-width="57px"/>
                            <fo:table-column column-width="212px"/>
                            <fo:table-column column-width="66px"/>
                            <fo:table-column column-width="45px"/>
                            <fo:table-column column-width="49px"/>
                            <fo:table-column column-width="60px"/>
                            <fo:table-column column-width="70px"/>
                            <fo:table-body>
                                <fo:table-row height="30pt" >
                                    <fo:table-cell xsl:use-attribute-sets="table-cell-border-left-top-bottom">
                                        <fo:block padding-top="5pt"/>
                                        <fo:block xsl:use-attribute-sets="black-courier-bold-10" text-align="center">Code article</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell xsl:use-attribute-sets="table-cell-border-top-bottom">
                                        <fo:block padding-top="5pt"/>
                                        <fo:block xsl:use-attribute-sets="black-courier-bold-10" text-align="center">Désignation article</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell xsl:use-attribute-sets="table-cell-border-top-bottom">
                                        <fo:block padding-top="5pt"/>
                                        <fo:block xsl:use-attribute-sets="black-courier-bold-10" text-align="center">PU H.T EUR</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell xsl:use-attribute-sets="table-cell-border-top-bottom">
                                        <fo:block padding-top="5pt"/>
                                        <fo:block xsl:use-attribute-sets="black-courier-bold-10" text-align="center">Taux TVA</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell xsl:use-attribute-sets="table-cell-border-top-bottom">
                                        <fo:block padding-top="5pt"/>
                                        <fo:block xsl:use-attribute-sets="black-courier-bold-10" text-align="center">Qté</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell xsl:use-attribute-sets="table-cell-border-top-bottom">
                                        <fo:block padding-top="5pt"/>
                                        <fo:block xsl:use-attribute-sets="black-courier-bold-10" text-align="center">Total TVA EUR</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell xsl:use-attribute-sets="table-cell-border-right-top-bottom">
                                        <fo:block padding-top="5pt"/>
                                        <fo:block xsl:use-attribute-sets="black-courier-bold-10" text-align="center">Total TTC EUR</fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row  height="5pt" >
                                    <fo:table-cell xsl:use-attribute-sets="table-cell-content-border table-cell-border-left-bold">
                                    </fo:table-cell>
                                    <fo:table-cell xsl:use-attribute-sets="table-cell-content-border">
                                    </fo:table-cell>
                                    <fo:table-cell xsl:use-attribute-sets="table-cell-content-border">
                                    </fo:table-cell>
                                    <fo:table-cell xsl:use-attribute-sets="table-cell-content-border">
                                    </fo:table-cell>
                                    <fo:table-cell xsl:use-attribute-sets="table-cell-content-border">
                                    </fo:table-cell>
                                    <fo:table-cell xsl:use-attribute-sets="table-cell-content-border">
                                    </fo:table-cell>
                                    <fo:table-cell xsl:use-attribute-sets="table-cell-border-right-bold">
                                    </fo:table-cell>
                                </fo:table-row>
                                <xsl:for-each select="items/item[@code]">
                                    <xsl:call-template name="printrow"/>
                                </xsl:for-each>
                                <xsl:for-each select="items/item[not(@code)]">
                                    <xsl:call-template name="printrow"/>
                                </xsl:for-each>
                                <fo:table-row  height="5pt" >
                                    <fo:table-cell xsl:use-attribute-sets="table-cell-border-bottom table-cell-border-left-bold">
                                    </fo:table-cell>
                                    <fo:table-cell xsl:use-attribute-sets="table-cell-border-bottom">
                                    </fo:table-cell>
                                    <fo:table-cell xsl:use-attribute-sets="table-cell-border-bottom">
                                    </fo:table-cell>
                                    <fo:table-cell xsl:use-attribute-sets="table-cell-border-bottom">
                                    </fo:table-cell>
                                    <fo:table-cell xsl:use-attribute-sets="table-cell-border-bottom">
                                    </fo:table-cell>
                                    <fo:table-cell xsl:use-attribute-sets="table-cell-border-bottom">
                                    </fo:table-cell>
                                    <fo:table-cell xsl:use-attribute-sets="table-cell-border-bottom table-cell-border-right-bold">
                                    </fo:table-cell>
                                </fo:table-row>
                                <xsl:for-each select="vats/taux_tva">
                                    <xsl:call-template name="printVATRow"/>
                                </xsl:for-each>
                                <fo:table-row height="20pt">
                                    <fo:table-cell xsl:use-attribute-sets="table-cell-border-left-top">
                                        <fo:block padding-top="5pt"/>
                                        <fo:block xsl:use-attribute-sets="black-normal-10-center"/>
                                    </fo:table-cell>
                                    <fo:table-cell xsl:use-attribute-sets="table-cell-border-top">
                                        <fo:block padding-top="5pt"/>
                                        <fo:block xsl:use-attribute-sets="black-normal-10-center" text-align="left">Total HT en EUR:</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell xsl:use-attribute-sets="table-cell-border-top" background-color="#C0C0C0">
                                        <fo:block padding-top="5pt"/>
                                        <fo:block xsl:use-attribute-sets="black-normal-10-center" text-align="right"><xsl:value-of select="total/@total_ht"/></fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell xsl:use-attribute-sets="table-cell-border-top">
                                        <fo:block padding-top="5pt"/>
                                        <fo:block xsl:use-attribute-sets="black-normal-10-center"/>
                                    </fo:table-cell>
                                    <fo:table-cell xsl:use-attribute-sets="table-cell-border-top" number-columns-spanned="2">
                                        <fo:block padding-top="5pt"/>
                                        <fo:block xsl:use-attribute-sets="black-normal-10-center" text-align="left">Total TTC en EUR:</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell xsl:use-attribute-sets="table-cell-border-right-top" background-color="#C0C0C0">
                                        <fo:block padding-top="5pt"/>
                                        <fo:block xsl:use-attribute-sets="black-normal-10-center" text-align="right"><xsl:value-of select="total/@total_ttc"/></fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row height="20pt">
                                    <fo:table-cell xsl:use-attribute-sets="table-cell-border-left-bottom">
                                        <fo:block padding-top="5pt"/>
                                        <fo:block xsl:use-attribute-sets="black-normal-10-center"/>
                                    </fo:table-cell>
                                    <fo:table-cell xsl:use-attribute-sets="table-cell-border-bottom">
                                        <fo:block padding-top="5pt"/>
                                        <fo:block xsl:use-attribute-sets="black-normal-10-center" text-align="left">Total TVA en EUR:</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell xsl:use-attribute-sets="table-cell-border-bottom" background-color="#C0C0C0">
                                        <fo:block padding-top="5pt"/>
                                        <fo:block xsl:use-attribute-sets="black-normal-10-center" text-align="right"><xsl:value-of select="total/@total_tva"/></fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell xsl:use-attribute-sets="table-cell-border-bottom">
                                        <fo:block padding-top="5pt"/>
                                        <fo:block xsl:use-attribute-sets="black-normal-10-center"/>
                                    </fo:table-cell>
                                    <fo:table-cell xsl:use-attribute-sets="table-cell-border-bottom" number-columns-spanned="2">
                                        <fo:block padding-top="5pt"/>
                                        <fo:block xsl:use-attribute-sets="black-normal-10-center"/>
                                    </fo:table-cell>
                                    <fo:table-cell xsl:use-attribute-sets="table-cell-border-right-bottom" background-color="#C0C0C0">
                                        <fo:block padding-top="5pt"/>
                                        <fo:block xsl:use-attribute-sets="black-normal-10-center" />
                                    </fo:table-cell>
                                </fo:table-row>
                            </fo:table-body>
                        </fo:table>
                    </fo:table-cell>
                </fo:table-row>
            </fo:table-body>
        </fo:table>
    </xsl:template>
</xsl:stylesheet>