<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:lxslt="http://xml.apache.org/xslt"
    xmlns:fo="http://www.w3.org/1999/XSL/Format"
    xmlns:xlink="http://www.w3.org/1999/xlink">
    
	<xsl:template name="footer">
		<fo:block padding-top="4px"/>
		<fo:table xsl:use-attribute-sets="table-tight">
			<fo:table-column column-width="proportional-column-width(100%)"/>
			<fo:table-body>
				<fo:table-row>
                    <fo:table-cell text-align="center">
                        <fo:block text-align="center" xsl:use-attribute-sets="black-bold-7">CASTORAMA France S.A.S au capital de 304 186 300 Euros - Si?ge social : Parc d'activit?s 59175 Templ
                            emars</fo:block>
                        <fo:block text-align="center" xsl:use-attribute-sets="black-bold-7">SIRET : 451 678 973 01572</fo:block>
                        <fo:block text-align="center" xsl:use-attribute-sets="black-bold-7">RCS Lille 451 678 973 - TVA FR 874 516 789 73</fo:block>
                    </fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
	</xsl:template>

</xsl:stylesheet>
