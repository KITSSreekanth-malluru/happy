<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <!-- Table tight body   -->
    <xsl:attribute-set name="table-tight">
        <xsl:attribute name="table-layout">fixed</xsl:attribute>
        <xsl:attribute name="width">100%</xsl:attribute>
        <xsl:attribute name="padding">0pt</xsl:attribute>
        <xsl:attribute name="margin">0pt</xsl:attribute>
        <xsl:attribute name="border">0pt</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="logo-cell">
        <xsl:attribute name="table-layout">fixed</xsl:attribute>
        <xsl:attribute name="padding-left">20pt</xsl:attribute>
        <xsl:attribute name="text-align">left</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="table-tight-border">
        <xsl:attribute name="table-layout">fixed</xsl:attribute>
        <xsl:attribute name="width">100%</xsl:attribute>
        <xsl:attribute name="padding">1pt</xsl:attribute>
        <xsl:attribute name="margin">1pt</xsl:attribute>
        <xsl:attribute name="border">1pt</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="table-tight-fixed">
        <xsl:attribute name="table-layout">fixed</xsl:attribute>
        <xsl:attribute name="padding">0pt</xsl:attribute>
        <xsl:attribute name="margin">0pt</xsl:attribute>
        <xsl:attribute name="border">0pt</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="black-courier-bold-12">
        <xsl:attribute name="font-weight">bold</xsl:attribute>
        <xsl:attribute name="font-size">12pt</xsl:attribute>
        <xsl:attribute name="text-align">left</xsl:attribute>
        <xsl:attribute name="font-family">Courier</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="black-courier-bold-10">
        <xsl:attribute name="font-weight">bold</xsl:attribute>
        <xsl:attribute name="font-size">10pt</xsl:attribute>
        <xsl:attribute name="text-align">left</xsl:attribute>
        <xsl:attribute name="font-family">Courier</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="black-normal-10-center">
        <xsl:attribute name="font-weight">normal</xsl:attribute>
        <xsl:attribute name="font-size">10pt</xsl:attribute>
        <xsl:attribute name="text-align">center</xsl:attribute>
        <xsl:attribute name="font-family">Times</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="black-normal-10-center-taxd3e">
        <xsl:attribute name="font-weight">normal</xsl:attribute>
        <xsl:attribute name="font-size">10pt</xsl:attribute>
        <xsl:attribute name="text-align">center</xsl:attribute>
        <xsl:attribute name="font-family">Times</xsl:attribute>
        <xsl:attribute name="start-indent">8mm</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="black-normal-10-center-taxrep">
        <xsl:attribute name="font-weight">normal</xsl:attribute>
        <xsl:attribute name="font-size">10pt</xsl:attribute>
        <xsl:attribute name="text-align">center</xsl:attribute>
        <xsl:attribute name="font-family">Times</xsl:attribute>
        <xsl:attribute name="start-indent">17mm</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="black-bold-10">
        <xsl:attribute name="font-weight">bold</xsl:attribute>
        <xsl:attribute name="font-size">10pt</xsl:attribute>
        <xsl:attribute name="text-align">left</xsl:attribute>
        <xsl:attribute name="font-family">Times</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="black-normal-11">
        <xsl:attribute name="font-weight">normal</xsl:attribute>
        <xsl:attribute name="font-size">11pt</xsl:attribute>
        <xsl:attribute name="text-align">left</xsl:attribute>
        <xsl:attribute name="font-family">Times</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="black-bold-11">
        <xsl:attribute name="font-weight">bold</xsl:attribute>
        <xsl:attribute name="font-size">11pt</xsl:attribute>
        <xsl:attribute name="text-align">left</xsl:attribute>
        <xsl:attribute name="font-family">Times</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="black-normal-12">
        <xsl:attribute name="font-weight">normal</xsl:attribute>
        <xsl:attribute name="font-size">12pt</xsl:attribute>
        <xsl:attribute name="text-align">left</xsl:attribute>
        <xsl:attribute name="font-family">Times</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="black-bold-12">
        <xsl:attribute name="font-weight">bold</xsl:attribute>
        <xsl:attribute name="font-size">12pt</xsl:attribute>
        <xsl:attribute name="text-align">left</xsl:attribute>
        <xsl:attribute name="font-family">Times</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="black-normal-20">
        <xsl:attribute name="font-weight">normal</xsl:attribute>
        <xsl:attribute name="font-size">20pt</xsl:attribute>
        <xsl:attribute name="text-align">left</xsl:attribute>
        <xsl:attribute name="font-family">Times</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="black-normal-7">
        <xsl:attribute name="font-weight">normal</xsl:attribute>
        <xsl:attribute name="font-size">7pt</xsl:attribute>
        <xsl:attribute name="text-align">center</xsl:attribute>
        <xsl:attribute name="font-family">Times</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="black-bold-7">
        <xsl:attribute name="font-weight">bold</xsl:attribute>
        <xsl:attribute name="font-size">7pt</xsl:attribute>
        <xsl:attribute name="text-align">center</xsl:attribute>
        <xsl:attribute name="font-family">Times</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="black-normal-16">
        <xsl:attribute name="font-weight">normal</xsl:attribute>
        <xsl:attribute name="font-size">16pt</xsl:attribute>
        <xsl:attribute name="text-align">center</xsl:attribute>
        <xsl:attribute name="font-family">Times</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="black-bold-16">
        <xsl:attribute name="font-weight">bold</xsl:attribute>
        <xsl:attribute name="font-size">16pt</xsl:attribute>
        <xsl:attribute name="text-align">center</xsl:attribute>
        <xsl:attribute name="font-family">Times</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="black-bold-20">
        <xsl:attribute name="font-weight">bold</xsl:attribute>
        <xsl:attribute name="font-size">20pt</xsl:attribute>
        <xsl:attribute name="text-align">left</xsl:attribute>
        <xsl:attribute name="font-family">Times</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="table-header">
        <xsl:attribute name="border">0.5px</xsl:attribute>
        <xsl:attribute name="border-color">black</xsl:attribute>
        <xsl:attribute name="border-style">solid</xsl:attribute>
        <xsl:attribute name="background-color">#FFFFCC</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="table-cell-border-left-top-bottom">
        <xsl:attribute name="border-start-width">1px</xsl:attribute>
        <xsl:attribute name="border-start-color">black</xsl:attribute>
        <xsl:attribute name="border-start-style">solid</xsl:attribute>
        <xsl:attribute name="border-top">1px</xsl:attribute>
        <xsl:attribute name="border-top-color">black</xsl:attribute>
        <xsl:attribute name="border-top-style">solid</xsl:attribute>
        <xsl:attribute name="border-bottom">1px</xsl:attribute>
        <xsl:attribute name="border-bottom-color">black</xsl:attribute>
        <xsl:attribute name="border-bottom-style">solid</xsl:attribute>
        <xsl:attribute name="border-end-width">thin</xsl:attribute>
        <xsl:attribute name="border-end-color">black</xsl:attribute>
        <xsl:attribute name="border-end-style">solid</xsl:attribute>

        <xsl:attribute name="background-color">#C0C0C0</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="table-cell-border-top-bottom">
        <xsl:attribute name="border-start-width">thin</xsl:attribute>
        <xsl:attribute name="border-start-color">black</xsl:attribute>
        <xsl:attribute name="border-start-style">solid</xsl:attribute>
        <xsl:attribute name="border-top-width">1px</xsl:attribute>
        <xsl:attribute name="border-top-color">black</xsl:attribute>
        <xsl:attribute name="border-top-style">solid</xsl:attribute>
        <xsl:attribute name="border-bottom">1px</xsl:attribute>
        <xsl:attribute name="border-bottom-color">black</xsl:attribute>
        <xsl:attribute name="border-bottom-style">solid</xsl:attribute>
        <xsl:attribute name="border-end-width">thin</xsl:attribute>
        <xsl:attribute name="border-end-color">black</xsl:attribute>
        <xsl:attribute name="border-end-style">solid</xsl:attribute>

        <xsl:attribute name="background-color">#C0C0C0</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="table-cell-border-right-top-bottom">
        <xsl:attribute name="border-start-width">thin</xsl:attribute>
        <xsl:attribute name="border-start-color">black</xsl:attribute>
        <xsl:attribute name="border-start-style">solid</xsl:attribute>
        <xsl:attribute name="border-end-width">1px</xsl:attribute>
        <xsl:attribute name="border-end-color">black</xsl:attribute>
        <xsl:attribute name="border-end-style">solid</xsl:attribute>
        <xsl:attribute name="border-top-width">1px</xsl:attribute>
        <xsl:attribute name="border-top-color">black</xsl:attribute>
        <xsl:attribute name="border-top-style">solid</xsl:attribute>
        <xsl:attribute name="border-bottom">1px</xsl:attribute>
        <xsl:attribute name="border-bottom-color">black</xsl:attribute>
        <xsl:attribute name="border-bottom-style">solid</xsl:attribute>
        <xsl:attribute name="background-color">#C0C0C0</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="table-cell-border-left-top">
        <xsl:attribute name="border-start-width">1px</xsl:attribute>
        <xsl:attribute name="border-start-color">black</xsl:attribute>
        <xsl:attribute name="border-start-style">solid</xsl:attribute>
        <xsl:attribute name="border-top-width">1px</xsl:attribute>
        <xsl:attribute name="border-top-color">black</xsl:attribute>
        <xsl:attribute name="border-top-style">solid</xsl:attribute>
        <xsl:attribute name="border-end-width">thin</xsl:attribute>
        <xsl:attribute name="border-end-color">black</xsl:attribute>
        <xsl:attribute name="border-end-style">solid</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="table-cell-border-top">
        <xsl:attribute name="border-start-width">thin</xsl:attribute>
        <xsl:attribute name="border-start-color">black</xsl:attribute>
        <xsl:attribute name="border-start-style">solid</xsl:attribute>
        <xsl:attribute name="border-top-width">1px</xsl:attribute>
        <xsl:attribute name="border-top-color">black</xsl:attribute>
        <xsl:attribute name="border-top-style">solid</xsl:attribute>
        <xsl:attribute name="border-end-width">thin</xsl:attribute>
        <xsl:attribute name="border-end-color">black</xsl:attribute>
        <xsl:attribute name="border-end-style">solid</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="table-cell-border-right-top">
        <xsl:attribute name="border-start-width">thin</xsl:attribute>
        <xsl:attribute name="border-start-color">black</xsl:attribute>
        <xsl:attribute name="border-start-style">solid</xsl:attribute>
        <xsl:attribute name="border-end-width">1px</xsl:attribute>
        <xsl:attribute name="border-end-color">black</xsl:attribute>
        <xsl:attribute name="border-end-style">solid</xsl:attribute>
        <xsl:attribute name="border-top">1px</xsl:attribute>
        <xsl:attribute name="border-top-color">black</xsl:attribute>
        <xsl:attribute name="border-top-style">solid</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="table-cell-border-left-bottom">
        <xsl:attribute name="border-start-width">1px</xsl:attribute>
        <xsl:attribute name="border-start-color">black</xsl:attribute>
        <xsl:attribute name="border-start-style">solid</xsl:attribute>
        <xsl:attribute name="border-bottom-width">1px</xsl:attribute>
        <xsl:attribute name="border-bottom-color">black</xsl:attribute>
        <xsl:attribute name="border-bottom-style">solid</xsl:attribute>
        <xsl:attribute name="border-end-width">thin</xsl:attribute>
        <xsl:attribute name="border-end-color">black</xsl:attribute>
        <xsl:attribute name="border-end-style">solid</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="table-cell-border-bottom">
        <xsl:attribute name="border-start-width">thin</xsl:attribute>
        <xsl:attribute name="border-start-color">black</xsl:attribute>
        <xsl:attribute name="border-start-style">solid</xsl:attribute>
        <xsl:attribute name="border-bottom-width">1px</xsl:attribute>
        <xsl:attribute name="border-bottom-color">black</xsl:attribute>
        <xsl:attribute name="border-bottom-style">solid</xsl:attribute>
        <xsl:attribute name="border-end-width">thin</xsl:attribute>
        <xsl:attribute name="border-end-color">black</xsl:attribute>
        <xsl:attribute name="border-end-style">solid</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="table-cell-border-right-bottom">
        <xsl:attribute name="border-start-width">thin</xsl:attribute>
        <xsl:attribute name="border-start-color">black</xsl:attribute>
        <xsl:attribute name="border-start-style">solid</xsl:attribute>
        <xsl:attribute name="border-end-width">1px</xsl:attribute>
        <xsl:attribute name="border-end-color">black</xsl:attribute>
        <xsl:attribute name="border-end-style">solid</xsl:attribute>
        <xsl:attribute name="border-bottom">1px</xsl:attribute>
        <xsl:attribute name="border-bottom-color">black</xsl:attribute>
        <xsl:attribute name="border-bottom-style">solid</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="table-cell-border-left-bold">
        <xsl:attribute name="border-start-width">1px</xsl:attribute>
        <xsl:attribute name="border-start-color">black</xsl:attribute>
        <xsl:attribute name="border-start-style">solid</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="table-cell-border-right-bold">
        <xsl:attribute name="border-end-width">1px</xsl:attribute>
        <xsl:attribute name="border-end-color">black</xsl:attribute>
        <xsl:attribute name="border-end-style">solid</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="table-cell-border">
        <xsl:attribute name="border">1px</xsl:attribute>
        <xsl:attribute name="border-color">black</xsl:attribute>
        <xsl:attribute name="border-style">solid</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="table-header-text">
        <xsl:attribute name="font-weight">bold</xsl:attribute>
        <xsl:attribute name="font-size">9pt</xsl:attribute>
        <xsl:attribute name="text-align">left</xsl:attribute>
        <xsl:attribute name="font-family">Times</xsl:attribute>
        <xsl:attribute name="margin">2px</xsl:attribute>
        <xsl:attribute name="padding">3px</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="table-cell-text">
        <xsl:attribute name="font-weight">normal</xsl:attribute>
        <xsl:attribute name="font-size">9pt</xsl:attribute>
        <xsl:attribute name="text-align">left</xsl:attribute>
        <xsl:attribute name="font-family">Times</xsl:attribute>
        <xsl:attribute name="margin">3px</xsl:attribute>
        <xsl:attribute name="padding">5px</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="table-cell-text1">
        <xsl:attribute name="font-weight">normal</xsl:attribute>
        <xsl:attribute name="font-size">9pt</xsl:attribute>
        <xsl:attribute name="text-align">left</xsl:attribute>
        <xsl:attribute name="font-family">Times</xsl:attribute>
        <xsl:attribute name="margin">3px</xsl:attribute>
        <xsl:attribute name="padding-left">5px</xsl:attribute>
        <xsl:attribute name="padding-right">5px</xsl:attribute>
        <xsl:attribute name="padding-top">1px</xsl:attribute>
        <xsl:attribute name="padding-bottom">0px</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="table-row-grey">
        <xsl:attribute name="background-color">#C0C0C0</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="table-cell-border">
        <xsl:attribute name="border-color">black</xsl:attribute>
        <xsl:attribute name="border-style">solid</xsl:attribute>
        <xsl:attribute name="background-color">#C0C0C0</xsl:attribute>
        <xsl:attribute name="text-align">justify</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="table-cell-content-border">
        <xsl:attribute name="border-end-width">thin</xsl:attribute>
        <xsl:attribute name="border-end-color">black</xsl:attribute>
        <xsl:attribute name="border-end-style">solid</xsl:attribute>
    </xsl:attribute-set>

    <xsl:attribute-set name="table-cell-content-border-top">
        <xsl:attribute name="border-top-width">thin</xsl:attribute>
        <xsl:attribute name="border-top-color">black</xsl:attribute>
        <xsl:attribute name="border-top-style">solid</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="table-cell-content-border1">
        <xsl:attribute name="border-end-width">thin</xsl:attribute>
        <xsl:attribute name="border-end-color">black</xsl:attribute>
        <xsl:attribute name="border-end-style">solid</xsl:attribute>
        <xsl:attribute name="border-top-width">thin</xsl:attribute>
        <xsl:attribute name="border-top-color">black</xsl:attribute>
        <xsl:attribute name="border-top-style">solid</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="table-cell-content-border2">
        <xsl:attribute name="border-start-width">thin</xsl:attribute>
        <xsl:attribute name="border-start-color">black</xsl:attribute>
        <xsl:attribute name="border-start-style">solid</xsl:attribute>
        <xsl:attribute name="border-end-width">thin</xsl:attribute>
        <xsl:attribute name="border-end-color">black</xsl:attribute>
        <xsl:attribute name="border-end-style">solid</xsl:attribute>
        <xsl:attribute name="border-bottom-width">thin</xsl:attribute>
        <xsl:attribute name="border-bottom-color">black</xsl:attribute>
        <xsl:attribute name="border-bottom-style">solid</xsl:attribute>
    </xsl:attribute-set>
</xsl:stylesheet>