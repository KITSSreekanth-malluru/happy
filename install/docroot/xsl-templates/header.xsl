<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:lxslt="http://xml.apache.org/xslt"
    xmlns:fo="http://www.w3.org/1999/XSL/Format"
    xmlns:xlink="http://www.w3.org/1999/xlink">
    
	<xsl:template name="header">
       <fo:table xsl:use-attribute-sets="table-tight">
           <fo:table-column column-width="proportional-column-width(50%)"/>
           <fo:table-column column-width="proportional-column-width(50%)"/>
           <fo:table-body>
               <fo:table-row>
                   <fo:table-cell xsl:use-attribute-sets="logo-cell" >
			           <fo:block text-align="left">
			               <fo:external-graphic width="189px" height="32px" src="{$url}/store/images/pdf/casto-logo.gif"/>
			           </fo:block>
                   </fo:table-cell>
                   <fo:table-cell text-align="right" padding="9pt" >
                    <fo:table xsl:use-attribute-sets="table-tight-border">
		               <fo:table-column column-width="proportional-column-width(32%)"/>
		               <fo:table-column column-width="proportional-column-width(68%)"/>
		               <fo:table-body>
		                   <fo:table-row>
		                       <fo:table-cell text-align="right">
		                       </fo:table-cell>
		                       <fo:table-cell text-align="center" border="0.5pt solid black" >
		                           <fo:block text-align="center" xsl:use-attribute-sets="black-normal-16">www.castorama.fr</fo:block>
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
