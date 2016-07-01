<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns:lxslt="http://xml.apache.org/xslt"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                xmlns:xlink="http://www.w3.org/1999/xlink">

  <xsl:template match="ticket">
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">

    <fo:layout-master-set>

        <fo:simple-page-master master-name="repeat_common"
                               page-width="8.5in"
                               page-height="11in"
                               margin-right="2.915in"
                               margin-left="2.915in"
                               margin-top="1.0in"
                               margin-bottom="0.1in" >
          <fo:region-body/>
        </fo:simple-page-master>

    </fo:layout-master-set>
  
    <fo:page-sequence master-reference="repeat_common">
      <fo:flow flow-name="xsl-region-body">
        <fo:block text-align="center">
            Unable to generate PDF
        </fo:block>
      </fo:flow>
    </fo:page-sequence>
    
    </fo:root>
  </xsl:template>

</xsl:stylesheet>

