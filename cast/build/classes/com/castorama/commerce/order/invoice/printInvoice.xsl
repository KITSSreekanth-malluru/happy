<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns:lxslt="http://xml.apache.org/xslt"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                xmlns:xlink="http://www.w3.org/1999/xlink">


    <xsl:param name="url"/>

    <xsl:variable name="basePath" select="$url"/>

    <xsl:template match="invoice">
        <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master page-width="8.26in" page-height="11.42in" master-name="common" margin-right="0.4in" margin-left="0.1in" margin-top="0.1in" margin-bottom="0.2in">
                <fo:region-body margin-top="2cm" margin-bottom="2cm"/>
                <fo:region-before region-name="before-region" extent="2cm"/>
                <fo:region-after region-name="after-region" extent="2cm"/>
                <fo:region-start region-name="start-region" extent="1pt"/>
                <fo:region-end region-name="end-region" extent="1pt"/>
            </fo:simple-page-master>
            <fo:page-sequence-master master-name="repeat_common">
                <fo:repeatable-page-master-reference master-reference="common"/>
            </fo:page-sequence-master>
        </fo:layout-master-set>
        <fo:page-sequence master-reference="repeat_common">
            <fo:static-content flow-name="before-region">
                <xsl:call-template name="header"/>
            </fo:static-content>
            <fo:static-content flow-name="after-region">
                <xsl:call-template name="footer"/>
                <xsl:call-template name="pageNumber"/>
            </fo:static-content>
            <fo:static-content flow-name="start-region">
                <!-- xsl:call-template name="borderVertical"/-->
            </fo:static-content>
            <fo:static-content flow-name="end-region">
                <!--xsl:call-template name="borderVertical"/-->
            </fo:static-content>
            <fo:flow flow-name="xsl-region-body">
                <xsl:call-template name="print_invoice"/>
                <xsl:call-template name="invoiceInfo"/>
                <fo:block id="lastBlock"/>
            </fo:flow>
        </fo:page-sequence>

        </fo:root>
    </xsl:template>

    <xsl:template name="header">
    </xsl:template>

    <xsl:template name="invoiceInfo">
    </xsl:template>

    <xsl:template name="footer">
    </xsl:template>

    <xsl:template name="pageNumber">
        <fo:table xsl:use-attribute-sets="table-tight">
            <fo:table-column column-width="proportional-column-width(100%)"/>
            <fo:table-body>
                <fo:table-row>
                    <fo:table-cell text-align="center">
                        <fo:block padding-top="10px"/>
                        <fo:block text-align="center" xsl:use-attribute-sets="black-normal-7">Page <fo:page-number/> / <fo:page-number-citation ref-id="lastBlock"/></fo:block>
                    </fo:table-cell>
                </fo:table-row>
            </fo:table-body>
        </fo:table>
    </xsl:template>

    <xsl:include href="invoice.xsl"/>
    <xsl:include href="formats.xsl"/>
</xsl:stylesheet>
